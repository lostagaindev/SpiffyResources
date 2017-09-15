package lostagain.nl.spiffyresources.client.spiffycore.graphlayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SpiffyRadialTreeSiblingList extends RadialTreeEndNode implements  Iterable<RadialTreeEndNode> {

	Logger Log = Logger.getLogger("SpiffyGWT.GraphLayout.SpiffyRadialTreeSiblingList");


	private HashMap<RadialTreeEndNode,NodePoint> childLocations = Maps.newHashMap();
	private ArrayList<RadialTreeEndNode> children = Lists.newArrayList();

	private double minDeziredRadialWidth =Math.toRadians(10f);
	private double requiredradialWidthWithoutPadding =0; // (calculated from all subwidths, what the width would be without padding)
	private double radialpaddingPerNode = 0;





	/**
	 * 
	 * Parent supply's center x/y for tree, as well as the object to visualize at middle.
	 * 
	 * @param centerX
	 * @param centerY
	 */
	public SpiffyRadialTreeSiblingList(String name,float centerX, float centerY, Object centerObject) {
		super();

		this.centerX = centerX;
		this.centerY = centerY;
		this.centerObject = centerObject;
		this.name=name;

	}




	public SpiffyRadialTreeSiblingList(String name, Object centerObject) {
		super(name, centerObject);
	}




	//
	/**'
	 * has to be worked out after padding
	 * used to centralize elements relative to starting angle. T
	 * This does NOT give the space needed by this total tree to look nice fitting into ITs parent
	 * @return
	 */
	public double getTotalRadialWidthrelativeToCenter(){ //relative to its center point

		if (minDeziredRadialWidth>-1 & minDeziredRadialWidth>requiredradialWidthWithoutPadding){
			return minDeziredRadialWidth;
		} else {
			return requiredradialWidthWithoutPadding;
		}


	}

	public double getRadialNodePadding(){
		return radialpaddingPerNode;
	}

	private void recalcminRequiredwidth(){
		//add all subwidths (including sub-subwidths)

		//1. recalc min required width		
		double total =0;		//radians

		for (RadialTreeEndNode node : children) {

			node.calcRadialRequiredSpace(this.radius);

			total=total+node.getRadialRequiredspace();			
		}

		requiredradialWidthWithoutPadding = total;	

		//2. (work out spare)
		if (minDeziredRadialWidth>requiredradialWidthWithoutPadding){

			double spare = minDeziredRadialWidth-requiredradialWidthWithoutPadding;

			Log.info("spare radians:"+spare +"("+minDeziredRadialWidth+"-"+requiredradialWidthWithoutPadding+")");

			//3. (and devide evenly to get padding per node)

			radialpaddingPerNode = spare/childLocations.keySet().size();
			Log.info("padding set to:"+Math.toDegrees(radialpaddingPerNode));

		}




	}




	//public void addPositionable(Object newPopsitionable,double xsize,double ysize){
	//	addPositionable(new PositionableNodeWrapper(newPopsitionable,xsize,ysize,this));

	//}

	//public boolean add(PositionalNode newPopsitionable){
	//	return this.addPositionable(newPopsitionable);
	//}


	/**
	 * 
	 * @param newPositionable
	 * @return
	 */
	public boolean addPositionable(RadialTreeEndNode newPositionable){
		return addPositionable(newPositionable,true);
	}

	/**
	 * 
	 * @param newPositionable
	 * @param recalcData
	 * @return
	 */
	public boolean addPositionable(RadialTreeEndNode newPositionable,boolean recalcData){

		NodePoint value = new NodePoint();

		childLocations.put(newPositionable, value);

		//ensure its parent is us
		newPositionable.setParentNode(this);

		//and add it
		boolean addedSuccessfully = children.add(newPositionable);

		if (recalcData){
			recalcData();	//recalc our data
			//and the added child if its also a tree
			if (newPositionable instanceof SpiffyRadialTreeSiblingList){
				((SpiffyRadialTreeSiblingList)newPositionable).recalcData();
			}
			
		}

		return addedSuccessfully;


	}

	public void recalcData() {
		//1. recalc min width (which works out work out spare and padding)
		recalcminRequiredwidth();

		//relayout
		recalcXYs(); //Optimized by doing after adding a batch

		//tell any parents of this?
	}

	double angCenter = 0;

	float radius=100;


	class NodePoint {
		double x =-1;
		double y =-1;
	}





	public float getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
		this.recalcData();
	}


	/**
	 * set the center point which this child radial list will be centered around.
	 * 
	 * ang = radians measured from the bottom anti-clockwise

	public void setCenterPointandelativeangle(float x,float y,float newRadius,double angularCentre)
	{

		//update if changed
		boolean changed = false;

		if (centerX!=x || centerY!=y && parentNode ==null) //normally we ge x/y from parent
		{
			this.setXY(x,y);
			changed=true;
		}

		if (radius!=newRadius || angCenter!=angularCentre){
			this.angCenter = angularCentre;
			this.radius=newRadius;
			changed=true;
		}

		if (changed){		
			recalcData();
		}

	}*/



	public void recalcXYs(){

		//update x/y from parent, if any
		//	if (parentNode != null){
		//		centerX = parentNode.getX();
		//		centerY = parentNode.getY();
		//	}

		//loop over all elements to first work out there required space angularly (that is, set there getRadialRequiredspace() based on their size and distance away (radius))
		//	for (PositionalNode node : this) {		

		//	node.calcRadialRequiredSpace(radius); //todo; needs to be set after x/y ? how to work out real spacing not theoretical max needed?

		//	}


		//loop over all elements
		//add widths+padding to get final radial % (all radius is same)
		double currentRadialAng=0;//(radialpaddingPerNode); //start from half padding?

		double startFrom = getCurrentRadialAng() - (getTotalRadialWidthrelativeToCenter()/2.0); //angular width wrong? do we take into account padding??

		currentRadialAng=startFrom;

		for (RadialTreeEndNode node : children) {		


			node.setCurrentRadialAng(currentRadialAng);	
			Log.info("angle of "+" set to:"+Math.toDegrees(currentRadialAng));

			currentRadialAng=
					currentRadialAng
					+node.getRadialRequiredspace()
					+radialpaddingPerNode;

			Log.info("( radialpad:"+Math.toDegrees(node.getRadialRequiredspace())+","+Math.toDegrees(radialpaddingPerNode)+")");

		}

		//convert radial to abs using supplied center
		// x = r *  sin ( ang );
		// y = r *  cos ( ang );
		// ang needs to be relative to horizontal

		//getCurrentRadialAng needs width compensation

		Log.info("getCurrentRadialAng=  "+ Math.toDegrees(getCurrentRadialAng()));
		Log.info("getTotalRadialWidth=  "+ Math.toDegrees(getTotalRadialWidthrelativeToCenter()));
		Log.info("requiredradialWidth=  "+ Math.toDegrees(requiredradialWidthWithoutPadding));

		Log.info("startFrom=  "+Math.toDegrees(startFrom));



		for (RadialTreeEndNode node : children) {	

			double ang = node.getCurrentRadialAng();// +startFrom;

			double x = radius *  Math.sin ( ang );
			double y = radius *  Math.cos ( ang );

			childLocations.get(node).x = x;
			childLocations.get(node).y = y;


			Log.info("nODE X/Y SET TO: =  "+x+","+y+"  ang was:"+Math.toDegrees(ang));

			//node.setXY(centerX+x,centerY+y); 

		} 




	}

	/*
	public double getRadialRequiredWidthForGrandParent(float parentX,float parentY) {

		//get nodes at each end?
		//then 
		//a) draw a line from them to its center point
		//b) get the angle between them
		//c) use this new angle as the minimum size for this childs radial spaceing
		//Note; special case might be needed if theres only 1 node, or subnodes are in total all smalller?

		//parent should be stored?
		//would it be better for nodes to be the same as tree lists, just with on element? probably


		return 0;
	}*/


	//--
	//TODO; replace as much of the above with these;
	//--

	//	@Override
	//	public void setParent(PositionalNode parentNode) {
	//		this.parentNode=parentNode;
	//	}

	public void setXY(double x,double y) {

		this.centerX=x;		
		this.centerY=y;

	}

	double getChildX(RadialTreeEndNode childnode) {
		//get our own x + the x displacement of the child
		double ourx = this.getX();
		double childx = childLocations.get(childnode).x;

		Log.info("our x="+ourx+","+childx);

		//Note; Yes, we could cache our own location, but unless we are dealing with thousands of nodes, performance of checking parent shouldnt matter
		//and this ensures they are always in sycn

		return ourx+childx;
	}


	double getChildY(RadialTreeEndNode childnode) {
		//get our own x + the x displacement of the child
		double oury = this.getY();
		double childy = childLocations.get(childnode).y;

		Log.info("our y="+oury+","+childy);

		//Note; Yes, we could cache our own location, but unless we are dealing with thousands of nodes, performance of checking parent shouldnt matter
		//and this ensures they are always in sync

		return oury+childy;
	}


	//@Override
	//public void setRadialRequiredSpace(double radialrequiredspace) {

	//	if (parentNode==null){
	//no parent = no space to take up on it
	//		RequiredSpaceOnParent=-1;
	//	}



	//Has to be worked out after internal layout is calculated (ie,all x/ys)


	//needs to be calculated in a complexish way;
	//1. get first and last nodes
	//2. get co-ordinates of most clockwise and anticlockwise points on them
	//3. draw a imaginary line from them to the parents of this trees center
	//4. the total angle between them is the space this tree should use up

	//}



	public void setMinimumAngularSize(double radians) {
		minDeziredRadialWidth=radians;
	}



	public int size() {
		return childLocations.keySet().size();
	}



	public RadialTreeEndNode get(int i) {		
		return children.get(i);
	}



	public ArrayList<RadialTreeEndNode> getDirectChildren() {
		return children;
	}

	/*
	public Iterator<RadialTreeEndNode> getIteratorOfAllChildren() {

		Iterator<RadialTreeEndNode> it = children.iterator();

		for (RadialTreeEndNode node : children) {

			Iterables.concat(node.)

		}



		return children.iterator();
	}

	 */
	/**
	 * calcRadialRequiredSpace on parent, needs centerX/centerY known?
	 */
	public void calcRadialRequiredSpace(double parentRadius) {

		//	double radius = parentRadius; //????

		double internalRadial = getTotalRadialWidthrelativeToCenter(); //we need to convert this to the parents center by drawing lines back from each end

		//find endpoints 		
		double centerX = parentRadius; //temp? is this correct?
		double centerY = 0;

		//get start and end x and y;

		//from zero to internalRadial		
		double sx = centerX + radius * Math.sin(0);
		double sy = centerY + radius * Math.cos(0);
		//---

		//from zero to internalRadial		
		double ex = centerX + radius * Math.sin(internalRadial);
		double ey = centerY + radius * Math.cos(internalRadial);
		//---


		//work out ang of those points to our parent
		double ang  = Math.atan2(sx, sy);
		double ang2 = Math.atan2(ex, ey); 

		//and get the difference
		RequiredSpaceOnParent = Math.abs(ang2-ang);


		//alternative;
		//find earlist xy based on first nodes angle - (angular size / 2.0)		
		//find last nodes xy based on first nodes angle - (angular size / 2.0)		
		//angle between them 		
		//doesnt include size

		//RequiredSpaceOnParent=-1;
	}




	/**
	 * Will iterate over itself and all children,greenchildren etc.
	 */
	@Override
	public Iterator<RadialTreeEndNode> iterator() {	

		Iterator<RadialTreeEndNode> myself = super.iterator(); //singleton iterator of ourselves

		Iterable<RadialTreeEndNode> childs = Iterables.concat(children);

		Iterator<RadialTreeEndNode> combo = Iterators.concat(myself,childs.iterator());


		return combo ;
	}




	public void layoutAllChildren() {
		//layout ourselves
		this.recalcData();
		
		Iterator<RadialTreeEndNode> it = this.iterator();
		while (it.hasNext()) {
			RadialTreeEndNode child = (RadialTreeEndNode) it.next();
			
			if (child instanceof SpiffyRadialTreeSiblingList && child!=this){
				((SpiffyRadialTreeSiblingList)child).layoutAllChildren();
			}
		
		}
		
	}




	
	public double getChildDisplacementX(RadialTreeEndNode radialTreeEndNode) {
		return childLocations.get(radialTreeEndNode).x;
	}
	public double getChildDisplacementY(RadialTreeEndNode radialTreeEndNode) {
		return childLocations.get(radialTreeEndNode).y;
	}
	

}
