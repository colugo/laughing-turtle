package test.java.tests;

import java.util.ArrayList;

import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;

public class SimulatedTestScenario {

	private TestScenario _scenario;
	private ArrayList<SimulatedTestVector> _vectors = new ArrayList<SimulatedTestVector>();

	public SimulatedTestScenario(TestScenario scenario) {
		this._scenario = scenario;
		this.createSimulatedVectors();
	}

	@Override
	public String toString()
	{
		return this._scenario.getName();
	}
	
	private void createSimulatedVectors() {
		for(TestVector vector : this._scenario.getVectors()) {
			SimulatedTestVector simulatedVector = new SimulatedTestVector(vector);
			this._vectors .add(simulatedVector);
		}
	}

	public TestScenario getScenario() {
		return this._scenario;
	}

	public ArrayList<SimulatedTestVector> getSimulatedVectors() {
		return this._vectors;
	}

	public boolean allAssertionsPassed() {
		for(SimulatedTestVector vector : this._vectors)
		{
			if(vector.hasAssertionFailed())
			{
				return false;
			}
		}
		return true;
	}

	public boolean wereExceptionsRaised() {
		for(SimulatedTestVector vector : this._vectors)
		{
			if(vector.hasExceptionBeenRaised())
			{
				return true;
			}
		}
		return false;
	}

	public boolean needsAttention() {
		boolean needsAttention = !allAssertionsPassed() || wereExceptionsRaised();
		return needsAttention;
	}

}
