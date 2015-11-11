package test.java.tests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.TestHelper;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotGeneratedFromClassValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class CancelEventTests extends TestCase {

	public CancelEventTests(String name) {
		super(name);
	}
	
	public void test_cant_cancel_event_that_cant_be_identified() throws InvalidActionLanguageSyntaxException, NameNotFoundException{
		
		// write simple proc here for add stock to order
		// test cancel in init proc
		
		String initialProc = "";
		initialProc += "CREATE cart FROM ShoppingCart;\n";
		initialProc += "CANCEL cantFindEvent FROM self TO cart;\n";
		
		String addStockToOrderProc = "";
		addStockToOrderProc += "CREATE cart FROM ShoppingCart;\n";
		addStockToOrderProc += "GENERATE addSelection(itemName=\"item1\", quantity=5) TO cart;";
		
		EntityDomain cartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = cartDomain.getEntityClassWithName("ShoppingCart");
		
		EntityState addStockToOrderState = shoppingCart.getStateWithName("Adding Selection To Order"); 
		EntityProcedure addStockToOrderProcedure = new EntityProcedure(addStockToOrderState);
		addStockToOrderProcedure.setProcedure(addStockToOrderProc);
		
		EntityState initialState = shoppingCart.getInitialState();
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		initialProcedure.setProcedure(initialProc);

		
		Assert.assertTrue(addStockToOrderProcedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(addStockToOrderProcedure));

		Assert.assertTrue(!initialProcedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(initialProcedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(initialProcedure, EventNotFoundInClassValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(initialProcedure, EventNotGeneratedFromClassValidationError.class));
	}
	
	public void test_cant_cancel_event_that_is_not_sent_from_sending_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		// write simple proc here for add stock to order
		// test cancel in init proc
		
		String initialProc = "";
		initialProc += "CREATE cart FROM ShoppingCart;\n";
		initialProc += "CANCEL addSelection FROM self TO cart;\n";
		
		String addStockToOrderProc = "";
		addStockToOrderProc += "CREATE cart FROM ShoppingCart;\n";
		
		EntityDomain cartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = cartDomain.getEntityClassWithName("ShoppingCart");
		
		EntityState addStockToOrderState = shoppingCart.getStateWithName("Adding Selection To Order"); 
		EntityProcedure addStockToOrderProcedure = new EntityProcedure(addStockToOrderState);
		addStockToOrderProcedure.setProcedure(addStockToOrderProc);
		
		EntityState initialState = shoppingCart.getInitialState();
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		initialProcedure.setProcedure(initialProc);

		
		Assert.assertTrue(addStockToOrderProcedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(addStockToOrderProcedure));

		Assert.assertTrue(!initialProcedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(initialProcedure));
		TestHelper.checkValidationResultsForAnErrorOfType(initialProcedure, EventNotGeneratedFromClassValidationError.class);
	}
	
	public void test_cancel_event() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		// write simple proc here for add stock to order
		// test cancel in init proc
		
		String initialProc = "";
		initialProc += "CREATE cart FROM ShoppingCart;\n";
		initialProc += "CANCEL addSelection FROM self TO cart;\n";
		
		String addStockToOrderProc = "";
		addStockToOrderProc += "CREATE cart FROM ShoppingCart;\n";
		addStockToOrderProc += "GENERATE addSelection(itemName=\"item1\", quantity=5) TO cart;";
		
		EntityDomain cartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = cartDomain.getEntityClassWithName("ShoppingCart");
		
		EntityState addStockToOrderState = shoppingCart.getStateWithName("Adding Selection To Order"); 
		EntityProcedure addStockToOrderProcedure = new EntityProcedure(addStockToOrderState);
		addStockToOrderProcedure.setProcedure(addStockToOrderProc);
		
		EntityState initialState = shoppingCart.getInitialState();
		EntityProcedure initialProcedure = new EntityProcedure(initialState);
		initialProcedure.setProcedure(initialProc);

		
		Assert.assertTrue(addStockToOrderProcedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(addStockToOrderProcedure));

		TestHelper.printValidationErrors(initialProcedure);
		Assert.assertTrue(initialProcedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(initialProcedure));
	}

}

