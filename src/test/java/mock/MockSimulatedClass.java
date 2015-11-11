package test.java.mock;

import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class MockSimulatedClass extends SimulatedClass {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MockSimulatedClass other = (MockSimulatedClass) obj;
		if (_name == null) {
			if (other._name != null) {
				return false;
			}
		} else if (!_name.equals(other._name)) {
			return false;
		}
		return true;
	}

	private String _name;

	public MockSimulatedClass(String name) {
		super();
		this._name = name;
		this._simulator = new MockSimulator();
	}

	@Override
	public String getName() {
		return this._name;
	}

	@Override
	public boolean hasStates() {
		return false;
	}

	@Override
	public SimulatedInstance createInstance() {
		MockSimulatedInstance instance = new MockSimulatedInstance();
		instance.setMockClass(this);
		instance.setIdentifier(this.getNextSimulatedInstanceIdentifier());

		return instance;
	}
}
