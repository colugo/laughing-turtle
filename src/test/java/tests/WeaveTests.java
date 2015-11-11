package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;

import java.util.Collection;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.weaveModelTests.DomainFactory;
import test.java.weaveModelTests.DomainStore;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.weaving.AttributeWeave;
import main.java.avii.editor.metamodel.weaving.ClassWeave;
import main.java.avii.editor.metamodel.weaving.DomainWeave;
import main.java.avii.editor.metamodel.weaving.EventWeave;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddClassToWeaveIfItsDomainIsntRegistered;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateDomainToDomainWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddSuperClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.ClassNotInWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.ClassesFromDomainAreUsedInClassWeavesException;
import main.java.avii.editor.metamodel.weaving.exception.DomainNotInWeaveException;

public class WeaveTests extends TestCase {

	public WeaveTests(String name) {
		super(name);
	}

	public void test_weave_has_specified_name() {
		String domainWeaveName = "testWeave1";
		DomainWeave domainWeave = new DomainWeave(domainWeaveName);
		Assert.assertEquals(domainWeaveName, domainWeave.getName());
	}

	public void test_weave_can_change_name() {
		String domainWeaveName = "testWeave1";
		DomainWeave domainWeave = new DomainWeave("notTestWeave1");
		Assert.assertTrue(!domainWeave.getName().equals(domainWeaveName));
		domainWeave.setName(domainWeaveName);
		Assert.assertEquals(domainWeaveName, domainWeave.getName());
	}

	public void test_can_register_domain_into_weave() throws CannotAddDuplicateDomainToDomainWeaveException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		domainWeave.registerDomain(busDomain);
	}

	public void test_can_determine_if_domain_is_registered_into_weave() throws CannotAddDuplicateDomainToDomainWeaveException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		Assert.assertTrue(!domainWeave.hasRegisteredDomain(busDomain));
		domainWeave.registerDomain(busDomain);
		Assert.assertTrue(domainWeave.hasRegisteredDomain(busDomain));
	}

	public void test_weave_can_list_woven_domains() throws CannotAddDuplicateDomainToDomainWeaveException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		Assert.assertTrue(!domainWeave.getWovenDomains().contains(busDomain));
		domainWeave.registerDomain(busDomain);
		Assert.assertTrue(domainWeave.getWovenDomains().contains(busDomain));
	}

	public void test_cant_register_already_registered_domain_into_weave() throws CannotAddDuplicateDomainToDomainWeaveException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		domainWeave.registerDomain(busDomain);
		try {
			domainWeave.registerDomain(busDomain);
			fail();
		} catch (CannotAddDuplicateDomainToDomainWeaveException ne) {
		}
	}

	public void test_weave_can_create_class_weave() {
		DomainWeave domainWeave = new DomainWeave("");
		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertEquals(domainWeave, classWeave.getDomainWeave());
	}

	public void test_created_class_weaves_have_incrementing_identifiers() {
		DomainWeave domainWeave = new DomainWeave("");
		ClassWeave classWeave1 = domainWeave.createClassWeave();
		ClassWeave classWeave2 = domainWeave.createClassWeave();
		Assert.assertEquals(1, classWeave1.getWeaveIdentifier());
		Assert.assertEquals(2, classWeave2.getWeaveIdentifier());
	}

	public void test_created_domain_weave_has_list_of_class_weaves() {
		DomainWeave domainWeave = new DomainWeave("");
		ClassWeave classWeave1 = domainWeave.createClassWeave();
		ClassWeave classWeave2 = domainWeave.createClassWeave();
		Assert.assertTrue(domainWeave.getClassWeaves().contains(classWeave1));
		Assert.assertTrue(domainWeave.getClassWeaves().contains(classWeave2));
	}

	public void test_can_add_class_to_class_weave() throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotAddDuplicateDomainToDomainWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);

	}

	public void test_cant_add_class_with_sub_classes_to_class_weave() throws CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotAddDuplicateDomainToDomainWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		// this is because you can't create a class with subtypes - a
		// fundamental of class weaving
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass bus = busDomain.getEntityClassWithName("Bus");

		ClassWeave classWeave = domainWeave.createClassWeave();
		try {
			classWeave.registerClass(bus);
			fail();
		} catch (CannotAddSuperClassToClassWeaveException e) {
		}
	}

	public void test_class_weave_can_identify_if_class_is_in_weave() throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotAddDuplicateDomainToDomainWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertTrue(!classWeave.isClassRegistered(car));
		classWeave.registerClass(car);
		Assert.assertTrue(classWeave.isClassRegistered(car));
	}

	public void test_class_weave_can_identify_all_woven_classes() throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotAddDuplicateDomainToDomainWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertTrue(!classWeave.isClassRegistered(car));
		Assert.assertTrue(!classWeave.getRegisteredClasses().contains(car));
		classWeave.registerClass(car);
		Assert.assertTrue(classWeave.getRegisteredClasses().contains(car));
	}

	public void test_cant_add_class_not_from_domain_in_owning_weave_to_class_weave() throws CannotAddSuperClassToClassWeaveException,
			CannotAddDuplicateClassToClassWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		ClassWeave classWeave = domainWeave.createClassWeave();

		EntityClass newClass = new EntityClass("");
		try {
			classWeave.registerClass(newClass);
			fail();
		} catch (CannotAddClassToWeaveIfItsDomainIsntRegistered cactwiidir) {
		}
	}

	public void test_cant_add_class_multiple_times_to_class_weave() throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotAddDuplicateDomainToDomainWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertTrue(!classWeave.isClassRegistered(car));
		classWeave.registerClass(car);
		Assert.assertTrue(classWeave.isClassRegistered(car));
		try {
			classWeave.registerClass(car);
			fail();
		} catch (CannotAddDuplicateClassToClassWeaveException cadctcwe) {
		}
	}

	public void test_can_de_register_domain_into_weave() throws CannotAddDuplicateDomainToDomainWeaveException, DomainNotInWeaveException,
			ClassesFromDomainAreUsedInClassWeavesException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		domainWeave.registerDomain(busDomain);
		domainWeave.deregisterDomain(busDomain);
	}

	public void test_cant_de_register_domain_not_in_weave() throws CannotAddDuplicateDomainToDomainWeaveException, ClassesFromDomainAreUsedInClassWeavesException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		try {
			domainWeave.deregisterDomain(busDomain);
			fail();
		} catch (DomainNotInWeaveException dniwe) {
		}

	}

	public void test_can_determine_if_domain_is_de_registered_into_weave() throws CannotAddDuplicateDomainToDomainWeaveException, DomainNotInWeaveException,
			ClassesFromDomainAreUsedInClassWeavesException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		Assert.assertTrue(!domainWeave.hasRegisteredDomain(busDomain));
		domainWeave.registerDomain(busDomain);
		Assert.assertTrue(domainWeave.hasRegisteredDomain(busDomain));
		domainWeave.deregisterDomain(busDomain);
		Assert.assertTrue(!domainWeave.hasRegisteredDomain(busDomain));
	}

	public void test_weave_doesnt_list_de_registered_woven_domains() throws CannotAddDuplicateDomainToDomainWeaveException, DomainNotInWeaveException,
			ClassesFromDomainAreUsedInClassWeavesException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		DomainWeave domainWeave = new DomainWeave("busWeave");
		Assert.assertTrue(!domainWeave.getWovenDomains().contains(busDomain));
		domainWeave.registerDomain(busDomain);
		Assert.assertTrue(domainWeave.getWovenDomains().contains(busDomain));
		domainWeave.deregisterDomain(busDomain);
		Assert.assertTrue(!domainWeave.getWovenDomains().contains(busDomain));
	}

	public void test_cannot_remove_domain_from_weave_if_class_weaves_use_classes_from_victim_domain() throws CannotAddDuplicateDomainToDomainWeaveException,
			CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered,
			DomainNotInWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);
		try {
			domainWeave.deregisterDomain(busDomain);
			fail();
		} catch (ClassesFromDomainAreUsedInClassWeavesException e) {
		}
	}

	public void test_class_can_determine_if_it_is_in_weave() throws CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException,
			CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		Assert.assertTrue(!domainWeave.hasClassBeenWoven(car));

		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);

		Assert.assertTrue(domainWeave.hasClassBeenWoven(car));

	}

	public void test_class_weave_can_deregister_class() throws CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException,
			CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, ClassNotInWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertTrue(!classWeave.isClassRegistered(car));
		classWeave.registerClass(car);
		Assert.assertTrue(classWeave.isClassRegistered(car));
		classWeave.deregisterClass(car);
		Assert.assertTrue(!classWeave.isClassRegistered(car));
		Assert.assertTrue(!domainWeave.hasClassBeenWoven(car));

	}

	public void test_class_weave_cant_deregister_un_registered_class() throws CannotAddDuplicateDomainToDomainWeaveException {
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		domainWeave.registerDomain(busDomain);
		EntityClass car = busDomain.getEntityClassWithName("Car");

		ClassWeave classWeave = domainWeave.createClassWeave();
		Assert.assertTrue(!classWeave.isClassRegistered(car));

		try {
			classWeave.deregisterClass(car);
			fail();
		} catch (ClassNotInWeaveException e) {
		}
	}

	
	public void test_class_can_get_other_classes_in_weave() throws CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, ClassNotInWeaveException, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException
	{
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");

		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass task = ttdDomain.getEntityClassWithName("Task");
		
		domainWeave.registerDomain(busDomain);
		domainWeave.registerDomain(shoppingCartDomain);
		domainWeave.registerDomain(ttdDomain);
		
		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);
		classWeave.registerClass(cart);
		classWeave.registerClass(task);
		
		Collection<EntityClass> allClasses = classWeave.getWovenClasses();
		Collection<EntityClass> allClassesExceptCar = classWeave.getWovenClassesExceptFor(car);
		Collection<EntityClass> allClassesExceptCart = classWeave.getWovenClassesExceptFor(cart);
		Collection<EntityClass> allClassesExceptTask = classWeave.getWovenClassesExceptFor(task);
		
		Assert.assertEquals(3, allClasses.size());
		Assert.assertEquals(2, allClassesExceptCar.size());
		Assert.assertEquals(2, allClassesExceptCart.size());
		Assert.assertEquals(2, allClassesExceptTask.size());
		
		Assert.assertTrue(allClasses.contains(car));
		Assert.assertTrue(allClasses.contains(cart));
		Assert.assertTrue(allClasses.contains(task));

		Assert.assertTrue(!allClassesExceptCar.contains(car));
		Assert.assertTrue(allClassesExceptCar.contains(cart));
		Assert.assertTrue(allClassesExceptCar.contains(task));
		
		Assert.assertTrue(allClassesExceptCart.contains(car));
		Assert.assertTrue(!allClassesExceptCart.contains(cart));
		Assert.assertTrue(allClassesExceptCart.contains(task));
		
		Assert.assertTrue(allClassesExceptTask.contains(car));
		Assert.assertTrue(allClassesExceptTask.contains(cart));
		Assert.assertTrue(!allClassesExceptTask.contains(task));
		
	}
	
	
	public void test_classes_cannot_be_woven_with_other_classes_in_the_same_domain() throws CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException
	{
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		EntityClass personal = busDomain.getEntityClassWithName("Personal");
		
		domainWeave.registerDomain(busDomain);
		
		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);
		
		try{
			classWeave.registerClass(personal);
			fail();
		}
		catch(CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException crmcftsdcwe)
		{
		}
	}

	public void test_can_create_attribute_weave_from_class_weave() throws CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException, NameNotFoundException, NameAlreadyBoundException
	{
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		EntityAttribute carNumberDoors = car.getAttributeWithName("NumberDoors");
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");

		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass task = ttdDomain.getEntityClassWithName("Task");
		EntityAttribute taskNumber = new EntityAttribute("Number", IntegerEntityDatatype.getInstance());
		task.addAttribute(taskNumber);
		
		domainWeave.registerDomain(busDomain);
		domainWeave.registerDomain(shoppingCartDomain);
		domainWeave.registerDomain(ttdDomain);
		
		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);
		classWeave.registerClass(cart);
		classWeave.registerClass(task);

		AttributeWeave attributeWeave = classWeave.createAttributeWeave();
		Assert.assertEquals(true, classWeave.getAttributeWeaves().contains(attributeWeave));
		Assert.assertEquals(false, attributeWeave.isAttributeInWeave(carNumberDoors));
		Assert.assertEquals(false, attributeWeave.isAttributeInWeave(taskNumber));
		Assert.assertEquals(false, classWeave.isAttributeInAnyWeave(carNumberDoors));
		Assert.assertEquals(false, classWeave.isAttributeInAnyWeave(taskNumber));
		Assert.assertEquals(null, classWeave.getAttributeWeaveContainingAttribute(carNumberDoors));
		Assert.assertEquals(null, classWeave.getAttributeWeaveContainingAttribute(taskNumber));
		
		
		attributeWeave.addAttribute(carNumberDoors);
		attributeWeave.addAttribute(taskNumber);
		
		Assert.assertEquals(true, attributeWeave.isAttributeInWeave(carNumberDoors));
		Assert.assertEquals(true, attributeWeave.isAttributeInWeave(taskNumber));
		Assert.assertEquals(true, classWeave.isAttributeInAnyWeave(carNumberDoors));
		Assert.assertEquals(true, classWeave.isAttributeInAnyWeave(taskNumber));
		Assert.assertEquals(attributeWeave, classWeave.getAttributeWeaveContainingAttribute(carNumberDoors));
		Assert.assertEquals(attributeWeave, classWeave.getAttributeWeaveContainingAttribute(taskNumber));
	}
	
	
	public void test_cant_weave_attributes_that_are_of_different_types() throws CannotAddDuplicateDomainToDomainWeaveException, NameNotFoundException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException
	{
		DomainWeave domainWeave = new DomainWeave("");
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass car = busDomain.getEntityClassWithName("Car");
		EntityAttribute carNumberDoors = car.getAttributeWithName("NumberDoors");
		
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");

		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass task = ttdDomain.getEntityClassWithName("Task");
		EntityAttribute taskName = task.getAttributeWithName("Name");
		
		domainWeave.registerDomain(busDomain);
		domainWeave.registerDomain(shoppingCartDomain);
		domainWeave.registerDomain(ttdDomain);
		
		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(car);
		classWeave.registerClass(cart);
		classWeave.registerClass(task);

		AttributeWeave attributeWeave = classWeave.createAttributeWeave();
		Assert.assertEquals(false, attributeWeave.isAttributeInWeave(carNumberDoors));
		Assert.assertEquals(false, attributeWeave.isAttributeInWeave(taskName));
		
		attributeWeave.addAttribute(carNumberDoors);
		attributeWeave.addAttribute(taskName);
		
		Assert.assertEquals(true, attributeWeave.isAttributeInWeave(carNumberDoors));
		// because taskName is a different type, this won't work
		Assert.assertEquals(false, attributeWeave.isAttributeInWeave(taskName));
		
	}

	public void test_can_create_event_weave_from_class_weave() throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException
	{
		DomainWeave domainWeave = new DomainWeave("");
		DomainFactory factoryDomain = new DomainFactory();
		DomainStore storeDomain = new DomainStore();
		
		domainWeave.registerDomain(factoryDomain.domain);
		domainWeave.registerDomain(storeDomain.domain);
		
		ClassWeave classWeave = domainWeave.createClassWeave();
		classWeave.registerClass(factoryDomain.item);
		classWeave.registerClass(storeDomain.item);

		EventWeave eventWeave = classWeave.createEventWeave();
		
		Assert.assertEquals(true, classWeave.getEventWeaves().contains(eventWeave));
		Assert.assertEquals(false, eventWeave.isEventInWeave(storeDomain.outOfStockSpec));
		Assert.assertEquals(false, eventWeave.isEventInWeave(factoryDomain.produceItemSpec));
		Assert.assertEquals(false, classWeave.isEventInAnyWeave(storeDomain.outOfStockSpec));
		Assert.assertEquals(false, classWeave.isEventInAnyWeave(factoryDomain.produceItemSpec));
		Assert.assertEquals(null, classWeave.getEventWeaveContainingAttribute(storeDomain.outOfStockSpec));
		Assert.assertEquals(null, classWeave.getEventWeaveContainingAttribute(factoryDomain.produceItemSpec));
		
		eventWeave.addEvent(storeDomain.outOfStockSpec);
		eventWeave.addEvent(factoryDomain.produceItemSpec);
		
		Assert.assertEquals(true, eventWeave.isEventInWeave(storeDomain.outOfStockSpec));
		Assert.assertEquals(true, eventWeave.isEventInWeave(factoryDomain.produceItemSpec));
		Assert.assertEquals(true, classWeave.isEventInAnyWeave(storeDomain.outOfStockSpec));
		Assert.assertEquals(true, classWeave.isEventInAnyWeave(factoryDomain.produceItemSpec));
		Assert.assertEquals(eventWeave, classWeave.getEventWeaveContainingAttribute(storeDomain.outOfStockSpec));
		Assert.assertEquals(eventWeave, classWeave.getEventWeaveContainingAttribute(factoryDomain.produceItemSpec));
	}
	
}

