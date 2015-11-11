package test.java.tests;

import test.java.helper.DomainBus;
import test.java.helper.DomainShoppingCart;
import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotCreateAssociationClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotCreateSuperClassValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class CreateTests extends TestCase {

	public CreateTests(String name) {
		super(name);
	}
	
	public void test_cant_create_class_that_has_sub_classes() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE bus FROM Bus;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CanNotCreateSuperClassValidationError.class));
	}
	
	public void test_can_create_class_that_have_no_sub_classes() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE Personal FROM Personal;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState( "Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_cant_create_association_class() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE selection FROM ItemSelection;\n";

		EntityDomain busd = DomainShoppingCart.getShoppingCartDomain();
		EntityClass user = busd.getEntityClassWithName("ShoppingCart");
		EntityState state = user.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CanNotCreateAssociationClassValidationError.class));
	}

}

