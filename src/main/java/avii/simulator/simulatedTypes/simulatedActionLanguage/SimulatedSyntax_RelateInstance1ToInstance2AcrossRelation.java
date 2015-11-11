package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossRelation;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation extends BaseSimulatedActionLanguage implements IRelationAlteringSyntax {

	private Syntax_RelateInstance1ToInstance2AcrossRelation _specificSyntax;

	public SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_RelateInstance1ToInstance2AcrossRelation) theConcreteSyntax;
	}

	public SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation() {
	}

	@Override
	public void simulate() {
		SimulatedRelationship relationship = this._simulator.getRelationshipWithName(this._specificSyntax.get_Relation());
		SimulatedInstance instance1 = this.getInstanceWithName(this._specificSyntax.get_Instance1());
		SimulatedInstance instance2 = this.getInstanceWithName(this._specificSyntax.get_Instance2());
		
		SimulatedRelationInstance relationInstance = relationship.createInstance();
		relationInstance.relateNonReflexiveInstance(instance1.getIdentifier());
		relationInstance.relateNonReflexiveInstance(instance2.getIdentifier());
		
		relationship.storeRelationInstance(relationInstance);
	}

}
