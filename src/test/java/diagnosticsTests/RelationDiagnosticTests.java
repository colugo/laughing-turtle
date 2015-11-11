package test.java.diagnosticsTests;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.mock.MockSimulatedClass;
import test.java.mock.MockSimulatedRelation;
import main.java.avii.diagnostics.DiagnosticRelaionForInstance;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.diagnostics.RelationDiagnosticItemsForRelation;
import main.java.avii.diagnostics.RelationInstanceDiagnosticItem;
import main.java.avii.simulator.relations.NonReflexiveRelationshipStorage;
import main.java.avii.simulator.relations.SimulatedRelationInstance;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class RelationDiagnosticTests extends TestCase {
	public RelationDiagnosticTests(String name) {
		super(name);
	}

	public void test_can_create_realtion_instance_diagnostic_item()
	{
		MockSimulatedClass room = new MockSimulatedClass("Room");
		SimulatedInstance room_1 = room.createInstance();
		
		MockSimulatedRelation r1 = new MockSimulatedRelation("R1");
		
		RelationInstanceDiagnosticItem  ridi = new RelationInstanceDiagnosticItem();
		ridi.setRelatedInstance(room_1.getIdentifier());
		ridi.setRelation(r1);
		
		Assert.assertEquals(room_1.getIdentifier(), ridi.getRelatedInstance());
		Assert.assertEquals(r1, ridi.getRelation());
	}
	
	public void test_can_create_realtion_instance_diagnostic_item_and_add_association()
	{
		MockSimulatedClass booking = new MockSimulatedClass("Booking");
		SimulatedInstance booking_1 = booking.createInstance();
		
		RelationInstanceDiagnosticItem  ridi = new RelationInstanceDiagnosticItem();
		Assert.assertEquals(false, ridi.hasAssociation());
		ridi.setAssociationInstance(booking_1.getIdentifier());
		Assert.assertEquals(true, ridi.hasAssociation());
		Assert.assertEquals(booking_1.getIdentifier(), ridi.getAssociation());
		
	}
	
	
	public void test_can_get_relation_isntance_diagnostic_items_for_non_reflexive_relation_storage()
	{
		MockSimulatedClass room = new MockSimulatedClass("Room");
		MockSimulatedClass guest = new MockSimulatedClass("Guest");
		
		SimulatedRelationship relation = new SimulatedRelationship();
		relation.setClassA(room);
		relation.setClassB(guest);
		
		NonReflexiveRelationshipStorage storage = new NonReflexiveRelationshipStorage(relation);
		SimulatedInstanceIdentifier instanceA = new SimulatedInstanceIdentifier(0, room);
		
		ArrayList<SimulatedInstanceIdentifier> instanceBList = new ArrayList<SimulatedInstanceIdentifier>();
		for(int i = 0; i < 10; i ++)
		{
			SimulatedRelationInstance relationInstance = relation.createInstance();
			SimulatedInstanceIdentifier instanceB = new SimulatedInstanceIdentifier(i, guest);
			instanceBList.add(instanceB);
			relationInstance.relateNonReflexiveInstance(instanceA);
			relationInstance.relateNonReflexiveInstance(instanceB);
			storage.storeRelationInstance(relationInstance);
		}
		
		Assert.assertEquals(10, storage.getRelationsForInstance(instanceA).size());
		RelationDiagnosticItemsForRelation diagnosticsForRelation = storage.getDiagnosticRelationItems();
		Assert.assertEquals(true, diagnosticsForRelation.hasRelationsForInstane(instanceA));
		Assert.assertEquals(10, diagnosticsForRelation.relationsForInstance(instanceA).size());
		
		for(SimulatedInstanceIdentifier instanceB : instanceBList)
		{
			Assert.assertEquals(1, storage.getRelationsForInstance(instanceB).size() );
			
			Assert.assertEquals(true, diagnosticsForRelation.hasRelationsForInstane(instanceB));
			Assert.assertEquals(1, diagnosticsForRelation.relationsForInstance(instanceB).size());
			Assert.assertEquals(instanceA, diagnosticsForRelation.relationsForInstance(instanceB).get(0).getRelatedInstance());
			
		}
	}

	public void test_can_list_related_instances_from_multiple_relations_for_instance()
	{
		MockSimulatedClass hotel = new MockSimulatedClass("Hotel");
		MockSimulatedClass room = new MockSimulatedClass("Room");
		MockSimulatedClass guest = new MockSimulatedClass("Guest");
		
		final SimulatedRelationship r_RoomGuest = new SimulatedRelationship();
		r_RoomGuest.setClassA(room);
		r_RoomGuest.setClassB(guest);
		
		final SimulatedRelationship r_RoomHotel = new SimulatedRelationship();
		r_RoomHotel.setClassA(room);
		r_RoomHotel.setClassB(hotel);
		
		final SimulatedRelationship r_HotelGuest = new SimulatedRelationship();
		r_HotelGuest.setClassA(guest);
		r_HotelGuest.setClassB(hotel);
		
		NonReflexiveRelationshipStorage s_RoomGuest = new NonReflexiveRelationshipStorage(r_RoomGuest);
		r_RoomGuest.setRelationshipStorage(s_RoomGuest);
		NonReflexiveRelationshipStorage s_RoomHotel = new NonReflexiveRelationshipStorage(r_RoomHotel);
		r_RoomHotel.setRelationshipStorage(s_RoomHotel);
		NonReflexiveRelationshipStorage s_HotelGuest = new NonReflexiveRelationshipStorage(r_HotelGuest);
		r_HotelGuest.setRelationshipStorage(s_HotelGuest);
		
		SimulatedInstanceIdentifier room_01 = new SimulatedInstanceIdentifier(0, room);
		SimulatedInstanceIdentifier guest_01 = new SimulatedInstanceIdentifier(0, guest);
		SimulatedInstanceIdentifier guest_02 = new SimulatedInstanceIdentifier(1, guest);
		SimulatedInstanceIdentifier hotel_01 = new SimulatedInstanceIdentifier(0, hotel);
		SimulatedInstanceIdentifier hotel_02 = new SimulatedInstanceIdentifier(2, hotel);
		
		s_RoomGuest.storeRelationInstance(r_RoomGuest.createInstance().relateNonReflexiveInstance(room_01).relateNonReflexiveInstance(guest_01));
		s_RoomGuest.storeRelationInstance(r_RoomGuest.createInstance().relateNonReflexiveInstance(room_01).relateNonReflexiveInstance(guest_02));
		s_RoomHotel.storeRelationInstance(r_RoomHotel.createInstance().relateNonReflexiveInstance(room_01).relateNonReflexiveInstance(hotel_01));
		s_HotelGuest.storeRelationInstance(r_HotelGuest.createInstance().relateNonReflexiveInstance(guest_01).relateNonReflexiveInstance(hotel_01));
		
		ArrayList<SimulatedRelationship> relationships = new ArrayList<SimulatedRelationship>(){
			private static final long serialVersionUID = 1L;
		{add(r_RoomGuest); add(r_RoomHotel); add(r_HotelGuest);}};
		
		RelationshipDiagnostics relationshipDiagnostics = new RelationshipDiagnostics();
		relationshipDiagnostics.calculateRelationships(relationships);

		Assert.assertEquals(true, relationshipDiagnostics.hasRelationsForInstance(room_01));
		Assert.assertEquals(true, relationshipDiagnostics.hasRelationsForInstance(guest_01));
		Assert.assertEquals(true, relationshipDiagnostics.hasRelationsForInstance(guest_02));
		Assert.assertEquals(true, relationshipDiagnostics.hasRelationsForInstance(hotel_01));
		Assert.assertEquals(false, relationshipDiagnostics.hasRelationsForInstance(hotel_02));
		
		DiagnosticRelaionForInstance room_01_diag = relationshipDiagnostics.getRelationsForInstance(room_01);
		Assert.assertEquals(true, room_01_diag.hasRelationsOverRelation(r_RoomGuest));
		Assert.assertEquals(true, room_01_diag.hasRelationsOverRelation(r_RoomHotel));
		Assert.assertEquals(false, room_01_diag.hasRelationsOverRelation(r_HotelGuest));
		
		Assert.assertEquals(2, room_01_diag.getRelationsOverRelation(r_RoomGuest).size());
		
		Assert.assertEquals(true, room_01_diag.getRelationsOverRelation(r_RoomGuest).get(0).getRelatedInstance().equals(guest_01) | room_01_diag.getRelationsOverRelation(r_RoomGuest).get(1).getRelatedInstance().equals(guest_01) );
		Assert.assertEquals(true, room_01_diag.getRelationsOverRelation(r_RoomGuest).get(0).getRelatedInstance().equals(guest_02) | room_01_diag.getRelationsOverRelation(r_RoomGuest).get(1).getRelatedInstance().equals(guest_02) );
		
		Assert.assertEquals(1, room_01_diag.getRelationsOverRelation(r_RoomHotel).size());
		Assert.assertEquals(hotel_01, room_01_diag.getRelationsOverRelation(r_RoomHotel).get(0).getRelatedInstance());

	}
	
}
