package vt.cs.smells.analyzer.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class HardCodedMediaSequenceAnalyzer extends Analyzer {
	private static final String name = "HardCodedMediaSequence";
	private static final String abbr = "HCMS";
	DescriptiveStatistics sequenceSizeStats = new DescriptiveStatistics();

	private Report report;
	int count = 0;
	boolean multipleCostume = false;
	List<Block> costumeRelatedBlocks = new ArrayList<>();

	@Override
	public void analyze() throws AnalysisException {
		report = new ListAnalysisReport(name,abbr);
		//check if there are more than one costume
		for (Scriptable s : project.getAllScriptables().values()) {
			List<String> mediaSeq = s.getCostumes();
			multipleCostume = multipleCostume||mediaSeq.size()>1;
		}
		
		//check if there are usage of costume related commands at all
		String[] costumeRelatedCommands = new String[]{"lookLike:", "nextCostume","startScene"};
		
		for(String command: costumeRelatedCommands){
			costumeRelatedBlocks.addAll(Collector.collect(new Evaluator.BlockCommand(command), project));
		}
		
		
		for (Scriptable s : project.getAllScriptables().values()) {
			List<String> mediaSeq = s.getCostumes();
			List<List<String>> patterns = getMediaAccessSameControlStructure(s);
			
			for (List<String> accessSeq : patterns) {
				List<String> subseq = matchSubsequence(accessSeq, mediaSeq);
				if (subseq.size() > 2) {
					JSONObject record = new JSONObject();
					record.put("seq", accessSeq);
					sequenceSizeStats.addValue(subseq.size());
					record.put("scriptable", s.getName());
					report.addRecord(record);
					count++;
				}
			}
		}
	}

	public List<String> matchSubsequence(List<String> seq1, List<String> seq2) {
		return ListUtils.longestCommonSubsequence(seq1, seq2);
	}

	public List<List<String>> getMediaAccessSameControlStructure(Scriptable sprite1) {
		List<List<String>> mediaAccessOrder = new ArrayList<>();
		for (Script sc : sprite1.getScripts()) {
			List<String> pattern = new ArrayList<String>();
			for (Block b : sc.getBlocks()) {

				if (b.hasCommand("lookLike:") || b.hasCommand("startScene")) {
					pattern.add(b.getArgs(0).toString());
				} else if (b.hasNestedBlocks()) {
					mediaAccessOrder.addAll(extractAccessPatterns(b));
				}
			}
			mediaAccessOrder.add(pattern);
		}
		return mediaAccessOrder;
	}

	private List<List<String>> extractAccessPatterns(Block blockWithNested) {
		{
			List<List<String>> mediaAccessOrder = new ArrayList<>();
			if (blockWithNested.hasNestedBlocks()) {
				for (List<Block> nested : blockWithNested.getNestedGroup()) {
					List<String> pattern = new ArrayList<String>();
					for (Block b : nested) {
						if (b.hasCommand("lookLike:") || b.hasCommand("startScene")) {
							pattern.add(b.getArgs(0).toString());
						}
						if (b.hasNestedBlocks()) {
							mediaAccessOrder.addAll(extractAccessPatterns(b));
						}
					}
					mediaAccessOrder.add(pattern);
				}
			}
			return mediaAccessOrder;
		}
	}


	@Override
	public Report getReport() {
		
		JSONObject conciseReport = new JSONObject();
		
		if(count>0){
			conciseReport.put("count", count);
			conciseReport.put("groupSize", sequenceSizeStats.getMean());
		}else if(count ==0 && multipleCostume==true && !costumeRelatedBlocks.isEmpty()){
			conciseReport.put("count", count);
			conciseReport.put("groupSize", 0);
		}
	
		report.setConciseJSONReport(conciseReport);
		
		return report;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, AnalysisException, ParseException, ParsingException {
//		String csvResult = AnalysisManager.runAnalysis2(new HardCodedMediaSequenceAnalyzer(), 1);
//		FileUtils.writeStringToFile(new File(HardCodedMediaSequenceAnalyzer.class+".csv"), csvResult);
		String projectSrc = Util.retrieveProjectOnline(17407891);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		Analyzer analyzer = new HardCodedMediaSequenceAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getConciseJSONReport());
//		System.out.println(analyzer.getReport().getJSONReport());
	}
}
