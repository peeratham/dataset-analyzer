package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class TestCustomBlockParsing {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, ParseException, ParsingException {
		String stringInput = Util.retrieveProjectOnline(102125336);
		ScratchProject project = ScratchProject.loadProject(stringInput);
	}

}
