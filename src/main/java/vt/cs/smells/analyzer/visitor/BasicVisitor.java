package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class BasicVisitor implements Visitor{

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
			scratchProject.getScriptable(name).accept(this);;
		}
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		for (int i = 0; i < scriptable.getNumScripts(); i++) {
			scriptable.getScript(i).accept(this);
		}
	}


	public void visitScript(Script script) throws VisitFailure {
		for (int i = 0; i < script.getBlocks().size(); i++) {
			script.getBlocks().get(i).accept(this);
		}
	}

	public void visitBlock(Block block) {
		System.out.println(block);
	}

}
