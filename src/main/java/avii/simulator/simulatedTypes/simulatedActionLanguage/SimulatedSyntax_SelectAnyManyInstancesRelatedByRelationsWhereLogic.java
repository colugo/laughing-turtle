package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.Collection;
import java.util.HashSet;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IRelationList;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic extends BaseSimulatedActionLanguage {
	
	private Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic _syntax;

	public SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic) theConcreteSyntax;
	}

	
	public SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic() {
	}


	@Override
	public void simulate() {
		
		String instanceSetName = this._syntax.get_Instance1();		
		IRelationList relations = this._syntax.get_Relations();

		String className = "";
		try {
			className = this._simulator.getSimulatingState().getConcreteState().getProcedure().identifyClass(instanceSetName, this._lineNumber).getName();
		} catch (Exception e) {
				// has been validated
		}
		SimulatedClass simulatedClass = this._simulator.getSimulatedClass(className);
		
		SimulatedInstance initialInstance = this.getInstanceWithName(relations.GetInitialInstance());
		Collection<SimulatedInstanceIdentifier> initialSet = new HashSet<SimulatedInstanceIdentifier>();
		getInitialInstanceSet(relations, initialInstance, initialSet);
		
		SimulatedInstanceSet whereInstances = this.createInstanceSetWithWhereLogic(this._syntax.get_AnyMany() == ENUM_ANY_MANY.ANY, this._simulator.getSimulatingState(), initialSet);
		
		
		if(this._syntax.get_AnyMany() == ENUM_ANY_MANY.MANY)
		{
			createInstanceSet(instanceSetName, whereInstances);
		}
		else
		{
			createInstance(instanceSetName, simulatedClass, whereInstances);
		}
		
	}

	
	private SimulatedInstanceSet createInstanceSetWithWhereLogic(boolean isAny, SimulatedState simulatingState, Collection<SimulatedInstanceIdentifier> instances) {
		SimulatedInstanceSet instanceSet = new SimulatedInstanceSet();
		for(SimulatedInstanceIdentifier instanceIdentifier : instances)
		{
			SimulatedInstance instance = this._simulator.getInstanceFromIdentifier(instanceIdentifier);
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

	private void getInitialInstanceSet(IRelationList relations, SimulatedInstance initialInstance, Collection<SimulatedInstanceIdentifier> initialSet) {
		initialSet.add(initialInstance.getIdentifier());
		for(IActionLanguageRelation relation : relations)
		{
			String relationName = relation.get_Name();
			SimulatedRelationship simulatedRelation = this._simulator.getRelationshipWithName(relationName);
			Collection<SimulatedInstanceIdentifier> otherEnd = null;
			
			if(!simulatedRelation.isReflexive())
			{
				otherEnd = simulatedRelation.getOtherEnd(initialSet);
				initialSet.clear();
				initialSet.addAll(otherEnd);
			}
			else
			{
				otherEnd = simulatedRelation.getOtherEnd(initialSet, relation.get_VerbPhrase());
				initialSet.clear();
				initialSet.addAll(otherEnd);				
			}
		}
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
