package test.java.simulatorTests;

import test.java.helper.DomainWarehouse;

import java.util.Collection;

import javax.naming.NameAlreadyBoundException;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedGeneralisationTests extends TestCase {
	
	public SimulatedGeneralisationTests(String name) {
		super(name);
	}

	public void test_simulator_can_create_simulated_hierarchy_class() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("ShippingClerk");
		Assert.assertTrue(shippingClerkSimulated instanceof SimulatedHierarchyClass);
	}
	
	public void test_can_create_simulated_hierarchy_instance() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		
		SimulatedInstance shippingClerkInstance = shippingClerk.createInstance();
		Assert.assertTrue(shippingClerkInstance instanceof SimulatedHierarchyInstance);
	}
	
	public void test_when_creating_sub_class_all_super_class_instances_are_created() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		
		shippingClerk.createInstance();
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(1,shippingClerk.getInstances().size());
	}
	
	public void test_simulated_hierarchy_instances_can_return_list_of_hierarchy_identifiers() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		
		SimulatedInstance shippingClerk001 = shippingClerk.createInstance();
		SimulatedInstance warehouseClerk001 = warehouseClerk.getInstances().get(0);
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(1,shippingClerk.getInstances().size());
		
		Collection<SimulatedInstanceIdentifier> shippingClerkHierarchyIds = ((SimulatedHierarchyInstance)shippingClerk001).getHierarchyIdentifiers();
		Collection<SimulatedInstanceIdentifier> warehouseClerkHierarchyIds = ((SimulatedHierarchyInstance)warehouseClerk001).getHierarchyIdentifiers();
		
		Assert.assertEquals(2, shippingClerkHierarchyIds.size());
		Assert.assertEquals(2, warehouseClerkHierarchyIds.size());
		
		Assert.assertEquals(shippingClerkHierarchyIds, warehouseClerkHierarchyIds);
		Assert.assertEquals(true, shippingClerkHierarchyIds.contains(shippingClerk001.getIdentifier()));
		Assert.assertEquals(true, shippingClerkHierarchyIds.contains(warehouseClerk001.getIdentifier()));
		
		Assert.assertEquals(true, warehouseClerkHierarchyIds.contains(shippingClerk001.getIdentifier()));
		Assert.assertEquals(true, warehouseClerkHierarchyIds.contains(warehouseClerk001.getIdentifier()));
	}
	
	public void test_all_instances_in_hierarchy_know_the_other_class_types_in_the_hierarchy() throws CannotSimulateDomainThatIsInvalidException, NameAlreadyBoundException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");

		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		SimulatedHierarchyInstance warehouseClerkInstance = (SimulatedHierarchyInstance) warehouseClerk.getInstances().get(0);
		
		Assert.assertTrue(shippingClerkInstance.hasHierarchyInstanceForClass(warehouseClerk));
		Assert.assertTrue(warehouseClerkInstance.hasHierarchyInstanceForClass(shippingClerk));
	}
	
	
	public void tesSimulatedHierarchyClassHasListOfRequiredSimulatedHierarchyClassSuperClasses() throws CannotSimulateDomainThatIsInvalidException, NameAlreadyBoundException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedHierarchyClass warehouseClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("WarehouseClerk");
		SimulatedHierarchyClass shippingClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("ShippingClerk");

		Collection<SimulatedHierarchyClass> requiredHierarchyClasses = shippingClerk.getHierarchyDependants();
		Assert.assertEquals(2, requiredHierarchyClasses.size());
		Assert.assertTrue(requiredHierarchyClasses.contains(warehouseClerk));
		Assert.assertTrue(requiredHierarchyClasses.contains(shippingClerk));
	}
	
	
	public void test_all_instances_in_hierarchy_know_the_other_instances_in_the_hierarchy() throws CannotSimulateDomainThatIsInvalidException, NameAlreadyBoundException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedHierarchyClass warehouseClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("WarehouseClerk");
		SimulatedHierarchyClass shippingClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("ShippingClerk");

		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		SimulatedHierarchyInstance warehouseClerkInstance = (SimulatedHierarchyInstance) warehouseClerk.getInstances().get(0);
		
		Assert.assertEquals(shippingClerkInstance, warehouseClerkInstance.getHierarchyInstanceForClass(shippingClerk));
		Assert.assertEquals(warehouseClerkInstance, shippingClerkInstance.getHierarchyInstanceForClass(warehouseClerk));
	}
	
	public void test_when_reclassifing_hierarchy_the_removed_class_is_removed_and_the_added_class_is_added() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(1,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		Assert.assertEquals(shippingClerk, shippingClerkInstance.getLeafInstance().getSimulatedClass());
		Assert.assertEquals(shippingClerk, ((SimulatedHierarchyInstance)warehouseClerk.getInstances().get(0)).getLeafInstance().getSimulatedClass());
		Assert.assertEquals(shippingClerkInstance, shippingClerkInstance.getLeafInstance());
		Assert.assertEquals(shippingClerkInstance, ((SimulatedHierarchyInstance)warehouseClerk.getInstances().get(0)).getLeafInstance());
		
		
		SimulatedHierarchyInstance newHandle = shippingClerkInstance.reclassifyTo(stockClerk);
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(1,stockClerk.getInstances().size());
	
		Assert.assertEquals(stockClerk, shippingClerkInstance.getLeafInstance().getSimulatedClass());
		Assert.assertEquals(stockClerk, ((SimulatedHierarchyInstance)warehouseClerk.getInstances().get(0)).getLeafInstance().getSimulatedClass());
		Assert.assertEquals(stockClerk.getInstances().get(0), shippingClerkInstance.getLeafInstance());
		Assert.assertEquals(stockClerk.getInstances().get(0), ((SimulatedHierarchyInstance)warehouseClerk.getInstances().get(0)).getLeafInstance());
		
		Assert.assertTrue(newHandle.hasHierarchyInstanceForClass(warehouseClerk));
		Assert.assertTrue(!newHandle.hasHierarchyInstanceForClass(shippingClerk));
		Assert.assertTrue(newHandle.hasHierarchyInstanceForClass(stockClerk));
	}
	
	
	public void test_when_reclassifing_hierarchy_the_added_instance_id_is_reference_equal_to_the_new_id() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		SimulatedInstanceIdentifier shippingId = shippingClerkInstance.getIdentifier();
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(1,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		SimulatedHierarchyInstance newHandle = shippingClerkInstance.reclassifyTo(stockClerk);
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(1,stockClerk.getInstances().size());
	
		SimulatedInstanceIdentifier stockId = stockClerk.getInstances().get(0).getIdentifier();
		
		Assert.assertTrue(newHandle.hasHierarchyInstanceForClass(warehouseClerk));
		Assert.assertTrue(!newHandle.hasHierarchyInstanceForClass(shippingClerk));
		Assert.assertTrue(newHandle.hasHierarchyInstanceForClass(stockClerk));
		
		Assert.assertEquals(true, shippingId == stockId);
	}
	
	
	public void test_when_reclassifing_hierarchy_the_persisted_class_is_unchanged() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedHierarchyClass warehouseClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("WarehouseClerk");
		SimulatedHierarchyClass shippingClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		SimulatedHierarchyInstance warehouseClerkInstanceBeforeRefactor = (SimulatedHierarchyInstance) warehouseClerk.getInstances().get(0);
		
		@SuppressWarnings("unused")
		SimulatedHierarchyInstance newHandle = shippingClerkInstance.reclassifyTo(stockClerk);
		
		SimulatedHierarchyInstance warehouseClerkInstanceAfterRefactor = (SimulatedHierarchyInstance) warehouseClerk.getInstances().get(0);
		
		Assert.assertEquals(warehouseClerkInstanceBeforeRefactor, warehouseClerkInstanceAfterRefactor);
	}
	
	
	public void test_hierarchy_instance_has_its_direct_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		Assert.assertTrue(shippingClerkInstance.hasOwnedAttribute("AwaitingAssignment"));
		Assert.assertTrue(!shippingClerkInstance.hasOwnedAttribute("ClerkId"));
	}
	
	public void test_hierarchy_instance_can_get_set_its_direct_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();

		Assert.assertEquals(false,shippingClerkInstance.getAttribute("AwaitingAssignment"));
		shippingClerkInstance.setAttribute("AwaitingAssignment", true);
		Assert.assertEquals(true,shippingClerkInstance.getAttribute("AwaitingAssignment"));
	}
	
	
	public void test_hierarchy_instance_has_hierarchy_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		Assert.assertTrue(shippingClerkInstance.hasAttribute("AwaitingAssignment"));
		Assert.assertTrue(shippingClerkInstance.hasAttribute("ClerkId"));
		Assert.assertTrue(!shippingClerkInstance.hasOwnedAttribute("Idle"));
	}
	
	public void test_hierarchy_instance_can_get_set_hierarchy_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();

		Assert.assertEquals(0,shippingClerkInstance.getAttribute("ClerkId"));
		shippingClerkInstance.setAttribute("ClerkId", 37);
		Assert.assertEquals(37,shippingClerkInstance.getAttribute("ClerkId"));
	}
	
	public void test_reclassified_hierarchy_instance_has_hierarchy_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		
		SimulatedHierarchyInstance stockClerkInstance = shippingClerkInstance.reclassifyTo(stockClerk);
		
		Assert.assertTrue(!stockClerkInstance.hasAttribute("AwaitingAssignment"));
		Assert.assertTrue(stockClerkInstance.hasAttribute("ClerkId"));
		Assert.assertTrue(stockClerkInstance.hasOwnedAttribute("Idle"));
	}
	
	public void test_reclassified_hierarchy_instance_does_not_modify_unchanged_attributes() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		
		Assert.assertEquals(0,shippingClerkInstance.getAttribute("ClerkId"));
		shippingClerkInstance.setAttribute("ClerkId", 37);
		Assert.assertEquals(37,shippingClerkInstance.getAttribute("ClerkId"));
		
		SimulatedHierarchyInstance stockClerkInstance = shippingClerkInstance.reclassifyTo(stockClerk);

		Assert.assertEquals(37,shippingClerkInstance.getAttribute("ClerkId"));
		
		Assert.assertTrue(!stockClerkInstance.hasAttribute("AwaitingAssignment"));
		Assert.assertTrue(stockClerkInstance.hasAttribute("ClerkId"));
		Assert.assertTrue(stockClerkInstance.hasOwnedAttribute("Idle"));
	}
	
}

