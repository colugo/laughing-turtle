package test.java.tests;

import test.java.helper.DomainShoppingCart;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityEventParamReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EventParamUsageTests extends TestCase {

	public EventParamUsageTests(String name) {
		super(name);
	}

	public void test_can_identify_when_an_event_param_is_used_as_event_param_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityEventParamReferenced(itemName));
		ArrayList<EntityEventParamReference> referenceList = entityUsageReader.getEntityEventParamReferences(itemName);
		Assert.assertEquals(1, referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2, firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;", firstReference.getReferencedLineText());

	}

	public void test_can_rename_an_event_param_is_used_as_event_param_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityEventParam(itemName, "RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("itemName", "RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityEventParam(itemName, "RenamedName");

		Assert.assertEquals(updatedProcdure2, updatedProcdure);

	}

	public void test_can_identify_when_an_event_param_is_used_as_event_param_creator_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityEventParamReferenced(itemName));
		ArrayList<EntityEventParamReference> referenceList = entityUsageReader.getEntityEventParamReferences(itemName);
		Assert.assertEquals(1, referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2, firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;",
				firstReference.getReferencedLineText());

	}

	public void test_can_rename_an_event_param_is_used_as_event_param_creator_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityEventParam(itemName, "RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("itemName", "RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityEventParam(itemName, "RenamedName");

		Assert.assertEquals(updatedProcdure2, updatedProcdure);

	}

	public void test_can_identify_when_an_event_param_is_used_as_event_param_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityEventParamReferenced(itemName));
		ArrayList<EntityEventParamReference> referenceList = entityUsageReader.getEntityEventParamReferences(itemName);
		Assert.assertEquals(1, referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3, firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO cart;", firstReference.getReferencedLineText());

	}

	public void test_can_rename_an_event_param_is_used_as_event_param_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityEventParam(itemName, "RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("itemName", "RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityEventParam(itemName, "RenamedName");

		Assert.assertEquals(updatedProcdure2, updatedProcdure);

	}

	public void test_can_identify_when_an_event_param_is_used_as_event_param_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");

		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityEventParamReferenced(itemName));
		ArrayList<EntityEventParamReference> referenceList = entityUsageReader.getEntityEventParamReferences(itemName);
		Assert.assertEquals(1, referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3, firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;", firstReference.getReferencedLineText());

	}

	public void test_can_rename_an_event_param_is_used_as_event_param_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,
			NameNotFoundException {
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = item.getEventSpecificationWithName("startCart");
		EntityEventParam itemName = startCart.getParamWithName("itemName");
		
		EntityState state = item.getInitialState();
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());

		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityEventParam(itemName, "RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace("itemName", "RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityEventParam(itemName, "RenamedName");

		Assert.assertEquals(updatedProcdure2, updatedProcdure);

	}

}

