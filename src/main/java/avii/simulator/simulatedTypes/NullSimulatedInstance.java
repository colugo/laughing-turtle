package main.java.avii.simulator.simulatedTypes;

import main.java.avii.simulator.exceptions.CannotAccessNullSimulatedInstanceException;

public class NullSimulatedInstance extends SimulatedHierarchyInstance {

	private static int sequence = 1000000;

	@Override
	public String toString()
	{
		return "NullInstance";
	}
	
	public NullSimulatedInstance(SimulatedClass simulatedClass) {
		this._classType = simulatedClass;
		this._identifier = new SimulatedInstanceIdentifier(NullSimulatedInstance.sequence ++, simulatedClass);
	}

	public Object getAttribute(String attributeName){
		String reference = this.getInstanceName() + "." + attributeName;
		throw new CannotAccessNullSimulatedInstanceException(reference);
	}

	public void setAttribute(String attributeName, Object attributeValue){
		String reference = this.getInstanceName() + "." + attributeName + " = " + attributeValue.toString();
		throw new CannotAccessNullSimulatedInstanceException(reference);
	}
	
	private String getInstanceName()
	{
		String instanceName = this._classType.getSimulator().getSimulatingState().getNameForInstanceId(this.getIdentifier());
		if(instanceName == null)
		{
			instanceName = "UnableToIdentifyInstanceName";
		}
		return instanceName;
	}

}
