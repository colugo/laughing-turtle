package test.java.modelTests;

import test.java.helper.DomainTTD;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;

public class TestVectorRelationshipTablesTests extends TestCase {
	public TestVectorRelationshipTablesTests(String name)
	{
		super(name);
	}
	
	public void test_can_add_relationship_table_to_vector() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		Assert.assertEquals(r1, table.getTableRelation());
	}
	
	public void test_initial_table_has_no_rows() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		Assert.assertEquals(r1, table.getTableRelation());
		Assert.assertEquals(0, table.getInstances().size());
	}
	
	public void test_can_create_relation_instance_and_specify_ends() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		TestVectorRelationInstance instance = table.createInstance();
		
		Assert.assertEquals(table, instance.getTable());
		
		instance.setEndA("endA");
		instance.setEndB("endB");
		Assert.assertEquals(false, instance.hasAssociation());
		instance.setAssociation("assoc");
		Assert.assertEquals(true, instance.hasAssociation());
		
		Assert.assertEquals("endA", instance.getEndA());
		Assert.assertEquals("endB", instance.getEndB());
		Assert.assertEquals("assoc", instance.getAssociation());
	}
	
	public void test_can_add_instances_to_table() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		Assert.assertEquals(r1, table.getTableRelation());
		Assert.assertEquals(0, table.getInstances().size());
		
		table.createInstance();
		
		Assert.assertEquals(1, table.getInstances().size());
	}
	
	public void test_serialise_table_without_specifying_association() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		
		TestVectorRelationInstance instance = table.createInstance();
		instance.setEndA("endA");
		instance.setEndB("endB");
		
		String expected = "RELATE endA TO endB ACROSS R1 CREATING Assignment_0;\n";
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_serialise_table_with_specified_association() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r1 = domain.getRelationWithName("R1");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r1);
		
		TestVectorRelationInstance instance = table.createInstance();
		instance.setEndA("endA");
		instance.setEndB("endB");
		instance.setAssociation("assoc");
		
		String expected = "RELATE endA TO endB ACROSS R1 CREATING assoc;\n";
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_serialise_reflexive_table() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r4 = domain.getRelationWithName("R4");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r4);
		
		Assert.assertEquals("Leads", r4.getClassAVerb());
		
		TestVectorRelationInstance instance = table.createInstance();
		instance.setEndA("endA");
		instance.setEndB("endB");
		
		String expected = "RELATE endA TO endB ACROSS R4.\"Leads\";\n";
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_serialise_reflexive_table_with_unspecified_association() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r3 = domain.getRelationWithName("R3");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r3);
		
		Assert.assertEquals("Leads", r3.getClassAVerb());
		
		TestVectorRelationInstance instance = table.createInstance();
		instance.setEndA("endA");
		instance.setEndB("endB");
		
		String expected = "RELATE endA TO endB ACROSS R3.\"Leads\" CREATING Sequence_0;\n";
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
	public void test_serialise_reflexive_table_with_specified_association() throws NameNotFoundException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation r3 = domain.getRelationWithName("R3");
		
		TestVectorRelationTable table = new TestVectorRelationTable(r3);
		
		Assert.assertEquals("Leads", r3.getClassAVerb());
		
		TestVectorRelationInstance instance = table.createInstance();
		instance.setEndA("endA");
		instance.setEndB("endB");
		instance.setAssociation("assoc");
		
		String expected = "RELATE endA TO endB ACROSS R3.\"Leads\" CREATING assoc;\n";
		Assert.assertEquals(expected, table.serialiseToInitialState());
	}
	
}
