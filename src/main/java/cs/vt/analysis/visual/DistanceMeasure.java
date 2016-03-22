package cs.vt.analysis.visual;

import java.util.HashSet;

import cs.vt.analysis.analyzer.analysis.Coordinate;
import cs.vt.analysis.analyzer.nodes.Script;

public abstract class DistanceMeasure {
	public  abstract double getDist(ScriptProperty pA, ScriptProperty pB);
	
	public static final class SharedVariableBased extends DistanceMeasure{

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			if(pA==pB){
				return 0;
			}
			HashSet<String> aVars = pA.getVariables();
			HashSet<String> bVars = pB.getVariables();
			HashSet<String> union = new HashSet<String>();
			union.addAll(aVars);
			union.addAll(bVars);
			HashSet<String> intersect = new HashSet<String>(aVars);
			intersect.retainAll(bVars);
			
			if(union.size()==0){
				return 1;
			}
			double dist = 1 - (double)intersect.size()/(double)union.size();
			return dist;
		}
	}
	
	public static final class CoordinateBased extends DistanceMeasure {

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			return Coordinate.dist(pA.getCoordinate(), pB.getCoordinate());
		}
		
	}

	
}
