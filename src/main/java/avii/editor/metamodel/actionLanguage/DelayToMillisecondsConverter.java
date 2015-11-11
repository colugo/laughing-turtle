package main.java.avii.editor.metamodel.actionLanguage;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;

public class DelayToMillisecondsConverter {

	@SuppressWarnings("incomplete-switch")
	public static long convertDelayIntoMilliseconds(int quantity, DelayUnits delayUnit) {

		long multiplicand = 0;

		switch (delayUnit) {
		case MilliSecond:
			multiplicand = 1;
			break;
		case Second:
			multiplicand = 1000;
			break;
		case Minute:
			multiplicand = 60000; //1000 * 60
			break;
		case Hour:
			multiplicand = 3600000; //1000 * 60 * 60
			break;
		case Day:
			multiplicand = 86400000; //1000 * 60 * 60 * 24
			break;
		}

		return quantity * multiplicand;
	}

}
