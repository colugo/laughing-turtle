package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_DeleteState;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import junit.framework.Assert;
import junit.framework.TestCase;

public class DeleteStateCommandTests extends TestCase {
	public DeleteStateCommandTests(String name) {
		super(name);
	}
	
	public void test_can_delete_state_from_class() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_DeleteState command = new EditorCommand_DeleteState();
		command.setDomain(domain);
		command.setStateId(newStateName);
		command.setClassId("Class");
		
		Assert.assertEquals(true, klass.hasStates());
		
		Assert.assertEquals(true, command.doCommand().returnStatus());
		
		Assert.assertEquals(false, klass.hasStates());
	}
	
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String newClassName = "NewClass";
		String newStateName = "NewStateName";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(newClassName);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddStateToClass addStateToClass = new EditorCommand_AddStateToClass();
		addStateToClass.setStateId(newStateName);
		addStateToClass.setClassId(newClassName);
		history.performCommand(addStateToClass);
		
		EditorCommand_DeleteState deleteState = new EditorCommand_DeleteState();
		deleteState.setStateId(newStateName);
		deleteState.setClassId(newClassName);
		history.performCommand(deleteState);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(false, history2.getDomain().getEntityClassWithId(newClassName).hasStateWithName(newStateName));
	}
	
	public void test_cant_delete_state_from_class_that_doesnt_exist() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_DeleteState command = new EditorCommand_DeleteState();
		command.setDomain(domain);
		command.setStateId(newStateName + "!");
		command.setClassId("Class1");
		
		Assert.assertEquals(true, klass.hasStates());
		
		Assert.assertEquals(false, command.doCommand().returnStatus());
		
		Assert.assertEquals(true, klass.hasStates());
	}
	
	public void test_cant_delete_state_from_class_when_state_doesnt_exist() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_DeleteState command = new EditorCommand_DeleteState();
		command.setDomain(domain);
		command.setStateId(newStateName + "!");
		command.setClassId("Class");
		
		Assert.assertEquals(true, klass.hasStates());
		
		Assert.assertEquals(false, command.doCommand().returnStatus());
		
		Assert.assertEquals(true, klass.hasStates());
	}
}
