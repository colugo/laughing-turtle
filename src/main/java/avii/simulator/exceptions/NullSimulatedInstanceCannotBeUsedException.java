package main.java.avii.simulator.exceptions;


@SuppressWarnings("serial")
public class NullSimulatedInstanceCannotBeUsedException extends SimulationException {

	private String _instanceName;

	public NullSimulatedInstanceCannotBeUsedException(String instance) {
		this._instanceName = instance;
	}
	
	@Override
	public String getMessage()
	{
		String message = "An attempt was made to reference an instance("+ this._instanceName +") that was null.";
		return message;
	}

}
