package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Insert;

public class BlockSpec {
	private String category;
	private String shape;
	private String command;
	private String spec;
	private List<Object> defaults;
	private String flag;
	private List<Object> parts;



	public BlockSpec() {
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

	public void setDefaults(List<Object> defaults) {
		this.defaults = defaults;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setParts(List<Object> parts) {
		this.parts = parts;
	}

	public BlockSpec(String category,String flag, String shape, String name, String spec, List<Object> defaults, List<Object> parts) {
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
	
	public List<Object> getParts() {
		return parts;
	}
	
	public static BlockSpec parseCustomBlockSpec(JSONArray jsonCustomBlock){
		BlockSpec blockSpec = new BlockSpec();
		blockSpec.command = (String)jsonCustomBlock.get(0);
    	blockSpec.shape = CommandLoader.SHAPE_FLAGS.get("h");
    	blockSpec.spec = (String)jsonCustomBlock.get(1);
    	blockSpec.category = CommandLoader.CATEGORY_IDS.get(10);
    	blockSpec.defaults = (List<Object>) jsonCustomBlock.get(3);
    	blockSpec.parts = parseToParts(blockSpec.spec);
		return blockSpec;
		
	}
	
	

	@Override
	public String toString() {
		return "BlockSpec [category=" + category + ", shape=" + shape
				+ ", spec=" + spec + "]";
	}

	public List<Object> getDefaults() {
		return defaults;
	}

	public static List<Object> parseToParts(String spec) {
		String pattern = "(%.(?:\\.[A-z]+)?)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(spec);
	
		ArrayList<Object> result = new ArrayList<Object>();
		int end = 0;
		while(m.find()){
			result.add(spec.substring(end, m.start()));
			end = m.end();
			result.add(new Insert(m.group()));
		}
		if(end!=spec.length()){
			result.add(spec.substring(end,spec.length()));
		}
		return result;
	}
}
