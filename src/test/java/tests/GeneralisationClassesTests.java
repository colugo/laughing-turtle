package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainWarehouse;
import test.java.helper.TestHelper;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityClassValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.MultipleSuperClassesWithSameLineageValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.AttributeNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class GeneralisationClassesTests extends TestCase {

	public GeneralisationClassesTests(String name) {
		super(name);
	}

	public void test_all_classes_in_generalisation_have_states_when_added_to_root() throws NameAlreadyBoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		tyre.addState(sharedState);

		Assert.assertTrue(tyre.hasStates());
		Assert.assertTrue(snowTyre.hasStates());
		Assert.assertTrue(roadTyre.hasStates());
	}

	public void test_all_classes_in_generalisation_have_states_when_added_to_leaf1() throws NameAlreadyBoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		snowTyre.addState(sharedState);

		Assert.assertTrue(tyre.hasStates());
		Assert.assertTrue(snowTyre.hasStates());
		Assert.assertTrue(roadTyre.hasStates());
	}

	public void test_all_classes_in_generalisation_have_states_when_added_to_leaf2() throws NameAlreadyBoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		roadTyre.addState(sharedState);

		Assert.assertTrue(tyre.hasStates());
		Assert.assertTrue(snowTyre.hasStates());
		Assert.assertTrue(roadTyre.hasStates());
	}
	
	public void test_all_classes_in_generalisation_get_state_when_added_to_root() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		tyre.addState(sharedState);

		Assert.assertEquals(sharedState, tyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, snowTyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, roadTyre.getStateWithName("Shared"));
	}

	public void test_all_classes_in_generalisation_get_state_when_added_to_leaf1() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		snowTyre.addState(sharedState);

		Assert.assertEquals(sharedState, tyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, snowTyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, roadTyre.getStateWithName("Shared"));
	}
	
	public void test_all_classes_in_generalisation_get_state_when_added_to_leaf1_via_get_states() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		snowTyre.addState(sharedState);
		
		ArrayList<EntityState> theStates = snowTyre.getStates();

		Assert.assertEquals(theStates, tyre.getStates());
		Assert.assertEquals(theStates, snowTyre.getStates());
		Assert.assertEquals(theStates, roadTyre.getStates());
	}

	public void test_all_classes_in_generalisation_get_state_when_added_to_leaf2() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain busDomain = DomainBus.getBusDomain();
		EntityClass tyre = busDomain.getEntityClassWithName("Tyre");
		EntityClass snowTyre = busDomain.getEntityClassWithName("SnowTyre");
		EntityClass roadTyre = busDomain.getEntityClassWithName("RoadTyre");

		Assert.assertTrue(!tyre.hasStates());
		Assert.assertTrue(!snowTyre.hasStates());
		Assert.assertTrue(!roadTyre.hasStates());

		EntityState sharedState = new EntityState("Shared");
		roadTyre.addState(sharedState);

		Assert.assertEquals(sharedState, tyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, snowTyre.getStateWithName("Shared"));
		Assert.assertEquals(sharedState, roadTyre.getStateWithName("Shared"));
	}

	/**
	* 
	* 
	* 
	* 
	* 
	*                          +-------+--------+
	*                          |    Account     |
	*                          +-------.--------+
	*                                 / \
	*                                /-+-\
	*                                  |
	*                      +-----------+-----------+
	*                      |                       |
	*              +-------+--------+      +-------+--------+
	*              |Checking Account|      |Savings Account |
	*              +-------.--------+      +---------------++
	*                     / \                              |
	*                    /-+-\                             |
	*                      |                               |
	*          +-----------+-----------+                   |
	*          |                       |                   |
	*  +-------+--------+      +-------+--------+          |
	*  |RegularChecking |      |InterestChecking|          |
	*  +----------------+      +--------+-------+          |
	*                                   +------------------+
	*                                                    \-+-/
	*                                                     \ /
	*                                              +-------'--------+
	*                                              |InterestBearing |
	*                                              +----------------+
	 * @throws NameAlreadyBoundException 
	* 
	*/
	public void test_can_get_all_classes_in_generalisation_hierarchy_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = account.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_can_get_all_classes_in_generalisation_hierarchy_checking_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = checkingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_can_get_all_classes_in_generalisation_hierarchy_savings_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = savingsAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_can_get_all_classes_in_generalisation_hierarchy_regular_checking_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = regularCheckingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_can_get_all_classes_in_generalisation_hierarchy_interest_checking_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = interestCheckingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_can_get_all_classes_in_generalisation_hierarchy_interest_bearing_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = interestBearingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(savingsAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestBearingAccount));
	}
	
	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_account() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		account.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
		
	}

	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_checking_account() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		checkingAccount.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
	}

	
	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_savings_account() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		savingsAccount.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
		
	}

	
	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_regular_checking() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		regularCheckingAccount.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
		
	}

	
	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_interest_checking() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		interestCheckingAccount.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
		
	}

	
	public void test_classes_in_generalisation_with_multiple_roots_can_only_have_one_state_machine_interest_bearing() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		Assert.assertTrue(!account.hasStates());
		Assert.assertTrue(!checkingAccount.hasStates());
		Assert.assertTrue(!savingsAccount.hasStates());
		Assert.assertTrue(!regularCheckingAccount.hasStates());
		Assert.assertTrue(!interestCheckingAccount.hasStates());
		Assert.assertTrue(!interestBearingAccount.hasStates());
		
		EntityState shared = new EntityState("Shared");
		interestBearingAccount.addState(shared);
		
		Assert.assertTrue(account.hasStates());
		Assert.assertTrue(checkingAccount.hasStates());
		Assert.assertTrue(savingsAccount.hasStates());
		Assert.assertTrue(regularCheckingAccount.hasStates());
		Assert.assertTrue(interestCheckingAccount.hasStates());
		Assert.assertTrue(interestBearingAccount.hasStates());
	}

	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		account.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_checking_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		checkingAccount.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_saving_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		savingsAccount.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	
	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_regular_checking_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		regularCheckingAccount.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_interest_checking_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		interestCheckingAccount.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	public void test_classes_in_generalisation_with_multiple_roots_can_get_state_machine_interest_bearing_account() throws NameAlreadyBoundException, NameNotFoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState shared = new EntityState("Shared");
		interestBearingAccount.addState(shared);
		
		Assert.assertEquals(shared, account.getStateWithName("Shared"));
		Assert.assertEquals(shared, checkingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, savingsAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, regularCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestCheckingAccount.getStateWithName("Shared"));
		Assert.assertEquals(shared, interestBearingAccount.getStateWithName("Shared"));
	}
	
	public void test_different_classes_in_generalisations_can_set_their_own_initial_states() throws NameAlreadyBoundException {
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityState state1 = new EntityState("state1");
		interestBearingAccount.addState(state1);
		EntityState state2 = new EntityState("state2");
		interestBearingAccount.addState(state2);
		EntityState state3 = new EntityState("state3");
		interestBearingAccount.addState(state3);
		
		savingsAccount.setInitial(state1);
		regularCheckingAccount.setInitial(state2);
		interestCheckingAccount.setInitial(state3);
		
		Assert.assertEquals(state1, savingsAccount.getInitialState());
		Assert.assertEquals(state2, regularCheckingAccount.getInitialState());
		Assert.assertEquals(state3, interestCheckingAccount.getInitialState());
	}
	
	public void test_validate_complex_generalisation_structure()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		interestBearingAccount.addSubClass(savingsAccount);
		interestBearingAccount.addSubClass(interestCheckingAccount);
		
		EntityDomainValidator validator = new EntityDomainValidator(accounts); 
		Assert.assertTrue(validator.validate());
	}
	
	public void test_validation_will_fail_with_impropper_multiple_validation()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass savingsAccount = new EntityClass("SavingsAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		EntityClass interestBearingAccount = new EntityClass("InterestBearingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(savingsAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		accounts.addClass(interestBearingAccount);
		
		account.addSubClass(checkingAccount);
		account.addSubClass(savingsAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		savingsAccount.addSubClass(interestBearingAccount);
		savingsAccount.addSubClass(interestCheckingAccount);
		
		EntityDomainValidator validator = new EntityDomainValidator(accounts); 
		Assert.assertTrue(!validator.validate());
		ArrayList<IValidationError> errors = validator.getValidationErrors();
		Assert.assertEquals(1,errors.size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, MultipleSuperClassesWithSameLineageValidationError.class));
	}

	public void test_can_validate_generic_state_machine_for_each_leaf_sub_class_in_the_hierarchy() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException {
		// I think I get this for free.
		// as part of class validation, all the states of a class get validated,
		// so long as i re-caclulate the lifespan map (change definition of self)
		// for each validation, and only validate the leaves

		EntityDomain domainWarehouse = DomainWarehouse.getWarehouseDomainWithActionLanguage();
		EntityDomainValidator validator = new EntityDomainValidator(domainWarehouse);
		TestHelper.printValidationErrors(validator);
		Assert.assertTrue(validator.validate());
	}
	
	public void test_validate_generic_state_machine_fails_when_super_class_uses_sub_class_attributes() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException {
		// I think I get this for free.
		// as part of class validation, all the states of a class get validated,
		// so long as i re-caclulate the lifespan map (change definition of self)
		// for each validation, and only validate the leaves

		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState generalState = warehouseClerk.getStateWithName("GeneralState");
		String generalProcString = "";
		generalProcString += "RECLASSIFY TO WarehouseClerk;\n";
		generalProcString += "self.ClerkId = 37;\n";
		generalProcString += "self.Idle = false;\n";
		new EntityProcedure(generalState).setProcedure(generalProcString);
		
		
		EntityDomainValidator validator = new EntityDomainValidator(warehouseDomain);
		Assert.assertTrue(!validator.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, AttributeNotFoundInClassValidationError.class));
	}
	
	
	public void test_states_for_generalisation_classes_must_have_reclassify_instruction_first_with_non_reclassify_first() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		/*
		 * I can't think of any other way to determine what type the instance is in any particulare
		 * generalisation state 
		 */
	
		EntityDomain domainWarehouse = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = domainWarehouse.getEntityClassWithName("WarehouseClerk");
		EntityState offDuty = warehouseClerk.getStateWithName("OffDutyState");
		EntityProcedure procedure = new EntityProcedure(offDuty);
		
		String proc = "temp = 6;\n";
		
		procedure.setProcedure(proc);
		EntityClassValidator validator = new EntityClassValidator(warehouseClerk);
		Assert.assertTrue(!validator.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(validator, FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError.class));
	}
	
	public void test_states_for_generalisation_classes_must_have_reclassify_instruction_first_with_reclassify_first() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		/*
		 * I can't think of any other way to determine what type the instance is in any particulare
		 * generalisation state 
		 */
	
		EntityDomain domainWarehouse = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = domainWarehouse.getEntityClassWithName("WarehouseClerk");
		EntityState offDuty = warehouseClerk.getStateWithName("OffDutyState");
		EntityProcedure procedure = new EntityProcedure(offDuty);
		
		String proc = "RECLASSIFY TO OffDutyClerk;\n";
		proc += "temp = 6;\n";
		
		procedure.setProcedure(proc);
		EntityClassValidator validator = new EntityClassValidator(warehouseClerk);
		TestHelper.printValidationErrors(validator);
		Assert.assertTrue(validator.validate());
	}
	
	public void test_states_for_generalisation_classes_must_have_reclassify_instruction_first_with_whitespace_first() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		/*
		 * I can't think of any other way to determine what type the instance is in any particulare
		 * generalisation state 
		 */
	
		EntityDomain domainWarehouse = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = domainWarehouse.getEntityClassWithName("WarehouseClerk");
		EntityState offDuty = warehouseClerk.getStateWithName("OffDutyState");
		EntityProcedure procedure = new EntityProcedure(offDuty);
		
		String proc = "\n\n\nRECLASSIFY TO OffDutyClerk;\n";
		proc += "temp = 6;\n";
		
		procedure.setProcedure(proc);
		EntityClassValidator validator = new EntityClassValidator(warehouseClerk);
		Assert.assertTrue(validator.validate());
	}
	
	
	public void test_states_for_generalisation_classes_must_have_reclassify_instruction_first_with_comment_first() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		/*
		 * I can't think of any other way to determine what type the instance is in any particulare
		 * generalisation state 
		 */
	
		EntityDomain domainWarehouse = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = domainWarehouse.getEntityClassWithName("WarehouseClerk");
		EntityState offDuty = warehouseClerk.getStateWithName("OffDutyState");
		EntityProcedure procedure = new EntityProcedure(offDuty);
		
		String proc = "#This is a comment\n";
		proc += "RECLASSIFY TO OffDutyClerk;\n";
		proc += "temp = 6;\n";
		
		procedure.setProcedure(proc);
		EntityClassValidator validator = new EntityClassValidator(warehouseClerk);
		Assert.assertTrue(validator.validate());
	}
	
	public void test_can_remove_superclass_from_middle_generalisation_hierarchy_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass account = new EntityClass("Account");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		
		accounts.addClass(account);
		accounts.addClass(checkingAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		
		account.addSubClass(checkingAccount);
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = checkingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		
		Assert.assertTrue(checkingAccount.getSuperClasses().contains(account));
		Assert.assertTrue(account.getsubClasses().contains(checkingAccount));
		Assert.assertTrue(regularCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(regularCheckingAccount));
		Assert.assertTrue(interestCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(interestCheckingAccount));
		
		Assert.assertEquals(true, account.isGeneralisation());
		Assert.assertEquals(true, checkingAccount.isGeneralisation());
		Assert.assertEquals(true, regularCheckingAccount.isGeneralisation());
		Assert.assertEquals(true, interestCheckingAccount.isGeneralisation());
		
		checkingAccount.removeSuperClasses();
		hierarchy = checkingAccount.getAllClassesInHierarchy();
		
		Assert.assertFalse(hierarchy.contains(account));
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		
		Assert.assertFalse(checkingAccount.getSuperClasses().contains(account));
		Assert.assertFalse(account.getsubClasses().contains(checkingAccount));
		Assert.assertTrue(regularCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(regularCheckingAccount));
		Assert.assertTrue(interestCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(interestCheckingAccount));
		
		Assert.assertEquals(false, account.isGeneralisation());
		Assert.assertEquals(true, checkingAccount.isGeneralisation());
		Assert.assertEquals(true, regularCheckingAccount.isGeneralisation());
		Assert.assertEquals(true, interestCheckingAccount.isGeneralisation());
	}
	
	public void test_can_remove_superclass_generalisation_hierarchy_account()
	{
		EntityDomain accounts = new EntityDomain("Accounts");
		EntityClass checkingAccount = new EntityClass("CheckingAccount");
		EntityClass regularCheckingAccount = new EntityClass("RegularCheckingAccount");
		EntityClass interestCheckingAccount = new EntityClass("InterestCheckingAccount");
		
		accounts.addClass(checkingAccount);
		accounts.addClass(regularCheckingAccount);
		accounts.addClass(interestCheckingAccount);
		
		checkingAccount.addSubClass(regularCheckingAccount);
		checkingAccount.addSubClass(interestCheckingAccount);
		
		ArrayList<EntityClass> hierarchy = checkingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertTrue(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		
		Assert.assertTrue(regularCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(regularCheckingAccount));
		Assert.assertTrue(interestCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(interestCheckingAccount));
		
		Assert.assertEquals(true, checkingAccount.isGeneralisation());
		Assert.assertEquals(true, regularCheckingAccount.isGeneralisation());
		Assert.assertEquals(true, interestCheckingAccount.isGeneralisation());
		
		regularCheckingAccount.removeSuperClasses();
		hierarchy = checkingAccount.getAllClassesInHierarchy();
		
		Assert.assertTrue(hierarchy.contains(checkingAccount));
		Assert.assertFalse(hierarchy.contains(regularCheckingAccount));
		Assert.assertTrue(hierarchy.contains(interestCheckingAccount));
		
		Assert.assertFalse(regularCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertFalse(checkingAccount.getsubClasses().contains(regularCheckingAccount));
		Assert.assertTrue(interestCheckingAccount.getSuperClasses().contains(checkingAccount));
		Assert.assertTrue(checkingAccount.getsubClasses().contains(interestCheckingAccount));
		
		Assert.assertEquals(true, checkingAccount.isGeneralisation());
		Assert.assertEquals(false, regularCheckingAccount.isGeneralisation());
		Assert.assertEquals(true, interestCheckingAccount.isGeneralisation());

	}
	
}

