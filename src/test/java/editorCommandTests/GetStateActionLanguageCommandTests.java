package test.java.editorCommandTests;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.helper.GetStateActionLanguageHelper;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;

public class GetStateActionLanguageCommandTests extends TestCase {
	public GetStateActionLanguageCommandTests(String name) {
		super(name);
	}
	
	public void test_can_add_initial_procedure_to_state() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		GetStateActionLanguageHelper editStateProcedure = new GetStateActionLanguageHelper();
		
		Assert.assertEquals(true, editStateProcedure.doCommand(domain, "Class", newStateName).returnStatus());
		Assert.assertEquals("", editStateProcedure.getActionLanguage());
		
	}
	
	public void test_can_edit_procedure() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		state.setProcedureText(actionLanguage);
		
		GetStateActionLanguageHelper editStateProcedure = new GetStateActionLanguageHelper();
		
		Assert.assertEquals(true, editStateProcedure.doCommand(domain, "Class", newStateName).returnStatus());
		Assert.assertEquals(actionLanguage, editStateProcedure.getActionLanguage());		

	}
	
	public void test_cant_add_initial_procedure_to_state_where_class_not_found() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		GetStateActionLanguageHelper editStateProcedure = new GetStateActionLanguageHelper();
		
		Assert.assertEquals(false, editStateProcedure.doCommand(domain, "Class1", newStateName).returnStatus());
	}
	
	public void test_cant_add_initial_procedure_to_state_where_state_not_found() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		GetStateActionLanguageHelper editStateProcedure = new GetStateActionLanguageHelper();
				
		Assert.assertEquals(false, editStateProcedure.doCommand(domain, "Class", newStateName + "1").returnStatus());
	}
}
