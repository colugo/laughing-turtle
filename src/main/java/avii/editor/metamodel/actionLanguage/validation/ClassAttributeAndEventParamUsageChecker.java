package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.EncounteredNonItemLeafNodeException;
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
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenRcvdEvent;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;

public class ClassAttributeAndEventParamUsageChecker implements IActionLanguageVisitor {

	private EntityAttribute _attribute;
	private EntityClass _class;
	private EntityDomain _domain;
	private StateInstanceLifespanManager _lifespanManager;
	private EntityEventParam _eventParam;
	private boolean _wasAttributeSet = false;
	private boolean _wasAttributeRead = false;
	private boolean _wasEventParamRead;
	private final String CLASS_NOT_FOUND = "CLASS_NOT_FOUND";
	
	
	public ClassAttributeAndEventParamUsageChecker(EntityClass theClass, EntityAttribute theAttribute, EntityDomain domain, StateInstanceLifespanManager instanceLifespanManager) {
		this._class = theClass;
		this._attribute = theAttribute;
		this._lifespanManager = instanceLifespanManager;
		this._domain = domain;
	}

	public ClassAttributeAndEventParamUsageChecker(EntityEventParam eventParam, EntityDomain domain, StateInstanceLifespanManager instanceLifespanManager) {
		this._eventParam = eventParam;
		this._lifespanManager = instanceLifespanManager;
		this._domain = domain;
	}

	private void foundAttributeSetter() {
		this._wasAttributeSet = true;
	}
	
	private void foundAttributeGetter() {
		this._wasAttributeRead = true;
	}
	
	private void foundEventParamReader()
	{
		this._wasEventParamRead = true;
	}
	
	public boolean wasAttributeSet() {
		return this._wasAttributeSet;
	}
	
	public boolean wasAttributeRead() {
		return this._wasAttributeRead;
	}
	
	public boolean wasEventParamRead() {
		return this._wasEventParamRead;
	}

	public void visit(Syntax_GenerateEventParamsToClassCreator syntax_GenerateEventParamsToClassCreator, int lineNumber) {
	}

	public void visit(Syntax_GenerateEventParamsToClassCreatorDelayDuration syntax_GenerateEventParamsToClassCreatorDelayDuration, int lineNumber) {
	}

	public void visit(Syntax_GenerateEventParamsToInstance syntax_GenerateEventParamsToInstance, int lineNumber) {
	}

	public void visit(Syntax_GenerateEventParamsToInstanceDelayDuration syntax_GenerateEventParamsToInstanceDelayDuration, int lineNumber) {
	}

	public void visit(Syntax_TempExpression syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}

	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}

	public void visit(Syntax_IfLogic syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}

	public void visit(Syntax_AttributeExpression syntax, int lineNumber) {
		// check for attribute setter
		try
		{
			String instanceName = syntax.get_Instance();
			String attributeName = syntax.get_Attribute();
			String className = identifyInstance(lineNumber, instanceName);
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityAttribute theAttribute = theClass.getAttributeWithName(attributeName);
			if(isThisTheAttributeWereLookingFor(theClass, theAttribute))
			{
				foundAttributeSetter();
			}
		}
		catch(Exception e)
		{
			// I don't care about exceptions here, as they should all have been picked up during validation
		}
		
		// now check for attribute getter
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}
	
	// unused syntax lines ----------------------------------------------------------

	public void visit(Syntax_CreateInstanceFromClass syntax_CreateInstanceFromClass, int lineNumber) {
	}

	public void visit(Syntax_DeleteInstance syntax_DeleteInstance, int lineNumber) {
	}
	
	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation syntax_RelateInstance1ToInstance2AcrossReflexiveRelation, int lineNumber) {
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3,int lineNumber) {
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelation syntax_RelateInstance1ToInstance2AcrossRelation, int lineNumber) {
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3 syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3, int lineNumber) {
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClass syntax_SelectAnyManyInstanceFromInstancesOfClass, int lineNumber) {
	}

	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelations syntax_SelectAnyManyInstancesRelatedByRelations, int lineNumber) {
	}

	public void visit(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation, int lineNumber) {
	}

	public void visit(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation, int lineNumber) {
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossRelation syntax_UnrelateInstance1FromInstance2AcrossRelation, int lineNumber) {
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation, int lineNumber) {
	}

	public void visit(Syntax_CancelEventnameFromSenderToTarget syntax_CancelEventnameFromSenderToTarget, int lineNumber) {
	}

	public void visit(Syntax_ReclassifyInstanceToClass syntax_ReclassifyInstance1ToInstance2, int lineNumber) {
	}

	public void visit(Syntax_ForInstanceInInstanceset syntax_ForInstanceInInstanceset, int lineNumber) {
	}

	public void visit(Syntax_EndFor syntax_EndFor, int lineNumber) {
	}

	public void visit(Syntax_EndIf syntax_EndIf, int lineNumber) {
	}

	public void visit(Syntax_IfEmptyInstancesetThen syntax_IfEmptyInstancesetThen, int lineNumber) {
	}

	public void visit(Syntax_IfNotEmptyInstancesetThen syntax_IfNotEmptyInstancesetThen, int lineNumber) {
	}

	public void visit(Syntax_Else syntax_Else, int lineNumber) {
	}
	// helpers ----------------------------------------------------------------------

	
	private void checkLogicTreeForAttributeAndEventParamRead(int lineNumber, LogicExpressionTree logicTree) {
		if(doesExpressoinTreeReadAttribute(lineNumber, logicTree))
		{
			foundAttributeGetter();
		}
		if(doesExpressoinTreeReadEventParam(lineNumber,logicTree))
		{
			foundEventParamReader();
		}
	}
	
	private boolean doesDomainHaveClassWithName(String className) {
		return _domain.hasEntityClassWithName(className);
	}

	private EntityClass getClassWithNameFromDomain(String className) {
		if (doesDomainHaveClassWithName(className)) {
			return _domain.getEntityClassWithName(className);
		}
		return null;
	}
	
	private String identifyInstance(int lineNumber, String instanceName) throws NameNotFoundException, OperationNotSupportedException {
		String className = CLASS_NOT_FOUND;
		className = _lifespanManager.identifyInstance(instanceName, lineNumber);
		return className;
	}

	private EntityAttribute getAttributeFromInstanceAndAttributeName(String instanceName, String attributeName, int lineNumber)
	{
		try
		{
			String className = identifyInstance(lineNumber, instanceName);
			EntityClass theClass = getClassWithNameFromDomain(className);
			EntityAttribute entityAttribute = theClass.getAttributeWithName(attributeName);
			return entityAttribute;
		}
		catch(Exception e)
		{
			// I don't care about exceptions here, as they should all have been picked up during validation
			return null;
		}
	}
	
	private boolean isThisTheAttributeWereLookingFor(EntityClass theClass, EntityAttribute theAttribute)
	{
		if(theClass.equals(this._class))
		{
			if(this._attribute.equals(theAttribute))
			{
				return true;
			}
		}
		return false;
	}
	

	private boolean isThisTheEventParamWereLookingFor(String foundParamName) {
		if(foundParamName.equals(_eventParam.getName()))
		{
			return true;
		}
		return false;
	}
	
	
	
	private boolean doesExpressoinTreeReadAttribute(int lineNumber, LogicExpressionTree logicTree) {
		ArrayList<ConstantToken> leafTokens;
		try {
			leafTokens = logicTree.getLeafNodes();
		} catch (EncounteredNonItemLeafNodeException e1) {
			return false;
		}
		for (ConstantToken constantToken : leafTokens) {
			IActionLanguageToken theToken = ActionLanguageTokenIdentifier.IdentifyToken(constantToken.getTokenValue());
			if (theToken instanceof ActionLanguageTokenAttribute) {
				try
				{
					ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) theToken;
					String instanceName = attributeToken.getInstanceName();
					String attributeName = attributeToken.getAttributeName();
					String className = identifyInstance(lineNumber, instanceName);
					EntityClass theClass = getClassWithNameFromDomain(className);
					EntityAttribute theAttribute = getAttributeFromInstanceAndAttributeName(instanceName,attributeName,lineNumber);
					
					if(isThisTheAttributeWereLookingFor(theClass, theAttribute))
					{
						return true;
					}
				}
				catch(Exception e)
				{
					// I don't care about exceptions here, as they should all have been picked up during validation
				}
			}
		}
		return false;
	}
	
	
	private boolean doesExpressoinTreeReadEventParam(int lineNumber, LogicExpressionTree logicTree) {
		ArrayList<ConstantToken> leafTokens;
		try {
			leafTokens = logicTree.getLeafNodes();
		} catch (EncounteredNonItemLeafNodeException e1) {
			return false;
		}
		for (ConstantToken constantToken : leafTokens) {
			IActionLanguageToken theToken = ActionLanguageTokenIdentifier.IdentifyToken(constantToken.getTokenValue());
			if (theToken instanceof ActionLanguageTokenRcvdEvent) {
				try
				{
					ActionLanguageTokenRcvdEvent eventToken = (ActionLanguageTokenRcvdEvent) theToken;
					String foundParamName = eventToken.getParamName();
					if(isThisTheEventParamWereLookingFor(foundParamName))
					{
						return true;
					}
				}
				catch(Exception e)
				{
					// I don't care about exceptions here, as they should all have been picked up during validation
				}
			}
		}
		return false;
	}

	public void visit(Syntax_Return syntax_Return, int lineNumber) {
		// this method has been intentionally left blank
	}

	public void visit(Syntax_FailAssertion syntax, int lineNumber) {
		LogicExpressionTree logicTree = syntax.get_Logic();
		checkLogicTreeForAttributeAndEventParamRead(lineNumber, logicTree);
	}

}
