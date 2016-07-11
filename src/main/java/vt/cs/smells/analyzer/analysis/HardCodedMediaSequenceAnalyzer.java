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
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;

public class HardCodedMediaSequenceAnalyzer extends Analyzer {
	private static final String name = "HardCodedMediaSequence";
	private static final String abbr = "HCMS";
	DescriptiveStatistics sequenceSizeStats = new DescriptiveStatistics();

	private Report report;
	int count = 0;

	@Override
	public void analyze() throws AnalysisException {
		report = new ListAnalysisReport(name,abbr);

		for (Scriptable s : project.getAllScriptables().values()) {
			List<String> mediaSeq = s.getCostumes();
			List<List<String>> patterns = getMediaAccessSameControlStructure(s);
			JSONObject record = new JSONObject();
			for (List<String> accessSeq : patterns) {
				List<String> subseq = matchSubsequence(accessSeq, mediaSeq);
				if (subseq.size() > 2) {
					record.put("seq", subseq);
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
		conciseReport.put("count", count);
		if(count>0){
			conciseReport.put("groupSize", sequenceSizeStats.getMean());
		}else{
			conciseReport.put("groupSize", 0);
		}
	
		report.setConciseJSONReport(conciseReport);
		
		return report;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, AnalysisException, ParseException, ParsingException {
//		String csvResult = AnalysisManager.runAnalysis2(new HardCodedMediaSequenceAnalyzer(), 1);
//		FileUtils.writeStringToFile(new File(HardCodedMediaSequenceAnalyzer.class+".csv"), csvResult);
		Report result= AnalysisManager.runSingleAnalysis(17407891, new HardCodedMediaSequenceAnalyzer());
		System.out.println(result.getJSONReport());
	}
}
