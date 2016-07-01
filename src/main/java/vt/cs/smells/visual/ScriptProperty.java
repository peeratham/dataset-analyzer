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
		this.coordinate = new Coordinate(pos[0], pos[1]);

		variables = PropertiesCollector.collectVariables(script);
	}

	public Block getFirstBlock() {
		return script.getBlocks().get(0);
	}

	@Override
	public String toString() {
		return hashCode()+getFirstBlock().toString();
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ScriptProperty)){
			return false;
		}
		ScriptProperty other = (ScriptProperty)o;
		if(!other.getFirstBlock().equals(this.getFirstBlock())){
			return false;
		}
		if(other.getFirstBlock().hasCommand("whenIReceive")){
			if(!other.getFirstBlock().getArgs(0).equals(this.getFirstBlock().getArgs(0))){
				return false;
			}
		}
		
		if(other.getVariables().isEmpty() && this.getVariables().isEmpty()){
			return false;
		}
		
		if(!other.getVariables().equals(this.getVariables())){
			return false;
		}
		
		return true;
	}

	public Script getScript() {
		return script;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + getFirstBlock().getCommand().hashCode();
		if (getFirstBlock().hasCommand("whenIReceive")) {
			hash = 31 * hash + getFirstBlock().getArgs(0).hashCode();
		}
		hash = 31 * hash + getVariables().hashCode();
		return hash;
	}

}
