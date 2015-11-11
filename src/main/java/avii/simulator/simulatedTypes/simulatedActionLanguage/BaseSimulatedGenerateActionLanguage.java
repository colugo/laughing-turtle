package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public abstract class BaseSimulatedGenerateActionLanguage extends BaseSimulatedActionLanguage {

	public BaseSimulatedGenerateActionLanguage(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax) {
		super(theSimulator, theConcreteSyntax);
	}

	public BaseSimulatedGenerateActionLanguage(){}
	
	protected void addParamsToEventInstance(HashMap<String, String> params, SimulatedEventInstance eventInstance) {
		for (String paramName : params.keySet()) {
			String rawParamValue = params.get(paramName);

			SimulatedState simulatingState = this._simulator.getSimulatingState();
			SimulatedLogicExpressionTree simulatedLogicTree = new SimulatedLogicExpressionTree(new LogicExpressionTree(rawParamValue));
			simulatedLogicTree.setExpressionTokenLookup(simulatingState);
			Object expressionValue = simulatedLogicTree.evaluateExpression();

			eventInstance.setParam(paramName, expressionValue);
		}
	}

}
