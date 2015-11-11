package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_FailAssertion;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;

public class SimulatedSyntax_FailAssertion extends BaseSimulatedActionLanguage {

	private Syntax_FailAssertion _specificSyntax;

	public SimulatedSyntax_FailAssertion(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_FailAssertion) theConcreteSyntax;
	}

	public SimulatedSyntax_FailAssertion() {
	}

	@Override
	public void simulate() {
		SimulatedTestVector simulatingTestVector = (SimulatedTestVector) this._simulator.getSimulatingState();
		LogicExpressionTree logicTree = _specificSyntax.get_Logic();
		SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(logicTree);
		simulatedLogicTree.setExpressionTokenLookup(simulatingTestVector);
		
		Object expressionValue = simulatedLogicTree.evaluateExpression();
		simulatingTestVector.setAssertionFailValue(expressionValue);
		
	}

}
