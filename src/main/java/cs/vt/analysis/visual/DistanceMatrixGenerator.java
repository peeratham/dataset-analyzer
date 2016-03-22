package cs.vt.analysis.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import cs.vt.analysis.analyzer.analysis.Coordinate;

public class DistanceMatrixGenerator {
	public HashMap<String, Coordinate> coords;
	public static ArrayList<ScriptProperty> propList;
	public double[][] matrix;

	public DistanceMatrixGenerator() {
		coords = new HashMap<String, Coordinate>();
		propList = new ArrayList<ScriptProperty>();
	}

	public void add(String scriptID, ScriptProperty property) {
		propList.add(property);
	}
	
	public double[][] computeDistanceMatrix(DistanceMeasure measure) {
		matrix = new double[propList.size()][propList.size()];
		
		for(int c = 0; c< propList.size(); c++){
			ScriptProperty c1 = propList.get(c);
			for (int r = 0; r < propList.size(); r++) {
				ScriptProperty c2 = propList.get(r);
				matrix[c][r] = measure.getDist(c1, c2);
			}
		}
		
		return matrix;
	}
	
	public static void printDistanceMatrix(double[][] matrix) {
		System.out.println(Arrays.toString(getHeader()));
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.printf(String.format("%.1f ", matrix[i][j])+", ");
			}
			System.out.println();
		}
	}
	
	public static String[] getHeader(){
		String[] header = new String[propList.size()];
		for(int i =0; i< header.length; i++){
			header[i] = i+""+propList.get(i).getCoordinate();
		}
		return header;
	}
	
	public static void computeClustering(double[][] matrix) {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(matrix, getHeader(),
		    new AverageLinkageStrategy());
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
		
		JFrame frame = new JFrame("Clustering");
		frame.add(dp);
		frame.setVisible(true);
	}
	


	

}
