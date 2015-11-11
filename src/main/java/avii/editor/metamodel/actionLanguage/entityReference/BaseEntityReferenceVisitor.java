package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
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
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClass;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelations;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_UnrelateInstance1FromInstance2AcrossRelation;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.validation.IActionLanguageVisitor;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public abstract class BaseEntityReferenceVisitor implements IActionLanguageVisitor {

	private HashMap<EntityClass,ArrayList<EntityClassReference>> _entityClassReferences = new HashMap<EntityClass,ArrayList<EntityClassReference>>();
	private HashMap<EntityEventSpecification,ArrayList<EntityEventReference>> _eventReferences = new HashMap<EntityEventSpecification,ArrayList<EntityEventReference>>();
	private HashMap<EntityRelation,ArrayList<EntityRelationReference>> _relationReferences = new HashMap<EntityRelation,ArrayList<EntityRelationReference>>();
	private HashMap<EntityAttribute,ArrayList<EntityAttributeReference>> _attributeReferences = new HashMap<EntityAttribute,ArrayList<EntityAttributeReference>>();
	private HashMap<EntityEventParam,ArrayList<EntityEventParamReference>> _eventParamReferences = new HashMap<EntityEventParam,ArrayList<EntityEventParamReference>>();
	
	
	protected EntityDomain _domain;
	protected StateInstanceLifespanManager _instanceLifespanManager;
	
	public BaseEntityReferenceVisitor(EntityDomain domain) {
		this._domain = domain;
	}

	public void setInstanceLifespanManager(StateInstanceLifespanManager instanceLifespanManager) {
		this._instanceLifespanManager = instanceLifespanManager;		
	}
	
	/////////////////////////// class references
	protected boolean hasEntityClassBeenReferenced(EntityClass theClass)
	{
		return this._entityClassReferences.containsKey(theClass);
	}
	
	protected ArrayList<EntityClassReference> getReferencesForEntityClass(EntityClass theClass) {
		return this._entityClassReferences.get(theClass);
	}
	
	private void addReference(EntityClassReference entityReference)
	{
		EntityClass theClass = entityReference.getEntityClass();
		if(!this._entityClassReferences.containsKey(theClass))
		{
			this._entityClassReferences.put(theClass,new ArrayList<EntityClassReference>());
		}
		ArrayList<EntityClassReference> referenceList = this._entityClassReferences.get(theClass);
		if(!referenceList.contains(entityReference))
		{
			referenceList.add(entityReference);
			this._entityClassReferences.put(theClass, referenceList);
		}
	}
	///////////////////////////
	
	
/////////////////////////// event references
	protected boolean hasEventBeenReferenced(EntityEventSpecification theEvent)
	{
		return this._eventReferences.containsKey(theEvent);
	}
	
	protected ArrayList<EntityEventReference> getReferencesForEvent(EntityEventSpecification theEvent) {
		return this._eventReferences.get(theEvent);
	}
	
	private void addReference(EntityEventReference entityReference)
	{
		EntityEventSpecification theClass = entityReference.getEvent();
		if(!this._eventReferences.containsKey(theClass))
		{
			this._eventReferences.put(theClass,new ArrayList<EntityEventReference>());
		}
		ArrayList<EntityEventReference> referenceList = this._eventReferences.get(theClass);
		if(!referenceList.contains(entityReference))
		{
			referenceList.add(entityReference);
			this._eventReferences.put(theClass, referenceList);
		}
	}
	///////////////////////////
	
	
	/////////////////////////// relation references
	protected boolean hasRelationBeenReferenced(EntityRelation theRelation)
	{
		return this._relationReferences.containsKey(theRelation);
	}
	
	protected ArrayList<EntityRelationReference> getReferencesForRelation(EntityRelation theRelation) {
		return this._relationReferences.get(theRelation);
	}
	
	private void addReference(EntityRelationReference entityReference)
	{
		EntityRelation theClass = entityReference.getRelation();
		if(!this._relationReferences.containsKey(theClass))
		{
			this._relationReferences.put(theClass,new ArrayList<EntityRelationReference>());
		}
		ArrayList<EntityRelationReference> referenceList = this._relationReferences.get(theClass);
		if(!referenceList.contains(entityReference))
		{
			referenceList.add(entityReference);
			this._relationReferences.put(theClass, referenceList);
		}
	}
	///////////////////////////
	
	
	/////////////////////////// attribute references
	protected boolean hasAttributeBeenReferenced(EntityAttribute theAttribute)
	{
		return this._attributeReferences.containsKey(theAttribute);
	}
	
	protected ArrayList<EntityAttributeReference> getReferencesForAttribute(EntityAttribute theAttribute) {
		return this._attributeReferences.get(theAttribute);
	}
	
	private void addReference(EntityAttributeReference entityReference)
	{
		EntityAttribute theParam = entityReference.getAttribute();
		if(!this._attributeReferences.containsKey(theParam))
		{
			this._attributeReferences.put(theParam,new ArrayList<EntityAttributeReference>());
		}
		ArrayList<EntityAttributeReference> referenceList = this._attributeReferences.get(theParam);
		if(!referenceList.contains(entityReference))
		{
			referenceList.add(entityReference);
			this._attributeReferences.put(theParam, referenceList);
		}
	}
	///////////////////////////
	
	/////////////////////////// event param references
	protected boolean hasEventParamBeenReferenced(EntityEventParam theParam)
	{
		return this._eventParamReferences.containsKey(theParam);
	}
	
	protected ArrayList<EntityEventParamReference> getReferencesForEventParam(EntityEventParam theEventParam) {
		return this._eventParamReferences.get(theEventParam);
	}
	
	private void addReference(EntityEventParamReference entityReference)
	{
		EntityEventParam theParam = entityReference.getEventParam();
		if(!this._eventParamReferences.containsKey(theParam))
		{
			this._eventParamReferences.put(theParam,new ArrayList<EntityEventParamReference>());
		}
		ArrayList<EntityEventParamReference> referenceList = this._eventParamReferences.get(theParam);
		if(!referenceList.contains(entityReference))
		{
			referenceList.add(entityReference);
			this._eventParamReferences.put(theParam, referenceList);
		}
	}
	///////////////////////////
	
	
	
	private boolean doesDomainHaveClassWithName(String className) {
		return _domain.hasEntityClassWithName(className);
	}

	private EntityClass getClassWithNameFromDomain(String className) {
		if (doesDomainHaveClassWithName(className)) {
			return _domain.getEntityClassWithName(className);
		}
		return null;
	}
	
	private void handleSelectInstancesOf(IActionLanguageSyntax syntax, int lineNumber, String className)
	{
		EntityClass theClass = getClassWithNameFromDomain(className);
		
		EntityClassReference reference = new EntityClassReference(lineNumber, syntax, theClass);
		addReference(reference);
	}

	private String identifyInstanceOnLine(int lineNumber, String instanceName) throws NameNotFoundException, OperationNotSupportedException {
		String className = this._instanceLifespanManager.identifyInstance(instanceName, lineNumber);
		return className;
	}
	
	private void findAttributeReferencesInLogicTree(IActionLanguageSyntax syntax, int lineNumber, LogicExpressionTree logicTree) {
		try
		{
			for(ConstantToken node : logicTree.getLeafNodes())
			{
				String tokenValue = node.getTokenValue();
				IActionLanguageToken nodeToken = ActionLanguageTokenIdentifier.IdentifyToken(tokenValue);
				if(nodeToken instanceof ActionLanguageTokenAttribute)
				{
					ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute)nodeToken;
					String instanceName = attributeToken.getInstanceName();
					String attributeName = attributeToken.getAttributeName();
					
					this.createEntityAttributeReference(syntax, lineNumber, instanceName, attributeName);
				}
			}
		}
		catch(Exception e){}
	}
	
	private EntityEventSpecification getEventSpecificationWithNameFromClass(String eventName, String className) throws NameNotFoundException
	{
		EntityClass theClass = this.getClassWithNameFromDomain(className);
		return theClass.getEventSpecificationWithName(eventName);
	}

	private void findAttributeAndEventParamReferencesInEventParams(IActionLanguageSyntax syntax, int lineNumber, HashMap<String, String> params, EntityEventSpecification eventSpecification)
			throws NameNotFoundException, OperationNotSupportedException {
		for(String paramName : params.keySet())
		{
			String paramValue = params.get(paramName);
			EntityEventParam eventParam = eventSpecification.getParamWithName(paramName);
			EntityEventParamReference eventParamReference = new EntityEventParamReference(lineNumber, syntax, eventParam);
			addReference(eventParamReference);
			
			
			IActionLanguageToken paramToken = ActionLanguageTokenIdentifier.IdentifyToken(paramValue);
			if(paramToken instanceof ActionLanguageTokenAttribute)
			{
				ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) paramToken;
				String instanceName = attributeToken.getInstanceName();
				String attributeName = attributeToken.getAttributeName();
				
				this.createEntityAttributeReference(syntax, lineNumber, instanceName, attributeName);
			}
		}
	}	


	private void createEntityAttributeReference(IActionLanguageSyntax syntax, int lineNumber, String instanceName, String attributeName)
			throws NameNotFoundException, OperationNotSupportedException {
		String className = this.identifyInstanceOnLine(lineNumber, instanceName);
		EntityClass theClass = this.getClassWithNameFromDomain(className);
		EntityAttribute theAttribute = theClass.getAttributeWithName(attributeName);
		EntityAttributeReference reference = new EntityAttributeReference(lineNumber, syntax, theAttribute);
		addReference(reference);
	}
	
	public void visit(Syntax_CreateInstanceFromClass syntax, int lineNumber) {
		String className = syntax.get_Class();
		EntityClass theClass = getClassWithNameFromDomain(className);
		
		EntityClassReference reference = new EntityClassReference(lineNumber, syntax, theClass);
		addReference(reference);
	}

	public void visit(Syntax_DeleteInstance syntax_DeleteInstance, int lineNumber) {
		// NA
	}

	public void visit(Syntax_GenerateEventParamsToClassCreator syntax, int lineNumber) {
		try {
			// class reference
			String className = syntax.get_Class();
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityClassReference classReference = new EntityClassReference(lineNumber, syntax, theClass);
			addReference(classReference);

			// event reference
			String eventName = syntax.eventName();
			EntityEventSpecification theEvent = theClass.getEventSpecificationWithName(eventName);
			EntityEventReference eventReference = new EntityEventReference(lineNumber, syntax, theEvent);
			addReference(eventReference);
			
			EntityEventSpecification eventSpec = getEventSpecificationWithNameFromClass(syntax.eventName(), className);
			
			// attribute reference
			HashMap<String,String> params = syntax.getParams();
			findAttributeAndEventParamReferencesInEventParams(syntax, lineNumber, params,eventSpec);
		} catch (Exception e) {
		}
	}

	public void visit(Syntax_GenerateEventParamsToClassCreatorDelayDuration syntax, int lineNumber) {
		try {
			// class reference
			String className = syntax.get_Class();
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityClassReference classReference = new EntityClassReference(lineNumber, syntax, theClass);
			addReference(classReference);

			// event reference
			String eventName = syntax.eventName();
			EntityEventSpecification theEvent = theClass.getEventSpecificationWithName(eventName);
			EntityEventReference eventReference = new EntityEventReference(lineNumber, syntax, theEvent);
			addReference(eventReference);
			
			EntityEventSpecification eventSpec = getEventSpecificationWithNameFromClass(syntax.eventName(), className);
			
			// attribute reference
			HashMap<String,String> params = syntax.getParams();
			findAttributeAndEventParamReferencesInEventParams(syntax, lineNumber, params, eventSpec);
		} catch (Exception e) {
		}
	}

	public void visit(Syntax_GenerateEventParamsToInstance syntax, int lineNumber) {
		try {
			String instanceName = syntax.get_Instance();
			String className = identifyInstanceOnLine(lineNumber, instanceName);
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityClassReference classReference = new EntityClassReference(lineNumber, syntax, theClass);
			addReference(classReference);

			// event reference
			String eventName = syntax.eventName();
			EntityEventSpecification theEvent = theClass.getEventSpecificationWithName(eventName);
			EntityEventReference eventReference = new EntityEventReference(lineNumber, syntax, theEvent);
			addReference(eventReference);
			
			EntityEventSpecification eventSpec = getEventSpecificationWithNameFromClass(syntax.eventName(), className);
			
			// attribute reference
			HashMap<String,String> params = syntax.getParams();
			findAttributeAndEventParamReferencesInEventParams(syntax, lineNumber, params, eventSpec);
		} catch (Exception e) {
		}
	}

	public void visit(Syntax_GenerateEventParamsToInstanceDelayDuration syntax, int lineNumber) {
		try {
			String instanceName = syntax.get_Instance();
			String className = identifyInstanceOnLine(lineNumber, instanceName);
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityClassReference classReference = new EntityClassReference(lineNumber, syntax, theClass);
			addReference(classReference);

			// event reference
			String eventName = syntax.eventName();
			EntityEventSpecification theEvent = theClass.getEventSpecificationWithName(eventName);
			EntityEventReference eventReference = new EntityEventReference(lineNumber, syntax, theEvent);
			addReference(eventReference);
			
			EntityEventSpecification eventSpec = getEventSpecificationWithNameFromClass(syntax.eventName(), className);
			
			// attribute reference
			HashMap<String,String> params = syntax.getParams();
			findAttributeAndEventParamReferencesInEventParams(syntax, lineNumber, params, eventSpec);
		} catch (Exception e) {
		}
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation syntax, int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(
			Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 syntax,
			int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelation syntax, int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3 syntax,
			int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_TempExpression syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClass syntax, int lineNumber) {
		handleSelectInstancesOf(syntax,lineNumber,syntax.get_Class());
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic syntax, int lineNumber) {
		handleSelectInstancesOf(syntax,lineNumber,syntax.get_Class());
		
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
	}

	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelations syntax, int lineNumber) {
		for(IActionLanguageRelation relationSyntax : syntax.get_Relations())
		{
			try{
				String relationName = relationSyntax.get_Name();
				EntityRelation relation = this._domain.getRelationWithName(relationName);
				EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
				addReference(reference);
			}catch(Exception e){}
		}
	}

	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic syntax, int lineNumber) {
		for(IActionLanguageRelation relationSyntax : syntax.get_Relations())
		{
			try{
				String relationName = relationSyntax.get_Name();
				EntityRelation relation = this._domain.getRelationWithName(relationName);
				EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
				addReference(reference);
			}catch(Exception e){}
		}
		
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
	}

	public void visit(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation syntax, int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation syntax,	int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossRelation syntax, int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation syntax, int lineNumber) {
		try{
			String relationName = syntax.get_Relation();
			EntityRelation relation = this._domain.getRelationWithName(relationName);
			EntityRelationReference reference = new EntityRelationReference(lineNumber, syntax, relation);
			addReference(reference);
		}catch(Exception e){}
	}

	public void visit(Syntax_CancelEventnameFromSenderToTarget syntax, int lineNumber) {
		try {
			String instanceName = syntax.get_Target();
			String className = identifyInstanceOnLine(lineNumber, instanceName);
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityClassReference classReference = new EntityClassReference(lineNumber, syntax, theClass);
			addReference(classReference);

			// event reference
			String eventName = syntax.get_Eventname();
			EntityEventSpecification theEvent = theClass.getEventSpecificationWithName(eventName);
			EntityEventReference eventReference = new EntityEventReference(lineNumber, syntax, theEvent);
			addReference(eventReference);
		} catch (Exception e) {
		}
	}

	public void visit(Syntax_ReclassifyInstanceToClass syntax, int lineNumber) {
		String className = syntax.get_Class();
		EntityClass theClass = getClassWithNameFromDomain(className);
		
		EntityClassReference reference = new EntityClassReference(lineNumber, syntax, theClass);
		addReference(reference);
	}

	public void visit(Syntax_ForInstanceInInstanceset syntax_ForInstanceInInstanceset, int lineNumber) {
		// NA
	}

	public void visit(Syntax_EndFor syntax_EndFor, int lineNumber) {
		// NA
	}

	public void visit(Syntax_EndIf syntax_EndIf, int lineNumber) {
		// NA
	}

	public void visit(Syntax_IfEmptyInstancesetThen syntax_IfEmptyInstancesetThen, int lineNumber) {
		// NA
	}

	public void visit(Syntax_IfNotEmptyInstancesetThen syntax_IfNotEmptyInstancesetThen, int lineNumber) {
		// NA
	}

	public void visit(Syntax_Else syntax_Else, int lineNumber) {
		// NA
	}

	public void visit(Syntax_IfLogic syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
	}

	public void visit(Syntax_AttributeExpression syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
		
		try
		{
			String instanceName = syntax.get_Instance();
			String attributeName = syntax.get_Attribute();
			
			createEntityAttributeReference(syntax, lineNumber, instanceName, attributeName);
			
		}catch(Exception e){}
	}

	public void visit(Syntax_FailAssertion syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		findAttributeReferencesInLogicTree(syntax, lineNumber, logicTree);
	}


}
