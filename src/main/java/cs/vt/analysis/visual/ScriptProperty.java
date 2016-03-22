package cs.vt.analysis.visual;

import java.util.HashSet;

import cs.vt.analysis.analyzer.analysis.Coordinate;
import cs.vt.analysis.analyzer.nodes.Script;

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

	@Override
	public String toString() {
		return "ScriptProperty ["
				+ (script != null ? "script=" + script + ", " : "")
				+ (coordinate != null ? "coordinate=" + coordinate + ", " : "")
				+ (variables != null ? "variables=" + variables : "") + "]";
	}
	
	
	
	
}
