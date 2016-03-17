package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDownSubTreeCollector;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class CloneAnalyzer extends Analyzer{
	private AnalysisReport report = new AnalysisReport();
	private ArrayList<Block> subtreeList;
	private HashMap<Integer, ArrayList<Block>> cloneDictionary = new HashMap<Integer, ArrayList<Block>>();

	@SuppressWarnings("unchecked")
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
				String fragment = clones.get(0).toString();
				JSONObject cloneRecordJSON = new JSONObject();
				JSONArray loc = new JSONArray();
				for(Block cl: clones){
					JSONObject locItem = new JSONObject();
					locItem.put("sprite", cl.getBlockPath().getPathList().get(0));
					locItem.put("path", cl.getBlockPath().toString());
					loc.add(locItem);
				}
				cloneRecordJSON.put("fragment", clones.get(0).toString());
				cloneRecordJSON.put("size", CloneUtil.getSubTreeSize(clones.get(0)));
				cloneRecordJSON.put("loc", loc);
				report.addRecord(cloneRecordJSON.toJSONString());
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
