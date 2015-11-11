package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation extends BaseSimulatedActionLanguage {

	private Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation _specificSyntax;

	public SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation) theConcreteSyntax;
	}

	public SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation() {
	}

	@Override
	public void simulate() {
		SimulatedRelationship relationship = this._simulator.getRelationshipWithName(this._specificSyntax.get_Relation());
		SimulatedInstance instance1 = this.getInstanceWithName(this._specificSyntax.get_Instance2());
		SimulatedInstance instance2 = this.getInstanceWithName(this._specificSyntax.get_Instance3());
		
		SimulatedInstanceIdentifier associationIdentifier = ((NonReflexiveRelationshipStorage) relationship.getRelationshipStorage()).getAssociationInstance(instance1.getIdentifier(), instance2.getIdentifier());
		
		SimulatedInstance association = null;
		
		if(associationIdentifier != null)
		{
			association = this._simulator.getInstanceFromIdentifier(associationIdentifier);
		}
		else
		{
			association = new NullSimulatedInstance(relationship.getAssociationClass());
		}

		this._simulator.getSimulatingState().registerInstance(this._specificSyntax.get_Instance1(), association);

	}

}
