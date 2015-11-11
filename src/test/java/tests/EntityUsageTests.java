package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityClassReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityUsageTests extends TestCase {

	public EntityUsageTests(String name) {
		super(name);
	}
	
	public void test_can_identify_when_a_class_is_used_in_create_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityClassReferenced(car));
		ArrayList<EntityClassReference> referenceList = entityUsageReader.getEntityClassReferences(car);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(1,firstReference.getReferencedLineNumber());
		Assert.assertEquals("CREATE car FROM Car;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(busd.getEntityClassWithName("Bus")));
		
	}
	
	
	public void test_can_rename_a_class_used_in_create_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityClass(car,"HybridCar");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("Car","HybridCar"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityClass(car,"HybridCar");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	
	public void test_can_identify_when_a_class_is_used_in_select_instances_of_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "SELECT ANY car FROM INSTANCES OF Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityClassReferenced(car));
		ArrayList<EntityClassReference> referenceList = entityUsageReader.getEntityClassReferences(car);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(1,firstReference.getReferencedLineNumber());
		Assert.assertEquals("SELECT ANY car FROM INSTANCES OF Car;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(busd.getEntityClassWithName("Bus")));
		
	}
	
	
	public void test_can_rename_a_class_used_in_select_instances_of_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "SELECT ANY car FROM INSTANCES OF Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityClass(car,"HybridCar");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("Car","HybridCar"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityClass(car,"HybridCar");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	public void test_can_rename_a_class_is_used_in_generate_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityClass(cart,"HybridCart");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("ShoppingCart","HybridCart"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityClass(cart,"HybridCart");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	public void test_can_identify_when_a_class_is_used_in_generate_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityClassReferenced(cart));
		ArrayList<EntityClassReference> referenceList = entityUsageReader.getEntityClassReferences(cart);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(1,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=\"item1\", quantity=5) TO ShoppingCart CREATOR;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(shoppingCartDomain.getEntityClassWithName("Item")));
		
	}
	
	public void test_can_identify_when_a_class_is_used_in_reclassify_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "#self is Personal;\n";
		proc += "RECLASSIFY TO Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass personal = busd.getEntityClassWithName("Personal");
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		personal.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityClassReferenced(car));
		ArrayList<EntityClassReference> referenceList = entityUsageReader.getEntityClassReferences(car);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("RECLASSIFY TO Car;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(busd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_a_class_is_used_in_reclassify_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "#self is Personal\n";
		proc += "RECLASSIFY TO Car;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass personal = busd.getEntityClassWithName("Personal");
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityState state = new EntityState("Initial");
		personal.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityClass(car,"HybridCart");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("Car","HybridCart"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityClass(car,"HybridCart");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
}

