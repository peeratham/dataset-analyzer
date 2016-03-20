package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.visual.DistanceMatrix;

public class DistanceBasedAnalyzer extends Analyzer {
	ArrayList<DistanceMatrix> matrixList = new ArrayList<DistanceMatrix>();
	
	

	@Override
	public void analyze() throws AnalysisException {
		Map<String, Scriptable> scriptDict = project.getAllScriptables();
		for(String scriptableName: scriptDict.keySet()){
			Scriptable scriptable = scriptDict.get(scriptableName);
			DistanceMatrix matrix = buildDistanceMatrix(scriptable);
			matrix.printDistanceMatrix();
			matrixList.add(matrix);
		}
		
		DistanceMatrix m = matrixList.get(0);
		m.computeClustering();
		
	}
	
	private DistanceMatrix buildDistanceMatrix(Scriptable scriptable) {
		DistanceMatrix matrix = new DistanceMatrix();
		for (Script s : scriptable.getScripts()) {
			int[] pos = s.getPosition();
			matrix.add(s.getPosition().toString() , new Coordinate(pos[0],pos[1]));
		}
		return matrix;
	}
	
	
	

	public static ArrayList<Coordinate> getLocationOfScriptsForScriptable(Scriptable s){
		ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
		for(Script sc : s.getScripts()){
			int[] pos = sc.getPosition();
			coordList.add(new Coordinate(pos[0],pos[1]));
		}
		return coordList;
	}
	
	public static Map<String, ArrayList<Coordinate>> getLocationOfScripts(){
		Map<String, ArrayList<Coordinate>> coordMap = new HashMap<String, ArrayList<Coordinate>>();  
		return coordMap;
	}
	
	public double dist(Script a, Script b){
		return 0;
	}
	
	

	@Override
	public AnalysisReport getReport() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
