package main.java.avii.simulator.exceptions;


@SuppressWarnings("serial")
public class CannotAccessNullSimulatedInstanceException extends SimulationException {

	private String _instanceName;

	public CannotAccessNullSimulatedInstanceException(String instanceName)
	{
		this._instanceName = instanceName;
	}
	
	@Override
	public String getMessage()
	{
		String message = "An attempt was made to reference an instance("+ this._instanceName +") that was null.";
		return message;
	}

	
}
