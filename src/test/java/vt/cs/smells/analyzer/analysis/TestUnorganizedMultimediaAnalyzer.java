package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Util;

public class TestUnorganizedMultimediaAnalyzer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(109963080);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testObtainingMultimediaOrder() {
		Scriptable s = project.getAllScriptables().get("Sprite1");
		assertTrue(s.getCostumes().size() > 1);
	}

	@Test
	public void testObtainingMultimediaAccessPattern() {
		Scriptable sprite1 = project.getAllScriptables().get("Sprite1");
		Analyzer analyzer = new HardCodedMediaSequenceAnalyzer();
		List<List<String>> patterns1 = ((HardCodedMediaSequenceAnalyzer) analyzer).getMediaAccessPatternFlat(sprite1);
		assertTrue(!patterns1.isEmpty());

		Scriptable stage = project.getAllScriptables().get("Stage");
		List<List<String>> patterns2 = ((HardCodedMediaSequenceAnalyzer) analyzer).getMediaAccessPatternFlat(stage);
		assertTrue(!patterns2.isEmpty());
	}

	@Test
	public void testExactMatchSequence(){
		List<String> seq1 = new ArrayList<>();
		seq1.add("a");
		seq1.add("2");
		seq1.add("3");
		
		List<String> seq2 = new ArrayList<>();
		seq2.add("a");
		seq2.add("2");
		seq2.add("3");
		
		assertTrue(seq1.equals(seq2));
	}
	
	@Test
	public void testSubSequenceMatch(){
		List<String> seq1 = new ArrayList<>();
		seq1.add("a");
		seq1.add("2");
		seq1.add("3");
		
		List<String> seq2 = new ArrayList<>();
		seq2.add("a");
		seq2.add("2");
		seq2.add("3");
		Analyzer analyzer = new HardCodedMediaSequenceAnalyzer();
		List<String> matched = ((HardCodedMediaSequenceAnalyzer) analyzer).matchSubsequence(seq1,seq2);
		System.out.println(matched);
		
	}
	

	

	@Test
	public void testMediaOrderingNotMatchedFrequentMediaReference() throws AnalysisException {
		Analyzer analyzer = new HardCodedMediaSequenceAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
//		System.out.println(analyzer);
		System.out.println(analyzer.getReport().getJSONReport());
	}
	
	@Test
	public void testObtainMediaAccessPatternWithinSameControlStructure(){
		Scriptable sprite1 = project.getAllScriptables().get("Sprite2");
		Analyzer analyzer = new HardCodedMediaSequenceAnalyzer();
		List<List<String>> patterns = ((HardCodedMediaSequenceAnalyzer) analyzer).getMediaAccessPatternFlat(sprite1);
		String[] expected1 = new String[]{"costume2", "costume3"};
		String[] expected2 = new String[]{"costume1","costume2", "costume3"};
		String[] expected3 = new String[]{"costume1"};
		System.out.println(patterns);
		assertTrue(patterns.contains(Arrays.asList(expected1)));
		assertTrue(patterns.contains(Arrays.asList(expected2)));
		assertTrue(patterns.contains(Arrays.asList(expected3)));
	}
	


}
