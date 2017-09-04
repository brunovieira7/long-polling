package org.springframework.samples.async.move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class MoveController {

    private final MoveRepository moveRepository;

    private Map<DeferredResult<List<MoveMsg>>, Integer> moveRequests = new ConcurrentHashMap<DeferredResult<List<MoveMsg>>, Integer>();

    private List<String> players = new ArrayList<String>();

    @Autowired
    public MoveController(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    @RequestMapping(value = "/move", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<MoveMsg>> getMovements(@RequestParam int moveIndex) {
        // System.out.println("==========message index: " +messageIndex );

        final DeferredResult<List<MoveMsg>> deferredResult = new DeferredResult<List<MoveMsg>>(null, Collections.emptyList());
        this.moveRequests.put(deferredResult, moveIndex);

        deferredResult.onCompletion(new Runnable() {
            @Override
            public void run() {
                moveRequests.remove(deferredResult);
            }
        });

        List<MoveMsg> messages = this.moveRepository.getMoves(moveIndex);
        if (!messages.isEmpty()) {
            deferredResult.setResult(messages);
        }

        return deferredResult;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ResponseBody
    public void postMovement(@RequestBody MoveMsg moveMsg) {
        System.out.println("moving to: " + moveMsg);

        /*
         * ObjectMapper mapper = new ObjectMapper(); MoveMsg movemsg =
         * mapper.readValue(message, MoveMsg.class);
         */
        if (!this.moveRepository.addMove(moveMsg)) {
            return;
        }

        // Update all chat requests as part of the POST request
        // See Redis branch for a more sophisticated, non-blocking approach

        for (Entry<DeferredResult<List<MoveMsg>>, Integer> entry : this.moveRequests.entrySet()) {
            List<MoveMsg> messages = this.moveRepository.getMoves(entry.getValue());
            entry.getKey().setResult(messages);
        }
    }

    @RequestMapping(value = "/player", method = RequestMethod.GET)
    @ResponseBody
    public MoveMsg registerPlayer() {
        String player = "p" + (players.size() + 1);
        players.add(player);

        return new MoveMsg(player, -7, -1);
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    @ResponseBody
    public void clear() {
        this.moveRepository.clear();
    }

}
