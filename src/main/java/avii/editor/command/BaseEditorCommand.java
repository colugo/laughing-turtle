package main.java.avii.editor.command;

import java.util.Date;

import org.simpleframework.xml.Attribute;

import main.java.avii.editor.command.result.FailureEditorCommand;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public abstract class BaseEditorCommand implements IEditorCommand {

	@Attribute
	protected Date _createdTimestamp = new Date();
	protected EntityDomain _domain = null;
	
	public void setDomain(EntityDomain theDomain)
	{
		this._domain = theDomain;
	}
	
	public IEditorCommandResult doCommand(){
		// some base action here
		
		IEditorCommandResult canDoResult = canDoCommand();
		if(canDoResult.returnStatus() == false)
		{
			return canDoResult;
		}
		
		IEditorCommandResult concreteResult = concreteDoCommand();
		return concreteResult;
	}

	
	protected abstract IEditorCommandResult concreteDoCommand();
	public abstract IEditorCommandResult canDoCommand();
	
	
	public void setCreatedTime(Date timestamp)
	{
		this._createdTimestamp =  timestamp;
	}
	
	public Date getCreatedTime()
	{
		return this._createdTimestamp;
	}
	

	public static IEditorCommandResult failWhenClassExists(String className) {
		return new FailureEditorCommand("A class with name '" + className + "' already exists in the domain");
	}
	
	public static IEditorCommandResult failWhenClassNotFound(String className) {
		return new FailureEditorCommand("No class with ID '" + className + "' was found in the domain");
	}
	
	public static IEditorCommandResult failWhenStateNotFound(String stateId) {
		return new FailureEditorCommand("No state with ID '" + stateId + "' was found in the domain");
	}
	
	
	public static IEditorCommandResult failWhenEventSpecNotFound(String specId) {
		return new FailureEditorCommand("No event specification with ID '" + specId + "' was found in the class");
	}
	
	public static IEditorCommandResult failWhenEventInstanceNotFound(String instanceId) {
		return new FailureEditorCommand("No event instance with ID '" + instanceId + "' was found in the class");
	}
	
	public static IEditorCommandResult failWhenStateWithNameAlreadyExists(String stateName) {
		return new FailureEditorCommand("A state with name '" + stateName + "' exists in the class");
	}
	
	public static IEditorCommandResult failWhenAttributeNotOnClass(String attributeName, String className) {
		return new FailureEditorCommand("Could not find an attribute with ID '" + attributeName + "' on class '" + className + "'.");
	}

	public static IEditorCommandResult failWhenAttributeAlreadyExists(String attributeName, String className) {
		return new FailureEditorCommand("An attribute with name '" + attributeName + "' already exists on class '" + className + "'.");
	}

	public static IEditorCommandResult failWhenStateNotFound(String className, String stateName) {
		return new FailureEditorCommand("A state with name '" + stateName + "' does not exist in the class '"+ className +"'");
	}

	public static IEditorCommandResult failWhenRelationAlreadyExists(String relationName) {
		return new FailureEditorCommand("A relation with name '" + relationName + "' already exists in the domain");
	}

	public static IEditorCommandResult failWhenRelationIdNotFound(String relationId) {
		return new FailureEditorCommand("No relation with ID '" + relationId + "' was found in the domain");
	}
		
	
	public static IEditorCommandResult failWhenRelationWithNameExists(String relationName) {
		return new FailureEditorCommand("A relation is already named '" + relationName + "'");
	}

	public static IEditorCommandResult failWhenSpecNotFound(String classId, String specId) {
		return new FailureEditorCommand("No specification with id '" + specId + "' found in the class '"+ classId +"'");
	}
	
	public static IEditorCommandResult failWhenSpecWithNameAlreadyExists(String specName) {
		return new FailureEditorCommand("An event specification with name '" + specName + "' exists in the class");
	}

	public static IEditorCommandResult failWhenParamNotFound(String paramId) {
		return new FailureEditorCommand("No event param with id '" + paramId + "' found");
	}
}
