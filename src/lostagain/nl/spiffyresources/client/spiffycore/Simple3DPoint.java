package lostagain.nl.spiffyresources.client.spiffycore;

public class Simple3DPoint {
	
	public int x;
	public int y;
	public int z;
	

	public Simple3DPoint(int x, int y,int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Parses a comma separated list of points and stores them as ints
	 * Note, we convert to int by "(int)Math.round(Double.parseDouble(cz))"
	 * 
	 * @param commaSeperatedCoOrdinates
	 */
	public Simple3DPoint(String commaSeperatedCoOrdinates) {
		
		String cparray[] = commaSeperatedCoOrdinates.split(","); //split co-ordinates by comma

		// get the new x/y and optional z 
		String cx  = cparray[0];
		String cy  = cparray[1];	
		
		//Double.parseDouble
		//x = (int) Integer.parseInt(cx);

		x = (int)Math.round(Double.parseDouble(cx));
		//y = (int) Integer.parseInt(cy);
		y = (int)Math.round(Double.parseDouble(cy));
		if (cparray.length==3){ //optional z
			String cz  = cparray[2];
			
			//z = (int) Integer.parseInt(cz);	
			z = (int)Math.round(Double.parseDouble(cz));
			
		} else {
			z=0;
		}
			
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Simple3DPoint other = (Simple3DPoint) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		
		return true;
	}



	@Override
	public String toString() {
		return x+","+y+","+z;
	}


	public Simple3DPoint copy() {
		return new Simple3DPoint(x,y,z);
	}

	
	public void set(Simple3DPoint settothis) {
		x=settothis.x;
		y=settothis.y;
		z=settothis.z;
		
	}

	public void set(int nx, int ny, int nz) {
		
		x=nx;
		y=ny;
		z=nz;
	
	}

}