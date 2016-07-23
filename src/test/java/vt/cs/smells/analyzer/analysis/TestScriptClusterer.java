package vt.cs.smells.analyzer.analysis;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.visual.Node;
import vt.cs.smells.visual.ScriptOrganizationAnalyzer;

public class TestScriptClusterer {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(97125575);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws AnalysisException {
		ScriptClusterer clusterer = new ScriptClusterer();
		ScriptOrganizationAnalyzer analyzer = new ScriptOrganizationAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		HashMap<String, List<Node>> scriptNodes = analyzer.getAllScriptNodes();
		for(String scriptable:scriptNodes.keySet()){
			List nodes = scriptNodes.get(scriptable);
			
			List<Cluster> clusters = clusterer.cluster(nodes,0.001);
			if(clusters.size()<2){
				continue;
			}
			for(Cluster c : clusters){
				System.out.println(scriptable);
				System.out.println(c.getPoints());
			}
		}
	
		
	}

}
