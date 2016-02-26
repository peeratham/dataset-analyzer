package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.analyzer.visitor.One;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class BroadVarScopeVisitor extends One implements AnalysisVisitor {

	public HashMap<String, HashSet<String>> globalVarRef = new HashMap<String, HashSet<String>>();
	List<String> varRelatedCommands = new ArrayList<String>();
	private AnalysisReport report = new AnalysisReport();

	public BroadVarScopeVisitor() {
		super(null);

		varRelatedCommands.add("setVar:to:");
		varRelatedCommands.add("changeVar:by:");
		varRelatedCommands.add("readVariable");
		varRelatedCommands.add("showVariable:");
		varRelatedCommands.add("hideVariable:");

		class BroadVarScopeVisitorHelper implements Visitor {

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {
				Scriptable stage = scratchProject.getScriptable("Stage");
				Map<String, Object> globals = stage.getAllVariables();
				for (String varName : globals.keySet()) {
					globalVarRef.put(varName, new HashSet<String>());
				}

			}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {

			}

			public void visitScript(Script script) throws VisitFailure {
				// TODO Auto-generated method stub

			}

			public void visitBlock(Block block) throws VisitFailure {
				if (varRelatedCommands.contains(block.getCommand())) {
					List<Object> parts = block.getBlockSpec().getParts();
					Iterator<Object> args = block.getArgs().iterator();
					for (int i = 0; i < parts.size(); i++) {
						
						if (parts.get(i) instanceof Insert) {
							Object arg = args.next();
							if (((Insert) parts.get(i)).getName().contains("var") && globalVarRef.containsKey(arg)) {
								globalVarRef.get(arg).add(block.getBlockPath().getPathList().get(0));
							}
							
						}
						

					}

				}
		

			}
		}

		visitor = new TopDown(new BroadVarScopeVisitorHelper());
	}


	public HashMap<String, HashSet<String>> getGlobalVarRef() {
		return globalVarRef;
	}

	public AnalysisReport getReport() {
		report.setTitle("Too Broad Variable Scope");
		for (String varName: globalVarRef.keySet()) {
			if(globalVarRef.get(varName).size()==1){
				report.addRecord(varName + globalVarRef.get(varName));
			}
			
		}
		return report;
	}

	

}
