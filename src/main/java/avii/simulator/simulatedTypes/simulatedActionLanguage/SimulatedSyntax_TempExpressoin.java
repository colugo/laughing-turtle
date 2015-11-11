package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_TempExpressoin extends BaseSimulatedActionLanguage {
	
	private Syntax_TempExpression _specificSyntax;

	public SimulatedSyntax_TempExpressoin(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_TempExpression)theConcreteSyntax;
	}

	public SimulatedSyntax_TempExpressoin() {
	}

	@Override
	public void simulate() {
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		
		LogicExpressionTree logicTree = _specificSyntax.get_Logic();
		SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(logicTree);
		simulatedLogicTree.setExpressionTokenLookup(simulatingState);
		
		
		Object expressionValue = simulatedLogicTree.evaluateExpression();
		
		simulatingState.setTempVariable(this._specificSyntax.getTempName(),expressionValue);
	}

}
