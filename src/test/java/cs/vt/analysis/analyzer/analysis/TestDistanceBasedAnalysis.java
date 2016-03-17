package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Util;

public class TestDistanceBasedAnalysis {

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(101548138);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void getAllScriptLocationsForASprite(){
		Scriptable sprite1 = project.getScriptable("Sprite1");
		ArrayList<Coordinate> coordList = DistanceBasedAnalyzer.getLocationOfScriptsForScriptable(sprite1);
		System.out.println(coordList);
	}
	
	@Test
	public void getSetOfVariablesInAScript(){
		
	}
	
	@Test
	public void getSetOfMethodUsedInAScript(){
		
	}

	@Test
	public void extractDictionaryOfScriptProperties() throws AnalysisException {
		DistanceBasedAnalyzer analyzer = new DistanceBasedAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
//		try{Thread.sleep(10000);}catch(InterruptedException e){}
	}
	
	
	@Test
	public void testIfDistanceIsValidBasedOnVariablesAndMethods(){
		
	}
	
	@Test
	public void getDistanceMetrixForAScriptable(){
		
	}
	
}
