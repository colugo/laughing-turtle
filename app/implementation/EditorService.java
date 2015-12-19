package implementation;


import java.util.Collection;

import main.java.models.JsonClassDiagramCoordinatesHelper;
import main.java.avii.editor.command.EditorCommand_AddAttributeToClass;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddEventParam;
import main.java.avii.editor.command.EditorCommand_AddRelationBetweenClasses;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_AddStateTransition;
import main.java.avii.editor.command.EditorCommand_ChangeAttributeDatatype;
import main.java.avii.editor.command.EditorCommand_ChangeClassDiagramCoordinates;
import main.java.avii.editor.command.EditorCommand_ChangeEventSpecificationParamType;
import main.java.avii.editor.command.EditorCommand_ChangeRelation;
import main.java.avii.editor.command.EditorCommand_ChangeStateTransition;
import main.java.avii.editor.command.EditorCommand_ChangeSuperClass;
import main.java.avii.editor.command.EditorCommand_CreateAssociation;
import main.java.avii.editor.command.EditorCommand_CreateEventSpecification;
import main.java.avii.editor.command.EditorCommand_DeleteAttributeFromClass;
import main.java.avii.editor.command.EditorCommand_DeleteClass;
import main.java.avii.editor.command.EditorCommand_DeleteEventSpecification;
import main.java.avii.editor.command.EditorCommand_DeleteEventSpecificationParam;
import main.java.avii.editor.command.EditorCommand_DeleteRelation;
import main.java.avii.editor.command.EditorCommand_DeleteState;
import main.java.avii.editor.command.EditorCommand_EditStateActionLanguage;
import main.java.avii.editor.command.EditorCommand_RenameAttribute;
import main.java.avii.editor.command.EditorCommand_RenameClass;
import main.java.avii.editor.command.EditorCommand_RenameEventSpecification;
import main.java.avii.editor.command.EditorCommand_RenameEventSpecificationParam;
import main.java.avii.editor.command.EditorCommand_RenameRelation;
import main.java.avii.editor.command.EditorCommand_RenameState;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.command.helper.GetAttributeDatatypeHelper;
import main.java.avii.editor.command.helper.GetStateActionLanguageHelper;
import main.java.avii.editor.command.helper.GetStateNamesForClassHelper;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;
import main.java.avii.editor.service.IUUIDIdentifier;

public class EditorService{
	
	private void throwCommandExceptionIfRequired(IEditorCommandResult result) throws EditorServiceException {
		if(!result.returnStatus())
		{
			throw new EditorServiceException(result.explainResult());
		}
	}

	private EntityClass getEntityClassWithId(String classId, EntityDomain domain) throws EditorServiceException {
		if(!domain.hasEntityClassWithId(classId))
		{
			throw new EditorServiceException("No class with ud '"+ classId +"' exists in the domain");
		}
		EntityClass theClass = domain.getEntityClassWithId(classId);
		return theClass;
	}
	
	
	/////////////////// public - private divide ////////////////////
	
	public EditorCommandHistory createNewDomain(String domainName) {
		EditorCommandHistory newHistory = new EditorCommandHistory(domainName);
		IUUIDIdentifier uuid = new ConcreteUUIDIdentifier();
		newHistory.setUUID(uuid);
		
		return newHistory;
	}


	public void addClass(EditorCommandHistory retrievedHistory, String classId) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_AddClassToDomain command = new EditorCommand_AddClassToDomain();
		command.setClassId(classId);
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public Collection<String> getClassIdsInDomain(EditorCommandHistory retrievedHistory) throws DomainNotFoundException {
		EntityDomain domain = retrievedHistory.getDomain();
		Collection<String> classIds = domain.getClassIds();
		return classIds;
	}
	
	public Collection<String> getClassNamesInDomain(EditorCommandHistory retrievedHistory) throws DomainNotFoundException {
		EntityDomain domain = retrievedHistory.getDomain();
		Collection<String> classNames = domain.getClassNames();
		return classNames;
	}

	public void changeClassName(EditorCommandHistory retrievedHistory, String classId, String newClassName) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_RenameClass command = new EditorCommand_RenameClass();
		command.setNewClassName(newClassName);
		command.setClassId(classId);
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void addAttribute(EditorCommandHistory retrievedHistory, String classId, String attributeUUID) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_AddAttributeToClass command = new EditorCommand_AddAttributeToClass();
		command.setClassId(classId);
		command.setAttributeUUID(attributeUUID);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public Collection<String> getAttributeNamesOnClass(EditorCommandHistory retrievedHistory, String classId) throws DomainNotFoundException, EditorServiceException {
		EntityDomain domain = retrievedHistory.getDomain();
		EntityClass theClass = getEntityClassWithId(classId, domain);
			
		Collection<String> attributeNames = theClass.getAttributeNames();
		return attributeNames;
	}

	public void changeAttributeName(EditorCommandHistory retrievedHistory, String classId, String attributeId, String newAttributeName) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_RenameAttribute command = new EditorCommand_RenameAttribute();
		command.setClassId(classId);
		command.setAttributeIdToRename(attributeId);
		command.setNewAttributeName(newAttributeName);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeAttributeDatatype(EditorCommandHistory retrievedHistory, String classId, String attributeId, IEntityDatatype datatype) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_ChangeAttributeDatatype command = new EditorCommand_ChangeAttributeDatatype();
		command.setClassId(classId);
		command.setAttributeId(attributeId);
		command.setDatatype(datatype);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}
	
	public String getAttributeDatatype(EditorCommandHistory retrievedHistory, String className, String attributeId)  throws DomainNotFoundException, EditorServiceException
	{
		EntityDomain domain = retrievedHistory.getDomain();
		
		GetAttributeDatatypeHelper helper = new GetAttributeDatatypeHelper();
		
		throwCommandExceptionIfRequired(helper.doCommand(domain, className, attributeId));
		
		return helper.getAttributeName();
	}

	public Collection<String> getStateNamesForClass(EditorCommandHistory retrievedHistory, String classId) throws DomainNotFoundException, EditorServiceException {
		EntityDomain domain = retrievedHistory.getDomain();
		
		GetStateNamesForClassHelper helper = new GetStateNamesForClassHelper();
		throwCommandExceptionIfRequired(helper.doCommand(domain, classId));
		
		Collection<String> stateNames = helper.getStateNames();
		return stateNames;
	}

	public void addStateToClass(EditorCommandHistory retrievedHistory, String classId, String stateId) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_AddStateToClass command = new EditorCommand_AddStateToClass();
		command.setClassId(classId);
		command.setStateId(stateId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public String getActionLanguage(EditorCommandHistory retrievedHistory, String classId, String stateId) throws EditorServiceException, DomainNotFoundException {
		GetStateActionLanguageHelper helper = new GetStateActionLanguageHelper();
		
		throwCommandExceptionIfRequired(helper.doCommand(retrievedHistory.getDomain(), classId, stateId));
		
		return helper.getActionLanguage();
	}

	public void setActionLanguage(EditorCommandHistory retrievedHistory, String classId, String stateId, String actionLanguage) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_EditStateActionLanguage command = new EditorCommand_EditStateActionLanguage();
		command.setClassId(classId);
		command.setStateId(stateId);
		command.setActionLanguage(actionLanguage);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void renameState(EditorCommandHistory retrievedHistory, String classId, String stateId, String newStateName) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_RenameState command =  new EditorCommand_RenameState();
		command.setClassId(classId);
		command.setNewStateName(newStateName);
		command.setStateId(stateId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void deleteState(EditorCommandHistory retrievedHistory, String classId, String stateId) throws EditorServiceException, DomainNotFoundException {
		EditorCommand_DeleteState command =  new EditorCommand_DeleteState();
		command.setClassId(classId);
		command.setStateId(stateId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public EntityDomain getDomain(EditorCommandHistory retrievedHistory) throws DomainNotFoundException {
		
//		if(uuid.getUUIDString().equals("undefined")){
//			return DomainShoppingCart.getShoppingCartDomain();
//		}
//		
//		if(uuid.getUUIDString().equals("warehouse")){
//			try {
//				return DomainWarehouse.getWarehouseDomain();
//			} catch (NameAlreadyBoundException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		if(uuid.getUUIDString().equals("bus")){
//			return DomainBus.getBusDomain();	
//		}
		
		EntityDomain domain = retrievedHistory.getDomain();
		return domain;
	}

	public void deleteClass(EditorCommandHistory retrievedHistory, String uuidToDelete) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_DeleteClass command = new EditorCommand_DeleteClass();
		command.setClassId(uuidToDelete);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));		
	}

	public void deleteAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_DeleteAttributeFromClass command = new EditorCommand_DeleteAttributeFromClass();
		command.setClassId(uuidOfClass);
		command.setAttributeUUID(uuidOfAttribute);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void createNewRelation(EditorCommandHistory retrievedHistory, String relationUUID, String endAUUID, String endBUUID) throws DomainNotFoundException, EditorServiceException{
		EditorCommand_AddRelationBetweenClasses command = new EditorCommand_AddRelationBetweenClasses();
		command.setClassAId(endAUUID);
		command.setClassBId(endBUUID);
		command.setRelationUUID(relationUUID);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeRelation(EditorCommandHistory retrievedHistory, String relationId, String classAId, String verbA, String cardinalityA, String classBId, String verbB, String cardinalityB) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_ChangeRelation command = new EditorCommand_ChangeRelation();
		command.setRelationId(relationId);
		command.setClassAId(classAId);
		command.setClassAVerb(verbA);
		command.setClassACardinality(cardinalityA);
		command.setClassBId(classBId);
		command.setClassBVerb(verbB);
		command.setClassBCardinality(cardinalityB);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeRelationName(EditorCommandHistory retrievedHistory, String relationId, String newName) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_RenameRelation command = new EditorCommand_RenameRelation();
		command.setRelationId(relationId);
		command.setNewName(newName);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void deleteRelation(EditorCommandHistory retrievedHistory, String relationId) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_DeleteRelation command = new EditorCommand_DeleteRelation();
		command.setRelationId(relationId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void createAssociation(EditorCommandHistory retrievedHistory, String relationId, String associationClassId) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_CreateAssociation command = new EditorCommand_CreateAssociation();
		command.setRelationId(relationId);
		command.setAssociationClassId(associationClassId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
		
	}

	public void changeSuperClass(EditorCommandHistory retrievedHistory, String classId, String superClassId) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_ChangeSuperClass command = new EditorCommand_ChangeSuperClass();
		command.setClassId(classId);
		command.setSuperClassId(superClassId);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeClassDiagramCoordinates(EditorCommandHistory retrievedHistory, JsonClassDiagramCoordinatesHelper coordinatesHelper) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_ChangeClassDiagramCoordinates command = new EditorCommand_ChangeClassDiagramCoordinates();
		command.setCoordinatesHelper(coordinatesHelper);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void createNewStateTransition(EditorCommandHistory retrievedHistory,	String classUUID, String fromUUID, String toUUID, String instanceUUID) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_AddStateTransition command = new EditorCommand_AddStateTransition();
		command.setClassId(classUUID);
		command.setFromStateId(fromUUID);
		command.setToStateId(toUUID);
		command.setInstanceId(instanceUUID);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeStateTransition(EditorCommandHistory retrievedHistory, String instanceUUID, String fromUUID, String toUUID, String specUUID, String classUUID) throws DomainNotFoundException, EditorServiceException {
		EditorCommand_ChangeStateTransition command = new EditorCommand_ChangeStateTransition();
		command.setClassId(classUUID);
		command.setSpecId(specUUID);
		command.setFromStateId(fromUUID);
		command.setToStateId(toUUID);
		command.setInstanceId(instanceUUID);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void createEventSpecification(EditorCommandHistory retrievedHistory,	String classUUID, String specificationUUID)  throws DomainNotFoundException, EditorServiceException {
		EditorCommand_CreateEventSpecification command = new EditorCommand_CreateEventSpecification();
		command.setClassId(classUUID);
		command.setSpecId(specificationUUID);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void renameSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String newName) throws EditorServiceException {
		EditorCommand_RenameEventSpecification command = new EditorCommand_RenameEventSpecification();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);
		command.setNewSpecName(newName);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void addParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam) throws EditorServiceException {
		EditorCommand_AddEventParam command = new EditorCommand_AddEventParam();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);
		command.setParamId(uuidOfParam);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void renameSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam, String newName) throws EditorServiceException {
		EditorCommand_RenameEventSpecificationParam command = new EditorCommand_RenameEventSpecificationParam();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);
		command.setParamId(uuidOfParam);
		command.setNewParamName(newName);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}
	
	public void deleteSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam) throws EditorServiceException {
		EditorCommand_DeleteEventSpecificationParam command = new EditorCommand_DeleteEventSpecificationParam();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);
		command.setParamId(uuidOfParam);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void changeSpecParamType(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam, IEntityDatatype dataType) throws EditorServiceException {
		EditorCommand_ChangeEventSpecificationParamType command = new EditorCommand_ChangeEventSpecificationParamType();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);
		command.setParamId(uuidOfParam);
		command.setDatatype(dataType);
		
		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

	public void deleteSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec) throws EditorServiceException {
		EditorCommand_DeleteEventSpecification command = new EditorCommand_DeleteEventSpecification();
		command.setClassId(uuidOfClass);
		command.setSpecId(uuidOfSpec);

		throwCommandExceptionIfRequired(retrievedHistory.performCommand(command));
	}

}
