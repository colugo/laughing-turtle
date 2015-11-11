package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_RenameState;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;

public class RenameStateCommandTests extends TestCase {
	public RenameStateCommandTests(String name) {
		super(name);
	}
	
	public void test_can_rename_state() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String stateName = "State1";
		String newStateName = "State2";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState newState = new EntityState(stateName);
		klass.addState(newState);
		
		EditorCommand_RenameState command = new EditorCommand_RenameState();
		command.setDomain(domain);
		command.setStateId(stateName);
		command.setNewStateName(newStateName);
		command.setClassId("Class");

		Assert.assertEquals(false, klass.hasStateWithName(newStateName));
		Assert.assertEquals(true, klass.hasStateWithName(stateName));
		
		Assert.assertEquals(true, command.canDoCommand().returnStatus());
		Assert.assertEquals(true, command.doCommand().returnStatus());	

		Assert.assertEquals(true, klass.hasStateWithName(newStateName));
		Assert.assertEquals(false, klass.hasStateWithName(stateName));
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "NewClass";
		String oldStateName = "OldStateName";
		String newStateName = "NewStateName";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddStateToClass addStateToClass = new EditorCommand_AddStateToClass();
		addStateToClass.setStateId(oldStateName);
		addStateToClass.setClassId(classId);
		history.performCommand(addStateToClass);
		
		EditorCommand_RenameState command = new EditorCommand_RenameState();
		command.setStateId(oldStateName);
		command.setNewStateName(newStateName);
		command.setClassId(classId);
		history.performCommand(command);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId(classId).hasStateWithName(newStateName));
		Assert.assertEquals(false, history2.getDomain().getEntityClassWithId(classId).hasStateWithName(oldStateName));
	}
	
	public void test_cant_rename_state_where_state_exists() throws NameAlreadyBoundException
	{
		String stateName = "State1";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState newState = new EntityState(stateName);
		klass.addState(newState);
		
		EditorCommand_RenameState command = new EditorCommand_RenameState();
		command.setDomain(domain);
		command.setStateId(stateName);
		command.setNewStateName(stateName);
		command.setClassId("Class");

		Assert.assertEquals(true, klass.hasStateWithName(stateName));
		
		Assert.assertEquals(false, command.canDoCommand().returnStatus());
		Assert.assertEquals(false, command.doCommand().returnStatus());
		
		Assert.assertEquals(true, klass.hasStateWithName(stateName));
	}
	
	public void test_cant_rename_state_where_class_not_found() throws NameAlreadyBoundException
	{
		String stateName = "State1";
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_RenameState command = new EditorCommand_RenameState();
		command.setDomain(domain);
		command.setStateId(stateName);
		command.setNewStateName(stateName);
		command.setClassId("Class1");

		Assert.assertEquals(false, command.canDoCommand().returnStatus());
		Assert.assertEquals(false, command.doCommand().returnStatus());	
	}
	
	public void test_cant_rename_state_where_state_not_found() throws NameAlreadyBoundException
	{
		String stateName = "State1";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		
		EditorCommand_RenameState command = new EditorCommand_RenameState();
		command.setDomain(domain);
		command.setStateId(stateName);
		command.setNewStateName(stateName);
		command.setClassId("Class");

		Assert.assertEquals(false, command.canDoCommand().returnStatus());
		Assert.assertEquals(false, command.doCommand().returnStatus());
	}
}
