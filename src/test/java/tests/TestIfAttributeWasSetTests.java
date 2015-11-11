package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public class TestIfAttributeWasSetTests extends TestCase {

	public TestIfAttributeWasSetTests(String name) {
		super(name);
	}
	
	public void test_empty_action_language_does_not_set_attribute() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureSetValueToAttribute(car,numberDoors));
	}
	
	public void test_setter_action_language_does_set_attribute() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE car FROM Car;\n";
		proc += "car.NumberDoors = 1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureSetValueToAttribute(car,numberDoors));
	}
	
	public void test_empty_action_language_does_not_read_attribute() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadAttributeValue(car,numberDoors));
	}
	
	public void test_non_reading_attribute_action_language_does_not_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";
		proc += "car.NumberDoors = 1;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadAttributeValue(car,numberDoors));
	}
	
	public void test_simple_reading_action_language_does_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";
		proc += "temp = car.NumberDoors;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadAttributeValue(car,numberDoors));
	}
	
	public void test_if_logic_does_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";
		proc += "IF car.NumberDoors > 3 THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadAttributeValue(car,numberDoors));
	}
	
	public void test_if_logic_does_not_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE car FROM Car;\n";
		proc += "IF car.License == \"3\" THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass car = busd.getEntityClassWithName("Car");
		EntityAttribute numberDoors = car.getAttributeWithName("NumberDoors");
		EntityState state = new EntityState("Initial");
		car.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadAttributeValue(car,numberDoors));
	}
	
	public void test_select_related_by_where_attribute_does_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE bus FROM Personal;\n";
		proc += "SELECT MANY selectedTyre RELATED BY bus->R1 WHERE selected.DaysWear > 2;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass tyre = busd.getEntityClassWithName("Tyre");
		EntityAttribute daysWear = tyre.getAttributeWithName("DaysWear");
		EntityState state = new EntityState("Initial");
		tyre.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadAttributeValue(tyre,daysWear));
	}
	
	public void test_select_related_by_where_attribute_does_not_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE bus FROM Personal;\n";
		proc += "SELECT MANY selectedTyre RELATED BY bus->R1 WHERE newAttribute > 2;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass tyre = busd.getEntityClassWithName("Tyre");

		EntityAttribute newAttribute = new EntityAttribute("newAttribute",IntegerEntityDatatype.getInstance());
		tyre.addAttribute(newAttribute);
		
		EntityAttribute daysWear = tyre.getAttributeWithName("DaysWear");
		EntityState state = new EntityState("Initial");
		tyre.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadAttributeValue(tyre,daysWear));
	}
	
	public void test_select_from_instances_where_attribute_does_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE bus FROM Personal;\n";
		proc += "SELECT ANY selectedTyre FROM INSTANCES OF Tyre WHERE selected.DaysWear > 2;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass tyre = busd.getEntityClassWithName("Tyre");
		EntityAttribute daysWear = tyre.getAttributeWithName("DaysWear");
		EntityState state = new EntityState("Initial");
		tyre.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadAttributeValue(tyre,daysWear));
	}
	
	public void test_select_from_instances_where_attribute_does_not_read_attribute() throws NameNotFoundException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "CREATE bus FROM Personal;\n";
		proc += "SELECT ANY selectedTyre FROM INSTANCES OF Tyre WHERE selected.newAttribute > 2;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass tyre = busd.getEntityClassWithName("Tyre");
		
		EntityAttribute newAttribute = new EntityAttribute("newAttribute",IntegerEntityDatatype.getInstance());
		tyre.addAttribute(newAttribute);
		
		EntityAttribute daysWear = tyre.getAttributeWithName("DaysWear");
		EntityState state = new EntityState("Initial");
		tyre.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadAttributeValue(tyre,daysWear));
	}
	
	
	public void test_attribute_expression_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		proc += "item.Name = rcvd_event.itemName;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_attribute_expression_does_not_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_attribute_expression_does_not_registers_read_of_event_param_for_non_triggering_event() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("cancel");
		EntityEventParam param = null;// = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_attribute_expression_does_not_registers_read_of_mismatched_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = new EntityEventParam("someName", InvalidEntityDatatype.getInstance());
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	
	public void test_if_logic_expression_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		proc += "item.Name = \"fred\";\n";
		proc += "IF item.Name == rcvd_event.itemName THEN\n";
		proc += "END IF;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_if_logic_expression_does_not_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		proc += "item.Name = \"fred\";\n";
		proc += "IF item.Name == \"fred\" THEN\n";
		proc += "END IF;\n";
		
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_temp_expression_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "temp = rcvd_event.itemName;\n";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	public void test_temp_expression_does_not_registers_read_of_event_param() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "temp = 3;\n";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getStateWithName("New Order");
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification foundEvent = cart.getEventSpecificationWithName("startCart");
		EntityEventParam param = foundEvent.getParamWithName("itemName");
		
		Assert.assertTrue(procedure.validate());
		Assert.assertTrue(!procedure.doesProcedureReadEventParam(foundEvent,param));
	}
	
	
}

