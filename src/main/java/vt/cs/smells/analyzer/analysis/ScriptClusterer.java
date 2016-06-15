package vt.cs.smells.analyzer.analysis;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.math3.ml.clustering.Clusterable;

import de.lmu.ifi.dbs.elki.algorithm.clustering.optics.OPTICSList;
import de.lmu.ifi.dbs.elki.algorithm.clustering.optics.OPTICSXi;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.OPTICSModel;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import vt.cs.smells.visual.Node;

public class ScriptClusterer<T extends Clusterable> {

	public ScriptClusterer() {
	}

	public final static Color[] colors = { Color.red, Color.green, Color.blue, Color.orange, Color.pink, Color.magenta,
			Color.gray, Color.black, };

	static Transformer<Node, Point2D> vertexToPoint2D = new Transformer<Node, Point2D>() {
		public Point2D transform(Node n) {
			Point2D p = new Point2D.Double(n.x, n.y);
			return p;
		}
	};

	public List<List<Node>> performClustering(List<Node> nodes, double d) {
		List<List<Node>> clusters = cluster(nodes, d);
		updateClusterIndex(clusters);
		return clusters;
	}

	public void updateClusterIndex(List<List<Node>> clusters) {
		for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
			List<Node> nodesInCluster = clusters.get(clusterIndex);
			for (Node n : nodesInCluster) {
				n.setCluster(clusterIndex);
			}
		}
	}

	public List<List<Node>> cluster(List<Node> nodes, double xi_id) {
		double[][] data = new double[nodes.size()][2];
		for (int ni = 0; ni < nodes.size(); ni++) {
			data[ni][0] = nodes.get(ni).x;
			data[ni][1] = nodes.get(ni).y;
		}

		DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
		Database db = new StaticArrayDatabase(dbc, null);
		db.initialize();

		ListParameterization params = new ListParameterization();
		params.addParameter(OPTICSList.Parameterizer.MINPTS_ID, 2);
		params.addParameter(OPTICSXi.Parameterizer.XI_ID, xi_id);
		params.addParameter(OPTICSXi.Parameterizer.XIALG_ID, OPTICSList.class);
		OPTICSXi opticsxi = ClassGenericsUtil.parameterizeOrAbort(OPTICSXi.class, params);

		Clustering<OPTICSModel> c = opticsxi.run(db);
		Relation<NumberVector> rel = null;
		List<List<Node>> resultClusters = new ArrayList<>();
		try{
			 rel = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
		}catch(Exception e){
			e.printStackTrace();
			return resultClusters;
		}
		
		DBIDRange ids = (DBIDRange) rel.getDBIDs();

		
		for (de.lmu.ifi.dbs.elki.data.Cluster<?> cluster : c.getAllClusters()) {
			if (!cluster.isNoise()) {
				ArrayList<Node> aCluster = new ArrayList<Node>();
				for (DBIDIter iter = cluster.getIDs().iter(); iter.valid(); iter.advance()) {
					final int offset = ids.getOffset(iter);
					aCluster.add(nodes.get(offset));
				}
				resultClusters.add(aCluster);
			}
		}
		return resultClusters;
	}
}
