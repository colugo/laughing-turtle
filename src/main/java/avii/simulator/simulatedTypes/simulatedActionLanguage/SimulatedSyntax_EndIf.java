package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.simulator.Simulator;

public class SimulatedSyntax_EndIf extends BaseSimulatedActionLanguage {

	
	public SimulatedSyntax_EndIf(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public SimulatedSyntax_EndIf() {
	}

	@Override
	public void simulate() {
		// nop
	}

}
