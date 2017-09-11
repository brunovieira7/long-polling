package org.springframework.samples.async.move;

public class ActionMsg {
    private String player;
    private String action;
    private float x;
    private float y;

    public ActionMsg(String player, String action, float x, float y) {
        this.player = player;
        this.action = action;
        this.x = x;
        this.y = y;
    }

    public ActionMsg() {

    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean samePlace(ActionMsg msg) {
        if (msg.getX() == x && msg.getY() == y)
            return true;

        return false;
    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "ActionMsg [player=" + player + ", action=" + action + ", x=" + x + ", y=" + y + "]";
	}
}
