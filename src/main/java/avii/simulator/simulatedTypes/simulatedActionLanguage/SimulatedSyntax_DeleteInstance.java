package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_DeleteInstance;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSyntax_DeleteInstance extends BaseSimulatedActionLanguage implements IRelationAlteringSyntax {

	private Syntax_DeleteInstance _specificSyntax;

	public SimulatedSyntax_DeleteInstance(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_DeleteInstance)theConcreteSyntax;
	}

	public SimulatedSyntax_DeleteInstance() {
	}

	@Override
	public void simulate() {
		String instanceName = this._specificSyntax.get_Instance();
		SimulatedInstance instanceToDelete = this.getInstanceWithName(instanceName);
		instanceToDelete.deleteInstance();
	}

}
