package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClass;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass extends BaseSimulatedActionLanguage {
	
	private Syntax_SelectAnyManyInstanceFromInstancesOfClass _syntax;

	public SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_SelectAnyManyInstanceFromInstancesOfClass) theConcreteSyntax;
	}

	
	public SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass() {
	}


	@Override
	public void simulate() {
		String className = this._syntax.get_Class();
		String instanceSetName = this._syntax.get_Instance();
		
		SimulatedClass simulatedClass = this._simulator.getSimulatedClass(className);
		ArrayList<SimulatedInstance> instances = simulatedClass.getInstances();
		
		if(this._syntax.get_AnyMany() == ENUM_ANY_MANY.MANY)
		{
			createInstanceSet(instanceSetName, instances);
		}
		else
		{
			createInstance(instanceSetName, simulatedClass, instances);
		}
	}


	private void createInstance(String instanceSetName, SimulatedClass simulatedClass, ArrayList<SimulatedInstance> instances) {
		SimulatedInstance selectedInstance = null;
		if(!instances.isEmpty())
		{
			selectedInstance = instances.get(0);
		}
		else
		{
			selectedInstance = new NullSimulatedInstance(simulatedClass);
		}
		this._simulator.getSimulatingState().registerInstance(instanceSetName, selectedInstance);
	}


	private void createInstanceSet(String instanceSetName, ArrayList<SimulatedInstance> instances) {
		SimulatedInstanceSet instanceSet = new SimulatedInstanceSet();
		for(SimulatedInstance instance : instances)
		{
			instanceSet.addInstance(instance);
		}
		this._simulator.getSimulatingState().registerInstanceSet(instanceSetName, instanceSet);
	}

}
