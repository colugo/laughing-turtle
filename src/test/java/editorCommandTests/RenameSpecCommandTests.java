package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_CreateEventSpecification;
import main.java.avii.editor.command.EditorCommand_RenameEventSpecification;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class RenameSpecCommandTests extends TestCase {
	public RenameSpecCommandTests(String name) {
		super(name);
	}
	
	public void test_can_rename_spec() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String oldSpecName = "oldSpecName";
		String newSpecName = "newSpecName";
		
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityEventSpecification spec = new EntityEventSpecification(klass, oldSpecName);

		
		EditorCommand_RenameEventSpecification command = new EditorCommand_RenameEventSpecification();
		command.setDomain(domain);
		command.setSpecId(spec.getId());
		command.setNewSpecName(newSpecName);
		command.setClassId("Class");

		Assert.assertEquals(false, klass.hasEventSpecificationWithName(newSpecName));
		Assert.assertEquals(true, klass.hasEventSpecificationWithName(oldSpecName));
		
		Assert.assertEquals(true, command.canDoCommand().returnStatus());
		Assert.assertEquals(true, command.doCommand().returnStatus());	

		Assert.assertEquals(true, klass.hasEventSpecificationWithName(newSpecName));
		Assert.assertEquals(false, klass.hasEventSpecificationWithName(oldSpecName));
		
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "NewClass";
		String specId = "SpecId";
		String oldSpecName = "OldSpecName";
		String newSpecName = "NewSpecName";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_CreateEventSpecification createSpec = new EditorCommand_CreateEventSpecification();
		createSpec.setClassId(classId);
		createSpec.setSpecId(specId);
		history.performCommand(createSpec);
		
		EditorCommand_RenameEventSpecification renameCommand = new EditorCommand_RenameEventSpecification();
		renameCommand.setSpecId(specId);
		renameCommand.setNewSpecName(newSpecName);
		renameCommand.setClassId(classId);
		history.performCommand(renameCommand);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId(classId).hasEventSpecificationWithName(newSpecName));
		Assert.assertEquals(false, history2.getDomain().getEntityClassWithId(classId).hasEventSpecificationWithName(oldSpecName));
		
		history2.getDomain().getEntityClassWithId(classId).getAllEntityEventInstances();
	}
}
