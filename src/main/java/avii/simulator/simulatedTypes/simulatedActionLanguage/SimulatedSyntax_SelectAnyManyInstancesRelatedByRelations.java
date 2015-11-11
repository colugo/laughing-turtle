package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.Collection;
import java.util.HashSet;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IRelationList;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelations;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations extends BaseSimulatedActionLanguage {
	
	private Syntax_SelectAnyManyInstancesRelatedByRelations _syntax;

	public SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_SelectAnyManyInstancesRelatedByRelations) theConcreteSyntax;
	}

	
	public SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations() {
	}


	@Override
	public void simulate() {
		
		String instanceSetName = this._syntax.get_Instance1();
		
		IRelationList relations = this._syntax.get_Relations();
		SimulatedInstance initialInstance = this.getInstanceWithName(relations.GetInitialInstance());
		
		checkForNullInstance(initialInstance);
		
		HashSet<SimulatedInstanceIdentifier> initialSet = new HashSet<SimulatedInstanceIdentifier>();
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
		
		
		if(this._syntax.get_AnyMany() == ENUM_ANY_MANY.MANY)
		{
			createInstanceSet(instanceSetName, initialSet);
		}
		else
		{
			createInstance(instanceSetName, initialInstance.getSimulatedClass(), initialSet);
		}
		
	}


	private void createInstance(String instanceSetName, SimulatedClass simulatedClass, Collection<SimulatedInstanceIdentifier> instances) {
		SimulatedInstance selectedInstance = null;
		if(!instances.isEmpty())
		{
			SimulatedInstanceIdentifier identifier = instances.iterator().next();
			selectedInstance = this._simulator.getInstanceFromIdentifier(identifier);
		}
		else
		{
			selectedInstance = new NullSimulatedInstance(simulatedClass);
		}
		this._simulator.getSimulatingState().registerInstance(instanceSetName, selectedInstance);
	}


	private void createInstanceSet(String instanceSetName, Collection<SimulatedInstanceIdentifier> instances) {
		SimulatedInstanceSet instanceSet = new SimulatedInstanceSet();
		for(SimulatedInstanceIdentifier instance : instances)
		{
			instanceSet.addInstance(this._simulator.getInstanceFromIdentifier(instance));
		}
		this._simulator.getSimulatingState().registerInstanceSet(instanceSetName, instanceSet);
	}

}
