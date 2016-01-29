package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;



public class Block implements Visitable {
	
	private String command;
	private ArrayList<Object> args;
	private BlockSpec blockSpec;
	private Block nextBlock;
	private Block previousBlock;
	private boolean hasNestedBlocks = false;
	private Block firstChild;
	private List<Block> nestedBlocks;

	public Block() {
	}

	public Block getNextBlock() {
		return nextBlock;
	}

	public void setNextBlock(Block nextBlock) {
		this.nextBlock = nextBlock;
	}

	public Block getPreviousBlock() {
		return previousBlock;
	}

	public void setPreviousBlock(Block previousBlock) {
		this.previousBlock = previousBlock;
	}

	public Block(String command, BlockSpec blockSpec, ArrayList<Object> args) {
		this.command = command;
		this.args = args;
		this.blockSpec = blockSpec;
	}

	

	public BlockSpec getBlockSpec() {
		return blockSpec;
	}

	@Override
	public String toString() {
		String result="";
		try {
			result = stringify(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private String stringify(Block obj) throws Exception {
		
		ArrayList<Object> args = (ArrayList<Object>) getArgs();
		if(args == null){
			args = (ArrayList<Object>) blockSpec.getDefaults();
		}
		ArrayList<String> argString = new ArrayList<String>();
		
		for(Object o : args){
				if(o instanceof String){
					argString.add("\""+o+"\"");
				}else if(o instanceof Number){
					argString.add(o+"");
				}else if(o instanceof Block){
					if(((Block) o).blockSpec !=null && ((Block) o).blockSpec.getShape().equals("boolean")){
						argString.add("("+o+")");
					}else{
						argString.add(o.toString());
					}
					
				}else if(o instanceof ArrayList){			
					String nested = "";
					for(Object el : (ArrayList<Object>)o){
						nested += "\n";
						nested += el.toString();
					}
					
					nested = nested.replace("\n", "\n    ");
					argString.add(nested);
					
				}else{
					if(o==null && (obj.getBlockSpec().getFlag().contains("c")|
							obj.getBlockSpec().getFlag().contains("e"))){
						String e = "\n"+String.valueOf(o);
						e = e.replace("\n", "\n    ");
						argString.add(e);
						
					}else{
						argString.add(String.valueOf(o));
					}
				}
		}
		
		String formattedString = "";
		try{
			List<Object> parts = null;
			try{
			parts = this.blockSpec.getParts();
			}catch(Exception e){
				System.err.println(this);;
			}
			ArrayList<String> formattedStringArray = new ArrayList<String>();
			for (int i = 0; i < parts.size(); i++) {
				if(parts.get(i) instanceof String){
					formattedStringArray.add((String)parts.get(i));
				}else if(parts.get(i) instanceof Insert){
					formattedStringArray.add(argString.remove(0));
				}
				
			}
			formattedString = String.join("", formattedStringArray);

		}catch(Exception e){
			System.err.println(obj);
			throw new Exception();
		}
		
		if(blockSpec.getFlag()!=null){
			if(blockSpec.getFlag().equals("e")||blockSpec.getFlag().contains("c")){
				formattedString += "\nend";
			}
		}
		
		
		return formattedString;
	}
	
	public boolean hasCommand(String command){
		return this.command.equals(command);
	}

	public String getCommand() {
		return command;
	}

	public Object getArgs() {
		return this.args;
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitBlock(this);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) {return false;}		
		if(obj==this){return true;}
		if (obj.getClass() != getClass()) {
		     return false;
		   }
		
		Block rhs = (Block) obj;
		Block lhs = this;
		
		if (!rhs.getCommand().equals(lhs.getCommand())){
			return false;
		}else {
			boolean success = true;
			List<Object> lhsArgs = (List<Object>) lhs.getArgs();
			List<Object> rhsArgs = (List<Object>) rhs.getArgs();
			if(lhsArgs.size()==rhsArgs.size()){
				for (int i = 0; i < lhsArgs.size(); i++) {
					success = lhs.args.get(i)==rhs.args.get(i);
				}
			}
			return success;
		}
		
		
		
		
	}

	public void setCommand(String command) {
		this.command = command;
		
	}

	public void setBlockSpec(BlockSpec blockSpec) {
		this.blockSpec = blockSpec;
		
	}

	public void setArgs(ArrayList<Object> args) {
		this.args = args;
		
	}

	public void hasNestedBlocks(boolean b) {
		this.hasNestedBlocks = b;
		
	}
	
	public boolean hasNestedBlocks() {
		return this.hasNestedBlocks;
		
	}

	public void setFirstChild(Block previous) {
		this.firstChild = previous;
	}
	
	public Block getFirstChild() {
		return this.firstChild;
	}

	public void setNestedBlocks(Object arg) {
		this.nestedBlocks = (List<Block>) arg;
		
	}
	
	public List<Block> getNestedBlocks(){
		return nestedBlocks;
	}

}
