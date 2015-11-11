package main.java.avii.simulator.events;

import java.util.Date;

public class SimulatedEventTimeHelper implements IEventTimeProvider{

	public static long getCurrentTimeHelper()
	{
		return new Date().getTime();
	}
	
	public long getCurrentTime()
	{
		return SimulatedEventTimeHelper.getCurrentTimeHelper();
	}
	
}
