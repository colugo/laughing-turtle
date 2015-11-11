/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceDestroyBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceDestroyActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class Syntax_DeleteInstance implements IInstanceDestroyActionLanguage, IVisitableActionLanguage {

	private static final String regex = "DELETE " + //
			ActionLanguageSyntaxHelper.GenericName + //
			ActionLanguageSyntaxHelper.EOL;

	private String _Instance;

	public String getRegex() {
		return regex;
	}

	public IActionLanguageSyntax populateSyntax(String line) {

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		m.find();
		_Instance = m.group(1);
		return this;
	}

	@Override
	public String toString() {
		return "DELETE " + _Instance + ";";
	}

	public boolean matchesLine(String line) {
		return line.matches(regex);
	}

	public String get_Instance() {
		return _Instance;
	}

	public InstanceDestroyBean getDestroyInstance() {
		return new InstanceDestroyBean(_Instance);
	}

	public void accept(IActionLanguageVisitor visitor, int lineNumber) {
		visitor.visit(this, lineNumber);
	}

}
