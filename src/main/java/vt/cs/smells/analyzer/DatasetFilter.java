package vt.cs.smells.analyzer;

import static java.lang.Math.toIntExact;

import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.crawler.AnalysisDBManager;
import vt.cs.smells.crawler.Crawler;
import vt.cs.smells.crawler.ProjectMetadata;

public class DatasetFilter {
	private static final int remixes = 10;
	JSONParser jsonParser = new JSONParser();
	HashMap<Long, JSONObject> datasetDict = new HashMap<>();
	private int scriptableThreshold;
	private double avgScriptPerSpriteThreshold;
	Crawler crawler = new Crawler();
	AnalysisDBManager dbManager = new AnalysisDBManager("localhost", "exploration");

	public void setDataSource(List<String> lines) {
		for (String line : lines) {
			JSONObject srcLine;
			try {
				srcLine = (JSONObject) jsonParser.parse(line.toString());
				Long id = (Long) srcLine.get("_id");
				datasetDict.put(id, srcLine);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
	}

	public void setScriptableThreshold(int i) {
		this.scriptableThreshold = i;
	}

	public void setAvgScriptPerSprite(double d) {
		this.avgScriptPerSpriteThreshold = d;

	}

	public HashMap<Integer, JSONObject> getFilteredProjects() {
		HashMap<Integer, JSONObject> results = processProjects(1);
		HashMap<Integer, JSONObject> filtered = filtering(results);
		return filtered;

	}

	private HashMap<Integer, JSONObject> filtering(HashMap<Integer, JSONObject> results) {
		HashMap<Integer, JSONObject> filteredResults = new HashMap<>();
		for (Integer projectID : results.keySet()) {
			Document smellReport = dbManager.findAnalysisReport(projectID);
			Document metricReport = dbManager.findMetricsReport(projectID);
			// double avgScript = (double)
			// metricReport.getInteger("scriptCount") /
			// metricReport.getInteger("spriteCount");
			// if (metricReport.getInteger("spriteCount") < scriptableThreshold
			// || avgScript < avgScriptPerSpriteThreshold) {
			// continue;
			// }
			Document metadata = dbManager.findMetadata(projectID);
			// if(metadata!=null){
			// if(metadata.getInteger("remixes")<10){
			// continue;
			// }
			// if(metadata.getInteger("views")<100){
			// continue;
			// };
			// }
			if (metadata != null) {
				filteredResults.put(projectID, results.get(projectID));
			}
		}
		return filteredResults;
	}

	private HashMap<Integer, JSONObject> processProjects(double d) {
		HashMap<Integer, JSONObject> result = new HashMap<>();
		AnalysisManager manager = new AnalysisManager();
		double sizeLimit = d * datasetDict.size();
		int count = 0;
		for (Long id : datasetDict.keySet()) {
			try {
				// JSONObject projectJson = datasetDict.get(id);
				int projectID = toIntExact(id);

				// if (dbManager.findAnalysisReport(projectID) == null) {
				// JSONObject reportJSON = manager.analyze((String)
				// projectJson.get("src"));
				// Document report = Document.parse(reportJSON.toJSONString());
				// Document smellReport = report.get("smells", Document.class);
				// Document metricReport = report.get("metrics",
				// Document.class);
				//
				// dbManager.putAnalysisReport(projectID, smellReport);
				// dbManager.putMetricsReport(projectID, metricReport);
				//
				// ProjectMetadata metadata = new ProjectMetadata(projectID);
				// metadata = crawler.retrieveProjectMetadata(metadata);
				// dbManager.putMetadata(metadata.toDocument());
				// }
				if (dbManager.findAnalysisReport(projectID) != null) {
					result.put(projectID, datasetDict.get(id));
					count++;
					System.out.println(count);
					if (count > sizeLimit) {
						break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public HashMap<Integer, JSONObject> getFilteredProjectsFrom(double d) {
		HashMap<Integer, JSONObject> results = processProjects(d);
		HashMap<Integer, JSONObject> filtered = filtering(results);
		return filtered;
	}
}