package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

@SuppressWarnings("serial")
public class NoMatchingSimulatedActionLanguageException extends Exception {
	private IActionLanguageSyntax _concreteSyntax;

	public NoMatchingSimulatedActionLanguageException(IActionLanguageSyntax  concreteSyntax)
	{
		this._concreteSyntax = concreteSyntax;
	}

	@Override
	public String toString()
	{
		return "No simulated action language matches " + this._concreteSyntax.getClass().getName();
	}
	
}
