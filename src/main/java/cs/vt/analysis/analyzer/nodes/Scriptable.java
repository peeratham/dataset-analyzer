package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class Scriptable implements Visitable {
	String name;
	List<Script> scripts;
	private Map<String,Object> variables;

	public Scriptable(){
		scripts = new ArrayList<Script>();
		variables = new HashMap<String, Object>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addScript(Script script) {
		scripts.add(script);
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Script scrpt : scripts) {
			sb.append(scrpt);
			sb.append("\n");
		}
		return sb.toString();
	}

	public Script getScript(int index){
		return scripts.get(index);
	}
	
	public List<Script> getScripts(){
		return scripts;
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitScriptable(this);
		
	}

	public int getNumScripts() {
		return scripts.size();
	}
	
	public void addVar(String name, Object value) {
		variables.put(name, value);
	}
	
	public Map<String, Object> getAllVariables() {
		return variables;
	}

	public void setVars(Map<String, Object> loadVariables) {
		this.variables = loadVariables;
		
	}
	

}
