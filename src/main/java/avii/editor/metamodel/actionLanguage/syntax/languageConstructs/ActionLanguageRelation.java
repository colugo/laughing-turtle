/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;

public class ActionLanguageRelation implements IActionLanguageRelation {

	private String _Name;
	public String get_Name() {
		return _Name;
	}
	public String get_VerbPhrase() {
		return _VerbPhrase;
	}
	private String _VerbPhrase;
	
	
	public ActionLanguageRelation(String name, String verb)
	{
		_Name = name;
		_VerbPhrase = verb;
		if(_VerbPhrase == null)
		{
			_VerbPhrase = "";
		}
	}
	
	public String toString()
	{
		return "[" + _Name + ", " + _VerbPhrase + "]";
    }
	
	public void set_Name(String newRelationName) {
		this._Name = newRelationName;		
	}
}

