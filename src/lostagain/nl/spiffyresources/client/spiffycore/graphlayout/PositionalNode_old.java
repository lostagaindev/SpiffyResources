package lostagain.nl.spiffyresources.client.spiffycore.graphlayout;

public interface PositionalNode_old {


	//void setParent(PositionalNode parent); //if this is a child node, set its parent. This is needed to calculate layout in tree sibling lists correctly
	
	
	void setXY(double x,double y); //set final x 
	double getX();  //calculated final x 
	double getY();  //calculated final y 

	Object getContent();

	double getRadialRequiredspace();
	
	//void setRadialRequiredSpace(double radialrequiredspace);

	/**
	 * radial angle on the parent, excluding starting position
	 * In future this is probably better stored on the parent itself?
	 * Then again, any children of THIS will need this angle as there own starting angle so maybe not.
	 * 
	 * @param currentRadialAng
	 */
	void setCurrentRadialAng(double currentRadialAng);  //calculated angle (excluding starting angle)
	
	double getCurrentRadialAng();  //calculated angle (excluding starting angle)


	/**
	 * returns the angular space this should take up on a parent at a given distance 
	 * @param radius
	 */
	void calcRadialRequiredSpace(double radius);
	

	


}