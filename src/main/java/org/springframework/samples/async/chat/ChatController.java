package org.springframework.samples.async.chat;

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
@RequestMapping("/mvc/chat")
public class ChatController {

	private final ChatRepository chatRepository;

	private final Map<DeferredResult<List<MoveMsg>>, Integer> chatRequests =
			new ConcurrentHashMap<DeferredResult<List<MoveMsg>>, Integer>();


	private List<String> players = new ArrayList<String>();
	
	@Autowired
	public ChatController(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public DeferredResult<List<MoveMsg>> getMessages(@RequestParam int messageIndex) {
		System.out.println("==========message index: " +messageIndex );

		final DeferredResult<List<MoveMsg>> deferredResult = new DeferredResult<List<MoveMsg>>(null, Collections.emptyList());
		this.chatRequests.put(deferredResult, messageIndex);

		deferredResult.onCompletion(new Runnable() {
			@Override
			public void run() {
				chatRequests.remove(deferredResult);
			}
		});

		List<MoveMsg> messages = this.chatRepository.getMessages(messageIndex);
		if (!messages.isEmpty()) {
			deferredResult.setResult(messages);
		}

		return deferredResult;
	}

	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public void postMessage(@RequestBody MoveMsg moveMsg)  {
		System.out.println("msg: "+moveMsg);
		
/*		ObjectMapper mapper = new ObjectMapper();
		MoveMsg movemsg = mapper.readValue(message, MoveMsg.class);*/
		
		this.chatRepository.addMessage(moveMsg);

		// Update all chat requests as part of the POST request
		// See Redis branch for a more sophisticated, non-blocking approach

		for (Entry<DeferredResult<List<MoveMsg>>, Integer> entry : this.chatRequests.entrySet()) {
			List<MoveMsg> messages = this.chatRepository.getMessages(entry.getValue());
			entry.getKey().setResult(messages);
		}
	}
	
	@RequestMapping(value="/player", method=RequestMethod.GET)
	@ResponseBody
	public MoveMsg registerPlayer()  {
		String player = "p" + (players.size() + 1);
		players.add(player);
		
		return new MoveMsg(player, -7, -1);
	}

}
