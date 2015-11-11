package test.java.mock;

import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class MockSimulatedInstance extends SimulatedInstance {
	public void setMockClass(MockSimulatedClass theClass)
	{
		this._classType = theClass;
	}
	
	@Override
	public String toString()
	{
		String domainName = "Mocking";
		String className = this._classType.getName();
		int instanceNumber = this._identifier.getSequence();
		
		String description = className + '[' + domainName + ']' + instanceNumber; 
		return description;
	}
}
