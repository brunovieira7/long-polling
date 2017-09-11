package org.springframework.samples.async.move;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private float xmax = 8f;
    private float xmin = -7f;
    private float ymax = 0f;
    private float ymin = -3f;
    
    private Map<String, ActionMsg> players = new HashMap<>();
    
    private int index = 1;
    
    public String enemyGotHit(ActionMsg msg) {
    	ActionMsg pPos = players.get(msg.getPlayer());
    	ActionMsg enemy = players.values().stream().filter(x -> x.getPlayer().startsWith("e") && x.getY() == pPos.getY()).findFirst().orElse(null);
    	
    	// projectile from left to right and enemy is on the right
    	if (msg.getX() > 0 && (pPos.getX() < enemy.getX())) {
    		return enemy.getPlayer();
    	}
    	return null;
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
