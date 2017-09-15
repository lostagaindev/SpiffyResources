package lostagain.nl.spiffyresources.client.spiffycore;

//ok, this class is too complex to explain, sorry
public class Simple2DPoint {
	
	public int x;
	public int y;
	

	public Simple2DPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}


	@Override
	public String toString() {
		return x+","+y;
	}

}