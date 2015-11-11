package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;

public class AddStateToClassCommandTests extends TestCase {
	public AddStateToClassCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_state_to_class()
	{
		String newStateId = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		
		EditorCommand_AddStateToClass addStateToClass = new EditorCommand_AddStateToClass();
		addStateToClass.setDomain(domain);
		addStateToClass.setStateId(newStateId);
		addStateToClass.setClassId("Class");
		
		Assert.assertEquals(false, klass.hasStates());
		Assert.assertEquals(false, klass.hasStateWithId(newStateId));
		
		Assert.assertEquals(true, addStateToClass.doCommand().returnStatus());
		
		EntityState newState = addStateToClass.getNewState();
		Assert.assertEquals("State_" + newStateId, newState.getName());
		Assert.assertEquals(true, klass.hasStateWithId(newStateId));
		
		Assert.assertEquals(true, klass.hasStates());
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
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId(newClassName).hasStateWithName("State_" + newStateName));
	}
	
	public void test_cant_add_state_to_class_that_doesnt_exist()
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_AddStateToClass addStateToClass = new EditorCommand_AddStateToClass();
		addStateToClass.setDomain(domain);
		addStateToClass.setStateId(newStateName);
		addStateToClass.setClassId("Class");
		Assert.assertEquals(false, addStateToClass.canDoCommand().returnStatus());
		Assert.assertEquals(false, addStateToClass.doCommand().returnStatus());
	}
	
	
}
