package org.springframework.samples.async.move;

public class PlayerInfo {
	public String player;
	public int hp;
	
	public PlayerInfo(String player, int hp) {
		this.player = player;
		this.hp = hp;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}
	
	public void dropHp(int amount) {
		this.hp -= amount;
	}
}
