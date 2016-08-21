package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.Report.ReportType;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.visual.Node;
import vt.cs.smells.visual.NodeClass;

public class ScriptClusterImpurity extends Analyzer {
	private static final String name = "ScriptClusterImpurity";
	private static final String abbr = "SCI";
	public double averageImpurity = -1;

	HashMap<String, List<Node>> nodesForEachScriptable = new HashMap<>();
	private static final int max_xi_id = 1000000;
	private ListAnalysisReport report = null;
	public DescriptiveStatistics impurityStats = new DescriptiveStatistics();
	private HashMap<String, Double> impurityMap = new HashMap<>();


	@Override
	public void analyze() throws AnalysisException {
		report = new ListAnalysisReport(name, abbr);
		for (String scriptableName : project.getAllScriptables().keySet()) {
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (Script s : project.getAllScriptables().get(scriptableName)
					.getScripts()) {
				Block firstBlock = s.getBlocks().get(0);
				if (firstBlock.getBlockType().getShape().equals("hat")) {
					Node node = new Node(s);
					nodes.add(node);
				}
			}
			if (nodes.isEmpty()) {
				continue;
			}
			
			// clustering analysis
			ScriptClusterer<Node> clusterer = new ScriptClusterer<Node>();
			List<List<Node>> clusters = clusterer.performClustering(nodes,
					(double) 1 / max_xi_id);

			nodesForEachScriptable.put(scriptableName, nodes);

			// if every nodes are the same no purity evaluation is useless
			HashSet<NodeClass> nodeClassSet = new HashSet<NodeClass>();
			for (List<Node> cluster : clusters) {
				for (Node n : cluster) {
					nodeClassSet.add(n.getNodeClass());
				}
			}
			if (!(nodeClassSet.size() > 1)) {
				continue;
			}
			// purity evaluation
			DescriptiveStatistics impurityPerSprite = new DescriptiveStatistics();
			for (List<Node> cluster : clusters) {
				HashMap<NodeClass, Integer> clusterCount = new HashMap<>();
				for (Node n : cluster) {
					if (clusterCount.get(n.getNodeClass()) == null) {
						clusterCount.put(n.getNodeClass(), 1);
					} else {
						int increment = clusterCount.get(n.getNodeClass()) + 1;
						clusterCount.put(n.getNodeClass(), increment);
					}
				}
				int majorityCount = -1;
				int totalNodes = cluster.size();

				//no grouping can be done, every script is different
				if(totalNodes == clusterCount.keySet().size()){
					continue;
				}
				
				for (NodeClass nc : clusterCount.keySet()) {
					if (clusterCount.get(nc) > majorityCount) {
						majorityCount = clusterCount.get(nc);
					}
				}

				double impurityVal =1- (double) majorityCount / totalNodes;
				impurityPerSprite.addValue(impurityVal);
			}
			if(impurityPerSprite.getN()>0){
				impurityMap.put(scriptableName, impurityPerSprite.getMean());
				impurityStats.addValue(impurityPerSprite.getMean());
				JSONObject record = new JSONObject();
				record.put(scriptableName, impurityPerSprite.getMean());
				report.addRecord(record);
			}

		}
		if(impurityMap.isEmpty()){
			averageImpurity = -1;
		}else{
			averageImpurity = impurityStats.getMean();
		}
		
	}

	public HashMap<String, List<Node>> getAllScriptNodes() {
		return nodesForEachScriptable;
	}
	
	@Override
	public Report getReport() {
		report.setName(name);
		report.setAbbr(abbr);
		report.setReportType(ReportType.METRIC);
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("impurity_vals", impurityMap.values());
		
		report.setConciseJSONReport(conciseReport);
		return report;
	}

	public static void main(String[] args) throws IOException, ParseException,
			ParsingException, AnalysisException {
		
		String projectSrc = Util.retrieveProjectOnline(10135365);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		ScriptClusterImpurity analyzer = new ScriptClusterImpurity();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}
}
