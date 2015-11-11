package test.java.editorCommandTests;

import test.java.helper.TestHelper;
import test.java.helper.TestingUUID;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.EditorCommand_AddClassToDomain;
import main.java.avii.editor.command.EditorCommand_AddStateToClass;
import main.java.avii.editor.command.EditorCommand_EditStateActionLanguage;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EditStateActionLanguageCommandTests extends TestCase {
	public EditStateActionLanguageCommandTests(String name)
	{
		super(name);
	}
	
	public void test_can_add_initial_procedure_to_state() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setDomain(domain);
		editStateProcedure.setStateId(newStateName);
		editStateProcedure.setClassId("Class");
		editStateProcedure.setActionLanguage(actionLanguage);
		
		Assert.assertEquals(true, editStateProcedure.doCommand().returnStatus());
		
		Assert.assertEquals(actionLanguage, state.getProcedure().getRawText());
	}
	
	public void test_round_trip_serialisation() throws Exception
	{
		String domainName = "NewDomain";
		String classId = "ID1";
		String newStateName = "NewStateName";
		String actionLanguage = "CREATE bob FROM Person;\n";
		EditorCommandHistory history = new EditorCommandHistory(domainName);
		history.setUUID(new TestingUUID());
		
		EditorCommand_AddClassToDomain addClassToDomain = new EditorCommand_AddClassToDomain();
		addClassToDomain.setClassId(classId);
		history.performCommand(addClassToDomain);
		
		EditorCommand_AddStateToClass addStateToClass = new EditorCommand_AddStateToClass();
		addStateToClass.setStateId(newStateName);
		addStateToClass.setClassId(classId);
		history.performCommand(addStateToClass);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setStateId(newStateName);
		editStateProcedure.setClassId(classId);
		editStateProcedure.setActionLanguage(actionLanguage);
		history.performCommand(editStateProcedure);
	
		EditorCommandHistory history2 = TestHelper.roundTripEditorHistoryCommandHelper(history);
		Assert.assertEquals(actionLanguage, history2.getDomain().getEntityClassWithId(classId).getStateWithName("State_" + newStateName).getRawText());
	}
	
	public void test_can_edit_procedure() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		String newActionLanguage = "temp = 2;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		state.setProcedureText(actionLanguage);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setDomain(domain);
		editStateProcedure.setStateId(newStateName);
		editStateProcedure.setClassId("Class");
		
		editStateProcedure.setActionLanguage(newActionLanguage);
		
		Assert.assertEquals(true, editStateProcedure.doCommand().returnStatus());
		
		Assert.assertEquals(newActionLanguage, state.getProcedure().getRawText());
	}
	
	public void test_cant_add_initial_procedure_to_state_where_class_not_found() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setDomain(domain);
		editStateProcedure.setStateId(newStateName);
		editStateProcedure.setClassId("Class1");
		
		editStateProcedure.setActionLanguage(actionLanguage);
		
		Assert.assertEquals(false, editStateProcedure.doCommand().returnStatus());
	}
	
	public void test_cant_add_initial_procedure_to_state_where_state_not_found() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "temp = 1;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setDomain(domain);
		editStateProcedure.setStateId(newStateName+ "1");
		editStateProcedure.setClassId("Class");
		
		editStateProcedure.setActionLanguage(actionLanguage);
		
		Assert.assertEquals(false, editStateProcedure.doCommand().returnStatus());
	}
	
	public void test_cant_set_invalid_actionlanguage() throws NameAlreadyBoundException
	{
		String newStateName = "NewStateName";
		String actionLanguage = "CREATE bob FROM Person a;\n";
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Class");
		domain.addClass(klass);
		EntityState state = new EntityState(newStateName);
		klass.addState(state);
		
		EditorCommand_EditStateActionLanguage editStateProcedure = new EditorCommand_EditStateActionLanguage();
		editStateProcedure.setDomain(domain);
		editStateProcedure.setStateId(newStateName);
		editStateProcedure.setClassId("Class");
		
		editStateProcedure.setActionLanguage(actionLanguage);
		
		Assert.assertEquals(false, editStateProcedure.doCommand().returnStatus());
	}
}
