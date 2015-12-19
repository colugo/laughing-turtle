package implementation;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

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

public class DomainShoppingCart {

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
		
		ShoppingCart.x = 93.0;
		ShoppingCart.y = 90.0;
		Item.x = 538.0;
		Item.y = 180.0;
		ItemSelection.x = 409.0;
		ItemSelection.y = 136.0;
		
		// attributes
		try
		{
			ItemSelection.addAttribute(new EntityAttribute("Quantity", IntegerEntityDatatype.getInstance()));
			Item.addAttribute(new EntityAttribute("Name", StringEntityDatatype.getInstance()));
		}catch(Exception e)
		{}
		
		// relations
		EntityRelation R1 = new EntityRelation("R1");
		R1.setEndA(ShoppingCart, CardinalityType.ONE_TO_MANY, "Contains for purchase");
		R1.setEndB(Item, CardinalityType.ZERO_TO_MANY, "Will be bought with others in");
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
}
