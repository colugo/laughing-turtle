package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_IfLogic extends BaseSimulatedActionLanguage {

	private Syntax_IfLogic _syntax = null;
	private boolean _wasIfTrue = false;
	
	public SimulatedSyntax_IfLogic(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._syntax = (Syntax_IfLogic) this._concreteSyntax;
	}

	public SimulatedSyntax_IfLogic() {
	}

	@Override
	public void simulate() {
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		LogicExpressionTree logicTree = _syntax.get_Logic();
		SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(logicTree);
		simulatedLogicTree.setExpressionTokenLookup(simulatingState);
		
		Object expressionValue = simulatedLogicTree.evaluateExpression();
		// this is an if, expression value must be boolean
		this._wasIfTrue = (Boolean) expressionValue;
	}

	public int getNextInstructionLineNumber()
	{
		if(this._wasIfTrue)
		{
			return this._lineNumber + 1;	
		}
		int closeIfBlock = this._simulator.getSimulatingState().getProcedure().closingBlockLineNumber(this._lineNumber);
		// else statements are only executed when the top body of the if statement has ended.
		// when the if statement is false, it jumps AFTER the else statement
		// but before the end if
		return closeIfBlock;
	}
	
}
