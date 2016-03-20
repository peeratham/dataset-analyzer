package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Insert;

public class BlockType {
	private String category;
	private String shape;
	private String command;
	private String spec;
	private ArrayList<Object> defaults;
	private String flag;
	private ArrayList<Object> parts;



	public BlockType() {
		// TODO Auto-generated constructor stub
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public void setDefaults(ArrayList<Object> defaults) {
		this.defaults = defaults;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setParts(ArrayList<Object> parts) {
		this.parts = parts;
	}

	public BlockType(String category,String flag, String shape, String name, String spec, ArrayList<Object> defaults, List<Object> parts) {
		this.category = category;
		this.flag = flag;
		this.shape = shape;
		this.command = name;
		this.spec = spec;
		this.defaults = defaults;
		this.parts = parseToParts(spec);
	}

	public String getShape() {
		return shape;
	}

	public String getFlag() {
		return flag;
	}

	public String getSpec() {
		return spec;
	}
	
	public ArrayList<Object> getParts() {
		return parts;
	}
	
	public static BlockType parseCustomBlockSpec(JSONArray jsonCustomBlock){
		BlockType blockSpec = new BlockType();
		blockSpec.command = (String)jsonCustomBlock.get(0);
    	blockSpec.shape = CommandLoader.SHAPE_FLAGS.get("h");
    	blockSpec.spec = (String)jsonCustomBlock.get(1);
    	blockSpec.category = CommandLoader.CATEGORY_IDS.get(10);
    	blockSpec.defaults = (ArrayList<Object>) jsonCustomBlock.get(3);
    	blockSpec.parts = parseToParts(blockSpec.spec);
		return blockSpec;
		
	}
	
	

	@Override
	public String toString() {
		return "BlockSpec ["
				+ (category != null ? "category=" + category + ", " : "")
				+ (shape != null ? "shape=" + shape + ", " : "")
				+ (command != null ? "command=" + command + ", " : "")
				+ (spec != null ? "spec=" + spec + ", " : "")
				+ (defaults != null ? "defaults=" + defaults + ", " : "")
				+ (flag != null ? "flag=" + flag + ", " : "")
				+ (parts != null ? "parts=" + parts : "") + "]";
	}

	public List<Object> getDefaults() {
		return defaults;
	}

	public static ArrayList<Object> parseToParts(String spec) {
		String pattern = "(%.(?:\\.[A-z]+)?)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(spec);
	
		ArrayList<Object> result = new ArrayList<Object>();
		int end = 0;
		while(m.find()){
			result.add(spec.substring(end, m.start()));
			end = m.end();
			String typeSymbol = m.group();
			String inputType = CommandLoader.INSERT_SHAPES.get(typeSymbol);
			result.add(new Insert(typeSymbol, inputType));
		}
		if(end!=spec.length()){
			result.add(spec.substring(end,spec.length()));
		}
		return result;
	}

	public String getCategory() {
		return category;
	}
	
}


