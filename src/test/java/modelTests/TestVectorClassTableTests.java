package test.java.modelTests;

import test.java.helper.DomainTTD;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;

public class TestVectorClassTableTests extends TestCase{
	public TestVectorClassTableTests(String name)
	{
		super(name);
	}
	
	public void test_can_create_vector_table_for_entity_class() {
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		Assert.assertEquals(task, table.getTableClass());
	}
		
	public void test_count_instances_in_table_should_be_0_initially()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		Assert.assertEquals(0, table.getInstances().size());
	}
	
	public void test_can_create_instance_for_table()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		table.createInstance();
		
		Assert.assertEquals(1, table.getInstances().size());
	}
	
	public void test_can_add_attribute_to_table() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		Assert.assertEquals(0, table.getRequestedAttributes().size());
		
		table.addAttribute(taskName);
		
		Assert.assertEquals(1, table.getRequestedAttributes().size());
	}
	
	public void test_instance_from_table_gets_default_value_of_attributes() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		table.addAttribute(taskName);
		
		TestVectorClassInstance instance = table.createInstance();
		Assert.assertEquals(taskName.getType().getDefaultValue(), instance.getInitialAttribute("Name"));
	}
	
	public void test_all_instances_get_default_of_newly_added_attributes() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		EntityAttribute taskStatus = task.getAttributeWithName("Status");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		table.addAttribute(taskName);
		Assert.assertEquals(true, table.getRequestedAttributes().contains(taskName));
		
		TestVectorClassInstance instance = table.createInstance();
		Assert.assertEquals(taskName.getType().getDefaultValue(), instance.getInitialAttribute("Name"));
		
		Assert.assertEquals(false, table.getRequestedAttributes().contains(taskStatus));
		
		Assert.assertEquals(null, instance.getInitialAttribute("Status"));
		
		table.addAttribute(taskStatus);
		
		Assert.assertEquals(taskStatus.getType().getDefaultValue(), instance.getInitialAttribute("Status"));
	}
	
	public void test_can_set_initial_attribute_value_to_instance() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		table.addAttribute(taskName);
		
		TestVectorClassInstance instance = table.createInstance();
		Assert.assertEquals(taskName.getType().getDefaultValue(), instance.getInitialAttribute("Name"));
		instance.setInitialAttribute("Name", "Clean room");
		Assert.assertEquals("Clean room", instance.getInitialAttribute("Name"));
	}

	
	public void test_all_instances_get_unique_name() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		
		TestVectorClassInstance instance0 = table.createInstance();
		TestVectorClassInstance instance1 = table.createInstance();
		
		Assert.assertNotSame(instance0.getName(), instance1.getName());
	}
	
	public void test_can_serialise_to_initial_state_test_table_to_actionlanguage()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		
		table.createInstance();
		
		String expected = "CREATE Task_001 FROM Task;\n";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_can_serialise_to_initial_state_test_table_with_custom_instance_name_to_actionlanguage()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		
		TestVectorClassInstance instance = table.createInstance();
		instance.setName("Fred");
		
		String expected = "CREATE Fred FROM Task;\n";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_can_serialise_to_initial_state_test_table_with_multiple_instances_to_actionlanguage()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		
		table.createInstance();
		table.createInstance();
		
		String expected = "CREATE Task_001 FROM Task;\nCREATE Task_002 FROM Task;\n";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_can_serialise_to_initial_state_test_table_with_attributes_to_actionlanguage() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		table.addAttribute(taskName);
		
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Name", "Frank");
		
		String expected = "CREATE Task_001 FROM Task;\nTask_001.Name = \"Frank\";\n";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_can_create_table_for_association_tables_but_wont_create_action_language_for_them() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass sequence = domain.getEntityClassWithName("Sequence");
		EntityAttribute taskOrder = sequence.getAttributeWithName("Order");
		
		TestVectorClassTable table = new TestVectorClassTable(sequence);
		table.addAttribute(taskOrder);
		
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Order", "1");
		instance.setName("seq");
		
		String expected = "";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
		
		expected = "seq.Order = 1;\n";
		Assert.assertEquals(expected, table.serialiseToInitialStateForAssociationClasses());
	}

	public void test_specify_state_for_each_instance_or_null_for_default()
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityClass task = domain.getEntityClassWithName("Task");
		
		TestVectorClassTable table = new TestVectorClassTable(task);
		
		TestVectorClassInstance instance1 = table.createInstance();
		TestVectorClassInstance instance2 = table.createInstance();
		instance1.setInitialState("state1");
		
		String expected = "CREATE Task_001 FROM Task;\nCREATE Task_002 FROM Task;\n";
		
		Assert.assertEquals(expected, table.serialiseToInitialState());
		Assert.assertEquals(true, instance1.hasSpecifiedInitialState());
		Assert.assertEquals(false, instance2.hasSpecifiedInitialState());
		Assert.assertEquals("state1", instance1.getInitialState());
		Assert.assertEquals(null, instance2.getInitialState());
	}
	
}
