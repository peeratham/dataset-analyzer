package vt.cs.smells.select;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Visitable;

public abstract class Evaluator {
	private Evaluator(){}
	public abstract boolean matches(Block element);
	
	public static final class  BlockCommand extends Evaluator {
		private String command;
		public BlockCommand(String command){
			this.command = command;
		}
		@Override
		public boolean matches(Block element) {
			return (element.getCommand().equals(command));
		}
		
	}
	
	public static final class  AnyBlock extends Evaluator {
		public AnyBlock(){
		}
		@Override
		public boolean matches(Block element) {
			return true;
		}
		
	}
}
