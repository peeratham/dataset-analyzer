package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

public class DistanceMatrix {
	public HashMap<String, Coordinate> coords;
	public double[][] matrix;

	public DistanceMatrix() {
		coords = new HashMap<String, Coordinate>();
	}

	public void add(String scriptableName, Coordinate coordinate) {
		coords.put(scriptableName, coordinate);
	}

	public String[] getNodes() {
		return coords.keySet().toArray(new String[coords.keySet().size()]);
	}

	public void computeDistanceMatrix() {
		matrix = new double[coords.size()][coords.size()];
		String[] nodes = getNodes();
		for (int c = 0; c < nodes.length; c++) {
			Coordinate c1 = coords.get(nodes[c]);
			for (int r = 0; r < nodes.length; r++) {
				Coordinate c2 = coords.get(nodes[r]);
				matrix[c][r] = Coordinate.dist(c1, c2);
			}
		}

	}

	public double[][] getDistanceMatrix() {
		if (matrix == null) {
			computeDistanceMatrix();
		}
		return matrix;
	}

	public void printDistanceMatrix() {
		if (matrix == null) {
			computeDistanceMatrix();
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.printf(String.format("%.1f ", matrix[i][j]));
			}
			System.out.println();
		}
	}

	public void computeClustering() {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(matrix, getNodes(),
		    new AverageLinkageStrategy());
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
		
		JFrame frame = new JFrame("Clustering");
		frame.add(dp);
		frame.setVisible(true);
		
		
	}

}
