SystemTest = TestCase("SystemTest");

/*
 * get stdout logging with : 	jstestdriver.console.log(category, stuff);
 * 
 */

function getProjectId() {
	var strUrl = "/newDomain?domainName=testDomain", strReturn = "";

	$.ajax({
		url : strUrl,
		success : function(html) {
			strReturn = html;
		},
		error : function(html) {
			strReturn = html;
		},
		async : false
	});

	return strReturn;
}

function getVM(){
	var projectId = getProjectId();
	window.vm = new ViewModel(projectId);
	ko.applyBindings(window.vm);
	window.vm.load();
	
	return window.vm;
}


SystemTest.prototype.testCanProgramaticallyDoWorkInClient = function() {
	var vm = getVM();
	
	assertEquals(0, vm.actionList.actions().length);
	assertNull(vm.actionList.message());
	vm.newClassAction();
	assertEquals(1, vm.actionList.actions().length);
	vm.saveActions();
	
	// save was successful
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
};

SystemTest.prototype.testCanRenameClass = function() {
	var vm = getVM();
	
	vm.newClassAction();
	var newClass = vm.domain.entityClasses()[0];
	
	assertNotEquals("Frank", newClass.name());
	vm.renameClassAction(newClass, "Frank");
	vm.saveActions();

	// save was successful
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	
	assertEquals("Frank", newClass.name());
	
};

SystemTest.prototype.testCannotHaveTwoAttributesWithSameName = function() {
	var vm = getVM();
	
	vm.newClassAction();
	assertNull(vm.actionList.message());
	vm.saveActions();
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	var newClass = vm.domain.entityClasses()[0];
	
	vm.addAttributeAction(newClass);
	assertNull(vm.actionList.message());
	vm.saveActions();
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	var attribute1 = newClass.attributes()[0];
	
	vm.addAttributeAction(newClass);
	assertNull(vm.actionList.message());
	vm.saveActions();
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	var attribute2 = newClass.attributes()[1];

	assertNotEquals(attribute1.uuid, attribute2.uuid);
	assertNotEquals(attribute1.name(), attribute2.name());
	
	vm.renameAttributeAction(newClass.uuid, attribute1, "Age");
	assertNull(vm.actionList.message());
	vm.saveActions();
	// save was successful
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	
	vm.renameAttributeAction(newClass.uuid, attribute2, "Age");
	assertNotNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	vm.saveActions();

};


SystemTest.prototype.testCantHaveClassesWithSameName = function() {
	var vm = getVM();
	
	vm.newClassAction();
	var newClass = vm.domain.entityClasses()[0];
	
	assertNotEquals("Frank", newClass.name());
	vm.renameClassAction(newClass, "Frank");
	vm.saveActions();

	assertEquals("Success", vm.actionList.lastSaveResult);
	assertNull(vm.actionList.message());
	assertEquals(0, vm.actionList.actions().length);
	
	vm.newClassAction();
	var newClass1 = vm.domain.entityClasses()[1];
	vm.saveActions();
	
	vm.renameClassAction(newClass1, "Frank");
	assertNotNull(vm.actionList.message());
	vm.saveActions();
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertEquals(0, vm.actionList.actions().length);
	assertNotEquals("Frank", newClass1.name());
};

ActionTests.prototype.testCantHaveRelationsWithSameName = function() {
	var vm = getVM();
	vm.newClassAction();
	assertNull(vm.actionList.message());
	var room = vm.domain.entityClasses()[0];
	vm.newClassAction();
	assertNull(vm.actionList.message());
	var guest = vm.domain.entityClasses()[1];

	vm.modelSelection.select(room);
	room.newRelationUUID(guest.uuid);
	assertEquals(0, vm.domain.entityRelations().length);
	vm.createRelationAction(vm);
	assertNull(vm.actionList.message());
	assertEquals(1, vm.domain.entityRelations().length);
	
	var relation = vm.domain.entityRelations()[0];
	vm.renameRelationAction(relation.uuid, "R1");
	assertNull(vm.actionList.message());
	assertEquals("R1", relation.name());
	
	vm.createRelationAction(vm);
	assertNull(vm.actionList.message());
	assertEquals(2, vm.domain.entityRelations().length);
	var relation2 = vm.domain.entityRelations()[1];
	
	vm.renameRelationAction(relation2.uuid, "R1");
	assertNotNull(vm.actionList.message());
};

ActionTests.prototype.testCanDeleteRelation = function() {
	var vm = getVM();
	vm.newClassAction();
	assertNull(vm.actionList.message());
	var room = vm.domain.entityClasses()[0];
	vm.newClassAction();
	assertNull(vm.actionList.message());
	var guest = vm.domain.entityClasses()[1];

	vm.modelSelection.select(room);
	room.newRelationUUID(guest.uuid);
	assertEquals(0, vm.domain.entityRelations().length);
	vm.createRelationAction(vm);
	assertNull(vm.actionList.message());
	assertEquals(1, vm.domain.entityRelations().length);
	
	var relation = vm.domain.entityRelations()[0];
	vm.deleteRelationAction(relation);
	vm.saveActions();
	// save successful
	assertEquals("Success", vm.actionList.lastSaveResult);
	assertEquals(0, vm.actionList.actions().length);
	assertNull(vm.actionList.message());
};

ActionTests.prototype.testCanChangeAssociationClassAndThenUndo = function() {
	var vm = getVM();
	
	
	// Create 4 classes
	vm.newClassAction();
	assertNull(vm.actionList.message());
	assertEquals(1, vm.domain.entityClasses().length);
	var endA = vm.domain.entityClasses()[0];
	vm.newClassAction();
	assertNull(vm.actionList.message());
	assertEquals(2, vm.domain.entityClasses().length);
	var endB = vm.domain.entityClasses()[1];
	vm.newClassAction();
	assertNull(vm.actionList.message());
	assertEquals(3, vm.domain.entityClasses().length);
	var potentialAssociationA = vm.domain.entityClasses()[2];
	vm.newClassAction();
	assertNull(vm.actionList.message());
	assertEquals(4, vm.domain.entityClasses().length);
	var potentialAssociationB = vm.domain.entityClasses()[3];
		
	// Relate A-B
	var newRelationAction = new ClassDiagram_Actions.NewRelationAction(endA.uuid, endB.uuid);
	newRelationAction.apply(vm.domain);
	assertEquals(1,vm.domain.entityRelations().length);
	var AB = newRelationAction.theRelation;
	
	// Set potentialAssociationA as association of AB
	assertFalse(AB.hasAssociation());
	assertFalse(potentialAssociationA.isAssociation());
	assertFalse(potentialAssociationB.isAssociation());
	assertNull(AB.associationClass());
	
	window.vm.modelSelection.select(AB);
	var createAssociationAction = new ClassDiagram_Actions.CreateAssociationAction(potentialAssociationA.uuid);
	createAssociationAction.apply(vm.domain);
	window.vm.modelSelection.deselect();
	
	assertTrue(AB.hasAssociation());
	assertTrue(potentialAssociationA.isAssociation());
	assertFalse(potentialAssociationB.isAssociation());
	assertEquals(potentialAssociationA, AB.associationClass());
	
	// Change association class to potentialAssociationB
	window.vm.modelSelection.select(AB);
	var changeAssociationAction = new ClassDiagram_Actions.CreateAssociationAction(potentialAssociationB.uuid);
	changeAssociationAction.apply(vm.domain);
	window.vm.modelSelection.deselect();
	
	assertTrue(AB.hasAssociation());
	assertFalse(potentialAssociationA.isAssociation());
	assertTrue(potentialAssociationB.isAssociation());
	assertEquals(potentialAssociationB, AB.associationClass());
	
	// undo potentialAssociationB as association
	changeAssociationAction.undo(vm.domain);
	
	assertTrue(AB.hasAssociation());
	assertTrue(potentialAssociationA.isAssociation());
	assertFalse(potentialAssociationB.isAssociation());
	assertEquals(potentialAssociationA, AB.associationClass());
	
};


ActionTests.prototype.test_dont_allow_deselect_or_navigate_away_with_actionlanguage_changes = function() {
	assertTrue(false);
};

ActionTests.prototype.test_handle_refactoring_names_and_impacts_on_actionlanguage = function() {
	assertTrue(false);
};

ActionTests.prototype.test_handle_state_machines_for_generalisation_classes = function() {
	assertTrue(false);
};