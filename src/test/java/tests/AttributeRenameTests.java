package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityAttributeReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class AttributeRenameTests extends TestCase {

	public AttributeRenameTests(String name) {
		super(name);
	}
	
	public void test_can_identify_when_an_attribute_is_used_in_temp_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "temp = user.Name + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(userName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(userName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("temp = user.Name + \"Hello\";",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_an_attribute_is_used_in_temp_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "temp = user.Name + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	public void test_can_identify_when_an_attribute_is_used_in_if_logic_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "IF user.Name == \"Hello\" THEN\n";
		proc += "END IF;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(userName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(userName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("IF user.Name == \"Hello\" THEN",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_an_attribute_is_used_in_if_logic_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "IF user.Name == \"Hello\" THEN\n";
		proc += "END IF;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	public void test_can_identify_when_an_attribute_is_used_in_select_instances_of_where_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "SELECT ANY car FROM INSTANCES OF Car WHERE selected.NumberDoors == 3;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberOfDoors = car.getAttributeWithName("NumberDoors");
		
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(numberOfDoors));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(numberOfDoors);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(1,firstReference.getReferencedLineNumber());
		Assert.assertEquals("SELECT ANY car FROM INSTANCES OF Car WHERE selected.NumberDoors == 3;",firstReference.getReferencedLineText());
	}
	
	
	public void test_can_rename_an_attribute_is_used_in_select_instances_of_where_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "SELECT ANY car FROM INSTANCES OF Car WHERE selected.NumberDoors == 3;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberOfDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(numberOfDoors,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".NumberDoors",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(numberOfDoors,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	public void test_can_identify_when_an_attribute_is_used_in_select_related_by_where_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\" WHERE selected.Complete == true;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass step = ttdd.getEntityClassWithName("Step");
		EntityAttribute complete = step.getAttributeWithName("Complete");
		EntityState state = new EntityState("Initial");
		step.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(complete));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(complete);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\" WHERE selected.Complete == true;",firstReference.getReferencedLineText());
	}
	
	
	public void test_can_rename_an_attribute_is_used_in_select_related_by_where_syntax() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\" WHERE selected.Complete == true;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass step = ttdd.getEntityClassWithName("Step");
		EntityAttribute complete = step.getAttributeWithName("Complete");
		EntityState state = new EntityState("Initial");
		step.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
	
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(complete,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".Complete",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(complete,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}

	public void test_can_identify_when_an_attribute_is_used_in_attribute_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "user.Name = user.Name + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(userName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(userName);
		Assert.assertEquals(2,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("user.Name = user.Name + \"Hello\";",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_an_attribute_is_used_in_attribute_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "user.Name = user.Name + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	public void test_can_identify_when_an_attribute_is_used_on_left_of_attribute_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "user.Name = user.Age + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(userName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(userName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("user.Name = user.Age + \"Hello\";",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_an_attribute_is_used_on_left_of_attribute_expression_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "user.Name = user.Age + \"Hello\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityAttribute userName = user.getAttributeWithName("Name");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(userName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}

	
	public void test_can_identify_when_an_attribute_is_used_as_event_param_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(itemName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(itemName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;",firstReference.getReferencedLineText());
		
		
	}
	
	public void test_can_rename_an_attribute_is_used_as_event_param_creator_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR;\n";
		
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(itemName,"RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(itemName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
	
	public void test_can_identify_when_an_attribute_is_used_as_event_param_creator_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(itemName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(itemName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;",firstReference.getReferencedLineText());
		
		
	}
	
	public void test_can_rename_an_attribute_is_used_as_event_param_creator_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO ShoppingCart CREATOR DELAY 100Seconds;\n";
		
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(itemName,"RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(itemName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
	public void test_can_identify_when_an_attribute_is_used_as_event_param_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(itemName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(itemName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO cart;",firstReference.getReferencedLineText());
		
		
	}
	
	public void test_can_rename_an_attribute_is_used_as_event_param_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart;\n";
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(itemName,"RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(itemName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
	public void test_can_identify_when_an_attribute_is_used_as_event_param_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;\n";

		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		Assert.assertTrue(procedure.validate());
		
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityAttributeReferenced(itemName));
		ArrayList<EntityAttributeReference> referenceList = entityUsageReader.getEntityAttributeReferences(itemName);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;",firstReference.getReferencedLineText());
		
		
	}
	
	public void test_can_rename_an_attribute_is_used_as_event_param_delay_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE item FROM Item;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=item.Name, quantity=5) TO cart DELAY 100Seconds;\n";
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		EntityAttribute itemName = item.getAttributeWithName("Name");
		
		EntityState state = new EntityState("Initial");
		item.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityAttribute(itemName,"RenamedName");
		Assert.assertTrue(!proc.equals(updatedProcdure));
		Assert.assertEquals(proc.replace(".Name",".RenamedName"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityAttribute(itemName,"RenamedName");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
		
	}
	
}

