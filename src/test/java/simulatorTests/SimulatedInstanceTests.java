package test.java.simulatorTests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;
import test.java.helper.DomainWarehouse;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import test.java.mock.MockSimulator;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedInstanceTests extends TestCase {

	public SimulatedInstanceTests(String name) {
		super(name);
	}

	public void test_can_create_instance_of_a_simulated_instance() throws NameNotFoundException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		//user has name and age
		
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance user01 = simulatedUser.createInstance();
		Assert.assertEquals(user01.getAttribute("Name"),user.getAttributeWithName("Name").getType().getDefaultValueAsObject());
		Assert.assertEquals(user01.getAttribute("Age"),user.getAttributeWithName("Age").getType().getDefaultValueAsObject());
	}
	
	public void test_instances_get_unique_id_when_created()
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance user01 = simulatedUser.createInstance();
		SimulatedInstance user02 = simulatedUser.createInstance();
		
		SimulatedInstanceIdentifier user01Id = user01.getIdentifier();
		SimulatedInstanceIdentifier user02Id = user02.getIdentifier();
		
		Assert.assertEquals(simulatedUser, user01Id.getSimulatedClass());
		Assert.assertEquals(simulatedUser, user02Id.getSimulatedClass());
		Assert.assertTrue(user02Id.getSequence() > user01Id.getSequence());
	}
	
	public void test_can_set_attribute_values_of_an_instance()
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		//user has name and age
		
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance user01 = simulatedUser.createInstance();
		
		String newName = "Fred";
		int newAge = 12;
		
		user01.setAttribute("Name",newName);
		user01.setAttribute("Age",newAge);
		
		Assert.assertEquals(user01.getAttribute("Name"),newName);
		Assert.assertEquals(user01.getAttribute("Age"),newAge);
	}
	
	public void test_seting_float_thats_an_int_to_attribute_stores_it_as_an_int() throws NameNotFoundException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		//EntityAttribute age = user.getAttributeWithName("Age");
		//age.setType(FloatingEntityDatatype.getInstance());
		
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedUser = new SimulatedClass(user, mock);
		SimulatedInstance user01 = simulatedUser.createInstance();
		
		String newName = "Fred";
		double newAge = 12.0d;
		
		user01.setAttribute("Name",newName);
		user01.setAttribute("Age",newAge);
		
		Assert.assertEquals(user01.getAttribute("Name"),newName);
		Assert.assertEquals(12,user01.getAttribute("Age"));
	}
	
	public void test_simulated_instance_gets_simulated_classes_initial_state_on_creation() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState cartInitialState = cart.getInitialState();
		
		ISimulator mock = new MockSimulator();
		SimulatedClass simulatedCart = new SimulatedClass(cart, mock);
		SimulatedInstance cart01 = simulatedCart.createInstance();
		SimulatedState cart01State = cart01.getSimulatedState();
		Assert.assertEquals(cartInitialState, cart01State.getConcreteState());
		Assert.assertEquals(cartInitialState.getName(), cart01.getAttribute(EntityAttribute.STATE_ATTRIBUTE_NAME));
	}
	
	public void test_deleting_simulated_instance_decreases_simulated_class_count_of_instances_by_1() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Simulator simulator = new Simulator(shoppingCartDomain);
		SimulatedClass simulatedCart = simulator.getSimulatedClass(cart.getName());
		
		Assert.assertEquals(0, simulatedCart.getInstances().size());
		SimulatedInstance cart01 = simulatedCart.createInstance();
		Assert.assertEquals(1, simulatedCart.getInstances().size());
		cart01.deleteInstance();
		Assert.assertEquals(0, simulatedCart.getInstances().size());
	}
	
	public void test_deleting_child_in_hierarchy_instance_deletes_entire_hierarchy() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		Simulator simulator = new Simulator(warehouse);
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
		
		shippingClerkInstance.deleteInstance();
		
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
	}
	
	public void test_deleting_parent_in_hierarchy_instance_deletes_entire_hierarchy() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		ISimulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		SimulatedHierarchyClass stockClerk = (SimulatedHierarchyClass) simulator.getSimulatedClass("StockClerk");
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		shippingClerk.createInstance();
		SimulatedInstance warehouseClerkInstance = warehouseClerk.getInstances().get(0);
		
		Assert.assertEquals(1,warehouseClerk.getInstances().size());
		Assert.assertEquals(1,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
		
		warehouseClerkInstance.deleteInstance();
		
		Assert.assertEquals(0,warehouseClerk.getInstances().size());
		Assert.assertEquals(0,shippingClerk.getInstances().size());
		Assert.assertEquals(0,stockClerk.getInstances().size());
	}
	
	
	public void test_deleting_instance_removes_all_events_for_the_instance() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = new SimulatedInstanceIdentifier(0, userSimulated);
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(2, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(3, simulator.getEventsInQueueSize());
		
		task01.deleteInstance();
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
	}
	
	public void test_deleting_instance_removes_all_events_for_the_instance_but_leaves_others() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstance user01 = userSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		SimulatedEventInstance event1 = new SimulatedEventInstance(destination, sender);
		
		simulator.registerEventInstance(event1);
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(2, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(3, simulator.getEventsInQueueSize());
		
		task01.deleteInstance();
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
	}
	
	public void test_deleting_child_hierarchy_instance_removes_all_events_for_the_instance() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		Simulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		
		SimulatedInstanceIdentifier sender = new SimulatedInstanceIdentifier(0, warehouseClerk);
		SimulatedInstanceIdentifier destination = shippingClerkInstance.getIdentifier();
	
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		simulator.registerEventInstance(event0);
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(2, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(3, simulator.getEventsInQueueSize());
		
		shippingClerkInstance.deleteInstance();
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
	}
	
	public void test_deleting_parent_hierarchy_instance_removes_all_events_for_the_instance() throws NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouse = DomainWarehouse.getWarehouseDomain();
		Simulator simulator = new Simulator(warehouse);
		SimulatedClass warehouseClerk = simulator.getSimulatedClass("WarehouseClerk");
		SimulatedClass shippingClerk = simulator.getSimulatedClass("ShippingClerk");
		
		SimulatedHierarchyInstance shippingClerkInstance = (SimulatedHierarchyInstance) shippingClerk.createInstance();
		SimulatedInstance warehouseClerkInstance = warehouseClerk.getInstances().get(0);
		
		SimulatedInstanceIdentifier sender = new SimulatedInstanceIdentifier(0, warehouseClerk);
		SimulatedInstanceIdentifier destination = shippingClerkInstance.getIdentifier();
	
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		simulator.registerEventInstance(event0);
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(2, simulator.getEventsInQueueSize());
		
		simulator.registerEventInstance(event0);
		Assert.assertEquals(3, simulator.getEventsInQueueSize());
		
		warehouseClerkInstance.deleteInstance();
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
	}
	
	public void test_deleting_instance_unrelates_all_relations() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass step = simulator.getSimulatedClass("Step");
		SimulatedClass task = simulator.getSimulatedClass("Task");
		SimulatedRelationship r2 = simulator.getRelationshipWithName("R2");
		
		SimulatedInstance step01 = step.createInstance();
		SimulatedInstance task01 = task.createInstance();
		
		Assert.assertEquals(false, r2.isInstanceInRelationship(step01.getIdentifier()));
		Assert.assertEquals(false, r2.isInstanceInRelationship(task01.getIdentifier()));
		
		SimulatedRelationInstance r2Instance = r2.createInstance();
		r2Instance.relateNonReflexiveInstance(step01.getIdentifier());
		r2Instance.relateNonReflexiveInstance(task01.getIdentifier());
		r2.storeRelationInstance(r2Instance);
		
		Assert.assertEquals(true, r2.isInstanceInRelationship(step01.getIdentifier()));
		Assert.assertEquals(true, r2.isInstanceInRelationship(task01.getIdentifier()));
		
		step01.deleteInstance();
		
		Assert.assertEquals(false, r2.isInstanceInRelationship(step01.getIdentifier()));
		Assert.assertEquals(false, r2.isInstanceInRelationship(task01.getIdentifier()));
	}
	
	public void test_deleting_instance_unrelated_all_reflexive_relations() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass task = simulator.getSimulatedClass("Task");
		SimulatedRelationship r4 = simulator.getRelationshipWithName("R4");
		
		SimulatedInstance task01 = task.createInstance();
		SimulatedInstance task02 = task.createInstance();
		
		Assert.assertEquals(false, r4.isInstanceInRelationship(task02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r4.isInstanceInRelationship(task01.getIdentifier(), "Follows"));
		
		SimulatedRelationInstance r4Instance = r4.createInstance();
		r4Instance.relateReflexiveInstance(task02.getIdentifier(), "Leads");
		r4Instance.relateReflexiveInstance(task01.getIdentifier(), "Follows");
		r4.storeRelationInstance(r4Instance);
		
		Assert.assertEquals(true, r4.isInstanceInRelationship(task02.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r4.isInstanceInRelationship(task01.getIdentifier(), "Follows"));
		
		task02.deleteInstance();
		
		Assert.assertEquals(false, r4.isInstanceInRelationship(task02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r4.isInstanceInRelationship(task01.getIdentifier(), "Follows"));
	}
	
	public void test_deleting_child_hierarchy_instance_unrelates_all_relations() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass personal = simulator.getSimulatedClass("Personal");
		SimulatedClass roadTyre = simulator.getSimulatedClass("RoadTyre");
		SimulatedClass bus = simulator.getSimulatedClass("Bus");
		SimulatedClass vehicle = simulator.getSimulatedClass("Bus");
		SimulatedClass tyre = simulator.getSimulatedClass("Tyre");
		SimulatedRelationship r1 = simulator.getRelationshipWithName("R1");
		
		SimulatedInstance personal01 = personal.createInstance();
		SimulatedInstance roadTyre01 = roadTyre.createInstance();
		
		Assert.assertEquals(1, personal.getInstances().size());
		Assert.assertEquals(1, bus.getInstances().size());
		Assert.assertEquals(1, vehicle.getInstances().size());
		
		SimulatedInstance bus01 = bus.getInstances().get(0);
		SimulatedInstance tyre01 = tyre.getInstances().get(0);
			
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		SimulatedRelationInstance r1Instance = r1.createInstance();
		r1Instance.relateNonReflexiveInstance(personal01.getIdentifier());
		r1Instance.relateNonReflexiveInstance(roadTyre01.getIdentifier());
		r1.storeRelationInstance(r1Instance);
		
		Assert.assertEquals(true, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(true, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		personal01.deleteInstance();
		
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		Assert.assertEquals(0, personal.getInstances().size());
		Assert.assertEquals(0, bus.getInstances().size());
		Assert.assertEquals(0, vehicle.getInstances().size());
	}
	
	public void test_deleting_parent_hierarchy_instance_unrelates_all_relations() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass personal = simulator.getSimulatedClass("Personal");
		SimulatedClass roadTyre = simulator.getSimulatedClass("RoadTyre");
		SimulatedClass bus = simulator.getSimulatedClass("Bus");
		SimulatedClass vehicle = simulator.getSimulatedClass("Bus");
		SimulatedClass tyre = simulator.getSimulatedClass("Tyre");
		SimulatedRelationship r1 = simulator.getRelationshipWithName("R1");
		
		SimulatedInstance personal01 = personal.createInstance();
		SimulatedInstance roadTyre01 = roadTyre.createInstance();
		
		Assert.assertEquals(1, personal.getInstances().size());
		Assert.assertEquals(1, bus.getInstances().size());
		Assert.assertEquals(1, vehicle.getInstances().size());
		
		SimulatedInstance bus01 = bus.getInstances().get(0);
		SimulatedInstance tyre01 = tyre.getInstances().get(0);
		
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		SimulatedRelationInstance r1Instance = r1.createInstance();
		r1Instance.relateNonReflexiveInstance(personal01.getIdentifier());
		r1Instance.relateNonReflexiveInstance(roadTyre01.getIdentifier());
		r1.storeRelationInstance(r1Instance);
		
		Assert.assertEquals(true, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(true, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		bus01.deleteInstance();
		
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		Assert.assertEquals(0, personal.getInstances().size());
		Assert.assertEquals(0, bus.getInstances().size());
		Assert.assertEquals(0, vehicle.getInstances().size());
	}
	
	public void test_deleting_root_hierarchy_instance_unrelates_all_relations() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass personal = simulator.getSimulatedClass("Personal");
		SimulatedClass roadTyre = simulator.getSimulatedClass("RoadTyre");
		SimulatedClass bus = simulator.getSimulatedClass("Bus");
		SimulatedClass vehicle = simulator.getSimulatedClass("Bus");
		SimulatedClass tyre = simulator.getSimulatedClass("Tyre");
		SimulatedRelationship r1 = simulator.getRelationshipWithName("R1");
		
		SimulatedInstance personal01 = personal.createInstance();
		SimulatedInstance roadTyre01 = roadTyre.createInstance();
		
		Assert.assertEquals(1, personal.getInstances().size());
		Assert.assertEquals(1, bus.getInstances().size());
		Assert.assertEquals(1, vehicle.getInstances().size());
		
		SimulatedInstance bus01 = bus.getInstances().get(0);
		SimulatedInstance tyre01 = tyre.getInstances().get(0);
		SimulatedInstance vehicle01 = vehicle.getInstances().get(0);
		
				
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		SimulatedRelationInstance r1Instance = r1.createInstance();
		r1Instance.relateNonReflexiveInstance(personal01.getIdentifier());
		r1Instance.relateNonReflexiveInstance(roadTyre01.getIdentifier());
		r1.storeRelationInstance(r1Instance);
		
		Assert.assertEquals(true, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(true, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		vehicle01.deleteInstance();
		
		Assert.assertEquals(false, r1.isInstanceInRelationship(bus01.getIdentifier()));
		Assert.assertEquals(false, r1.isInstanceInRelationship(tyre01.getIdentifier()));
		
		Assert.assertEquals(0, personal.getInstances().size());
		Assert.assertEquals(0, bus.getInstances().size());
		Assert.assertEquals(0, vehicle.getInstances().size());
	}
	
	public void test_delete_reflexive_association_instance_other_way() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass step = simulator.getSimulatedClass("Step");
		SimulatedRelationship r3 = simulator.getRelationshipWithName("R3");
		
		SimulatedInstance step01 = step.createInstance();
		SimulatedInstance step02 = step.createInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		
		SimulatedRelationInstance r3Instance = r3.createInstance();
		r3Instance.relateReflexiveInstance(step02.getIdentifier(), "Follows");
		r3Instance.relateReflexiveInstance(step01.getIdentifier(), "Leads");
		SimulatedInstanceIdentifier sequence01Id = r3Instance.getAssociationInstance();
		SimulatedInstance sequence01 = simulator.getInstanceFromIdentifier(sequence01Id);
		r3.storeRelationInstance(r3Instance);
		
		Assert.assertEquals(true, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		
		sequence01.deleteInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
	}
	
	public void test_delete_reflexive_association_from_one_way_leaves_other_relation() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass step = simulator.getSimulatedClass("Step");
		SimulatedRelationship r3 = simulator.getRelationshipWithName("R3");
		
		SimulatedInstance step01 = step.createInstance();
		SimulatedInstance step02 = step.createInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
		
		SimulatedRelationInstance r3Instance = r3.createInstance();
		r3Instance.relateReflexiveInstance(step02.getIdentifier(), "Follows");
		r3Instance.relateReflexiveInstance(step01.getIdentifier(), "Leads");
		SimulatedInstanceIdentifier sequence01Id = r3Instance.getAssociationInstance();
		SimulatedInstance sequence01 = simulator.getInstanceFromIdentifier(sequence01Id);
		r3.storeRelationInstance(r3Instance);
		
		SimulatedRelationInstance r3Instance_2 = r3.createInstance();
		r3Instance_2.relateReflexiveInstance(step02.getIdentifier(), "Leads");
		r3Instance_2.relateReflexiveInstance(step01.getIdentifier(), "Follows");
		SimulatedInstanceIdentifier sequence02Id = r3Instance_2.getAssociationInstance();
		SimulatedInstance sequence02 = simulator.getInstanceFromIdentifier(sequence02Id);
		r3.storeRelationInstance(r3Instance_2);
		
		Assert.assertEquals(true, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
		
		sequence01.deleteInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
		
		sequence02.deleteInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Follows"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
	}
	
	public void test_when_deleting_an_association_instance_its_relation_is_deleted() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainTTD.getTTDDomain();
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass step = simulator.getSimulatedClass("Step");
		SimulatedRelationship r3 = simulator.getRelationshipWithName("R3");
		
		SimulatedInstance step01 = step.createInstance();
		SimulatedInstance step02 = step.createInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
		
		SimulatedRelationInstance r3Instance = r3.createInstance();
		r3Instance.relateReflexiveInstance(step02.getIdentifier(), "Leads");
		r3Instance.relateReflexiveInstance(step01.getIdentifier(), "Follows");
		SimulatedInstanceIdentifier sequence01Id = r3Instance.getAssociationInstance();
		SimulatedInstance sequence01 = simulator.getInstanceFromIdentifier(sequence01Id);
		r3.storeRelationInstance(r3Instance);
		
		Assert.assertEquals(true, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(true, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
		
		sequence01.deleteInstance();
		
		Assert.assertEquals(false, r3.isInstanceInRelationship(step02.getIdentifier(), "Leads"));
		Assert.assertEquals(false, r3.isInstanceInRelationship(step01.getIdentifier(), "Follows"));
	}
	
	public void test_when_deleting_a_child_hierarchy_association_instance_its_relation_is_deleted() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		
		EntityClass classA = new EntityClass("A");
		domain.addClass(classA);
		EntityClass classB = new EntityClass("B");
		domain.addClass(classB);
		EntityRelation R2 = new EntityRelation("R2");
		R2.setEndA(classA, CardinalityType.ONE_TO_MANY);
		R2.setEndB(classB, CardinalityType.ONE_TO_MANY);
		R2.setAssociation(roadTyre);
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass simA = simulator.getSimulatedClass(classA.getName());
		SimulatedClass simB = simulator.getSimulatedClass(classB.getName());
		SimulatedRelationship SR2 = simulator.getRelationshipWithName(R2.getName());
		
		SimulatedInstance a001 = simA.createInstance();
		SimulatedInstance b001 = simB.createInstance();
		
		SimulatedInstanceIdentifier a001Id = a001.getIdentifier();
		SimulatedInstanceIdentifier b001Id = b001.getIdentifier();
		
		Assert.assertEquals(false, SR2.doesRelationshipExistBetween(a001Id, b001Id));
		
		SimulatedRelationInstance SR2_1 = SR2.createInstance();
		SR2_1.relateNonReflexiveInstance(a001Id);
		SR2_1.relateNonReflexiveInstance(b001Id);
		SimulatedInstanceIdentifier rt001Id = SR2_1.getAssociationInstance();
		SimulatedInstance rt001 = simulator.getInstanceFromIdentifier(rt001Id);
		SR2.storeRelationInstance(SR2_1);
		
		Assert.assertEquals(true, SR2.doesRelationshipExistBetween(a001Id, b001Id));
		
		SimulatedInstanceIdentifier associationIdentifier = ((NonReflexiveRelationshipStorage) SR2.getRelationshipStorage()).getAssociationInstance(a001Id, b001Id);
		Assert.assertEquals(rt001Id, associationIdentifier);
		
		rt001.deleteInstance();
		
		Assert.assertEquals(false, SR2.doesRelationshipExistBetween(a001Id, b001Id));
	}
	
	public void test_when_deleting_a_parent_hierarchy_association_instance_its_relation_is_deleted() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainBus.getBusDomain();
		EntityClass roadTyre = domain.getEntityClassWithName("RoadTyre");
		
		EntityClass classA = new EntityClass("A");
		domain.addClass(classA);
		EntityClass classB = new EntityClass("B");
		domain.addClass(classB);
		EntityRelation R2 = new EntityRelation("R2");
		R2.setEndA(classA, CardinalityType.ONE_TO_MANY);
		R2.setEndB(classB, CardinalityType.ONE_TO_MANY);
		R2.setAssociation(roadTyre);
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass simA = simulator.getSimulatedClass(classA.getName());
		SimulatedClass simB = simulator.getSimulatedClass(classB.getName());
		SimulatedHierarchyClass simTyre = (SimulatedHierarchyClass) simulator.getSimulatedClass("Tyre");
		SimulatedRelationship SR2 = simulator.getRelationshipWithName(R2.getName());
		
		SimulatedInstance a001 = simA.createInstance();
		SimulatedInstance b001 = simB.createInstance();
		
		SimulatedInstanceIdentifier a001Id = a001.getIdentifier();
		SimulatedInstanceIdentifier b001Id = b001.getIdentifier();
		
		Assert.assertEquals(false, SR2.doesRelationshipExistBetween(a001Id, b001Id));
		
		SimulatedRelationInstance SR2_1 = SR2.createInstance();
		SR2_1.relateNonReflexiveInstance(a001Id);
		SR2_1.relateNonReflexiveInstance(b001Id);
		SimulatedInstanceIdentifier rt001Id = SR2_1.getAssociationInstance();
		SimulatedInstance rt001 = simulator.getInstanceFromIdentifier(rt001Id);
		SR2.storeRelationInstance(SR2_1);
		
		Assert.assertEquals(true, SR2.doesRelationshipExistBetween(a001Id, b001Id));
		
		SimulatedInstanceIdentifier associationIdentifier = ((NonReflexiveRelationshipStorage) SR2.getRelationshipStorage()).getAssociationInstance(a001Id, b001Id);
		Assert.assertEquals(rt001Id, associationIdentifier);
		
		SimulatedHierarchyInstance tyre001 = ((SimulatedHierarchyInstance)rt001).getHierarchyInstanceForClass(simTyre); 
		tyre001.deleteInstance();
		
		Assert.assertEquals(false, SR2.doesRelationshipExistBetween(a001Id, b001Id));
	}
	
	public void test_can_delete_instance_with_actionlanguage() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += "CREATE user1 FROM User;\n";
		procedureText += "CREATE user2 FROM User;\n";
		procedureText += "DELETE user1;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedClass simUser = simulator.getSimulatedClass("User");
		Assert.assertEquals(1, simUser.instanceCount());
		Assert.assertEquals(1, simUser.getInstances().get(0).getIdentifier().getSequence());
	}
}

