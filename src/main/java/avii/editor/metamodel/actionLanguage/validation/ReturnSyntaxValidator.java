package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IDescriptiveActionLanguage;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Else;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_EndIf;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Return;

public class ReturnSyntaxValidator {
	
	private HashMap<Integer, IActionLanguageSyntax> _syntaxMap = null;
	private HashMap<IActionLanguageSyntax, Integer> _flipMap = new HashMap<IActionLanguageSyntax, Integer>();
	private ArrayList<IActionLanguageSyntax> _nopList;

	public ReturnSyntaxValidator(HashMap<Integer, IActionLanguageSyntax> syntaxMap) {
		this._syntaxMap  = syntaxMap;
		this._nopList = this.prepareSyntaxMapForReturnValidation();
		this.flipMap();
	}

	private void flipMap() {
		for(Integer i : this._syntaxMap.keySet())
		{
			IActionLanguageSyntax key = this._syntaxMap.get(i);
			this._flipMap.put(key, i);
		}
	}

	private ArrayList<IActionLanguageSyntax> prepareSyntaxMapForReturnValidation()
	{
		ArrayList<IActionLanguageSyntax> noNopSyntax = new ArrayList<IActionLanguageSyntax>();
		for(int i = 1; i <= this._syntaxMap.size(); i++)
		{
			IActionLanguageSyntax syntax = this._syntaxMap.get(i);
			if(!(syntax instanceof IDescriptiveActionLanguage))
			{
				noNopSyntax.add(syntax);
			}
		}
		return noNopSyntax;
	}
	
	
	public ArrayList<Integer> getLinesWithInvalidReturns()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		// no need to check last return
		for(int i = 0; i < this._nopList.size()-1; i++)
		{
			if(!(this._nopList.get(i) instanceof Syntax_Return))
			{
				continue;
			}

			IActionLanguageSyntax next = this._nopList.get(i+1);
			if( !( next instanceof Syntax_Else || next instanceof Syntax_EndIf ))
			{
				list.add(i);
			}
		}
		
		return list;
	}
	
}
