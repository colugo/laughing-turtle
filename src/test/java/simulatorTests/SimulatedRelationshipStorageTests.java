package test.java.simulatorTests;


import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import test.java.mock.MockSimulatedClass;

import org.junit.Assert;

import main.java.avii.simulator.relations.BaseRelationshipStorage;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.ReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedRelationshipStorageTests extends TestCase {
	public SimulatedRelationshipStorageTests(String name)
	{
		super(name);
	}
	
	private MockSimulatedClass mockClassA = new MockSimulatedClass("A");
	private MockSimulatedClass mockClassB = new MockSimulatedClass("B");
	private MockSimulatedClass mockClassC = new MockSimulatedClass("C");

	private int _aCount = 0;
	private int _bCount = 0;
	
	
	private SimulatedInstanceIdentifier getIdA()
	{
		SimulatedInstanceIdentifier idA = new SimulatedInstanceIdentifier(this._aCount++, this.mockClassA);
		return idA;
	}
	
	private SimulatedInstanceIdentifier getIdB()
	{
		SimulatedInstanceIdentifier idB = new SimulatedInstanceIdentifier(this._bCount++, this.mockClassB);
		return idB;
	}
	
	public void test_non_reflexive_relationship_storage_can_retrieve_relation_from_either_end()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdB();
		relationInstance.relateNonReflexiveInstance(instanceA);
		relationInstance.relateNonReflexiveInstance(instanceB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceA));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceB));
		storage.storeRelationInstance(relationInstance);
		Assert.assertEquals(true, storage.hasRelationForInstance(instanceA));
		Assert.assertEquals(true, storage.hasRelationForInstance(instanceB));
		
		Assert.assertTrue(storage.getRelationsForInstance(instanceA).contains(relationInstance));
		Assert.assertTrue(storage.getRelationsForInstance(instanceB).contains(relationInstance));
	}
	

	public void test_simulated_instance_can_be_related_to_many_simulated_instances_for_non_reflexive()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			instanceBList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB).size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceB());
		}
	}
	
	public void test_simulated_instance_can_be_related_to_many_simulated_instances_each_creates_an_association_instance_for_non_reflexive()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		relation.setAssociationClass(this.mockClassC);
		
		BaseRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		HashSet<SimulatedInstanceIdentifier> associationSet = new HashSet<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> bList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> assocList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			bList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
			
			SimulatedInstanceIdentifier associationInstance = relationInstance.getAssociationInstance();
			associationSet.add(associationInstance);
			assocList.add(associationInstance);
		}
		
		Assert.assertEquals(10, associationSet.size());
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier foundAssocBA = ((NonReflexiveRelationshipStorage)storage).getAssociationInstance(bList.get(i), instanceA);
			SimulatedInstanceIdentifier foundAssocAB = ((NonReflexiveRelationshipStorage)storage).getAssociationInstance(instanceA, bList.get(i));
			
			Assert.assertNotNull(foundAssocAB);
			Assert.assertEquals(foundAssocBA, foundAssocAB);
			Assert.assertEquals(foundAssocBA, assocList.get(i));
		}
	}
	
	public void test_simulated_instance_can_be_unrelated_from_a_simulated_instances_for_non_reflexive()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			instanceBList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB).size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceB());
		}
		
		storage.unrelateInstances(instanceA, instanceBList.get(0));
		
		Assert.assertEquals(9, storage.getRelationsForInstance(instanceA).size());
		for(int i = 0; i < 10; i ++)
		{
			if(i==0)
			{
				Assert.assertEquals(false, storage.hasRelationForInstance(instanceBList.get(i)));
			}
			else
			{
				Assert.assertEquals(true, storage.hasRelationForInstance(instanceBList.get(i)));
			}
		}
	}
	
	public void test_simulated_instance_can_be_unrelated_from_a_simulated_instances_for_non_reflexive_other_way()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			instanceBList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB).size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB).iterator().next().getInstanceB());
		}
		
		storage.unrelateInstances(instanceBList.get(0), instanceA);
		
		Assert.assertEquals(9, storage.getRelationsForInstance(instanceA).size());
		for(int i = 0; i < 10; i ++)
		{
			if(i==0)
			{
				Assert.assertEquals(false, storage.hasRelationForInstance(instanceBList.get(i)));
			}
			else
			{
				Assert.assertEquals(true, storage.hasRelationForInstance(instanceBList.get(i)));
			}
		}
	}
	
	public void test_non_reflexive_relationship_storage_can_determine_if_a_relation_exists_between_2_instances()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		BaseRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdB();
		
		Assert.assertEquals(false, ((NonReflexiveRelationshipStorage) storage).doesRelationExistBetween(instanceA, instanceB));
		Assert.assertEquals(false, ((NonReflexiveRelationshipStorage) storage).doesRelationExistBetween(instanceB, instanceA));
		
		SimulatedRelationInstance relationInstance = relation.createInstance();
		relationInstance.relateNonReflexiveInstance(instanceA);
		relationInstance.relateNonReflexiveInstance(instanceB);
		storage.storeRelationInstance(relationInstance);
		
		Assert.assertEquals(true, ((NonReflexiveRelationshipStorage) storage).doesRelationExistBetween(instanceA, instanceB));
		Assert.assertEquals(true, ((NonReflexiveRelationshipStorage) storage).doesRelationExistBetween(instanceB, instanceA));
	}

	/////////////////// REFLEXIVE /////////////////////////
	
	public void test_relationship_storage_can_retrieve_reflexive_relation_from_either_end()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdA();
		relationInstance.relateReflexiveInstance(instanceA,"leads");
		relationInstance.relateReflexiveInstance(instanceB,"follows");
		
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceA,"leads"));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceB,"follows"));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceA,"follows"));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceB,"leads"));
		storage.storeRelationInstance(relationInstance);
		Assert.assertEquals(true, storage.hasRelationForInstance(instanceA,"leads"));
		Assert.assertEquals(true, storage.hasRelationForInstance(instanceB,"follows"));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceA,"follows"));
		Assert.assertEquals(false, storage.hasRelationForInstance(instanceB,"leads"));
		
		Assert.assertTrue(storage.getRelationsForInstance(instanceA, "leads").contains(relationInstance));
		Assert.assertTrue(storage.getRelationsForInstance(instanceB, "follows").contains(relationInstance));
	}

	public void test_simulated_instance_can_be_related_to_many_simulated_instances_for_reflexive()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA, "leads").size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB,"follows").size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceB());
		}
	}
	
	public void test_simulated_instance_can_be_related_to_many_simulated_instances_for_reflexive_and_create_associations_for_each()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setAssociationClass(this.mockClassC);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> assocList = new ArrayList<SimulatedInstanceIdentifier>();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		HashSet<SimulatedInstanceIdentifier> assocSet = new HashSet<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			instanceBList.add(instanceB);
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
			SimulatedInstanceIdentifier assoc = relationInstance.getAssociationInstance();
			assocSet.add(assoc);
			assocList.add(assoc);
		}
		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier foundAssocBA = storage.getAssociationInstance(instanceBList.get(i), "follows",  instanceA);
			SimulatedInstanceIdentifier foundAssocAB = storage.getAssociationInstance(instanceA, "leads", instanceBList.get(i));
			
			Assert.assertNotNull(foundAssocAB);
			Assert.assertEquals(foundAssocBA, foundAssocAB);
			Assert.assertEquals(foundAssocBA, assocList.get(i));
			
			SimulatedInstanceIdentifier foundAssocBA2 = storage.getAssociationInstance(instanceBList.get(i), "leads",  instanceA);
			SimulatedInstanceIdentifier foundAssocAB2 = storage.getAssociationInstance(instanceA, "follows", instanceBList.get(i));
			Assert.assertNull(foundAssocAB2);
			Assert.assertNull(foundAssocBA2);
		}		

	}
	
	public void test_simulated_instance_can_be_unrelated_from_simulated_instances_for_reflexive()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			instanceBList.add(instanceB);
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA, "leads").size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB,"follows").size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceB());
		}
		
		storage.unrelateInstances(instanceBList.get(0), "follows", instanceA);
		
		Assert.assertEquals(9, storage.getRelationsForInstance(instanceA, "leads").size());
		for(int i = 0; i < 10; i ++)
		{
			if(i==0)
			{
				Assert.assertEquals(false, storage.hasRelationForInstance(instanceBList.get(i), "follows"));
			}
			else
			{
				Assert.assertEquals(true, storage.hasRelationForInstance(instanceBList.get(i), "follows"));
			}
		}
	}
	
	public void test_simulated_instance_can_be_unrelated_from_simulated_instances_for_reflexive_other_way()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			instanceBList.add(instanceB);
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA, "leads").size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB,"follows").size() );
			
			Assert.assertEquals(instanceA, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceA());
			Assert.assertEquals(instanceB, storage.getRelationsForInstance(instanceB,"follows").iterator().next().getInstanceB());
		}
		
		storage.unrelateInstances(instanceA, "leads", instanceBList.get(0));
		
		Assert.assertEquals(9, storage.getRelationsForInstance(instanceA, "leads").size());
		for(int i = 0; i < 10; i ++)
		{
			if(i==0)
			{
				Assert.assertEquals(false, storage.hasRelationForInstance(instanceBList.get(i), "follows"));
			}
			else
			{
				Assert.assertEquals(true, storage.hasRelationForInstance(instanceBList.get(i), "follows"));
			}
		}
	}
	
	/*
	 * 0..1
	 * 1..1 
	 * 1..*
	 * 
	 * 1..x = all instances of this type must exist in a relation - can check this after every procedure
	 * x..1 = only 1 relation for each related instance - can check this after every operation
	 * 
	 */
	
	public void test_can_get_list_of_instances_that_fail_upper_bound_cardinality_in_relationship_for_endpoint_a()
	{
		// count occurances of the id in the relation
		// if there are more then one, there is a fail
		
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		
		Assert.assertEquals(0, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			instanceBList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		Assert.assertEquals(1, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		
	}
	
	public void test_can_get_list_of_instances_that_fail_upper_bound_cardinality_in_relationship_for_endpoint_b()
	{
		// count occurances of the id in the relation
		// if there are more then one, there is a fail
		
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		
		Assert.assertEquals(0, storage.getInstanceBsThatFailUpperBoundCardinality().size());
		
		
		SimulatedInstanceIdentifier instanceA = this.getIdB();
		
		ArrayList<SimulatedInstanceIdentifier> instanceAList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			instanceAList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		Assert.assertEquals(1, storage.getInstanceBsThatFailUpperBoundCardinality().size());
		
	}
	
	public void test_can_get_list_of_instances_that_fail_upper_bound_cardinality_in_reflexive_relationship_for_endpoint_a()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		Assert.assertEquals(0, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA, "leads").size());
		Assert.assertEquals(1, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		Assert.assertTrue(storage.getInstanceAsThatFailUpperBoundCardinality().contains(instanceA));
	}
	
	public void test_can_get_list_of_instances_that_fail_upper_bound_cardinality_in_reflexive_relationship_for_endpoint_b()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbB("leads");
		relation.setVerbA("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		Assert.assertEquals(0, storage.getInstanceBsThatFailUpperBoundCardinality().size());
		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			relationInstance.relateReflexiveInstance(instanceA,"leads");
			relationInstance.relateReflexiveInstance(instanceB,"follows");
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA, "leads").size());
		Assert.assertEquals(1, storage.getInstanceBsThatFailUpperBoundCardinality().size());
		Assert.assertTrue(storage.getInstanceBsThatFailUpperBoundCardinality().contains(instanceA));
	}

	public void test_can_get_list_of_instances_that_fail_lower_bound_cardinality_in_relationship_for_endpoint_b()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> badBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier instanceB = this.getIdB();
			instanceBList.add(instanceB);
			if(i % 2 == 0)
			{
				SimulatedRelationInstance relationInstance = relation.createInstance();
				relationInstance.relateNonReflexiveInstance(instanceA);
				relationInstance.relateNonReflexiveInstance(instanceB);
				storage.storeRelationInstance(relationInstance);
			}
			else
			{
				badBList.add(instanceB);
			}
		}

		storage.setCurrentListOfSimulatedInstanceBs(instanceBList);
		
		Assert.assertEquals(5, storage.getRelationsForInstance(instanceA).size());
		Assert.assertEquals(5, storage.getInstanceBsThatFailLowerBoundCardinality().size());
		for(SimulatedInstanceIdentifier failingId : storage.getInstanceBsThatFailLowerBoundCardinality())
		{
			Assert.assertTrue(badBList.contains(failingId));
		}
	}
	
	public void test_can_get_list_of_instances_that_fail_lower_bound_cardinality_in_relationship_for_endpoint_a()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceB = this.getIdB();
		
		ArrayList<SimulatedInstanceIdentifier> instanceAList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> badAList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier instanceA = this.getIdA();
			instanceAList.add(instanceA);
			if(i % 2 == 0)
			{
				SimulatedRelationInstance relationInstance = relation.createInstance();
				relationInstance.relateNonReflexiveInstance(instanceA);
				relationInstance.relateNonReflexiveInstance(instanceB);
				storage.storeRelationInstance(relationInstance);
			}
			else
			{
				badAList.add(instanceA);
			}
		}

		storage.setCurrentListOfSimulatedInstanceAs(instanceAList);
		
		Assert.assertEquals(5, storage.getRelationsForInstance(instanceB).size());
		Assert.assertEquals(5, storage.getInstanceAsThatFailLowerBoundCardinality().size());
		for(SimulatedInstanceIdentifier failingId : storage.getInstanceAsThatFailLowerBoundCardinality())
		{
			Assert.assertTrue(badAList.contains(failingId));
		}
	}
	
	
	public void test_can_get_list_of_instances_that_fail_lower_bound_cardinality_in_reflexive_relationship_for_endpoint_b()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		Assert.assertEquals(0, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> badBList = new ArrayList<SimulatedInstanceIdentifier>();
		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier instanceB = this.getIdA();
			instanceBList.add(instanceB);
			
			if(i % 2 == 0)
			{
				SimulatedRelationInstance relationInstance = relation.createInstance();
				relationInstance.relateReflexiveInstance(instanceA,"leads");
				relationInstance.relateReflexiveInstance(instanceB,"follows");
				storage.storeRelationInstance(relationInstance);
			}
			else
			{
				badBList.add(instanceB);
			}
		}
		
		storage.setCurrentListOfSimulatedInstanceBs(instanceBList);
		
		Assert.assertEquals(5, storage.getRelationsForInstance(instanceA, "leads").size());
		Assert.assertEquals(5, storage.getInstanceBsThatFailLowerBoundCardinality().size());
		
		for(SimulatedInstanceIdentifier failingId : storage.getInstanceBsThatFailLowerBoundCardinality())
		{
			Assert.assertTrue(badBList.contains(failingId));
		}
	}
	
	public void test_can_get_list_of_instances_that_fail_lower_bound_cardinality_in_reflexive_relationship_for_endpoint_a()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		
		SimulatedInstanceIdentifier instanceB = this.getIdA();
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		Assert.assertEquals(0, storage.getInstanceAsThatFailUpperBoundCardinality().size());
		
		ArrayList<SimulatedInstanceIdentifier> instanceAList = new ArrayList<SimulatedInstanceIdentifier>();
		ArrayList<SimulatedInstanceIdentifier> badAList = new ArrayList<SimulatedInstanceIdentifier>();
		
		for(int i = 0; i < 10; i ++)
		{
			SimulatedInstanceIdentifier instanceA = this.getIdA();
			instanceAList.add(instanceA);
			
			if(i % 2 == 0)
			{
				SimulatedRelationInstance relationInstance = relation.createInstance();
				relationInstance.relateReflexiveInstance(instanceA,"leads");
				relationInstance.relateReflexiveInstance(instanceB,"follows");
				storage.storeRelationInstance(relationInstance);
			}
			else
			{
				badAList.add(instanceA);
			}
		}
		
		storage.setCurrentListOfSimulatedInstanceAs(instanceAList);
		
		Assert.assertEquals(5, storage.getRelationsForInstance(instanceB, "follows").size());
		Assert.assertEquals(5, storage.getInstanceAsThatFailLowerBoundCardinality().size());
		
		for(SimulatedInstanceIdentifier failingId : storage.getInstanceAsThatFailLowerBoundCardinality())
		{
			Assert.assertTrue(badAList.contains(failingId));
		}
	}

	public void test_non_reflexive_relationship_storage_can_retrieve_other_end_of_relation()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdB();
		relationInstance.relateNonReflexiveInstance(instanceA);
		relationInstance.relateNonReflexiveInstance(instanceB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		storage.storeRelationInstance(relationInstance);
		
		Assert.assertTrue(storage.getOtherEnd(instanceA).contains(instanceB));
		Assert.assertEquals(1, storage.getOtherEnd(instanceA).size());
		Assert.assertTrue(storage.getOtherEnd(instanceB).contains(instanceA));
		Assert.assertEquals(1, storage.getOtherEnd(instanceB).size());
	
		Assert.assertEquals(0, storage.getOtherEnd(this.getIdA()).size());
	}
	
	public void test_relationship_storage_can_retrieve_reflexive_other_end()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdA();
		relationInstance.relateReflexiveInstance(instanceA,"leads");
		relationInstance.relateReflexiveInstance(instanceB,"follows");
		
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		storage.storeRelationInstance(relationInstance);

		Assert.assertTrue(storage.getOtherEnd(instanceA, "leads").contains(instanceB));
		Assert.assertEquals(1, storage.getOtherEnd(instanceA, "leads").size());
		Assert.assertTrue(storage.getOtherEnd(instanceB, "follows").contains(instanceA));
		Assert.assertEquals(1, storage.getOtherEnd(instanceB, "follows").size());
	
		Assert.assertEquals(0, storage.getOtherEnd(this.getIdA(), "leads").size());
		Assert.assertEquals(0, storage.getOtherEnd(this.getIdA(), "follows").size());
	}
	
	public void test_non_reflexive_relationship_storage_can_retrieve_other_end_of_relation_from_collection()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassB);
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdB();
		relationInstance.relateNonReflexiveInstance(instanceA);
		relationInstance.relateNonReflexiveInstance(instanceB);
		
		ArrayList<SimulatedInstanceIdentifier> aList = new ArrayList<SimulatedInstanceIdentifier>();
		aList.add(instanceA);
		ArrayList<SimulatedInstanceIdentifier> bList = new ArrayList<SimulatedInstanceIdentifier>();
		bList.add(instanceB);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		storage.storeRelationInstance(relationInstance);
		
		Assert.assertTrue(storage.getOtherEnd(aList).contains(instanceB));
		Assert.assertEquals(1, storage.getOtherEnd(aList).size());
		Assert.assertTrue(storage.getOtherEnd(bList).contains(instanceA));
		Assert.assertEquals(1, storage.getOtherEnd(bList).size());
	
	}
	
	public void test_relationship_storage_can_retrieve_reflexive_other_end_from_collection()
	{
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(this.mockClassA);
		relation.setClassB(this.mockClassA);
		relation.setVerbA("leads");
		relation.setVerbB("follows");
		SimulatedRelationInstance relationInstance = relation.createInstance();
		SimulatedInstanceIdentifier instanceA = this.getIdA();
		SimulatedInstanceIdentifier instanceB = this.getIdA();
		relationInstance.relateReflexiveInstance(instanceA,"leads");
		relationInstance.relateReflexiveInstance(instanceB,"follows");

		ArrayList<SimulatedInstanceIdentifier> aList = new ArrayList<SimulatedInstanceIdentifier>();
		aList.add(instanceA);
		ArrayList<SimulatedInstanceIdentifier> bList = new ArrayList<SimulatedInstanceIdentifier>();
		bList.add(instanceB);
	
		ReflexiveRelationshipStorage storage = new ReflexiveRelationshipStorage(relation);
		storage.storeRelationInstance(relationInstance);

		Assert.assertTrue(storage.getOtherEnd(aList, "leads").contains(instanceB));
		Assert.assertEquals(1, storage.getOtherEnd(aList, "leads").size());
		Assert.assertTrue(storage.getOtherEnd(bList, "follows").contains(instanceA));
		Assert.assertEquals(1, storage.getOtherEnd(bList, "follows").size());
		}
	
}
