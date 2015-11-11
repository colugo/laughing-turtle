package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.simulator.Simulator;

public class SimulatedNOP extends BaseSimulatedActionLanguage {

	public SimulatedNOP(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public SimulatedNOP() {
	}

	@Override
	public void simulate() {

	}

}
