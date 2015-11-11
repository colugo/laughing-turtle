package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstance;

public class Renamable_GenerateEventParamsToInstance extends BaseRenamableActionLanguage {
	private Syntax_GenerateEventParamsToInstance _syntax = null;
	
	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
		this._syntax = (Syntax_GenerateEventParamsToInstance) syntax;
	}

	public void renameEvent(String newEventName) {
		this._syntax.SetEventName(newEventName);
	}
	
	@Override
	public void renameAttribute(String theClassNeme, String theOldAttributeName, String theNewAttributeName,
			StateInstanceLifespanManager instanceLifespanManager, int lineNumber) {
		
		HashMap<String,String> params = this._syntax.getParams();
		try
		{
			renameAttributeInParamMap(theClassNeme, theOldAttributeName, theNewAttributeName, instanceLifespanManager, lineNumber, params);
			this._syntax.setParams(params);
		}
		catch(Exception e){}
	}
	
	@Override
	public void renameEventParam(String theOldParamName, String theNewParamName) {
		HashMap<String,String> params = this._syntax.getParams();
		try
		{
			renameEventParamInParamMap(theOldParamName, theNewParamName, params);
			this._syntax.setParams(params);
		}
		catch(Exception e){}
	}
}
