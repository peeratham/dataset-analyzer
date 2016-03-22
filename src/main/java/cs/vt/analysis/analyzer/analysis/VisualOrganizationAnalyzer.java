package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.visual.DistanceMatrixGenerator;
import cs.vt.analysis.visual.DistanceMeasure;
import cs.vt.analysis.visual.DistanceMeasure.CoordinateBased;
import cs.vt.analysis.visual.ScriptProperty;

public class VisualOrganizationAnalyzer extends Analyzer {
	
	ArrayList<Class<?>> measures = new ArrayList<Class<?>>();
	
	@Override
	public void analyze() throws AnalysisException {
		measures.add(DistanceMeasure.CoordinateBased.class);
		measures.add(DistanceMeasure.SharedVariableBased.class);
	
		Map<String, Scriptable> scriptDict = project.getAllScriptables();
		for(String scriptableName: scriptDict.keySet()){
			Scriptable scriptable = scriptDict.get(scriptableName);
			System.out.println(scriptable.getName());
			for (Class<?> m : measures) {
				try {
					System.out.println(m.getName());
					double[][] matrix = buildDistanceMatrix(scriptable, (DistanceMeasure) m.newInstance());
					DistanceMatrixGenerator.printDistanceMatrix(matrix);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			
			
			
		}
		
//		DistanceMatrixGenerator m = matrices.get(0);
//		m.computeClustering();
		
	}
	
	
	
	public static double[][] buildDistanceMatrix(Scriptable scriptable,
			DistanceMeasure measure) {
		DistanceMatrixGenerator matrixGenerator = new DistanceMatrixGenerator();
		for (Script s : scriptable.getScripts()) {
			ScriptProperty prop = new ScriptProperty(s);
			matrixGenerator.add(s.getPosition().toString() , prop);
		}
		
		
		return matrixGenerator.computeDistanceMatrix(measure);
	}



//	private DistanceMatrix buildUserDistanceMatrix(Scriptable scriptable) {
//		DistanceMatrix matrix = new DistanceMatrix();
//		for (Script s : scriptable.getScripts()) {
//			int[] pos = s.getPosition();
//			ScriptProperty prop = new ScriptProperty(s);
//			matrix.add(s.getPosition().toString() , new Coordinate(pos[0],pos[1]));
//			
//		}
//		return matrix;
//	}

	@Override
	public AnalysisReport getReport() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
