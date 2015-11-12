StateMachineTest = TestCase("StateMachineTest");

StateMachineTest.prototype.testNewStateGetsUniqueUUID = function(){
	var state1 = new StateDiagram_Entities.EntityState();
	var state2 = new StateDiagram_Entities.EntityState();
	assertNotEquals(state1.uuid, state2.uuid);
};

StateMachineTest.prototype.testNewStateGetsExpectedName = function(){
	var state1 = new StateDiagram_Entities.EntityState();
	assertEquals("State_" + state1.uuid, state1.name());
};

StateMachineTest.prototype.testCanAddStateToClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	assertEquals(0, newClass.stateMachine.states().length);
	var newState = newClass.stateMachine.createState();
	assertEquals(1, newClass.stateMachine.states().length);
	assertEquals(newState.uuid, newClass.stateMachine.states()[0].uuid);
};


StateMachineTest.prototype.testCanDeleteStateFromClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	assertEquals(0, newClass.stateMachine.states().length);
	var newState = newClass.stateMachine.createState();
	assertEquals(1, newClass.stateMachine.states().length);
	newClass.stateMachine.deleteState(newState);
	assertEquals(0, newClass.stateMachine.states().length);
};

StateMachineTest.prototype.testCanAddSpecificationToClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var oldSpecCount = newClass.stateMachine.specifications().length;
	var newSpecification = newClass.stateMachine.createSpecification();
	var newSpecCount = newClass.stateMachine.specifications().length;
	assertEquals(newSpecCount, oldSpecCount + 1);
};

StateMachineTest.prototype.testSubsequentSpecificationsGetUniqueUUIDS = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newSpecification1 = newClass.stateMachine.createSpecification();
	var newSpecification2 = newClass.stateMachine.createSpecification();
	assertNotEquals(newSpecification1.uuid, newSpecification2.uuid);
};

StateMachineTest.prototype.testCanAddEventInstanceToClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newSpecification = newClass.stateMachine.createSpecification();
	newSpecification.name("DefaultEventSpecification");
	assertEquals(0, newClass.stateMachine.instances().length);
	var newInstance = newClass.stateMachine.createInstance();
	assertEquals(1, newClass.stateMachine.instances().length);
	assertEquals("DefaultEventSpecification", newInstance.specification().name());
};

StateMachineTest.prototype.testCanSetFromAndToStateOnInstance = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	
	var fromState = newClass.stateMachine.createState();
	var toState = newClass.stateMachine.createState();
	
	//var newSpecification = newClass.stateMachine.createSpecification();
	assertEquals(0, newClass.stateMachine.instances().length);
	var newInstance = newClass.stateMachine.createInstance();
	newInstance.fromState(fromState);
	newInstance.toState(toState);
	
	assertEquals(fromState, newInstance.fromState());
	assertEquals(toState, newInstance.toState());
};


StateMachineTest.prototype.testCanAddParamToSpecification = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var spec = newClass.stateMachine.createSpecification();
	var paramLength = spec.params().length;
	var param1 = spec.createParam();
	var param2 = spec.createParam();
	assertEquals(paramLength + 2 , spec.params().length);
	assertNotEquals(param1.uuid, param2.uuid);
	assertNotEquals(param1.name(), param2.name());
	assertEquals(spec, param1.spec());
	assertEquals(spec, param2.spec());

};

StateMachineTest.prototype.testCantEditDefaultEventSpec = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var defaultSpec = newClass.stateMachine.createSpecification();
	defaultSpec.name("DefaultEventSpecification");
	var newSpecification = newClass.stateMachine.createSpecification();
	assertEquals(false, defaultSpec.isEditable());
	assertEquals(true, newSpecification.isEditable());
};