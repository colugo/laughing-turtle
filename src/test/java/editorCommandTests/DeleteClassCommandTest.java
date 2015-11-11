package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_DeleteClass;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public class DeleteClassCommandTest extends TestCase {
	public DeleteClassCommandTest(String name)
	{
		super(name);
	}
	
	public void test_can_delete_class_from_domain(){
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		Assert.assertEquals(true, domain.hasEntityClassWithId(klass.getId()));
		
		EditorCommand_DeleteClass deleteClass = new EditorCommand_DeleteClass();
		deleteClass.setDomain(domain);
		deleteClass.setClassId(klass.getId());
		
		
		Assert.assertEquals(true, deleteClass.doCommand().returnStatus());
		
		Assert.assertEquals(false, domain.hasEntityClassWithId(klass.getId()));
	}
	
	
	public void test_round_trip_serialisation() throws Exception
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		Assert.assertEquals(true, domain.hasEntityClassWithId(klass.getId()));
		
		EditorCommand_DeleteClass deleteClass = new EditorCommand_DeleteClass();
		deleteClass.setDomain(domain);
		deleteClass.setClassId(klass.getId());
		
		EditorCommandHistory history = new EditorCommandHistory(domain.getName());
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(klass.getId());
		history.performCommand(addClassToDomain);
		
		history.performCommand(deleteClass);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(false, history2.getDomain().hasEntityClassWithId(klass.getId()));
	}
	
	public void test_cant_delete_class_not_in_domain(){
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_DeleteClass deleteClass = new EditorCommand_DeleteClass();
		deleteClass.setDomain(domain);
		deleteClass.setClassId("not an id of a class");
		
		
		Assert.assertEquals(false, deleteClass.doCommand().returnStatus());
	}
}
