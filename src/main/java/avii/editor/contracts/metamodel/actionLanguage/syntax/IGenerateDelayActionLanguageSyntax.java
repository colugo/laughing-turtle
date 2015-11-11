package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax;

import java.util.HashMap;

public interface IGenerateDelayActionLanguageSyntax extends IGenerateActionLanguageSyntax{

	/*private static long calculateDelay(String input) {
		long multiplicand = 0;
		long quotient = 0;
		//30MilliSeconds
		if (input.matches("\\d\\d*MilliSecond(s?)")) {
			multiplicand = 1;
		}
		//30Seconds
		if (input.matches("\\d\\d*Second(s?)")) {
			multiplicand = 1000;
		}
		//1Minute
		if (input.matches("\\d\\d*Minute(s?)")) {
			multiplicand = 1000 * 60;
		}
		//4Hours
		if (input.matches("\\d\\d*Hour(s?)")) {
			multiplicand = 1000 * 60 * 60;
		}
		//2Days
		if (input.matches("\\d\\d*Day(s?)")) {
			multiplicand = 1000 * 60 * 60 * 24;
		}

		Pattern p = Pattern.compile("(\\d+).*");
		Matcher m = p.matcher(input);

		if (m.find()) {
		    quotient = Long.parseLong(m.group(1));
		}		
		return quotient * multiplicand;
	}
*/
	
	public static String Regex = "(\\d+)(\\S+)";
	public enum DelayUnits {MilliSecond, Second, Minute, Hour, Day, Invalid};
	public String getDelayQuantity();
	public DelayUnits getDelayUnit();
	public String getRawDelayUnit();
	@SuppressWarnings("serial")
	public HashMap<String,DelayUnits> DelayUnitsLookup = new HashMap<String,DelayUnits>()
	{{
		put("millisecond",DelayUnits.MilliSecond);
		put("second",DelayUnits.Second);
		put("minute",DelayUnits.Minute);
		put("hour",DelayUnits.Hour);
		put("day",DelayUnits.Day);
		put("milliseconds",DelayUnits.MilliSecond);
		put("seconds",DelayUnits.Second);
		put("minutes",DelayUnits.Minute);
		put("hours",DelayUnits.Hour);
		put("days",DelayUnits.Day);
	}};
	
	
}
