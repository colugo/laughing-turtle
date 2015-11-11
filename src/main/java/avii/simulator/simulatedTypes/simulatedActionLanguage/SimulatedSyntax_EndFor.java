package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.simulator.Simulator;

public class SimulatedSyntax_EndFor extends BaseSimulatedActionLanguage {

	
	public SimulatedSyntax_EndFor(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public SimulatedSyntax_EndFor() {
	}

	@Override
	public void simulate() {
		// nop
	}
	
	public int getNextInstructionLineNumber()
	{
		// need to get the line number of this's opening for block
		int openForBlock = this._simulator.getSimulatingState().getProcedure().openingBlockLineNumber(this._lineNumber);
		return openForBlock;
	}

}
