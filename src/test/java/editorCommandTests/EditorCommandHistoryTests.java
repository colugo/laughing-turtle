package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;

import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import main.java.avii.editor.command.EditorCommand_AddAttributeToClass;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_ChangeAttributeDatatype;
import main.java.avii.editor.command.EditorCommand_RenameAttribute;
import main.java.avii.editor.command.EditorCommand_RenameClass;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.command.history.EditorCommandHistoryPlaybackException;
import main.java.avii.editor.command.history.EditorCommandHistorySerializer;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.editor.service.IUUIDIdentifier;

public class EditorCommandHistoryTests extends TestCase {

	public EditorCommandHistoryTests(String name) {
		super(name);
	}
	
	public void test_can_create_history_and_create_domain() throws EditorCommandHistoryPlaybackException
	{
		String domainName = "NewDomain";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.playAll();
		EntityDomain domain = history.getDomain();
		Assert.assertEquals(domainName,domain.getName());
	}
	
	public void test_can_create_history_and_create_domain_and_get_u_u_i_d() throws EditorCommandHistoryPlaybackException
	{
		String domainName = "NewDomain";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.playAll();
		EntityDomain domain = history.getDomain();
		Assert.assertEquals(domainName,domain.getName());
	}
	
	public void test_can_add_class_using_history() throws EditorCommandHistoryPlaybackException
	{
		String domainName = "NewDomain";
		String newClassName = "NewClass";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(newClassName);
		history.performCommand(addClassToDomain);
		
		history.playAll();
		
		EntityDomain domain = history.getDomain();
		Assert.assertTrue(domain.hasEntityClassWithId(newClassName));
	}
	
	public void test_can_add_attribute_using_history() throws EditorCommandHistoryPlaybackException
	{
		String domainName = "NewDomain";
		String classId = "NewClass";
		String attributeId = "attributeId";

		EditorCommandHistory history = new EditorCommandHistory(domainName);
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);

		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setClassId(classId);
		addAttributeToClass.setAttributeUUID(attributeId);
		Assert.assertTrue(history.performCommand(addAttributeToClass).returnStatus());
		Assert.assertTrue(!history.performCommand(addAttributeToClass).returnStatus());
		
				
		EntityDomain domain = history.getDomain();
		Assert.assertTrue(domain.hasEntityClassWithId(classId));
		EntityClass klass = domain.getEntityClassWithId(classId);
		Assert.assertTrue(klass.hasAttribute("Attribute_"+attributeId));
	}

	public void test_serializing_editor_history() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "ID1";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		Serializer serializer = new Persister();
		StringWriter stringWriter = new StringWriter();
		serializer.write(history,stringWriter);
		
		Assert.assertTrue(stringWriter.toString().contains("EditorCommand_AddClassToDomain"));
		Assert.assertTrue(stringWriter.toString().contains("_classId=\"ID1\""));
	}
	
	public void test_serializing_editor_history_with_add_attribute() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "ID1";
		String attributeId = "attributeId";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setClassId(classId);
		addAttributeToClass.setAttributeUUID(attributeId);
		history.performCommand(addAttributeToClass);
		
		Serializer serializer = new Persister();
		StringWriter stringWriter = new StringWriter();
		serializer.write(history,stringWriter);
		Assert.assertTrue(stringWriter.toString().contains("EditorCommand_AddClassToDomain"));
		Assert.assertTrue(stringWriter.toString().contains("_classId=\"ID1\""));
	}
	
	public void test_can_deserialize_simple_editor_history() throws Exception
	{
		String xml = "<editorCommandHistory _createdTimestamp=\"2012-01-11 13:41:12.72 EST\" _domainName=\"NewDomain\"><_commands></_commands><_uuid class=\"test.java.helper.TestingUUID\" _uuid=\"\"/></editorCommandHistory>";
		Serializer serializer = new Persister();

		EditorCommandHistory history = serializer.read(EditorCommandHistory.class, xml);
		history.playAll();
		EntityDomain domain = history.getDomain();
		Assert.assertEquals("NewDomain",domain.getName());
	}
	
	public void test_can_deserialize_editor_history_with_add_class() throws Exception
	{
		String xml = "<editorCommandHistory _createdTimestamp=\"2012-01-11 13:41:12.72 EST\" _domainName=\"NewDomain\"><_uuid class=\"test.java.helper.TestingUUID\" _uuid=\"\"/><_commands><baseEditorCommand _createdTimestamp=\"2012-01-11 13:25:10.87 EST\" class=\"main.java.avii.editor.command.EditorCommand_AddClassToDomain\" _classId=\"NewClass\" _className=\"NewClass\"/></_commands></editorCommandHistory>";
		
		Serializer serializer = new Persister();
		EditorCommandHistory history = serializer.read(EditorCommandHistory.class, xml);
		history.playAll();
		EntityDomain domain = history.getDomain();
		Assert.assertEquals("NewDomain",domain.getName());
		Assert.assertTrue(domain.hasEntityClassWithId("NewClass"));
	}

	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String newClassId = "ID1";
		String attributeId = "attributeId";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(newClassId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setClassId(newClassId);
		addAttributeToClass.setAttributeUUID(attributeId);
		history.performCommand(addAttributeToClass);
		
		TestHelper.roundTripEditorHistoryCommandHelper(history);
	}
	
	private EditorCommandHistory bootstrapHistory()
	{
		String domainName = "NewDomain";
		String newClassId = "NewClass";
		String attributeId = "attributeId";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(newClassId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setClassId(newClassId);
		addAttributeToClass.setAttributeUUID(attributeId);
		history.performCommand(addAttributeToClass);
		
		return history;
	}

	public void test_rename_class() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		IUUIDIdentifier uuid = history.getUUID();
		
		EditorCommand_RenameClass renameClass = new EditorCommand_RenameClass();
		renameClass.setClassId("NewClass");
		renameClass.setNewClassName("NewerClass");
		history.performCommand(renameClass);
		
		EditorCommandHistory replayHistory = TestHelper.roundTripEditorHistoryCommandHelper(history);
		EntityDomain domain = replayHistory.getDomain();
		
		Assert.assertTrue(domain.hasEntityClassWithName("NewerClass"));
		Assert.assertEquals(uuid, replayHistory.getUUID());
	}

	public void test_rename_attribute() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		
		String oldAttributeName = "Attribute_attributeId";
		String newAttributeName = "newAttributeName";
		EntityDomain origDomain = history.getDomain();
		EntityClass origClass = origDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(origClass.getAttributeNames().contains(oldAttributeName));
		Assert.assertTrue(!origClass.getAttributeNames().contains(newAttributeName));
		
		EditorCommand_RenameAttribute renameAttribute = new EditorCommand_RenameAttribute();
		renameAttribute.setClassId("NewClass");
		renameAttribute.setAttributeIdToRename("attributeId");
		renameAttribute.setNewAttributeName(newAttributeName);
		
		history.performCommand(renameAttribute);
		
		EditorCommandHistory replayHistory = TestHelper.roundTripEditorHistoryCommandHelper(history);
		EntityDomain domain = replayHistory.getDomain();
		EntityClass theClass = domain.getEntityClassWithId("NewClass");
		
		Assert.assertTrue(!theClass.getAttributeNames().contains(oldAttributeName));
		Assert.assertTrue(theClass.getAttributeNames().contains(newAttributeName));
	}
	
	public void testcant_rename_attribute_that_doesnt_exist() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		
		String oldAttributeName = "CantFindAttributeName";
		String newAttributeName = oldAttributeName;
		EntityDomain origDomain = history.getDomain();
		EntityClass origClass = origDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(!origClass.getAttributeNames().contains(oldAttributeName));
		
		EditorCommand_RenameAttribute renameAttribute = new EditorCommand_RenameAttribute();
		renameAttribute.setClassId("NewClass");
		renameAttribute.setAttributeIdToRename(oldAttributeName);
		renameAttribute.setNewAttributeName(newAttributeName);
		
		IEditorCommandResult result = history.performCommand(renameAttribute);
		Assert.assertTrue(!result.returnStatus());
	}

	public void testcant_rename_attribute_to_existing_name() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		
		String oldAttributeName = "Attribute_attributeId";
		String newAttributeName = oldAttributeName;
		EntityDomain origDomain = history.getDomain();
		EntityClass origClass = origDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(origClass.getAttributeNames().contains(oldAttributeName));
		
		EditorCommand_RenameAttribute renameAttribute = new EditorCommand_RenameAttribute();
		renameAttribute.setClassId("NewClass");
		renameAttribute.setAttributeIdToRename(oldAttributeName);
		renameAttribute.setNewAttributeName(newAttributeName);
		
		IEditorCommandResult result = history.performCommand(renameAttribute);
		Assert.assertTrue(!result.returnStatus());
	}
	
	public void test_ensure_dates_are_preserved_during_serialization() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		IUUIDIdentifier uuid = history.getUUID();
		
		EditorCommand_RenameClass renameClass = new EditorCommand_RenameClass();
		renameClass.setClassId("NewClass");
		renameClass.setNewClassName("NewerClass");
		history.performCommand(renameClass);
			
		String origionalHistoryString = EditorCommandHistorySerializer.serialize(history);
		
		// make some time difference so I can determine if the timestamps are different
		Thread.sleep(100);
		
		EditorCommandHistory replayHistory = TestHelper.roundTripEditorHistoryCommandHelper(history);
		EntityDomain domain = replayHistory.getDomain();
		
		String replayHistoryString = EditorCommandHistorySerializer.serialize(replayHistory);
		
		Assert.assertTrue(domain.hasEntityClassWithName("NewerClass"));
		Assert.assertEquals(uuid, replayHistory.getUUID());
		
		Assert.assertEquals(origionalHistoryString,replayHistoryString);
	}
	
	
	public void test_change_attribute_datatype() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		
		String attributeId = "attributeId";

		EntityDomain origDomain = history.getDomain();
		EntityClass origClass = origDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(origClass.getAttributeNames().contains("Attribute_"+attributeId));
		EntityAttribute origAttribute = origClass.getAttributeWithName("Attribute_"+attributeId);
		Assert.assertEquals(origAttribute.getType().getClass(), StringEntityDatatype.class);
		
		EditorCommand_ChangeAttributeDatatype renameAttribute = new EditorCommand_ChangeAttributeDatatype();
		renameAttribute.setClassId("NewClass");
		renameAttribute.setAttributeId(attributeId);
		renameAttribute.setDatatype(new BooleanEntityDatatype());
		
		IEditorCommandResult result =  history.performCommand(renameAttribute);
		Assert.assertTrue(result.returnStatus());
		
		EditorCommandHistory replayHistory = TestHelper.roundTripEditorHistoryCommandHelper(history);
		EntityDomain replayDomain = replayHistory.getDomain();
		EntityClass replayClass = replayDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(replayClass.getAttributeNames().contains("Attribute_" + attributeId));
		EntityAttribute replayAttribute = replayClass.getAttributeWithName("Attribute_"+attributeId);
		Assert.assertEquals(replayAttribute.getType().getClass(), BooleanEntityDatatype.class);

	}
	
	public void test_cant_change_attribute_datatype_that_doesnt_exist() throws Exception
	{
		EditorCommandHistory history = bootstrapHistory();
		
		String attributeId = "notAnAttributeId";

		EntityDomain origDomain = history.getDomain();
		EntityClass origClass = origDomain.getEntityClassWithId("NewClass");
		Assert.assertTrue(!origClass.getAttributeNames().contains("Attribute_"+attributeId));
		
		EditorCommand_ChangeAttributeDatatype renameAttribute = new EditorCommand_ChangeAttributeDatatype();
		renameAttribute.setClassId("NewClass");
		renameAttribute.setAttributeId(attributeId);
		renameAttribute.setDatatype(new BooleanEntityDatatype());
		
		IEditorCommandResult result =  history.performCommand(renameAttribute);
		Assert.assertTrue(!result.returnStatus());
		
	}
	
}

