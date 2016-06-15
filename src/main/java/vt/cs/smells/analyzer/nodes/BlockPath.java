package vt.cs.smells.analyzer.nodes;

import java.util.ArrayList;

public class BlockPath {

	
	ArrayList<String> path = new ArrayList<String>();
	private Scriptable scriptable;
	private Script script;
	public BlockPath(Block current) {
		
		
		do {
			
			if (current.hasNestedBlocks()) {
				path.add(0, current.getCommand());
			} else {
				path.add(0, current.toString());
			}
			
			if (!(current.getParent() instanceof Block)) {
				break;
			}else{
				current = (Block) current.getParent();
			}
		} while (true);
		
		script = (Script) current.getParent();
		String firstBlock = script.getBlocks().get(0).getCompactString();
		String scriptString = "Script@x"+script.getPosition()[0]+" y"+script.getPosition()[1]+"["+firstBlock+"]";
		path.add(0,scriptString);
		scriptable = script.getParent();
		path.add(0, scriptable.getName());
	}

	public void setPathList(ArrayList<String> path) {
		this.path = path;
	}
	
	public ArrayList<String> getPathList(){
		return path;
	}

	
	public Scriptable getScriptable() {
		return scriptable;
	}

	public Script getScript() {
		return script;
	}

	@Override
	public String toString(){
		return String.join("|", path);
	}

}
