package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_ChangeSuperClass;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;

public class ChangeSuperClassCommandTests extends TestCase {
	public ChangeSuperClassCommandTests(String name) {
		super(name);
	}
	
	public void test_can_create_superclass()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass bus = new EntityClass("bus");
		domain.addClass(bus);
		EntityClass vehicle = new EntityClass("vehicle");
		domain.addClass(vehicle);
		
		Assert.assertEquals(false, bus.hasSuperClasses());
		Assert.assertEquals(false, vehicle.hasSubClasses());
		
		EditorCommand_ChangeSuperClass changeSuperClass = new EditorCommand_ChangeSuperClass();
		changeSuperClass.setDomain(domain);
		changeSuperClass.setClassId(bus.getId());
		changeSuperClass.setSuperClassId(vehicle.getId());
		Assert.assertEquals(true, changeSuperClass.doCommand().returnStatus());
		
		Assert.assertEquals(true, bus.hasSuperClasses());
		Assert.assertEquals(true, vehicle.hasSubClasses());
		Assert.assertEquals(true, bus.getSuperClasses().contains(vehicle));
		Assert.assertEquals(true, vehicle.getsubClasses().contains(bus));
	}

	public void test_can_delete_superclass()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass bus = new EntityClass("bus");
		domain.addClass(bus);
		EntityClass vehicle = new EntityClass("vehicle");
		domain.addClass(vehicle);
		bus.addSuperClass(vehicle);
		
		Assert.assertEquals(true, bus.hasSuperClasses());
		Assert.assertEquals(true, vehicle.hasSubClasses());
		Assert.assertEquals(true, bus.getSuperClasses().contains(vehicle));
		Assert.assertEquals(true, vehicle.getsubClasses().contains(bus));	
		
		EditorCommand_ChangeSuperClass changeSuperClass = new EditorCommand_ChangeSuperClass();
		changeSuperClass.setDomain(domain);
		changeSuperClass.setClassId(bus.getId());
		changeSuperClass.setSuperClassId(null);
		Assert.assertEquals(true, changeSuperClass.doCommand().returnStatus());
		
		Assert.assertEquals(false, bus.hasSuperClasses());
		Assert.assertEquals(false, vehicle.hasSubClasses());
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classAId = "IDbus";
		String classBId = "IDvehicle";

		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassA = new EditorCommand_AddClassToDomain();
		addClassA.setClassId(classAId);
		history.performCommand(addClassA);
		
		EditorCommand_AddClassToDomain addClassB = new EditorCommand_AddClassToDomain();
		addClassB.setClassId(classBId);
		history.performCommand(addClassB);
		
		EditorCommand_ChangeSuperClass changeSuperClass = new EditorCommand_ChangeSuperClass();
		changeSuperClass.setClassId("IDbus");
		changeSuperClass.setSuperClassId("IDvehicle");
		history.performCommand(changeSuperClass);

		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId("IDbus").isGeneralisation());
		Assert.assertEquals(true, history2.getDomain().getEntityClassWithId("IDbus").getSuperClasses().contains(history2.getDomain().getEntityClassWithId("IDvehicle")));
	}
	
	public void test_round_trip_serialisation_delete_superclass() throws Exception
	{
		String domainName = "NewDomain";
		String classAId = "IDbus";
		String classBId = "IDvehicle";

		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassA = new EditorCommand_AddClassToDomain();
		addClassA.setClassId(classAId);
		history.performCommand(addClassA);
		
		EditorCommand_AddClassToDomain addClassB = new EditorCommand_AddClassToDomain();
		addClassB.setClassId(classBId);
		history.performCommand(addClassB);
		
		EditorCommand_ChangeSuperClass changeSuperClass = new EditorCommand_ChangeSuperClass();
		changeSuperClass.setClassId("IDbus");
		changeSuperClass.setSuperClassId("IDvehicle");
		history.performCommand(changeSuperClass);

		EditorCommand_ChangeSuperClass changeSuperClass2 = new EditorCommand_ChangeSuperClass();
		changeSuperClass2.setClassId("IDbus");
		changeSuperClass2.setSuperClassId(null);
		history.performCommand(changeSuperClass2);
		
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(false, history2.getDomain().getEntityClassWithId("IDbus").isGeneralisation());

	}
	
}
