package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.simulator.Simulator;

public class SimulatedSyntax_Return extends BaseSimulatedActionLanguage {

	public SimulatedSyntax_Return(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public SimulatedSyntax_Return() {
	}

	@Override
	public void simulate() {
	}
	
	@Override
	public int getNextInstructionLineNumber()
	{
		return Integer.MAX_VALUE;
	}

}
