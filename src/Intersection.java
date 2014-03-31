

public class Intersection {
	int NScars, EWcars, x, y;
	Direction NSDir, EWDir;
	public enum Direction{N, S, E, W}
	
	public Intersection(int x, int y, int NScars, int EWcars, Direction NSDir, Direction EWDir){
		this.x = x;
		this.y = y;
		this.NScars = NScars;
		this.EWcars = EWcars;
		this.NSDir = NSDir;
		this.EWDir =  EWDir ;
	}
}
