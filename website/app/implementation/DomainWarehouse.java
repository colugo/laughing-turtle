package implementation;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class DomainWarehouse{

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
		
		
		warehouse.x = 121.0;
		warehouse.y = 41.0;
		warehouseClerk.x = 56.0;
		warehouseClerk.y = 256.0;
		shippingClerk.x = 443.0;
		shippingClerk.y = 390.0;
		stockClerk.x = 443.0;
		stockClerk.y = 279.0;
		offDutyClerk.x = 443.0;
		offDutyClerk.y = 195.0;
		

		
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
}
