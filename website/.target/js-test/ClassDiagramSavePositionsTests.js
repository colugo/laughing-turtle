ClassDiagramSavePositionTests = TestCase("ClassDiagramSavePositionTests");

ClassDiagramSavePositionTests.prototype.testCanConvertClassToPosition = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	
	var entityClassPosition = new util.EntityClassPosition(newClass);
	assertEquals(newClass.x(), entityClassPosition.x);
	assertEquals(newClass.y(), entityClassPosition.y);
	assertEquals(newClass.uuid, entityClassPosition.uuid);
};

ClassDiagramSavePositionTests.prototype.testCanConvertClassToPosition = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	
	var newClass2 = domain.createClass();
	newClass2.x(400);
	newClass2.y(600);
	
	var entityClassPositions = new util.ClassDiagramCoordinateSet(domain);
	assertEquals(2, entityClassPositions.classes.length);
};
