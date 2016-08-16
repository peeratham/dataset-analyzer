package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestUncommunicativeNaming {

	@Test
	public void detectDefaultSpriteDefaultNaming() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(97396677);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		
		UncommunicativeNamingAnalyzer analyzer = new UncommunicativeNamingAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		assertEquals(4, analyzer.count);
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}
}
