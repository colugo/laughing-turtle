package test.java.mock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceCreateActionLanguage;
import main.java.avii.simulator.IInstanceModifier;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class MockInstanceCreator implements IInstanceModifier, IInstanceCreateActionLanguage {

	
	public InstanceCreateBean getCreateInstance() {
		return null;
	}

	
	public SimulatedInstance createInstance(SimulatedClass simulatedClass) {
		SimulatedInstance instance = new SimulatedInstance(simulatedClass, this);
		simulatedClass.setupNewInstance(instance);
		return instance;
	}

	public SimulatedInstance createHierarchyInstance(SimulatedHierarchyClass simulatedClass) {
		return null;
	}

	public void aboutToDeleteInstance(SimulatedInstance instance) {
	}

	public void aboutToGenerateEvent(SimulatedEventInstance eventInstance) {
				
	}

}
