/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax;

import java.util.HashMap;

public interface IGenerateActionLanguageSyntax extends IActionLanguageSyntax{

	public boolean matchesLine(String line);
	public IActionLanguageSyntax populateSyntax(String line);
	public String getRegex();

	public String eventName();
	public void SetEventName(String name);
	public String target();
	public HashMap<String,String> getParams();
	
}
