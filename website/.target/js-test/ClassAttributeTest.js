WhenAddingAnAttributeTest = TestCase("WhenAddingAnAttributeTest");

WhenAddingAnAttributeTest.prototype.testCanCreateNewAttribute = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	var newAttribute = newClass.addAttribute(newAttribute);
	assertEquals(true, newClass.hasAttributes());
	assertEquals(1, newClass.attributes().length);
};

WhenAddingAnAttributeTest.prototype.testNewAttributesHaveDifferentNames = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newAttribute1 = new ClassDiagram_Entities.EntityAttribute(newClass);
	var newAttribute2 = new ClassDiagram_Entities.EntityAttribute(newClass);
	assertNotEquals(newAttribute1.name(), newAttribute2.name());
};

WhenAddingAnAttributeTest.prototype.testCanDeleteAttribute = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	var newAttribute = newClass.addAttribute(newAttribute);
	assertEquals(true, newClass.hasAttributes());
	assertEquals(1, newClass.attributes().length);
	assertTrue(newClass.hasAttributeWithName(newAttribute.name()));
	newClass.deleteAttribute(newAttribute);
	assertEquals(false, newClass.hasAttributes());
	assertEquals(0, newClass.attributes().length);
	assertFalse(newClass.hasAttributeWithName(newAttribute.name()));
};


WhenChangingAttributeDatatTypeTest = TestCase("WhenChangingAttributeDatatTypeTest");

WhenChangingAttributeDatatTypeTest.prototype.testDefaultTypeIsString = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newAttribute = newClass.addAttribute(newAttribute);
	assertEquals("string", newAttribute.type());
};

WhenChangingAttributeDatatTypeTest.prototype.testCanChangeType = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var newAttribute = newClass.addAttribute(newAttribute);
	assertNotEquals("boolean", newAttribute.type());
	newAttribute.type('int');
	assertEquals("int", newAttribute.type());
};
