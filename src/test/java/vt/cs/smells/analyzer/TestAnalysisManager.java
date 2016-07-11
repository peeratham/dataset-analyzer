package vt.cs.smells.analyzer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Main;
import vt.cs.smells.analyzer.parser.ParsingException;

public class TestAnalysisManager {
	private JSONParser jsonParser;
	private AnalysisManager blockAnalyzer;

	@Before
	public void setUp(){
		 jsonParser = new JSONParser();
		 blockAnalyzer = new AnalysisManager();
	}
	
	@Ignore
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
				 blockAnalyzer.analyze(src);
				 JSONObject report = blockAnalyzer.getFullJSONReport();
//			     System.out.println(report.toJSONString());
//			     System.out.println(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
	}

}
