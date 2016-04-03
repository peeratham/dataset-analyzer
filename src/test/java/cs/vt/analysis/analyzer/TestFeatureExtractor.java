package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;

public class TestFeatureExtractor {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Ignore
	@Test
	public void test() throws IOException, ParseException, ParsingException {
		Extractor extractor = new DistanceFeatureExtractor();
		String OUTPUT_DIR = "C:\\Users\\Peeratham\\workspace\\feature-selection-dataset";
		String INPUT_DIR = "C:\\Users\\Peeratham\\workspace\\scratch-dataset";
		File inputDirPath = new File(INPUT_DIR);
		File baseOutputPath = new File(OUTPUT_DIR, "");
		
		ArrayList<String> dataset = new ArrayList<String>(Arrays.asList(
				"78704342"));
		for (String projectID : dataset) {
			File projectPath = new File(inputDirPath, projectID+".json");
			String projectSrc = FileUtils.readFileToString(projectPath);
			ScratchProject project = ScratchProject.loadProject(projectSrc);
			extractor.extract(project);
		}
		
		String result = extractor.generateCSVOutput();
		File outputPath = new File(baseOutputPath,"out.csv");
		FileUtils.writeStringToFile(outputPath , result);
	}
	
	

}
