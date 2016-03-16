package cs.vt.analysis.analyzer.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDistanceMatrix {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddCoordinate() {
		DistanceMatrix matrix = new DistanceMatrix();
		matrix.add("Sprite1", new Coordinate(1, 0));
		matrix.add("Sprite2", new Coordinate(2, 0));
		String[] nodes = matrix.getNodes();
		System.out.println(Arrays.toString(nodes));
		assertEquals(2, nodes.length);
	}
	
	@Test
	public void testGetDistanceMatrix(){
		DistanceMatrix matrix = new DistanceMatrix();
		matrix.add("Sprite1", new Coordinate(1, 0));
		matrix.add("Sprite2", new Coordinate(2, 0));
		matrix.add("Sprite3", new Coordinate(3, 0));
		matrix.computeDistanceMatrix();
		matrix.printDistanceMatrix();
		double[][] expected = new double[][] {{0,1,2},{1,0,1},{2,1,0}};
		assertArrayEquals(expected, matrix.getDistanceMatrix());
	}
	

}
