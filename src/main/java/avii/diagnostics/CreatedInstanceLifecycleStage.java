package main.java.avii.diagnostics;

import javax.imageio.metadata.IIOMetadataNode;

import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class CreatedInstanceLifecycleStage extends BaseInstanceLifecycleStage {

	public CreatedInstanceLifecycleStage(SimulatedInstance instance) {
		super(instance);
	}

	@Override
	public String getStageLog() {
		String instanceDescription = this._instance.toString() + " : ";
		String currentStateName = getStateName();
		if(currentStateName == "")
		{
			return instanceDescription +"Created";
		}
		return instanceDescription + "Created in " + currentStateName;
	}
	
	@Override
	public String toString() {
		String instanceDescription = this._instance.toString() + " : ";
		String currentStateName = getStateName();
		if(currentStateName == "")
		{
			return instanceDescription +"Created";
		}
		return instanceDescription + "Created in " + currentStateName;
	}

	@Override
	public IIOMetadataNode serialise(int sequence) {
		IIOMetadataNode node = this.createBaseNode("Instance created", sequence);
		node.setAttribute("instanceId", this._instance.getIdentifier().toString());
		return node;
	}
	
}
