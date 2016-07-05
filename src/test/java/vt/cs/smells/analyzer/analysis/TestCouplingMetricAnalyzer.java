package vt.cs.smells.analyzer.analysis;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;

public class TestCouplingMetricAnalyzer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(115367090);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetNumberOfUniqueOutGoingEdgeToOtherScriptable() throws AnalysisException {
		CouplingMetricAnalyzer analyzer = new CouplingMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		
		Set<CouplingMetricAnalyzer.Node> nodes = analyzer.getMessageReceiverNodes("Sprite1");
		
//		System.out.println(analyzer.getReport().getJSONReport());
		

	}

}
