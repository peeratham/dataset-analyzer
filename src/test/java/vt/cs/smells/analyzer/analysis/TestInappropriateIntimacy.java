package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;

public class TestInappropriateIntimacy {

	@Test
	public void test() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(116505707);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		InappropriateIntimacy analyzer = new InappropriateIntimacy();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getConciseJSONReport());
		System.out.println(analyzer.getReport().getJSONReport());
		assertEquals(4, analyzer.totalIntimacy);
	}
}
