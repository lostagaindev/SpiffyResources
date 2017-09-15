package lostagain.nl.spiffyresources.client.spiffycore.graphlayout;

import java.util.logging.Logger;


/**
 * simple wrapper that lets a object "(ie gwt widget) be positioned in a radial tree list.
 * 
 * @author darkflame
 *
 */
public class PositionableNodeWrapper_old implements PositionalNode_old {

	Logger Log = Logger.getLogger("SpiffyGWT.GraphLayout.PositionableNodeWrapper");
	Object positionthis; // [hastohave]
//	SpiffyRadialTreeSiblingList sublist; //[optional]

	private double radialrequiredspace = -1; 
	//required, but in future might need better calculation for this ;
	//a) draw a line from each corner to parents center point
	//b) get the angle between them
	//c) use this new angle as the minimum size for this childs radial spaceing
	//Note; requires parent for this to work
	double sizeX=-1; //required for above
	double sizeY=-1;
	//for now its just calculated to the maxspace, which is based on the diagonal size and doesnt change based on this nodes angular position 
	
	
	//calculated;
	double radialAng =0; //calculated angle (excluding starting angle)
	double x =0; //calculated x position
	double y =0; //calculated y position
	


	private SpiffyRadialTreeSiblingList parentNode;

	/**
	 * wraps any object to become a PositionalNode. You will need to supply the size manually so that (if a parent is specified)
	 * its needed angular space can be deduced from the corners
	 * 
	 * Note ; assumes object is rectangular
	 * Note2; needs to be deduced after x/y center set or changed as its angular space depends of them
	 * (however, this will cause issues if minimum radual space isnt set on the parent tree, as the total radial size of the trees siblings might
	 * reduce if any of the individual ones reduce and theres no increase in padding to make up for it)
	 * 
	 * @param positionthis
	 * @param sizeX
	 * @param sizeY
	 * @param spiffyRadialTreeSiblingList 
	 */
	
	public PositionableNodeWrapper_old(Object positionthis, double sizeX,double sizeY, SpiffyRadialTreeSiblingList parent) {
		super();
		this.positionthis = positionthis;
				
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.parentNode = parent;

		Log.info("size="+sizeX+","+sizeY);
	}

//	public PositionableNodeWrapper(Object positionthis, SpiffyRadialTreeSiblingList sublist) {
	//	super();
//		this.positionthis = positionthis;
		//this.sublist = sublist;
//	}
	
	public void setXY(double x,double y) {
		this.x = x;
		this.y = y;

		//calcRadialRequiredSpace(); //not used yet
	}
	

	/**
	 * wrongly assumes angle is 45 for now (which gives maxspace it would theoretically need)
	 * @param radiusAwayFromCenter
	 */
	public void calcRadialRequiredSpace(double radiusAwayFromCenter) {
		
		double parentX  = 0; //currently ignore our own position, so we ignore parents too
		double parentY  = 0;
		
		//change to real center values when known?
		//double cx = x;
		//double cy = y;

		//assume our center is at 45 for now 
		double ang = Math.toRadians(45);
		double cx = radiusAwayFromCenter *  Math.sin(ang);
		double cy = radiusAwayFromCenter  * Math.cos(ang);

		Log.info("cx="+cx+","+cy);
		
		//
		double ourLeft   = cx - (sizeX/2.0);
		double ourRight  = cx + (sizeX/2.0);
		double ourTop    = cy - (sizeY/2.0);
		double ourBottom = cy + (sizeY/2.0);
		
		
		//work out angles for each corner relative to parentX center 
		 double   xleft_parentRelative =  parentX + ourLeft;
		 double  xright_parentRelative =  parentX + ourRight;
		 double    xtop_parentRelative =  parentY + ourTop;
		 double xbottom_parentRelative =  parentY + ourBottom;

			Log.info("topleft="+xleft_parentRelative+","+xtop_parentRelative);
			Log.info("bottomright="+xright_parentRelative+","+xbottom_parentRelative);
				 
		//get angles between each corner and the ...umm..horizontal? arg. this is confusing
		double a1 = Math.atan2(xleft_parentRelative, xtop_parentRelative);
		double a2 = Math.atan2(xleft_parentRelative, xbottom_parentRelative);
		double a3 = Math.atan2(xright_parentRelative, xtop_parentRelative);
		double a4 = Math.atan2(xright_parentRelative, xbottom_parentRelative);
			
		//need to find min/max of above?
		double min = Math.min(Math.min(a1, a2),Math.min(a3,a4));
		double max = Math.max(Math.max(a1, a2),Math.max(a3,a4));
		
		Log.info("min ang="+min);
		Log.info("max ang="+max);
		double neededAng = Math.abs(max-min);
		
		Log.info("diff="+neededAng+" ( "+Math.toDegrees(neededAng)+")");
		
		
		setRadialRequiredSpace(neededAng);
		
		
}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

	public Object getContent() {
		return positionthis;
	}
	
	
	public void setCurrentRadialAng(double radialAng){
		this.radialAng=radialAng;
	}

	@Override
	public double getCurrentRadialAng() {
		return radialAng;
	}
	
	@Override
	public double getRadialRequiredspace() {
		//if sub SpiffyRadialTreeSiblingList we need to get it from there, else we use the objects 
		//if (sublist!=null){
		//	return sublist.getRadialrequiredspace();
		//}
		//
		return radialrequiredspace;
	}
	

	public void setRadialRequiredSpace(double radialrequiredspace) {
		this.radialrequiredspace = radialrequiredspace;
	}
	
	
	//no op
//	@Override
//	public void setParent(PositionalNode parent) {
//	}

	


	

}