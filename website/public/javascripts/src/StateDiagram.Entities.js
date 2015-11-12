var StateDiagram_Entities = new function() {
	
	var ColourSelected = "#C0FFC0";
	var ColourSelectedEvent = "#22C0FF";
	var ColourState = "#FFFFC0";

	/*
	 * Datatypes
	 * 
	 */
	availableTypes = ['string','boolean','int','float'];
	
	
	/* State Machine
	 * 
	 * 
	 */
	this.StateMachine = function(parentClass){
		var self = this;
		self.theClass = parentClass;
	    self.states = new ko.observableArray();
	    self.specifications = new ko.observableArray();
	    self.instances = new ko.observableArray();
	    
	    self.newStateToTransitionToUUID = new ko.observable();
	    
		self.width = new ko.computed(function(){
			var biggestWidth = 1000;
//			for(var i = 0; i < self.domain.entityClasses().length; i++){
//				var currentClass = self.domain.entityClasses()[i];
//				var currentX = currentClass.x() + currentClass.width();
//				if(currentX > biggestWidth){
//					biggestWidth = currentX;
//				}
//			}
			return biggestWidth + 200;
		}, this);
		
		self.height = new ko.computed(function(){
			var paneHeight = $('#stateDiagramPane').height();
			var biggestHeight = 100;
//			for(var i = 0; i < self.domain.entityClasses().length; i++){
//				var currentClass = self.domain.entityClasses()[i];
//				var currentY = currentClass.y() + currentClass.height();
//				if(currentY > biggestHeight){
//					biggestHeight = currentY;
//				}
//			}
			return Math.max(paneHeight, biggestHeight + 100);
		}, this);
	    
	    self.createState = function(){
	    	var state = new StateDiagram_Entities.EntityState(self);
	    	self.states().push(state);
	    	self.states.valueHasMutated();
	    	return state;
	    };
	    
	    self.createSpecification = function(){
	    	var specification = new StateDiagram_Entities.EventSpecification(self);
	    	self.specifications().push(specification);
	    	return specification;
	    };
	    
	    self.createInstance = function(){
	    	var instance = new StateDiagram_Entities.EventInstance(self);
	    	self.instances().push(instance);
	    	return instance;
	    };
	    
	    self.createInstanceWithStates = function(from, to){
	    	var instance = new StateDiagram_Entities.EventInstance(self);
	    	instance.fromState(from);
	    	instance.toState(to);
	    	self.instances().push(instance);
	    	return instance;
	    };
	    
	    self.deleteState = function(theState){
	    	self.states.remove(theState);
	    };
	    
		self.getStateWithUUID = function(uuid) {
			for ( var i = 0; i < self.states().length; i++) {
				var currentState = self.states()[i];
				if (currentState.uuid == uuid) {
					return currentState;
				}
			}
		};
		
		self.hasStateWithUUID = function(uuid) {
			for ( var i = 0; i < self.states().length; i++) {
				var currentState = self.states()[i];
				if (currentState.uuid == uuid) {
					return true;
				}
			}
			return false;
		};
		
		self.getInstanceWithUUID = function(uuid) {
			for ( var i = 0; i < self.instances().length; i++) {
				var currentInstance = self.instances()[i];
				if (currentInstance.uuid == uuid) {
					return currentInstance;
				}
			}
		};
		
		self.getSpecWithUUID = function(uuid) {
			for ( var i = 0; i < self.specifications().length; i++) {
				var currentSpec = self.specifications()[i];
				if (currentSpec.uuid == uuid) {
					return currentSpec;
				}
			}
		};
		
		self.hasStateWithName = function(name) {
			for ( var i = 0; i < self.states().length; i++) {
				var currentState = self.states()[i];
				if (currentState.name() == name) {
					return true;
				}
			}
			return false;
		};
		
		self.hasSpecWithName = function(name) {
			for ( var i = 0; i < self.specifications().length; i++) {
				var currentSpec = self.specifications()[i];
				if (currentSpec.name() == name) {
					return true;
				}
			}
			return false;
		};
	    
	    self.hasStates = function() {return self.states().length > 0;};
	    self.hasStatesComputed = ko.computed(self.hasStates, this);
	    
	    self.isStateNameValid = function(newName){
			if(self.hasStateWithName(newName)){
				return "A state is already named '" + newName+"'";
			}
			return true;
	    };
	    
	    self.isSpecNameValid = function(newName){
			if(self.hasSpecWithName(newName)){
				return "An event specification is already named '" + newName+"'";
			}
			return true;
	    };
	    
	    self.getRelationPointWithUUID = function(uuid){
			for ( var j = 0; j < self.states().length; j++){
				var currentState = self.states()[j];
				for ( var k= 0; k < currentState.relationPoints().length; k++){
					var rp = currentState.relationPoints()[k];
					if(rp.uuid() == uuid){
						return rp;
					}
				}
			}
	    };
	    
	    self.getDefaultEventSpecification = function(){
	    	for ( var j = 0; j < self.specifications().length; j++){
				var spec = self.specifications()[j];
				if(spec.name() == "DefaultEventSpecification"){
					return spec;
				}
	    	}
	    };
	    
	};
	
	/*
	 * Entity State
	 * 
	 */
	this.EntityState = function(stateMachine) {
		var self = this;
		self.stateMachine = stateMachine;
		self.type = "EntityState";
		self.uuid = util.makeId(10);
		self._procedure = "CREATE fred FROM person;";
		self.procedure = new ko.computed({
				read: function(){
					return self._procedure;
				},
				write: function(value){
					self._procedure = value;
				},
				owner : this
		});
		
		self.name = ko.observable("State_" + self.uuid);
	    self.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromStateName = self.stateMachine.isStateNameValid(value);
	        	if(noErrorsFromStateName == true){
	        		window.vm.renameStateAction(self, value);
	        	}else{
	        		window.vm.actionList.message(noErrorsFromStateName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
		
		
		/////////////////
	    //   diagram   //
	    /////////////////
	    self.isSelected = function(){
	    	if(window.vm && window.vm.modelSelection.oneThingSelected() == self){
	    		return true;
	    	}
	    	return false;
	    };
		
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
	    	var longestLine = 0;
//	    	for(var i = 0; i < self.attributes().length; i++){
//	    		var attributeLength = util.getStringWidth(self.attributes()[i].nameAndType())[0];
//	    		if(attributeLength > longestLine){
//	    			longestLine = attributeLength;
//	    		}
//	    	}
	    	return Math.max(minWidth, nameWidth, longestLine + 4);
	    }, this);
	    
	    self.height = new ko.computed(function(){
	    	var minHeight = 30;
	    	var numberOfLines = 0.5;
	    	var textHeight = util.getStringWidth("A")[1];
	    	var heightWithLines = (2 * textHeight) + (numberOfLines * textHeight);
	    	return Math.max(minHeight, heightWithLines);
	    }, this);
	    
	    self.stateNameX = new ko.computed(function(){
	    	var bbw = util.getStringWidth(self.name())[0];
	    	var halfbbw = bbw / 2;
	        return self.x() + (self.width() / 2) - halfbbw;
	    }, this);
	    
	    self.stateNameY = new ko.computed(function(){
	    	return self.y() + util.getStringWidth(self.name())[1];
	    }, this);
	    
	    self.colour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelected;
	    	}
	    	return ColourState;
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
	    
	};
	this.EntityState.prototype.toJSON = function() {
		return "";
	};
	
	/*
	 * Event Specification
	 * 
	 */
	this.EventSpecification = function(stateMachine) {
		var self = this;
		self.type = "EventSpecification";
		self.uuid = util.makeId(10);
		self.stateMachine = stateMachine;
		self.name = ko.observable("EventSpecification_" + self.uuid);
	    self.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromSpecName = self.stateMachine.isSpecNameValid(value);
	        	if(noErrorsFromSpecName == true){
	        		window.vm.renameSpecAction(self.uuid, self.stateMachine.theClass.uuid, value);
	        	}else{
	        		window.vm.actionList.message(noErrorsFromSpecName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
	    self.params = new ko.observableArray();
	    self.createParam = function(){
	    	var param = new StateDiagram_Entities.EventParam(self);
	    	self.params.push(param);
	    	return param;
	    };
	    
	    self.getParamWithUUID = function(uuid) {
			for ( var i = 0; i < self.params().length; i++) {
				var currentParam = self.params()[i];
				if (currentParam.uuid == uuid) {
					return currentParam;
				}
			}
		};
	    
		self.isParamNameValid = function(newName){
			for ( var i = 0; i < self.params().length; i++) {
				var currentParam = self.params()[i];
				if (currentParam.name() == newName) {
					return "An event param is already named '" + newName+"'";;
				}
			}
			return true;
		};
		
		self.displayText = new ko.computed(function(){
			var output = self.name();
			if(self.params().length == 0){
				return output + "()";
			}
			output += "( ";
			for ( var i = 0; i < self.params().length; i++) {
				var currentParam = self.params()[i];
				output += currentParam.name() + ":" + currentParam.type();
				if( i < self.params().length-1){
					output += ", ";
				}
			}
			output += " )";
			return output;
		}, this);
		
		self.isEditable = function(){
			if(self.stateMachine.getDefaultEventSpecification().uuid == self.uuid){
				return false;
			}
			return true;
		};
		
	};
	this.EventSpecification.prototype.toJSON = function() {
		return "";
	};
	
	/*
	 * Event Instance
	 * 
	 */
	this.EventInstance = function(stateMachine) {
		var self = this;
		self.stateMachine = stateMachine;
		self.type = "EventInstance";
		self.uuid = util.makeId(10);
		self.specification = new ko.observable( stateMachine.getDefaultEventSpecification() );
		self.fromState = new ko.observable();
		self.toState = new ko.observable();
		
		self.changableSpecification = new ko.computed({
	        read: function(){
	        	if(self.specification() == null){
	        		return "";
	        	}
	        	return self.specification().uuid;
	        },
	        write: function (value) {
	        	if(value != self.specification().uuid){
	        		window.vm.changeStateTransitionAction(self.uuid, self.fromState().uuid, self.toState().uuid, value);
	        	}
	        },
	        owner: this
	    });
	
		/////////////////
	    //   diagram   //
	    /////////////////
		
		self.isSelected = function(){
	    	if(window.vm && window.vm.modelSelection.oneThingSelected() == self){
	    		return true;
	    	}
	    	return false;
	    };
		
		self.displayText = new ko.computed(function(){
			if(self.specification() == null){
				return "()";
			}
			return self.specification().displayText();
		}, this);

		self.toRelationPointIndex = new ko.observable(1);
		self.fromRelationPointIndex = new ko.observable(4);
		
		self.toRelationPoint = function(){
			return self.toState().relationPoints()[self.toRelationPointIndex()];
		};
		
		self.fromRelationPoint = function(){
			return self.fromState().relationPoints()[self.fromRelationPointIndex()];
		};
		
		
		self._fromDragX = new ko.observable(null);
		self.fromDragX = new ko.computed({
	        read: function(){
	        	if(self.fromState() == null){
	        		return 0;
	        	}
	        	if(self._fromDragX() == null){
	        		self._fromDragX(self.fromRelationPoint().offsetX() - self.fromRelationPoint().x());
	        	}
	        	return self.fromRelationPoint().x() + self._fromDragX();
	        },
        	write: function (value) {
        		self._fromDragX(value);
        	},
        	owner: this
	    });
		self._fromDragY = new ko.observable(null);
		self.fromDragY = new ko.computed({
	        read: function(){
	        	if(self.fromState() == null){
	        		return 0;
	        	}
	        	if(self._fromDragY() == null){
	        		self._fromDragY(self.fromRelationPoint().offsetY() - self.fromRelationPoint().y());
	        	}
	        	return self.fromRelationPoint().y() + self._fromDragY();
	        },
        	write: function (value) {
        		self._fromDragY(value);
        	},
        	owner: this
	    });
		self._toDragX = new ko.observable(null);
		self.toDragX = new ko.computed({
	        read: function(){
	        	if(self.toState() == null){
	        		return 0;
	        	}
	        	if(self._toDragX() == null){
	        		self._toDragX(self.toRelationPoint().offsetX() - self.toRelationPoint().x());
	        	}
	        	return self.toRelationPoint().x() + self._toDragX();
	        },
        	write: function (value) {
        		self._toDragX(value);
        	},
        	owner: this
	    });
		self._toDragY = new ko.observable(null);
		self.toDragY = new ko.computed({
	        read: function(){
	        	if(self.toState() == null){
	        		return 0;
	        	}
	        	if(self._toDragY() == null){
	        		self._toDragY(self.toRelationPoint().offsetY() - self.toRelationPoint().y());
	        	}
	        	return self.toRelationPoint().y() + self._toDragY();
	        },
        	write: function (value) {
        		self._toDragY(value);
        	},
        	owner: this
	    });
		
		
		
		self.path = new ko.computed(function(){
			if(self.fromState() == undefined  || self.toState() == undefined ){
				return "M 100 100 C 50 20 150 60 300 120";
			}
			var output = "M ";
			output += self.fromRelationPoint().x() + " " + self.fromRelationPoint().y() + " C";
			output += " " + self.fromDragX() + " " + self.fromDragY();
			output += " " + self.toDragX() + " " + self.toDragY();
			output += " " + self.toRelationPoint().x() + " " + self.toRelationPoint().y();
			return output;
		}, this);
		
		self.arrowHeadPath = new ko.computed(function(){
			if(self.fromState() == undefined  || self.toState() == undefined ){
				return "M 100 100 C 50 20 150 60 300 120";
			}
			var headlen = 15;   // length of head in pixels
		    var angle = Math.atan2(self.toRelationPoint().y()-self.toDragY(),self.toRelationPoint().x()-self.toDragX());
			
		    var aMinusPIon6 = angle-Math.PI/6;
		    var aPlusPIon6 = angle+Math.PI/6;
		    
		    var relationPoints = new util.RelationPointCollection();
		    relationPoints.xy(self.toRelationPoint().x() - headlen * Math.cos(aMinusPIon6), self.toRelationPoint().y() - headlen * Math.sin(aMinusPIon6));
		    relationPoints.xy(self.toRelationPoint().x(), self.toRelationPoint().y());
		    relationPoints.xy(self.toRelationPoint().x() - headlen*Math.cos(aPlusPIon6),self.toRelationPoint().y()-headlen*Math.sin(aPlusPIon6));
		    
		    return relationPoints.getPointString();
		}, this);
		
		self.B1 = function(t) { return t*t*t;};
		self.B2 = function(t) { return 3*t*t*(1-t);};
		self.B3 = function(t) { return 3*t*(1-t)*(1-t);};
		self.B4 = function(t) { return (1-t)*(1-t)*(1-t);};
		
		self.halfWayPoint = function(){
			var x = self.fromRelationPoint().x() * self.B1(0.5) + self.fromDragX() * self.B2(0.5) + self.toDragX() * self.B3(0.5) + self.toRelationPoint().x() * self.B4(0.5);
			var y = self.fromRelationPoint().y() * self.B1(0.5) + self.fromDragY() * self.B2(0.5) + self.toDragY() * self.B3(0.5) + self.toRelationPoint().y() * self.B4(0.5);
			var pos = new util.RelationPoint(x,y);
			return pos;
		};
		
	    self.displayTextOrigin = function(){
	    	var midPoint = self.halfWayPoint();
	    	var dimensions = util.getStringWidth(self.displayText());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	
	    	var startX = midPoint.x - (width / 2); 
	    	var startY =  midPoint.y + (height / 2);
	    	
	    	return new util.RelationPoint(startX, startY);
	    };
		
	    self.displayTextBoxOrigin = function(){
	    	var midPoint = self.halfWayPoint();
	    	var dimensions = util.getStringWidth(self.displayText());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	var fudge = 3;
	    	
	    	var startX = midPoint.x - (width / 2); 
	    	var startY =  midPoint.y - (height / 2) + fudge;
	    	
	    	return new util.RelationPoint(startX, startY);
	    };

	    self.displayTextDimensions = function(){
	    	var dimensions = util.getStringWidth(self.displayText());
	    	var width = dimensions[0];
	    	var height = dimensions[1];
	    	return new util.RelationPoint(width, height);
	    };
	    
	    self.backgroundColour = new ko.computed(function(){
	    	if(self.isSelected()){
	    		return ColourSelectedEvent;
	    	}
	    	return "#FFFFFF";
	    }, this);
	    
	};
	this.EventInstance.prototype.toJSON = function() {
		return "";
	};
	
	/*
	 * Event Param
	 * 
	 */
	this.EventParam = function(spec) {
		var self = this;
		self.uuid = util.makeId(10);
		self.type = ko.observable(availableTypes[0]);
		self.spec = new ko.observable(spec);
		self.name = new ko.observable("EventParam_" + self.uuid);
	    self.renamableName = ko.computed({
	        read: this.name,
	        write: function (value) {
	        	var noErrorsFromParamName = self.spec().isParamNameValid(value);
	        	if(noErrorsFromParamName == true){
	        		window.vm.renameParamAction(self.spec().uuid, self.spec().stateMachine.theClass.uuid, self.uuid,  value);
	        	}else{
	        		window.vm.actionList.message(noErrorsFromParamName);
	        		self.name.valueHasMutated();
	        	}
	        },
	        owner: this
	    });
		this.changableType = ko.computed({
	        read: this.type,
	        write: function (value) {
	        	window.vm.changeParamTypeAction(self.spec().uuid, self.spec().stateMachine.theClass.uuid, self.uuid,  value);
	        },
	        owner: this
	    });
	};
	this.EventParam.prototype.toJSON = function() {
		return "";
	};
	
};


