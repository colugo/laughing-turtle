package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.metadata.IIOMetadataNode;

import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class Diagnostics {

	private int _instructionsExecuted;
	private ArrayList<BaseInstanceLifecycleStage> _instanceLifecycleStages = new ArrayList<BaseInstanceLifecycleStage>();
	private HashSet<SimulatedInstanceIdentifier> _instancesInDiagnostics = new HashSet<SimulatedInstanceIdentifier>();

	public int countOfInstructionsExecuted() {
		return this._instructionsExecuted;
	}

	private boolean hasStages() {
		return !this._instanceLifecycleStages.isEmpty();
	}

	public void instructionExecuted(StatementExecutedDiagnosticsType type) {
		this._instructionsExecuted++;
		if (!hasStages()) {
			return;
		}

		SimulatedInstanceIdentifier currentlyExecutingInstance = type.getInstance().getIdentifier();
		InstanceLifecycle lifeCycle = this.getInstanceLifecycle(currentlyExecutingInstance);

		if (!lifeCycle.hasStages()) {
			return;
		}

		BaseInstanceLifecycleStage lastStage = lifeCycle.getLastStage();
		lastStage.registerStatement(type);
	}
	
	public int countOfInstanceLifeCycleStages() {
		return this._instanceLifecycleStages.size();
	}

	public void registerInstanceStage(BaseInstanceLifecycleStage stage) {
		this._instanceLifecycleStages.add(stage);
		SimulatedInstanceIdentifier id = stage.getInstance().getIdentifier();
		this._instancesInDiagnostics.add(id);
	}

	public String getInstanceLifecycleLog() {
		String log = "\n\n---------------InstanceLifecycleLog-----------------\n";
		for (BaseInstanceLifecycleStage stage : this._instanceLifecycleStages) {
			log += stage.getStageLog() + "\n";
		}
		log += "\n\n";
		return log;
	}

	public int getInstanceCount() {
		return this._instancesInDiagnostics.size();
	}

	public InstanceLifecycle getInstanceLifecycle(SimulatedInstanceIdentifier instanceIdentifier) {
		InstanceLifecycle lifecycle = new InstanceLifecycle();
		for (BaseInstanceLifecycleStage stage : this._instanceLifecycleStages) {
			if (stage.getInstance().getIdentifier() == instanceIdentifier) {
				lifecycle.add(stage);
			}
		}
		return lifecycle;
	}

	public String getInstanceLifecycleLogWithProcedures() {
		String log = "\n\n---------------InstanceLifecycleLogWithProcedures-----------------\n";
		for (BaseInstanceLifecycleStage stage : this._instanceLifecycleStages) {
			log += stage.toString() + "\n";
		}
		log += "\n\n";
		return log;
	}

	public IIOMetadataNode serialise() {
		IIOMetadataNode root = new IIOMetadataNode("diagnostics");
		int stageCount = 0;
		for(BaseInstanceLifecycleStage baseStage : this._instanceLifecycleStages)
		{
			root.appendChild(baseStage.serialise(stageCount++));
		}
		return root;
	}



}
