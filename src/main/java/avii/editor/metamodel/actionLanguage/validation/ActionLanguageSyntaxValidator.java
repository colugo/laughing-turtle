package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser.IInstanceSelectRelatedByActionLanguage;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.ITempDeclarationActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBooleanOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.EncounteredNonItemLeafNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper.ENUM_ANY_MANY;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseIf;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockElse;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockManager;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenIf;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.InstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.lookups.NameDataTypeLineNumber;
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
import main.java.avii.editor.metamodel.actionLanguage.tempLifespan.TempVariableLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenLiteral;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenRcvdEvent;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenTemp;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.AttributeNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.BooleanOrEqualityOperatorUsedInArithmeticExpression;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotChangeIteratingInstanceSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotCreateAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotCreateSuperClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CodeBlockMismatchValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CodeCannotFollowReturnStatementValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CreatorEventIsNotFromInitialState;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.DuplicateIdentifierFoundValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassDoesNotHaveExitingRelationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassIsNotPartOfAGeneralisationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInRelationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassesAreNotInTheSameGeneralisationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeCompared;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotConnectClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationDoesNotHaveAnAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationIsNotReflexiveValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationIsReflexiveValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNeedsAnAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityRelationNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotGeneratedFromClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventParamNotFoundInEventInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventParamNotFoundInEventSpecificationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.FailSyntaxCanOnlyBeUsedInAssertionProceduresValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.GenericLogicExpressionFailureValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceCannotBeTreatedAsAnInstanceSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceNameFromSelectIsManyAndShouldBeAPluralValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InstanceSetCannotBeTreatedAsAnInstance;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InvalidDelayUnitsValidationException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.LogicTreeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.ReflexiveEntityRelationMustHaveSameClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.ReflexiveEntityRelationVerbPhraseMustMatchValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.StateAttributeCanOnlyBeUsedInAssertionProceduresValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.StateAttributeCannotBeModifiedValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.UnknownValidationError;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;
import main.java.avii.scenario.AssertionTestVectorProcedure;

public class ActionLanguageSyntaxValidator implements IActionLanguageValidatorVisitor, IActionLanguageTokenIdentifier {

	private EntityDomain _domain;
	private ArrayList<IActionLanguageValidationError> _errors = new ArrayList<IActionLanguageValidationError>();
	private StateInstanceLifespanManager _lifespanManager;
	private TempVariableLifespanManager _tempManager;
	private CodeBlockManager _codeBlockManager = new CodeBlockManager();
	private final String CLASS_NOT_FOUND = "CLASS_NOT_FOUND";
	private IActionLanguageSyntax _datatypeSyntax;
	private int _datatypeLineNumber = -100;
	private EntityState _theState;
	private HashMap<Integer, IActionLanguageSyntax> _syntaxMap = null;
	private EntityProcedure _procedure;
	
	public ActionLanguageSyntaxValidator(EntityDomain domain, StateInstanceLifespanManager lifespanManager, TempVariableLifespanManager tempManager, HashMap<Integer, IActionLanguageSyntax> syntaxMap) {
		this._domain = domain;
		this._lifespanManager = lifespanManager;
		this._tempManager = tempManager;
		this._syntaxMap  = syntaxMap;
	}

	public void validatingForState(EntityState theState) {
		this._theState = theState;
	}

	
	public void postValidate() {
		checkCodeBlockManagerIsValid();
	}
	
	private void checkCodeBlockManagerIsValid() {
		if(!_codeBlockManager.validate())
		{
			addError(new CodeBlockMismatchValidationError());
		}
	}

	private void addCouldNotFindAttributeOnClassError(IActionLanguageSyntax syntax, int lineNumber, String className, String attributeName) {
		addError(new AttributeNotFoundInClassValidationError(className, lineNumber, syntax, attributeName));
	}

	private void addCouldNotFindClassError(IActionLanguageSyntax syntax, int lineNumber, String className) {
		addError(new EntityClassNotFoundInDomainValidationError(className, lineNumber, syntax));
	}

	private void addError(IActionLanguageValidationError error) {
		if (!doesErrorListHaveErrorAlready(error)) {
			_errors.add(error);
			error.setThrowingState(this._theState);
		}
	}

	private boolean checkClassExistsInDomain(IActionLanguageSyntax syntax, int lineNumber, String className) {
		if (!doesDomainHaveClassWithName(className)) {
			addCouldNotFindClassError(syntax, lineNumber, className);
			return false;
		}
		return true;
	}

	private boolean checkClassExistsInRelationOrAddError(EntityClass theClass, EntityRelation relation, IActionLanguageSyntax syntax, int lineNumber) {
		if (!relation.hasClass(theClass)) {
			addError(new EntityClassNotFoundInRelationValidationError(theClass.getName(), lineNumber, syntax, relation.getName()));
			return false;
		}
		return true;
	}

	private boolean checkClassHasAttribute(IActionLanguageSyntax syntax, int lineNumber, String className, String attributeName) {
		if (!doesClassWithNameHaveAttributeWithName(className, attributeName)) {
			addCouldNotFindAttributeOnClassError(syntax, lineNumber, className, attributeName);
			return false;
		}
		return true;
	}

	private void checkDatatypesForComparissonOrAddError(IActionLanguageSyntax syntax, int lineNumber, IEntityDatatype leftDatatype,
			IEntityDatatype rightDatatype) {
		if (leftDatatype != null && rightDatatype != null) {
			if (!leftDatatype.canBeComparedToDatatype(rightDatatype)) {
				addError(new EntityDatatypesCannotBeCompared(leftDatatype, rightDatatype, syntax, lineNumber));
			}
		}
	}

	private void checkSelectDoesntClobberIteratingInstanceSet(IActionLanguageSyntax syntax,ENUM_ANY_MANY anyMany, String instanceSetName, int lineNumber) {
		if(anyMany == ENUM_ANY_MANY.MANY)
		{
			// check if this line is within a for loop - only for select many
			if(this._codeBlockManager.doesLineFallWithinForLoop(lineNumber))
			{
				ArrayList<CodeBlockOpenFor> openForLoops = this._codeBlockManager.getContainingOpeningBlocksForLine(lineNumber);
				for(CodeBlockOpenFor openForLoop : openForLoops)
				{
					Syntax_ForInstanceInInstanceset openForSyntax = openForLoop.getForSyntax();
					String openForLoopInstanceSetName = openForSyntax.get_Instanceset();
					if(instanceSetName.equals(openForLoopInstanceSetName))
					{
						addError(new CanNotChangeIteratingInstanceSetValidationError(lineNumber, syntax, openForSyntax));
						// no need to further validate this line - it will just confuse the validator
						return;
					}
				}
			}
		}
	}
	
	private void checkDatatypesForSetOrAddError(IActionLanguageSyntax syntax, int lineNumber, IEntityDatatype leftDatatype,
			IEntityDatatype rightDatatype) {
		if (leftDatatype != null && rightDatatype != null) {
			if (!leftDatatype.canBeSetToDatatype(rightDatatype)) {
				addError(new EntityDatatypesCannotBeSet(leftDatatype, rightDatatype, syntax, lineNumber));
			}
		}
	}

	private void checkEntityRelationDoesntHaveAssociationClassAndAddError(IActionLanguageSyntax syntax, int lineNumber, String relationName,
			EntityRelation relation) {
		if (relation != null) {
			if (relation.hasAssociation()) {
				addError(new EntityRelationNeedsAnAssociationClassValidationError(relationName, relation.getAssociation().getName(), lineNumber, syntax));
			}
		}
	}

	private void CheckEventGenerationDelay(IGenerateDelayActionLanguageSyntax syntax, int lineNumber) {
		DelayUnits delayUnits = syntax.getDelayUnit();

		if (delayUnits.equals(DelayUnits.Invalid)) {
			addError(new InvalidDelayUnitsValidationException(syntax, lineNumber, syntax.getRawDelayUnit()));
		}
	}

	private void CheckEventIsFromInitialState(IGenerateActionLanguageSyntax syntax, EntityClass targetClass, EntityEventSpecification theEvent, int lineNumber) {
		// check if the event specification has an event instance that is from the initial state
		if(!targetClass.hasEventInstanceFromState(theEvent, targetClass.getInitialState()))
		{
			addError(new CreatorEventIsNotFromInitialState(targetClass.getName(), theEvent.getName(), targetClass.getInitialState().getName(), lineNumber, syntax));
		}
	}

	private void CheckEventParamatersOrAddErrors(EntityEventSpecification theEvent, IGenerateActionLanguageSyntax syntax, int lineNumber)
	{
		if(theEvent == null)
		{
			return;
		}
		HashMap<String,String> namedParams = syntax.getParams();
		for(String paramName : namedParams.keySet())
		{
			String paramValue = namedParams.get(paramName);
			IActionLanguageToken paramToken = ActionLanguageTokenIdentifier.IdentifyToken(paramValue);
			IEntityDatatype paramDatatype = getDatatypeFromTokenOrAddError(syntax, lineNumber, paramToken);
			if(theEvent.hasParamWithName(paramName))
			{
				try {
					EntityEventParam eventParam = theEvent.getParamWithName(paramName);
					IEntityDatatype eventParamDatatype = eventParam.getType();
					checkDatatypesForSetOrAddError(syntax, lineNumber, eventParamDatatype, paramDatatype);
				} catch (NameNotFoundException e) {
					// this had better work
				}
			}
			else
			{
				addError(new EventParamNotFoundInEventSpecificationValidationError(theEvent, paramName, lineNumber, syntax));
			}
		}
		
		for(EntityEventParam entityParam : theEvent.getEventParams())
		{
			String entityParamName = entityParam.getName();
			if(!namedParams.containsKey(entityParamName))
			{
				addError(new EventParamNotFoundInEventInstanceValidationError(theEvent, entityParamName, lineNumber, syntax));
			}
		}
	}
	
	private void checkAbleToCreateInstanceWithName(String instanceName, int line, IActionLanguageSyntax syntax) {
		try {
			this._lifespanManager.identifyInstance(instanceName, line - 1);
			addError(new DuplicateIdentifierFoundValidationError(instanceName, line, syntax));
		} catch (Exception e) {
			if(e.getMessage().contains(InstanceLifespanManager.DuplicateDeclareError))
			{
				addError(new DuplicateIdentifierFoundValidationError(instanceName, line, syntax));
			}
		}
	}

	private void checkClassHasNoSubClasses(Syntax_CreateInstanceFromClass syntax, int lineNumber, String className) {
		EntityClass theClass = getClassWithNameFromDomain(className);
		if(!theClass.getsubClasses().isEmpty())
		{
			addError(new CanNotCreateSuperClassValidationError(className, lineNumber, syntax));
		}
	}
	
	private void checkClassIsNotAnAssociation(Syntax_CreateInstanceFromClass syntax, int lineNumber, String className) {
		EntityClass theClass = getClassWithNameFromDomain(className);
		if(theClass.isAssociation())
		{
			addError(new CanNotCreateAssociationClassValidationError(className, lineNumber, syntax));
		}
	}

	private void checkLogicExpressionEqualityPairsHaveSameDatatype(LogicExpressionTree logic, IActionLanguageSyntax syntax, int lineNumber)
			throws CannotInterpretExpressionNodeException {
		ArrayList<ILogicNode> equalityParents = logic.getAllNodesOfType(IEqualityOperatorToken.class);
		for (ILogicNode parent : equalityParents) {
			ILogicParentNode booleanParent = (ILogicParentNode) parent;
			IEntityDatatype leftDatatype = null;
			IEntityDatatype rightDatatype = null;

			// identify the datatypes from the tokens
			rightDatatype = getDatatypeFromILogicNodeOrAddError(booleanParent.getRight(), syntax, lineNumber);
			leftDatatype = getDatatypeFromILogicNodeOrAddError(booleanParent.getLeft(), syntax, lineNumber);

			// check the two datatypes can be compared
			checkDatatypesForComparissonOrAddError(syntax, lineNumber, leftDatatype, rightDatatype);
		}
	}

	private void checkLogicExpressionLeafsCanHaveTheirDatatypeIdentified(LogicExpressionTree logic, IActionLanguageSyntax syntax, int lineNumber) {
		ArrayList<ConstantToken> leafTokens;
		try {
			leafTokens = logic.getLeafNodes();
		} catch (EncounteredNonItemLeafNodeException e1) {
			addError(new GenericLogicExpressionFailureValidationError(logic.getRawLogic(), syntax, lineNumber));
			return;
		}
		for (ConstantToken constantToken : leafTokens) {
			getDatatypeFromILogicNodeOrAddError(constantToken, syntax, lineNumber);
		}
	}

	private void checkPluralityOfSelectedNameAndAddError(String instanceName, ENUM_ANY_MANY anyMany, int lineNumber, IActionLanguageSyntax syntax) {
		if (anyMany.equals(ENUM_ANY_MANY.MANY)) {
			if (!instanceName.toLowerCase().endsWith("s")) {
				addError(new InstanceNameFromSelectIsManyAndShouldBeAPluralValidationError(instanceName, lineNumber, syntax));
			}
		} else {
			if (instanceName.toLowerCase().endsWith("s")) {
				addError(new InstanceNameFromSelectIsAnyAndShouldBeASingleValidationError(instanceName, lineNumber, syntax));
			}

		}
	}

	private boolean checkRelationExistsBetweenClassesOrAddError(EntityClass classA, EntityClass classB, EntityRelation relation, int line,
			IActionLanguageSyntax syntax) {
		if (relation.hasClass(classA) && relation.hasClass(classB)) {
			return true;
		}
		addError(new EntityRelationDoesNotConnectClassesValidationError(line, syntax, relation, classA, classB));
		return false;
	}

	private EntityClass checkRelationHasAssociationClassOrAddError(EntityRelation relation, int line, IActionLanguageSyntax syntax) {
		if (!relation.hasAssociation()) {
			addError(new EntityRelationDoesNotHaveAnAssociationClassValidationError(relation.getName(), line, syntax));
			return null;
		}
		return relation.getAssociation();
	}

	private Boolean checkRelationsInSelectCanBeFoundOrAddError(IInstanceSelectRelatedByActionLanguage syntax, int lineNumber) {
		Boolean oneOrMoreRelationsNotFound = false;
		for (IActionLanguageRelation relationDescription : syntax.get_Relations()) {
			String specifiedVerb = relationDescription.get_VerbPhrase();
			EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, relationDescription.get_Name(), syntax);
			if (relation != null && relation.isReflexive()) {
				if (!relation.acceptsVerbPhrase(specifiedVerb)) {
					addError(new ReflexiveEntityRelationVerbPhraseMustMatchValidationError(specifiedVerb, relation, lineNumber, syntax));
				}
			}
			if (relation == null) {
				oneOrMoreRelationsNotFound = true;
			}
		}
		return oneOrMoreRelationsNotFound;
	}
	
	private boolean doesClassWithNameHaveAttributeWithName(String className, String attributeName) {
		if (doesDomainHaveClassWithName(className)) {
			EntityClass entityClass = getClassWithNameFromDomain(className);
			return entityClass.hasAttribute(attributeName);
		}
		return false;
	}

	private boolean doesDomainHaveClassWithName(String className) {
		return _domain.hasEntityClassWithName(className);
	}

	private boolean doesErrorListHaveErrorAlready(IActionLanguageValidationError theNewError) {
		String newErrorString = theNewError.explainError();
		for (IActionLanguageValidationError error : _errors) {
			if (error.explainError().equals(newErrorString)) {
				return true;
			}
		}
		return false;
	}

	private EntityAttribute getAttributeFromInstanceNameAndAttributeName(IActionLanguageSyntax syntax, int lineNumber, String attributeInstanceName,
			String attributeName) {
		String attributeInstanceClassName = identifyInstanceOrAddError(lineNumber, attributeInstanceName, syntax);
		EntityClass entityClass = getClassWithNameFromDomain(attributeInstanceClassName);
		EntityAttribute entityAttribute = null;
		try {
			entityAttribute = entityClass.getAttributeWithName(attributeName);
		} catch (NameNotFoundException e) {
			addCouldNotFindAttributeOnClassError(syntax, lineNumber, attributeInstanceClassName, attributeName);
		}
		return entityAttribute;
	}

	private EntityClass getClassWithNameFromDomain(String className) {
		if (doesDomainHaveClassWithName(className)) {
			return _domain.getEntityClassWithName(className);
		}
		return null;
	}

	private IEntityDatatype getDatatypeFromILogicNodeOrAddError(ILogicNode node, IActionLanguageSyntax syntax, int lineNumber) {
		IEntityDatatype theDatatype = null;
		if (node instanceof ILogicParentNode) {

			ILogicParentNode parent = (ILogicParentNode) node;
			IEntityDatatype leftDatatype = getDatatypeFromILogicNodeOrAddError(parent.getLeft(), syntax, lineNumber);
			IEntityDatatype rightDatatype = getDatatypeFromILogicNodeOrAddError(parent.getRight(), syntax, lineNumber);
			if (leftDatatype.canBeComparedToDatatype(rightDatatype)) {
				return leftDatatype.getResultingDatatypeWhenCombinedWith(rightDatatype);
			}
			theDatatype = InvalidEntityDatatype.getInstance();
		} else {
			IActionLanguageToken theToken = ActionLanguageTokenIdentifier.IdentifyToken(node.getTokenValue());
			theDatatype = getDatatypeFromTokenOrAddError(syntax, lineNumber, theToken);
		}
		return theDatatype;
	}

	private IEntityDatatype getDatatypeFromToken(IActionLanguageSyntax syntax, int lineNumber, IActionLanguageToken token) throws Exception {
		try {
			if (token instanceof ActionLanguageTokenAttribute) {
				ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) token;
				EntityAttribute entityAttribute = getAttributeFromInstanceNameAndAttributeName(syntax, lineNumber, attributeToken.getInstanceName(), attributeToken.getAttributeName());
				
				if(entityAttribute.getName().equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
				{
					if(isNotAssertionProcedure()){
						addError(new StateAttributeCanOnlyBeUsedInAssertionProceduresValidationError());
					}
				}
				
				NameDataTypeLineNumber ndtl = new NameDataTypeLineNumber(attributeToken.getInstanceName()+"."+attributeToken.getAttributeName(), entityAttribute.getType(), lineNumber);
				this._procedure.addNameDatatypeLineNumber(ndtl);
				return entityAttribute.getType();
			}
			if (token instanceof ActionLanguageTokenTemp) {
				ActionLanguageTokenTemp tempToken = (ActionLanguageTokenTemp) token;
				
				//1: huge = 1
				//2: huge = huge + 1
				// I need to use the declaration of huge on line 1 to calculate huge on line 2
				
				// essentailly, when working out the datatype of a temp line, use the previous 
				// declaration of the temp syntax (or lineNumber -1)
				int effectiveLineNumber = lineNumber;
				ITempDeclarationActionLanguageSyntax tempSyntax = _tempManager.identifyTempSyntax(tempToken.getName(), lineNumber);


				// work out if this temp syntax uses itself in evaluation
				Syntax_TempExpression tempExpressionSyntax = (Syntax_TempExpression) tempSyntax;
				LogicExpressionTree tempExpressionTree = tempExpressionSyntax.get_Logic();
				for(ILogicNode leafNode : tempExpressionTree.getLeafNodes())
				{
					if(leafNode.getTokenValue().equals(tempToken.getName()))
					{
						effectiveLineNumber = effectiveLineNumber -1;
						tempSyntax = _tempManager.identifyTempSyntax(tempToken.getName(), effectiveLineNumber);
						break;
					}
				}
								
				if (tempSyntax instanceof Syntax_TempExpression) {
					
					LogicExpressionTree logic = ((Syntax_TempExpression)tempSyntax).get_Logic();
					IEntityDatatype logicDatatype = getRootDatatypeFromLogicNode(logic, tempSyntax, effectiveLineNumber);
					
					NameDataTypeLineNumber ndtl = new NameDataTypeLineNumber(tempSyntax.getTempName(), logicDatatype, lineNumber);
					this._procedure.addNameDatatypeLineNumber(ndtl);
					return logicDatatype;
				}
				throw new Exception();
			}
			if (token instanceof ActionLanguageTokenLiteral) {
				ActionLanguageTokenLiteral literalToken = (ActionLanguageTokenLiteral) token;
				return literalToken.getDatatype();
			}
			
			if (token instanceof ActionLanguageTokenRcvdEvent) {
				ActionLanguageTokenRcvdEvent rcvdEventToken = (ActionLanguageTokenRcvdEvent) token;
				if(this._theState == null)
				{
					addError(new UnknownValidationError("Attempted to identify the datatype of a rcvd_event paramater, but couldn't identify what state this procedure is for. " + syntax.toString()));
				}
				else
				{
					try
					{
						EntityEventSpecification rcvdEventSpec = this._theState.getRcvdEvent();
						if(!rcvdEventSpec.hasParamWithName(rcvdEventToken.getParamName()))
						{
							addError(new EventParamNotFoundInEventSpecificationValidationError(rcvdEventSpec, rcvdEventToken.getParamName(), lineNumber, syntax));
						}
						else
						{
							EntityEventParam rcvdEventParam = rcvdEventSpec.getParamWithName(rcvdEventToken.getParamName());
							NameDataTypeLineNumber ndtl = new NameDataTypeLineNumber("rcvd_event." + rcvdEventParam.getName(), rcvdEventParam.getType(), lineNumber);
							this._procedure.addNameDatatypeLineNumber(ndtl);
							return rcvdEventParam.getType();
						}
					}
					catch(Exception e)
					{
						addError(new UnknownValidationError("Attempted to identify the datatype of a rcvd_event paramater, but couldn't identify what state this procedure is for. " + syntax.toString()));
					}
				}
			}
			
		} catch (Exception e) {
		}
		throw new Exception();
	}

	private IEntityDatatype getDatatypeFromTokenOrAddError(IActionLanguageSyntax syntax, int lineNumber, IActionLanguageToken theToken) {
		IEntityDatatype theDatatype = null;
		try {
			theDatatype = getDatatypeFromToken(syntax, lineNumber, theToken);
		} catch (Exception e) {
			addError(new CouldNotIdentifyDatatypeValidationError(theToken, lineNumber, syntax));
		}
		return theDatatype;
	}

	private EntityEventSpecification getEventForClass(IGenerateActionLanguageSyntax syntax, int lineNumber, String eventName, String className) {
		EntityClass targetClass;
		EntityEventSpecification theEvent;
		if (wasClassFound(className)) {
			boolean classExists = checkClassExistsInDomain(syntax, lineNumber, className);
			if (classExists) {
				targetClass = getClassWithNameFromDomain(className);
				theEvent = getEventSpecificationWithNameFromClassOrAddError(syntax, lineNumber, eventName, targetClass);
				return theEvent;
			}
		}
		return null;
	}

	private EntityEventSpecification getEventSpecificationWithNameFromClassOrAddError(IActionLanguageSyntax syntax, int lineNumber, String eventName, EntityClass targetClass) {
		if (!targetClass.hasEventSpecificationWithName(eventName)) {
			addError(new EventNotFoundInClassValidationError(targetClass.getName(), eventName, lineNumber, syntax));
		} else {
			try {
				EntityEventSpecification theEvent = targetClass.getEventSpecificationWithName(eventName);
				return theEvent;
			} catch (NameNotFoundException e) {
				// this had really better work
				e.printStackTrace();
			}
		}
		return null;
	}

	private EntityRelation getRelationFromDomainOrAddError(int lineNumber, String relationName, IActionLanguageSyntax syntax) {
		if (_domain.hasRelationWithName(relationName)) {
			try {
				return _domain.getRelationWithName(relationName);
			} catch (NameNotFoundException e) {
				// this had really ought to work
				e.printStackTrace();
			}
		}
		addError(new EntityRelationNotFoundInDomainValidationError(relationName, lineNumber, syntax));
		return null;
	}

	public ArrayList<IActionLanguageValidationError> getValidationErrors() {
		return _errors;
	}

	private String identifyInstanceOrAddError(int lineNumber, String instanceName, IActionLanguageSyntax syntax) {
		String className = CLASS_NOT_FOUND;
		if (_lifespanManager.isInvalid()) {
			addError(new CouldNotIdentifyInstanceValidationError(instanceName, lineNumber, syntax));
			return className;
		}
		try {
			className = _lifespanManager.identifyInstance(instanceName, lineNumber);
		} catch (NameNotFoundException e) {
			addError(new CouldNotIdentifyInstanceValidationError(instanceName, lineNumber, syntax));
		} catch (OperationNotSupportedException e) {
			addError(new UnknownValidationError(syntax, lineNumber));
		}
		return className;
	}
	
	private String identifyInstanceSetOrAddError(int lineNumber, String instanceName, IActionLanguageSyntax syntax) {
		String className = CLASS_NOT_FOUND;
		if (_lifespanManager.isInvalid()) {
			addError(new CouldNotIdentifyInstanceValidationError(instanceName, lineNumber, syntax));
			return className;
		}
		try {
			className = _lifespanManager.identifyInstance(instanceName, lineNumber);
		} catch (NameNotFoundException e) {
			addError(new CouldNotIdentifyInstanceSetValidationError(instanceName, lineNumber, syntax));
		} catch (OperationNotSupportedException e) {
			addError(new UnknownValidationError(syntax, lineNumber));
		}
		return className;
	}
	
	private Boolean isInstanceAnInstanceSetOrAddError(int lineNumber, String instanceName, IActionLanguageSyntax syntax) {
		if (_lifespanManager.isInvalid()) {
			addError(new CouldNotIdentifyInstanceValidationError(instanceName, lineNumber, syntax));
			return false;
		}
		try {
			return _lifespanManager.isInstanceAnInstanceSetOnLine(instanceName, lineNumber);
		} catch (NameNotFoundException e) {
			addError(new CouldNotIdentifyInstanceValidationError(instanceName, lineNumber, syntax));
		} catch (OperationNotSupportedException e) {
			addError(new UnknownValidationError(syntax, lineNumber));
		}
		return false;
	}

	private EntityRelation validateRelateNonReflexive(String instanceAName, String instanceBName, String relationName, IActionLanguageSyntax syntax,
			int lineNumber) {
		String classAName = identifyInstanceOrAddError(lineNumber, instanceAName, syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, instanceBName, syntax);

		EntityClass classA = null;
		EntityClass classB = null;
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, relationName, syntax);

		if (wasClassFound(classAName)) {
			classA = getClassWithNameFromDomain(classAName);
		}
		if (wasClassFound(classBName)) {
			classB = getClassWithNameFromDomain(classBName);
		}

		if (classA != null && classB != null && relation != null) {

			EntityClass relationClassA = classA;
			EntityClass relationClassB = classB;
			
			boolean isClassARelationClassA = classA.isClassInParentPath(relation.getClassA());
			if(!isClassARelationClassA)
			{
				relationClassB = classA;
				relationClassA = classB;
			}
			
			boolean isClassAInLineage = relationClassA.getAllSuperClasses().contains(relation.getClassA());
			boolean isClassBInLineage = relationClassB.getAllSuperClasses().contains(relation.getClassB());
			boolean bothEndsOfTheRelationAreCovered = isClassAInLineage && isClassBInLineage;
			
			if(!bothEndsOfTheRelationAreCovered)
			{
				addError(new EntityRelationDoesNotConnectClassesValidationError(lineNumber, syntax, relation, relationClassA, relationClassB));
			}
			
			if (relation.isReflexive()) {
				addError(new EntityRelationIsReflexiveValidationError(lineNumber, syntax, relation.getName()));
			}

			checkClassExistsInRelationOrAddError(classA, relation, syntax, lineNumber);
			checkClassExistsInRelationOrAddError(classB, relation, syntax, lineNumber);
		}
		return relation;
	}

	private EntityRelation validateRelateReflexive(String instanceAName, String instanceBName, String relationName, String verb, IActionLanguageSyntax syntax,
			int lineNumber) {
		String classAName = identifyInstanceOrAddError(lineNumber, instanceAName, syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, instanceBName, syntax);

		EntityClass classA = null;
		EntityClass classB = null;
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, relationName, syntax);

		if (wasClassFound(classAName)) {
			classA = getClassWithNameFromDomain(classAName);
		}
		if (wasClassFound(classBName)) {
			classB = getClassWithNameFromDomain(classBName);
		}

		if (relation != null && !relation.isReflexive()) {
			addError(new EntityRelationIsNotReflexiveValidationError(lineNumber, syntax, relationName));
		} else {

			if (classA != null && classB != null && relation != null) {
				
				boolean isBSuperClassOfA = classA.getAllSuperClasses().contains(classB);
				boolean isASuperClassOfB = classB.getAllSuperClasses().contains(classA);
				boolean nietherIsSuperClassOfTheOther = !(isBSuperClassOfA || isASuperClassOfB);
				
				EntityClass relationClassA = classA;
				EntityClass relationClassB = classB;
				
				boolean isClassARelationClassA = relation.getClassAVerb().equals(verb);
				if(!isClassARelationClassA)
				{
					relationClassB = classA;
					relationClassA = classB;
				}
				
				boolean isClassAInLineage = relationClassA.getAllSuperClasses().contains(relation.getClassA());
				boolean isClassBInLineage = relationClassB.getAllSuperClasses().contains(relation.getClassB());
				boolean bothEndsOfTheRelationAreCovered = isClassAInLineage && isClassBInLineage;
				
				boolean inSameHierarchy = classA.getAllClassesInHierarchy().contains(classB);
				boolean inHierarchyAndEndsCovered = inSameHierarchy && bothEndsOfTheRelationAreCovered;
				
				boolean differentClasses = classA != classB; 
				
				if(!bothEndsOfTheRelationAreCovered)
				{
					addError(new EntityRelationDoesNotConnectClassesValidationError(lineNumber, syntax, relation, classA, classB));
					return relation;
				}
				
				if (differentClasses && nietherIsSuperClassOfTheOther && !(inHierarchyAndEndsCovered)) {
					addError(new ReflexiveEntityRelationMustHaveSameClassesValidationError(classAName, classBName, relationName, lineNumber, syntax));
					return relation;
				}

				boolean classAExistsInRelation = checkClassExistsInRelationOrAddError(classA, relation, syntax, lineNumber);
				if (classAExistsInRelation) {
					if (!relation.getClassAVerb().equals(verb) && !relation.getClassBVerb().equals(verb)) {
						addError(new ReflexiveEntityRelationVerbPhraseMustMatchValidationError(verb, relation, lineNumber, syntax));
					}
				}
			}
		}
		return relation;
	}

	private EntityClass validateSelectFromInstancesOf(IActionLanguageSyntax syntax, int lineNumber, String className, String instanceName, ENUM_ANY_MANY anyMany) {
		EntityClass theClass = null;
		if (doesDomainHaveClassWithName(className)) {
			theClass = getClassWithNameFromDomain(className);
			checkPluralityOfSelectedNameAndAddError(instanceName, anyMany, lineNumber, syntax);
		} else {
			addError(new EntityClassNotFoundInDomainValidationError(className, lineNumber, syntax));
		}
		
		if(anyMany == ENUM_ANY_MANY.ANY)
		{
			checkAbleToCreateInstanceWithName(instanceName, lineNumber, syntax);
		}
		
		return theClass;
	}

	private void validateSelectOneSyntax(String classAName, String classBName, EntityRelation relation, IActionLanguageSyntax syntax, int lineNumber) {
		if (wasClassFound(classAName) && wasClassFound(classBName) && relation != null) {
			EntityClass classA = getClassWithNameFromDomain(classAName);
			EntityClass classB = getClassWithNameFromDomain(classBName);
			
			if(classA == null)
			{
				addError(new EntityClassNotFoundInDomainValidationError(classAName, lineNumber, syntax));
			}
			if(classB == null)
			{
				addError(new EntityClassNotFoundInDomainValidationError(classBName, lineNumber, syntax));
			}

			if(classA == null || classB == null)
			{
				return;
			}
			
			boolean doesRelationConnectClasses = checkRelationExistsBetweenClassesOrAddError(classA, classB, relation, lineNumber, syntax);
			if (doesRelationConnectClasses) {
				checkRelationHasAssociationClassOrAddError(relation, lineNumber, syntax);
			}
		}

	}

	private void validateSelectRelatedBy(IInstanceSelectRelatedByActionLanguage syntax, int lineNumber) {
		
		String initialClassName = identifyInstanceOrAddError(lineNumber, syntax.get_Relations().GetInitialInstance(), syntax);
		if (!wasClassFound(initialClassName)) {
			return;
		}
		EntityClass previousClass = getClassWithNameFromDomain(initialClassName);

		Boolean oneOrMoreRelationsNotFound = checkRelationsInSelectCanBeFoundOrAddError(syntax, lineNumber);
		if (!oneOrMoreRelationsNotFound) {
			for (IActionLanguageRelation relationDescription : syntax.get_Relations()) {
				EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, relationDescription.get_Name(), syntax);
				if (!relation.hasClass(previousClass)) {
					addError(new EntityClassDoesNotHaveExitingRelationValidationError(previousClass, lineNumber, syntax, relation));
					return;
				}
				try{
					previousClass = relation.getOppositeClass(previousClass);
				}
				catch(NameNotFoundException nfne){
					addError(new EntityRelationNotFoundInDomainValidationError(previousClass.getName(), lineNumber, syntax));
					return;
				}
			}
		}
	}

	private void validateUnrelateSyntax(String classAName, String classBName, EntityRelation relation, IActionLanguageSyntax syntax, int lineNumber) {
		if (wasClassFound(classAName) && wasClassFound(classBName) && relation != null) {
			EntityClass classA = getClassWithNameFromDomain(classAName);
			EntityClass classB = getClassWithNameFromDomain(classBName);
			
			if(classA == null)
			{
				addError(new EntityClassNotFoundInDomainValidationError(classAName, lineNumber, syntax));
			}
			if(classB == null)
			{
				addError(new EntityClassNotFoundInDomainValidationError(classBName, lineNumber, syntax));
			}

			if(classA == null || classB == null)
			{
				return;
			}
			
			checkRelationExistsBetweenClassesOrAddError(classA, classB, relation, lineNumber, syntax);
		}

	}

	private void validateLogicExpressionTree(LogicExpressionTree logic, IActionLanguageSyntax syntax, int lineNumber) {
		if (!logic.isValid()) {
			addError(new GenericLogicExpressionFailureValidationError(logic.getInvalidMessage(), syntax, lineNumber));
			return;
		}

		try {
			logic.getRootNode();
			checkLogicExpressionLeafsCanHaveTheirDatatypeIdentified(logic, syntax, lineNumber);
			
			addLogicExpressionTreeValidationErrors(logic, syntax, lineNumber);
			
			//only do this for arithmetic, not string concatenation
			checkLogicExpressionEqualityPairsHaveSameDatatype(logic, syntax, lineNumber);
			
			// check can get root node datatype
			getRootDatatypeFromLogicNode(logic, syntax, lineNumber);
			
			
		} catch (CannotInterpretExpressionNodeException e) {
			addError(new GenericLogicExpressionFailureValidationError(e.getRawExpression(), syntax, lineNumber));
			return;
		}
	}

	private void addLogicExpressionTreeValidationErrors(LogicExpressionTree logic, IActionLanguageSyntax syntax, int lineNumber) {
		// this is important, it sets up the right line for the call back
		this._datatypeLineNumber = lineNumber;
		this._datatypeSyntax = syntax;
		
		// if other errors exist, this will return false positives
		
		if(this._errors.isEmpty())
		{
			if(!logic.validate(this))
			{
				for(LogicTreeValidationException validationError : logic.getValidationErrors())
				{
					this.addError(new LogicTreeValidationError(lineNumber, syntax, validationError));
				}
			}
		}
	}

	public void visit(Syntax_CancelEventnameFromSenderToTarget syntax, int lineNumber) {
		// cancel eventname from sender to target
		// 1. find target type
		// 2. check target type accepts event name (in any state)
		// 3. find sender type
		// 4. determine if sender creates event name (from any procedure)
		
		String eventName = syntax.get_Eventname();
		String senderClassName = identifyInstanceOrAddError(lineNumber, syntax.get_Sender(), syntax);
		String targetClassName = identifyInstanceOrAddError(lineNumber, syntax.get_Target(), syntax);
		EntityClass senderClass = getClassWithNameFromDomain(senderClassName);
		EntityClass targetClass = getClassWithNameFromDomain(targetClassName);
		if(senderClass == null)
		{
			addCouldNotFindClassError(syntax,lineNumber,senderClassName);
		}
		if(targetClass == null)
		{
			addCouldNotFindClassError(syntax,lineNumber,targetClassName);
		}
		if(senderClass == null || targetClass == null)
		{
			return;
		}
		
		// both classes exist
		
		// add error if event is not accepted by target class
		getEventSpecificationWithNameFromClassOrAddError(syntax, lineNumber, eventName, targetClass);
		try {
			// check sender generates this event
			ArrayList<String> generatedEventNames = senderClass.getGeneratedEventNames();
			if(!generatedEventNames.contains(eventName))
			{
				addError(new EventNotGeneratedFromClassValidationError(senderClassName,eventName,lineNumber,syntax));
			}
		} catch (InvalidActionLanguageSyntaxException e) {
			addError(new UnknownValidationError("There were too many problems with the action language to validate this procedure"));
		}


	}

	public void visit(Syntax_CreateInstanceFromClass syntax, int lineNumber) {
		String className = syntax.get_Class();
		boolean classFound = checkClassExistsInDomain(syntax, lineNumber, className);
		if(!classFound)
		{
			return;
		}
		
		checkAbleToCreateInstanceWithName(syntax.get_Instance(), lineNumber, syntax);
		
		checkClassHasNoSubClasses(syntax, lineNumber, className);
		checkClassIsNotAnAssociation(syntax, lineNumber, className);
	}

	public void visit(Syntax_DeleteInstance syntax, int lineNumber) {
		String instanceName = syntax.get_Instance();
		identifyInstanceOrAddError(lineNumber, instanceName, syntax);
	}

	public void visit(Syntax_GenerateEventParamsToClassCreator syntax, int lineNumber) {
		String className = syntax.get_Class();
		String eventName = syntax.get_eventName();
		EntityClass targetClass = getClassWithNameFromDomain(className);
		EntityEventSpecification theEvent = getEventForClass(syntax, lineNumber, eventName, className);
		if (theEvent != null) {
			CheckEventIsFromInitialState(syntax, targetClass, theEvent, lineNumber);
		}
		CheckEventParamatersOrAddErrors(theEvent,syntax, lineNumber);
	}

	public void visit(Syntax_GenerateEventParamsToClassCreatorDelayDuration syntax, int lineNumber) {
		String className = syntax.get_Class();
		String eventName = syntax.get_eventName();
		EntityClass targetClass = getClassWithNameFromDomain(className);
		EntityEventSpecification theEvent = getEventForClass(syntax, lineNumber, eventName, className);
		if (theEvent != null) {
			CheckEventGenerationDelay(syntax, lineNumber);
			CheckEventIsFromInitialState(syntax, targetClass, theEvent, lineNumber);
		}
		CheckEventParamatersOrAddErrors(theEvent,syntax, lineNumber);
	}

	public void visit(Syntax_GenerateEventParamsToInstance syntax, int lineNumber) {
		String instanceName = syntax.get_Instance();
		String eventName = syntax.get_eventName();
		String className = identifyInstanceOrAddError(lineNumber, instanceName, syntax);
		EntityEventSpecification theEvent = getEventForClass(syntax, lineNumber, eventName, className);
		CheckEventParamatersOrAddErrors(theEvent,syntax, lineNumber);
	}

	public void visit(Syntax_GenerateEventParamsToInstanceDelayDuration syntax, int lineNumber) {
		String instanceName = syntax.get_Instance();
		String eventName = syntax.get_eventName();
		EntityEventSpecification theEvent = null;
		// identify the instance

		String className = identifyInstanceOrAddError(lineNumber, instanceName, syntax);
		theEvent = getEventForClass(syntax, lineNumber, eventName, className);
		CheckEventGenerationDelay(syntax, lineNumber);
		CheckEventParamatersOrAddErrors(theEvent,syntax, lineNumber);
	}
	
	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelation syntax, int lineNumber) {
		String instanceAName = syntax.get_Instance1();
		String instanceBName = syntax.get_Instance2();
		String relationName = syntax.get_Relation();
		String verb = syntax.get_VerbPhrase();
		EntityRelation relation = validateRelateReflexive(instanceAName, instanceBName, relationName, verb, syntax, lineNumber);
		checkEntityRelationDoesntHaveAssociationClassAndAddError(syntax, lineNumber, relationName, relation);
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3 syntax, int lineNumber) {
		String instanceAName = syntax.get_Instance1();
		String instanceBName = syntax.get_Instance2();
		String relationName = syntax.get_Relation();
		String verb = syntax.get_VerbPhrase();
		validateRelateReflexive(instanceAName, instanceBName, relationName, verb, syntax, lineNumber);
		
		checkAbleToCreateInstanceWithName(syntax.get_Instance3(), lineNumber, syntax);
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelation syntax, int lineNumber) {
		String instanceAName = syntax.get_Instance1();
		String instanceBName = syntax.get_Instance2();
		String relationName = syntax.get_Relation();

		EntityRelation relation = validateRelateNonReflexive(instanceAName, instanceBName, relationName, syntax, lineNumber);
		checkEntityRelationDoesntHaveAssociationClassAndAddError(syntax, lineNumber, relationName, relation);
	}

	public void visit(Syntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3 syntax, int lineNumber) {
		String instanceAName = syntax.get_Instance1();
		String instanceBName = syntax.get_Instance2();
		String relationName = syntax.get_Relation();

		validateRelateNonReflexive(instanceAName, instanceBName, relationName, syntax, lineNumber);
		checkAbleToCreateInstanceWithName(syntax.get_Instance3(), lineNumber, syntax);
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClass syntax, int lineNumber) {
		String className = syntax.get_Class();
		String instanceName = syntax.get_Instance();
		ENUM_ANY_MANY anyMany = syntax.get_AnyMany();

		validateSelectFromInstancesOf(syntax, lineNumber, className, instanceName, anyMany);
	}

	public void visit(Syntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic syntax, int lineNumber) {
		String className = syntax.get_Class();
		String instanceName = syntax.get_Instance();
		ENUM_ANY_MANY anyMany = syntax.get_AnyMany();

		EntityClass theClass = validateSelectFromInstancesOf(syntax, lineNumber, className, instanceName, anyMany);
		if (theClass != null) {
			validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
		}
	}

	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelations syntax, int lineNumber) {
		
		checkSelectDoesntClobberIteratingInstanceSet(syntax, syntax.get_AnyMany(),syntax.get_Instance1(), lineNumber);
		
		validateSelectRelatedBy(syntax, lineNumber);
	}


	public void visit(Syntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic syntax, int lineNumber) {
		checkSelectDoesntClobberIteratingInstanceSet(syntax, syntax.get_AnyMany(),syntax.get_Instance1(), lineNumber);
		validateSelectRelatedBy(syntax, lineNumber);
		validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
	}
	
	public void visit(
			Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation syntax,	int lineNumber) {
		String classAName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance2(), syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance3(), syntax);
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, syntax.get_Relation(), syntax);

		validateSelectOneSyntax(classAName, classBName, relation, syntax, lineNumber);
	}

	public void visit(Syntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation syntax, int lineNumber) {

		String classAName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance2(), syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance3(), syntax);
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, syntax.get_Relation(), syntax);

		validateSelectOneSyntax(classAName, classBName, relation, syntax, lineNumber);
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation syntax, int lineNumber) {
		String classAName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance1(), syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance2(), syntax);
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, syntax.get_Relation(), syntax);

		validateUnrelateSyntax(classAName, classBName, relation, syntax, lineNumber);
	}

	public void visit(Syntax_UnrelateInstance1FromInstance2AcrossRelation syntax, int lineNumber) {
		String classAName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance1(), syntax);
		String classBName = identifyInstanceOrAddError(lineNumber, syntax.get_Instance2(), syntax);
		EntityRelation relation = getRelationFromDomainOrAddError(lineNumber, syntax.get_Relation(), syntax);

		validateUnrelateSyntax(classAName, classBName, relation, syntax, lineNumber);
	}

	private boolean wasClassFound(String className) {
		return !className.equals(CLASS_NOT_FOUND);
	}

	public void visit(Syntax_ReclassifyInstanceToClass syntax, int lineNumber) {
		String instanceName = syntax.get_Instance();
		String className = syntax.get_Class();
		String instanceClassName = identifyInstanceOrAddError(lineNumber, instanceName, syntax);
		
		EntityClass instanceClass = null;
		EntityClass targetClass = null;
		// find classes
		if(wasClassFound(instanceClassName))
		{
			instanceClass = getClassWithNameFromDomain(instanceClassName);
		}
		targetClass = getClassWithNameFromDomain(className);
		if(targetClass == null)
		{
			addError(new EntityClassNotFoundInDomainValidationError(className, lineNumber, syntax));
		}
		
		// ensure both classes exist
		if(targetClass == null || instanceClass == null)
		{
			return;
		}
		
		// ensure both classes are part of a generalisation
		if(!targetClass.isGeneralisation() || !instanceClass.isGeneralisation())
		{
			if(!targetClass.isGeneralisation())
			{
				addError(new EntityClassIsNotPartOfAGeneralisationValidationError(className, lineNumber, syntax));
			}
			if(!instanceClass.isGeneralisation())
			{
				addError(new EntityClassIsNotPartOfAGeneralisationValidationError(instanceClassName, lineNumber, syntax));
			}
			return;
		}
		
		// ensure classes are in the same generalisation
		if(!targetClass.isInGeneralisationWith(instanceClass))
		{
			addError(new EntityClassesAreNotInTheSameGeneralisationValidationError(targetClass, instanceClass, syntax, lineNumber));
		}
		
	}

	public void visit(Syntax_ForInstanceInInstanceset syntax, int lineNumber) {
		String instanceSetName = syntax.get_Instanceset();
		String instanceSetClassName = identifyInstanceSetOrAddError(lineNumber,instanceSetName,syntax);
		
		checkAbleToCreateInstanceWithName(syntax.get_Instance(), lineNumber, syntax);
		
		CodeBlockOpenFor openForBlock = new CodeBlockOpenFor();
		openForBlock.setForSyntax(syntax);
		if(!_codeBlockManager.addBlock(openForBlock, lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(syntax,lineNumber));
		}
		
		if(wasClassFound(instanceSetClassName))
		{
			Boolean isInstanceSetLabelAnInstanceSet = false;
			try {
				isInstanceSetLabelAnInstanceSet = _lifespanManager.isInstanceAnInstanceSetOnLine(instanceSetName, lineNumber);
			} catch (Exception e) {
				// we already know the lifespanManager has the instanceSetName
			}
			if(!isInstanceSetLabelAnInstanceSet)
			{
				addError(new InstanceCannotBeTreatedAsAnInstanceSet(instanceSetName, lineNumber, syntax));
			}
		}
	}

	public void visit(Syntax_EndFor syntax, int lineNumber) {
		if(!_codeBlockManager.addBlock(new CodeBlockCloseFor(), lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(syntax,lineNumber));
		}
	}

	public void visit(Syntax_EndIf syntax, int lineNumber) {
		if(!_codeBlockManager.addBlock(new CodeBlockCloseIf(), lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(syntax,lineNumber));
		}
	}

	public void visit(Syntax_IfEmptyInstancesetThen syntax, int lineNumber) {
		validateIfInstancesetSyntax(syntax.get_Instance(), syntax, lineNumber);
	}
	
	public void visit(Syntax_IfNotEmptyInstancesetThen syntax, int lineNumber) {
		validateIfInstancesetSyntax(syntax.get_Instance(), syntax, lineNumber);
	}
	
	private void validateIfInstancesetSyntax(String instanceName, IActionLanguageSyntax syntax, int lineNumber) {
		identifyInstanceSetOrAddError(lineNumber,instanceName,syntax);
		if(!_codeBlockManager.addBlock(new CodeBlockOpenIf(), lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(syntax,lineNumber));
		}
	}

	public void visit(Syntax_Else syntax_Else, int lineNumber) {
		if(!_codeBlockManager.addBlock(new CodeBlockElse(), lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(new CodeBlockElse()));
		}
	}

	public void visit(Syntax_IfLogic syntax, int lineNumber) {
		if(!_codeBlockManager.addBlock(new CodeBlockOpenIf(), lineNumber))
		{
			addError(new CodeBlockMismatchValidationError(syntax,lineNumber));
		}
		
		validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
		
	}

	public void visit(Syntax_AttributeExpression syntax, int lineNumber) {
		String instanceName = syntax.get_Instance();
		String attributeName = syntax.get_Attribute();
		String className = identifyInstanceOrAddError(lineNumber, instanceName, syntax);

		if(attributeName.equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
		{
			addError(new StateAttributeCannotBeModifiedValidationError());
		}
		
		if (wasClassFound(className)) {
			boolean doesClassExist = checkClassExistsInDomain(syntax, lineNumber, className);
			if (doesClassExist) {

				// check if instance is an instanceset
				Boolean isInstanceSet = isInstanceAnInstanceSetOrAddError(lineNumber, instanceName, syntax);
				if (isInstanceSet) {
					addError(new InstanceSetCannotBeTreatedAsAnInstance(instanceName, lineNumber, syntax));
					return;
				}

				boolean doesAttributeExist = checkClassHasAttribute(syntax, lineNumber, className, attributeName);
				if (doesAttributeExist) {
					try
					{
						EntityClass theClass = getClassWithNameFromDomain(className);
						EntityAttribute attribute = theClass.getAttributeWithName(attributeName);
						
						IEntityDatatype logicDatatype = getRootDatatypeFromLogicNode(syntax.get_Logic(), syntax, lineNumber);
						
						if(!attribute.getType().canBeSetToDatatype(logicDatatype)){
							addError(new EntityDatatypesCannotBeSet(attribute.getType(), logicDatatype, syntax, lineNumber));
						}
					}
					catch(Exception e)
					{
					}
				}
			}
		}
		validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);		
		ensureLogicIsValidArithmeticExpression(syntax.get_Logic(), syntax, lineNumber);
	}

	private HashMap<String, IEntityDatatype> _logicStringLookup = new HashMap<String, IEntityDatatype>();
	private IEntityDatatype getRootDatatypeFromLogicNode(LogicExpressionTree logic, IActionLanguageSyntax syntax, int lineNumber) throws CannotInterpretExpressionNodeException {
		if(this._logicStringLookup.containsKey(logic.getRawLogic()))
		{
			return this._logicStringLookup.get(logic.getRawLogic());
		}
		this._datatypeLineNumber = lineNumber;
		this._datatypeSyntax = syntax;
		IEntityDatatype logicDatatype = logic.getRootNode().getDatatype(this);
		_logicStringLookup.put(logic.getRawLogic(), logicDatatype);
		return logicDatatype;
	}

	public IEntityDatatype getDatatypeForToken(IActionLanguageToken token) {
		try {
			return getDatatypeFromToken(_datatypeSyntax, _datatypeLineNumber, token);
		} catch (Exception e) {
		}
		return InvalidEntityDatatype.getInstance();
	}

	private boolean hasTempVariableBeenPreviouslyDefined(String tempName, int lineNumber)
	{
		try {
			_tempManager.identifyTempSyntax(tempName, lineNumber - 1);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	public void visit(Syntax_TempExpression syntax, int lineNumber) {
		String tempName = syntax.getTempName();
		if(hasTempVariableBeenPreviouslyDefined(tempName, lineNumber))
		{	
			ActionLanguageTokenTemp tempToken = new ActionLanguageTokenTemp(tempName);
			IEntityDatatype tempDatatype = InvalidEntityDatatype.getInstance();
			try {
				tempDatatype = getDatatypeFromToken(syntax, lineNumber, tempToken);
			} catch (Exception e) {
				addError(new CouldNotIdentifyDatatypeValidationError(tempToken, lineNumber, syntax));
				return;
			}
			
			validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
			
			ensureLogicIsValidArithmeticExpression(syntax.get_Logic(), syntax, lineNumber);
	
			IEntityDatatype logicDatatype = InvalidEntityDatatype.getInstance();
			try {
				logicDatatype = getRootDatatypeFromLogicNode(syntax.get_Logic(), syntax, lineNumber);
			} catch (CannotInterpretExpressionNodeException e) {
				addError(new GenericLogicExpressionFailureValidationError(e.getRawExpression(), syntax, lineNumber));
			}
			
			if(!tempDatatype.canBeSetToDatatype(logicDatatype)){
				addError(new EntityDatatypesCannotBeSet(tempDatatype, logicDatatype, syntax, lineNumber));
			}
		}
		else
		{
			validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
		}
	}

	private void ensureLogicIsValidArithmeticExpression(LogicExpressionTree Logic, IActionLanguageSyntax syntax, int lineNumber) {
		// arithmetic cannot have AND or OR 
		try
		{
			if(!Logic.getAllNodesOfType(IEqualityOperatorToken.class).isEmpty())
			{
				addError(new BooleanOrEqualityOperatorUsedInArithmeticExpression());
			}
			if(!Logic.getAllNodesOfType(IBooleanOperatorToken.class).isEmpty())
			{
				addError(new BooleanOrEqualityOperatorUsedInArithmeticExpression());
			}
		}catch(Exception e)
		{
			addError(new UnknownValidationError("Error validating expression " + Logic.getRawLogic()));
		}
	}
	
	public void visit(Syntax_Return syntax_Return, int lineNumber) {
	
		ReturnSyntaxValidator returnValidator = new ReturnSyntaxValidator(this._syntaxMap);
		// returns are only valid as the last line in the procedure, or the last line in an if block (else or end if)
		
		for(Integer i : returnValidator.getLinesWithInvalidReturns())
		{
			addError(new CodeCannotFollowReturnStatementValidationError(i));
		}
	}

	public void visit(Syntax_FailAssertion syntax, int lineNumber) {
		if(isNotAssertionProcedure())
		{
			addError(new FailSyntaxCanOnlyBeUsedInAssertionProceduresValidationError());
		}
		validateLogicExpressionTree(syntax.get_Logic(), syntax, lineNumber);
		ensureLogicIsValidArithmeticExpression(syntax.get_Logic(), syntax, lineNumber);
	}

	public void setProcedure(EntityProcedure entityProcedure) {
		this._procedure = entityProcedure;		
	}

	private boolean isNotAssertionProcedure()
	{
		if(this._procedure instanceof AssertionTestVectorProcedure)
		{
			return false;
		}
		return true;
	}
	
}
