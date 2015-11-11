package main.java.avii.simulator.events;

import java.util.Comparator;

import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;

public class SimulatedEventInstanceComparitor implements Comparator<SimulatedEventInstance> {

	public int compare(SimulatedEventInstance event1, SimulatedEventInstance event2) {
		if(event1.getGoTime() < event2.getGoTime())
		{
			return -1;
		}
		if(event2.getGoTime() < event1.getGoTime())
		{
			return 1;
		}
		return 0;
	}

}
