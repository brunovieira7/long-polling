package org.springframework.samples.async.move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Board {
    private float xmax = 8f;
    private float xmin = -7f;
    private float ymax = 0f;
    private float ymin = -3f;
    
    private Map<String, ActionMsg> players = new HashMap<>();
    
    private int index = 1;
    
    public ActionMsg enemyGotHit(ActionMsg msg) {
    	ActionMsg pPos = players.get(msg.getPlayer());
    	ActionMsg enemy = players.values().stream().filter(x -> x.getPlayer().startsWith("e") && x.getY() == pPos.getY()).findFirst().orElse(null);
    	
    	// projectile from left to right and enemy is on the right
    	if (enemy != null && msg.getX() > 0 && (pPos.getX() < enemy.getX())) {
    		return enemy;
    	}
    	return null;
    }
    
    public List<ActionMsg> getPlayers() {
    	return players.values().stream().filter(x -> x.getPlayer().startsWith("p")).collect(Collectors.toList());
    }
    
    public List<ActionMsg> playersGotHit(ActionMsg msg, int radius) {
    	
    	List<ActionMsg> playersHit = players.values().stream()
    			.filter(x -> x.getPlayer().startsWith("p") && playerOnSpell(x, msg, radius))
    			.collect(Collectors.toList());

    	return playersHit;
    }
    
    public List<ActionMsg> getNeighbours(ActionMsg msg, int radius) {
    	List<ActionMsg> msgs = new ArrayList<>();
    	
    	//System.out.println("ORIGIN: " + msg);
    	
    	float yStep = ((float)radius/(float)2);
    	for (float x = -radius; x <= radius; x++) {
    		for (float y = -yStep; y <= yStep; y+=0.5) {
    			ActionMsg tmp = new ActionMsg(msg.getPlayer(), msg.getAction(), msg.getX() + x, msg.getY() + y);
    			
    			//System.out.println("PLC: " + tmp);
    			if (!isOutOfBounds(tmp))
    				msgs.add(tmp);
    		}
    	}
    	
    	return msgs;
    }
    
    public boolean playerOnSpell(ActionMsg player, ActionMsg msg, int radius) {
    	return getNeighbours(msg, radius).stream().filter(x -> x.samePlace(player)).findFirst().isPresent();
    }
    
    public boolean canMove(ActionMsg msg) {
    	msg = getFinalPos(msg);
    	
        if (isOutOfBounds(msg))
            return false;
        
        boolean canMove = true;
        for (String p : players.keySet()) {
            ActionMsg pPos = players.get(p);
            if (!p.equals(msg.getPlayer()) && pPos.samePlace(msg)) {
                return false;
            }
        }
        
        if (canMove) {
            players.put(msg.getPlayer(), msg);
        }
        
        return canMove;
    }
    
    private ActionMsg registerEnemy() {
    	return new ActionMsg("e1","move",7,-1.5f);
    }
    
    public ActionMsg registerPlayer(String prefix) {
    	if (prefix.equals("e")) {
    		return registerEnemy();
    	}
    	
    	ActionMsg msg = new ActionMsg();
    	for (float y = ymin; y <= ymax; y+=0.5f) {
    		msg = new ActionMsg(prefix + index, "move", xmin, y);
    		if (canMove(msg)) {
    			index++;
    			return msg;
    		}
    	}
    	
    	return msg;
    }
    
    private boolean isOutOfBounds(ActionMsg msg) {
        if (msg.getX() > xmax || msg.getX() < xmin || msg.getY() > ymax || msg.getY() < ymin)
            return true;
        
        return false;
    }
    
    private ActionMsg getFinalPos(ActionMsg msg) {
        for (String p : players.keySet()) {
        	if (p.equals(msg.getPlayer())) {
        		ActionMsg pPos = players.get(p);
        		msg.setX(pPos.getX() + msg.getX());
        		msg.setY(pPos.getY() + msg.getY());
        	}
        }
        
        return msg;
    }
}
