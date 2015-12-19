package implementation;

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
public class DomainBus{


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

		vehicle.x = 108.0;
		vehicle.y = 20.0;
		car.x = 34.0;
		car.y = 185.0;
		bus.x = 223.0;
		bus.y = 184.0;
		personal.x = 116.0;
		personal.y = 342.0;
		commercial.x = 319.0;
		commercial.y = 344.0;
		tyre.x = 651.0;
		tyre.y = 175.0;
		snowTyre.x = 797.0;
		snowTyre.y = 351.0;
		roadTyre.x = 593.0;
		roadTyre.y = 354.0;
		
		
		
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

}
