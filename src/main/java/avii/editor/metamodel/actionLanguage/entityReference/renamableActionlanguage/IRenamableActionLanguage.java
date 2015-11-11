package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;

public interface IRenamableActionLanguage {
	void setOriginalSyntax(IActionLanguageSyntax syntax);
	void renameClass(String newClassName);
	void renameEvent(String newEventName);
	void renameRelation(String oldRelationName, String theNewRelationName);
	void renameAttribute(String theClassNeme, String theOldAttributeName, String theNewAttributeName, StateInstanceLifespanManager _instanceLifespanManager, int lineNumber);
	void renameEventParam(String theOldParamName, String theNewParamName);
}