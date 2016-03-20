package cs.vt.analysis.visual;

import javax.swing.JFrame;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;


public class Clustering {
	public static void main(String[] args){
//		String[] names = new String[] { "O1", "O2", "O3", "O4", "O5", "O6" };
//		double[][] distances = new double[][] { 
//		    { 0, 1, 9, 7, 11, 14 },
//		    { 1, 0, 4, 3, 8, 10 }, 
//		    { 9, 4, 0, 9, 2, 8 },
//		    { 7, 3, 9, 0, 6, 13 }, 
//		    { 11, 8, 2, 6, 0, 10 },
//		    { 14, 10, 8, 13, 10, 0 }};
		String[] names = new String[] { "O1", "O2", "O3"};
		double[][] distances = new double[][] { 
		    { 0, 1, 5},
		    { 1, 0, 10},
		    { 5, 10, 0}};

		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(distances, names,
		    new AverageLinkageStrategy());
		
		cluster.getChildren();
		cluster.toConsole(1);
		for(Cluster c: cluster.getChildren()){
			
		}
		
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
		
		JFrame frame = new JFrame("JFrame Example");
		frame.add(dp);
		frame.setVisible(true);


	}
}
