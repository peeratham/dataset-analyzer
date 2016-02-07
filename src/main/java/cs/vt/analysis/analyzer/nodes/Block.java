package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;



public class Block implements Visitable, Cloneable {
	
	private String command;
	private ArrayList<Object> args;
	private BlockSpec blockSpec;
	private Block nextBlock;
	private Block previousBlock;
	private boolean hasNestedBlocks = false;
	private Block firstChild;
	private ArrayList<ArrayList<Block>> nestedGroup = new ArrayList<ArrayList<Block>>();
	private Object parent;

	public Object getParent() {
		return parent;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

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
			ArrayList<Object> args = (ArrayList<Object>) getArgs();
			result = stringify(this, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private String stringify(Block obj, ArrayList<Object> args) throws Exception {
		if(obj.command.equals("Position")){
			return "UNDEFINED";
		}
		
		
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
	
	public String getCompactString() {
		String result = "";
		result +=this.getCommand();
		if(this.command.equals("procDef")){
			result += this.getArgs();
		}
		if(this.command.equals("doIfElse")){
			result += "("+((ArrayList)(this.getArgs())).get(0)+")";
		}
		return result;
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
					success = lhs.args.get(i).equals(rhs.args.get(i));
					if(!success){
						return false;
					}
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

	public void setHasNestedBlocks(boolean b) {
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
	
	public void addNestedBlocks(Object arg) {
		
		nestedGroup.add((ArrayList<Block>) arg);
	}
	
	public List<ArrayList<Block>> getNestedGroup(){
		return nestedGroup;
	}

	public String getPath() {
		ArrayList<String> path = new ArrayList<String>();
		Block current = this;
		
		do {
			
			if (current.hasNestedBlocks) {
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
			
		return String.join("/", path);
	}

	public Block(Block b){
		this.args =  (ArrayList<Object>) b.args.clone();
		this.blockSpec = b.blockSpec;
		this.command = b.command;
		this.firstChild = b.firstChild;
		this.hasNestedBlocks = b.hasNestedBlocks;
		this.nestedGroup =  (ArrayList<ArrayList<Block>>) b.nestedGroup.clone();
		this.nextBlock = b.nextBlock;
		this.previousBlock = b.previousBlock;
		this.parent =  b.parent;
	}
	
	public Block copy(){
		return new Block(this);
	}
	
	public Block clone() {
            return copy();
    }

}
