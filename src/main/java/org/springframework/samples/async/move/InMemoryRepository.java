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
			
			ActionMsg enemy = board.enemyGotHit(msg);
			if (enemy != null) {
				playerInfo.get(enemy.getPlayer()).dropHp(1);
				this.moves.add(new ActionMsg(enemy.getPlayer(), "hp", playerInfo.get(enemy.getPlayer()).getHpPercentage(), 0));
				msg.setX(enemy.getX());
			}
			else {
				if (msg.getX() > 0) msg.setX(10);
				else msg.setX(-10);
			}
			
			this.moves.add(msg);
			return true;
		}
		
		if (msg.getAction().equals("snake")) {
			List<ActionMsg> players = board.playersGotHit(msg, 1);
			if (players != null && !players.isEmpty()) {
				players.stream().forEach(x -> {
					playerInfo.get(x.getPlayer()).dropHp(10);
					this.moves.add(new ActionMsg(x.getPlayer(), "hp", playerInfo.get(x.getPlayer()).getHpPercentage(), 0));
				});
			}
			
			this.moves.add(msg);
			return true;
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
	    playerInfo = new HashMap<>();
	}

	@Override
	public ActionMsg registerPlayer(String prefix) {
		ActionMsg msg = board.registerPlayer(prefix);
		playerInfo.put(msg.getPlayer(), new PlayerInfo(msg.getPlayer(), 100));
		
		return msg;
	}

	@Override
	public List<ActionMsg> getPlayers() {
		// TODO Auto-generated method stub
		return board.getPlayers();
	}
}
