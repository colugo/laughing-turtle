var util = {};
util.makeId = function(length) {
	var text = "";
	var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	// first character must not be a number
	text = possible.charAt(Math.floor(Math.random() * (possible.length - 10)));
	for ( var i = 0; i < length - 1; i++)
		text += possible.charAt(Math.floor(Math.random() * possible.length));

	return text;
};


util.contains = function(s, sub) {
	return s.indexOf(sub) !== -1;
};

util.getStringWidth = function(theString) {
    var svgnode = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    var textElem = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    textElem.setAttribute("font-size","12");
    textElem.textContent = theString;
    
    svgnode.appendChild(textElem);
    $('#stringBoundaryHelper').append(svgnode);
    var width = textElem.getBBox().width;
    var stringHeight = textElem.getBBox().height;
    $('#stringBoundaryHelper').empty();
    return [width, stringHeight];
};

util.areBothRelationPointsOnSameSide = function(classARP, classBRP){
	if( (classARP.isLeft() && classBRP.isLeft()) ||
		(classARP.isRight() && classBRP.isRight()) ||
		(classARP.isTop() && classBRP.isTop()) ||
		(classARP.isBottom() && classBRP.isBottom()) ){
		return true;
	}
	return false;
};

util.getClassMostInDirectionOfRelationPoint = function(classARP, classBRP){
	// both RPs must be on the same side
	if(classARP.isLeft() && classBRP.isLeft()){
		if(classARP.offsetX() == Math.min(classARP.offsetX(), classBRP.offsetX())){
			return classARP.theClass;
		}
		return classBRP.theClass;
	};
	
	if(classARP.isRight() && classBRP.isRight()){
		if(classARP.offsetX() == Math.max(classARP.offsetX(), classBRP.offsetX())){
			return classARP.theClass;
		}
		return classBRP.theClass;
	};
	
	if(classARP.isTop() && classBRP.isTop()){
		if(classARP.offsetY() == Math.min(classARP.offsetY(), classBRP.offsetY())){
			return classARP.theClass;
		}
		return classBRP.theClass;
	};
	
	if(classARP.isBottom() && classBRP.isBottom()){
		if(classARP.offsetY() == Math.max(classARP.offsetY(), classBRP.offsetY())){
			return classARP.theClass;
		}
		return classBRP.theClass;
	};
	
	return "getClassMostInDirectionOfRelationPoint - both relation points weren't on the same side";
};

util.doRelationPointsPointInwards = function(classARP, classBRP){
	if( (classARP.isLeft() && classBRP.isRight() && classBRP.offsetX() <= classARP.offsetX()) ||
		(classARP.isRight() && classBRP.isLeft() && classARP.offsetX() <= classBRP.offsetX()) ||
		(classARP.isTop() && classBRP.isBottom() && classBRP.offsetY() <= classARP.offsetY()) ||
		(classARP.isBottom() && classBRP.isTop() && classARP.offsetY() <= classBRP.offsetY()) ){
		return true;
	}
	return false;
};

util.doRelationPointsPointOutwards = function(classARP, classBRP){
	if( (classARP.isLeft() && classBRP.isRight() && classARP.offsetX() <= classBRP.offsetX()) ||
		(classARP.isRight() && classBRP.isLeft() && classBRP.offsetX() <= classARP.offsetX()) ||
		(classARP.isTop() && classBRP.isBottom() && classARP.offsetY() <= classBRP.offsetY()) ||
		(classARP.isBottom() && classBRP.isTop() && classBRP.offsetY() <= classARP.offsetY()) ){
		return true;
	}
	return false;
};

util.RelationPoint = function(x, y) {
	var self = this;
	self.x = x;
	self.y = y;
	self.name = x + ", " + y;
};

util.RelationPointCollection = function(){
	var self = this;
	self.collection = [];

	self.xy = function(x,y){
		var point = new util.RelationPoint(x,y);
		self.add(point);
	};
	
	self.showPoints =  function(msg){
		jstestdriver.console.log(msg,"start");
		for(var i = 0; i < self.collection.length; i++){
			var current = self.collection[i];
			jstestdriver.console.log("Point name", current.name + "_" + current.x + "_" + current.y);
		}
		jstestdriver.console.log(msg,"end");
	};
	
	self.add = function(point){
		self.collection.push(point);
	};
	
	self.lengthOfSegment = function(prev, current){
		  var xs = 0;
		  var ys = 0;

		  xs = current.x - prev.x;
		  xs = xs * xs;

		  ys = current.y - prev.y;
		  ys = ys * ys;

		  return Math.sqrt( xs + ys );
	};
	
	self.length = function(){
		if(self.collection.length <= 1){
			return 0;
		}
		var total = 0;
		var prev = self.collection[0];
		for(var i = 1; i < self.collection.length; i++){
			var current = self.collection[i];
			total += self.lengthOfSegment(prev, current);
			prev = current;
		}
		return total;
	};
	
	self.getPointAtPercent = function(percentage){
		var expectedDistance = self.length() * percentage;
		if(self.collection.length <= 1){
			return 0;
		}
		
		var goneLength = 0;
		var prev = self.collection[0];
		
		
		for(var i = 1; i < self.collection.length; i++){
			var current = self.collection[i];
			var lengthOfCurrentSegment = self.lengthOfSegment(prev, current);
			if ( goneLength + lengthOfCurrentSegment >= expectedDistance ){
				var expectedDistanceMinusGoneLength = expectedDistance - goneLength;
				var percentageNeeded = expectedDistanceMinusGoneLength / lengthOfCurrentSegment;
				
				var newX = (current.x - prev.x) * percentageNeeded + prev.x;
				var newY = (current.y - prev.y) * percentageNeeded + prev.y;
				
				return new util.RelationPoint(newX, newY);
			}
			goneLength += lengthOfCurrentSegment;
			prev = current;
		}
	};
	
	self.getPointString = function(){
		var output = "";
		
		if(self.collection.length == 0){
			return "";
		}
		
		for(var i = 0; i < self.collection.length; i++){
			var current = self.collection[i];
			output += current.x + " " + current.y + ", ";
		}
		output = output.substring(0, output.length - 2);
		return output;
	};
		
};

util.calculateDragRelativeToBaseAndExistionPosition = function(base, drag, currentOffset){
	return drag - base + currentOffset;
};

//jQuery's hasClass doesn't work for SVG, but this does!
//takes an object obj and checks for class has
//returns true if the class exits in obj, false otherwise
util.hasClassSVG = function(obj, has) {
 var classes = $(obj).attr('class');
 if(classes == undefined){
	 return false;
 }
 var index = classes.search(has);
 	
 if (index == -1) {
 	return false;
 }
 else {
 	return true;
 }
};


util.EntityClassPosition = function(entityClass){
	var self = this;
	self.uuid = entityClass.uuid;
	self.x = entityClass.x();
	self.y = entityClass.y();
	self.superClassTriangleIndex = entityClass.superClassTriangleIndex();
	self.states = [];
	for(var k = 0; k < entityClass.stateMachine.states().length; k++){
		var theState = entityClass.stateMachine.states()[k];
		self.states.push(new util.EntityStatePosition(theState));
	}
	self.instances = [];
	for(var k = 0; k < entityClass.stateMachine.instances().length; k++){
		var theInstance = entityClass.stateMachine.instances()[k];
		self.instances.push(new util.EventTransitionPosition(theInstance));
	}
};

util.EntityStatePosition = function(entityState){
	var self = this;
	self.uuid = entityState.uuid;
	self.x = entityState.x();
	self.y = entityState.y();
};

util.EventTransitionPosition = function(eventInstance){
	var self = this;
	self.uuid = eventInstance.uuid;
	self.fromIndex = eventInstance.fromRelationPointIndex();
	self.toIndex = eventInstance.toRelationPointIndex();
	self.fromDragX = eventInstance._fromDragX();
	self.fromDragY = eventInstance._fromDragY();
	self.toDragX = eventInstance._toDragX();
	self.toDragY = eventInstance._toDragY();
};

util.EntityRelationPosition = function(entityRelation){
	var self = this;
	self.uuid = entityRelation.uuid;
	self.endAIndex = entityRelation.relationPointIndexEndA();
	self.endBIndex = entityRelation.relationPointIndexEndB();
	self.verbAOffsetX = entityRelation.verbAOffsetX;
	self.verbAOffsetY = entityRelation.verbAOffsetY;
	self.verbBOffsetX = entityRelation.verbBOffsetX;
	self.verbBOffsetY = entityRelation.verbBOffsetY;
};

util.ClassDiagramCoordinateSet = function(domain){
	var self = this;
	self.classes = [];
	self.relations = [];
	for(var i = 0; i<domain.entityClasses().length; i++){
		self.classes.push(new util.EntityClassPosition(domain.entityClasses()[i]));
	}
	for(var i = 0; i<domain.entityRelations().length; i++){
		self.relations.push(new util.EntityRelationPosition(domain.entityRelations()[i]));
	}
};