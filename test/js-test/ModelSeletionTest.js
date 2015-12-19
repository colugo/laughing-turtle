ModelSelectionTest = TestCase("ModelSelectionTest");

ModelSelectionTest.prototype.testModelSelectionCanHaveSingleObjectSelected = function() {
	var modelSelection = new ModelSelection();
	ko.applyBindings(modelSelection);
	assertEquals(false, modelSelection.isOneThingSelectedInner());
	modelSelection.select(new ClassDiagram_Entities.EntityClass());
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	assertEquals("EntityClass", modelSelection.typeOfOneThingSelected());
};

ModelSelectionTest.prototype.testModelSelectionStopsHavingSingleObjectSelectedWhenMoreThenOneThingSelected = function() {
	var modelSelection = new ModelSelection();
	assertEquals(false, modelSelection.isOneThingSelectedInner());
	modelSelection.select("bob");
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	modelSelection.select(7);
	assertEquals(false, modelSelection.isOneThingSelectedInner());
};


ModelSelectionTest.prototype.testModelSelectionHasPropertyForTheSelectedClassOrTheClassOfASelectedStateOrTheClassOfASelectedEventInstance = function() {
	var modelSelection = new ModelSelection();
	ko.applyBindings(modelSelection);
	assertEquals(false, modelSelection.isOneThingSelectedInner());
	
	var aClass = new ClassDiagram_Entities.EntityClass();
	var bClass = new ClassDiagram_Entities.EntityClass();
	assertEquals(false, bClass.stateMachine.hasStates());
	var bState = bClass.stateMachine.createState();
	var bInstance = bClass.stateMachine.createInstance();
	assertEquals(true, bClass.stateMachine.hasStates());
	
	modelSelection.select(aClass);
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	assertEquals("EntityClass", modelSelection.typeOfOneThingSelected());
	assertEquals(aClass, modelSelection.classWhenClasssOrStateSelected());
	
	modelSelection.deselect();
	
	modelSelection.select(bClass);
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	assertEquals("EntityClass", modelSelection.typeOfOneThingSelected());
	assertEquals(bClass, modelSelection.classWhenClasssOrStateSelected());
	
	modelSelection.deselect();
	
	modelSelection.select(bState);
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	assertEquals("EntityState", modelSelection.typeOfOneThingSelected());
	assertEquals(bClass, modelSelection.classWhenClasssOrStateSelected());
	
	modelSelection.deselect();
	
	modelSelection.select(bInstance);
	assertEquals(true, modelSelection.isOneThingSelectedInner());
	assertEquals("EventInstance", modelSelection.typeOfOneThingSelected());
	assertEquals(bClass, modelSelection.classWhenClasssOrStateSelected());
};