package org.springframework.samples.async.move;

public class MoveMsg {
	private String player;
	private float x;
	private float y;
	
	public MoveMsg(String player, float x, float y) {
		this.player = player;
		this.x = x;
		this.y = y;
	}

	public MoveMsg() {
		
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

	@Override
	public String toString() {
		return "MoveMsg [player=" + player + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
