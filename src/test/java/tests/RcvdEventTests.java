package test.java.tests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.TestHelper;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeCompared;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventParamNotFoundInEventSpecificationValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class RcvdEventTests extends TestCase {

	public RcvdEventTests(String name) {
		super(name);
	}
	
	public void test_state_can_determine_if_it_has_a_rcvd_event() throws NameNotFoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState scNewOrder = ShoppingCart.getStateWithName("New Order");
		Assert.assertTrue(scNewOrder.hasRcvdEvent());
	}
	
	public void test_state_can_determine_if_it_doesnt_have_a_rcvd_event() throws NameNotFoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState scInit = ShoppingCart.getStateWithName("Initial");
		Assert.assertTrue(!scInit.hasRcvdEvent());
	}

	public void test_state_can_get_event_specification_of_rcvd_event_if_it_has_one() throws NameNotFoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState scNewOrder = ShoppingCart.getStateWithName("New Order");
		EntityEventSpecification scStartCart = ShoppingCart.getEventSpecificationWithName("startCart");
		Assert.assertEquals(scStartCart, scNewOrder.getRcvdEvent());
	}
	
	public void test_state_cant_get_event_specification_of_rcvd_event_if_it_doesnt_have_one() throws NameNotFoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState scInit = ShoppingCart.getStateWithName("Initial");
		try {
			scInit.getRcvdEvent();
			fail("Should not get here");
		} catch (NameNotFoundException e) {
		}
	}
	
	public void test_action_language_validator_can_identify_if_rcvd_event_has_paramater() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "itemName = rcvd_event.itemName;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_action_language_validator_can_identify_if_rcvd_event_doesnt_have_paramater() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "itemName = rcvd_event.otherParam;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventSpecificationValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
	}
	
	
	public void test_can_set_attribute_to_rcvd_event_param() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "SELECT ANY specificItem FROM INSTANCES OF Item WHERE selected.Name == rcvd_event.itemName;\n";
		proc += "RELATE specificItem TO self ACROSS R1 CREATING itemSelection;\n";
		proc += "itemSelection.Quantity = rcvd_event.quantity;\n";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_cant_test_rcvd_event_param_in_select_with_wrong_datatype() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "SELECT ANY specificItem FROM INSTANCES OF Item WHERE selected.Name == rcvd_event.quantity;\n";
		proc += "RELATE specificItem TO self ACROSS R1 CREATING itemSelection;\n";
		proc += "itemSelection.Quantity = rcvd_event.quantity;\n";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeCompared.class));
	}
	
	public void test_cant_set_rcvd_event_param_to_attribute_with_wrong_datatype() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "SELECT ANY specificItem FROM INSTANCES OF Item WHERE selected.Name == rcvd_event.quantity;\n";
		proc += "RELATE specificItem TO self ACROSS R1 CREATING itemSelection;\n";
		proc += "itemSelection.Quantity = rcvd_event.name;\n";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
}

