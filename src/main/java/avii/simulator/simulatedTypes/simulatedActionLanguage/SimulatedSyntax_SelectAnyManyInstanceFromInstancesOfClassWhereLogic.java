package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic extends BaseSimulatedActionLanguage {
	
	private Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic _syntax;

	public SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic) theConcreteSyntax;
	}

	
	public SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic() {
	}


	@Override
	public void simulate() {
		String className = this._syntax.get_Class();
		String instanceSetName = this._syntax.get_Instance();
		
		boolean isAny = this._syntax.get_AnyMany() == ENUM_ANY_MANY.ANY;
		
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		SimulatedClass simulatedClass = this._simulator.getSimulatedClass(className);
		ArrayList<SimulatedInstance> instances = simulatedClass.getInstances();
		
		SimulatedInstanceSet instanceSet = createInstanceSetWithWhereLogic(isAny, simulatingState, instances);
		
		if(this._syntax.get_AnyMany() == ENUM_ANY_MANY.MANY)
		{
			createInstanceSet(instanceSetName, instanceSet);
		}
		else
		{
			createInstance(instanceSetName, simulatedClass, instanceSet);
		}
	
	}


	private SimulatedInstanceSet createInstanceSetWithWhereLogic(boolean isAny, SimulatedState simulatingState, ArrayList<SimulatedInstance> instances) {
		SimulatedInstanceSet instanceSet = new SimulatedInstanceSet();
		for(SimulatedInstance instance : instances)
		{
			// register the current instance with the name 'selected'
			this._simulator.getSimulatingState().registerInstance("selected", instance);
			
			LogicExpressionTree logicTree = _syntax.get_Logic();
			SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(logicTree);
			simulatedLogicTree.setExpressionTokenLookup(simulatingState);
			String expressionValue = simulatedLogicTree.evaluateExpression().toString();
			boolean wasInstanceSelected = Boolean.parseBoolean(expressionValue);
			
			if(wasInstanceSelected)
			{
				instanceSet.addInstance(instance);
				if(isAny)
				{
					break;
				}
			}
		}
		return instanceSet;
	}

	
	private void createInstance(String instanceSetName, SimulatedClass simulatedClass, SimulatedInstanceSet instanceSet) {
		SimulatedInstance selectedInstance = null;
		if(!(instanceSet.size() == 0))
		{
			selectedInstance = instanceSet.getInstanceAt(0);
		}
		else
		{
			selectedInstance = new NullSimulatedInstance(simulatedClass);
		}
		this._simulator.getSimulatingState().registerInstance(instanceSetName, selectedInstance);
	}


	private void createInstanceSet(String instanceSetName, SimulatedInstanceSet instanceSet) {
		this._simulator.getSimulatingState().registerInstanceSet(instanceSetName, instanceSet);
	}
	
}
