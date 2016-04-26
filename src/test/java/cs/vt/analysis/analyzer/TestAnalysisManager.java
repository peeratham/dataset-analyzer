package cs.vt.analysis.analyzer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.parser.ParsingException;

public class TestAnalysisManager {
	private JSONParser jsonParser;
	private AnalysisManager blockAnalyzer;

	@Before
	public void setUp(){
		 jsonParser = new JSONParser();
		 blockAnalyzer = new AnalysisManager();
	}

	@Test
	public void testAnalysisManager() throws IOException, ParsingException, AnalysisException {
		InputStream in = Main.class.getClassLoader().getResource("example-dataset.json").openStream();
    	String[] lines = IOUtils.toString(in).split("\n");
    	for(String line: lines){
    		Long id = null;
    		try {
				JSONObject record = (JSONObject) jsonParser.parse(line.toString());
				id =  (Long) record.get("_id");
				 String src = record.get("src").toString();
				 System.out.println(id);
			     JSONObject report = blockAnalyzer.analyze(src);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
	}

}
