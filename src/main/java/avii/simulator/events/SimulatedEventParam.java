package main.java.avii.simulator.events;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityEventParam;

public class SimulatedEventParam {

		private IEntityDatatype _type;
		private String _name;

		public SimulatedEventParam(EntityEventParam entityParam) {
			this._name = entityParam.getName();
			this._type = entityParam.getType();
		}

		public String getName() {
			return this._name;
		}

		public IEntityDatatype getType() {
			return this._type;
		}

	
}
