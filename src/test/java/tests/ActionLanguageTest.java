package test.java.tests;

/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageReader;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_BlankLine;


public class ActionLanguageTest extends TestCase {

	public ActionLanguageTest(String name) {
		super(name);
	}
	
	private void quelch(String input)
	{
	}
	
	public void test_invalid_syntax_throws_exception()
	{
		String input = "SELECT ANY Books RELATED BY self->R1-R2;\nCREATE Bob FROM Friends;\nnot a syntax line";
		InputStream is = new ByteArrayInputStream(input.getBytes());
		ActionLanguageReader reader = new ActionLanguageReader(is);
		try {
			reader.read();
			fail();
		} catch (InvalidActionLanguageSyntaxException e) {
		}
	}
	
	public void test() throws InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException
	{
		assertEquals("CANCEL shutdownTimeOut FROM self TO unit;",ActionLanguageSupportedSyntax.getSyntaxForLine("CANCEL shutdownTimeOut FROM self TO unit;").toString());
		assertEquals("CREATE invalidValueError FROM Error;",ActionLanguageSupportedSyntax.getSyntaxForLine("CREATE invalidValueError FROM Error;").toString());
		assertEquals("DELETE invalidValueError;",ActionLanguageSupportedSyntax.getSyntaxForLine("DELETE invalidValueError;").toString());
		assertEquals("ELSE",ActionLanguageSupportedSyntax.getSyntaxForLine("ELSE").toString());
		assertEquals("END FOR;",ActionLanguageSupportedSyntax.getSyntaxForLine("END FOR;").toString());
		assertEquals("END IF;",ActionLanguageSupportedSyntax.getSyntaxForLine("END IF;").toString());
		assertEquals("FOR parts IN relatedParts DO",ActionLanguageSupportedSyntax.getSyntaxForLine("FOR parts IN relatedParts DO").toString());
		assertEquals("GENERATE someEvent(Code=\"q12\", Quantity=4) TO shipping;",ActionLanguageSupportedSyntax.getSyntaxForLine("GENERATE someEvent(Quantity=4, Code=\"q12\") TO shipping;").toString());
		assertEquals("GENERATE someEvent(Code=\"q12\", Quantity=4) TO shipping DELAY 1MilliSecond;",ActionLanguageSupportedSyntax.getSyntaxForLine("GENERATE someEvent(Quantity=4, Code=\"q12\") TO shipping DELAY 1MilliSecond;").toString());
		assertEquals("GENERATE someEvent(Code=\"q12\", Quantity=4) TO shipping CREATOR;",ActionLanguageSupportedSyntax.getSyntaxForLine("GENERATE someEvent(Quantity=4, Code=\"q12\") TO shipping CREATOR;").toString());
		assertEquals("GENERATE someEvent(Code=\"q12\", Quantity=4) TO shipping CREATOR DELAY 1MilliSecond;",ActionLanguageSupportedSyntax.getSyntaxForLine("GENERATE someEvent(Quantity=4, Code=\"q12\") TO shipping CREATOR DELAY 1MilliSecond;").toString());

		//assertEquals("(HUGE) = (150.0);",ActionLanguageSupportedSyntax.getSyntaxForLine("HUGE = 150.0;").asString());
		assertEquals("IF EMPTY someInstances THEN",ActionLanguageSupportedSyntax.getSyntaxForLine("IF EMPTY someInstances THEN").toString());
		assertEquals("IF NOT EMPTY someInstances THEN",ActionLanguageSupportedSyntax.getSyntaxForLine("IF NOT EMPTY someInstances THEN").toString());
		assertEquals("IF a==b THEN",ActionLanguageSupportedSyntax.getSyntaxForLine("IF a==b THEN").toString());
		assertEquals("RECLASSIFY TO b;",ActionLanguageSupportedSyntax.getSyntaxForLine("RECLASSIFY TO b;").toString());
		assertEquals("RELATE instance1 TO instance2 ACROSS R;",ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE instance1 TO instance2 ACROSS R;").toString());
		assertEquals("RELATE instance1 TO instance2 ACROSS R.\"sample verb phrase\";",ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE instance1 TO instance2 ACROSS R.\"sample verb phrase\";").toString());
		assertEquals("RELATE instance1 TO instance2 ACROSS R CREATING Instance3;",ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE instance1 TO instance2 ACROSS R CREATING Instance3;").toString());
		assertEquals("RELATE instance1 TO instance2 ACROSS R.\"sample verb phrase\" CREATING Instance3;",ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE instance1 TO instance2 ACROSS R.\"sample verb phrase\" CREATING Instance3;").toString());
		assertEquals("RETURN;",ActionLanguageSupportedSyntax.getSyntaxForLine("RETURN;").toString());
		assertEquals("SELECT ANY Books RELATED BY self->R1.\"test\"->R2;",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ANY Books RELATED BY self->R1.\"test\"->R2;").toString());
		assertEquals("SELECT ANY Books RELATED BY self->R1.\"test\"->R2 WHERE selected.name == \"Fred\";",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ANY Books RELATED BY self->R1.\"test\"->R2 WHERE selected.name == \"Fred\";").toString());
		assertEquals("SELECT ANY Books FROM INSTANCES OF Publication;",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ANY Books FROM INSTANCES OF Publication;").toString());
		assertEquals("SELECT ANY Books FROM INSTANCES OF Publication WHERE selected.price > 5.26;",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ANY Books FROM INSTANCES OF Publication WHERE selected.price > 5.26;").toString());
		assertEquals("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4;",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4;").toString());
		assertEquals("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4.\"verb phrase\";",ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4.\"verb phrase\";").toString());
		assertEquals("UNRELATE Fred FROM Wilma ACROSS R4;",ActionLanguageSupportedSyntax.getSyntaxForLine("UNRELATE Fred FROM Wilma ACROSS R4;").toString());
		assertEquals("UNRELATE Fred FROM Wilma ACROSS R4.\"verb phrase\";",ActionLanguageSupportedSyntax.getSyntaxForLine("UNRELATE Fred FROM Wilma ACROSS R4.\"verb phrase\";").toString());
		assertEquals("FAIL \"Hello,\" + \" World\";",ActionLanguageSupportedSyntax.getSyntaxForLine("FAIL \"Hello,\" + \" World\";").toString());
	
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE Fred TO Wilma ACROSS R4.\"verb phrase\";").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE Fred TO Wilma ACROSS R4.\"a\";").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("UNRELATE Fred FROM Wilma ACROSS R4.\"a\";").toString());
		
		
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("RELATE Fred TO Wilma ACROSS R4R3.\"a\" CREATING F;").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("UNRELATE Fred FROM Wilma ACROSS R4R3;").toString());
		
		
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4;").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4.\"e\";").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("	SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4.\"e\";").toString());
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("#	SELECT ONE Pebbles THAT RELATES Fred TO Wilma ACROSS R4.\"e\";").toString());
		
		quelch(ActionLanguageSupportedSyntax.getSyntaxForLine("SELECT ANY Books RELATED BY self->R1->R2;").toString());
		
		Assert.assertEquals(Syntax_BlankLine.class,ActionLanguageSupportedSyntax.getSyntaxForLine("").getClass());
	
	}


	
}

