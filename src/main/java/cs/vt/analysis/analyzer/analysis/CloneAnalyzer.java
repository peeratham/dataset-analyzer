package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDownSubTreeCollector;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class CloneAnalyzer extends BaseAnalyzer{
	private AnalysisReport report = new AnalysisReport();
	private ArrayList<Block> subtreeList;
	private HashMap<Integer, ArrayList<Block>> cloneDictionary = new HashMap<Integer, ArrayList<Block>>();

	@Override
	public void analyze() throws AnalysisException {
		Visitor collector = new TopDownSubTreeCollector(new Identity());
		try {
			project.accept(collector);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
		subtreeList = ((TopDownSubTreeCollector)collector).getSubTreeList();
		for (Block subtree : subtreeList) {
			int hashVal = CloneUtil.hashSubTree(subtree);
			if(cloneDictionary.containsKey(hashVal)){
				cloneDictionary.get(hashVal).add(subtree);
			}else{
				cloneDictionary.put(hashVal, new ArrayList<Block>());
				cloneDictionary.get(hashVal).add(subtree);
			}
			
		}
		
		for (int key : cloneDictionary.keySet()) {
			if(cloneDictionary.get(key).size()>1){
				ArrayList<Block> clones = cloneDictionary.get(key);
				String cloneInstance = clones.get(0).toString();
				ArrayList<String> occurrences = new ArrayList<String>();
				for(Block cl: clones){
					occurrences.add(cl.getBlockPath().toString());
				}
				report.addRecord(clones.get(0).toString()+" @"+occurrences);
			}
		}
		
	}

	

	@Override
	public String toString() {
		return "CloneAnalyzer ["
				+ (cloneDictionary != null ? "cloneDictionary="
						+ cloneDictionary : "") + "]";
	}



	@Override
	public AnalysisReport getReport() {
		report.setTitle("Duplicate Code");
		return report;
	}

}
