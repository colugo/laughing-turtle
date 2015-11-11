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
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class DomainShoppingCart extends TestCase {

	public DomainShoppingCart(String name) {
		super(name);
	}

	/**
	*            +------------+
	*            |ShoppingCart|
	*            +-----+------+
	*                  |
	*                  |
	*                  |             +----------------+
	*               R1 |             |ItemSelection   |
	*                  |.............+----------------+
	*                  |             |Quantity:Integer|
	*                  |             +----------------+
	*                  |
	*                  |
	*            +-----+-----+
	*            |Item       |
	*            +-----------+
	*            |Name:String|
	*            +-----------+
	*            
	*
	*
	*
	*	State machine of ShoppingCart
	*		     ,-.
	*	        (int)
	*	         `-'
	*	          |
	*	          |
	*	    startCart(itemName:String,quantity:Integer)
	*	          |
	*	      /---V-----\
	*	      |New Order|-----------------------------------------------------------------
	*	      \---+-----/                                                                |
	*	          |                                                                      |
	*	   addSelection(itemName:String,quantity:Integer)                                |
	*	          |                                                                      |
	*	          |               _________                                              |
	*	          |              |      addSelection(itemName:String,quantity:Integer)   |
	*	          |              |         |                                             |
	*	     /----V--------------+-----<---+       /-----------------------\             |
	*	     |Adding Selection To Order|---------->|Cancelling Entire Order|<------------|
	*	     \-------------------------/           \-----------------------/
	*	                              cancel()
	 * @throws NameAlreadyBoundException 
	*/
	
	@SuppressWarnings("unused")
	public static EntityDomain getShoppingCartDomain()
	{
		EntityDomain domain = new EntityDomain("Shopping Cart");
		
		// classes
		EntityClass ShoppingCart = new EntityClass("ShoppingCart");
		domain.addClass(ShoppingCart);
		
		EntityClass Item = new EntityClass("Item");
		domain.addClass(Item);
		
		EntityClass ItemSelection = new EntityClass("ItemSelection");
		domain.addClass(ItemSelection);
		
		// attributes
		try
		{
			ItemSelection.addAttribute(new EntityAttribute("Quantity", IntegerEntityDatatype.getInstance()));
			Item.addAttribute(new EntityAttribute("Name", StringEntityDatatype.getInstance()));
		}catch(Exception e)
		{}
		
		// relations
		EntityRelation R1 = new EntityRelation("R1");
		R1.setEndA(ShoppingCart, CardinalityType.ZERO_TO_MANY);
		R1.setEndB(Item, CardinalityType.ZERO_TO_MANY);
		R1.setAssociation(ItemSelection);
		
		
		// states
		EntityState scInit = new EntityState( "Initial");
		EntityState scNewOrder = new EntityState( "New Order");
		EntityState scAddingSelectionToOrder = new EntityState( "Adding Selection To Order");
		EntityState scCancellingEntireOrder = new EntityState( "Cancelling Entire Order");
		ShoppingCart.addState(scInit);
		ShoppingCart.addState(scNewOrder);
		ShoppingCart.addState(scAddingSelectionToOrder);
		ShoppingCart.addState(scCancellingEntireOrder);
		
		
		// events
		EntityEventSpecification scStartCart = new EntityEventSpecification(ShoppingCart,"startCart");
		EntityEventInstance scStartCartInstance = new EntityEventInstance(scStartCart,scInit,scNewOrder);
		scStartCart.addEventParam(new EntityEventParam("itemName",StringEntityDatatype.getInstance()));
		scStartCart.addEventParam(new EntityEventParam("quantity",IntegerEntityDatatype.getInstance()));
		
		EntityEventSpecification scAddSelection = new EntityEventSpecification(ShoppingCart,"addSelection");
		EntityEventInstance scAddSelectionInstance = new EntityEventInstance(scAddSelection,scNewOrder,scAddingSelectionToOrder);
		EntityEventInstance scAddSelection1Instance = new EntityEventInstance(scAddSelection,scAddingSelectionToOrder,scAddingSelectionToOrder);
		scAddSelection.addEventParam(new EntityEventParam("itemName",StringEntityDatatype.getInstance()));
		scAddSelection.addEventParam(new EntityEventParam("quantity",IntegerEntityDatatype.getInstance()));
		
		EntityEventSpecification scCancel = new EntityEventSpecification(ShoppingCart,"cancel");
		EntityEventInstance scCancelInstance = new EntityEventInstance(scCancel,scAddingSelectionToOrder,scCancellingEntireOrder);
		EntityEventInstance scCancelNewInstance = new EntityEventInstance(scCancel,scNewOrder,scCancellingEntireOrder);
		
		
		return domain;
	}
	
	
	public static EntityDomain getShoppingCartDomainWithActionLanguage() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = getShoppingCartDomain();
		
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		
		EntityState scNewOrder = shoppingCart.getStateWithName("New Order");
		String newOrderProcText = "";
		newOrderProcText += "SELECT ANY newItem FROM INSTANCES OF Item WHERE selected.Name == rcvd_event.itemName;\n";
		newOrderProcText += "RELATE newItem TO self ACROSS R1 CREATING newItemSelection;\n";
		newOrderProcText += "newItemSelection.Quantity = rcvd_event.quantity;\n";
		EntityProcedure newOrderProc = new EntityProcedure(scNewOrder);
		newOrderProc.setProcedure(newOrderProcText);
		
		
		EntityState scAddingSelection = shoppingCart.getStateWithName("Adding Selection To Order");
		String addingSelectionText = "";
		addingSelectionText += "SELECT ANY newItem FROM INSTANCES OF Item WHERE selected.Name == rcvd_event.itemName;\n";
		addingSelectionText += "SELECT ONE itemSelectionExists THAT RELATES newItem TO self ACROSS R1;\n";
		addingSelectionText += "IF EMPTY itemSelectionExists THEN\n";
		addingSelectionText += "	RELATE newItem TO self ACROSS R1 CREATING newItemSelection;\n";
		addingSelectionText += "	newItemSelection.Quantity = rcvd_event.quantity;\n";
		addingSelectionText += "ELSE\n";
		addingSelectionText += "	itemSelectionExists.Quantity = itemSelectionExists.Quantity + rcvd_event.quantity;\n";
		addingSelectionText += "END IF;\n";
		EntityProcedure addingSelectionProc = new EntityProcedure(scAddingSelection);
		addingSelectionProc.setProcedure(addingSelectionText);
		

		EntityState scCancellingOrder = shoppingCart.getStateWithName("Cancelling Entire Order");
		String cancellingOrderText = "";
		cancellingOrderText += "SELECT MANY itemsInCart RELATED BY self->R1;\n";
		cancellingOrderText += "FOR currentItem IN itemsInCart DO\n";
		cancellingOrderText += "	SELECT ONE currentItemSelection THAT RELATES currentItem TO self ACROSS R1;\n";
		cancellingOrderText += "	currentItemSelection.Quantity = currentItemSelection.Quantity * 0;\n";
		cancellingOrderText += "	UNRELATE currentItem FROM self ACROSS R1;\n";
		cancellingOrderText += "END FOR;\n";
		EntityProcedure cancellingOrderProc = new EntityProcedure(scCancellingOrder);
		cancellingOrderProc.setProcedure(cancellingOrderText);
		
		return shoppingCartDomain;
	}
	
	public void testShoppingCartDomainSetup() throws NameNotFoundException
	{
		EntityDomain domain = getShoppingCartDomain();
		
		// classes
		Assert.assertTrue(domain.hasEntityClassWithName("ShoppingCart"));
		EntityClass ShoppingCart = domain.getEntityClassWithName("ShoppingCart");
		
		Assert.assertTrue(domain.hasEntityClassWithName("Item"));
		EntityClass Item = domain.getEntityClassWithName("Item");
		
		Assert.assertTrue(domain.hasEntityClassWithName("ItemSelection"));
		EntityClass ItemSelection = domain.getEntityClassWithName("ItemSelection");
		
		
		// attributes
		Assert.assertTrue(Item.hasAttribute("Name"));
		EntityAttribute Item_Name = Item.getAttributeWithName("Name");
		Assert.assertEquals(StringEntityDatatype.class, Item_Name.getType().getClass());
		
		Assert.assertTrue(ItemSelection.hasAttribute("Quantity"));
		EntityAttribute ItemSelection_Quantity = ItemSelection.getAttributeWithName("Quantity");
		Assert.assertEquals(IntegerEntityDatatype.class, ItemSelection_Quantity.getType().getClass());
	
		
		// relations
		Assert.assertTrue(domain.hasRelationWithName("R1"));
		EntityRelation R1 = domain.getRelationWithName("R1");
		Assert.assertTrue(R1.hasAssociation());
		Assert.assertEquals(ItemSelection, R1.getAssociation());
		Assert.assertTrue(R1.hasClass(ShoppingCart));
		Assert.assertTrue(R1.hasClass(Item));
		
		
		// states
		Assert.assertTrue(ShoppingCart.hasStateWithName("Initial"));
		EntityState scInitial = ShoppingCart.getStateWithName("Initial");
		
		Assert.assertTrue(ShoppingCart.hasStateWithName("New Order"));
		EntityState scNewOrder = ShoppingCart.getStateWithName("New Order");

		Assert.assertTrue(ShoppingCart.hasStateWithName("Adding Selection To Order"));
		EntityState scAddingSelectionToNewOrder = ShoppingCart.getStateWithName("Adding Selection To Order");

		Assert.assertTrue(ShoppingCart.hasStateWithName("Cancelling Entire Order"));
		EntityState scCancellingEntireOrder = ShoppingCart.getStateWithName("Cancelling Entire Order");
		
		// events
		//--------
		Assert.assertTrue(ShoppingCart.hasEventSpecificationWithName("startCart"));
		EntityEventSpecification scStartCart = ShoppingCart.getEventSpecificationWithName("startCart");
		Assert.assertTrue(ShoppingCart.hasEventInstance("startCart", "Initial", "New Order"));
		EntityEventInstance scStartCartInstance = ShoppingCart.getEventInstance("startCart", "Initial", "New Order");
		Assert.assertEquals(scInitial, scStartCartInstance.getFromState());
		Assert.assertEquals(scNewOrder, scStartCartInstance.getToState());
		
		Assert.assertTrue(scStartCart.hasParamWithName("itemName"));
		EntityEventParam scStartCart_ItemName = scStartCart.getParamWithName("itemName");
		Assert.assertEquals(StringEntityDatatype.class, scStartCart_ItemName.getType().getClass());
		
		Assert.assertTrue(scStartCart.hasParamWithName("quantity"));
		EntityEventParam scStartCart_Quantity = scStartCart.getParamWithName("quantity");
		Assert.assertEquals(IntegerEntityDatatype.class, scStartCart_Quantity.getType().getClass());
		
		//--------
		Assert.assertTrue(ShoppingCart.hasEventSpecificationWithName("addSelection"));
		EntityEventSpecification scAddSelection = ShoppingCart.getEventSpecificationWithName("addSelection");

		Assert.assertTrue(ShoppingCart.hasEventInstance("addSelection", "New Order","Adding Selection To Order"));
		EntityEventInstance scAddSelectionInstance = ShoppingCart.getEventInstance("addSelection", "New Order","Adding Selection To Order");
		Assert.assertEquals(scNewOrder, scAddSelectionInstance.getFromState());
		Assert.assertEquals(scAddingSelectionToNewOrder, scAddSelectionInstance.getToState());
		
		Assert.assertTrue(scAddSelection.hasParamWithName("itemName"));
		EntityEventParam scAddSelection_ItemName = scAddSelection.getParamWithName("itemName");
		Assert.assertEquals(StringEntityDatatype.class, scAddSelection_ItemName.getType().getClass());
		
		Assert.assertTrue(scAddSelection.hasParamWithName("quantity"));
		EntityEventParam scAddSelection_Quantity = scAddSelection.getParamWithName("quantity");
		Assert.assertEquals(IntegerEntityDatatype.class, scAddSelection_Quantity.getType().getClass());
		
		//--------
		Assert.assertTrue(ShoppingCart.hasEventSpecificationWithName("addSelection"));
		EntityEventSpecification scAddSelection1 = ShoppingCart.getEventSpecificationWithName("addSelection");
		Assert.assertTrue(ShoppingCart.hasEventInstance("addSelection","Adding Selection To Order" ,"Adding Selection To Order"));
		EntityEventInstance scAddSelection1Instance = ShoppingCart.getEventInstance("addSelection","Adding Selection To Order" ,"Adding Selection To Order");
		Assert.assertEquals(scAddingSelectionToNewOrder, scAddSelection1Instance.getFromState());
		Assert.assertEquals(scAddingSelectionToNewOrder, scAddSelection1Instance.getToState());
		
		Assert.assertTrue(scAddSelection1.hasParamWithName("itemName"));
		EntityEventParam scAddSelection1_ItemName = scAddSelection1.getParamWithName("itemName");
		Assert.assertEquals(StringEntityDatatype.class, scAddSelection1_ItemName.getType().getClass());
		
		Assert.assertTrue(scAddSelection1.hasParamWithName("quantity"));
		EntityEventParam scAddSelection1_Quantity = scAddSelection1.getParamWithName("quantity");
		Assert.assertEquals(IntegerEntityDatatype.class, scAddSelection1_Quantity.getType().getClass());
		
		//---------
		Assert.assertTrue(ShoppingCart.hasEventInstance("cancel","Adding Selection To Order" ,"Cancelling Entire Order"));
		EntityEventInstance scCancelInstance = ShoppingCart.getEventInstance("cancel","Adding Selection To Order" ,"Cancelling Entire Order");
		Assert.assertEquals(scAddingSelectionToNewOrder, scCancelInstance.getFromState());
		Assert.assertEquals(scCancellingEntireOrder, scCancelInstance.getToState());
	}

}
