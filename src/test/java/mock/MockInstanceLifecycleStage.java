package test.java.mock;

import main.java.avii.diagnostics.EventConsumedInstanceLifecycleStage;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class MockInstanceLifecycleStage extends EventConsumedInstanceLifecycleStage {

	private String _eventName = "";

	public MockInstanceLifecycleStage(MockSimulatedInstance instance) {
		super(instance);
	}
	
	@Override
	public SimulatedInstance getInstance()
	{
		return this._instance;
	}

	@Override
	public String getTriggeringEventNameAndParams() {
		return this._eventName+"()";
	}

	public void setEventName(String eventName) {
		this._eventName = eventName;		
	}

}
