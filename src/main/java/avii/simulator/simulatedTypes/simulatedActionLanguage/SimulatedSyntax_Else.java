package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.simulator.Simulator;

public class SimulatedSyntax_Else extends BaseSimulatedActionLanguage {

	public SimulatedSyntax_Else(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public SimulatedSyntax_Else() {
	}

	@Override
	public void simulate() {

	}
	
	public int getNextInstructionLineNumber()
	{
		// else statements are only executed when the top body of the if statement has ended.
		// when the if statement is false, it jumps AFTER the else statement
		int closeIfBlock = this._simulator.getSimulatingState().getProcedure().closingBlockLineNumber(this._lineNumber);
		return closeIfBlock;
	}
	
}
