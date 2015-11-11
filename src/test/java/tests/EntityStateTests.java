package test.java.tests;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityStateTests extends TestCase {
	public EntityStateTests(String name)
	{
		super(name);
	}
	
	public void test_can_get_next_state_for_event_specification() throws NameNotFoundException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = domain.getEntityClassWithName("ShoppingCart");
		EntityState addingSelection = shoppingCart.getStateWithName("Adding Selection To Order");
		EntityState cancelling = shoppingCart.getStateWithName("Cancelling Entire Order");
		
		EntityEventSpecification scAddSelection = shoppingCart.getEventSpecificationWithName("addSelection");
		EntityEventSpecification scCancel = shoppingCart.getEventSpecificationWithName("cancel");
		EntityEventSpecification scStartCart = shoppingCart.getEventSpecificationWithName("startCart");
		
		Assert.assertEquals(addingSelection, addingSelection.getNextStateForEventSpecification(scAddSelection));
		Assert.assertEquals(cancelling, addingSelection.getNextStateForEventSpecification(scCancel));
		Assert.assertEquals(null, addingSelection.getNextStateForEventSpecification(scStartCart));
		
	}
}

