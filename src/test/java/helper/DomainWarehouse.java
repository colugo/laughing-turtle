package test.java.helper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class DomainWarehouse extends TestCase {

	public DomainWarehouse(String name) {
		super(name);
	}

	/**
	*                                   +-----------------+
	*                                +--+ShippingClerk    |AwaitingAssignment:Boolean
	*                                |  +-----------------+
	*       +---------------+  /|    |
	*       |WarehouseClerk |.< +----+
	*       +------+--------+  \|    |  +-----------------+
	*              |R1               +--+StockClerk       |Idle:Boolean
	*              |                 |  +-----------------+
	*       +------+--------+        |
	*       | Warehouse     |        |
	*       +---------------+        |  +-----------------+
	*                                +--+OffDutyClerk     |
	*                                   +-----------------+
	 * @throws NameAlreadyBoundException 
	*/
	public static EntityDomain getWarehouseDomain() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("Warehouse domain");
		EntityClass warehouse = new EntityClass("Warehouse");
		domain.addClass(warehouse);
		EntityClass warehouseClerk = new EntityClass("WarehouseClerk");
		domain.addClass(warehouseClerk);
		EntityClass shippingClerk = new EntityClass("ShippingClerk");
		domain.addClass(shippingClerk);
		EntityClass stockClerk = new EntityClass("StockClerk");
		domain.addClass(stockClerk);
		EntityClass offDutyClerk = new EntityClass("OffDutyClerk");
		domain.addClass(offDutyClerk);
		
		warehouseClerk.addSubClass(shippingClerk);
		warehouseClerk.addSubClass(stockClerk);
		warehouseClerk.addSubClass(offDutyClerk);
		
		EntityAttribute clerkId = new EntityAttribute("ClerkId", IntegerEntityDatatype.getInstance());
		warehouseClerk.addAttribute(clerkId);
		
		EntityAttribute clerkName = new EntityAttribute("ClerkName", StringEntityDatatype.getInstance());
		warehouseClerk.addAttribute(clerkName);
		
		EntityAttribute goOffDutyAtEndOfJob = new EntityAttribute("GoOffDutyAtEndOfJob", BooleanEntityDatatype.getInstance());
		warehouseClerk.addAttribute(goOffDutyAtEndOfJob);
		
		EntityAttribute awaitingAssignment = new EntityAttribute("AwaitingAssignment", BooleanEntityDatatype.getInstance());
		shippingClerk.addAttribute(awaitingAssignment);
		
		EntityAttribute idle = new EntityAttribute("Idle", BooleanEntityDatatype.getInstance());
		stockClerk.addAttribute(idle);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(warehouseClerk, CardinalityType.ONE_TO_MANY);
		r1.setEndB(warehouse, CardinalityType.ONE_TO_ONE);

		/**
		 * OffDutyState
		 * 		startStocking()
		 * StockingState
		 * 		startShipping()
		 * ShippingState
		 * 		goGeneral()
		 * GeneralState
		 */
		
		EntityState offDutyState = new EntityState("OffDutyState");
		EntityState stockingState = new EntityState("StockingState");
		EntityState shippingState = new EntityState("ShippingState");
		EntityState generalState = new EntityState("GeneralState");
		
		EntityState offDutyInitialState = new EntityState("InitialOffDutyState");
		EntityState stockingInitialState = new EntityState("InitialStockingState");
		EntityState shippingInitialState = new EntityState("InitialShippingState");
		
		warehouseClerk.addState(offDutyState);
		warehouseClerk.addState(stockingState);
		warehouseClerk.addState(shippingState);
		warehouseClerk.addState(generalState);
		
		warehouseClerk.addState(offDutyInitialState);
		warehouseClerk.addState(stockingInitialState);
		warehouseClerk.addState(shippingInitialState);
		
		offDutyClerk.setInitial(offDutyInitialState);
		stockClerk.setInitial(stockingInitialState);
		shippingClerk.setInitial(shippingInitialState);
		
		
		EntityEventSpecification startStockingSpec = new EntityEventSpecification(warehouseClerk, "startStocking");
		EntityEventSpecification startShippingSpec = new EntityEventSpecification(warehouseClerk, "startShipping");
		EntityEventSpecification goGeneralSpec = new EntityEventSpecification(warehouseClerk, "goGeneral");
		EntityEventSpecification goInitSpec = new EntityEventSpecification(warehouseClerk, "goInit");

		EntityEventInstance startStockingInstance = new EntityEventInstance(startStockingSpec, offDutyState, stockingState);
		EntityEventInstance startShippingInstance = new EntityEventInstance(startShippingSpec, stockingState, shippingState);
		EntityEventInstance goGeneralInstance = new EntityEventInstance(goGeneralSpec, shippingState, generalState);
		
		warehouseClerk.addEventInstance(startShippingSpec, startStockingInstance);
		warehouseClerk.addEventInstance(startShippingSpec, startShippingInstance);
		warehouseClerk.addEventInstance(goGeneralSpec, goGeneralInstance);
		
		warehouseClerk.addEventInstance(goInitSpec, new EntityEventInstance(goInitSpec, shippingInitialState, shippingState));
		warehouseClerk.addEventInstance(goInitSpec, new EntityEventInstance(goInitSpec, stockingInitialState, stockingState));
		warehouseClerk.addEventInstance(goInitSpec, new EntityEventInstance(goInitSpec, offDutyInitialState, offDutyState));
		
		return domain;
	}
	
	
	public static EntityDomain getWarehouseDomainWithoutInitialStates() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("Warehouse domain");
		EntityClass warehouse = new EntityClass("Warehouse");
		domain.addClass(warehouse);
		EntityClass warehouseClerk = new EntityClass("WarehouseClerk");
		domain.addClass(warehouseClerk);
		EntityClass shippingClerk = new EntityClass("ShippingClerk");
		domain.addClass(shippingClerk);
		EntityClass stockClerk = new EntityClass("StockClerk");
		domain.addClass(stockClerk);
		EntityClass offDutyClerk = new EntityClass("OffDutyClerk");
		domain.addClass(offDutyClerk);
		
		warehouseClerk.addSubClass(shippingClerk);
		warehouseClerk.addSubClass(stockClerk);
		warehouseClerk.addSubClass(offDutyClerk);
		
		EntityAttribute clerkId = new EntityAttribute("ClerkId", IntegerEntityDatatype.getInstance());
		warehouseClerk.addAttribute(clerkId);
		
		EntityAttribute clerkName = new EntityAttribute("ClerkName", StringEntityDatatype.getInstance());
		warehouseClerk.addAttribute(clerkName);
		
		EntityAttribute goOffDutyAtEndOfJob = new EntityAttribute("GoOffDutyAtEndOfJob", BooleanEntityDatatype.getInstance());
		warehouseClerk.addAttribute(goOffDutyAtEndOfJob);
		
		EntityAttribute awaitingAssignment = new EntityAttribute("AwaitingAssignment", BooleanEntityDatatype.getInstance());
		shippingClerk.addAttribute(awaitingAssignment);
		
		EntityAttribute idle = new EntityAttribute("Idle", BooleanEntityDatatype.getInstance());
		stockClerk.addAttribute(idle);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(warehouseClerk, CardinalityType.ONE_TO_MANY);
		r1.setEndB(warehouse, CardinalityType.ONE_TO_ONE);

		/**
		 * OffDutyState
		 * 		startStocking()
		 * StockingState
		 * 		startShipping()
		 * ShippingState
		 * 		goGeneral()
		 * GeneralState
		 */
		
		EntityState offDutyState = new EntityState("OffDutyState");
		EntityState stockingState = new EntityState("StockingState");
		EntityState shippingState = new EntityState("ShippingState");
		EntityState generalState = new EntityState("GeneralState");
		warehouseClerk.addState(offDutyState);
		warehouseClerk.addState(stockingState);
		warehouseClerk.addState(shippingState);
		warehouseClerk.addState(generalState);
		
		shippingClerk.setInitial(null);
		stockClerk.setInitial(null);
		offDutyClerk.setInitial(null);
		
		EntityEventSpecification startStockingSpec = new EntityEventSpecification(warehouseClerk, "startStocking");
		EntityEventSpecification startShippingSpec = new EntityEventSpecification(warehouseClerk, "startShipping");
		EntityEventSpecification goGeneralSpec = new EntityEventSpecification(warehouseClerk, "goGeneral");

		EntityEventInstance startStockingInstance = new EntityEventInstance(startStockingSpec, offDutyState, stockingState);
		EntityEventInstance startShippingInstance = new EntityEventInstance(startShippingSpec, stockingState, shippingState);
		EntityEventInstance goGeneralInstance = new EntityEventInstance(goGeneralSpec, shippingState, generalState);
		
		warehouseClerk.addEventInstance(startShippingSpec, startStockingInstance);
		warehouseClerk.addEventInstance(startShippingSpec, startShippingInstance);
		warehouseClerk.addEventInstance(goGeneralSpec, goGeneralInstance);
		
		return domain;
	}
	
	public void testWarehouseDomain() throws NameNotFoundException, NameAlreadyBoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		Assert.assertTrue(warehouseDomain.hasEntityClassWithName("Warehouse"));
		Assert.assertTrue(warehouseDomain.hasEntityClassWithName("WarehouseClerk"));
		Assert.assertTrue(warehouseDomain.hasEntityClassWithName("ShippingClerk"));
		Assert.assertTrue(warehouseDomain.hasEntityClassWithName("StockClerk"));
		Assert.assertTrue(warehouseDomain.hasEntityClassWithName("OffDutyClerk"));
		
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		EntityClass shippingClerk = warehouseDomain.getEntityClassWithName("ShippingClerk");
		EntityClass stockClerk = warehouseDomain.getEntityClassWithName("StockClerk");
		EntityClass offDutyClerk = warehouseDomain.getEntityClassWithName("OffDutyClerk");
		
		Assert.assertTrue(shippingClerk.isClassInParentPath(warehouseClerk));
		Assert.assertTrue(stockClerk.isClassInParentPath(warehouseClerk));
		Assert.assertTrue(offDutyClerk.isClassInParentPath(warehouseClerk));
		
		Assert.assertTrue(warehouseDomain.hasRelationWithName("R1"));
		
		Assert.assertTrue(warehouseClerk.hasAttribute("ClerkId"));
		EntityAttribute warehouseClerkId = warehouseClerk.getAttributeWithName("ClerkId");
		Assert.assertEquals(IntegerEntityDatatype.class ,warehouseClerkId.getType().getClass());
		
		Assert.assertTrue(warehouseClerk.hasAttribute("ClerkName"));
		EntityAttribute warehouseClerkName = warehouseClerk.getAttributeWithName("ClerkName");
		Assert.assertEquals(StringEntityDatatype.class ,warehouseClerkName.getType().getClass());
		
		Assert.assertTrue(warehouseClerk.hasAttribute("GoOffDutyAtEndOfJob"));
		EntityAttribute warehouseGoOffDutyAtEndOfJob = warehouseClerk.getAttributeWithName("GoOffDutyAtEndOfJob");
		Assert.assertEquals(BooleanEntityDatatype.class ,warehouseGoOffDutyAtEndOfJob.getType().getClass());
		
		Assert.assertTrue(shippingClerk.hasAttribute("ClerkId"));
		EntityAttribute shippingClerkClerkId = shippingClerk.getAttributeWithName("ClerkId");
		Assert.assertEquals(IntegerEntityDatatype.class ,shippingClerkClerkId.getType().getClass());
		
		Assert.assertTrue(shippingClerk.hasAttribute("ClerkName"));
		EntityAttribute shippingClerkClerkName = shippingClerk.getAttributeWithName("ClerkName");
		Assert.assertEquals(StringEntityDatatype.class ,shippingClerkClerkName.getType().getClass());
		
		Assert.assertTrue(shippingClerk.hasAttribute("GoOffDutyAtEndOfJob"));
		EntityAttribute shippingClerkGoOffDutyAtEndOfJob = shippingClerk.getAttributeWithName("GoOffDutyAtEndOfJob");
		Assert.assertEquals(BooleanEntityDatatype.class ,shippingClerkGoOffDutyAtEndOfJob.getType().getClass());
		
		Assert.assertTrue(stockClerk.hasAttribute("ClerkId"));
		EntityAttribute stockClerkClerkId = stockClerk.getAttributeWithName("ClerkId");
		Assert.assertEquals(IntegerEntityDatatype.class ,stockClerkClerkId.getType().getClass());
		
		Assert.assertTrue(stockClerk.hasAttribute("ClerkName"));
		EntityAttribute stockClerkClerkName = stockClerk.getAttributeWithName("ClerkName");
		Assert.assertEquals(StringEntityDatatype.class ,stockClerkClerkName.getType().getClass());
		
		Assert.assertTrue(stockClerk.hasAttribute("GoOffDutyAtEndOfJob"));
		EntityAttribute stockClerkGoOffDutyAtEndOfJob = stockClerk.getAttributeWithName("GoOffDutyAtEndOfJob");
		Assert.assertEquals(BooleanEntityDatatype.class ,stockClerkGoOffDutyAtEndOfJob.getType().getClass());
		
		Assert.assertTrue(offDutyClerk.hasAttribute("ClerkId"));
		EntityAttribute offDutyClerkClerkId = offDutyClerk.getAttributeWithName("ClerkId");
		Assert.assertEquals(IntegerEntityDatatype.class ,offDutyClerkClerkId.getType().getClass());
		
		Assert.assertTrue(offDutyClerk.hasAttribute("ClerkName"));
		EntityAttribute offDutyClerkClerkName = offDutyClerk.getAttributeWithName("ClerkName");
		Assert.assertEquals(StringEntityDatatype.class ,offDutyClerkClerkName.getType().getClass());
		
		Assert.assertTrue(offDutyClerk.hasAttribute("GoOffDutyAtEndOfJob"));
		EntityAttribute offDutyClerkGoOffDutyAtEndOfJob = offDutyClerk.getAttributeWithName("GoOffDutyAtEndOfJob");
		Assert.assertEquals(BooleanEntityDatatype.class ,offDutyClerkGoOffDutyAtEndOfJob.getType().getClass());

		
		Assert.assertTrue(shippingClerk.hasAttribute("AwaitingAssignment"));
		EntityAttribute shippingClerkAwaitingAssignment = shippingClerk.getAttributeWithName("AwaitingAssignment");
		Assert.assertEquals(BooleanEntityDatatype.class ,shippingClerkAwaitingAssignment.getType().getClass());
		
		Assert.assertTrue(stockClerk.hasAttribute("Idle"));
		EntityAttribute stockClerkIdle = stockClerk.getAttributeWithName("Idle");
		Assert.assertEquals(BooleanEntityDatatype.class ,stockClerkIdle.getType().getClass());
	
		
		// states
		Assert.assertTrue(warehouseClerk.hasStateWithName("OffDutyState"));
		Assert.assertTrue(warehouseClerk.hasStateWithName("StockingState"));
		Assert.assertTrue(warehouseClerk.hasStateWithName("ShippingState"));
		Assert.assertTrue(warehouseClerk.hasStateWithName("GeneralState"));
		
		Assert.assertTrue(shippingClerk.hasStateWithName("OffDutyState"));
		Assert.assertTrue(shippingClerk.hasStateWithName("StockingState"));
		Assert.assertTrue(shippingClerk.hasStateWithName("ShippingState"));
		Assert.assertTrue(shippingClerk.hasStateWithName("GeneralState"));
		
		Assert.assertTrue(stockClerk.hasStateWithName("OffDutyState"));
		Assert.assertTrue(stockClerk.hasStateWithName("StockingState"));
		Assert.assertTrue(stockClerk.hasStateWithName("ShippingState"));
		Assert.assertTrue(stockClerk.hasStateWithName("GeneralState"));
		
		Assert.assertTrue(offDutyClerk.hasStateWithName("OffDutyState"));
		Assert.assertTrue(offDutyClerk.hasStateWithName("StockingState"));
		Assert.assertTrue(offDutyClerk.hasStateWithName("ShippingState"));
		Assert.assertTrue(offDutyClerk.hasStateWithName("GeneralState"));
		
		//event spec
		Assert.assertTrue(warehouseClerk.hasEventSpecificationWithName("startStocking"));
		Assert.assertTrue(warehouseClerk.hasEventSpecificationWithName("startShipping"));
		Assert.assertTrue(warehouseClerk.hasEventSpecificationWithName("goGeneral"));
		
		Assert.assertTrue(shippingClerk.hasEventSpecificationWithName("startStocking"));
		Assert.assertTrue(shippingClerk.hasEventSpecificationWithName("startShipping"));
		Assert.assertTrue(shippingClerk.hasEventSpecificationWithName("goGeneral"));
		
		Assert.assertTrue(stockClerk.hasEventSpecificationWithName("startStocking"));
		Assert.assertTrue(stockClerk.hasEventSpecificationWithName("startShipping"));
		Assert.assertTrue(stockClerk.hasEventSpecificationWithName("goGeneral"));
		
		Assert.assertTrue(offDutyClerk.hasEventSpecificationWithName("startStocking"));
		Assert.assertTrue(offDutyClerk.hasEventSpecificationWithName("startShipping"));
		Assert.assertTrue(offDutyClerk.hasEventSpecificationWithName("goGeneral"));

		
		//event instances
		Assert.assertTrue(warehouseClerk.hasEventInstance("startStocking", "OffDutyState", "StockingState"));
		Assert.assertTrue(warehouseClerk.hasEventInstance("startShipping", "StockingState", "ShippingState"));
		Assert.assertTrue(warehouseClerk.hasEventInstance("goGeneral", "ShippingState", "GeneralState"));
		
		Assert.assertTrue(shippingClerk.hasEventInstance("startStocking", "OffDutyState", "StockingState"));
		Assert.assertTrue(shippingClerk.hasEventInstance("startShipping", "StockingState", "ShippingState"));
		Assert.assertTrue(shippingClerk.hasEventInstance("goGeneral", "ShippingState", "GeneralState"));
		
		Assert.assertTrue(stockClerk.hasEventInstance("startStocking", "OffDutyState", "StockingState"));
		Assert.assertTrue(stockClerk.hasEventInstance("startShipping", "StockingState", "ShippingState"));
		Assert.assertTrue(stockClerk.hasEventInstance("goGeneral", "ShippingState", "GeneralState"));
		
		Assert.assertTrue(offDutyClerk.hasEventInstance("startStocking", "OffDutyState", "StockingState"));
		Assert.assertTrue(offDutyClerk.hasEventInstance("startShipping", "StockingState", "ShippingState"));
		Assert.assertTrue(offDutyClerk.hasEventInstance("goGeneral", "ShippingState", "GeneralState"));
		
	}

	public static EntityDomain getWarehouseDomainWithActionLanguage() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException {
		EntityDomain warehouseDomain = getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
				
		EntityState stockingState = warehouseClerk.getStateWithName("StockingState");
		String stockingProcString = "";
		stockingProcString += "RECLASSIFY TO StockClerk;\n";
		stockingProcString += "self.Idle = true;\n";
		stockingProcString += "self.ClerkId = 37;\n";
		new EntityProcedure(stockingState).setProcedure(stockingProcString);
		
		EntityState shippingState = warehouseClerk.getStateWithName("ShippingState");
		String shippingProcString = "";
		shippingProcString += "RECLASSIFY TO ShippingClerk;\n";
		shippingProcString += "self.AwaitingAssignment = true;\n";
		shippingProcString += "self.ClerkId = 37;\n";
		new EntityProcedure(shippingState).setProcedure(shippingProcString);
		
		EntityState generalState = warehouseClerk.getStateWithName("GeneralState");
		String generalProcString = "";
		generalProcString += "RECLASSIFY TO WarehouseClerk;\n";
		generalProcString += "self.ClerkId = 37;\n";
		new EntityProcedure(generalState).setProcedure(generalProcString);
		
		return warehouseDomain;
	}
	
	
}
