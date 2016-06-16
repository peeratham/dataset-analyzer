package vt.cs.smells.visual;

import java.util.ArrayList;

import org.apache.commons.math3.ml.clustering.Clusterable;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;

public class Node implements Clusterable{
	public int x;
	public int y;
	String firstBlock;
	Block first;
	String message;
	int[] pos;
	int clusterIndex;
	private Block block;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Node(Script s) {
		pos = s.getPosition();
		x = pos[0];
		y = pos[1];
		firstBlock = s.getBlocks().get(0).toString();
		block = s.getBlocks().get(0);
		if (s.getBlocks().get(0).hasCommand("whenIReceive")) {
			this.message = s.getBlocks().get(0).arg("message");
		}
	}

	public Node(double d, double e) {
		x = new Double(d).intValue();
		y = new Double(e).intValue();
	}

	public Node(Block b1, int x, int y) {
		this.block = b1;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		String val = "";
		val += firstBlock;
		val += "("+this.x + "," + this.y+")";
		
		return val;
	}

	@Override
	public double[] getPoint() {
		double[] point = new double[2];
		point[0] = x;
		point[1] = y;
		return point;
	}

	public void setCluster(int clusterIndex) {
		this.clusterIndex = clusterIndex;
		
	}

	public NodeClass getNodeClass() {
		NodeClass nc = new NodeClass();
		nc.command = this.block.getCommand();
		if(nc.command.equals("whenIReceive")){
			nc.args = this.block.getArgs();
		}else{
			nc.args = new ArrayList();
		}
		
		return nc;
	}

}
