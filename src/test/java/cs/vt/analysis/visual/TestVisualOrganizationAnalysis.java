package cs.vt.analysis.visual;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.analysis.Coordinate;
import cs.vt.analysis.analyzer.analysis.VisualOrganizationAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Util;

public class TestVisualOrganizationAnalysis {

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
	public void getSetOfVariablesInAScript(){
		
	}
	
	@Test
	public void getSetOfMethodUsedInAScript(){
		
	}

	@Test
	public void extractDictionaryOfScriptProperties() throws AnalysisException {
		VisualOrganizationAnalyzer analyzer = new VisualOrganizationAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
//		try{Thread.sleep(10000);}catch(InterruptedException e){}
	}
	
	
	@Test
	public void testSharedVariableBasedDistance(){
		Scriptable s = project.getScriptable("sharedVarBasedDistance");
		double[][] matrix = VisualOrganizationAnalyzer.buildDistanceMatrix(s, new DistanceMeasure.SharedVariableBased());
		double[][] userMatrix = VisualOrganizationAnalyzer.buildDistanceMatrix(s, new DistanceMeasure.CoordinateBased());
		DistanceMatrixGenerator.printDistanceMatrix(matrix);
		DistanceMatrixGenerator.printDistanceMatrix(userMatrix);
		DistanceMatrixGenerator.computeClustering(matrix);
	}
	
	@Test
	public void testCorrelation(){
		Scriptable s = project.getScriptable("correlationWithSharedVar");
		double[][] matrix = VisualOrganizationAnalyzer.buildDistanceMatrix(s, new DistanceMeasure.SharedVariableBased());
		double[][] userMatrix = VisualOrganizationAnalyzer.buildDistanceMatrix(s, new DistanceMeasure.CoordinateBased());
		DistanceMatrixGenerator.printDistanceMatrix(matrix);
		DistanceMatrixGenerator.printDistanceMatrix(userMatrix);
	}
}
