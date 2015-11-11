package test.java.tests;

import test.java.helper.DomainShoppingCart;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityEventReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EventUsageTests extends TestCase {

	public EventUsageTests(String name) {
		super(name);
	}
	
	public void test_can_identify_when_a_class_is_used_in_generate_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityEventSpecification cancel = cart.getEventSpecificationWithName("cancel");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEventReferenced(startCart));
		ArrayList<EntityEventReference> referenceList = entityUsageReader.getEventReferences(startCart);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(1,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEventReferenced(cancel));
		
	}
	
	public void test_can_rename_when_a_class_is_used_in_generate_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEvent(startCart,"renamedStartEvent");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("startCart","renamedStartEvent"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEvent(startCart,"renamedStartEvent");
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
	public void test_can_identify_when_a_class_is_used_in_generate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=\"item1\", quantity=5) TO cart;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityEventSpecification cancel = cart.getEventSpecificationWithName("cancel");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEventReferenced(startCart));
		ArrayList<EntityEventReference> referenceList = entityUsageReader.getEventReferences(startCart);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=\"item1\", quantity=5) TO cart;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEventReferenced(cancel));
		
	}
	
	public void test_can_rename_when_a_class_is_used_in_generate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=\"item1\", quantity=5) TO cart;\n";
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEvent(startCart,"renamedStartEvent");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("startCart","renamedStartEvent"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEvent(startCart,"renamedStartEvent");
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
	public void test_can_identify_when_a_class_is_used_in_cancel_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=\"item1\", quantity=5) TO cart;\n";
		proc += "CANCEL startCart FROM cart TO cart;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityEventSpecification cancel = cart.getEventSpecificationWithName("cancel");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEventReferenced(startCart));
		ArrayList<EntityEventReference> referenceList = entityUsageReader.getEventReferences(startCart);
		Assert.assertEquals(2,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(1);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("CANCEL startCart FROM cart TO cart;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEventReferenced(cancel));
		
	}
	
	public void test_can_rename_when_a_class_is_used_in_cancel_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=\"item1\", quantity=5) TO cart;\n";
		proc += "CANCEL startCart FROM cart TO cart;\n";
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = cart.getEventSpecificationWithName("startCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEvent(startCart,"renamedStartEvent");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("startCart","renamedStartEvent"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEvent(startCart,"renamedStartEvent");
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}

}

