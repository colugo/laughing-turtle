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
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

/**
*  Bus domain, for generalisation hierarchy testing 
*              +------------+
*              |  Vehicle   |
*              +------------+
*              |  License   |
*              +-----^------+
*                   / \
*                   -+-
*                    |
*          +---------+------------+
*    +-----+------+       +-------+----+             R1                  +------------+
*    |    Car     |       |    Bus     +---------------------------------+    Tyre    |
*    +------------+       +------------+                                 +------------+
*    |NumberDoors |       | HasToilet  |                                 |  DaysWear  |
*    +------------+       +-----^------+                                 +-----^------+
*                              / \                                            / \
*                              -+-                                            -+-
*                               |                                              |
*                     +---------+------------+                       +---------+------------+
*               +-----+------+       +-------+----+            +-----+------+       +-------+----+
*               |  Personal  |       | Commercial |            |  SnowTyre  |       |  RoadTyre  |
*               +------------+       +------------+            +------------+       +------------+
*               | LeaseAmount|       | CompanyName|            | SpikeNumber|       | SlickGrade |
*               +------------+       +------------+            +------------+       +------------+
*/
public class DomainBus extends TestCase {

	public DomainBus(String name) {
		super(name);
	}

	public static EntityDomain getBusDomain() {
		EntityDomain bd = new EntityDomain("BusDomain");

		// classes
		EntityClass vehicle = new EntityClass("Vehicle");
		bd.addClass(vehicle);

		EntityClass car = new EntityClass("Car");
		bd.addClass(car);

		EntityClass bus = new EntityClass("Bus");
		bd.addClass(bus);

		EntityClass personal = new EntityClass("Personal");
		bd.addClass(personal);

		EntityClass commercial = new EntityClass("Commercial");
		bd.addClass(commercial);

		EntityClass tyre = new EntityClass("Tyre");
		bd.addClass(tyre);

		EntityClass snowTyre = new EntityClass("SnowTyre");
		bd.addClass(snowTyre);

		EntityClass roadTyre = new EntityClass("RoadTyre");
		bd.addClass(roadTyre);
		
		// dummyClass
		EntityClass dummy = new EntityClass("Dummy");
		bd.addClass(dummy);

		// relations
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(bus, CardinalityType.ONE_TO_ONE);
		r1.setEndB(tyre, CardinalityType.ONE_TO_MANY);

		// generalisations
		vehicle.addSubClass(bus);
		vehicle.addSubClass(car);
		bus.addSubClass(personal);
		bus.addSubClass(commercial);
		tyre.addSubClass(roadTyre);
		tyre.addSubClass(snowTyre);

		// attributes
		try
		{
			EntityAttribute vehicleLicense = new EntityAttribute("License", StringEntityDatatype.getInstance());
			vehicle.addAttribute(vehicleLicense);
			EntityAttribute carDoors = new EntityAttribute("NumberDoors", IntegerEntityDatatype.getInstance());
			car.addAttribute(carDoors);
			EntityAttribute busHasToilet = new EntityAttribute("HasToilet", BooleanEntityDatatype.getInstance());
			bus.addAttribute(busHasToilet);
			EntityAttribute personalLeaseAmount = new EntityAttribute("LeaseAmount", FloatingEntityDatatype.getInstance());
			personal.addAttribute(personalLeaseAmount);
			EntityAttribute commercialCompanyName = new EntityAttribute("CompanyName", StringEntityDatatype.getInstance());
			commercial.addAttribute(commercialCompanyName);
			EntityAttribute tyreDaysWear = new EntityAttribute("DaysWear", IntegerEntityDatatype.getInstance());
			tyre.addAttribute(tyreDaysWear);
			EntityAttribute snowTyreSpikeNumber = new EntityAttribute("SpikeNumber", IntegerEntityDatatype.getInstance());
			snowTyre.addAttribute(snowTyreSpikeNumber);
			EntityAttribute roadTyreSlickGrade = new EntityAttribute("SlickGrade", IntegerEntityDatatype.getInstance());
			roadTyre.addAttribute(roadTyreSlickGrade);
		}catch(Exception e)
		{}

		return bd;
	}

	public void testBusDomainSetup() throws NameNotFoundException {
		EntityDomain bd = getBusDomain();

		// classes
		Assert.assertTrue(bd.hasEntityClassWithName("Vehicle"));
		EntityClass vehicle = bd.getEntityClassWithName("Vehicle");

		Assert.assertTrue(bd.hasEntityClassWithName("Car"));
		EntityClass car = bd.getEntityClassWithName("Car");

		Assert.assertTrue(bd.hasEntityClassWithName("Bus"));
		EntityClass bus = bd.getEntityClassWithName("Bus");

		Assert.assertTrue(bd.hasEntityClassWithName("Personal"));
		EntityClass personal = bd.getEntityClassWithName("Personal");

		Assert.assertTrue(bd.hasEntityClassWithName("Commercial"));
		EntityClass commercial = bd.getEntityClassWithName("Commercial");

		Assert.assertTrue(bd.hasEntityClassWithName("Tyre"));
		EntityClass tyre = bd.getEntityClassWithName("Tyre");

		Assert.assertTrue(bd.hasEntityClassWithName("SnowTyre"));
		EntityClass snowTyre = bd.getEntityClassWithName("SnowTyre");

		Assert.assertTrue(bd.hasEntityClassWithName("RoadTyre"));
		EntityClass roadTyre = bd.getEntityClassWithName("RoadTyre");

		// relations
		Assert.assertTrue(bd.hasRelationWithName("R1"));
		EntityRelation r1 = bd.getRelationWithName("R1");
		Assert.assertTrue(r1.hasClassWithName("Bus"));
		Assert.assertTrue(r1.hasClass(bus));
		Assert.assertTrue(r1.hasClassWithName("Tyre"));
		Assert.assertTrue(r1.hasClass(tyre));

		// generalisations
		Assert.assertTrue(vehicle.isGeneralisation());
		Assert.assertTrue(vehicle.getsubClasses().contains(bus));
		Assert.assertTrue(vehicle.getsubClasses().contains(car));
		Assert.assertTrue(bus.getsubClasses().contains(personal));
		Assert.assertTrue(bus.getsubClasses().contains(commercial));
		Assert.assertTrue(tyre.isGeneralisation());
		Assert.assertTrue(tyre.getsubClasses().contains(roadTyre));
		Assert.assertTrue(tyre.getsubClasses().contains(snowTyre));

		// attributes
		Assert.assertTrue(vehicle.hasAttribute("License"));
		EntityAttribute vehicleLicense = vehicle.getAttributeWithName("License");
		Assert.assertEquals(vehicleLicense.getType(), StringEntityDatatype.getInstance());

		Assert.assertTrue(car.hasAttribute("NumberDoors"));
		EntityAttribute carNumberDoors = car.getAttributeWithName("NumberDoors");
		Assert.assertEquals(carNumberDoors.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(bus.hasAttribute("HasToilet"));
		EntityAttribute busHasToilet = bus.getAttributeWithName("HasToilet");
		Assert.assertEquals(busHasToilet.getType(), BooleanEntityDatatype.getInstance());

		Assert.assertTrue(personal.hasAttribute("LeaseAmount"));
		EntityAttribute personalLeaseAmount = personal.getAttributeWithName("LeaseAmount");
		Assert.assertEquals(personalLeaseAmount.getType(), FloatingEntityDatatype.getInstance());

		Assert.assertTrue(commercial.hasAttribute("CompanyName"));
		EntityAttribute commercialCompanyName = commercial.getAttributeWithName("CompanyName");
		Assert.assertEquals(commercialCompanyName.getType(), StringEntityDatatype.getInstance());

		Assert.assertTrue(tyre.hasAttribute("DaysWear"));
		EntityAttribute tyreDaysWear = tyre.getAttributeWithName("DaysWear");
		Assert.assertEquals(tyreDaysWear.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(snowTyre.hasAttribute("SpikeNumber"));
		EntityAttribute snowTyreSpikeNumber = snowTyre.getAttributeWithName("SpikeNumber");
		Assert.assertEquals(snowTyreSpikeNumber.getType(), IntegerEntityDatatype.getInstance());

		Assert.assertTrue(roadTyre.hasAttribute("SlickGrade"));
		EntityAttribute roadTyreSlickGrade = roadTyre.getAttributeWithName("SlickGrade");
		Assert.assertEquals(roadTyreSlickGrade.getType(), IntegerEntityDatatype.getInstance());

	}

	
}
