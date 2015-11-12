package contracts;

import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;
import models.JsonClassDiagram;
import main.java.models.JsonClassDiagramCoordinatesHelper;

public interface IApplicationImplementation {

	public EditorCommandHistory newDomain(String domainName);
	
	public JsonClassDiagram getClassDiagram(EntityDomain entityDomain) throws DomainNotFoundException;

	/*
	 * Actions go here
	 * 
	 */
	public void Action_NewClassAction(EditorCommandHistory retrievedHistory, String uuidOfClass) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameClass(EditorCommandHistory retrievedHistory, String uuidOfClass, String newClassName) throws DomainNotFoundException, EditorServiceException;

	public void Action_DeleteClass(EditorCommandHistory retrievedHistory, String uuidToDelete) throws DomainNotFoundException, EditorServiceException;

	public void Action_AddAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute) throws DomainNotFoundException, EditorServiceException;

	public void Action_DeleteAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameAttribute(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute, String newAttributeName) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeAttributeType(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfAttribute, String newTypeHumanName) throws DomainNotFoundException, EditorServiceException;

	public void Action_CreateRelation(EditorCommandHistory retrievedHistory, String uuid, String endAUUID, String endBUUID) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeRelation(EditorCommandHistory retrievedHistory, String relationId, String classAId, String verbA, String cardinalityA, String classBId, String verbB, String cardinalityB) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameRelation(EditorCommandHistory retrievedHistory, String relationId, String newName) throws DomainNotFoundException, EditorServiceException;

	public void Action_DeleteRelation(EditorCommandHistory retrievedHistory, String relationId) throws DomainNotFoundException, EditorServiceException;

	public void Action_CreateAssociation(EditorCommandHistory retrievedHistory, String relationId, String associationClassId) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeSuperClass(EditorCommandHistory retrievedHistory, String classId, String superClassId) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeClassDiagramCoordinates(EditorCommandHistory retrievedHistory, JsonClassDiagramCoordinatesHelper coordinatesHelper) throws DomainNotFoundException, EditorServiceException;

	public void Action_AddState(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameState(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String newName)  throws DomainNotFoundException, EditorServiceException;

	public void Action_CreateStateTransition(EditorCommandHistory retrievedHistory, String classUUID, String fromUUID, String toUUID, String instanceUUID) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeStateTransition(EditorCommandHistory retrievedHistory, String instanceUUID, String fromUUID, String toUUID, String specUUID, String classUUID)  throws DomainNotFoundException, EditorServiceException;

	public void Action_CreateEventSpecification(EditorCommandHistory retrievedHistory, String classUUID, String specificationUUID) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String newName) throws DomainNotFoundException, EditorServiceException;

	public void Action_AddParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec, String uuidOfParam) throws DomainNotFoundException, EditorServiceException;

	public void Action_RenameSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String uuidOfParam, String newName) throws DomainNotFoundException, EditorServiceException;

	public void Action_DeleteSpecParam(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String uuidOfParam) throws DomainNotFoundException, EditorServiceException;

	public void Action_ChangeSpecParamType(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfState, String uuidOfParam, String newType) throws DomainNotFoundException, EditorServiceException;

	public void Action_DeleteSpec(EditorCommandHistory retrievedHistory, String uuidOfClass, String uuidOfSpec) throws DomainNotFoundException, EditorServiceException;
}
