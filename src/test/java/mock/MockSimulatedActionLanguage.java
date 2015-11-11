package test.java.mock;

import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.BaseSimulatedActionLanguage;

public class MockSimulatedActionLanguage extends BaseSimulatedActionLanguage {

	private String asString;

	public MockSimulatedActionLanguage(String asString, int lineNumber) {
		this.asString = asString;
		this._lineNumber = lineNumber;
	}

	@Override
	public String asString() {
		return this.asString;
	}

	@Override
	public void simulate() {

	}

}
