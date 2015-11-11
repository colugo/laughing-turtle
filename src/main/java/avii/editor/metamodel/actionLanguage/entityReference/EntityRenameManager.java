package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.IRenamableActionLanguage;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_CancelEventnameFromSenderToTarget;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_CreateInstanceFromClass;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_GenerateEventParamsToClassCreator;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_GenerateEventParamsToClassCreatorDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_GenerateEventParamsToInstance;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_GenerateEventParamsToInstanceDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_IfLogic;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_ReclassifyInstanceToClass;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_RelateInstance1ToInstance2AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_RelateInstance1ToInstance2AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_RelateInstance1ToInstance2AcrossRelationCreatingInstance3;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectAnyManyInstancesFromInstancesOfClass;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectAnyManyInstancesFromInstancesOfClassWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectAnyManyInstancesRelatedByRelations;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_UnrelateInstance1FromInstance2AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage.Renamable_UnrelateInstance1FromInstance2AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CancelEventnameFromSenderToTarget;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CreateInstanceFromClass;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToClassCreator;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToClassCreatorDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstance;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_GenerateEventParamsToInstanceDelayDuration;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_IfLogic;
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
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityRenameManager extends BaseEntityReferenceVisitor {

	private ArrayList<IActionLanguageSyntax> _syntaxList = null;

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("serial")
	private static HashMap<Class<?>,Class<?>> _renameLookup = new HashMap<Class<?>,Class<?>>()
	{{
		put(Syntax_CreateInstanceFromClass.class, Renamable_CreateInstanceFromClass.class);
		put(Syntax_SelectAnyManyInstanceFromInstancesOfClass.class, Renamable_SelectAnyManyInstancesFromInstancesOfClass.class );
		put(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic.class, Renamable_SelectAnyManyInstancesFromInstancesOfClassWhereLogic.class );
		put(Syntax_GenerateEventParamsToClassCreator.class, Renamable_GenerateEventParamsToClassCreator.class);
		put(Syntax_GenerateEventParamsToClassCreatorDelayDuration.class, Renamable_GenerateEventParamsToClassCreatorDelayDuration.class);
		put(Syntax_GenerateEventParamsToInstance.class, Renamable_GenerateEventParamsToInstance.class);
		put(Syntax_GenerateEventParamsToInstanceDelayDuration.class, Renamable_GenerateEventParamsToInstanceDelayDuration.class);
		put(Syntax_CancelEventnameFromSenderToTarget.class, Renamable_CancelEventnameFromSenderToTarget.class);
		put(Syntax_ReclassifyInstanceToClass.class, Renamable_ReclassifyInstanceToClass.class);
		put(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation.class, Renamable_RelateInstance1ToInstance2AcrossReflexiveRelation.class);
		put(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3.class, Renamable_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3.class);
		put(Syntax_RelateInstance1ToInstance2AcrossRelation.class, Renamable_RelateInstance1ToInstance2AcrossRelation.class);
		put(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3.class, Renamable_RelateInstance1ToInstance2AcrossRelationCreatingInstance3.class);
		put(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation.class, Renamable_UnrelateInstance1FromInstance2AcrossReflexiveRelation.class);
		put(Syntax_UnrelateInstance1FromInstance2AcrossRelation.class, Renamable_UnrelateInstance1FromInstance2AcrossRelation.class);
		put(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation.class, Renamable_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation.class);
		put(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation.class, Renamable_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation.class);
		put(Syntax_SelectAnyManyInstancesRelatedByRelations.class,Renamable_SelectAnyManyInstancesRelatedByRelations.class);
		put(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic.class,Renamable_SelectAnyManyInstancesRelatedByRelationsWhereLogic.class);
		put(Syntax_TempExpression.class,Renamable_TempExpression.class);
		put(Syntax_AttributeExpression.class,Renamable_AttributeExpression.class);
		put(Syntax_IfLogic.class,Renamable_IfLogic.class);
	}};
	
	private static IRenamableActionLanguage lookupRenamableActionLanguageForSyntax(IActionLanguageSyntax syntax) {
		try
		{
			Class<?> lookupResult = _renameLookup.get(syntax.getClass());
			IRenamableActionLanguage renamable = (IRenamableActionLanguage) lookupResult.newInstance();
			renamable.setOriginalSyntax(syntax);
			return renamable;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public EntityRenameManager(EntityDomain domain) {
		super(domain);
	}


	public void setSyntaxList(ArrayList<IActionLanguageSyntax> foundSyntax) {
		this._syntaxList = foundSyntax;
	}
	
	private String getNewProcedure()
	{
		StringBuilder sb = new StringBuilder();
		for(IActionLanguageSyntax syntax : this._syntaxList)
		{
			sb.append(syntax.toString() + "\n");
		}
		return sb.toString();
	}
	
	public String renameEntityClass(EntityClass theClass, String newClassName) {
		ArrayList<EntityClassReference> classReferences = this.getReferencesForEntityClass(theClass);
		for(EntityClassReference entityReference : classReferences)
		{
			IRenamableActionLanguage renamableActionLanguage = EntityRenameManager.lookupRenamableActionLanguageForSyntax(entityReference.getSyntax());
			renamableActionLanguage.renameClass(newClassName);
		}
		
		String newProcedure = getNewProcedure();
		return newProcedure;
	}

	public String renameEvent(EntityEventSpecification theEvent, String newEventName) {
		ArrayList<EntityEventReference> eventReferences = this.getReferencesForEvent(theEvent);
		for(EntityEventReference entityReference : eventReferences)
		{
			IRenamableActionLanguage renamableActionLanguage = EntityRenameManager.lookupRenamableActionLanguageForSyntax(entityReference.getSyntax());
			renamableActionLanguage.renameEvent(newEventName);
		}
		
		String newProcedure = getNewProcedure();
		return newProcedure;
	}

	public String renameEntityRelation(EntityRelation theRelation, String theNewRelationName) {
		ArrayList<EntityRelationReference> relationReferences = this.getReferencesForRelation(theRelation);
		for(EntityRelationReference entityReference : relationReferences)
		{
			IRenamableActionLanguage renamableActionLanguage = EntityRenameManager.lookupRenamableActionLanguageForSyntax(entityReference.getSyntax());
			renamableActionLanguage.renameRelation(theRelation.getName(), theNewRelationName);
		}
		
		String newProcedure = getNewProcedure();
		return newProcedure;
	}

	public String renameEntityAttribute(EntityAttribute theAttribute, String theNewAttributeName) {
		EntityClass theClass = theAttribute.getOwningClass();
		ArrayList<EntityAttributeReference> relationReferences = this.getReferencesForAttribute(theAttribute);
		for(EntityAttributeReference entityReference : relationReferences)
		{
			IRenamableActionLanguage renamableActionLanguage = EntityRenameManager.lookupRenamableActionLanguageForSyntax(entityReference.getSyntax());
			renamableActionLanguage.renameAttribute(theClass.getName(), theAttribute.getName(), theNewAttributeName, this._instanceLifespanManager, entityReference.getReferencedLineNumber());
		}

		String newProcedure = getNewProcedure();
		return newProcedure;
	}

	public String renameEntityEventParam(EntityEventParam theParam, String theNewParamName) {
		ArrayList<EntityEventParamReference> eventParamReferences = this.getReferencesForEventParam(theParam);
		for(EntityEventParamReference entityReference : eventParamReferences)
		{
			IRenamableActionLanguage renamableActionLanguage = EntityRenameManager.lookupRenamableActionLanguageForSyntax(entityReference.getSyntax());
			renamableActionLanguage.renameEventParam(theParam.getName(), theNewParamName);
		}

		String newProcedure = getNewProcedure();
		return newProcedure;
	}

	public void visit(Syntax_Return syntax_Return, int lineNumber) {
		// this method has been intentionally left blank
	}
	
}
