package vt.cs.smells.analyzer.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.visual.PropertiesCollector;
import vt.cs.smells.visual.ScriptProperty;

public class TooFineGrainScriptAnalyzer extends Analyzer {
	private static final String name = "TooFineGrainScript";
	private static final String abbr = "TFGS";
	
	
	private ListAnalysisReport report;
	DescriptiveStatistics sizeStats = new DescriptiveStatistics();
	int count = 0;

	@Override
	public void analyze() throws AnalysisException {
		report = new ListAnalysisReport(name,abbr);
		
		for (Scriptable s : project.getAllScriptables().values()) {
			HashMap<ScriptProperty, List<Script>> similarScripts = new HashMap<>();
			for (Script sc : s.getScripts()) {
				Block hatBlock = sc.getBlocks().get(0);
				int scriptSize;
				try {
					scriptSize = new ProjectSizeMetricAnalyzer().measureLength(sc);
				} catch (VisitFailure e) {
					continue;
				}
				if (scriptSize > 5) {
					continue;
				}

				if (!hatBlock.getBlockType().getShape().equals("hat")) {
					continue;
				}

				//ignore if no forever and 
				if (sc.containsBlock("doForever").isEmpty()) {
					continue;
				}
				
				//if there's a forever, ignore if there is wait until
				if (!sc.containsBlock("doWaitUntil").isEmpty()){
					continue;
				}

				ScriptProperty sProp = new ScriptProperty(sc);
				if (!similarScripts.containsKey(sProp)) {
					similarScripts.put(sProp, new ArrayList<>());
				}
				similarScripts.get(sProp).add(sc);

			}
			JSONArray groupsJSON = new JSONArray();
			for (ScriptProperty propertyKey : similarScripts.keySet()) {
				List<Script> group = similarScripts.get(propertyKey);
				if (!(group.size() >= 2)) {
					continue;
				}
				JSONObject grpRecord = new JSONObject();
				JSONArray grpJSON = new JSONArray();
				for (Script script : group) {
					grpJSON.add(script.getPath());
				}
				grpRecord.put("group", grpJSON);
				grpRecord.put("shared_vars", propertyKey.getVariables().toString());
				groupsJSON.add(grpRecord);
			}
			if (groupsJSON.isEmpty()) {
				continue;
			}
			JSONObject record = new JSONObject();
			record.put("scriptable", s.getName());
			record.put("groups", groupsJSON);
			
			sizeStats.addValue(groupsJSON.size());
			report.addRecord(record);
			count++;
		}

	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("count", count);
		if(count>0){
			conciseReport.put("size", sizeStats.getMean());
		}else{
			conciseReport.put("size", 0);
		}
		
		report.setConciseJSONReport(conciseReport);
		
		return report;
	}
	
	public static void main(String[] args)
			throws FileNotFoundException, IOException, AnalysisException, ParseException, ParsingException {
//		 String csvResult = AnalysisManager.runAnalysis2(new
//		 TooFineGrainScriptAnalyzer(), 1);
//		 FileUtils.writeStringToFile(new
//		 File(TooFineGrainScriptAnalyzer.class+".csv"), csvResult);
//		Report result = AnalysisManager.runSingleAnalysis(18801413, new ExtremeEventBasedScriptAnalyzer());
		// System.out.println(result.getJSONReport().toJSONString());
		// Document doc = Document.parse(result.getJSONReport().toJSONString());
		// System.out.println(result.getJSONReport());
		
		AnalysisManager.runAnalysis(TooFineGrainScriptAnalyzer.class.getName(), AnalysisManager.smallTestInput);
	}

}
