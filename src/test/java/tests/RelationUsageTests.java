package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRelationReference;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityUsageReferenceManager;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;

public class RelationUsageTests extends TestCase {

	public RelationUsageTests(String name) {
		super(name);
	}

	public void test_can_identify_when_a_relation_when_used_in_relate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "RELATE user TO task ACROSS R1 CREATING assignment;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityRelationReferenced(r1));
		ArrayList<EntityRelationReference> referenceList = entityUsageReader.getEntityRelationshipReferences(r1);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("RELATE user TO task ACROSS R1 CREATING assignment;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_a_relation_when_used_in_relate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "RELATE user TO task ACROSS R1 CREATING assignment;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("R1","RelationOne"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	public void test_can_identify_when_a_relation_when_used_in_unrelate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE user FROM task ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityRelationReferenced(r1));
		ArrayList<EntityRelationReference> referenceList = entityUsageReader.getEntityRelationshipReferences(r1);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("UNRELATE user FROM task ACROSS R1;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_a_relation_when_used_in_unrelate_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "UNRELATE user FROM task ACROSS R1;\n";
		
		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("R1","RelationOne"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	public void test_can_identify_when_a_relation_when_used_in_select_one_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO user ACROSS R1;\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityRelationReferenced(r1));
		ArrayList<EntityRelationReference> referenceList = entityUsageReader.getEntityRelationshipReferences(r1);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(3,firstReference.getReferencedLineNumber());
		Assert.assertEquals("SELECT ONE assignment THAT RELATES task TO user ACROSS R1;",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_a_relation_when_used_in_select_one_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "CREATE task FROM Task;\n";
		proc += "SELECT ONE assignment THAT RELATES task TO user ACROSS R1;\n";
		
		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("R1","RelationOne"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
	
	
	public void test_can_identify_when_a_relation_when_used_in_select_related_by_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\";\n";

		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
	
		Assert.assertTrue(procedure.validate());
	
		EntityUsageReferenceManager entityUsageReader = procedure.getEntityUsageManager();
		Assert.assertTrue(entityUsageReader.isEntityRelationReferenced(r1));
		ArrayList<EntityRelationReference> referenceList = entityUsageReader.getEntityRelationshipReferences(r1);
		Assert.assertEquals(1,referenceList.size());
		BaseEntityReference firstReference = referenceList.get(0);
		Assert.assertEquals(2,firstReference.getReferencedLineNumber());
		Assert.assertEquals("SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\";",firstReference.getReferencedLineText());
		
		Assert.assertTrue(!entityUsageReader.isEntityClassReferenced(ttdd.getEntityClassWithName("Bus")));
		
	}
	
	public void test_can_rename_a_relation_when_used_in_select_related_by_syntax() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "CREATE user FROM User;\n";
		proc += "SELECT ANY sequence RELATED BY user->R1->R2->R3.\"Leads\";\n";
		
		EntityDomain ttdd = DomainTTD.getTTDDomain();
		EntityClass user = ttdd.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		
		EntityRelation r1 = ttdd.getRelationWithName("R1");
		
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
	
		
		EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
		String updatedProcdure = entityRenameManager.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertTrue(!proc.equals(updatedProcdure));
		
		Assert.assertEquals(proc.replace("R1","RelationOne"), updatedProcdure);
		Assert.assertTrue(procedure.validate());
	
		EntityRenameManager entityRenameManager2 = procedure.getEntityRenameManager();
		String updatedProcdure2 = entityRenameManager2.renameEntityRelation(r1,"RelationOne");
		
		Assert.assertEquals(updatedProcdure2, updatedProcdure);
	}
	
}

