package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cs.vt.analysis.analyzer.analysis.AnalysisUtil;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class Script implements Visitable {

	private List<Block> blocks;
	private int xPos;
	private int yPos;
	private Scriptable parent;

	
	
	public Script() {
		blocks = new ArrayList();
		xPos = 0;
		yPos = 0;
	}

	public List<Block> getBlocks(){
		return blocks;
	}

	@Override
	public String toString() {
		String header = "Script@(x:" + xPos +", y:"+yPos+ ")\n";
		String content = "";
		for(Block b: blocks){
			content += b+"\n";
		}
		return header+content;
	}

	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	public int[] getPosition(){
		return new int[]{xPos,yPos};
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
		
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitScript(this);
		
	}
	
	public void setParent(Scriptable s){
		parent = s;
	}

	public Scriptable getParent() {
		return parent;
	}

	public String getPath() {
		ArrayList<String> path = new ArrayList<String>();
		String firstBlock = this.getBlocks().get(0).getCompactString();
		String scriptPath = "Script@x"+this.getPosition()[0]+" y"+this.getPosition()[1]+"["+firstBlock+"]";
		path.add(0,scriptPath);
		Scriptable scrptable = this.getParent();
		path.add(0, scrptable.getName());
		return String.join("|", path);
	}

	public ArrayList<Block> containsBlock(String blockCommand) {
//		AnalysisUtil finder = new AnalysisUtil(this);
		return AnalysisUtil.findBlock(this, blockCommand);
		
		
	}

	public HashSet<String> getVariables() {
		HashSet<String> vars = new HashSet<String>();
		for (Block b : this.blocks) {
			collectVars(b, vars);
		}
		
		return vars;
	}

	private void collectVars(Block block, HashSet<String> vars) {
	
		
	}
	
	
	
	

}
