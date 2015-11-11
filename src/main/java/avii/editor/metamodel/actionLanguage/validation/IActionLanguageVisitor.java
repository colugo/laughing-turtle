package main.java.avii.editor.metamodel.actionLanguage.validation;

import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CancelEventnameFromSenderToTarget;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CreateInstanceFromClass;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_DeleteInstance;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Else;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_EndFor;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_EndIf;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_FailAssertion;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ForInstanceInInstanceset;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToClassCreator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToClassCreatorDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstance;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstanceDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfEmptyInstancesetThen;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfNotEmptyInstancesetThen;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ReclassifyInstanceToClass;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Return;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClass;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelations;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossRelation;

public interface IActionLanguageVisitor {
	public void visit(Syntax_CreateInstanceFromClass syntax_CreateInstanceFromClass, int lineNumber);
	public void visit(Syntax_DeleteInstance syntax_DeleteInstance, int lineNumber);
	public void visit(Syntax_GenerateEventParamsToClassCreator syntax_GenerateEventParamsToClassCreator, int lineNumber);
	public void visit(Syntax_GenerateEventParamsToClassCreatorDelayDuration syntax_GenerateEventParamsToClassCreatorDelayDuration, int lineNumber);
	public void visit(Syntax_GenerateEventParamsToInstance syntax_GenerateEventParamsToInstance, int lineNumber);
	public void visit(Syntax_GenerateEventParamsToInstanceDelayDuration syntax_GenerateEventParamsToInstanceDelayDuration, int lineNumber);
	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation syntax_RelateInstance1ToInstance2AcrossReflexiveRelation, int lineNumber);
	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3,int lineNumber);
	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelation syntax_RelateInstance1ToInstance2AcrossRelation, int lineNumber);
	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3 syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3, int lineNumber);
	public void visit(Syntax_TempExpression syntax_TempExpression, int lineNumber);
	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClass syntax_SelectAnyManyInstanceFromInstancesOfClass, int lineNumber);
	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic, int lineNumber);
	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelations syntax_SelectAnyManyInstancesRelatedByRelations, int lineNumber);
	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic, int lineNumber);
	public void visit(
			Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation,
			int lineNumber);
	public void visit(
			Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation,
			int lineNumber);
	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossRelation syntax_UnrelateInstance1FromInstance2AcrossRelation, int lineNumber);
	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation, int lineNumber);
	public void visit(Syntax_CancelEventnameFromSenderToTarget syntax_CancelEventnameFromSenderToTarget, int lineNumber);
	public void visit(Syntax_ReclassifyInstanceToClass syntax_ReclassifyInstance1ToInstance2, int lineNumber);
	public void visit(Syntax_ForInstanceInInstanceset syntax_ForInstanceInInstanceset, int lineNumber);
	public void visit(Syntax_EndFor syntax_EndFor, int lineNumber);
	public void visit(Syntax_EndIf syntax_EndIf, int lineNumber);
	public void visit(Syntax_IfEmptyInstancesetThen syntax_IfEmptyInstancesetThen, int lineNumber);
	public void visit(Syntax_IfNotEmptyInstancesetThen syntax_IfNotEmptyInstancesetThen, int lineNumber);
	public void visit(Syntax_Else syntax_Else, int lineNumber);
	public void visit(Syntax_IfLogic syntax_IfLogic, int lineNumber);
	public void visit(Syntax_AttributeExpression syntax_AttributeExpression, int lineNumber);
	public void visit(Syntax_Return syntax_Return, int lineNumber);
	public void visit(Syntax_FailAssertion syntax_FailAssertion, int lineNumber);

}
