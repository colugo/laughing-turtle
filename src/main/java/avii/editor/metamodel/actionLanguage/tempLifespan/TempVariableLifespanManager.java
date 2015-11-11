package main.java.avii.editor.metamodel.actionLanguage.tempLifespan;

import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ITempDeclarationActionLanguageSyntax;

public class TempVariableLifespanManager {

	private HashMap<String, TempVariableLifespanRange> _ranges = new HashMap<String, TempVariableLifespanRange>();

	public void declareTemp(ITempDeclarationActionLanguageSyntax tempSyntax, int lineNumber) {
		String tempName = tempSyntax.getTempName();
		if (!_ranges.containsKey(tempName)) {
			_ranges.put(tempName, new TempVariableLifespanRange());
		}
		_ranges.get(tempName).declare(lineNumber, tempSyntax);
	}

	public ITempDeclarationActionLanguageSyntax identifyTempSyntax(String tempName, int lineNumber) throws NameNotFoundException {
		if (_ranges.containsKey(tempName)) {
			return _ranges.get(tempName).identify(lineNumber);
		}
		throw new NameNotFoundException("Could not find temp variable with name " + tempName + " on line " + lineNumber);
	}

}
