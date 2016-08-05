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

public class TestDuplicateCodeAnalyzer {

	@Test
	public void test() throws AnalysisException, IOException, ParseException, ParsingException {
		String projectSrc = Util.retrieveProjectOnline(101357446);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		DuplicateCodeAnalyzer analyzer = new DuplicateCodeAnalyzer();
		
		analyzer.setProject(project);
		analyzer.analyze();
		assertEquals(5, analyzer.cloneGroupCount);
//		assertEquals(9, analyzer.cloneGroupSizeStats.getSum(), 0.01);
//		assertEquals(17, analyzer.cloneInstanceSizeStats.getSum(), 0.01);
		assertEquals(4, analyzer.sameSpriteCount);
		assertEquals(1, analyzer.interSpriteCount);
		System.out.println(analyzer.getReport().getConciseJSONReport());
		System.out.println(analyzer.getReport().getJSONReport());
	}
}
