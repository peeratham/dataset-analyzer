package vt.cs.smells.visual;

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
import vt.cs.smells.analyzer.analysis.ScriptClusterer;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.parser.ParsingException;

public class ScriptOrganizationAnalyzer extends Analyzer {
	private static final String name = "ScriptOrganization";
	private static final String abbr = "SO";
	public double averagePurity = -1;

	HashMap<String, List<Node>> nodesForEachScriptable = new HashMap<>();
	private static final int max_xi_id = 1000000;
	private ListAnalysisReport report = null;
	public DescriptiveStatistics purityStats = new DescriptiveStatistics();
	private HashMap<String, Double> purityMap = new HashMap<>();


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
			DescriptiveStatistics purityPerSprite = new DescriptiveStatistics();
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
				for (NodeClass nc : clusterCount.keySet()) {
					if (clusterCount.get(nc) > majorityCount) {
						majorityCount = clusterCount.get(nc);
					}
				}

				double purityVal = (double) majorityCount / totalNodes;
				purityPerSprite.addValue(purityVal);
			}
			purityMap.put(scriptableName, purityPerSprite.getMean());
			purityStats.addValue(purityPerSprite.getMean());
			JSONObject record = new JSONObject();
			record.put(scriptableName, purityPerSprite.getMean());
			report.addRecord(record);

		}
		if(purityMap.isEmpty()){
			averagePurity = -1;
		}else{
			averagePurity = purityStats.getMean();
		}
		
	}

	public HashMap<String, List<Node>> getAllScriptNodes() {
		return nodesForEachScriptable;
	}
	
	@Override
	public Report getReport() {
		report.setName("ScriptOrganization");
		report.setAbbr("SO");
		report.setReportType(ReportType.METRIC);
		JSONObject conciseReport = new JSONObject();

		conciseReport.put("avg", averagePurity);
		conciseReport.put("purity_vals", purityMap.values());
		report.setConciseJSONReport(conciseReport);
		return report;
	}

	public static void main(String[] args) throws IOException, ParseException,
			ParsingException, AnalysisException {

	}

}
