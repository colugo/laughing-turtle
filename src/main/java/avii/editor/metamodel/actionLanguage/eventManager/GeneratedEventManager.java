package main.java.avii.editor.metamodel.actionLanguage.eventManager;

import java.util.ArrayList;

public class GeneratedEventManager {

	private ArrayList<String> _registeredEvents = new ArrayList<String>();
	
	public void registerEvent(String eventName) {
		if(!this.isEventRegistered(eventName))
		{
			this._registeredEvents.add(eventName);
		}
	}

	public int numberOfRegisteredEvents() {
		return _registeredEvents.size();
	}

	public boolean isEventRegistered(String eventName) {
		return _registeredEvents.contains(eventName);
	}

	public ArrayList<String> getRegisteredEvents() {
		return _registeredEvents;
	}

}
