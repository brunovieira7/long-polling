package org.springframework.samples.async.move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class ActionController {

    private final MessageRepository messageRepository;

    private Map<DeferredResult<List<ActionMsg>>, Integer> actionRequests = new ConcurrentHashMap<DeferredResult<List<ActionMsg>>, Integer>();

    @Autowired
    public ActionController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @RequestMapping(value = "/action", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<ActionMsg>> getMovements(@RequestParam int index) {
        System.out.println("==========message index: " +index );

        final DeferredResult<List<ActionMsg>> deferredResult = new DeferredResult<List<ActionMsg>>(null, Collections.emptyList());
        this.actionRequests.put(deferredResult, index);

        deferredResult.onCompletion(new Runnable() {
            @Override
            public void run() {
                actionRequests.remove(deferredResult);
            }
        });

        List<ActionMsg> messages = this.messageRepository.getMessages(index);
        if (!messages.isEmpty()) {
            deferredResult.setResult(messages);
        }

        return deferredResult;
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    @ResponseBody
    public void postMovement(@RequestBody ActionMsg message) {
        System.out.println("action: " + message);

        /*
         * ObjectMapper mapper = new ObjectMapper(); MoveMsg movemsg =
         * mapper.readValue(message, MoveMsg.class);
         */
        if (!this.messageRepository.addMessage(message)) {
            return;
        }

        // Update all chat requests as part of the POST request
        // See Redis branch for a more sophisticated, non-blocking approach

        for (Entry<DeferredResult<List<ActionMsg>>, Integer> entry : this.actionRequests.entrySet()) {
            List<ActionMsg> messages = this.messageRepository.getMessages(entry.getValue());
            entry.getKey().setResult(messages);
        }
    }

    @RequestMapping(value = "/player", method = RequestMethod.GET)
    @ResponseBody
    public ActionMsg registerPlayer() {
    	enemyStuff();
        return this.messageRepository.registerPlayer("p");
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    @ResponseBody
    public void clear() {
        this.messageRepository.clear();
    }

    public List<ActionMsg> getPlayers() {
    	return messageRepository.getPlayers();
    }
    
    public ActionMsg getRandomMove() {
    	List<ActionMsg> rnd = new ArrayList<>();
    	rnd.add(new ActionMsg("","",0f, -0.5f));
    	rnd.add(new ActionMsg("","",0f, 0.5f));
    	rnd.add(new ActionMsg("","",1f, 0f));
    	rnd.add(new ActionMsg("","",-1f, 0f));
    	
    	return rnd.get(new Random().nextInt(rnd.size()));
    }
    
	public void enemyStuff() {
		Thread thread = new Thread(new Runnable() {

		    @Override
		    public void run() {
		    	
		    	//while(true) {
		    		try {
						Thread.sleep(1000);
						messageRepository.registerPlayer("e");
						postMovement(new ActionMsg("e1","move",7,-1.5f));
						Thread.sleep(1000);
						
						while(true) {
							int moveRnd = new Random().nextInt(4);
							for (int x = 0; x < moveRnd; x++) {
								ActionMsg rnd = getRandomMove();
								postMovement(new ActionMsg("e1","move", rnd.getX(), rnd.getY()));
								Thread.sleep(1500);
							}
							
							ActionMsg player = getPlayers().get(new Random().nextInt(getPlayers().size()));
							
							postMovement(new ActionMsg("xx","square:3",player.getX(),player.getY()));
							Thread.sleep(2100);
							postMovement(new ActionMsg("s1","snake",player.getX(),player.getY()));
							Thread.sleep(1500);
						}
						
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	//}
		    }
		            
		});
		thread.start();
	}
}
