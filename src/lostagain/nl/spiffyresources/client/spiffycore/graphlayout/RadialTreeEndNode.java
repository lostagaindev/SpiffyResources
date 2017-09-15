package lostagain.nl.spiffyresources.client.spiffycore.graphlayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.collect.Iterators;
import com.google.gwt.libideas.logging.shared.Log;

public class RadialTreeEndNode implements Iterable<RadialTreeEndNode>  {

	Logger Log = Logger.getLogger("SpiffyGWT.GraphLayout.RadialTreeEndNode");
	public SpiffyRadialTreeSiblingList getParentNode() {
		return parentNode;
	}

	public void setParentNode(SpiffyRadialTreeSiblingList parentNode) {
		this.parentNode = parentNode;
	}



	protected SpiffyRadialTreeSiblingList parentNode;
	protected Object centerObject;
	protected String name = "[no name]";
	protected double centerX = -1;
	protected double centerY = -1;
	/**
	 * the required radial space needed on its parent (radians)
	 */
	protected double RequiredSpaceOnParent = -1;
	double radialAng = 0;
	

	public RadialTreeEndNode() {
		super();
	}

	/**
	 * 
	 * This constructor should be used for all but the middle/first sibling list
	 * 
	 * @param parent - the parent node we connect to
	 */
	public RadialTreeEndNode(String name,  Object centerObject) {
		super();		
//		this.parentNode = parent;				
		this.name=name;
		
	//	centerX = parentNode.getX();
	//	centerY = parentNode.getY();

		this.centerObject = centerObject;
			//parent.add(this);//add ourselves to the parent if its a tree
	}

	public double getX() {
		
		
		if (parentNode!=null){
			return parentNode.getChildX(this);			
		}
		
		return centerX;
	}

	public double getY() {
		if (parentNode!=null){
			return parentNode.getChildY(this);			
		}
		return centerY;
	}

	/**
	 * note; will return the center, but not sub-objects. You need to loop over them separately
	 */
	public Object getContent() {
		return centerObject;
	}
	

	private int height=0;
	private int width=0;
	
	/**
	 * As visual size of the content of this node.
	 * (ie, the pixel size of the object supplied into the constructor - as this class is agnostic to visual implementations
	 * you need to tell it the size manually here in order to help layout avoid overlaps) 
	 */
	public void setContentsSize(int offsetWidth, int offsetHeight) {
		this.width = offsetWidth;
		this.height = offsetHeight;
		
		
	}
	public double getRadialRequiredspace() {		
		return RequiredSpaceOnParent;
	}


	public void setCurrentRadialAng(double currentRadialAng) {
		radialAng = currentRadialAng;
	}

	public double getCurrentRadialAng() {
		return radialAng;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((centerObject == null) ? 0 : centerObject.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpiffyRadialTreeSiblingList other = (SpiffyRadialTreeSiblingList) obj;
		if (centerObject == null) {
			if (other.centerObject != null)
				return false;
		} else if (!centerObject.equals(other.centerObject))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	/**
	 * if placed at a certain radius, how much ang space do we need at the hub?
	 * The size needs to be set for this to work, as well as the correct current radial angle
	 * 
	 * @param radius
	 */
	public void calcRadialRequiredSpace(float radius) {

		//todo;
		//if parent position is set use real location, else assume biggest (45 degree)
		double x =  this.getParentNode().getChildDisplacementX(this);
		double y =  this.getParentNode().getChildDisplacementY(this);
		//-----------
//		if ( x == -1 || y == -1 ){
			double assumeAng = Math.toRadians(45); 
			//assume 45 degrees at radius
			x = radius * Math.sin(assumeAng);
			y = radius * Math.cos(assumeAng);
					
	//	}
		
			//if we are assuming a fixed max size, we could do this all more simply I think.
			//Isnt it just the diagonal length of the node "wrapped around" the circumfrence?
			//C=2 PI R
		    //arc length	=	R	C
		
		Log.info("our "+this.getName()+" centre:"+x+","+y);
		
		//side positions (relative to parent)
		double x_left =   x - width/2.0; //always seem to be the same ????
		double x_right =  x + width/2.0;
		double y_top =    y - height/2.0;
		double y_bottom = y + height/2.0;

		Log.info("x/y :"+x_left+","+y_top);
		Log.info("x/y::"+x_right+","+y_bottom);
		
		
		//find angle relative to hub at specified radius
		//topleft;
		double ang   = Math.atan2(x_left, y_top);
		//bottomleft
		double ang2  = Math.atan2(x_left, y_bottom);
		//topright;
		double ang3  = Math.atan2(x_right, y_top);
		//bottomright;
		double ang4  = Math.atan2(x_right, y_bottom);
			
		//find min and max angle
		//(is there a better way?)
		double min = Math.min(
					   Math.min(ang, ang2), 
				       Math.min(ang3, ang4)
				     );
		
		double max = Math.max(
				       Math.max(ang, ang2), 
			           Math.max(ang3, ang4)
		 	         );
		
		
		double width = Math.abs(max-min);
	
		Log.info("angle width:"+Math.toDegrees(width));
		
		RequiredSpaceOnParent = width;
		//todo: corners of object projected back to a point radius away, then angle between points
		
	//	RequiredSpaceOnParent = Math.toRadians(20); //temp
	}

	
	@Override
	public Iterator<RadialTreeEndNode> iterator() {
	    return Iterators.singletonIterator(this);
	}

	

}