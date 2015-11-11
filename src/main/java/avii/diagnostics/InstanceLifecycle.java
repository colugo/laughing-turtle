package main.java.avii.diagnostics;

import java.util.ArrayList;

import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class InstanceLifecycle {

	private ArrayList<BaseInstanceLifecycleStage> _stages = new ArrayList<BaseInstanceLifecycleStage>();
	private int _stagePointer = 0;

	public int getStageCount() {
		return this._stages.size();
	}

	public boolean hasStages()
	{
		return getStageCount() > 0;
	}
	
	private BaseInstanceLifecycleStage currentStage() {
		return this._stages.get(this._stagePointer);
	}

	public void add(BaseInstanceLifecycleStage stage) {
		this._stages.add(stage);
	}

	public String getCurrentState() {
		return currentStage().getCurrentState().getName();
	}

	public String getTriggeringEvent() {
		return ((EventConsumedInstanceLifecycleStage) currentStage()).getTriggeringEventNameAndParams();
	}

	public void forward() {
		this._stagePointer++;
	}

	public void back() {
		this._stagePointer--;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (BaseInstanceLifecycleStage stage : this._stages) {
			buffer.append(stage.getStageLog());
			buffer.append('\n');
		}

		String output = buffer.toString();
		return output;
	}

	public boolean wasIgnored() {
		return ((EventConsumedInstanceLifecycleStage) currentStage()).wasIgnored();
	}

	public boolean isInitial() {
		return currentStage() instanceof CreatedInstanceLifecycleStage;
	}

	public boolean hasWaitingEvents() {
		return currentStage().hasWaitingEvents();
	}

	public ProcedureDiagnostics getProcedureDiagnostics() {
		return currentStage().getProcedureDiagnostics();
	}

	public BaseInstanceLifecycleStage getLastStage() {
		return this._stages.get(getStageCount() - 1);
	}

	public ArrayList<SimulatedEventInstance> getWaitingEventsForInstance(SimulatedInstanceIdentifier id) {
		return currentStage().getWaitingEventsForInstance(id);
	}

}
