package test.java.simulator.helper;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;

public class SimulatorTestHelper {

	public static void pickFirstStateFromFirstClassForTesting(Simulator simulator)
	{
		for(SimulatedClass simulatedClass : simulator.getSimulatedClasses())
		{
			EntityClass entityClass = simulatedClass.getConcreteClass();
			if(entityClass.hasInitial())
			{
				simulator.setSimulatingState(entityClass.getInitialState());
				return;
			}
			for(EntityState concreteState : entityClass.getStates())
			{
				simulator.setSimulatingState(concreteState);
				return;
			}
		}
	}
	
}
