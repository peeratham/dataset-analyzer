package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Visitable;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDownSubTreeCollector;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class DuplicateCodeAnalyzer extends Analyzer {
	private static final String name = "DuplicateCode";
	private static final String abbr = "DC";
	ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	DescriptiveStatistics cloneInstanceSizeStats = new DescriptiveStatistics();
	DescriptiveStatistics cloneGroupSizeStats = new DescriptiveStatistics();
	int projectCloneGroupCount = 0;
	int projectSameSpriteCount = 0;
	int projectInterSpriteCount = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void analyze() throws AnalysisException {
		// subtree clones
		ListAnalysisReport interSpriteCloneReport = checkInterSpriteClone(project);
		projectInterSpriteCount = interSpriteCloneReport.getResult().size();

		for (String scriptableName : project.getAllScriptables().keySet()) {
			ListAnalysisReport sameSpriteCloneReport = checkSameSpriteCloneFor(project
					.getScriptable(scriptableName));
			projectSameSpriteCount += sameSpriteCloneReport.getResult().size();
			report.addRecord(sameSpriteCloneReport.getResult());
			
		}
		projectCloneGroupCount = projectSameSpriteCount;

	}

	private ListAnalysisReport checkInterSpriteClone(ScratchProject project) {
		ListAnalysisReport thisReport = new ListAnalysisReport(name, abbr);
		Visitor collector = traverseProjectTreeToCollectProgramFragment(project);
		ArrayList<Block> subtreeList = ((TopDownSubTreeCollector) collector)
				.getSubTreeList();
		List<JSONObject> subTreeClones = determineSubtreeClone(subtreeList);
		for (JSONObject subtreeClone : subTreeClones) {
			if ((boolean) subtreeClone.get("interSprite")) {
				thisReport.addRecord(subtreeClone);
			}
			;

		}

		// sequence clones
		List<List<Block>> initialCloneSequenceList = ((TopDownSubTreeCollector) collector)
				.getCloneSequenceList();

		List<JSONObject> sequenceClones = determineSequenceClones(initialCloneSequenceList);
		for (JSONObject seqCloneRecord : sequenceClones) {
			if ((boolean) seqCloneRecord.get("interSprite")) {
				thisReport.addRecord(seqCloneRecord);
			}
			;
		}

		return thisReport;
	}

	public ListAnalysisReport checkSameSpriteCloneFor(Visitable node) {
		ListAnalysisReport thisReport = new ListAnalysisReport(name, abbr);
		Visitor collector = traverseProjectTreeToCollectProgramFragment(node);
		ArrayList<Block> subtreeList = ((TopDownSubTreeCollector) collector)
				.getSubTreeList();
		List<JSONObject> subTreeClones = determineSubtreeClone(subtreeList);
		for (JSONObject subtreeClone : subTreeClones) {
			thisReport.addRecord(subtreeClone);
		}

		// sequence clones
		List<List<Block>> initialCloneSequenceList = ((TopDownSubTreeCollector) collector)
				.getCloneSequenceList();

		List<JSONObject> sequenceClones = determineSequenceClones(initialCloneSequenceList);
		for (JSONObject seqCloneRecord : sequenceClones) {
			thisReport.addRecord(seqCloneRecord);
		}

		return thisReport;
	}

	public List<JSONObject> determineSequenceClones(
			List<List<Block>> initialCloneSequenceList) {
		boolean interSprite = false;

		List<JSONObject> sequenceClones = new ArrayList<>();
		Map<Integer, List<List<Block>>> initialCloneSequenceDictionary = hashIntoCloneGroup(initialCloneSequenceList);

		List<List<Block>> filteredCloneList = new ArrayList<>();
		for (int key : initialCloneSequenceDictionary.keySet()) {
			if (initialCloneSequenceDictionary.get(key).size() > 1) {
				filteredCloneList.addAll(initialCloneSequenceDictionary
						.get(key));
			}
		}

		// now hash based on the first block of sequence
		Map<Integer, List<List<Block>>> seqWithSameFirstBlock = groupByFirstBlockInSequence(filteredCloneList);

		// now remove subclone sequence
		List<List<Block>> finalListCloneSeq = new ArrayList<>();
		for (int topBlockHash : seqWithSameFirstBlock.keySet()) {
			List<Block> largestSeqSoFar = seqWithSameFirstBlock.get(
					topBlockHash).get(0);
			if (seqWithSameFirstBlock.get(topBlockHash).size() > 1) {
				// select largest one
				for (List<Block> seq : seqWithSameFirstBlock.get(topBlockHash)) {
					if (seq.size() > largestSeqSoFar.size()) {
						largestSeqSoFar = seq;
					}
				}
			}
			finalListCloneSeq.add(largestSeqSoFar);
		}

		// remove subclone different start block in the same script?
		Map<Integer, List<List<Block>>> seqInSameSubtree = groupBySubtree(finalListCloneSeq);
		// now remove subclone by comparing each pair in same subtree
		List<List<Block>> filteredSeqInSameSubtree = new ArrayList<>();
		for (int hash : seqInSameSubtree.keySet()) {
			filteredSeqInSameSubtree.addAll(filterSubClones(seqInSameSubtree
					.get(hash)));
		}

		Map<Integer, List<List<Block>>> finalCloneSequenceDictionary = hashIntoCloneGroup(filteredSeqInSameSubtree);
		for (int key : finalCloneSequenceDictionary.keySet()) {
			List<List<Block>> cloneSeqList = finalCloneSequenceDictionary
					.get(key);
			JSONObject cloneSeqRecordJSON = new JSONObject();
			JSONArray loc = new JSONArray();
			Set<String> trackScriptablesWithClones = new HashSet<>();
			for (List<Block> cloneSeq : cloneSeqList) {
				JSONObject locItem = new JSONObject();
				trackScriptablesWithClones.add(cloneSeq.get(0).getBlockPath()
						.getScriptable().getName());
				locItem.put("path", cloneSeq.get(0).getBlockPath().toString());
				loc.add(locItem);
			}
			if (trackScriptablesWithClones.size() != 1) {
				interSprite = true;
			}
			cloneSeqRecordJSON.put("interSprite", interSprite);
			cloneSeqRecordJSON.put("fragment", cloneSeqList.get(0).toString());
			cloneSeqRecordJSON.put("size", cloneSeqList.get(0).size());
			cloneSeqRecordJSON.put("loc", loc);

			sequenceClones.add(cloneSeqRecordJSON);

			cloneInstanceSizeStats.addValue(cloneSeqList.get(0).size());
			cloneGroupSizeStats.addValue(loc.size());
		}
		return sequenceClones;
	}

	public List<JSONObject> determineSubtreeClone(List<Block> subtreeList) {
		HashMap<Integer, ArrayList<Block>> cloneDictionary = new HashMap<Integer, ArrayList<Block>>();
		List<JSONObject> subTreeCloneList = new ArrayList<>();
		for (Block subtree : subtreeList) {
			int hashVal = CloneUtil.hashSubTree(subtree);
			if (!cloneDictionary.containsKey(hashVal)) {
				cloneDictionary.put(hashVal, new ArrayList<Block>());
			}
			cloneDictionary.get(hashVal).add(subtree);
		}

		for (int key : cloneDictionary.keySet()) {
			if (cloneDictionary.get(key).size() > 1) {
				boolean interSpriteClone = false;
				ArrayList<Block> clones = cloneDictionary.get(key);
				JSONObject cloneRecordJSON = new JSONObject();
				JSONArray loc = new JSONArray();
				Set<String> trackScriptablesWithClones = new HashSet<>();
				for (Block cl : clones) {
					JSONObject locItem = new JSONObject();
					trackScriptablesWithClones.add(cl.getBlockPath()
							.getScriptable().getName());
					locItem.put("path", cl.getBlockPath().toString());
					loc.add(locItem);
				}
				if (trackScriptablesWithClones.size() != 1) {
					interSpriteClone = true;
				}

				int cloneSize = CloneUtil.getSubTreeSize(clones.get(0));
				if (cloneSize <= 2) {
					continue;
				}
				cloneRecordJSON.put("interSprite", interSpriteClone);
				cloneRecordJSON.put("fragment", clones.get(0).toString());
				cloneRecordJSON.put("size", cloneSize);
				cloneRecordJSON.put("loc", loc);
				subTreeCloneList.add(cloneRecordJSON);

				cloneInstanceSizeStats.addValue(cloneSize);
				cloneGroupSizeStats.addValue(clones.size());
			}
		}
		return subTreeCloneList;
	}

	public Visitor traverseProjectTreeToCollectProgramFragment(Visitable node) {
		Visitor collector = new TopDownSubTreeCollector(new Identity());
		try {
			node.accept(collector);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
		return collector;
	}

	public Comparator<List<Block>> sequenceComparator = new Comparator<List<Block>>() {
		@Override
		public int compare(List<Block> seq1, List<Block> seq2) {
			return seq1.size() - seq2.size();
		}
	};

	private List<List<Block>> filterSubClones(List<List<Block>> blockSeqs) {
		if (blockSeqs.size() == 1) {
			return blockSeqs;
		}
		List<List<Block>> filteredSubCloneSeqs = new ArrayList<>();
		// sort largest to smallest
		Collections.sort(blockSeqs, sequenceComparator);
		// compare largest to smaller and decide if subclone
		for (int i = 0; i < blockSeqs.size(); i++) {
			boolean isSubClone = false;
			List<Block> smallerSeq = blockSeqs.get(i);
			for (int j = i + 1; j < blockSeqs.size(); j++) {
				LinkedHashSet smallerSeqSet = new LinkedHashSet(smallerSeq);
				List<Block> largerSeq = blockSeqs.get(j);
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

	private Map<Integer, List<List<Block>>> groupByFirstBlockInSequence(
			List<List<Block>> sequenceList) {
		Map<Integer, List<List<Block>>> seqWithSameFirstBlock = new HashMap<>();
		for (List<Block> seq : sequenceList) {
			if (!seqWithSameFirstBlock.containsKey(seq.get(0).hashCode())) {
				seqWithSameFirstBlock.put(seq.get(0).hashCode(),
						new ArrayList());
			}
			seqWithSameFirstBlock.get(seq.get(0).hashCode()).add(seq);
		}
		return seqWithSameFirstBlock;
	}

	private Map<Integer, List<List<Block>>> groupBySubtree(
			List<List<Block>> sequenceList) {
		Map<Integer, List<List<Block>>> seqWithSameFirstBlock = new HashMap<>();
		for (List<Block> seq : sequenceList) {
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

	private Map<Integer, List<List<Block>>> hashIntoCloneGroup(
			List<List<Block>> initialCloneSequenceList) {
		Map<Integer, List<List<Block>>> initialCloneSequenceDictionary = new HashMap<>();
		for (List<Block> blockSeq : initialCloneSequenceList) {
			int hashVal = CloneUtil.hashBlockSequence(blockSeq);
			if (!initialCloneSequenceDictionary.containsKey(hashVal)) {
				initialCloneSequenceDictionary.put((Integer) hashVal,
						new ArrayList<>());
			}
			initialCloneSequenceDictionary.get(hashVal).add(blockSeq);
		}
		return initialCloneSequenceDictionary;
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("count", projectCloneGroupCount);
		if (projectCloneGroupCount == 0) {
			conciseReport.put("groupSize", 0);
			conciseReport.put("instanceSize", 0);
			conciseReport.put("sameSpriteClone", projectSameSpriteCount);
		} else {
			conciseReport.put("groupSize", cloneGroupSizeStats.getMean());
			conciseReport.put("instanceSize", cloneInstanceSizeStats.getMean());
			conciseReport.put("sameSpriteClone", projectSameSpriteCount);
			conciseReport.put("interSpriteClone", projectInterSpriteCount);
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
