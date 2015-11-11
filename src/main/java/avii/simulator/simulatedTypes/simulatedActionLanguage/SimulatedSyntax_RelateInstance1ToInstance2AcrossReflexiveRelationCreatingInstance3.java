package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 extends BaseSimulatedActionLanguage implements IRelationAlteringSyntax {

	private Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 _specificSyntax;

	public SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3) theConcreteSyntax;
	}

	public SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3() {
	}

	@Override
	public void simulate() {
		SimulatedRelationship relationship = this._simulator.getRelationshipWithName(this._specificSyntax.get_Relation());
		SimulatedInstance instance1 = this.getInstanceWithName(this._specificSyntax.get_Instance1());
		SimulatedInstance instance2 = this.getInstanceWithName(this._specificSyntax.get_Instance2());
		String instance1Verb = this._specificSyntax.get_VerbPhrase();
		String instance2Verb = relationship.getOtherVerb(instance1Verb);
		
		SimulatedRelationInstance relationInstance = relationship.createInstance();
		relationInstance.relateReflexiveInstance(instance1.getIdentifier(), instance1Verb);
		relationInstance.relateReflexiveInstance(instance2.getIdentifier(), instance2Verb);
		
		SimulatedInstanceIdentifier associationInstanceIdentifier = relationInstance.getAssociationInstance();
		SimulatedInstance associationInstance = this._simulator.getInstanceFromIdentifier(associationInstanceIdentifier);
		this._simulator.getSimulatingState().registerInstance(this._specificSyntax.get_Instance3(), associationInstance);
		
		relationship.storeRelationInstance(relationInstance);
	}

}
