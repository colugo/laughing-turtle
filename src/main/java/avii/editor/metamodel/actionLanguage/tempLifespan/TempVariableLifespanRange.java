package main.java.avii.editor.metamodel.actionLanguage.tempLifespan;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ITempDeclarationActionLanguageSyntax;
import main.java.avii.util.SetHelper;

public class TempVariableLifespanRange {

	private HashMap<Integer, ITempDeclarationActionLanguageSyntax> _range = new HashMap<Integer, ITempDeclarationActionLanguageSyntax>();
	private boolean _invalid = false;

	private void invalidate()
	{
		_invalid = true;
	}
	
	public boolean isValid()
	{
		return !_invalid;
	}
	
	public void declare(int i, ITempDeclarationActionLanguageSyntax tempSyntax) {
		if (_range.containsKey(new Integer(i))) {
			invalidate(); 
		}
		_range.put(new Integer(i), tempSyntax);
	}

	public ITempDeclarationActionLanguageSyntax identify(int lineNumber) throws NameNotFoundException {
		if(!isValid())
		{
			throw new NameNotFoundException("This temp range is invalid.");
		}
		
		if(_range.isEmpty())
		{
			throw new NameNotFoundException("There are no items in this range");
		}
		
		ArrayList<Integer> sortedKeySet = SetHelper.getSortedIntegerListFromSet(_range.keySet());
		
		if(lineNumber < sortedKeySet.get(0))
		{
			throw new NameNotFoundException("Input number is less then first record");
		}
		
		ITempDeclarationActionLanguageSyntax tempSyntax = _range.get(sortedKeySet.get(0));
		
		for(Integer i : sortedKeySet)
		{
			if(i > lineNumber)
			{
				break;
			}
			tempSyntax = _range.get(i);
		}
		return tempSyntax;
	}

}
