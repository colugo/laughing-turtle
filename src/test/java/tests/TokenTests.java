package test.java.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenLiteral;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenTemp;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.util.Text;


public class TokenTests extends TestCase {

	public TokenTests(String name) {
		super(name);
	}
	
	
	public void test_can_identify_literal_token_integer()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("1"); 
		Assert.assertTrue(token instanceof ActionLanguageTokenLiteral);
		ActionLanguageTokenLiteral literalToken = (ActionLanguageTokenLiteral) token;
		Assert.assertTrue(literalToken.getDatatype() instanceof IntegerEntityDatatype);
		Assert.assertEquals("1", literalToken.getValue());
	}
	
	public void test_can_identify_literal_token_floating()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("1.1"); 
		Assert.assertTrue(token instanceof ActionLanguageTokenLiteral);
		ActionLanguageTokenLiteral literalToken = (ActionLanguageTokenLiteral) token;
		Assert.assertTrue(literalToken.getDatatype() instanceof FloatingEntityDatatype);
		Assert.assertEquals("1.1", literalToken.getValue());
	}
	
	public void test_can_identify_literal_token_boolean()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("false"); 
		Assert.assertTrue(token instanceof ActionLanguageTokenLiteral);
		ActionLanguageTokenLiteral literalToken = (ActionLanguageTokenLiteral) token;
		Assert.assertTrue(literalToken.getDatatype() instanceof BooleanEntityDatatype);
		Assert.assertEquals("false", literalToken.getValue());
	}
	
	public void test_can_identify_literal_token_string()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("\"Fred\""); 
		Assert.assertTrue(token instanceof ActionLanguageTokenLiteral);
		ActionLanguageTokenLiteral literalToken = (ActionLanguageTokenLiteral) token;
		Assert.assertTrue(literalToken.getDatatype() instanceof StringEntityDatatype);
		Assert.assertEquals("\"Fred\"", literalToken.getValue());
	}
	
	
	public void test_can_identify_attribute_token()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("fred.Name"); 
		Assert.assertTrue(token instanceof ActionLanguageTokenAttribute);
		ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) token;
		Assert.assertEquals(attributeToken.getInstanceName(),"fred");
		Assert.assertEquals(attributeToken.getAttributeName(),"Name");
	}
	
	
	public void test_can_identify_temp_token()
	{
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken("HUGE"); 
		Assert.assertTrue(token instanceof ActionLanguageTokenTemp);
		ActionLanguageTokenTemp tempToken = (ActionLanguageTokenTemp) token;
		Assert.assertEquals(tempToken.getName(),"HUGE");
	}

	public void test_count_chars_in_string()
	{
		Assert.assertEquals(3,Text.countOccurancesOfCharacterInString('.', "this.is.a.test"));
	}
	
}

