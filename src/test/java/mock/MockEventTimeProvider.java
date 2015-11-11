package test.java.mock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.metamodel.actionLanguage.DelayToMillisecondsConverter;
import main.java.avii.simulator.events.IEventTimeProvider;
import main.java.avii.simulator.events.SimulatedEventTimeHelper;

public class MockEventTimeProvider implements IEventTimeProvider {

	private long _currentTime = SimulatedEventTimeHelper.getCurrentTimeHelper();

	public long getCurrentTime() {
		return this._currentTime;
	}

	public void addOffset(int quantity, DelayUnits delayUnit) {
		this._currentTime += DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit);
	}

}

