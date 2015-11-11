package main.java.avii.simulator.simulatedTypes;

import main.java.avii.simulator.exceptions.CannotAccessNullSimulatedInstanceException;

public class TestVectorSimulatedInstance extends SimulatedInstance {

	@Override
	public String toString()
	{
		return "TestVectorInstance";
	}
	
	public TestVectorSimulatedInstance() {
		this._identifier = new SimulatedInstanceIdentifier(0, null);
		this._identifier.setClassName("TestVectorClass");
	}

	public Object getAttribute(String attributeName){
		throw new CannotAccessNullSimulatedInstanceException(null);
	}

	public void setAttribute(String attributeName, Object attributeValue){
		throw new CannotAccessNullSimulatedInstanceException(null);
	}
}
