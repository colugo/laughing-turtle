WhenAddingAnEntityClassTest = TestCase("WhenAddingAnEntityClassTest");

WhenAddingAnEntityClassTest.prototype.testNewEntityDomainHas0Classes = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	assertEquals(0, domain.entityClasses().length);
};

WhenAddingAnEntityClassTest.prototype.testAddingNewEntityClassIncreasesEntityDomainClassCountBy1 = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	assertEquals(0, domain.entityClasses().length);
	domain.createClass();
	assertEquals(1, domain.entityClasses().length);
};

WhenAddingAnEntityClassTest.prototype.testCanSetEntityClassName = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.name("Fred");
	assertEquals(newClass.name(), "Fred");
};

WhenAddingAnEntityClassTest.prototype.testCanGetEntityClassWithNameFromDomain = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var class01 = domain.createClass();
	var class02 = domain.createClass();
	assertEquals(class01,domain.getClassWithUUID(class01.uuid));
	assertEquals(class02,domain.getClassWithUUID(class02.uuid));
};