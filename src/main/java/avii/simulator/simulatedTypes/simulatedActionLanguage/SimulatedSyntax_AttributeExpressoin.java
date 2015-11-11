package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedSyntax_AttributeExpressoin extends BaseSimulatedActionLanguage {

	private Syntax_AttributeExpression _specificSyntax;

	public SimulatedSyntax_AttributeExpressoin(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
		this._specificSyntax = (Syntax_AttributeExpression)theConcreteSyntax;
	}

	public SimulatedSyntax_AttributeExpressoin() {
	}

	@Override
	public void simulate() {
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		
		LogicExpressionTree logicTree = _specificSyntax.get_Logic();
		SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(logicTree);
		simulatedLogicTree.setExpressionTokenLookup(simulatingState);
		
		
		Object expressionValue = simulatedLogicTree.evaluateExpression();
		
		String instanceName = _specificSyntax.get_Instance();
		String attributeName = _specificSyntax.get_Attribute();
		
		
		SimulatedInstance instance = this.getInstanceWithName(instanceName);
		
		instance.setAttribute(attributeName, expressionValue);

	}

}
