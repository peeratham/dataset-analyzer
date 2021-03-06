package vt.cs.smells.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.analysis.BroadCastWorkAroundAnalyzer;
import vt.cs.smells.analyzer.analysis.BroadVarScopeAnalyzer;
import vt.cs.smells.analyzer.analysis.DuplicateCodeAnalyzer;
import vt.cs.smells.analyzer.analysis.DuplicateValueAnalyzer;
import vt.cs.smells.analyzer.analysis.HardCodedMediaSequenceAnalyzer;
import vt.cs.smells.analyzer.analysis.InappropriateIntimacy;
import vt.cs.smells.analyzer.analysis.MasteryAnalyzer;
import vt.cs.smells.analyzer.analysis.ProgrammingElementMetricAnalyzer;
import vt.cs.smells.analyzer.analysis.ProjectSizeMetricAnalyzer;
import vt.cs.smells.analyzer.analysis.ScriptClusterImpurity;
import vt.cs.smells.analyzer.analysis.ScriptOrganizationAnalyzer;
import vt.cs.smells.analyzer.analysis.TooFineGrainScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.TooLongScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UncommunicativeNamingAnalyzer;
import vt.cs.smells.analyzer.analysis.UnnecessaryBroadcastAnalyzer;
import vt.cs.smells.analyzer.analysis.UnorganizedScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UnreachableScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UnusedCustomBlockAnalyzer;
import vt.cs.smells.analyzer.analysis.UnusedVariableAnalyzer;
import vt.cs.smells.analyzer.analysis.VisitorBasedAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.UndefinedBlockException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.AnalysisVisitor;
import vt.cs.smells.analyzer.visitor.Visitor;
import vt.cs.smells.crawler.AnalysisDBManager;

public class AnalysisManager {
	JSONParser jsonParser = new JSONParser();;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	static Logger logger = Logger.getLogger(AnalysisManager.class);
	public static final String smallTestInput = "/home/peeratham/tpeera4/smell-analysis/hundred.json";
	public static final String largeTestInput = "/home/peeratham/tpeera4/smell-analysis/sources-0.json";
	private ArrayList<Report> reports;
	private String currentAnalyzerClass = "";

	public ArrayList<Report> analyze(ScratchProject project)
			throws AnalysisException {
		reports = new ArrayList<>();
		if (config == null) {
			config = getDefaultConfig();
		}
		try {
			projectID = project.getProjectID();

			for (Class klass : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if (Arrays.asList(klass.getInterfaces()).contains(
						AnalysisVisitor.class)) {
					AnalysisVisitor v = (AnalysisVisitor) klass.newInstance();
					analyzer = new VisitorBasedAnalyzer();
					((VisitorBasedAnalyzer) analyzer).addAnalysisVisitor(v);
				} else {
					analyzer = (Analyzer) klass.newInstance();
				}

				currentAnalyzerClass = klass.getName();
				analyzer.setProject(project);
				analyzer.analyze();
				reports.add(analyzer.getReport());
			}
		} catch (AnalysisException e) {
			// logger.error(klass + " fail to analyze project " + projectID);
			throw new AnalysisException("Analysis Error:"
					+ currentAnalyzerClass + "--" + e.getMessage());
			// + analyzer.getClass() + "]:\n" + e.getMessage());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return reports;
	}

	public ArrayList<Report> analyze(String src) throws ParsingException,
			AnalysisException {
		reports = new ArrayList<>();
		if (config == null) {
			config = getDefaultConfig();
		}
		ScratchProject project = null;
		try {
			project = ScratchProject.loadProject(src);
			projectID = project.getProjectID();

			for (Class klass : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if (Arrays.asList(klass.getInterfaces()).contains(
						AnalysisVisitor.class)) {
					AnalysisVisitor v = (AnalysisVisitor) klass.newInstance();
					analyzer = new VisitorBasedAnalyzer();
					((VisitorBasedAnalyzer) analyzer).addAnalysisVisitor(v);
				} else {
					analyzer = (Analyzer) klass.newInstance();
				}

				analyzer.setProject(project);

				try {
					analyzer.analyze();
				} catch (AnalysisException e) {
					logger.error(klass + " fail to analyze project "
							+ projectID);
					throw new AnalysisException("Analysis Error["
							+ analyzer.getClass() + "]:\n" + e.getMessage());
				}
				reports.add(analyzer.getReport());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reports;
	}

	public JSONObject getConciseJSONReports() {
		boolean concise = true;
		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		for (Report analysisReport : reports) {
			switch (analysisReport.getReportType()) {
			case SMELL:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			case METRIC:
				metricReports.add(analysisReport.getJSONReport(concise));
				break;
			default:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			}
		}

		JSONObject report = new JSONObject();
		report.put("_id", projectID);
		JSONObject smells = new JSONObject();

		for (JSONObject smellReport : smellReports) {
			smells.put(smellReport.get("name"), smellReport.get("records"));
		}
		report.put("smells", smells);

		JSONObject metrics = new JSONObject();
		for (JSONObject metricReport : metricReports) {
			metrics.put(metricReport.get("name"), metricReport.get("records"));
		}
		report.put("metrics", metrics);

		return report;
	}

	public JSONObject getFullJSONReport() {
		boolean concise = false;
		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		for (Report analysisReport : reports) {
			switch (analysisReport.getReportType()) {
			case SMELL:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			case METRIC:
				metricReports.add(analysisReport.getJSONReport(concise));
				break;
			default:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			}
		}

		JSONObject report = new JSONObject();
		report.put("_id", projectID);
		JSONObject smells = new JSONObject();

		for (JSONObject smellReport : smellReports) {
			smells.put(smellReport.get("name"), smellReport.get("records"));
		}
		report.put("smells", smells);

		JSONObject metrics = new JSONObject();
		for (JSONObject metricReport : metricReports) {
			metrics.put(metricReport.get("name"), metricReport.get("records"));
		}
		report.put("metrics", metrics);

		return report;
	}

	public int getProjectID() {
		return projectID;
	}

	private AnalysisConfigurator getDefaultConfig() {
		AnalysisConfigurator defaultConfig = new AnalysisConfigurator();
		try {
			defaultConfig.addAnalysis(MasteryAnalyzer.class.getName());
			defaultConfig
					.addAnalysis(UnreachableScriptAnalyzer.class.getName());
			defaultConfig.addAnalysis(TooLongScriptAnalyzer.class.getName());
			defaultConfig.addAnalysis(BroadCastWorkAroundAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(UncommunicativeNamingAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(BroadVarScopeAnalyzer.class.getName());
			defaultConfig.addAnalysis(DuplicateCodeAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnnecessaryBroadcastAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(UnusedVariableAnalyzer.class.getName());
			defaultConfig
					.addAnalysis(UnusedCustomBlockAnalyzer.class.getName());
			defaultConfig
					.addAnalysis(ProjectSizeMetricAnalyzer.class.getName());
			defaultConfig.addAnalysis(ProgrammingElementMetricAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(DuplicateValueAnalyzer.class.getName());
			defaultConfig.addAnalysis(TooFineGrainScriptAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(HardCodedMediaSequenceAnalyzer.class
					.getName());
			// defaultConfig.addAnalysis(InappropriateIntimacy.class.getName());
			// defaultConfig.addAnalysis(ScriptOrganizationAnalyzer.class.getName());
			defaultConfig.addAnalysis(ScriptClusterImpurity.class.getName());
			defaultConfig
					.addAnalysis(UnorganizedScriptAnalyzer.class.getName());

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

	public static String analysisReportGenerator(int[] projectIDs)
			throws Exception {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		StringBuilder sb = new StringBuilder();
		for (int id : projectIDs) {
			String src = Util.retrieveProjectOnline(id);
			blockAnalyzer.analyze(src);
			JSONObject report = blockAnalyzer.getFullJSONReport();
			String result = report.toJSONString();
			sb.append(id);
			sb.append("\t");
			sb.append(result);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	public static void runSingleAnalysis(String analysisClassURL,
			String inputFile) throws FileNotFoundException, IOException {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		JSONParser jsonParser = new JSONParser();
		AnalysisConfigurator mainConfig = new AnalysisConfigurator();

		try {

			mainConfig.addAnalysis(analysisClassURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		blockAnalyzer.setConfig(mainConfig);

		FileInputStream is = new FileInputStream(inputFile);
		String[] lines = IOUtils.toString(is).split("\n");
		DescriptiveStatistics stats = new DescriptiveStatistics();
		int found = 0;
		for (String line : lines) {
			Long id = null;
			try {
				JSONObject srcLine = (JSONObject) jsonParser.parse(line
						.toString());
				id = (Long) srcLine.get("_id");
				String src = srcLine.get("src").toString();
				blockAnalyzer.analyze(src);
				JSONObject report = blockAnalyzer.getFullJSONReport();
				JSONObject smells = (JSONObject) report.get("smells");
				String smellName = (String) smells.keySet().iterator().next();
				JSONObject record = (JSONObject) smells.get(smellName);
				Integer count = (Integer) record.get("count");
				stats.addValue(count);

				if (count > 0) {
					found += 1;
					logger.info(id);
					// System.out.println(report);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DecimalFormat df2 = new DecimalFormat(".##");
		System.out.println("Found:" + found + " out of " + stats.getN()
				+ " or " + df2.format((double) found / stats.getN() * 100)
				+ "%");
		System.out.println("Average:" + df2.format(stats.getMean())
				+ " per project");
	}

	public static void runAnalysis(String analysisClassName,
			String pathToJSONdata) throws FileNotFoundException, IOException {
		runSingleAnalysis(analysisClassName, pathToJSONdata);
	}

	public static String runAnalysis2(Analyzer analyzer, double percent)
			throws IOException {
		DatasetFilter filter = new DatasetFilter();
		FileInputStream is = new FileInputStream(AnalysisManager.smallTestInput);
		AnalysisDBManager dbManager = new AnalysisDBManager("localhost",
				"analysis");

		String[] lines = IOUtils.toString(is).split("\n");
		List<String> data = new ArrayList<>(Arrays.asList(lines));
		int endIndex = (int) Math.round(percent * lines.length);
		List<String> partition = data.subList(0, endIndex);
		filter.setDataSource(partition);
		filter.setScriptableThreshold(5);
		filter.setAvgScriptPerSprite(3.0);

		HashMap<Integer, JSONObject> datasetDict = filter
				.getFilteredProjectsFrom(1);
		System.out.println("Original Size: " + lines.length);
		System.out.println("Filtered Size: " + datasetDict.size());

		CSVGenerator gen = new CSVGenerator();
		gen.setColumn(new String[] { "ID", "count" });

		for (Integer id : datasetDict.keySet()) {
			JSONObject projectJson = datasetDict.get(id);
			ScratchProject project = null;
			try {
				project = ScratchProject.loadProject((String) projectJson
						.get("src"));
				analyzer.setProject(project);
				analyzer.analyze();
				Document report = Document.parse(analyzer.getReport()
						.getJSONReport().toJSONString());
				if (((Document) report.get("records")).getInteger("count") > 0) {
					System.out.println(id);
					System.out.println(analyzer.getReport().getJSONReport());
				}
				gen.addLine(new Object[] { id,
						((Document) report.get("records")).getInteger("count") });
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (AnalysisException e) {
				e.printStackTrace();
			}
		}
		return gen.generateCSV();
	}

	public static Report runSingleAnalysis(int i, Analyzer analyzer)
			throws AnalysisException, IOException, ParseException,
			ParsingException {
		String projectSrc = Util.retrieveProjectOnline(i);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		analyzer.setProject(project);
		analyzer.analyze();
		return analyzer.getReport();
	}

	public static void main(String[] args) throws IOException {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		int projectID = 117800592;
		ScratchProject project = null;
		try {
			// String src = Util.retrieveProjectOnline(projectID);
			// project = ScratchProject.loadProject(src);
			// blockAnalyzer.analyze(project);
			// JSONObject result = blockAnalyzer.getConciseJSONReports();
			// JSONObject fullResult = blockAnalyzer.getFullJSONReport();
			// System.out.println(fullResult .toJSONString());
			// System.out.println(result.toJSONString());

			int[] projectIDs = { 118908867, 118908833, 118908806, 118908780,
					118908558, 118907767, 118905612, 118905268, 118903025,
					118902731 };
			Map<Integer, String> idToTitle = new HashMap<>();
			idToTitle.put(118908867, "Directional");
			idToTitle.put(118908833, "Brownie Kappa");
			idToTitle.put(118908806, "Photosynthesis");
			idToTitle.put(118908780, "In Flight");
			idToTitle.put(118908558, "Heads or Tails?");
			idToTitle.put(118907767, "pinball     ");
			idToTitle.put(118905612, "Trailed Bounce");
			idToTitle.put(118905268, "Boat Race!");
			idToTitle.put(118903025, "the fite");
			idToTitle.put(118902731, "Angry Shoppers");

			analyzeMultipleProjects(projectIDs, idToTitle);

		} catch (ParseException e) {
			logger.error("Fail to parse JSON for projectID:" + projectID
					+ "..." + e.getMessage());
		} catch (ParsingException e) {
			logger.error("Fail to parse projectID:" + projectID + "...");
			e.printStackTrace();
		} catch (AnalysisException e) {
			logger.error("Fail to analyze projectID:" + projectID + "..."
					+ e.getMessage() + e.getCause());
		}
	}

	private static void analyzeMultipleProjects(int[] projectIDs,
			Map<Integer, String> idToTitle) throws IOException, ParseException,
			ParsingException, AnalysisException {
		String[] order = { "UC", "UV", "UCB", "DC", "DS", "UN", "LS", "EFGS",
				"US", "BVS", "UW", "HCMS" };

		for (int projectID : projectIDs){
			AnalysisManager blockAnalyzer = new AnalysisManager();
			System.out.print(projectID + "\t");
			System.out.println(idToTitle.get(projectID));
			String src = Util.retrieveProjectOnline(projectID);
			ScratchProject project = ScratchProject.loadProject(src);
			blockAnalyzer.analyze(project);
			JSONObject fullResult = blockAnalyzer.getFullJSONReport();
			System.out.println(fullResult.toJSONString());
		}
		
		formatHeaderInOrder(order);

		for (int projectID : projectIDs) {
			AnalysisManager blockAnalyzer = new AnalysisManager();
			ScratchProject project = null;

			String src = Util.retrieveProjectOnline(projectID);
			project = ScratchProject.loadProject(src);
			blockAnalyzer.analyze(project);
			JSONObject conciseResult = blockAnalyzer.getConciseJSONReports();
			JSONObject fullResult = blockAnalyzer.getFullJSONReport();
//			System.out.println(fullResult.toJSONString());
//			 System.out.println(conciseResult.toJSONString());
			System.out.print(idToTitle.get(projectID));
			System.out.print("\t");
			formatInOrder(conciseResult.toJSONString(), order);

		}

	}

	public static void formatHeaderInOrder(String[] order) {
		System.out.print("Project \t");
		System.out.print("&");
		for (String smellKey : order) {
			System.out.print("\t");
			System.out.print(smellKey);
			System.out.print("\t");
			System.out.print("&");
		}
		System.out.println("\t total  \\\\");
	}

	private static void formatInOrder(String jsonString, String[] order) {

		Document d = Document.parse(jsonString);
		Document smells = (Document) d.get("smells");

		System.out.print("&");
		int total = 0;
		for (String smellKey : order) {
			Document smellRecord = (Document) smells.get(smellKey);
			System.out.print("\t");
			if (smellRecord.isEmpty()) {
				System.out.print("--");
			} else {
				if(smellKey.equals("DC")){
					total+=smellRecord.getInteger("sameSpriteClone");
					System.out.print(smellRecord.getInteger("sameSpriteClone"));
				}else if(smellKey.equals("DS")){
					total+=smellRecord.getInteger("stringCount");
					System.out.print(smellRecord.getInteger("stringCount"));
				}
				else{
					total+=smellRecord.getInteger("count");
					System.out.print(smellRecord.getInteger("count"));
				}
				
			}
			System.out.print("\t");
			System.out.print("&");

		}
		System.out.println("\t "+total+"  \\\\");

	}

}
