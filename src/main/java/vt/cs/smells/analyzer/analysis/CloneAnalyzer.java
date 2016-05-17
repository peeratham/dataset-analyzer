package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.CloneUtil;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDownSubTreeCollector;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class CloneAnalyzer extends Analyzer {
	private ListAnalysisReport report = new ListAnalysisReport();
	private ArrayList<Block> subtreeList;
	private HashMap<Integer, ArrayList<Block>> cloneDictionary = new HashMap<Integer, ArrayList<Block>>();
	private HashMap<Integer, ArrayList<ArrayList<Block>>> cloneSequenceDictionary = new HashMap<Integer, ArrayList<ArrayList<Block>>>();
	private ArrayList<ArrayList<Block>> cloneSequenceList;

	@SuppressWarnings("unchecked")
	@Override
	public void analyze() throws AnalysisException {
		Visitor collector = new TopDownSubTreeCollector(new Identity());
		try {
			project.accept(collector);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
		subtreeList = ((TopDownSubTreeCollector) collector).getSubTreeList();

		for (Block subtree : subtreeList) {
			int hashVal = CloneUtil.hashSubTree(subtree);
			if (!cloneDictionary.containsKey(hashVal)) {
				cloneDictionary.put(hashVal, new ArrayList<Block>());
			}
			cloneDictionary.get(hashVal).add(subtree);
		}

		for (int key : cloneDictionary.keySet()) {
			if (cloneDictionary.get(key).size() > 1) {
				ArrayList<Block> clones = cloneDictionary.get(key);
				JSONObject cloneRecordJSON = new JSONObject();
				JSONArray loc = new JSONArray();
				for (Block cl : clones) {
					JSONObject locItem = new JSONObject();
					locItem.put("path", cl.getBlockPath().toString());
					loc.add(locItem);
				}
				cloneRecordJSON.put("fragment", clones.get(0).toString());
				cloneRecordJSON.put("size", CloneUtil.getSubTreeSize(clones.get(0)));
				cloneRecordJSON.put("loc", loc);
				report.addRecord(cloneRecordJSON);
			}
		}

		cloneSequenceList = ((TopDownSubTreeCollector) collector).getCloneSequenceList();
		for (ArrayList<Block> blockSeq : cloneSequenceList) {
			int hashVal = CloneUtil.hashBlockSequence(blockSeq);
			if (!cloneSequenceDictionary.containsKey(hashVal)) {
				cloneSequenceDictionary.put(hashVal, new ArrayList<ArrayList<Block>>());
			}
			cloneSequenceDictionary.get(hashVal).add(blockSeq);
		}
		
		for (int key: cloneSequenceDictionary.keySet()) {
			if (cloneSequenceDictionary.get(key).size() > 1) {
				ArrayList<ArrayList<Block>> cloneSeqList = cloneSequenceDictionary.get(key);
				JSONObject cloneSeqRecordJSON = new JSONObject();
				JSONArray loc = new JSONArray();
				for (ArrayList<Block> cloneSeq : cloneSeqList) {
					JSONObject locItem = new JSONObject();
					locItem.put("path", cloneSeq.get(0).getBlockPath().toString());
					loc.add(locItem);
				}
				cloneSeqRecordJSON.put("fragment", cloneSeqList.get(0).toString());
				cloneSeqRecordJSON.put("size", cloneSeqList.size());
				cloneSeqRecordJSON.put("loc", loc);
				report.addRecord(cloneSeqRecordJSON);
			}
		}
		
		
	}

	@Override
	public String toString() {
		return "CloneAnalyzer [" + (cloneDictionary != null ? "cloneDictionary=" + cloneDictionary : "") + "]";
	}

	@Override
	public Report getReport() {
		report.setTitle("Duplicate Code");
		return report;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		AnalysisManager.runAnalysis(CloneAnalyzer.class.getName());
	}

}
