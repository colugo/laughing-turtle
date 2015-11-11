package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;

public interface ISimulatedActionLanguage {
	void simulate();
	int getLineNumber();
	void setLineNumber(int lineNumber);
	int getNextInstructionLineNumber();
	String asString();
	IActionLanguageSyntax getConcreteSyntax();
}
