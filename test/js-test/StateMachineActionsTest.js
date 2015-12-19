StateActionTests = TestCase("StateActionTests");

StateActionTests.prototype.testCanAddState = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newClassUUID = newClass.uuid;
	
	assertEquals(false, newClass.stateMachine.hasStates());
	assertEquals(0, newClass.stateMachine.states().length);
	
	var newStateAction = new StateDiagram_Actions.AddStateAction(newClass);
	newStateAction.apply(domain);
	
	assertEquals(true, newClass.stateMachine.hasStates());
	assertEquals(1, newClass.stateMachine.states().length);
	
	newStateAction.undo(domain);
	
	assertEquals(false, newClass.stateMachine.hasStates());
	assertEquals(0, newClass.stateMachine.states().length);
};


StateActionTests.prototype.testCanCreateAndRenameNewAndUndoRenameState = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var newClassAction = new ClassDiagram_Actions.NewClassAction();
	actionList.apply(newClassAction);
	var theClass = domain.entityClasses()[0];
	var newStateAction = new StateDiagram_Actions.AddStateAction(theClass);
	actionList.apply(newStateAction);
	var theState = theClass.stateMachine.states()[0];
	
	var theOldName = theState.name();
	
	var renameStateAction = new StateDiagram_Actions.RenameState(theState.uuid, "Frank");
	actionList.apply(renameStateAction);
	assertEquals("Frank", theState.name());
	actionList.undo();
	assertEquals(theOldName, theState.name());
};

StateActionTests.prototype.testCanCreateStateTransition = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	
	assertEquals(0, room.stateMachine.states().length);
	var fromState = room.stateMachine.createState();
	var toState = room.stateMachine.createState();
	assertEquals(2, room.stateMachine.states().length);
	
	assertEquals(0, room.stateMachine.instances().length);
	
	var newTransition = new StateDiagram_Actions.NewStateTransitionAction(fromState.uuid, toState.uuid);
	actionList.apply(newTransition);
	
	assertEquals(1, room.stateMachine.instances().length);
	
	actionList.undo();
	
	assertEquals(0, room.stateMachine.instances().length);
};

StateActionTests.prototype.testCanChangeStateTransition = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var fromState = room.stateMachine.createState();
	var toState = room.stateMachine.createState();
	
	var defaultSpecification = room.stateMachine.createSpecification();
	defaultSpecification.name("DefaultEventSpecification");
	
	assertEquals(defaultSpecification, room.stateMachine.getDefaultEventSpecification());
	
	var newSpecification = room.stateMachine.createSpecification();
	
	assertEquals(2, room.stateMachine.states().length);
	var newTransitionAction = new StateDiagram_Actions.NewStateTransitionAction(fromState.uuid, toState.uuid);
	actionList.apply(newTransitionAction);
	var newTransition = newTransitionAction.theInstance;
	
	assertEquals(newTransition.specification(), defaultSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
	// change spec
	var changeTransition = new StateDiagram_Actions.ChangeStateTransitionAction(newTransition.uuid, fromState.uuid, toState.uuid, newSpecification.uuid);
	actionList.apply(changeTransition);
	
	assertEquals(newTransition.specification(), newSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
	actionList.undo();
	
	assertEquals(newTransition.specification(), defaultSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
	
	// change from state
	changeTransition = new StateDiagram_Actions.ChangeStateTransitionAction(newTransition.uuid, toState.uuid, toState.uuid, newSpecification.uuid);
	actionList.apply(changeTransition);
	
	assertEquals(newTransition.specification(), newSpecification);
	assertEquals(newTransition.fromState(), toState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
	actionList.undo();
	
	assertEquals(newTransition.specification(), defaultSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
	// change to state
	changeTransition = new StateDiagram_Actions.ChangeStateTransitionAction(newTransition.uuid, fromState.uuid, fromState.uuid, newSpecification.uuid);
	actionList.apply(changeTransition);
	
	assertEquals(newTransition.specification(), newSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), fromState);
	assertEquals(1, room.stateMachine.instances().length);
	
	actionList.undo();
	
	assertEquals(newTransition.specification(), defaultSpecification);
	assertEquals(newTransition.fromState(), fromState);
	assertEquals(newTransition.toState(), toState);
	assertEquals(1, room.stateMachine.instances().length);
	
};



StateActionTests.prototype.testCanRenameEventSpec = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var defaultSpecification = room.stateMachine.createSpecification();
	defaultSpecification.name("DefaultEventSpecification");
	var newSpecification = room.stateMachine.createSpecification();
	var oldName = newSpecification.name();
	assertNotEquals(defaultSpecification.name(), oldName);
	
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var changeSpecNameAction = new StateDiagram_Actions.RenameSpecAction(newSpecification.uuid, room.uuid, "newName");
	var result = actionList.apply(changeSpecNameAction);
	assertNull(actionList.message());
	assertEquals(newSpecification.name(), "newName");

	changeSpecNameAction.undo(domain);
	assertEquals(newSpecification.name(), oldName);
	
	var changeSpecNameAction = new StateDiagram_Actions.RenameSpecAction(newSpecification.uuid, room.uuid, defaultSpecification.name());
	var result = actionList.apply(changeSpecNameAction);
	assertNotNull(actionList.message());
	assertEquals(newSpecification.name(), oldName);
	assertEquals("An event specification is already named 'DefaultEventSpecification'", actionList.message());
};


StateActionTests.prototype.testCanCreateAndUndoParam = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var spec = room.stateMachine.createSpecification();
	var newParamAction = new StateDiagram_Actions.AddParamAction(spec);
	var oldParamCount = spec.params().length;
	actionList.apply(newParamAction);
	assertEquals(oldParamCount + 1, spec.params().length);
	actionList.undo();
	assertEquals(oldParamCount, spec.params().length);
};

StateActionTests.prototype.testCanRenameEventParam = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var defaultSpec = room.stateMachine.createSpecification();
	var param1 = defaultSpec.createParam();
	var oldName = param1.name();
	var param2 = defaultSpec.createParam();
	
	assertNotEquals(param1.name(), param2.name());
	
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var changeParamNameAction = new StateDiagram_Actions.RenameSpecParamAction(defaultSpec.uuid, room.uuid, param1.uuid, "newName");
	var result = actionList.apply(changeParamNameAction);
	assertNull(actionList.message());
	assertEquals(param1.name(), "newName");

	changeParamNameAction.undo(domain);
	assertEquals(param1.name(), oldName);
	
	var changeParamNameAction = new StateDiagram_Actions.RenameSpecParamAction(defaultSpec.uuid, room.uuid, param1.uuid, param2.name());
	var result = actionList.apply(changeParamNameAction);
	assertNotNull(actionList.message());
	assertEquals(param1.name(), oldName);
	assertEquals("An event param is already named '"+param2.name()+"'", actionList.message());
};

StateActionTests.prototype.testCanDeleteEventParam = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var defaultSpec = room.stateMachine.createSpecification();
	var param1 = defaultSpec.createParam();

	assertEquals(defaultSpec.getParamWithUUID(param1.uuid), param1);
	
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var deleteParamAction = new StateDiagram_Actions.DeleteSpecParamAction(defaultSpec.uuid, room.uuid, param1.uuid);
	var result = actionList.apply(deleteParamAction);

	assertNotEquals(defaultSpec.getParamWithUUID(param1.uuid), param1);
	
	deleteParamAction.undo(domain);
	assertEquals(defaultSpec.getParamWithUUID(param1.uuid), param1);
};

StateActionTests.prototype.testChangeEventParamType = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var defaultSpec = room.stateMachine.createSpecification();
	var param1 = defaultSpec.createParam();
	var oldType = param1.type();
	var newType = "int";
	
	assertNotEquals(oldType, newType);
	
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var changeParamTypeAction = new StateDiagram_Actions.ChangeParamTypeAction(defaultSpec.uuid, room.uuid, param1.uuid, newType);
	var result = actionList.apply(changeParamTypeAction);
	assertNull(actionList.message());
	assertEquals(param1.type(), newType);

	changeParamTypeAction.undo(domain);
	assertEquals(param1.type(), oldType);
};

StateActionTests.prototype.testCanDeleteEventSpec = function() {
	window.vm = new ViewModel(1);
	var domain = window.vm.domain;
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var room = domain.createClass();
	var stateA = room.stateMachine.createState();
	var stateB = room.stateMachine.createState();
	var stateC = room.stateMachine.createState();
	
	var ab = room.stateMachine.createInstanceWithStates(stateA, stateB);
	var ac = room.stateMachine.createInstanceWithStates(stateA, stateC);
	var ca = room.stateMachine.createInstanceWithStates(stateC, stateA);
	
	var defaultSpecification = room.stateMachine.createSpecification();
	defaultSpecification.name("DefaultEventSpecification");
	defaultSpecification.uuid = "DefaultEventSpecification";
	
	var spec1 = room.stateMachine.createSpecification();
	spec1.uuid = "spec1";
	var spec2 = room.stateMachine.createSpecification();
	spec2.uuid = "spec2";
	
	assertEquals(3, room.stateMachine.specifications().length);
	
	ab.specification(defaultSpecification);
	ac.specification(spec1);
	ca.specification(spec2);
	
	assertEquals(defaultSpecification.uuid, ab.specification().uuid);
	assertEquals(spec1.uuid, ac.specification().uuid);
	assertEquals(spec2.uuid, ca.specification().uuid);
	
	var actionList = new ClassDiagram_Actions.ActionList(domain);
	var deleteSpecAction = new StateDiagram_Actions.DeleteSpecificationAction(spec1.uuid, room.uuid);
	var result = actionList.apply(deleteSpecAction);
	
	assertEquals(defaultSpecification.uuid, ab.specification().uuid);
	assertEquals(defaultSpecification.uuid, ac.specification().uuid);
	assertEquals(spec2.uuid, ca.specification().uuid);
	assertEquals(2, room.stateMachine.specifications().length);
	
	actionList.undo();
	
	assertEquals(defaultSpecification.uuid, ab.specification().uuid);
	assertEquals(spec1.uuid, ac.specification().uuid);
	assertEquals(spec2.uuid, ca.specification().uuid);
	assertEquals(3, room.stateMachine.specifications().length);
};