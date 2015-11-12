WhenDeletingAnEntityClassTest = TestCase("WhenDeletingAnEntityClassTest");

WhenDeletingAnEntityClassTest.prototype.testAddingNewEntityClassIncreasesEntityDomainClassCountBy1 = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	assertEquals(0, domain.entityClasses().length);
	var newClass = domain.createClass();
	assertEquals(1, domain.entityClasses().length);
	domain.deleteClass(newClass.uuid);
	assertEquals(0, domain.entityClasses().length);
};
