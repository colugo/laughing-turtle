package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossRelation;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation extends BaseSimulatedActionLanguage implements IRelationAlteringSyntax {

	private Syntax_UnrelateInstance1FromInstance2AcrossRelation _specificSyntax;

	public SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_UnrelateInstance1FromInstance2AcrossRelation) theConcreteSyntax;
	}

	public SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation() {
	}

	@Override
	public void simulate() {
		SimulatedRelationship relationship = this._simulator.getRelationshipWithName(this._specificSyntax.get_Relation());
		SimulatedInstance instance1 = this.getInstanceWithName(this._specificSyntax.get_Instance1());
		SimulatedInstance instance2 = this.getInstanceWithName(this._specificSyntax.get_Instance2());

		if(relationship.hasAssociation())
		{
			SimulatedInstanceIdentifier associationIdentifier = ((NonReflexiveRelationshipStorage) relationship.getRelationshipStorage()).getAssociationInstance(instance1.getIdentifier(), instance2.getIdentifier());
			SimulatedInstance associationInstance = this._simulator.getInstanceFromIdentifier(associationIdentifier);
			associationInstance.deleteInstance();
		}
		else
		{
			relationship.unrelateInstances(instance1.getIdentifier(), instance2.getIdentifier());	
		}
	}

}
