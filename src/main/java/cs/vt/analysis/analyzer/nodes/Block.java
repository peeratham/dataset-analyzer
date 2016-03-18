package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.analysis.AnalysisUtil;
import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class Block implements Visitable, Cloneable {

	protected String command;
	protected BlockSpec blockType;
	protected ArrayList<Object> args;
	protected Object parent;

	public ArrayList<Object> getParts() {
		return parts;
	}

	public String getSpec() {
		return spec;
	}

	protected Block previousBlock;
	protected Block nextBlock;
	protected ArrayList<ArrayList<Block>> nestedGroup = new ArrayList<ArrayList<Block>>();
	protected boolean hasNestedBlocks = false;

	protected String shape;
	protected ArrayList<Object> parts;
	private String spec;
	private String flag;
	private List<Object> defaults;
	private String category;

	public Object getParent() {
		return parent;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

	public Block() {
	}

	public Block(String command, BlockSpec blockSpec, ArrayList<Object> args) {
		this.command = command;
		this.args = args;
		this.blockType = blockSpec;
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

	public BlockSpec getBlockType() {
		return blockType;
	}

	@Override
	public String toString() {
		String result = "";
		try {
			ArrayList<Object> args = (ArrayList<Object>) getArgs();
			result = stringify(this, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String stringify(Block obj, ArrayList<Object> args)
			throws Exception {
		if (obj.command.equals("Position")) {
			return "UNDEFINED";
		}

		if (args == null || args.isEmpty()) {
			args = (ArrayList<Object>) blockType.getDefaults();	//usually for procDef
		}
		ArrayList<String> argString = new ArrayList<String>();

		for (Object o : args) {
			if (o instanceof String) {
				argString.add("\"" + o + "\"");
			} else if (o instanceof Number) {
				argString.add(o + "");
			} else if (o instanceof Block) {
				if (((Block) o).blockType != null
						&& ((Block) o).blockType.getShape().equals("boolean")) {
					argString.add("(" + o + ")");
				} else {
					argString.add(o.toString());
				}

			} else if (o instanceof ArrayList) {
				String nested = "";
				for (Object el : (ArrayList<Object>) o) {
					nested += "\n";
					nested += el.toString();
				}

				nested = nested.replace("\n", "\n    ");
				argString.add(nested);

			} else {
				if (o == null
						&& (obj.getBlockType().getFlag().contains("c") | obj
								.getBlockType().getFlag().contains("e"))) {
					String e = "\n" + String.valueOf(o);
					e = e.replace("\n", "\n    ");
					argString.add(e);

				} else {
					argString.add(String.valueOf(o));
				}
			}
		}

		String formattedString = "";
		try {
			List<Object> parts = null;

			try {
				parts = this.blockType.getParts();
			} catch (Exception e) {
				System.err.println(this);
			}
			ArrayList<String> formattedStringArray = new ArrayList<String>();
			for (int i = 0; i < parts.size(); i++) {
				if (parts.get(i) instanceof String) {
					formattedStringArray.add((String) parts.get(i));
				} else if (parts.get(i) instanceof Insert) {
					formattedStringArray.add(argString.remove(0));
				}

			}
			formattedString = String.join("", formattedStringArray);

		} catch (Exception e) {
			System.err.println(obj);
			throw new Exception(e);
		}

		if (blockType.getFlag() != null) {
			if (blockType.getFlag().equals("e")
					|| blockType.getFlag().contains("c")) {
				formattedString += "\nend";
			}
		}

		return formattedString;
	}

	public String getCompactString() {
		String result = "";
		result += this.getCommand();
		if (this.command.equals("procDef")) {
			result += this.getArgs();
		}
		if (this.command.equals("doIfElse")) {
			result += "(" + ((ArrayList) (this.getArgs())).get(0) + ")";
		}
		return result;
	}

	public boolean hasCommand(String command) {
		return this.command.equals(command);
	}

	public String getCommand() {
		return command;
	}

	public List<Object> getArgs() {
		return this.args;
	}

	public void setArgs(ArrayList<Object> args) {
		this.args = args;

	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitBlock(this);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		Block rhs = (Block) obj;
		Block lhs = this;

		if (!rhs.getCommand().equals(lhs.getCommand())) {
			return false;
		}

		boolean success = true;
		List<Object> lhsArgs = (List<Object>) lhs.getArgs();
		List<Object> rhsArgs = (List<Object>) rhs.getArgs();

		if (lhsArgs.size() != rhsArgs.size()) {
			return false;
		}

		for (int i = 0; i < lhsArgs.size(); i++) {
			if (lhsArgs.get(i).getClass() != rhsArgs.get(i).getClass()) {
				return false;
			}

			if (lhsArgs.get(i) instanceof ArrayList) { // nested block
				ArrayList nestedLHS = (ArrayList) lhsArgs.get(i);
				ArrayList nestedRHS = (ArrayList) rhsArgs.get(i);
				for (int j = 0; j < nestedLHS.size(); j++) {
					success = nestedLHS.get(j).equals(nestedRHS.get(j));
					if (!success) {
						return false;
					}
				}
			} else {
				success = lhs.args.get(i).equals(rhs.args.get(i));
				if (!success) {
					return false;
				}
			}
		}
		return success;
	}

	public void setCommand(String command) {
		this.command = command;

	}

	public void setBlockType(BlockSpec blockSpec) {
		this.blockType = blockSpec;

	}

	public boolean hasNestedBlocks() {
		return !nestedGroup.isEmpty();

	}

	public void addNestedBlocks(Object arg) {
		nestedGroup.add((ArrayList<Block>) arg);
	}

	public List<ArrayList<Block>> getNestedGroup() {
		return nestedGroup;
	}

	public BlockPath getBlockPath() {
		return new BlockPath(this);
	}

	public Block(Block b) {
		this.args = (ArrayList<Object>) b.args.clone();
		this.blockType = b.blockType;
		this.command = b.command;
		this.hasNestedBlocks = b.hasNestedBlocks;
		this.nestedGroup = (ArrayList<ArrayList<Block>>) b.nestedGroup.clone();
		this.nextBlock = b.nextBlock;
		this.previousBlock = b.previousBlock;
		this.parent = b.parent;
	}

	public Block copy() {
		return new Block(this);
	}

	public Block clone() {
		return copy();
	}

	public boolean commandMatches(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		Block rhs = (Block) obj;
		Block lhs = this;

		if (!rhs.getCommand().equals(lhs.getCommand())) {
			return false;
		}

		boolean success = true;
		List<Object> lhsArgs = (List<Object>) lhs.getArgs();
		List<Object> rhsArgs = (List<Object>) rhs.getArgs();

		if (lhsArgs.size() != rhsArgs.size()) {
			return false;
		}

		for (int i = 0; i < lhsArgs.size(); i++) {
			if (lhsArgs.get(i).getClass() != rhsArgs.get(i).getClass()) {
				return false;
			}

			if (lhsArgs.get(i) instanceof ArrayList) { // nested block
				ArrayList nestedLHS = (ArrayList) lhsArgs.get(i);
				ArrayList nestedRHS = (ArrayList) rhsArgs.get(i);

				for (int j = 0; j < nestedLHS.size(); j++) {
					success = ((Block) nestedLHS.get(j))
							.commandMatches(nestedRHS.get(j));
					if (!success) {
						return false;
					}
				}
			}
			if (lhsArgs.get(i) instanceof Block) {

				success = ((Block) lhsArgs.get(i)).commandMatches(rhsArgs
						.get(i));
				if (!success) {
					return false;
				}
			}
			// don't consider other other kind of objects
		}
		return success;
	}

	public ArrayList<Block> containsBlock(String blockCommand) {
		AnalysisUtil finder = new AnalysisUtil();
		return finder.findBlock(this, blockCommand);
	}

	public void setSpec(String spec) {
		this.spec = spec;

	}

	public void setParts(ArrayList<Object> parts) {
		this.parts = parts;

	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void setFlag(String flag) {
		this.flag = flag;

	}

	public void setDefaults(List<Object> defaults) {
		this.defaults = defaults;

	}

	public void setCategory(String category) {
		this.category = category;

	}

	
	/*
	 *  create BlockType = [define %s] where arg = CustomBlockAsSignature e.g. [Assign Length # Unit]  
	 */
	public static Block makeCustomBlock(JSONArray blockArray) {
		Block defineBlock = new Block();
		defineBlock.args = new ArrayList<Object>();
		defineBlock.command = (String) blockArray.get(0);
		defineBlock.blockType = CommandLoader.COMMAND_TO_BLOCKSPEC.get(defineBlock.command);
		defineBlock.parts = defineBlock.blockType.getParts();
		
		CustomBlock customBlockArg = new CustomBlock();
		BlockSpec customBlockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC
				.get((String) blockArray.get(1));
		customBlockArg.setCommand(""); //to delete
		customBlockArg.setBlockType(customBlockSpec);
		customBlockArg.setParent(defineBlock);
		customBlockArg.setArgs(new ArrayList<Object>()); //empty args to get defaults
		defineBlock.args.add(customBlockArg);
		return defineBlock;
		
	}

}
