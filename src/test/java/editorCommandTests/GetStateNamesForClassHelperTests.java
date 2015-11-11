package test.java.editorCommandTests;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.command.helper.GetStateNamesForClassHelper;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import junit.framework.Assert;
import junit.framework.TestCase;

public class GetStateNamesForClassHelperTests extends TestCase {
	public GetStateNamesForClassHelperTests(String name) {
		super(name);
	}
	
	public void test_can_get_state_names_for_when_class_has_no_states() throws NameAlreadyBoundException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		
		GetStateNamesForClassHelper helper = new GetStateNamesForClassHelper();
		
		Assert.assertEquals(true, helper.doCommand(domain, "Class").returnStatus());
		Assert.assertEquals(0, helper.getStateNames().size());
		
	}
	
	public void test_can_get_state_names_for_when_class_has_states() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		state.setProcedureText(actionLanguage);
		
		GetStateNamesForClassHelper helper = new GetStateNamesForClassHelper();
		
		Assert.assertEquals(true, helper.doCommand(domain, "Class").returnStatus());
		Assert.assertEquals(1, helper.getStateNames().size());
		Assert.assertEquals(true, helper.getStateNames().contains(newStateName));

	}
	
	public void test_cant_get_state_names_where_class_not_found() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		GetStateNamesForClassHelper helper = new GetStateNamesForClassHelper();
		
		Assert.assertEquals(false, helper.doCommand(domain, "Class1").returnStatus());
	}
}
