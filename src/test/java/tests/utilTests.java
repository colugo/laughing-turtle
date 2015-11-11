package test.java.tests;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.metamodel.actionLanguage.DelayToMillisecondsConverter;
import main.java.avii.util.SetHelper;
import main.java.avii.util.Text;

public class utilTests extends TestCase {

	public utilTests(String name) {
		super(name);
	}
	
	
	public void test_replace_not_within_quotes()
	{
		String source = "abc\"abc\"abc";
		String expected ="def\"abc\"def";
		
		Assert.assertEquals(expected, Text.replaceStringWhenNotWithinQuotes(source, "abc", "def"));
	}
	
	public void test_un_wrap_outer_quotes()
	{
		Assert.assertEquals("abc",Text.removeOuterQuotes("\"abc\""));
	}
	
	public void test_un_wrap_outer_quotes1()
	{
		Assert.assertEquals("\"abc",Text.removeOuterQuotes("\"abc"));
	}
	
	public void test_un_wrap_outer_quotes2()
	{
		Assert.assertEquals("abc",Text.removeOuterQuotes("\"abc\" "));
	}
	
	public void test_convert10_milliseconds()
	{
		DelayUnits delayUnit = DelayUnits.MilliSecond;
		int quantity = 10;
		
		Assert.assertEquals(10, DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit));
	}
	
	public void test_convert30_seconds()
	{
		DelayUnits delayUnit = DelayUnits.Second;
		int quantity = 30;
		
		Assert.assertEquals(30000, DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit));
	}
	
	public void test_convert1_minute()
	{
		DelayUnits delayUnit = DelayUnits.Minute;
		int quantity = 1;
		
		Assert.assertEquals(60000, DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit));
	}
	
	public void test_convert4_hours()
	{
		DelayUnits delayUnit = DelayUnits.Hour;
		int quantity = 4;
		
		Assert.assertEquals(14400000, DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit));
	}
	
	public void test_convert2_days()
	{
		DelayUnits delayUnit = DelayUnits.Day;
		int quantity = 2;
		
		Assert.assertEquals(172800000 , DelayToMillisecondsConverter.convertDelayIntoMilliseconds(quantity, delayUnit));
	}

	@SuppressWarnings("unchecked")
	public void test_set_difference()
	{
		ArrayList<Integer> a = new ArrayList<Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		a.add(4);
		b.add(1);
		b.add(2);
		
		Collection<Integer> difAB = (Collection<Integer>) SetHelper.difference(a, b);
		Assert.assertEquals(2, difAB.size());
		Assert.assertTrue(difAB.contains(3));
		Assert.assertTrue(difAB.contains(4));
		
		Collection<Integer> difBA = (Collection<Integer>) SetHelper.difference(b, a);
		Assert.assertEquals(0, difBA.size());

		Collection<Integer> intAB = (Collection<Integer>) SetHelper.intersection(a,b);
		Assert.assertEquals(2, intAB.size());
		Assert.assertTrue(intAB.contains(1));
		Assert.assertTrue(intAB.contains(2));
		
	}
	
}

