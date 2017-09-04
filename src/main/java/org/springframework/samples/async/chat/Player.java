package org.springframework.samples.async.chat;

import java.io.Serializable;

public class Player implements Serializable {
	private String name;

	
	public Player() {
		
	}
	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
