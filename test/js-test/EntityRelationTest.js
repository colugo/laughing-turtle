EntityRelationTest = TestCase("EntityRelationTest");

EntityRelationTest.prototype.testNewRelation = function(){
	var room = new ClassDiagram_Entities.EntityClass();
	var guest = new ClassDiagram_Entities.EntityClass();
	var dummy = new ClassDiagram_Entities.EntityClass();
	var r1 = new ClassDiagram_Entities.EntityRelation(room, guest);
	assertEquals(room, r1.endA());
	assertEquals(guest, r1.endB());
	assertEquals(false, r1.isReflexive());
	
	assertTrue(r1.isClassInvolved(room));
	assertTrue(r1.isClassInvolved(guest));
	assertFalse(r1.isClassInvolved(dummy));
	
	assertEquals(guest, r1.getOtherEnd(room));
	assertEquals(room, r1.getOtherEnd(guest));
};

EntityRelationTest.prototype.testNewRelationIsReflexive = function(){
	var room = new ClassDiagram_Entities.EntityClass();
	var r1 = new ClassDiagram_Entities.EntityRelation(room, room);
	assertEquals(room, r1.endA());
	assertEquals(room, r1.endB());
	assertEquals(true, r1.isReflexive());
	
	assertTrue(r1.isClassInvolved(room));
};

EntityRelationTest.prototype.testRelationCanHaveAssociationClass = function(){
	window.vm = new ViewModel(1);
	ko.applyBindings(vm);
	var domain = vm.domain;
	var room = domain.createClass();
	var guest = domain.createClass();
	var booking = domain.createClass();
	
	room.uuid = "room";
	guest.uuid = "guest";
	booking.uuid = "booking";
	
	var r1 = new ClassDiagram_Entities.EntityRelation(room, guest);
	domain.entityRelations().push(r1);
	assertEquals(room, r1.endA());
	assertEquals(guest, r1.endB());

	assertFalse(r1.hasAssociation());
	assertFalse(booking.isAssociation());
	
	r1.setAssociation(booking);
	
	assertTrue(r1.hasAssociation());
	assertTrue(booking.isAssociation());
	assertEquals(booking, r1.associationClass());
	
	r1.clearAssociation();
	
	assertFalse(r1.hasAssociation());
	assertFalse(booking.isAssociation());
	
};


EntityRelationTest.prototype.testDomainCanDetermineWhatRelationsAClassIsIn = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	
	var r1 = new ClassDiagram_Entities.EntityRelation(room, guest);
	domain.entityRelations().push(r1);
	r1.name("R1");
	var r2 = new ClassDiagram_Entities.EntityRelation(room, dummy);
	domain.entityRelations().push(r2);
	r2.name("R2");
	var r3 = new ClassDiagram_Entities.EntityRelation(dummy, guest);
	domain.entityRelations().push(r3);
	r3.name("R3");

	assertEquals(3, domain.entityRelations().length);
	assertEquals(2, domain.getRelationsInvolvingClass(room).length);
	assertEquals("R1", domain.getRelationsInvolvingClass(room)[0].name());
	assertEquals("R2", domain.getRelationsInvolvingClass(room)[1].name());
};
