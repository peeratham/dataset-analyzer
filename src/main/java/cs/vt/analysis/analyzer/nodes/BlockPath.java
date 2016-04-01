package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;

public class BlockPath {

	
	ArrayList<String> path = new ArrayList<String>();
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
		
		Script scrpt = (Script) current.getParent();
		String firstBlock = scrpt.getBlocks().get(0).getCompactString();
		String scriptString = "Script@x"+scrpt.getPosition()[0]+" y"+scrpt.getPosition()[1]+"["+firstBlock+"]";
		path.add(0,scriptString);
		Scriptable scrptable = scrpt.getParent();
		path.add(0, scrptable.getName());
	}

	public void setPathList(ArrayList<String> path) {
		this.path = path;
	}
	
	public ArrayList<String> getPathList(){
		return path;
	}
	
	@Override
	public String toString(){
		return String.join("|", path);
	}

}
