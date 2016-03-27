package cs.vt.analysis.visual;

import cs.vt.analysis.analyzer.nodes.Script;

public class VisualMeasure {
	private static final double offAlignmentThreshold = 10;

	public static boolean isVerticalAligned(ScriptProperty a, ScriptProperty b){
		double xA = a.getCoordinate().getX();
		double xB = b.getCoordinate().getX();
		if(xA==xB){
			return true;
		}else{
			double difference = Math.abs(xA-xB); 
			System.out.println(difference);
			if(difference<=offAlignmentThreshold){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isHorizontalAligned(ScriptProperty a, ScriptProperty b){
		double yA = a.getCoordinate().getY();
		double yB = b.getCoordinate().getY();
		if(yA==yB){
			return true;
		}else{
			double difference = Math.abs(yA-yB); 
			System.out.println(difference);
			if(difference<=offAlignmentThreshold){
				return true;
			}
		}
		
		return false;
	}
}
