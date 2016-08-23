package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale.LanguageRange;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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

public class DuplicateCodeAnalyzer extends Analyzer {
	private static final String name = "DuplicateCode";
	private static final String abbr = "DC";
	ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	ArrayList<Block> subtreeList;
	HashMap<Integer, ArrayList<Block>> cloneDictionary = new HashMap<Integer, ArrayList<Block>>();
	HashMap<Integer, ArrayList<ArrayList<Block>>> cloneSequenceDictionary;
	ArrayList<ArrayList<Block>> cloneSequenceList;
	DescriptiveStatistics cloneInstanceSizeStats = new DescriptiveStatistics();
	DescriptiveStatistics cloneGroupSizeStats = new DescriptiveStatistics();
	int cloneGroupCount = 0;
	int sameSpriteCount = 0;
	int interSpriteCount = 0;

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
			if (cloneDictionary.get(key).size() > 2) {
				ArrayList<Block> clones = cloneDictionary.get(key);
				JSONObject cloneRecordJSON = new JSONObject();
				JSONArray loc = new JSONArray();
				Set<String> trackScriptablesWithClones = new HashSet<>();
				for (Block cl : clones) {
					JSONObject locItem = new JSONObject();
					trackScriptablesWithClones.add(cl.getBlockPath().getScriptable().getName());
					locItem.put("path", cl.getBlockPath().toString());
					loc.add(locItem);
				}
				if(trackScriptablesWithClones.size()==1){
					sameSpriteCount+=1;
				}else{
					interSpriteCount+=1;
				}
				
				int cloneSize = CloneUtil.getSubTreeSize(clones.get(0));
				cloneRecordJSON.put("fragment", clones.get(0).toString());
				cloneRecordJSON.put("size", cloneSize);
				cloneRecordJSON.put("loc", loc);

				report.addRecord(cloneRecordJSON);
				cloneInstanceSizeStats.addValue(cloneSize);
				cloneGroupSizeStats.addValue(clones.size());
				cloneGroupCount++;
			}
		}

		ArrayList<ArrayList<Block>> initialCloneSequenceList = ((TopDownSubTreeCollector) collector)
				.getCloneSequenceList();
		HashMap<Integer, ArrayList<ArrayList<Block>>> initialCloneSequenceDictionary = hashIntoCloneGroup(initialCloneSequenceList);

		ArrayList<ArrayList<Block>> filteredCloneList = new ArrayList<ArrayList<Block>>();
		for (int key : initialCloneSequenceDictionary.keySet()) {
			if (initialCloneSequenceDictionary.get(key).size() > 1) {
				filteredCloneList.addAll(initialCloneSequenceDictionary
						.get(key));
			}
		}

		// now hash based on the first block of sequence
		HashMap<Integer, ArrayList<ArrayList<Block>>> seqWithSameFirstBlock = groupByFirstBlockInSequence(filteredCloneList);

		// now remove subclone sequence
		ArrayList<ArrayList<Block>> finalListCloneSeq = new ArrayList<>();
		for (int topBlockHash : seqWithSameFirstBlock.keySet()) {
			ArrayList<Block> largestSeqSoFar = seqWithSameFirstBlock.get(
					topBlockHash).get(0);
			if (seqWithSameFirstBlock.get(topBlockHash).size() > 1) {
				// select largest one
				for (ArrayList<Block> seq : seqWithSameFirstBlock
						.get(topBlockHash)) {
					if (seq.size() > largestSeqSoFar.size()) {
						largestSeqSoFar = seq;
					}
				}
			}
			finalListCloneSeq.add(largestSeqSoFar);
		}

		// remove subclone different start block
		HashMap<Integer, ArrayList<ArrayList<Block>>> seqInSameSubtree = groupBySubtree(finalListCloneSeq);
		// now remove subclone by comparing each pair in same subtree
		ArrayList<ArrayList<Block>> filteredSeqInSameSubtree = new ArrayList<>();
		for (int hash : seqInSameSubtree.keySet()) {
			filteredSeqInSameSubtree.addAll(filterSubClones(seqInSameSubtree
					.get(hash)));
		}

		HashMap<Integer, ArrayList<ArrayList<Block>>> finalCloneSequenceDictionary = hashIntoCloneGroup(filteredSeqInSameSubtree);
		for (int key : finalCloneSequenceDictionary.keySet()) {
			ArrayList<ArrayList<Block>> cloneSeqList = finalCloneSequenceDictionary
					.get(key);
			JSONObject cloneSeqRecordJSON = new JSONObject();
			JSONArray loc = new JSONArray();
			Set<String> trackScriptablesWithClones = new HashSet<>();
			for (ArrayList<Block> cloneSeq : cloneSeqList) {
				JSONObject locItem = new JSONObject();
				trackScriptablesWithClones.add(cloneSeq.get(0).getBlockPath().getScriptable().getName());
				locItem.put("path", cloneSeq.get(0).getBlockPath().toString());
				loc.add(locItem);
			}
			if(trackScriptablesWithClones.size()==1){
				sameSpriteCount+=1;
			}else{
				interSpriteCount+=1;
			}
			
			cloneSeqRecordJSON.put("fragment", cloneSeqList.get(0).toString());
			cloneSeqRecordJSON.put("size", cloneSeqList.get(0).size());
			cloneSeqRecordJSON.put("loc", loc);
			
			report.addRecord(cloneSeqRecordJSON);
			cloneInstanceSizeStats.addValue(cloneSeqList.get(0).size());
			cloneGroupSizeStats.addValue(loc.size());
			
			cloneGroupCount++;
		}

	}

	public Comparator<ArrayList<Block>> sequenceComparator = new Comparator<ArrayList<Block>>() {
		@Override
		public int compare(ArrayList<Block> seq1, ArrayList<Block> seq2) {
			return seq1.size() - seq2.size();
		}
	};

	private ArrayList<ArrayList<Block>> filterSubClones(
			ArrayList<ArrayList<Block>> blockSeqs) {
		if (blockSeqs.size() == 1) {
			return blockSeqs;
		}
		ArrayList<ArrayList<Block>> filteredSubCloneSeqs = new ArrayList<>();
		// sort largest to smallest
		Collections.sort(blockSeqs, sequenceComparator);
		// compare largest to smaller and decide if subclone
		for (int i = 0; i < blockSeqs.size(); i++) {
			boolean isSubClone = false;
			ArrayList<Block> smallerSeq = blockSeqs.get(i);
			for (int j = i + 1; j < blockSeqs.size(); j++) {
				LinkedHashSet smallerSeqSet = new LinkedHashSet(smallerSeq);
				ArrayList<Block> largerSeq = blockSeqs.get(j);
				LinkedHashSet largerSeqSet = new LinkedHashSet(largerSeq);
				if (largerSeqSet.containsAll(smallerSeqSet)) {
					// filteredSubCloneSeqs.add(smallerSeq);
					isSubClone = true;
					break;
				}
			}
			if (!isSubClone) {
				filteredSubCloneSeqs.add(smallerSeq);
			}
		}
		return filteredSubCloneSeqs;
	}

	private HashMap<Integer, ArrayList<ArrayList<Block>>> groupByFirstBlockInSequence(
			ArrayList<ArrayList<Block>> sequenceList) {
		HashMap<Integer, ArrayList<ArrayList<Block>>> seqWithSameFirstBlock = new HashMap<>();
		for (ArrayList<Block> seq : sequenceList) {
			if (!seqWithSameFirstBlock.containsKey(seq.get(0).hashCode())) {
				seqWithSameFirstBlock.put(seq.get(0).hashCode(),
						new ArrayList());
			}
			seqWithSameFirstBlock.get(seq.get(0).hashCode()).add(seq);
		}
		return seqWithSameFirstBlock;
	}

	private HashMap<Integer, ArrayList<ArrayList<Block>>> groupBySubtree(
			ArrayList<ArrayList<Block>> sequenceList) {
		HashMap<Integer, ArrayList<ArrayList<Block>>> seqWithSameFirstBlock = new HashMap<>();
		for (ArrayList<Block> seq : sequenceList) {
			if (!seqWithSameFirstBlock.containsKey(seq.get(0).getParent()
					.hashCode())) {
				seqWithSameFirstBlock.put(seq.get(0).getParent().hashCode(),
						new ArrayList());
			}
			seqWithSameFirstBlock.get(seq.get(0).getParent().hashCode()).add(
					seq);
		}
		return seqWithSameFirstBlock;
	}

	private HashMap<Integer, ArrayList<ArrayList<Block>>> hashIntoCloneGroup(
			ArrayList<ArrayList<Block>> initialCloneSequenceList) {
		HashMap<Integer, ArrayList<ArrayList<Block>>> initialCloneSequenceDictionary = new HashMap<Integer, ArrayList<ArrayList<Block>>>();
		for (ArrayList<Block> blockSeq : initialCloneSequenceList) {
			int hashVal = CloneUtil.hashBlockSequence(blockSeq);
			if (!initialCloneSequenceDictionary.containsKey(hashVal)) {
				initialCloneSequenceDictionary.put(hashVal,
						new ArrayList<ArrayList<Block>>());
			}
			initialCloneSequenceDictionary.get(hashVal).add(blockSeq);
		}
		return initialCloneSequenceDictionary;
	}

	@Override
	public String toString() {
		return "CloneAnalyzer ["
				+ (cloneDictionary != null ? "cloneDictionary="
						+ cloneDictionary : "") + "]";
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("count", cloneGroupCount);
		if (cloneGroupCount == 0) {
			conciseReport.put("groupSize", 0);
			conciseReport.put("instanceSize", 0);
		} else {
			conciseReport.put("groupSize", cloneGroupSizeStats.getMean());
			conciseReport.put("instanceSize", cloneInstanceSizeStats.getMean());
			conciseReport.put("sameSpriteClone", sameSpriteCount);
			conciseReport.put("interSpriteClone", interSpriteCount);
		}

		report.setConciseJSONReport(conciseReport);
		return report;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		AnalysisManager.runAnalysis(DuplicateCodeAnalyzer.class.getName(),
				AnalysisManager.smallTestInput);
	}

}
