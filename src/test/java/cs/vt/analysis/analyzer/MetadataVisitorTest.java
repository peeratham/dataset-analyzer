package cs.vt.analysis.analyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;

public class MetadataVisitorTest {
	
	ScratchProject project;
	File[] dataset;
	String inputString;
	
	
	@Before
	public void setUp() throws Exception {
		
		InputStream in = Main.class.getClassLoader()
				.getResource("93160218.json").openStream();
		inputString = IOUtils.toString(in);
		in.close();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws ParseException, ParsingException {
		project = ScratchProject.loadProject(inputString);
		assertEquals(new Long(3),project.getScriptCount());
		assertEquals(new Long(2),project.getSpriteCount());
	}

}
