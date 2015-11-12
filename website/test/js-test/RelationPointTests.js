RelationPointTests = TestCase("RelationPointTests");

RelationPointTests.prototype.testRelationPointIds = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	var relationPoint = new ClassDiagram_Entities.RelationPoint(newClass, 0.25, 0, 0);
	assertEquals(newClass.uuid + '0', relationPoint.uuid());
	assertEquals(relationPoint.theClass.name(), domain.getRelationPointWithUUID(newClass.uuid + '0').theClass.name());
	
};

RelationPointTests.prototype.testCanCreateTopRelationPoint = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	// width of 100
	// height of 30
	var relationPoint = new ClassDiagram_Entities.RelationPoint(newClass, 0.25, 0);
	assertEquals(0, relationPoint.yPercent);
	assertEquals(true, relationPoint.isTop());
	assertEquals(false, relationPoint.isBottom());
	assertEquals(false, relationPoint.isLeft());
	assertEquals(false, relationPoint.isRight());
	assertEquals(relationPoint.x(), 125);
	assertEquals(relationPoint.y(), 200);
	assertEquals(relationPoint.offsetX(), 125);
	assertEquals(relationPoint.offsetY(), 160);
};

RelationPointTests.prototype.testCanCreateBottomRelationPoint = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	// width of 100
	// height of 30
	var relationPoint = new ClassDiagram_Entities.RelationPoint(newClass, 0.25, 1);
	assertEquals(1, relationPoint.yPercent);
	assertEquals(false, relationPoint.isTop());
	assertEquals(true, relationPoint.isBottom());
	assertEquals(false, relationPoint.isLeft());
	assertEquals(false, relationPoint.isRight());
	assertEquals(relationPoint.x(), 125);
	assertEquals(relationPoint.y(), 230);
	assertEquals(relationPoint.offsetX(), 125);
	assertEquals(relationPoint.offsetY(), 270);
};

RelationPointTests.prototype.testCanCreateLeftRelationPoint = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	// width of 100
	// height of 30
	var relationPoint = new ClassDiagram_Entities.RelationPoint(newClass, 0, 0.5);
	assertEquals(0, relationPoint.xPercent);
	assertEquals(false, relationPoint.isTop());
	assertEquals(false, relationPoint.isBottom());
	assertEquals(true, relationPoint.isLeft());
	assertEquals(false, relationPoint.isRight());
	assertEquals(relationPoint.x(), 100);
	assertEquals(relationPoint.y(), 215);
	assertEquals(relationPoint.offsetX(), 60);
	assertEquals(relationPoint.offsetY(), 215);
};

RelationPointTests.prototype.testCanCreateRightRelationPoint = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var newClass = domain.createClass();
	newClass.x(100);
	newClass.y(200);
	// width of 100
	// height of 30
	var relationPoint = new ClassDiagram_Entities.RelationPoint(newClass, 1, 0.5);
	assertEquals(1, relationPoint.xPercent);
	assertEquals(false, relationPoint.isTop());
	assertEquals(false, relationPoint.isBottom());
	assertEquals(false, relationPoint.isLeft());
	assertEquals(true, relationPoint.isRight());
	assertEquals(relationPoint.x(), 200);
	assertEquals(relationPoint.y(), 215);
	assertEquals(relationPoint.offsetX(), 240);
	assertEquals(relationPoint.offsetY(), 215);
};

RelationPointTests.prototype.testCanDetermineLeftMostClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(200);
	classB.y(200);
	
	var classARP = classA.relationPoints()[10];
	var classBRP = classB.relationPoints()[10];
	assertEquals(true, classARP.isLeft());
	assertEquals(true, classBRP.isLeft());
	
	assertEquals(true, util.areBothRelationPointsOnSameSide(classARP, classBRP));
	assertEquals(true, util.areBothRelationPointsOnSameSide(classBRP, classARP));
	assertEquals(classA, util.getClassMostInDirectionOfRelationPoint(classARP, classBRP));
	assertEquals(classA, util.getClassMostInDirectionOfRelationPoint(classBRP, classARP));

};

RelationPointTests.prototype.testCanDetermineRightMostClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(200);
	classB.y(200);
	
	var classARP = classA.relationPoints()[4];
	var classBRP = classB.relationPoints()[4];
	assertEquals(true, classARP.isRight());
	assertEquals(true, classBRP.isRight());
	
	assertEquals(true, util.areBothRelationPointsOnSameSide(classARP, classBRP));
	assertEquals(true, util.areBothRelationPointsOnSameSide(classBRP, classARP));
	assertEquals(classB, util.getClassMostInDirectionOfRelationPoint(classARP, classBRP));
	assertEquals(classB, util.getClassMostInDirectionOfRelationPoint(classBRP, classARP));
};

RelationPointTests.prototype.testCanDetermineTopMostClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(200);
	classB.y(400);
	
	var classARP = classA.relationPoints()[1];
	var classBRP = classB.relationPoints()[1];
	assertEquals(true, classARP.isTop());
	assertEquals(true, classBRP.isTop());
	
	assertEquals(true, util.areBothRelationPointsOnSameSide(classARP, classBRP));
	assertEquals(true, util.areBothRelationPointsOnSameSide(classBRP, classARP));
	assertEquals(classA, util.getClassMostInDirectionOfRelationPoint(classARP, classBRP));
	assertEquals(classA, util.getClassMostInDirectionOfRelationPoint(classBRP, classARP));
};

RelationPointTests.prototype.testCanDetermineBottomMostClass = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(200);
	classB.y(400);
	
	var classARP = classA.relationPoints()[7];
	var classBRP = classB.relationPoints()[7];
	assertEquals(true, classARP.isBottom());
	assertEquals(true, classBRP.isBottom());
	
	assertEquals(true, util.areBothRelationPointsOnSameSide(classARP, classBRP));
	assertEquals(true, util.areBothRelationPointsOnSameSide(classBRP, classARP));
	assertEquals(classB, util.getClassMostInDirectionOfRelationPoint(classARP, classBRP));
	assertEquals(classB, util.getClassMostInDirectionOfRelationPoint(classBRP, classARP));
};

RelationPointTests.prototype.testCanDetermineIfRelationPointsPointInwardsVertically = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(200);
	classB.y(400);
	
	var classARPB = classA.relationPoints()[7];
	var classARPT = classA.relationPoints()[1];
	var classBRPT = classB.relationPoints()[1];
	var classBRPB = classB.relationPoints()[7];
	assertEquals(true, classARPB.isBottom());
	assertEquals(true, classARPT.isTop());
	assertEquals(true, classBRPT.isTop());
	assertEquals(true, classBRPB.isBottom());
	
	assertEquals(true, util.doRelationPointsPointInwards(classARPB, classBRPT));
	assertEquals(false, util.doRelationPointsPointInwards(classARPT, classBRPB));
	assertEquals(false, util.doRelationPointsPointOutwards(classARPB, classBRPT));
	assertEquals(true, util.doRelationPointsPointOutwards(classARPT, classBRPB));
};

RelationPointTests.prototype.testCanDetermineIfRelationPointsPointInwardsHorizontally = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var classB = domain.createClass();
	classB.x(400);
	classB.y(400);
	
	var classARPR = classA.relationPoints()[4];
	var classARPL = classA.relationPoints()[10];
	var classBRPL = classB.relationPoints()[10];
	var classBRPR = classB.relationPoints()[4];
	assertEquals(true, classARPR.isRight());
	assertEquals(true, classARPL.isLeft());
	assertEquals(true, classBRPL.isLeft());
	assertEquals(true, classBRPR.isRight());
	
	assertEquals(true, util.doRelationPointsPointInwards(classARPR, classBRPL));
	assertEquals(false, util.doRelationPointsPointInwards(classARPL, classBRPR));
	assertEquals(false, util.doRelationPointsPointOutwards(classARPR, classBRPL));
	assertEquals(true, util.doRelationPointsPointOutwards(classARPL, classBRPR));
};


RelationPointTests.prototype.testCanCreateRelationPointCollection = function(){
	var collection = new util.RelationPointCollection();
	assertEquals(0, collection.length());
	var p0 = new util.RelationPoint(0,0);
	var p1 = new util.RelationPoint(10,10);
	var p2 = new util.RelationPoint(20,10);
	assertEquals(0, collection.collection.length);
	collection.add(p0);
	assertEquals(1, collection.collection.length);
	collection.add(p1);
	assertEquals(2, collection.collection.length);
	assertEquals(14.142135623730951, collection.length());
	collection.add(p2);
	assertEquals(3, collection.collection.length);
	assertEquals(24.142135623730951, collection.length());
};

RelationPointTests.prototype.testCanDeterminePointAt50PercentageOfRelationPointCollection = function(){
	var collection = new util.RelationPointCollection();
	var p0 = new util.RelationPoint(0,0);
	var p1 = new util.RelationPoint(10,10);
	var expectedLength = 14.142135623730951;
	collection.add(p0);
	collection.add(p1);
	assertEquals(expectedLength, collection.length());
	assertEquals(5, collection.getPointAtPercent(0.5).x);
	assertEquals(5, collection.getPointAtPercent(0.5).y);
	assertEquals(expectedLength * 0.5, collection.lengthOfSegment(p0, collection.getPointAtPercent(0.5)));
};

RelationPointTests.prototype.testCanDeterminePointAt50PercentageOfRelationPointCollection2 = function(){
	var collection = new util.RelationPointCollection();
	//300 150, 300 110, 300 -30, 60 -30, 60 10
	collection.xy(300, 150);
	collection.xy(300, 110);
	assertEquals(40, collection.length());
	collection.xy(300, -30);
	collection.xy(60, -30);
	collection.xy(60, 10);
	assertEquals(250, collection.getPointAtPercent(0.5).x);
	assertEquals(-30, collection.getPointAtPercent(0.5).y);
};


RelationPointTests.prototype.testCanDeterminePointsOfRelationPointCollection = function(){
	var collection = new util.RelationPointCollection();
	var p0 = new util.RelationPoint(0,0);
	var p1 = new util.RelationPoint(0,4);
	var p2 = new util.RelationPoint(2,6);
	
	var expectedString = "0 0, 0 4, 2 6";
	
	collection.add(p0);
	//assertEquals(0, collection.length());
	collection.add(p1);
	//assertEquals(4, collection.length());
	collection.add(p2);
		
	assertEquals(0, collection.getPointAtPercent(0.5).x);
		
	assertEquals(expectedString, collection.getPointString());
};


RelationPointTests.prototype.testCanGetVerbBoxForRelationPoint = function(){
	var domain = new ClassDiagram_Entities.EntityDomain();
	var classA = domain.createClass();
	classA.x(100);
	classA.y(200);
	
	var relationPoint = classA.relationPoints()[4];
	assertEquals(relationPoint.x(), 200);
	assertEquals(relationPoint.y(), 215);
	assertEquals(relationPoint.offsetX(), 240);
	assertEquals(relationPoint.offsetY(), 215);
	
	assertEquals(relationPoint.verbBoxX(), 190);
	assertEquals(relationPoint.verbBoxY(), 165);
};

RelationPointTests.prototype.testCanCalculateDrag = function(){
	assertEquals(-10, util.calculateDragRelativeToBaseAndExistionPosition(100,90,0));
	assertEquals(-20, util.calculateDragRelativeToBaseAndExistionPosition(100,90,-10));
	assertEquals(20, util.calculateDragRelativeToBaseAndExistionPosition(100,130,-10));
	assertEquals(20, util.calculateDragRelativeToBaseAndExistionPosition(100,90,30));
	assertEquals(30, util.calculateDragRelativeToBaseAndExistionPosition(100,120,10));
};