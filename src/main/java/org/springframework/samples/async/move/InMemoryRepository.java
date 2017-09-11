/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.async.move;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class InMemoryRepository implements MessageRepository {

	private List<ActionMsg> moves = new CopyOnWriteArrayList<ActionMsg>();
	
	private Board board = new Board();
	
	private Map<String, PlayerInfo> playerInfo = new HashMap<>();

	public List<ActionMsg> getMessages(int index) {
		if (this.moves.isEmpty()) {
			return Collections.<ActionMsg> emptyList();
		}
		Assert.isTrue((index >= 0) && (index <= this.moves.size()), "Invalid move index");
		return this.moves.subList(index, this.moves.size());
	}

	public boolean addMessage(ActionMsg msg) {
		if (msg.getAction().equals("kunai")) {
			this.moves.add(msg);
			String enemyId = board.enemyGotHit(msg);
			if (enemyId != null) {
				playerInfo.get(enemyId).dropHp(10);
				this.moves.add(new ActionMsg(enemyId, "hp", -10, 0));
			}
		}
		
		if (!msg.getAction().equals("move")) {
			this.moves.add(msg);
			return true;
		}
		
	    if (board.canMove(msg)) {
	        this.moves.add(msg);
	        return true;
	    }
	    
	    return false;
	}
	
	public void clear() {
	    board = new Board();
	    moves = new CopyOnWriteArrayList<ActionMsg>();
	}

	@Override
	public ActionMsg registerPlayer(String prefix) {
		ActionMsg msg = board.registerPlayer(prefix);
		playerInfo.put(msg.getPlayer(), new PlayerInfo(msg.getPlayer(), 100));
		
		return msg;
	}
}
