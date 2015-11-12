ClassHierarchyTest = TestCase("ClassHierarchyTest");

ClassHierarchyTest.prototype.testNewRelation = function(){
	window.vm = new ViewModel(1);
	ko.applyBindings(window.vm);
	var domain = window.vm.domain;
	
	
	var vehicle = domain.createClass();
	var car = domain.createClass();
	var bus = domain.createClass();

	vehicle.name("vehicle");
	car.name("car");
	bus.name("bus");
	
	assertEquals(false, car.hasSuperClass());
	assertEquals(false, bus.hasSuperClass());
	assertEquals(false, vehicle.hasSubClasses());

	car.setSuperClass(vehicle);
	bus.setSuperClass(vehicle);
	
	assertEquals(true, car.hasSuperClass());
	assertEquals(true, bus.hasSuperClass());
	assertEquals(true, vehicle.hasSubClasses());
	
	bus.clearSuperClass();
	assertEquals(true, car.hasSuperClass());
	assertEquals(false, bus.hasSuperClass());
	assertEquals(true, vehicle.hasSubClasses());
};
