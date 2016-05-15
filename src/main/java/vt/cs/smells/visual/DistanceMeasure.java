package vt.cs.smells.visual;

import java.util.HashSet;

import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.Coordinate;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.CustomBlock;
import vt.cs.smells.analyzer.nodes.Script;

public abstract class DistanceMeasure {

	public abstract double getDist(ScriptProperty pA, ScriptProperty pB);

	public static final class SharedVariableBased extends DistanceMeasure {

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			if (pA == pB) {
				return 0;
			}
			HashSet<String> aVars = pA.getVariables();
			HashSet<String> bVars = pB.getVariables();
			HashSet<String> union = new HashSet<String>();
			union.addAll(aVars);
			union.addAll(bVars);
			HashSet<String> intersect = new HashSet<String>(aVars);
			intersect.retainAll(bVars);

			if (union.size() == 0) {
				return 1;
			}
			double dist = 1 - (double) intersect.size() / (double) union.size();
			return dist;
		}
	}

	public static final class CoordinateBased extends DistanceMeasure {

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			return Coordinate.dist(pA.getCoordinate(), pB.getCoordinate());
		}

	}

	public static final class BlockHatBased extends DistanceMeasure {

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			if (pA.getFirstBlock().getCommand()
					.equals(pB.getFirstBlock().getCommand())) {
				return 0;
			} else {
				return 1;
			}

		}

	}

	public static final class SharedBlockBased extends DistanceMeasure {

		@Override
		public double getDist(ScriptProperty pA, ScriptProperty pB) {
			if (pA == pB) {
				return 0;
			}
			HashSet<String> customBlocksInA = new HashSet<String>();
			HashSet<String> customBlocksInB = new HashSet<String>();
			for(Block bA:AnalysisUtil.findBlock(pA.getScript(), "call")){
				customBlocksInA.add(bA.getBlockType().getSpec());
			}
			for(Block bB : AnalysisUtil.findBlock(pB.getScript(),"call")){
				customBlocksInB.add(bB.getBlockType().getSpec());
			}
			
			if(pA.getFirstBlock().hasCommand("procDef")){
				CustomBlock b = (CustomBlock) pA.getFirstBlock().getArgs(0);
				customBlocksInA.add(b.getBlockType().getSpec());
			}
			if(pB.getFirstBlock().hasCommand("procDef")){
				CustomBlock b = (CustomBlock) pB.getFirstBlock().getArgs(0);
				customBlocksInB.add(b.getBlockType().getSpec());
			}
			HashSet<String> union = new HashSet<String>();
			union.addAll(customBlocksInA);
			union.addAll(customBlocksInB);
			
			HashSet<String> intersect = new HashSet<String>(customBlocksInA);
			intersect.retainAll(customBlocksInB);
			
			if (union.size() == 0) {
				return 1;
			}
			double dist = 1 - (double) intersect.size() / (double) union.size();
			return dist;

		}

	}

}
