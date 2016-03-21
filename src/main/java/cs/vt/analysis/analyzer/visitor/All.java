package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class All implements Visitor{
	Visitor v;
	public All(Visitor v){
		this.v = v;
	}
//	@Override
//	public void visit(Object node) throws VisitFailure {
		
//		if(node instanceof Block){
//			Block b= (Block) node;
//			this.visit(b.getArgs());
//			
//		}else if(node instanceof List<?>){
//			List<Object> args = (List<Object>) node;
//			for (Object o : args) {
//				this.visit(o);
//			}
//		}else{
//			String value = node.toString();
//			System.out.println(value);
//		}
//	}


	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		for (String name : scratchProject.getAllScriptables().keySet()) {
			scratchProject.getScriptable(name).accept(v);;
		}
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		for (int i = 0; i < scriptable.getNumScripts(); i++) {
			scriptable.getScript(i).accept(v);
		}
	}


	public void visitScript(Script script) throws VisitFailure {
		for (int i = 0; i < script.getBlocks().size(); i++) {
			script.getBlocks().get(i).accept(v);
		}
	}

	public void visitBlock(Block block) throws VisitFailure{
		for(Object arg: block.getArgs()){
			if(arg instanceof ArrayList){
				for(Block b: (ArrayList<Block>)arg){
					b.accept(v);
				}
			}
			if(arg instanceof Block){
				((Block) arg).accept(v);
			}
		}
		
	}
}
