package test.java.simulatorTests;

import test.java.helper.DomainBus;
import junit.framework.TestCase;

import test.java.mock.MockSimulatedClass;

import org.junit.Assert;

import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedRelationshipInstanceTests extends TestCase {
	public SimulatedRelationshipInstanceTests(String name)
	{
		super(name);
	}

	private MockSimulatedClass mockClassA = new MockSimulatedClass("A");
	private MockSimulatedClass mockClassB = new MockSimulatedClass("B");
	private MockSimulatedClass mockClassC = new MockSimulatedClass("C");

	
	
	private SimulatedInstanceIdentifier getIdA()
	{
		SimulatedInstanceIdentifier idA = new SimulatedInstanceIdentifier(0, this.mockClassA);
		return idA;
	}
	
	private SimulatedInstanceIdentifier getIdB()
	{
		SimulatedInstanceIdentifier idB = new SimulatedInstanceIdentifier(0, this.mockClassB);
		return idB;
	}
	
	private SimulatedInstanceIdentifier getIdC()
	{
		SimulatedInstanceIdentifier idC = new SimulatedInstanceIdentifier(0, this.mockClassC);
		return idC;
	}
	
	public void test_simulated_instance_can_be_related_to_another_simulated_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdA()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdB()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdC()));
		relationInstance.relateNonReflexiveInstance(this.getIdA());
		relationInstance.relateNonReflexiveInstance(this.getIdB());
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(this.getIdA()));
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(this.getIdB()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdC()));
		Assert.assertEquals(this.getIdA(), relationInstance.getInstanceA());
		Assert.assertEquals(this.getIdB(), relationInstance.getInstanceB());
	}
		
	public void test_simulated_instance_can_be_related_to_the_same_simulated_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdA()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdB()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdC()));
		relationInstance.relateReflexiveInstance(this.getIdA(), "leads");
		relationInstance.relateReflexiveInstance(this.getIdA(), "follows");
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(this.getIdA()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdB()));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(this.getIdC()));
		Assert.assertEquals(this.getIdA(), relationInstance.getReflexiveInstance("leads"));
		Assert.assertEquals(this.getIdA(), relationInstance.getReflexiveInstance("follows"));

	}
	
	public void test_simulated_instance_can_be_related_to_a_different_simulated_instance_and_creates_an_association_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		relation.setAssociationClass(this.mockClassC);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		relationInstance.relateNonReflexiveInstance(this.getIdA());
		relationInstance.relateNonReflexiveInstance(this.getIdB());
		SimulatedInstanceIdentifier associationInstance = relationInstance.getAssociationInstance();
		Assert.assertEquals(associationInstance.getSimulatedClass(), this.mockClassC);
	}
	
	public void test_simulated_instance_can_be_related_to_the_same_simulated_instance_and_creates_an_association_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setAssociationClass(this.mockClassC);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		relationInstance.relateReflexiveInstance(this.getIdA(), "leads");
		relationInstance.relateReflexiveInstance(this.getIdA(), "follows");
		SimulatedInstanceIdentifier associationInstance = relationInstance.getAssociationInstance();
		Assert.assertEquals(associationInstance.getSimulatedClass(), this.mockClassC);
	}
	
	public void test_simulated_instance_can_be_related_to_a_different_simulated_instance_and_second_call_doesnt_create_an_association_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		relation.setAssociationClass(this.mockClassC);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		relationInstance.relateNonReflexiveInstance(this.getIdA());
		relationInstance.relateNonReflexiveInstance(this.getIdB());
		SimulatedInstanceIdentifier associationInstance1 = relationInstance.getAssociationInstance();
		SimulatedInstanceIdentifier associationInstance2 = relationInstance.getAssociationInstance();
		Assert.assertTrue(associationInstance1 == associationInstance2);
		
	}
	
	public void test_simulated_instance_can_be_related_to_the_same_simulated_instance_and_second_call_doesnt_create_an_association_instance()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setAssociationClass(this.mockClassC);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		relationInstance.relateReflexiveInstance(this.getIdA(), "leads");
		relationInstance.relateReflexiveInstance(this.getIdA(), "follows");
		SimulatedInstanceIdentifier associationInstance1 = relationInstance.getAssociationInstance();
		SimulatedInstanceIdentifier associationInstance2 = relationInstance.getAssociationInstance();
		Assert.assertTrue(associationInstance1 == associationInstance2);
	}
	
	public void test_can_create_relationship_when_one_instance_is_in_a_hierarchy_and_not_the_class_with_the_relation() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedRelationship R1 = simulator.getRelationshipWithName("R1");
		SimulatedClass personalClass = simulator.getSimulatedClass("Personal");
		SimulatedHierarchyClass busClass = (SimulatedHierarchyClass) simulator.getSimulatedClass("Bus");
		SimulatedClass roadTyreClass = simulator.getSimulatedClass("RoadTyre");
		SimulatedHierarchyClass tyreClass = (SimulatedHierarchyClass) simulator.getSimulatedClass("Tyre");
		
		SimulatedHierarchyClass vehicleClass = (SimulatedHierarchyClass) simulator.getSimulatedClass("Vehicle");
		
		SimulatedHierarchyInstance personal = (SimulatedHierarchyInstance) personalClass.createInstance();
		SimulatedHierarchyInstance bus = personal.getHierarchyInstanceForClass(busClass);
		SimulatedHierarchyInstance vehicle = personal.getHierarchyInstanceForClass(vehicleClass);
		Assert.assertNotNull(vehicle);
		
		SimulatedHierarchyInstance roadTyre = (SimulatedHierarchyInstance) roadTyreClass.createInstance();
		SimulatedHierarchyInstance tyre = roadTyre.getHierarchyInstanceForClass(tyreClass);
		Assert.assertNotNull(tyre);
		
		SimulatedInstanceIdentifier personalId = personal.getIdentifier();
		SimulatedInstanceIdentifier busId = bus.getIdentifier();
		SimulatedInstanceIdentifier roadTyreId = roadTyre.getIdentifier();
		SimulatedInstanceIdentifier tyreId = tyre.getIdentifier();
		
		SimulatedRelationInstance relationInstance = R1.createInstance();
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(personalId));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(busId));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(roadTyreId));
		Assert.assertEquals(false, relationInstance.isInsanceInRelation(tyreId));

		relationInstance.relateNonReflexiveInstance(personalId);
		relationInstance.relateNonReflexiveInstance(roadTyreId);

		Assert.assertEquals(true, relationInstance.isInsanceInRelation(busId));
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(personalId));
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(roadTyreId));
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(tyreId));
		Assert.assertEquals(true, relationInstance.isInsanceInRelation(vehicle.getIdentifier()));
		
		Assert.assertEquals(busId, relationInstance.getInstanceA());
		Assert.assertEquals(tyreId, relationInstance.getInstanceB());
	}
	
}

