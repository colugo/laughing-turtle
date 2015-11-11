/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_BlankLine;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CancelEventnameFromSenderToTarget;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Comment;
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

@SuppressWarnings("serial")
public class ActionLanguageSupportedSyntax {

	private static ArrayList<IActionLanguageSyntax> supportedSyntaxes = new ArrayList<IActionLanguageSyntax>()
	{{
		add(new Syntax_AttributeExpression());
		add(new Syntax_BlankLine());
		add(new Syntax_CancelEventnameFromSenderToTarget());
		add(new Syntax_Comment());
		add(new Syntax_CreateInstanceFromClass());
		add(new Syntax_DeleteInstance());
		add(new Syntax_Else());
		add(new Syntax_EndFor());
		add(new Syntax_EndIf());
		add(new Syntax_ForInstanceInInstanceset());
		add(new Syntax_GenerateEventParamsToInstance());
		add(new Syntax_GenerateEventParamsToInstanceDelayDuration());
		add(new Syntax_GenerateEventParamsToClassCreator());
		add(new Syntax_GenerateEventParamsToClassCreatorDelayDuration());
		add(new Syntax_IfEmptyInstancesetThen());
		add(new Syntax_IfNotEmptyInstancesetThen());
		add(new Syntax_IfLogic());
		add(new Syntax_ReclassifyInstanceToClass());
		add(new Syntax_RelateInstance1ToInstance2AcrossRelation());
		add(new Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation());
		add(new Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3());
		add(new Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3());
		add(new Syntax_Return());
		add(new Syntax_SelectAnyManyInstancesRelatedByRelations());
		add(new Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic());
		add(new Syntax_SelectAnyManyInstanceFromInstancesOfClass());
		add(new Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic());
		add(new Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation());
		add(new Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation());
		add(new Syntax_TempExpression());
		add(new Syntax_UnrelateInstance1FromInstance2AcrossRelation());
		add(new Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation());
		add(new Syntax_FailAssertion());
		
	}};
	
	public static IActionLanguageSyntax getSyntaxForLine(String line) throws InvalidActionLanguageLineException
	{
		for(IActionLanguageSyntax currentSyntax : supportedSyntaxes)
		{
			if(currentSyntax.matchesLine(line.trim()))
			{
				// return a new syntax object, not the one from the list
				IActionLanguageSyntax newSyntax = null;
				try {
					newSyntax = currentSyntax.getClass().newInstance();
					newSyntax.matchesLine(line.trim());
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				return newSyntax.populateSyntax(line.trim());
			}
		}
		throw new InvalidActionLanguageLineException(line);
	}
	
}
