package cs.vt.analysis.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.analysis.Analyzer;
import cs.vt.analysis.analyzer.analysis.Report;
import cs.vt.analysis.analyzer.analysis.VisitorBasedAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.AnalysisVisitor;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class AnalysisManager {
	JSONParser jsonParser;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	static Logger logger = Logger.getLogger(AnalysisManager.class);

	public AnalysisManager() {
		jsonParser = new JSONParser();
	}

	public JSONObject analyze(String src) throws ParsingException, AnalysisException {

		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		if (config == null) {
			config = getDefaultConfig();
		}
		ScratchProject project = null;
		try {
			project = ScratchProject.loadProject(src);
			projectID = project.getProjectID();

			for (Class k : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if (Arrays.asList(k.getInterfaces()).contains(AnalysisVisitor.class)) {
					AnalysisVisitor v = (AnalysisVisitor) k.newInstance();
					analyzer = new VisitorBasedAnalyzer();
					((VisitorBasedAnalyzer) analyzer).addAnalysisVisitor(v);
				} else {
					analyzer = (Analyzer) k.newInstance();
				}

				analyzer.setProject(project);

				try {
					analyzer.analyze();
				} catch (AnalysisException e) {
					logger.error(k + " fail to analyze project " + projectID);
					throw new AnalysisException("Analysis Error[" + analyzer.getClass() + "]:\n" + e.getMessage());
				}
				Report analysisReport = analyzer.getReport();
				switch (analysisReport.getReportType()) {
				case SMELL:
					smellReports.add(analysisReport.getJSONReport());
					break;
				case METRIC:
					metricReports.add(analysisReport.getJSONReport());
					break;
				default:
					smellReports.add(analysisReport.getJSONReport());
					break;
				}

			}
			JSONObject report = new JSONObject();
			report.put("_id", projectID);
			JSONObject smells = new JSONObject();


			for (JSONObject a : smellReports) {
				smells.put(a.get("name"), a.get("records"));
			}
			report.put("smells", smells);

			JSONObject metrics = new JSONObject();
			for (JSONObject m : metricReports) {
				metrics.put(m.get("name"), m.get("records"));
			}
			//additional
			metrics.put("scriptCount", project.getScriptCount());
			metrics.put("spriteCount", project.getSpriteCount());
			
			
			report.put("metrics", metrics);

			return report;

		} catch (ParseException e) {
			logger.error("Fail to read JSONObject of projectID: " + projectID);
			throw new ParsingException(e);
		} catch (ParsingException e) {
			logger.error("Fail to parse Scratch project: " + projectID);
			throw new ParsingException(e);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Fail to instantiate analyzer: " + e);
			throw new AnalysisException(e);
		} catch (Exception e) {
			logger.error("Fail: " + projectID);
			throw new AnalysisException(e);
		}
	}

	public int getProjectID() {
		return projectID;
	}

	private AnalysisConfigurator getDefaultConfig() {
		AnalysisConfigurator defaultConfig = new AnalysisConfigurator();
		try {
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.MasteryAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UnreachableAnalysisVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.TooLongScriptAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.BroadCastWorkAroundAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UncommunicativeNamingVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.BroadVarScopeAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.CloneAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UnnecessaryBroadcastAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UnusedVariableAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UnusedBlockAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.ScriptLengthMetricAnalyzer");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.DuplicateValueAnalyzer");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return defaultConfig;
	}

	private void setConfig(AnalysisConfigurator config) {
		this.config = config;

	}
	
	public static String analysisReportGenerator(int[] projectIDs) throws Exception{
		AnalysisManager blockAnalyzer = new AnalysisManager();
		StringBuilder sb = new StringBuilder();
		for(int id: projectIDs){
			String src = Util.retrieveProjectOnline(id); 
			JSONObject report = blockAnalyzer.analyze(src);
			String result = report.toJSONString();
			sb.append(id);
			sb.append("\t");
			sb.append(result);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	public static void main(String[] args) {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		String src = null;
		int projectID = 104240489;
		try {
			src = Util.retrieveProjectOnline(projectID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			JSONObject result = blockAnalyzer.analyze(src);
			System.out.println(result.toJSONString());
		} catch (AnalysisException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

}
