var StateDiagram_Actions = new function() {
	
	/*
	 * Add State Action
	 * 
	 */
	this.AddStateAction = function(classAddingTo) {
		var self = this;
		self.classAddingTo = classAddingTo;
		self.uuidOfClass = classAddingTo.uuid;
		self.newState = null;
		self.uuidOfState = null;
		self.type = "AddStateAction";
		self.apply = function(domain) {
			self.newState = self.classAddingTo.stateMachine.createState();
			self.uuidOfState = self.newState.uuid;
		};
		
		self.undo = function(domain) {
			 self.classAddingTo.stateMachine.deleteState(self.newState);
		};
	};
	
	
	/*
	 * Rename State
	 * 
	 */
	this.RenameState = function(stateUUID, newName) {
		var self = this;
		self.type = "RenameStateAction";
		self.uuidOfState = stateUUID;
		self.uuidOfClass = "";
		self.newName = newName;
		self.oldStateName = "";
		self.apply = function(domain) {
			var theState = domain.getStateWithUUID(self.uuidOfState);
			self.uuidOfClass = theState.stateMachine.theClass.uuid;
			
			var errorFromStateName = theState.stateMachine.isStateNameValid(self.newName);
			if(errorFromStateName != true){
				return errorFromStateName;
			}
			
			self.oldStateName = theState.name();
			theState.name(self.newName);
		};
		
		self.undo = function(domain) {
			var theState = domain.getStateWithUUID(self.uuidOfState);
			theState.name(self.oldStateName);
		};
	};
	
	
	/*
	 * New State Transition
	 * 
	 */
	this.NewStateTransitionAction = function(fromUUID, toUUID) {
		var self = this;
		self.type = "NewStateTransitionAction";
		self.fromUUID = fromUUID;
		self.toUUID = toUUID;
		self.classUUID = null;
		self.theInstance = null;
		self.instanceUUID = null;
		self.apply = function(domain) {
			var fromState = domain.getStateWithUUID(self.fromUUID);
			var toState = domain.getStateWithUUID(self.toUUID);
			self.classUUID = fromState.stateMachine.theClass.uuid;
			self.theInstance = fromState.stateMachine.createInstance();
			self.instanceUUID = self.theInstance.uuid;
			self.theInstance.fromState(fromState);
			self.theInstance.toState(toState);
			self.theInstance.stateMachine.instances.valueHasMutated();
		};
		self.undo = function(domain) {
			self.theInstance.stateMachine.instances.remove(self.theInstance);
		};
	};
	
	/*
	 * Change State Transition
	 * 
	 */
	this.ChangeStateTransitionAction = function(instanceUUID, fromUUID, toUUID, specUUID) {
		var self = this;
		self.type = "ChangeStateTransitionAction";
		self.fromUUID = fromUUID;
		self.toUUID = toUUID;
		self.instanceUUID = instanceUUID;
		self.specUUID = specUUID;
		self.classUUID = null;
		self.theInstance = null;
		self.theOldSpecification = null;
		self.theOldFromState = null;
		self.theOldToState = null;
		self.apply = function(domain) {
			var newFromState = domain.getStateWithUUID(self.fromUUID);
			var newToState = domain.getStateWithUUID(self.toUUID);
			var theNewSpec = newFromState.stateMachine.getSpecWithUUID(specUUID);

			self.classUUID = newFromState.stateMachine.theClass.uuid;
			self.theInstance = newFromState.stateMachine.getInstanceWithUUID(instanceUUID);
			self.theOldSpecification = self.theInstance.specification();
			self.theOldFromState = self.theInstance.fromState();
			self.theOldToState = self.theInstance.toState();
			
			self.theInstance.specification(theNewSpec);
			self.theInstance.fromState(newFromState);
			self.theInstance.toState(newToState);

		};
		self.undo = function(domain) {
			self.theInstance.specification(self.theOldSpecification);
			self.theInstance.fromState(self.theOldFromState);
			self.theInstance.toState(self.theOldToState);
		};
	};
	
	/*
	 * New Event Specification Action
	 * 
	 */
	this.NewEventSpecificationAction = function(theClass) {
		var self = this;
		self.type = "NewEventSpecificationAction";
		self.classUUID = theClass.uuid;
		self.theClass = theClass;
		self.specificationUUID = null;
		self.theSpecification = null;
		self.apply = function(domain) {
			self.theSpecification = self.theClass.stateMachine.createSpecification();
			self.specificationUUID = self.theSpecification.uuid;
			self.theClass.stateMachine.specifications.valueHasMutated();
		};
		self.undo = function(domain) {
			self.theClass.stateMachine.specifications.remove(self.theSpecification);
		};
	};
	
	
	/*
	 * Rename Spec
	 * 
	 */
	this.RenameSpecAction = function(specUUID, classUUID, newName) {
		var self = this;
		self.type = "RenameSpecAction";
		self.uuidOfSpec = specUUID;
		self.uuidOfClass = classUUID;
		self.newName = newName;
		self.oldName = "";
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			
			var errorFromSpecName = theClass.stateMachine.isSpecNameValid(self.newName);
			if(errorFromSpecName != true){
				return errorFromSpecName;
			}
			
			self.oldName = theSpec.name();
			theSpec.name(self.newName);
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			theSpec.name(self.oldName);
		};
	};
	
	
	/*
	 * Add Param Action
	 * 
	 */
	this.AddParamAction = function(specAddingTo) {
		var self = this;
		self.specAddingTo = specAddingTo;
		self.uuidOfSpec = specAddingTo.uuid;
		self.uuidOfClass = self.specAddingTo.stateMachine.theClass.uuid;
		self.newParam = null;
		self.uuidOfParam = null;
		self.type = "AddParamAction";
		self.apply = function(domain) {
			self.newParam = self.specAddingTo.createParam();
			self.uuidOfParam = self.newParam.uuid;
		};
		
		self.undo = function(domain) {
			 self.specAddingTo.params.remove(self.newParam);
		};
	};
	
	/*
	 * Rename Event Param
	 * 
	 */
	this.RenameSpecParamAction = function(specUUID, classUUID, paramUUID, newName) {
		var self = this;
		self.type = "RenameSpecParamAction";
		self.uuidOfSpec = specUUID;
		self.uuidOfClass = classUUID;
		self.uuidOfParam = paramUUID;
		self.newName = newName;
		self.oldName = "";
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			var theParam = theSpec.getParamWithUUID(self.uuidOfParam);
			
			var errorFromParamName = theSpec.isParamNameValid(self.newName);
			if(errorFromParamName != true){
				return errorFromParamName;
			}
			
			self.oldName = theParam.name();
			theParam.name(self.newName);
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			var theParam = theSpec.getParamWithUUID(self.uuidOfParam);
			theParam.name(self.oldName);
		};
	};
	
	
	/*
	 * Delete Event Param
	 * 
	 */
	this.DeleteSpecParamAction = function(specUUID, classUUID, paramUUID, newName) {
		var self = this;
		self.type = "DeleteSpecParamAction";
		self.uuidOfSpec = specUUID;
		self.uuidOfClass = classUUID;
		self.uuidOfParam = paramUUID;
		self.theParam = null;
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			self.theParam = theSpec.getParamWithUUID(self.uuidOfParam);
			theSpec.params.remove(self.theParam);
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			theSpec.params.push(self.theParam);
		};
	};
	
	
	/*
	 * Change Param Type Action
	 * 
	 */
	this.ChangeParamTypeAction = function(specUUID, classUUID, paramUUID, newType) {
		var self = this;
		self.uuidOfSpec = specUUID;
		self.uuidOfClass = classUUID;
		self.uuidOfParam = paramUUID;
		self.newType = newType;
		self.oldType = null;
		self.type = "ChangeParamTypeAction";
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			var theParam = theSpec.getParamWithUUID(self.uuidOfParam);
			self.oldType = theParam.type();
			theParam.type(newType);
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
			var theParam = theSpec.getParamWithUUID(self.uuidOfParam);
			theParam.type(self.oldType);
		};
	};
	
	/*
	 * Delete Spec
	 * 
	 */
	this.DeleteSpecificationAction = function(specUUID, classUUID) {
		var self = this;
		self.type = "DeleteSpecAction";
		self.uuidOfSpec = specUUID;
		self.uuidOfClass = classUUID;
		self.theSpec = null;
		self.instancesOnOldSpec = [];
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			var defaultSpec = theClass.stateMachine.getDefaultEventSpecification();
			
			self.theSpec = theClass.stateMachine.getSpecWithUUID(self.uuidOfSpec);
		
			for ( var i = 0; i < theClass.stateMachine.instances().length; i++) {
				var currentInstance = theClass.stateMachine.instances()[i];
				if(currentInstance.specification().uuid == self.uuidOfSpec){
					self.instancesOnOldSpec.push(currentInstance);
					currentInstance.specification(defaultSpec);
				}
			}
			
			self.theSpec.stateMachine.specifications.remove(self.theSpec);
			
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			for ( var i = 0; i < self.instancesOnOldSpec.length; i++) {
				var currentInstance = self.instancesOnOldSpec[i];
				currentInstance.specification(self.theSpec);
			}
			self.theSpec.stateMachine.specifications.push(self.theSpec);
		};
	};
	
};