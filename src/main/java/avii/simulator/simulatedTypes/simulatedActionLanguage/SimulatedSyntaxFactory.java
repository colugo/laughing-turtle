package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
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
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;

public class SimulatedSyntaxFactory {

	@SuppressWarnings("serial")
	private static HashMap<Class<?>,Class<?>> _availableSimulatedSyntax = new HashMap<Class<?>,Class<?>>()
	{{
		put(Syntax_AttributeExpression.class, SimulatedSyntax_AttributeExpressoin.class);
		put(Syntax_BlankLine.class, SimulatedNOP.class);
		put(Syntax_CancelEventnameFromSenderToTarget.class, SimulatedSyntax_CancelEventnameFromSenderToTarget.class);
		put(Syntax_Comment.class, SimulatedNOP.class);
		put(Syntax_CreateInstanceFromClass.class,SimulatedSynatx_CreateInstanceFromClass.class);
		put(Syntax_DeleteInstance.class, SimulatedSyntax_DeleteInstance.class);
		put(Syntax_Else.class, SimulatedSyntax_Else.class);
		put(Syntax_EndFor.class, SimulatedSyntax_EndFor.class);
		put(Syntax_EndIf.class, SimulatedSyntax_EndIf.class);
		put(Syntax_ForInstanceInInstanceset.class, SimulatedSyntax_ForInstanceInInstanceset.class);
		put(Syntax_GenerateEventParamsToInstance.class, SimulatedSyntax_GenerateEventParamsToInstance.class);
		put(Syntax_GenerateEventParamsToInstanceDelayDuration.class, SimulatedSyntax_GenerateEventParamsToInstanceDelayDuration.class);
		put(Syntax_GenerateEventParamsToClassCreator.class, SimulatedSyntax_GenerateEventParamsToClassCreator.class);
		put(Syntax_GenerateEventParamsToClassCreatorDelayDuration.class, SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration.class);
		put(Syntax_IfEmptyInstancesetThen.class, SimulatedSyntax_IfEmptyInstancesetThen.class);
		put(Syntax_IfNotEmptyInstancesetThen.class, SimulatedSyntax_IfNotEmptyInstancesetThen.class);
		put(Syntax_IfLogic.class, SimulatedSyntax_IfLogic.class);
		put(Syntax_ReclassifyInstanceToClass.class, SimulatedSyntax_ReclassifyToClass.class);
		put(Syntax_RelateInstance1ToInstance2AcrossRelation.class, SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation.class);
		put(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation.class, SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelation.class);
		put(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3.class, SimulatedSyntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3.class);
		put(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3.class, SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3.class);
		put(Syntax_Return.class, SimulatedSyntax_Return.class);
		put(Syntax_SelectAnyManyInstancesRelatedByRelations.class, SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations.class);
		put(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic.class, SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic.class);
		put(Syntax_SelectAnyManyInstanceFromInstancesOfClass.class, SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass.class);
		put(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic.class, SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic.class);
		put(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation.class, SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation.class);
		put(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation.class, SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation.class);
		put(Syntax_TempExpression.class, SimulatedSyntax_TempExpressoin.class);
		put(Syntax_UnrelateInstance1FromInstance2AcrossRelation.class, SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation.class);
		put(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation.class, SimulatedSyntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation.class);
		
		put(Syntax_FailAssertion.class, SimulatedSyntax_FailAssertion.class);
	}};
	
	@SuppressWarnings("unchecked")
	public static BaseSimulatedActionLanguage getSimulatedSyntax(ISimulator simulator, IActionLanguageSyntax concreteSyntax) throws NoMatchingSimulatedActionLanguageException {
		try
		{
			Class<?> simulatedClass = _availableSimulatedSyntax.get(concreteSyntax.getClass());
			Constructor<BaseSimulatedActionLanguage> constructor = (Constructor<BaseSimulatedActionLanguage>) simulatedClass.getConstructor(Simulator.class, IActionLanguageSyntax.class);
			BaseSimulatedActionLanguage simulatedLanguage = constructor.newInstance(simulator, concreteSyntax);
			return simulatedLanguage;
		}catch(Exception e)
		{
			throw new NoMatchingSimulatedActionLanguageException(concreteSyntax);
		}
	}

}
