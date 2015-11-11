package main.java.avii.simulator;

import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public interface IInstanceModifier {
	SimulatedInstance createInstance(SimulatedClass simulatedClass);
	SimulatedInstance createHierarchyInstance(SimulatedHierarchyClass simulatedClass);
	void aboutToDeleteInstance(SimulatedInstance instance);
	void aboutToGenerateEvent(SimulatedEventInstance eventInstance);
}
