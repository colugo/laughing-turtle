package test.java.editorCommandTests;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddAttributeToClass;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_CreateDomain;
import main.java.avii.editor.command.EditorCommand_DeleteAttributeFromClass;
import main.java.avii.editor.command.EditorCommand_RenameAttribute;
import main.java.avii.editor.command.IEditorCommand;
import main.java.avii.editor.command.IEditorCommandResult;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class EditorCommandTests extends TestCase {

	public EditorCommandTests(String name) {
		super(name);
	}
	
	public void test_add_new_class_to_domain_command()
	{
		String classId = "ID1";
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setDomain(domain);
		addClassToDomain.setClassId(classId);

		IEditorCommandResult commandResult = ((IEditorCommand)addClassToDomain).doCommand();
		Assert.assertTrue(commandResult.returnStatus());
		
		EntityClass newClass = addClassToDomain.getNewClass();
		Assert.assertEquals("Class_" + classId, newClass.getName());
		
		Assert.assertEquals(domain, newClass.getDomain());
		
		Assert.assertTrue(domain.hasEntityClassWithId(classId));
	}
	
	public void test_add_attribute_to_class_command()
	{
		String newClassName = "NewClassName";
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setDomain(domain);
		addClassToDomain.setClassId(newClassName);
		addClassToDomain.doCommand();
		
		String attributeId = "attributeId";
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setDomain(domain);
		
		addAttributeToClass.setClassId(newClassName);
		addAttributeToClass.setAttributeUUID(attributeId);
		
		IEditorCommandResult commandResult = ((IEditorCommand)addAttributeToClass).doCommand();
		Assert.assertTrue(commandResult.returnStatus());
		
		EntityAttribute newAttribute = addAttributeToClass.getNewAttribute();
		
		Assert.assertEquals("Attribute_"+attributeId, newAttribute.getName());
		Assert.assertTrue(addClassToDomain.getNewClass().hasAttribute("Attribute_"+attributeId));
		Assert.assertTrue(newAttribute.getType() instanceof StringEntityDatatype);
	}
	
	public void test_cant_add_attribute_with_same_name_to_class_command() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("");
		String attributeId = "attributeId";
		String className = "NewClass";
		EntityClass klass = new EntityClass(className);
		domain.addClass(klass);
		klass.addAttribute(new EntityAttribute("Fred", IntegerEntityDatatype.getInstance()));
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setDomain(domain);
		
		addAttributeToClass.setClassId(className);
		addAttributeToClass.setAttributeUUID(attributeId);
		
		IEditorCommandResult commandResult = ((IEditorCommand)addAttributeToClass).doCommand();
		Assert.assertTrue(commandResult.returnStatus());
		
		
		EditorCommand_RenameAttribute command = new EditorCommand_RenameAttribute();
		command.setDomain(domain);
		command.setClassId(className);
		command.setAttributeIdToRename(attributeId);
		command.setNewAttributeName("Fred");
		
		commandResult = ((IEditorCommand)command).doCommand();
		Assert.assertFalse(commandResult.returnStatus());
	}
	
	public void test_can_create_domain_command()
	{
		String newDomainName = "NewDomainName";
		EditorCommand_CreateDomain createDomain = new EditorCommand_CreateDomain();
		createDomain.setDomainName(newDomainName);
		IEditorCommandResult result = createDomain.doCommand();
		Assert.assertTrue(result.returnStatus());
		EntityDomain domain = createDomain.getDomain();
		Assert.assertEquals(newDomainName, domain.getName());
	}

	public void test_delete_attribute_from_class_command()
	{
		String newClassName = "NewClassName";
		EntityDomain domain = new EntityDomain("TestDomain");
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setDomain(domain);
		addClassToDomain.setClassId(newClassName);
		addClassToDomain.doCommand();
		
		String attributeId = "attributeId";
		
		EditorCommand_AddAttributeToClass addAttributeToClass = new EditorCommand_AddAttributeToClass();
		addAttributeToClass.setDomain(domain);
		addAttributeToClass.setClassId(newClassName);
		addAttributeToClass.setAttributeUUID(attributeId);
		
		IEditorCommandResult commandResult = ((IEditorCommand)addAttributeToClass).doCommand();
		Assert.assertTrue(commandResult.returnStatus());
		
		EntityAttribute newAttribute = addAttributeToClass.getNewAttribute();
		
		Assert.assertEquals("Attribute_"+attributeId, newAttribute.getName());
		Assert.assertTrue(addClassToDomain.getNewClass().hasAttribute("Attribute_"+attributeId));
		Assert.assertTrue(newAttribute.getType() instanceof StringEntityDatatype);
		
		EditorCommand_DeleteAttributeFromClass deleteAttributeFromClass = new EditorCommand_DeleteAttributeFromClass();
		deleteAttributeFromClass.setDomain(domain);
		deleteAttributeFromClass.setClassId(newClassName);
		deleteAttributeFromClass.setAttributeUUID(attributeId);
		
		commandResult = ((IEditorCommand)deleteAttributeFromClass).doCommand();
		Assert.assertTrue(commandResult.returnStatus());
		
		Assert.assertFalse(addClassToDomain.getNewClass().hasAttribute("Attribute_"+attributeId));
		
	}
	
}

