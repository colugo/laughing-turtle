package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;

public interface IExpressionTokenLookup {
	public String lookupExpressionToken(IActionLanguageToken token);
}
