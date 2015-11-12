var ClassDiagram_Entities = new function() {

	/*
	 * Colours
	 * 
	 */
	var ColourSelected = "#90CA77";
	var ColourClass = "#81C6DD";
	var ColourSelectedRelation = "#46D907";
	var ColourRelation = "#E9B64D";
	var ColourAssociationRelation = "#9E3B33";
	
	/*
	 * Valid name regex
	 * 
	 */
	var validNameRegexString = "'^[_a-zA-Z][_a-zA-Z0-9]*$'";
	var validNameRegexPattern = /^[_a-zA-Z][_a-zA-Z0-9]*$/;

	/*
	 * Datatypes
	 * 
	 */
	availableTypes = ['string','boolean','int','float'];
	
	
	/*
	 * Cardinality
	 * 
	 */
	cardinalityTypes = ['0..1','1..1','0..*','1..*'];
	
	
	/*
	 * Verb movement box dims
	 * 
	 */
	verbMovementBoxWidth = 100;
	verbMovementBoxHeight = 100;
	
	/*
	 * Entity Domain
	 * 
	 */
	this.EntityDomain = function() {
		var self = this;
		self.entityClasses = new ko.observableArray();
		self.entityRelations = new ko.observableArray();

		self.associationClassOptions = new ko.computed(function(){
			var options = new ko.observableArray();
			var unselected = new ClassDiagram_Entities.EntityClass();
			unselected.name("None");
			unselected.uuid = undefined;
			options().push(unselected);
			
			for(var i = 0; i < self.entityClasses().length; i++){
				options().push(self.entityClasses()[i]);
			}
			
			return options();
		}, this);
		
		self.createClass = function() {
			var newClass = new ClassDiagram_Entities.EntityClass();
			self.entityClasses().push(newClass);
			return newClass;
		};
		
		self.deleteClass = function(uuid) {
			self.removeClassWithUUID(uuid);
		};
		
		self.getClassWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityClasses().length; i++) {
				var currentClass = self.entityClasses()[i];
				if (currentClass.uuid == uuid) {
					return currentClass;
				}
			}
		};
		
		self.getRelationPointWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityClasses().length; i++) {
				var currentClass = self.entityClasses()[i];
				for ( var j = 0; j < currentClass.relationPoints().length; j++){
					var rp = currentClass.relationPoints()[j];
					if(rp.uuid() == uuid){
						return rp;	
					}
				}
			}
		};
		
		self.hasClassWithName = function(className) {
			for ( var i = 0; i < self.entityClasses().length; i++) {
				var currentClass = self.entityClasses()[i];
				if (currentClass.name() == className) {
					return true;
				}
			}
			return false;
		};
		
		self.removeClassWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityClasses().length; i++) {
				var currentClass = self.entityClasses()[i];
				if (currentClass.uuid == uuid) {
					self.entityClasses.remove(currentClass);
					return;
				}
			}
		};
		
		self.getRelationWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityRelations().length; i++) {
				var currentRelation = self.entityRelations()[i];
				if (currentRelation.uuid == uuid) {
					return currentRelation;
				}
			}
		};
		
		self.hasRelationWithName = function(relationName) {
			for ( var i = 0; i < self.entityRelations().length; i++) {
				var currentRelation = self.entityRelations()[i];
				if (currentRelation.name() == relationName) {
					return true;
				}
			}
			return false;
		};
		
		self.removeRelationWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityRelations().length; i++) {
				var currentRelation = self.entityRelations()[i];
				if (currentRelation.uuid == uuid) {
					self.entityRelations.remove(currentRelation);
					return;
				}
			}
		};
		
		self.getRelationsInvolvingClass = function(theClass){
			relationsInvolvingClass = [];
			for ( var i = 0; i < self.entityRelations().length; i++) {
				var currentRelation = self.entityRelations()[i];
				if(currentRelation.isClassInvolved(theClass)){
					relationsInvolvingClass.push(currentRelation);
				}
			}
			return relationsInvolvingClass;
		};
		
		self.getStateWithUUID = function(uuid) {
			for ( var i = 0; i < self.entityClasses().length; i++) {
				var currentClass = self.entityClasses()[i];
				if(currentClass.stateMachine.hasStates()){
					if(currentClass.stateMachine.hasStateWithUUID(uuid)){
						return currentClass.stateMachine.getStateWithUUID(uuid);
					}
				}
			}
			return null;
		};
		
	};
	// don't send the domain each save
	this.EntityDomain.prototype.toJSON = function() {
		return "";
	};

	/*
	 * RelationPointOffset 
	 * 
	 */
	var relationPointOffset = 40;
	
	/*
	 * Relation Point
	 * 
	 */
	this.RelationPoint = function(theClass, xPercent, yPercent, index){
		var self = this;
		self.theClass = theClass;
		self.xPercent = xPercent;
		self.yPercent = yPercent;
		self.index = index;
		
		self.uuid = ko.computed(function(){
			return self.theClass.uuid + self.index;
		}, this);
		
		self.x = ko.computed(function(){
			return self.theClass.x() + (self.theClass.width() * self.xPercent);
		}, this);
		self.y = ko.computed(function(){
			return self.theClass.y() + (self.theClass.height() * self.yPercent);
		}, this);
		
		self.isTop = ko.computed(function(){
			return self.yPercent == 0;
		}, this);

		self.isBottom = ko.computed(function(){
			return self.yPercent == 1;
		}, this);
		
		self.isLeft = ko.computed(function(){
			return self.xPercent == 0;
		}, this);
		
		self.isRight = ko.computed(function(){
			return self.xPercent == 1;
		}, this);
		
		self.isTopOrBottom = ko.computed(function(){
			return self.isTop() || self.isBottom();
		}, this);
		
		self.isLeftOrRight = ko.computed(function(){
			return self.isLeft() || self.isRight();
		}, this);
		
		self.offsetX = ko.computed(function(){
			if(self.isTopOrBottom()){
				return self.x();
			}
			if(self.isLeft()){
				return self.x() - relationPointOffset;
			}
			if(self.isRight()){
				return self.x() + relationPointOffset;
			}
		}, this);
		
		self.offsetY = ko.computed(function(){
			if(self.isLeftOrRight()){
				return self.y();
			}
			if(self.isTop()){
				return self.y() - relationPointOffset;
			}
			if(self.isBottom()){
				return self.y() + relationPointOffset;
			}
		}, this);
		
		self.verbBoxY = ko.computed(function(){
			return self.offsetY() -  (verbMovementBoxHeight / 2);
		}, this);
		
		self.verbBoxX = ko.computed(function(){
			return self.offsetX() -  (verbMovementBoxWidth / 2);
		}, this);
	};
	// prevent circular reference issues
	this.RelationPoint.prototype.toJSON = function() {
		return "";
	};
	
	/*
	 * Entity Class
	 * 
	 */
	this.EntityClass = function() {
		var self = this;
		self.type = "EntityClass";
		self.uuid = util.makeId(10);
		self.attributes = new ko.observableArray();
		self.stateMachine = new StateDiagram_Entities.StateMachine(self);
		self.newRelationUUID = new ko.observable();
		
	    self.name = ko.observable("Class_" + self.uuid);
	    self.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromClassName = self.isClassNameValid(value);
	        	if(noErrorsFromClassName == true){
	        		window.vm.renameClassAction(self, value);
	        	}else{
	        		window.vm.actionList.message(noErrorsFromClassName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
	    
	    
	    self.addAttribute = function(){
	    	var theAttribute = new ClassDiagram_Entities.EntityAttribute(self);
	    	self.attributes().push(theAttribute);
	    	self.attributes.valueHasMutated();
	    	return theAttribute;
	    };
	    
	    self.hasAttributes = function() {return self.attributes().length > 0;};
	    self.hasAttributesComputed = ko.computed(self.hasAttributes, this);
	    
	    self.deleteAttribute = function(theAttribute){
	    	self.attributes.remove(theAttribute);
	    };
	    
	    self.hasAttributeWithName = function(attributeName){
	    	for ( var i = 0; i < self.attributes().length; i++) {
				var currentAttribute = self.attributes()[i];
				if (currentAttribute.name() == attributeName) {
					return true;
				}
			}
			return false;
	    };
	   
	    self.isClassNameValid = function(newName){
			if(window.vm.domain.hasClassWithName(newName)){
				return "A class is already named '" + newName+"'";
			}
			if(!validNameRegexPattern.test(newName)){
				return "'"+newName+"' did not satisfy the regex: " + validNameRegexString;
			}
			return true;
	    };
	  
	    self.isAssociation = function(){
	    	for(var i = 0; i < window.vm.domain.entityRelations().length; i++) {
	    		var relation = window.vm.domain.entityRelations()[i];
	    		if(relation.hasAssociation() && relation.associationClass().uuid == self.uuid){
	    			return true;
	    		}
	    	}
	    	return false;
	    };
	    
	    self.superClass = new ko.observable(null);
	    self.superClassUUID = new ko.computed({
	        read: function(){
	        		if(this.superClass() != null){
	        			return this.superClass().uuid;
	        		}
	        	},
	        write: function (value) {
	        	if(window.vm.modelSelection.isOneThingSelected()){
	        		window.vm.changeSuperClassAction(value);
	        	}
	        },
	        owner: this
	    });
	    
	    self.hasSuperClass = new ko.computed(function(){
	    	return self.superClass() != null;
	    }, this);
	    
	    self.hasSubClasses = function(){
	    	for(var i = 0; i < window.vm.domain.entityClasses().length; i++){
	    		var currentClass = window.vm.domain.entityClasses()[i];
	    		if(currentClass.hasSuperClass() && currentClass.getSuperClass() == self){
	    			return true;
	    		}
	    	}
	    	return false;
	    };
	    
	    self.getSubClasses = function(){
	    	subClasses = [];
	    	for(var i = 0; i < window.vm.domain.entityClasses().length; i++){
	    		var currentClass = window.vm.domain.entityClasses()[i];
	    		if(currentClass.hasSuperClass() && currentClass.getSuperClass() == self){
	    			subClasses.push(currentClass);
	    		}
	    	}
	    	return subClasses;
	    };
	    
	    self.setSuperClass = function(superClass){
	    	self.superClass(superClass);
	    };
	    
	    self.getSuperClass = function(){
	    	return this.superClass();
	    };
	    
	    self.clearSuperClass = function(){
	    	self.superClass(null);
	    };
	    
	    
	    /////////////////
	    //   diagram   //
	    /////////////////
	    self.isSelected = function(){
	    	if(window.vm && window.vm.modelSelection.oneThingSelected() == self){
	    		return true;
	    	}
	    	
	    	for ( var i = 0; i < self.stateMachine.states().length; i++) {
				var currentState = self.stateMachine.states()[i];
				if(currentState.isSelected()){
					return true;
				}
	    	}
	    	
	    	return false;
	    };
	    
	    self.colour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelected;
	    	}
	    	return ColourClass;
	    }, this);
	    
	    
	    self._x = new ko.observable(10);
	    self.x = new ko.computed({
	        read: self._x,
        	write: function (value) {
        		self._x(parseInt(value,10));
        	},
        	owner: this
	    });
	    self._y = new ko.observable(10);
	    self.y = new ko.computed({
	        read: self._y,
        	write: function (value) {
        		self._y(parseInt(value,10));
        	},
        	owner: this
	    });
	    self.width = new ko.computed(function(){
	    	var textPadding = 20;
	    	var nameWidth = util.getStringWidth(self.name())[0] + textPadding;
	    	var minWidth = 100;
	    	var longestAttribute = 0;
	    	for(var i = 0; i < self.attributes().length; i++){
	    		var attributeLength = util.getStringWidth(self.attributes()[i].nameAndType())[0];
	    		if(attributeLength > longestAttribute){
	    			longestAttribute = attributeLength;
	    		}
	    	}
	    	return Math.max(minWidth, nameWidth, longestAttribute + 4);
	    }, this);
	    
	    self.midPoint = function(){
	    	return new util.RelationPoint(self.x() + (self.width() / 2 ), self.y() + ( self.height() / 2) ); 
	    };
	    
	    self.classNameX = new ko.computed(function(){
	    	var bbw = util.getStringWidth(self.name())[0];
	    	var halfbbw = bbw / 2;
	        return self.x() + (self.width() / 2) - halfbbw;
	    }, this);
	    self.classNameY = new ko.computed(function(){
	    	return self.y() + util.getStringWidth(self.name())[1];
	    }, this);
	    self.attributesY = new ko.computed(function(){
	    	return this.classNameY() + (1.5 * util.getStringWidth("A")[1]);
	    }, this);
	    self.textHeight = new ko.computed(function(){
	    	return util.getStringWidth("A")[1];
	    }, this);
	    self.height = new ko.computed(function(){
	    	var minHeight = 30;
	    	var heightWithAttrs = (2 * util.getStringWidth("A")[1]) + (self.attributes().length * self.textHeight());
	    	return Math.max(minHeight, heightWithAttrs);
	    }, this);
	    self.relationPoints = new ko.observableArray([
            new ClassDiagram_Entities.RelationPoint(self, 0.25, 0, 0),
            new ClassDiagram_Entities.RelationPoint(self, 0.5, 0, 1),
            new ClassDiagram_Entities.RelationPoint(self, 0.75, 0, 2),
            
            new ClassDiagram_Entities.RelationPoint(self, 1, 0.25, 3),
            new ClassDiagram_Entities.RelationPoint(self, 1, 0.5, 4),
            new ClassDiagram_Entities.RelationPoint(self, 1, 0.75, 5),
            
            new ClassDiagram_Entities.RelationPoint(self, 0.75, 1, 6),
            new ClassDiagram_Entities.RelationPoint(self, 0.5, 1, 7),
            new ClassDiagram_Entities.RelationPoint(self, 0.25, 1, 8),
            
            new ClassDiagram_Entities.RelationPoint(self, 0, 0.75, 9),
            new ClassDiagram_Entities.RelationPoint(self, 0, 0.5, 10),
            new ClassDiagram_Entities.RelationPoint(self, 0, 0.25, 11),
            
	    ]);
	    
	    self.superClassTriangleIndex = new ko.observable(7);
	    self.superClassTriangleRelationPoint = new ko.computed(function(){
	    	return self.relationPoints()[self.superClassTriangleIndex()];
	    }, this);
	    
	    self.superClassTrianglePoints = new ko.computed(function(){
	    	var rp = self.superClassTriangleRelationPoint();
	    	var length = 22;
	    	var angle = 30;
	    	if(rp.isLeft()){ angle = 150;}
	    	if(rp.isRight()){ angle = 30;}
	    	if(rp.isTop()){ angle = 150;}
	    	var radFor30Deg = Math.PI / 180 * angle;
	    	var radForNeg30Deg = radFor30Deg * -1;
	    	
	    	var relationPoints = new util.RelationPointCollection();
	    	
	    	relationPoints.xy(rp.x(), rp.y());
	    	if(rp.isLeftOrRight()){
	    		relationPoints.xy(rp.x() + length * Math.cos(radFor30Deg), rp.y() + length * Math.sin(radFor30Deg));
	    		relationPoints.xy(rp.x() + length * Math.cos(radForNeg30Deg), rp.y() + length * Math.sin(radForNeg30Deg));
	    	}else{
	    		relationPoints.xy(rp.x() + length * Math.sin(radFor30Deg), rp.y() + length * Math.cos(radFor30Deg));
	    		relationPoints.xy(rp.x() + length * Math.sin(radForNeg30Deg), rp.y() + length * Math.cos(radForNeg30Deg));
	    	}
	    	relationPoints.xy(rp.x(), rp.y());
	    	return relationPoints.getPointString();
	    }, this);
	    
	    self.superClassEndLinePoint = new ko.computed(function(){
	    	var distance = 75;
	    	var rp = self.superClassTriangleRelationPoint();
	    	if(rp.isLeft()){
	    		return new util.RelationPoint(rp.x() - distance, rp.y());
	    	}
	    	if(rp.isRight()){
	    		return new util.RelationPoint(rp.x() + distance, rp.y());
	    	}
	    	if(rp.isTop()){
	    		return new util.RelationPoint(rp.x(), rp.y() - distance);
	    	}
	    	if(rp.isBottom()){
	    		return new util.RelationPoint(rp.x(), rp.y() + distance);
	    	}
	    }, this);
	    
	    self.subClassMidPoint = new ko.computed(function(){
	    	if(self.getSuperClass() == null){
	    		return new util.RelationPoint(0,0);
	    	}
	    	
	    	var rp = self.getSuperClass().superClassTriangleRelationPoint();
	    	if(rp.isLeftOrRight()){
	    		return new util.RelationPoint( self.getSuperClass().superClassEndLinePoint().x, self.midPoint().y);
	    	}
	    	if(rp.isTopOrBottom()){
	    		return new util.RelationPoint(self.midPoint().x, self.getSuperClass().superClassEndLinePoint().y);
	    	}
	    }, this);
	};
	this.EntityClass.prototype.toJSON = function() {
		return "";
	};

	/*
	 * Entity Attribute
	 * 
	 */
	this.EntityAttribute = function(owningClass) {
		var self = this;
		self.uuid = util.makeId(10);
		self.owningClassUuid = owningClass.uuid;
	    this.name = ko.observable("Attribute_" + self.uuid);
	    this.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromAttributeName =  self.isAttributeNameValid(value);
	        	if(noErrorsFromAttributeName == true) {
	        		window.vm.renameAttributeAction(self.owningClassUuid, self, value);
	        	} else {
	        		window.vm.actionList.message(noErrorsFromAttributeName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
	    
	    self.type = ko.observable(availableTypes[0]);
	    this.changableType = ko.computed({
	        read: this.type,
	        write: function (value) {
	        	window.vm.changeAttributeTypeAction(self.owningClassUuid, self, value);
	        },
	        owner: this
	    });
	    
	    self.isAttributeNameValid = function(newName){
	    	var theClass = window.vm.domain.getClassWithUUID(self.owningClassUuid);
			if(theClass.hasAttributeWithName(newName)){
				return "Class '"+theClass.name()+"' already has an attribute named '" + newName+"'";
			}
			if(newName == "state"){
				return "'state' ( in lowercase ) is a reserved name for attributes. Try 'State'."; 
			}
			if(!validNameRegexPattern.test(newName)){
				return "'"+newName+"' did not satisfy the regex: " + validNameRegexString;
			}
			return true;
	    };
	    
	    self.nameAndType = new ko.computed(function(){
	    	return self.name() + " : " + self.type();
	    }, this);
	};
	this.EntityAttribute.prototype.toJSON = function() {
		return "";
	};
	
	
	/*
	 * Entity Relation
	 * 
	 */
	this.EntityRelation = function(endA, endB) {
		var self = this;
		self.type = "EntityRelation";
		self.uuid = util.makeId(10);
		self.associationClass = new ko.observable(null);
		self.associationClassUUID = new ko.computed({
	        read: function(){
	        		if(this.associationClass() != null){
	        			return this.associationClass().uuid;
	        		}
	        	},
	        write: function (value) {
	        	if(window.vm.modelSelection.isOneThingSelected()){
	        		window.vm.createAssociationAction(value);
	        	}
	        },
	        owner: this
	    });
		self.name = new ko.observable("Relation_" + self.uuid);
		self.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromRelationName = self.isRelationNameValid(value);
	        	if(noErrorsFromRelationName == true){
	        		window.vm.renameRelationAction(self.uuid, value);
	        	}else{
	        		window.vm.actionList.message(noErrorsFromRelationName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
		self.endA = new ko.observable(endA);
		self.endB = new ko.observable(endB);
		self.endAUUID = ko.computed({
	        read: function(){return this.endA().uuid;},
	        write: function (value) {
	        	window.vm.changeRelationAction(self.uuid, value, self.verbA(), self.cardinalityA(), self.endB().uuid, self.verbB(), self.cardinalityB());
	        },
	        owner: this
	    });
		self.endBUUID = ko.computed({
	        read: function(){return this.endB().uuid;},
	        write: function (value) {
	        	window.vm.changeRelationAction(self.uuid, self.endA().uuid, self.verbA(), self.cardinalityA(), value, self.verbB(), self.cardinalityB());
	        },
	        owner: this
	    });
		self.cardinalityA = new ko.observable('0..*');
		self.cardinalityAChangeable = ko.computed({
			read: self.cardinalityA,
		    write: function (value) {
		    	window.vm.changeRelationAction(self.uuid, self.endA().uuid, self.verbA() ,value, self.endB().uuid, self.verbB(), self.cardinalityB());
		    },
		    owner: this
		});
		self.cardinalityB = new ko.observable('0..*');
		self.cardinalityBChangeable = ko.computed({
			read: self.cardinalityB,
		    write: function (value) {
		    	window.vm.changeRelationAction(self.uuid, self.endA().uuid, self.verbA() ,self.cardinalityA(), self.endB().uuid, self.verbB(), value);
		    },
		    owner: this
		});
		self.verbA = new ko.observable('leads');
		self.verbAChangeable = ko.computed({
			read: self.verbA,
		    write: function (value) {
		    	window.vm.changeRelationAction(self.uuid, self.endA().uuid, value, self.cardinalityA(), self.endB().uuid, self.verbB(), self.cardinalityB());
		    },
		    owner: this
		});
		self.verbB = new ko.observable('follows');
		self.verbBChangeable = ko.computed({
			read: self.verbB,
		    write: function (value) {
		    	window.vm.changeRelationAction(self.uuid, self.endA().uuid, self.verbA(), self.cardinalityA(), self.endB().uuid, value, self.cardinalityB());
		    },
		    owner: this
		});
		self.isReflexive = function() {
			return self.endA() == self.endB();
		};
				
		self.classAName = ko.computed(function(){
			return self.endA().name();
		}, this);
		
		self.classBName = ko.computed(function(){
			return self.endB().name();
		}, this);
		
		self.isClassInvolved = function(aClass){
			if(self.endA() == aClass || self.endB() == aClass){
				return true;
			}
			return false;
		};
		
		self.getOtherEnd = function(aClass){
			if(aClass == self.endA()){
				return self.endB();
			}
			return self.endA();
		};
		
	    self.isRelationNameValid = function(newName){
	    	if(window.vm.domain.hasRelationWithName(newName)){
				return "A relation is already named '" + newName+"'";
			}
			if(!validNameRegexPattern.test(newName)){
				return "'"+newName+"' did not satisfy the regex: " + validNameRegexString;
			}
			return true;
	    };
	    
	    self.hasAssociation = new ko.computed(function(){
	    	return this.associationClass() != null;
	    }, this);
	    
	    self.setAssociation = function(theClass){
	    	self.associationClass(theClass);
	    };
	    
	    self.clearAssociation = function(){
	    	self.associationClass(null);
	    };
	    
	    /////////////////
	    //   diagram   //
	    /////////////////

	    self.isSelected = function(){
	    	if(window.vm && window.vm.modelSelection.oneThingSelected() == self){
	    		return true;
	    	}
	    	return false;
	    };
	    
	    self.colour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelectedRelation;
	    	}
	    	return ColourRelation;
	    }, this);
	    
	    self.backgroundColour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelectedRelation;
	    	}
	    	return "#FFFFFF";
	    }, this);
	    
	    self.associationColour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelectedRelation;
	    	}
	    	return ColourAssociationRelation;
	    }, this);
	    
		self.getPointsForRelationPointsOnBothSide = function(endARP, endBRP, relationPoints){
			// must do endARP points first
			
			//endARP.theClass.colour("#880000");
			//endBRP.theClass.colour("#008800");
			
			if(endARP.theClass == util.getClassMostInDirectionOfRelationPoint(endARP, endBRP)){
				if(endARP.isLeftOrRight()){
					relationPoints.xy(endARP.offsetX(), endBRP.offsetY());
				}
				if(endARP.isTopOrBottom()){
					relationPoints.xy(endBRP.offsetX(), endARP.offsetY());
				}
			}
			else{
				if(endBRP.isLeftOrRight()){
					relationPoints.xy(endBRP.offsetX(), endARP.offsetY());
				}
				if(endBRP.isTopOrBottom()){
					relationPoints.xy(endARP.offsetX(), endBRP.offsetY());
				}
			}
		};
		
		self.getPointsForRelationPointsPointInwards = function(endARP, endBRP, relationPoints){
			// must do endARP points first
			
			//endARP.theClass.colour("#888800");
			//endBRP.theClass.colour("#008888");
			
			var midX = Math.abs((endARP.offsetX() - endBRP.offsetX())) / 2 + Math.min(endARP.offsetX(), endBRP.offsetX());
			var midY = Math.abs((endARP.offsetY() - endBRP.offsetY())) / 2 + Math.min(endARP.offsetY(), endBRP.offsetY());
			
			if(endARP.isLeftOrRight()){
				relationPoints.xy(midX, endARP.offsetY());
				relationPoints.xy(midX, endBRP.offsetY());
			}else{
				relationPoints.xy(endARP.offsetX(), midY);
				relationPoints.xy(endBRP.offsetX(), midY);
			}
		};
		
		self.getPointsForRelationPointsPointOutwards = function(endARP, endBRP, relationPoints){
			// must do endARP points first
			
			//endARP.theClass.colour("#88FF88");
			//endBRP.theClass.colour("#FF88FF");
			
			var midX = Math.abs((endARP.offsetX() - endBRP.offsetX())) / 2 + Math.min(endARP.offsetX(), endBRP.offsetX());
			var midY = Math.abs((endARP.offsetY() - endBRP.offsetY())) / 2 + Math.min(endARP.offsetY(), endBRP.offsetY());
			
			if(endARP.isLeftOrRight()){
				relationPoints.xy(endARP.offsetX(), midY);
				relationPoints.xy(endBRP.offsetX(), midY);
			}else{
				relationPoints.xy(midX, endARP.offsetY());
				relationPoints.xy(midX, endBRP.offsetY());
			}
		};
		
		self.getAllElseFailsRelationPoints = function(endARP, endBRP, relationPoints){
			//endARP.theClass.colour("#000000");
			//endBRP.theClass.colour("#BBBBBB");
    		if(endARP.isLeftOrRight()){
    			relationPoints.xy(endARP.offsetX(), endBRP.offsetY());
			}else{
				relationPoints.xy(endBRP.offsetX(), endARP.offsetY());
			}
		};
		
	    self.getRelationPointsCollection = function(){
	    	var endARP = self.endA().relationPoints()[self.relationPointIndexEndA()];
	    	var endBRP = self.endB().relationPoints()[self.relationPointIndexEndB()];
			//endARP.theClass.colour("#81C6DD");
			//endBRP.theClass.colour("#81C6DD");

			var relationPoints = new util.RelationPointCollection();
			
	    	relationPoints.xy(endARP.x(), endARP.y());
	    	relationPoints.xy(endARP.offsetX(), endARP.offsetY());
	    	
	    	if(util.areBothRelationPointsOnSameSide(endARP, endBRP))
	    	{
	    		self.getPointsForRelationPointsOnBothSide(endARP, endBRP, relationPoints);
	    	}else if(util.doRelationPointsPointInwards(endARP, endBRP))
	    	{
	    		self.getPointsForRelationPointsPointInwards(endARP, endBRP, relationPoints);
	    	}else if(util.doRelationPointsPointOutwards(endARP, endBRP))
	    	{
	    		self.getPointsForRelationPointsPointOutwards(endARP, endBRP, relationPoints);
	    	}else{
				self.getAllElseFailsRelationPoints(endARP, endBRP, relationPoints);
	    	}

	    	relationPoints.xy(endBRP.offsetX(), endBRP.offsetY());
	    	relationPoints.xy(endBRP.x(), endBRP.y());
	    	
	    	return relationPoints;
	    };
		
	    self.relationPointIndexEndA = new ko.observable(4);
	    self.relationPointIndexEndB = new ko.observable(10);
	    self.points = new ko.computed(function(){
	    	// comma seperated list of space seperated points
	    	// 150 30, 165 15, 180 30
			
	    	return self.getRelationPointsCollection().getPointString();
	    }, this);
	    
		self.endARP = ko.computed(function(){
			return self.endA().relationPoints()[self.relationPointIndexEndA()];
		}, this);
		
		self.endBRP = ko.computed(function(){
			return self.endB().relationPoints()[self.relationPointIndexEndB()];
		}, this);
	    
	    self.midPoint = new ko.computed(function(){
	    	return self.getRelationPointsCollection().getPointAtPercent(0.5);
	    }, this);

	    self.relationNameOrigin = function(){
	    	var midPoint = self.midPoint();
	    	var dimensions = util.getStringWidth(self.name());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	
	    	var startX = midPoint.x - (width / 2); 
	    	var startY =  midPoint.y + (height / 2);
	    	
	    	return new util.RelationPoint(startX, startY);
	    };
	    
	    self.relationNameBoxOrigin = function(){
	    	var midPoint = self.midPoint();
	    	var dimensions = util.getStringWidth(self.name());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	var fudge = 3;
	    	
	    	var startX = midPoint.x - (width / 2); 
	    	var startY =  midPoint.y - (height / 2) + fudge;
	    	
	    	return new util.RelationPoint(startX, startY);
	    };

	    self.relationNameDimensions = function(){
	    	var dimensions = util.getStringWidth(self.name());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	return new util.RelationPoint(width, height);
	    };
	    
	    self.verbAOffsetX = new ko.observable(0);
	    self.verbAOffsetXChangeable = ko.computed({
			read: self.verbAOffsetX,
		    write: function (value) {
		    	
		    	if(value < -60){
		    		self.verbAOffsetX(-60);
		    	}else if(value > 40){
		    		self.verbAOffsetX(40);
		    	}else{
		    		self.verbAOffsetX(value);	
		    	}
		    },
		    owner: this
		});
	    
	    self.verbAOffsetY = new ko.observable(0);
	    self.verbAOffsetYChangeable = ko.computed({
			read: self.verbAOffsetY,
		    write: function (value) {
		    	
		    	if(value < -50){
		    		self.verbAOffsetY(-50);
		    	}else if(value > 50){
		    		self.verbAOffsetY(50);
		    	}else{
		    		self.verbAOffsetY(value);	
		    	}
		    },
		    owner: this
		});
	    
	    self.verbBOffsetX = new ko.observable(0);
	    self.verbBOffsetXChangeable = ko.computed({
			read: self.verbBOffsetX,
		    write: function (value) {
		    	
		    	if(value < -60){
		    		self.verbBOffsetX(-60);
		    	}else if(value > 40){
		    		self.verbBOffsetX(40);
		    	}else{
		    		self.verbBOffsetX(value);	
		    	}
		    },
		    owner: this
		});
	    
	    self.verbBOffsetY = new ko.observable(0);
	    self.verbBOffsetYChangeable = ko.computed({
			read: self.verbBOffsetY,
		    write: function (value) {
		    	
		    	if(value < -50){
		    		self.verbBOffsetY(-50);
		    	}else if(value > 50){
		    		self.verbBOffsetY(50);
		    	}else{
		    		self.verbBOffsetY(value);	
		    	}
		    },
		    owner: this
		});
	    
	    
	    self.verbAPosition = ko.computed(function(){
	    	var endBRP = self.endB().relationPoints()[self.relationPointIndexEndB()];
	    	var x = endBRP.offsetX() + self.verbBOffsetX();
	    	var y = endBRP.offsetY() + self.verbBOffsetY();
	    	return new util.RelationPoint(x,y);
	    }, this);
	    
	    self.verbBPosition = ko.computed(function(){
	    	var endARP = self.endA().relationPoints()[self.relationPointIndexEndA()];
	    	var x = endARP.offsetX() + self.verbAOffsetX();
	    	var y = endARP.offsetY() + self.verbAOffsetY();
	    	return new util.RelationPoint(x,y);
	    }, this);
	    
	    self.cardinalityAPosition = ko.computed(function(){
	    	var verbAPos = self.verbAPosition();
	    	var height = util.getStringWidth(self.name())[1];
	    	return new util.RelationPoint(verbAPos.x,verbAPos.y + height);
	    }, this);
	    
	    self.cardinalityBPosition = ko.computed(function(){
	    	var verbBPos = self.verbBPosition();
	    	var height = util.getStringWidth(self.name())[1];
	    	return new util.RelationPoint(verbBPos.x,verbBPos.y + height);
	    }, this);	    
	    
	};
	this.EntityRelation.prototype.toJSON = function() {
		return "";
	};

};