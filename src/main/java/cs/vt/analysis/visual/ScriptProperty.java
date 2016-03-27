package cs.vt.analysis.visual;

import java.util.HashSet;

import cs.vt.analysis.analyzer.analysis.Coordinate;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Visitable;

public class ScriptProperty {

	private Script script;
	private Coordinate coordinate;
	private HashSet<String> variables;

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public HashSet<String> getVariables() {
		return variables;
	}

	public ScriptProperty(Script s) {
		this.script = s;
		extractProperties();
	}

	private void extractProperties() {
		int[] pos = script.getPosition();
		this.coordinate = new Coordinate(pos[0],pos[1]);
		
		variables = PropertiesCollector.collectVariables(script);
	}
	
	public Block getFirstBlock(){
			return script.getBlocks().get(0);
	}

	@Override
	public String toString() {
		return "ScriptProperty ["
				+ (script != null ? "script=" + script + ", " : "")
				+ (coordinate != null ? "coordinate=" + coordinate + ", " : "")
				+ (variables != null ? "variables=" + variables : "") + "]";
	}

	public Script getScript() {
		return script;
	}
	
	
	
	
}
