package com.github.leialoha.playerwarps.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	public TickEvent() {

	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
	
}
