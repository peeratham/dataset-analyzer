package vt.cs.smells.analyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.Main;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestMetadataVisitor {
	
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
