package org.springframework.samples.async.move;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private float xmax = 8f;
    private float xmin = -7f;
    private float ymax = 2.5f;
    private float ymin = -1.5f;
    
    private Map<String, MoveMsg> players = new HashMap<>();
    
    public boolean canMove(MoveMsg msg) {
        if (!isOutOfBounds(msg))
            return false;
        
        boolean canMove = true;
        for (String p : players.keySet()) {
            MoveMsg pPos = players.get(p);
            if (!p.equals(msg.getPlayer()) && pPos.samePlace(msg)) {
                return false;
            }
        }
        
        if (canMove) {
            players.put(msg.getPlayer(), msg);
        }
        
        return canMove;
    }
    
    private boolean isOutOfBounds(MoveMsg msg) {
        if (msg.getX() > xmax || msg.getX() < xmin || msg.getY() > ymax || msg.getY() < ymin)
            return true;
        
        return false;
    }
}
