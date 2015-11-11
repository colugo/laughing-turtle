package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;

import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

import test.java.mock.MockSimulatedClass;
import test.java.mock.MockSimulator;

import org.junit.Assert;

import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.ReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedRelationshipTests extends TestCase {
	public SimulatedRelationshipTests(String name)
	{
		super(name);
	}
	
	public void test_can_create_simulated_relationship_with_name()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setName("R1");
		Assert.assertEquals("R1", relation.getName());
	}
	
	public void test_can_create_simulated_relationship_with_end_a()
	{
		MockSimulatedClass A = new MockSimulatedClass("A");
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(A);
		Assert.assertEquals(A, relation.getClassA());
	}
	
	public void test_can_create_simulated_relationship_with_end_b()
	{
		MockSimulatedClass B = new MockSimulatedClass("B");
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassB(B);
		Assert.assertEquals(B, relation.getClassB());
	}
	
	public void test_can_create_simulated_relationship_with_association_class()
	{
		MockSimulatedClass C = new MockSimulatedClass("C");
		SimulatedRelationship relation = new SimulatedRelationship();
		Assert.assertEquals(false, relation.hasAssociation());
		relation.setAssociationClass(C);
		Assert.assertEquals(true, relation.hasAssociation());
		Assert.assertEquals(C, relation.getAssociationClass());
	}
	
	public void test_relation_is_not_reflexive_if_a_and_b_are_different()
	{
		MockSimulatedClass A = new MockSimulatedClass("A");
		MockSimulatedClass B = new MockSimulatedClass("B");
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(A);
		relation.setClassB(B);
		Assert.assertEquals(false, relation.isReflexive());
	}

	public void test_relation_can_have_verb_prase_for_a()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setVerbA("leads");
		Assert.assertEquals("leads", relation.getVerbA());
	}
	
	public void test_relation_can_have_verb_prase_for_b()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setVerbB("follows");
		Assert.assertEquals("follows", relation.getVerbB());
	}
	
	public void test_can_create_simulated_relationship_with_end_a_cardinality()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setCardinalityA(CardinalityType.ZERO_TO_MANY);
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, relation.getCardinalityA());
	}
	
	public void test_can_create_simulated_relationship_with_end_b_cardinality()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setCardinalityB(CardinalityType.ZERO_TO_MANY);
		Assert.assertEquals(CardinalityType.ZERO_TO_MANY, relation.getCardinalityB());
	}
	
	public void test_can_create_simulated_relationship_from_entity_relationship() throws NameNotFoundException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityRelation R1 = domain.getRelationWithName("R1");
				
		MockSimulator mockSimulator = new MockSimulator();
		mockSimulator.addMockClass(new MockSimulatedClass("ShoppingCart"));
		mockSimulator.addMockClass(new MockSimulatedClass("Item"));
		mockSimulator.addMockClass(new MockSimulatedClass("ItemSelection"));
		
		SimulatedRelationship sR1 = new SimulatedRelationship(R1, mockSimulator);
		
		Assert.assertEquals(R1.getName(), sR1.getName());
		Assert.assertEquals(R1.getClassA().getName(), sR1.getClassA().getName());
		Assert.assertEquals(R1.getClassB().getName(), sR1.getClassB().getName());
		Assert.assertEquals(R1.hasAssociation(), sR1.hasAssociation());
		Assert.assertEquals(R1.isReflexive(), sR1.isReflexive());
		Assert.assertEquals(R1.getAssociation().getName(), sR1.getAssociationClass().getName());
		Assert.assertEquals(R1.getCardinalityA(), sR1.getCardinalityA());
		Assert.assertEquals(R1.getCardinalityB(), sR1.getCardinalityB());
	}
	
	public void test_simulated_non_reflexive_relationship_creates_non_reflexive_storage() throws NameNotFoundException
	{
		MockSimulator mockSimulator = new MockSimulator();
		mockSimulator.addMockClass(new MockSimulatedClass("Task"));
		mockSimulator.addMockClass(new MockSimulatedClass("Step"));
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation R2 = domain.getRelationWithName("R2");
		SimulatedRelationship sR2 = new SimulatedRelationship(R2, mockSimulator);
		Assert.assertTrue(sR2.getRelationshipStorage() instanceof NonReflexiveRelationshipStorage);
	}
	
	public void test_simulated_reflexive_relationship_creates_reflexive_storage() throws NameNotFoundException
	{
		MockSimulator mockSimulator = new MockSimulator();
		mockSimulator.addMockClass(new MockSimulatedClass("Task"));
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation R4 = domain.getRelationWithName("R4");
		SimulatedRelationship sR4 = new SimulatedRelationship(R4, mockSimulator);
		Assert.assertTrue(sR4.getRelationshipStorage() instanceof ReflexiveRelationshipStorage);
	}
	
	public void test_simulated_non_reflexive_relationsihp_can_store_relationship_instance() throws NameNotFoundException
	{
		MockSimulator mockSimulator = new MockSimulator();
		mockSimulator.addMockClass(new MockSimulatedClass("Task"));
		mockSimulator.addMockClass(new MockSimulatedClass("Step"));
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation R2 = domain.getRelationWithName("R2");
		SimulatedRelationship sR2 = new SimulatedRelationship(R2, mockSimulator);
		
		SimulatedInstanceIdentifier task01 = new SimulatedInstanceIdentifier(1, mockSimulator.getSimulatedClass("Task"));
		SimulatedInstanceIdentifier step01 = new SimulatedInstanceIdentifier(1, mockSimulator.getSimulatedClass("Step"));
		
		SimulatedRelationInstance sR2_01 = sR2.createInstance();
		sR2_01.relateNonReflexiveInstance(task01);
		sR2_01.relateNonReflexiveInstance(step01);
		
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(task01, step01));
		Assert.assertEquals(false, sR2.doesRelationshipExistBetween(step01, task01));
		sR2.storeRelationInstance(sR2_01);
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(task01, step01));
		Assert.assertEquals(true, sR2.doesRelationshipExistBetween(step01, task01));
	}
	
	public void test_simulated_reflexive_relationsihp_can_store_relationship_instance() throws NameNotFoundException
	{
		MockSimulator mockSimulator = new MockSimulator();
		mockSimulator.addMockClass(new MockSimulatedClass("Task"));
		EntityDomain domain = DomainTTD.getTTDDomain();
		EntityRelation R4 = domain.getRelationWithName("R4");
		SimulatedRelationship sR4 = new SimulatedRelationship(R4, mockSimulator);
		
		SimulatedInstanceIdentifier task01 = new SimulatedInstanceIdentifier(1, mockSimulator.getSimulatedClass("Task"));
		SimulatedInstanceIdentifier task02 = new SimulatedInstanceIdentifier(1, mockSimulator.getSimulatedClass("Task"));
		
		SimulatedRelationInstance sR4_01 = sR4.createInstance();
		sR4_01.relateReflexiveInstance(task01, "Leads");
		sR4_01.relateReflexiveInstance(task02, "Follows");
		
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task01, "Leads", task02,"Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task02, "Follows", task01, "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task02, "Leads", task01,"Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task01, "Follows", task02, "Leads"));
		sR4.storeRelationInstance(sR4_01);
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task01, "Leads", task02,"Follows"));
		Assert.assertEquals(true, sR4.doesRelationshipExistBetween(task02, "Follows", task01, "Leads"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task02, "Leads", task01,"Follows"));
		Assert.assertEquals(false, sR4.doesRelationshipExistBetween(task01, "Follows", task02, "Leads"));
	}
}
