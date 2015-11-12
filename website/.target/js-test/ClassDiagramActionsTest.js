ActionTests = TestCase("ActionTests");

ActionTests.prototype.testInitialActionListIsEmpty = function(){
	var actionList = new ClassDiagram_Actions.ActionList();
	assertEquals(0, actionList.actions().length);
};

ActionTests.prototype.testAddClassActionWillCreateClass = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var action = new ClassDiagram_Actions.NewClassAction();
	assertEquals(0, domain.entityClasses().length);
	action.apply(domain);
	assertEquals(1, domain.entityClasses().length);
};

ActionTests.prototype.testCanCreateAndUndoClass = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var newClassAction = new ClassDiagram_Actions.NewClassAction();
	assertEquals(0, domain.entityClasses().length);
	assertEquals(0, actionList.actions().length);
	actionList.apply(newClassAction);
	assertEquals(1, domain.entityClasses().length);
	assertEquals(1, actionList.actions().length);
	actionList.undo();
	assertEquals(0, domain.entityClasses().length);
	assertEquals(0, actionList.actions().length);
};



ActionTests.prototype.testCanRenameNewClass = function() {
	window.vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var newClass = domain.createClass();
	var newClassUuid = newClass.uuid;
	var newNewClassName = "Fred";
	var action = new ClassDiagram_Actions.RenameClass(newClassUuid, newNewClassName);
	action.apply(domain);
	assertEquals(newNewClassName, newClass.name());
};

ActionTests.prototype.testCanCreateAndRenameNewClass = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var newClassAction = new ClassDiagram_Actions.NewClassAction();
	actionList.apply(newClassAction);
	var theClass = domain.entityClasses()[0];
	var renameClassAction = new ClassDiagram_Actions.RenameClass(theClass.uuid, "Frank");
	actionList.apply(renameClassAction);
	assertEquals("Frank", theClass.name());
};

ActionTests.prototype.testCanCreateAndRenameNewAndUndoRenameClass = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var newClassAction = new ClassDiagram_Actions.NewClassAction();
	actionList.apply(newClassAction);
	var theClass = domain.entityClasses()[0];
	var theOldName = theClass.name();
	var renameClassAction = new ClassDiagram_Actions.RenameClass(theClass.uuid, "Frank");
	actionList.apply(renameClassAction);
	assertEquals("Frank", theClass.name());
	actionList.undo();
	assertEquals(theOldName, theClass.name());
};

ActionTests.prototype.testCanRelateClasses = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var guest = domain.createClass();
	assertEquals(0, domain.entityRelations().length);
	
	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	actionList.apply(newRelationAction);
	
	assertEquals(1, domain.entityRelations().length);
	
	var theRelation = domain.entityRelations()[0];
	assertEquals(room, theRelation.endA());
	assertEquals(guest, theRelation.endB());
	assertEquals(false, theRelation.isReflexive());
};

ActionTests.prototype.testCanGetClassNamesOnNewRelation = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var guest = domain.createClass();
	room.name("Room");
	guest.name("Guest");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	actionList.apply(newRelationAction);
	
	var theRelation = domain.entityRelations()[0];
	assertEquals("Room", theRelation.classAName());
	assertEquals("Guest", theRelation.classBName());
};


ActionTests.prototype.testDeleteClassActionWillDeleteClass = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	assertEquals(1, domain.entityClasses().length);
	var deleteAction = new ClassDiagram_Actions.DeleteClassAction(newClass.uuid);
	deleteAction.apply(domain);
	assertEquals(0, domain.entityClasses().length);
};

ActionTests.prototype.testUndoDeleteClassActionWillUndoDeleteClass = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	assertEquals(1, domain.entityClasses().length);
	var deleteAction = new ClassDiagram_Actions.DeleteClassAction(newClass.uuid);
	deleteAction.apply(domain);
	assertEquals(0, domain.entityClasses().length);
	assertEquals(null, domain.getClassWithUUID(newClassUUID));
	deleteAction.undo(domain);
	assertEquals(1, domain.entityClasses().length);
	assertNotNull(domain.getClassWithUUID(newClassUUID));
};

ActionTests.prototype.testDeleteClassActionWillDeleteAllRelations = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var endA = domain.createClass();
	var endB = domain.createClass();
	assertEquals(2, domain.entityClasses().length);
	
	var r1 = new ClassDiagram_Entities.EntityRelation(endA, endB);
	domain.entityRelations().push(r1);
	assertEquals(1, domain.entityRelations().length);
	assertEquals(endA, r1.endA());
	assertEquals(endB, r1.endB());
	
	var deleteAction = new ClassDiagram_Actions.DeleteClassAction(endA.uuid);
	deleteAction.apply(domain);
	assertEquals(1, domain.entityClasses().length);
	assertEquals(0, domain.entityRelations().length);
	
	deleteAction.undo(domain);
	assertEquals(1, domain.entityRelations().length);
	assertEquals(2, domain.entityClasses().length);	
};

ActionTests.prototype.testDeleteClassActionWillDeleteAllSubClassRelations = function() {
	window.vm = new ViewModel();
	var domain = window.vm.domain;
	var endA = domain.createClass();
	var endB = domain.createClass();
	
	endB.superClass(endA);
	assertEquals(endA, endB.getSuperClass());
	assertEquals(true, endA.hasSubClasses());
	assertEquals(true, endB.hasSuperClass());
	
	assertEquals(2, domain.entityClasses().length);
	var deleteAction = new ClassDiagram_Actions.DeleteClassAction(endA.uuid);
	deleteAction.apply(domain);
	assertEquals(1, domain.entityClasses().length);

	assertEquals(false, endA.hasSubClasses());
	assertEquals(false, endB.hasSuperClass());
	
	deleteAction.undo(domain);
	
	assertEquals(endA, endB.getSuperClass());
	assertEquals(true, endA.hasSubClasses());
	assertEquals(true, endB.hasSuperClass());
};

ActionTests.prototype.testCanAddAttributeToClassAction = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	
	var newAttributeAction = new ClassDiagram_Actions.AddAttributeAction(newClass);
	newAttributeAction.apply(domain);
	
	assertEquals(true, newClass.hasAttributes());
	assertEquals(1, newClass.attributes().length);
	
	newAttributeAction.undo(domain);
	
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
};


ActionTests.prototype.testCanDeleteAttributeFromClassAction = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	
	var newAttributeAction = new ClassDiagram_Actions.AddAttributeAction(newClass);
	newAttributeAction.apply(domain);
	
	assertEquals(true, newClass.hasAttributes());
	assertEquals(1, newClass.attributes().length);
	
	var deleteAttributeAction = new ClassDiagram_Actions.DeleteAttributeAction(newClass, newClass.attributes()[0]);
	deleteAttributeAction.apply(domain);
		
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	
	deleteAttributeAction.undo(domain);
	
	assertEquals(true, newClass.hasAttributes());
	assertEquals(1, newClass.attributes().length);
};

ActionTests.prototype.testCanRenameAttributeAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	var newAttributeName = "Age";
	
	var newAttributeAction = new ClassDiagram_Actions.AddAttributeAction(newClass);
	newAttributeAction.apply(domain);
	var attribute = newAttributeAction.newAttribute;
	
	assertNotEquals(newAttributeName, attribute.name());
	
	var renameAttributeAction = new ClassDiagram_Actions.RenameAttributeAction(newClassUUID, attribute, newAttributeName);
	renameAttributeAction.apply(domain);
	
	assertEquals(newAttributeName, attribute.name());
	
	renameAttributeAction.undo(domain);
	
	assertNotEquals(newAttributeName, attribute.name());
};


ActionTests.prototype.testCanChangeAttributeTypeAction = function() {
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	var newAttributeName = "Age";
	
	var newAttributeAction = new ClassDiagram_Actions.AddAttributeAction(newClass);
	newAttributeAction.apply(domain);
	var attribute = newAttributeAction.newAttribute;
	assertEquals('string', attribute.type());
	
	var changeAttributeTypeAction = new ClassDiagram_Actions.ChangeAttributeTypeAction(newClassUUID, attribute, 'boolean');
	changeAttributeTypeAction.apply(domain);
	assertEquals('boolean', attribute.type());
	
	changeAttributeTypeAction.undo(domain);
	assertEquals('string', attribute.type());
};


ActionTests.prototype.testCanChangeClassAOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, dummy.uuid, 'leads', '0..*', guest.uuid, 'follows', '0..*');
	changeRelationAction.apply(domain);
	assertEquals("Dummy", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};

ActionTests.prototype.testCanChangeClassAVerbOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, room.uuid, 'verbAChanged', '0..*', guest.uuid, 'follows', '0..*');
	changeRelationAction.apply(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("verbAChanged", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};

ActionTests.prototype.testCanChangeClassACardinalityOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, room.uuid, 'leads', '0..1', guest.uuid, 'follows', '0..*');
	changeRelationAction.apply(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..1", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};


ActionTests.prototype.testCanChangeClassBOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, room.uuid, 'leads', '0..*', dummy.uuid, 'follows', '0..*');
	changeRelationAction.apply(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Dummy", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};

ActionTests.prototype.testCanChangeClassBVerbOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, room.uuid, 'leads', '0..*', guest.uuid, 'verbBChanged', '0..*');
	changeRelationAction.apply(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("verbBChanged", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};

ActionTests.prototype.testCanChangeClassBCardinalityOnRelation = function(){
	var vm = new ViewModel(1);
	var domain = vm.domain;
	ko.applyBindings(vm);
	var room = domain.createClass();
	var guest = domain.createClass();
	var dummy = domain.createClass();
	room.name("Room");
	guest.name("Guest");
	dummy.name("Dummy");

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	
	var r1 = domain.entityRelations()[0];
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
	
	var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(r1.uuid, room.uuid, 'leads', '0..*', guest.uuid, 'follows', '0..1');
	changeRelationAction.apply(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..1", r1.cardinalityB());
	
	changeRelationAction.undo(domain);
	assertEquals("Room", r1.classAName());
	assertEquals("leads", r1.verbA());
	assertEquals("0..*", r1.cardinalityA());
	assertEquals("Guest", r1.classBName());
	assertEquals("follows", r1.verbB());
	assertEquals("0..*", r1.cardinalityB());
};


ActionTests.prototype.testCanRenameRelationAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var room = domain.createClass();
	var guest = domain.createClass();

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	var relation = newRelationAction.theRelation;
	
	var renameRelationAction = new ClassDiagram_Actions.RenameRelationAction(relation.uuid, "R1");
	renameRelationAction.apply(domain);
	
	assertEquals("R1", relation.name());
	
	renameRelationAction.undo(domain);
	
	assertNotEquals("R1", relation.name());
};

ActionTests.prototype.testCanDeleteRelationAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var room = domain.createClass();
	var guest = domain.createClass();

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	assertEquals(1, domain.entityRelations().length);
	var relation = newRelationAction.theRelation;
	
	var deleteRelationAction = new ClassDiagram_Actions.DeleteRelationAction(relation.uuid);
	deleteRelationAction.apply(domain);
	assertEquals(0, domain.entityRelations().length);
	
	deleteRelationAction.undo(domain);
	assertEquals(1, domain.entityRelations().length);
};

ActionTests.prototype.testCanCreateAssociationAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var room = domain.createClass();
	var guest = domain.createClass();
	var booking = domain.createClass();

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	assertEquals(1, domain.entityRelations().length);
	var r1 = newRelationAction.theRelation;
	
	
	assertFalse(r1.hasAssociation());
	assertFalse(booking.isAssociation());
	
	window.vm.modelSelection.select(r1);
	var createAssociationAction = new ClassDiagram_Actions.CreateAssociationAction(booking.uuid);
	createAssociationAction.apply(domain);
	window.vm.modelSelection.deselect();
	
	assertTrue(r1.hasAssociation());
	assertTrue(booking.isAssociation());
	assertEquals(booking, r1.associationClass());
	
	createAssociationAction.undo(domain);
	
	assertFalse(r1.hasAssociation());
	assertFalse(booking.isAssociation());
};

ActionTests.prototype.testCanSetAssociationClassToNoneAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var room = domain.createClass();
	var guest = domain.createClass();
	var booking = domain.createClass();
	
	var unselected = new ClassDiagram_Entities.EntityClass();
	unselected.name("None");
	unselected.uuid = undefined;

	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(room.uuid, guest.uuid);
	newRelationAction.apply(domain);
	assertEquals(1, domain.entityRelations().length);
	var r1 = newRelationAction.theRelation;
	r1.setAssociation(booking);

	assertTrue(r1.hasAssociation());
	assertTrue(booking.isAssociation());
	assertEquals(booking, r1.associationClass());

	window.vm.modelSelection.select(r1);
	var createAssociationAction = new ClassDiagram_Actions.CreateAssociationAction(unselected.uuid);
	createAssociationAction.apply(domain);
	window.vm.modelSelection.deselect();
	
	
	assertFalse(r1.hasAssociation());
	assertFalse(booking.isAssociation());
	assertFalse(unselected.isAssociation());
	assertNull(r1.associationClass());
	
	createAssociationAction.undo(domain);
	
	assertTrue(r1.hasAssociation());
	assertTrue(booking.isAssociation());
	assertEquals(booking, r1.associationClass());
};

ActionTests.prototype.testCanSetSuperClassAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var vehicle = domain.createClass();
	var bus = domain.createClass();

	assertEquals(false, vehicle.hasSubClasses());
	assertEquals(false, bus.hasSuperClass());
	
	window.vm.modelSelection.select(bus);
	var changeSuperClassAction = new ClassDiagram_Actions.ChangeSuperClassAction(vehicle.uuid);
	changeSuperClassAction.apply(domain);
	window.vm.modelSelection.deselect();
	
	assertEquals(true, vehicle.hasSubClasses());
	assertEquals(true, bus.hasSuperClass());
	assertEquals(vehicle, bus.getSuperClass());
	
	changeSuperClassAction.undo(domain);
	
	assertEquals(false, vehicle.hasSubClasses());
	assertEquals(false, bus.hasSuperClass());
};

ActionTests.prototype.testCanClearSetSuperClassAction = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var vehicle = domain.createClass();
	var bus = domain.createClass();
	bus.setSuperClass(vehicle);
	
	assertEquals(true, vehicle.hasSubClasses());
	assertEquals(true, bus.hasSuperClass());
	
	window.vm.modelSelection.select(bus);
	var changeSuperClassAction = new ClassDiagram_Actions.ChangeSuperClassAction(undefined);
	changeSuperClassAction.apply(domain);
	window.vm.modelSelection.deselect();
	
	assertEquals(false, vehicle.hasSubClasses());
	assertEquals(false, bus.hasSuperClass());
	assertEquals(null, bus.getSuperClass());
	
	changeSuperClassAction.undo(domain);
	
	assertEquals(true, vehicle.hasSubClasses());
	assertEquals(true, bus.hasSuperClass());
};


