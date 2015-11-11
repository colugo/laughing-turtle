package test.java.helper;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

/**
*      Domain model for Tick Tick Done
* 
*       +--------+      R1      +------+        R2     +--------+
*       |  User  |------+-------| Task +---------------|  Step  |----+
*       +--------+      .       +------+               +--------+    |
*       | Name   |      .       | Name |               |Complete|    |
*       | Age    | +----+-----+ |Status+----+          +--------+    | R3
*       +--------+ |Assignment| +------+    |                |       |
*                  +----------+     |       | R4             +---+---+
*                  | Age      |     +---+---+                    .
*                  |Difficulty|                                  .
*                  +----------+                             +----+---+
*                                                           |Sequence|
*                                                           +--------+
*                                                           |  Order |
*                                                           +--------+
* 
* 
*     (made with JavE -- disable javadoc formatting in eclipse to preserve this)
*/
public class DomainTTD extends TestCase {
	public DomainTTD(String name) {
		super(name);
	}


	public static EntityDomain getTTDDomain() {
		EntityDomain ttd = new EntityDomain("TickTickDone");

		// classes
		EntityClass user = new EntityClass("User");
		ttd.addClass(user);

		EntityClass task = new EntityClass("Task");
		ttd.addClass(task);

		EntityClass step = new EntityClass("Step");
		ttd.addClass(step);

		EntityClass assignment = new EntityClass("Assignment");
		ttd.addClass(assignment);

		EntityClass sequence = new EntityClass("Sequence");
		ttd.addClass(sequence);

		// relations
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(user, CardinalityType.ONE_TO_MANY);
		r1.setEndB(task, CardinalityType.ONE_TO_ONE);

		EntityRelation r2 = new EntityRelation("R2");
		r2.setEndA(step, CardinalityType.ONE_TO_MANY);
		r2.setEndB(task, CardinalityType.ONE_TO_ONE);

		EntityRelation r3 = new EntityRelation("R3");
		r3.setEndA(step, CardinalityType.ONE_TO_ONE, "Leads");
		r3.setEndB(step, CardinalityType.ONE_TO_MANY, "Follows");

		EntityRelation r4 = new EntityRelation("R4");
		r4.setEndA(task, CardinalityType.ONE_TO_ONE, "Leads");
		r4.setEndB(task, CardinalityType.ONE_TO_MANY, "Follows");

		// associations
		r1.setAssociation(assignment);
		r3.setAssociation(sequence);

		// attributes
		try
		{
			EntityAttribute userAge = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
			user.addAttribute(userAge);
			EntityAttribute userName = new EntityAttribute("Name", StringEntityDatatype.getInstance());
			user.addAttribute(userName);
			EntityAttribute assignmentAge = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
			assignment.addAttribute(assignmentAge);
			EntityAttribute assignmentDifficulty = new EntityAttribute("Difficulty", IntegerEntityDatatype.getInstance());
			assignment.addAttribute(assignmentDifficulty);
			EntityAttribute taskName = new EntityAttribute("Name", StringEntityDatatype.getInstance());
			task.addAttribute(taskName);
			EntityAttribute taskStatus = new EntityAttribute("Status", StringEntityDatatype.getInstance());
			task.addAttribute(taskStatus);
			EntityAttribute stepComplete = new EntityAttribute("Complete", BooleanEntityDatatype.getInstance());
			step.addAttribute(stepComplete);
			EntityAttribute sequenceOrder = new EntityAttribute("Order", IntegerEntityDatatype.getInstance());
			sequence.addAttribute(sequenceOrder);
		}catch(Exception e)
		{}

		return ttd;
	}
	
	
	public void testTTDDomainSetup() throws NameNotFoundException {
		EntityDomain ttd = getTTDDomain();

		// classes exist
		Assert.assertTrue(ttd.hasEntityClassWithName("User"));
		EntityClass user = ttd.getEntityClassWithName("User");
		Assert.assertTrue(ttd.hasEntityClassWithName("Task"));
		EntityClass task = ttd.getEntityClassWithName("Task");
		Assert.assertTrue(ttd.hasEntityClassWithName("Step"));
		EntityClass step = ttd.getEntityClassWithName("Step");
		Assert.assertTrue(ttd.hasEntityClassWithName("Assignment"));
		EntityClass assignment = ttd.getEntityClassWithName("Assignment");
		Assert.assertTrue(ttd.hasEntityClassWithName("Sequence"));
		EntityClass sequence = ttd.getEntityClassWithName("Sequence");

		// relations exist
		Assert.assertTrue(ttd.hasRelationWithName("R1"));
		EntityRelation r1 = ttd.getRelationWithName("R1");
		Assert.assertTrue(ttd.hasRelationWithName("R2"));
		EntityRelation r2 = ttd.getRelationWithName("R2");
		Assert.assertTrue(ttd.hasRelationWithName("R3"));
		EntityRelation r3 = ttd.getRelationWithName("R3");
		Assert.assertTrue(ttd.hasRelationWithName("R4"));
		EntityRelation r4 = ttd.getRelationWithName("R4");

		// classes have relations
		Assert.assertTrue(user.hasRelation("R1"));
		Assert.assertTrue(user.hasRelation(r1));
		Assert.assertTrue(task.hasRelation("R1"));
		Assert.assertTrue(task.hasRelation(r1));
		Assert.assertTrue(task.hasRelation("R2"));
		Assert.assertTrue(task.hasRelation(r2));
		Assert.assertTrue(task.hasRelation("R4"));
		Assert.assertTrue(task.hasRelation(r4));
		Assert.assertTrue(step.hasRelation("R2"));
		Assert.assertTrue(step.hasRelation(r2));
		Assert.assertTrue(step.hasRelation("R3"));
		Assert.assertTrue(step.hasRelation(r3));

		// relations have classes
		Assert.assertTrue(r1.hasClassWithName("User"));
		Assert.assertTrue(r1.hasClass(user));
		Assert.assertTrue(r1.hasClassWithName("Task"));
		Assert.assertTrue(r1.hasClass(task));
		Assert.assertTrue(r2.hasClassWithName("Task"));
		Assert.assertTrue(r2.hasClass(task));
		Assert.assertTrue(r2.hasClassWithName("Step"));
		Assert.assertTrue(r2.hasClass(step));
		Assert.assertTrue(r3.hasClassWithName("Step"));
		Assert.assertTrue(r3.hasClass(step));
		Assert.assertTrue(r4.hasClassWithName("Task"));
		Assert.assertTrue(r4.hasClass(task));

		// reflexive
		Assert.assertTrue(r3.isReflexive());
		Assert.assertEquals("Leads", r3.getClassAVerb());
		Assert.assertEquals("Follows", r3.getClassBVerb());
		Assert.assertTrue(r4.isReflexive());
		Assert.assertEquals("Leads", r4.getClassAVerb());
		Assert.assertEquals("Follows", r4.getClassBVerb());

		// association classes
		Assert.assertTrue(r1.hasAssociation());
		Assert.assertEquals(r1.getAssociation(), assignment);
		Assert.assertTrue(r3.hasAssociation());
		Assert.assertEquals(r3.getAssociation(), sequence);

		// attribtutes
		Assert.assertTrue(user.hasAttribute("Age"));
		EntityAttribute userAge = user.getAttributeWithName("Age");
		Assert.assertEquals(userAge.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(user.hasAttribute("Name"));
		EntityAttribute userName = user.getAttributeWithName("Name");
		Assert.assertEquals(userName.getType(), StringEntityDatatype.getInstance());

		Assert.assertTrue(assignment.hasAttribute("Age"));
		EntityAttribute assignmentAge = assignment.getAttributeWithName("Age");
		Assert.assertEquals(assignmentAge.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(assignment.hasAttribute("Difficulty"));
		EntityAttribute assignmentDifficulty = assignment.getAttributeWithName("Difficulty");
		Assert.assertEquals(assignmentDifficulty.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(task.hasAttribute("Name"));
		EntityAttribute taskName = task.getAttributeWithName("Name");
		Assert.assertEquals(taskName.getType(), StringEntityDatatype.getInstance());

		Assert.assertTrue(task.hasAttribute("Status"));
		EntityAttribute taskStatus = task.getAttributeWithName("Status");
		Assert.assertEquals(taskStatus.getType(), StringEntityDatatype.getInstance());

		Assert.assertTrue(step.hasAttribute("Complete"));
		EntityAttribute stepComplete = step.getAttributeWithName("Complete");
		Assert.assertEquals(stepComplete.getType(), BooleanEntityDatatype.getInstance());

		Assert.assertTrue(sequence.hasAttribute("Order"));
		EntityAttribute sequenceOrder = sequence.getAttributeWithName("Order");
		Assert.assertEquals(sequenceOrder.getType(), IntegerEntityDatatype.getInstance());
	}


}
