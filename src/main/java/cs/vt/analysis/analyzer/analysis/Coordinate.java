package cs.vt.analysis.analyzer.analysis;

public class Coordinate {
	double x;
	double y;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static double dist(Coordinate c1, Coordinate c2){

		return Math.sqrt((c1.x-c2.x)*(c1.x-c2.x) + (c1.y-c2.y)*(c1.y-c2.y));
	}

	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + "]";
	}

}
