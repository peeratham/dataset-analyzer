package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.TestUtil;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class BlockTest {
	JSONParser jsonParser = new JSONParser();
	Parser parser = new Parser();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStringifyNoBlockKind() throws Exception {
		String input = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(input);
		Block b = Parser.loadBlock(jsonInput);
		assertEquals(b.toString(), "say 'Hello!' for 2 secs");
		
	}
	
	@Test
	public void testStringifyWithBlockKind() throws Exception {
		String input = "[\"changeGraphicEffect:by:\", \"color\", 25]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(input);
		Block b = Parser.loadBlock(jsonInput);
		assertEquals(b.toString(), "change 'color' effect by 25");
	}
	
	@Test
	public void testStringifyOnNestedBlock() throws Exception {
		String input = "[\"doIf\", [\"<\", \"1\", \"2\"], [[\"broadcast:\", \"message1\"], [\"changeGraphicEffect:by:\", \"color\", 25]]]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(input);
		Block b = Parser.loadBlock(jsonInput);
		String expectResult = "if ('1' < '2') then\n    broadcast 'message1'\n    change 'color' effect by 25\nend";
		assertEquals(b.toString(), expectResult);

	}

	@Test
	public void testToStringOnDoubleNestedBlock() throws Exception {
		String input = "[\"doIf\", [\"<\", 1, 2], \n  "
				+ "[[\"broadcast:\", \"message1\"],\n  "
				+ "[\"doIf\", [\"<\", 1, 2], \n    "
				+ "[[\"broadcast:\", \"message1\"],"
				+ "[\"changeGraphicEffect:by:\", \"color\", 25]]],"
				+ "\n\t  [\"changeGraphicEffect:by:\", \"color\", 25]]]";
		JSONArray jsonInput = (JSONArray) jsonParser.parse(input);
		Block b = Parser.loadBlock(jsonInput);
		
		String expectResult = "if (1 < 2) then\n    broadcast 'message1'\n    if (1 < 2) then\n        broadcast 'message1'\n        change 'color' effect by 25\n    end\n    change 'color' effect by 25\nend";
		assertEquals(expectResult, b.toString());
	}
	
	@Test
	public void testBlockEquals() throws Exception {
		String inputRHS = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		JSONArray jsonInputRHS = (JSONArray) jsonParser.parse(inputRHS);
		Block rhs = Parser.loadBlock(jsonInputRHS);
		
		String inputLHS = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		JSONArray jsonInputLHS = (JSONArray) jsonParser.parse(inputLHS);
		Block lhs = Parser.loadBlock(jsonInputLHS);
		assertEquals(lhs, rhs);
	}
	
	@Test
	public void testToStringOnEmptyBlockInput() throws Exception {
		String stringInput = Util.retrieveProjectOnline(TestConstant.PARSER_TEST_PROJECT);
		JSONArray scriptableInput = TestUtil.getScripts(stringInput,TestConstant.TEST_EMPTYBLOCKINPUT);
		Script script = Parser.loadScript(scriptableInput.get(0));
	}
	
	@Test
	public void testBlockEqualsMethod() throws ParseException, ParsingException {
		String input = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input);
		JSONArray jsonInput2 = (JSONArray) jsonInput1.clone();
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = Parser.loadBlock(jsonInput2);
		assertEquals(b1,b2);
	}
	

	@Test
	public void testEqualsForIFBlock() throws ParseException, ParsingException {
		String input = "[\"doIf\", [\"<\", \"1\", \"2\"], [[\"broadcast:\", \"message1\"], [\"changeGraphicEffect:by:\", \"color\", 25]]]";
		String input2 = "[\"doIf\", [\"<\", \"1\", \"2\"], [[\"broadcast:\", \"message2\"], [\"changeGraphicEffect:by:\", \"color\", 25]]]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input);
		JSONArray jsonInput2 = (JSONArray) jsonParser.parse(input);
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = Parser.loadBlock(jsonInput2);
		
		assertEquals(b1,b2);
		JSONArray jsonInput3 = (JSONArray) jsonParser.parse(input2);
		Block b3 = Parser.loadBlock(jsonInput3);
		assertNotEquals(b1, b3);
	}
	
	@Test
	public void testEqualsForIfElseBlock() throws ParseException, ParsingException {
		String input1 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]],[[\"doIf\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]]],[\"turnLeft:\",15]]]";
		String input2 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]],[[\"doIf\",[\"=\",\"1\",\"1\"],[[\"forward:\",20]]],[\"turnLeft:\",15]]]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input1);
		JSONArray jsonInput2= (JSONArray) jsonParser.parse(input1); 
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = Parser.loadBlock(jsonInput2);
		assertEquals(b1,b2);
		
		JSONArray jsonInput3 = (JSONArray) jsonParser.parse(input2);
		Block b3 = Parser.loadBlock(jsonInput3);
		assertNotEquals(b1,b3);
	}
	
	@Test
	public void testBlockClone() throws ParsingException, ParseException{
		String input = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input);
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = b1.copy();
		assertEquals(b1,b2);
		assertTrue(b1!=b2);
	}
	
	@Test
	public void testBlockCloneForBlockWithNestedBlockArg() throws ParseException, ParsingException{
		String input1 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]],[[\"doIf\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]]],[\"turnLeft:\",15]]]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input1);
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = b1.copy();
		assertEquals(b1,b2);
		assertTrue(b1!=b2);
	}
	
	@Test
	public void testCommandMatchForSimpleBlock() throws ParseException, ParsingException{
		String input1 = "[\"say:duration:elapsed:from:\", \"Hello!\", 2]";
		String input2 = "[\"say:duration:elapsed:from:\", \"Howdy!\", 3]";
		String input3 = "[\"changeGraphicEffect:by:\", \"color\", 25]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input1);
		JSONArray jsonInput2 = (JSONArray) jsonParser.parse(input2);
		JSONArray jsonInput3 = (JSONArray) jsonParser.parse(input3);
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = Parser.loadBlock(jsonInput2);
		Block b3 = Parser.loadBlock(jsonInput3);
		assertNotEquals(b1,b2);
		b1.commandMatches(b2);
		assertTrue(b1.commandMatches(b2));
		b1.commandMatches(b3);
		assertFalse(b1.commandMatches(b3));
	}
	
	@Test
	public void testCommandMatchForNestedBlock() throws ParseException, ParsingException {
		String input1 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]],[[\"doIf\",[\"=\",\"1\",\"1\"],[[\"forward:\",10]]],[\"turnLeft:\",15]]]";
		String input2 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"forward:\",13]],[[\"doIf\",[\"=\",\"1\",\"2\"],[[\"forward:\",20]]],[\"turnLeft:\",30]]]";
		String input3 = "[\"doIfElse\",[\"=\",\"1\",\"1\"],[[\"turnLeft:\",30]],[[\"doIf\",[\"=\",\"1\",\"2\"],[[\"forward:\",20]]],[\"forward:\",13]]]";
		JSONArray jsonInput1 = (JSONArray) jsonParser.parse(input1);
		JSONArray jsonInput2= (JSONArray) jsonParser.parse(input2);
		JSONArray jsonInput3= (JSONArray) jsonParser.parse(input3); 
		Block b1 = Parser.loadBlock(jsonInput1);
		Block b2 = Parser.loadBlock(jsonInput2);
		Block b3 = Parser.loadBlock(jsonInput3);
		
		assertNotEquals(b1,b2);
		assertTrue(b1.commandMatches(b2));
		assertNotEquals(b1,b3);
		assertFalse(b1.commandMatches(b3));
	}
	
	@Test
	public void testParsingDoIf() throws ParseException, ParsingException {
		String input = "[\"doIf\", [\"=\", [\"readVariable\", \"b\"], [\"readVariable\", \"a\"]], [[\"forward:\", 10]]]";
		JSONArray jsonInput= (JSONArray) jsonParser.parse(input);
		Block b = Parser.loadBlock(jsonInput);
	}
	
}
