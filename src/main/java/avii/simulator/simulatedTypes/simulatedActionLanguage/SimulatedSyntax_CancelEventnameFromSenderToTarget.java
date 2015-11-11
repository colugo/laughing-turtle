package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CancelEventnameFromSenderToTarget;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedSyntax_CancelEventnameFromSenderToTarget extends BaseSimulatedActionLanguage {

	private Syntax_CancelEventnameFromSenderToTarget _specificSyntax;

	public SimulatedSyntax_CancelEventnameFromSenderToTarget(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_CancelEventnameFromSenderToTarget) theConcreteSyntax;
	}

	public SimulatedSyntax_CancelEventnameFromSenderToTarget() {
	}

	@Override
	public void simulate() {
		SimulatedInstance sender = this.getInstanceWithName(this._specificSyntax.get_Sender());
		SimulatedInstance target = this.getInstanceWithName(this._specificSyntax.get_Target());
		String eventName = this._specificSyntax.get_Eventname();
		
		this._simulator.cancelEvent(eventName, sender, target);
	}

}
