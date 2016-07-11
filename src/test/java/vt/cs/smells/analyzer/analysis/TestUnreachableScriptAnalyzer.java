package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestUnreachableScriptAnalyzer {

	@Test
	public void testAnalysisResult() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(93160218);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		UnreachableScriptAnalyzer analyzer = new UnreachableScriptAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		
		assertEquals(1, analyzer.count);
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
