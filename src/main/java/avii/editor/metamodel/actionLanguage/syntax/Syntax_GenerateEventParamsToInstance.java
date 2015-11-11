/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;
import main.java.avii.util.SetHelper;

public class Syntax_GenerateEventParamsToInstance implements IGenerateActionLanguageSyntax, IVisitableActionLanguage {

	private static final String regex = "GENERATE " + //
			IActionLanguageSyntax.HELPER_BASIC_EVENT + //
			" TO " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _eventName;
	private String _Instance;
	private HashMap<String, String> _params = new HashMap<String, String>();

	public String eventName() {
		return get_eventName();
	}

	public String getRegex() {
		return regex;
	}

	public String get_eventName() {
		return _eventName;
	}

	public void SetEventName(String eventName) {
		_eventName = eventName;
	}

	public String target() {
		return _Instance;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getParams() {
		HashMap<String, String> output = (HashMap<String, String>) _params.clone();
		return output;
	}

	public IActionLanguageSyntax populateSyntax(String line) {
		return this;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append("GENERATE " + _eventName + "(");
		for (String key : SetHelper.getSortedListFromSet(_params.keySet())) {
			output.append("" + key + "=" + _params.get(key) + ", ");
		}
		if (!_params.isEmpty()) {
			output.setLength(output.length() - 2);
		}
		output.append(") TO " + _Instance + ";");
		return output.toString();
	}

	public boolean matchesLine(String line) {
		if (line.matches(regex)) {
			// matches the simple case
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(line);
			m.find();
			_eventName = m.group(1);
			_Instance = m.group(m.groupCount());
			try {
				enumerateEventParameters(line, _eventName, _Instance);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	protected void enumerateEventParameters(String line, String beforeParameters, String afterParameters) {
		line = line.replace("GENERATE " + beforeParameters + "(", "");
		line = line.replace(") TO " + afterParameters + ";", "");
		String[] tokens = line.split(",");
		for (int i = 0; line.length() > 0 && i < tokens.length; i++) {
			String token = tokens[i].trim();
			String[] parts = token.split("=");
			_params.put(parts[0].trim(), parts[1].trim());
		}
	}

	public String get_Instance() {
		return _Instance;
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

	public void setParams(HashMap<String, String> theRenamedParams) {
		this._params = theRenamedParams;
	}

}
