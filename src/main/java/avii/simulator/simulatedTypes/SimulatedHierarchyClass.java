package main.java.avii.simulator.simulatedTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.Simulator;

public class SimulatedHierarchyClass extends SimulatedClass implements Comparable<SimulatedHierarchyClass> {

	
	public SimulatedHierarchyClass(EntityClass theClassToSimulate, Simulator simulator) {
		super(theClassToSimulate, simulator);
	}

	@Override
	public SimulatedInstance createInstance() {
		SimulatedInstance instance = this._simulator.getInstanceModifier().createHierarchyInstance(this);
		return instance;
	}
	
	@Override
	public SimulatedState getSimulatedStateForEntityState(EntityState theState)
	{
		SimulatedState theSimulatedState = this._simulatedStates.get(theState.getName());
		return theSimulatedState;
	}
	
	public SimulatedInstance createInstance(HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> hierarchyInstances)
	{
		for(EntityClass superClass : this._classToSimulate.getAllSuperClasses())
		{
			if(superClass.equals(this._classToSimulate))
			{
				continue;
			}
			SimulatedHierarchyClass simulatedSuperClass = (SimulatedHierarchyClass) this._simulator.getSimulatedClass(superClass.getName());
			SimulatedHierarchyInstance simulatedSuperInstance = (SimulatedHierarchyInstance) this._simulator.getInstanceModifier().createInstance(simulatedSuperClass);
			
			addInitialStateToNewInstance(simulatedSuperInstance);
			insertInstanceIntoMapAndShareMapWithInstance(hierarchyInstances, simulatedSuperClass, simulatedSuperInstance);
		}
		SimulatedHierarchyInstance instance = (SimulatedHierarchyInstance) this._simulator.getInstanceModifier().createInstance(this);
		insertInstanceIntoMapAndShareMapWithInstance(hierarchyInstances, this, instance);
		
		return instance;
	}

	public void insertInstanceIntoMapAndShareMapWithInstance(HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> hierarchyInstances, SimulatedHierarchyClass simulatedHierarchyClass, SimulatedHierarchyInstance simulatedHierarchyInstance) {
		hierarchyInstances.put(simulatedHierarchyClass, simulatedHierarchyInstance);
		simulatedHierarchyInstance.setHierarchyInstances(hierarchyInstances);
	}
	
	public Collection<SimulatedHierarchyClass> getHierarchyDependants() {
		HashSet<SimulatedHierarchyClass> dependants = new HashSet<SimulatedHierarchyClass>();
		dependants.add(this);
	
		for(EntityClass superEntityClass : this._classToSimulate.getAllSuperClasses()){
			SimulatedHierarchyClass superHierarchyClass = (SimulatedHierarchyClass) this._simulator.getSimulatedClass(superEntityClass.getName());
			dependants.add(superHierarchyClass);
		}
		
		return dependants;
	}

	public int compareTo(SimulatedHierarchyClass other) {
		return this.getName().compareTo(other.getName());
	}
	
}
