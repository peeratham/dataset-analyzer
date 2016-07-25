package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.bson.Document;
import org.json.simple.parser.ParseException;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.visual.ScriptOrganizationAnalyzer;

public class TestScriptOrganizationAnalyzer {
	@Ignore
	@Test
	public void test() throws AnalysisException, IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(94833586);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		ScriptOrganizationAnalyzer analyzer = new ScriptOrganizationAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
		assertEquals(0.8, analyzer.purityStats.getMean(),0.01);
//		analyzer.showVisualization();
	}
	@Ignore
	@Test
	public void testMinusOneForProjectWithOneScriptInASprite() throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(116825938);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		ScriptOrganizationAnalyzer analyzer = new ScriptOrganizationAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
		assertEquals(-1, analyzer.averagePurity, 0.01);
	}

}
