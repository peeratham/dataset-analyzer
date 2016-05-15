package vt.cs.smells.visual;

import java.util.HashSet;

import vt.cs.smells.analyzer.Coordinate;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Visitable;

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
