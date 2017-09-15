package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import lostagain.nl.spiffyresources.client.SpiffyResources;
import lostagain.nl.spiffyresources.client.spiffycore.graphlayout.RadialTreeEndNode;
import lostagain.nl.spiffyresources.client.spiffycore.graphlayout.SpiffyRadialTreeSiblingList;

//
//notes for radialtreesiblingArrayList
//
//can position individualnodes, or nodes which our themselves a set
//structure has to be fully set before working out positions.
//
//// Testing
//1. use labels as nodes.
//a) center label
//b) two sets of labels for radials
//2. Aside labels to 2 radialsibling sets with correct spacing per label
//3. set center, radius, and initial radius positions
//4. loop over all sets placing labels at their deduced x/y
//note; can we use a set too to start? with 360 degree as desired width?
//
////test2
//Experiment with second and third layer of sets (sets in sets)


public class SpiffyRadialTreeTester extends AbsolutePanel {

	Logger Log = Logger.getLogger("SpiffyGWT.GraphLayout.SpiffyRadialTreeTester");
	Label center = new Label("(center)");
	
	public SpiffyRadialTreeTester() {
		super();
		super.setSize("1000px", "1000px");
		center.setWidth("50px");
		center.setHeight("20px");
		center.getElement().getStyle().setBackgroundColor("white");
		
		super.add(center,500-25,500-10); 
		
	}

	public void setup(){
				
		
		SpiffyRadialTreeSiblingList centerlist = new SpiffyRadialTreeSiblingList("center",500,500,center);
		centerlist.setMinimumAngularSize(Math.toRadians(360)); //360 at center
		
		

		SpiffyRadialTreeSiblingList list1 = this.generateARandomBranch("AAAA", "red", 9);
		SpiffyRadialTreeSiblingList list2 = this.generateARandomBranch("BBBB", "green", 3);
		SpiffyRadialTreeSiblingList list3 = this.generateARandomBranch("CCCC", "purple", 5);
		SpiffyRadialTreeSiblingList list3b = this.generateARandomBranch("DDDD", "white", 5);
		
		centerlist.addPositionable(list1,false);
		centerlist.addPositionable(list2,false);
		centerlist.addPositionable(list3,false);
		list3.addPositionable(list3b,false);
		
		centerlist.layoutAllChildren(); //we layout here rather then at each add statement (which is more efficient)
		
		
		//combine lists to debug
		ArrayList<SpiffyRadialTreeSiblingList> allLists = Lists.newArrayList();

		allLists.add(centerlist);
		allLists.add(list1);
		allLists.add(list2);
		allLists.add(list3);
		allLists.add(list3b);
		
		//allLists.add(list2a);
		//allLists.add(listabcd);
		
		for (SpiffyRadialTreeSiblingList list : allLists) {
		
	
		//debug all sibling lists

			
			SpiffyResources.screenlog("Lists:");
			SpiffyResources.screenlog("_______________");
			SpiffyResources.screenlog("Name:"+list.getName());
			SpiffyResources.screenlog("Angle To Parent:"+Math.toDegrees(list.getCurrentRadialAng()));
			SpiffyResources.screenlog("Number of Child Nodes:"+list.size());
			
			
			SpiffyResources.screenlog("----> Ang Size of node #0 :"+Math.toDegrees(list.get(0).getRadialRequiredspace()));
			SpiffyResources.screenlog("----> Ang of node #0 :"+Math.toDegrees(list.get(0).getCurrentRadialAng()));
		//	SpiffyResources.screenlog("----> X of node #1 :"+(list.get(1).getX()));
		//	SpiffyResources.screenlog("----> x of node #2 :"+(list.get(2).getX()));
			
			SpiffyResources.screenlog("Padding per node:"+Math.toDegrees(list.getRadialNodePadding()));
			SpiffyResources.screenlog("Total needed:"+Math.toDegrees(list.getTotalRadialWidthrelativeToCenter()));
			
			
			SpiffyResources.screenlog("Ang Size Needed By Them relative to list center:"+Math.toDegrees(list.getRadialRequiredspace()));
			SpiffyResources.screenlog("_______________");
			//name
			//angle to its parents center
			//number of subnodes (angular gap between them)
		
			
		}
		
		//getTotalRadialWidthrelativeToCenter
/*
			//combine lists nodes
			ArrayList<RadialTreeEndNode> allNodes = Lists.newArrayList();
			
			allNodes.addAll(centerlist.getDirectChildren());			
			allNodes.addAll(list1.getDirectChildren());
			allNodes.addAll(list2.getDirectChildren());
			allNodes.addAll(list3.getDirectChildren());
			allNodes.addAll(list3b.getDirectChildren());
			
			*/
			//allNodes.addAll(list2a.getChildren());
		//	allNodes.addAll(listabcd.getChildren());

			Iterator<RadialTreeEndNode> it = centerlist.iterator();
			
			
			while (it.hasNext()) {
				RadialTreeEndNode node = (RadialTreeEndNode) it.next();
				
	///		}
		//draw all
	//	for (RadialTreeEndNode node : allNodes) {
			
			Label nodeContent =	(Label) node.getContent();			
			
			//displace back x/y as getx/gety returns the center
			
			int offsetWidth  = nodeContent.getOffsetWidth();
			int offsetHeight = nodeContent.getOffsetHeight();

			SpiffyResources.screenlog("_______________offsetWidth:"+offsetWidth);
			
			int tlx = (int)(node.getX() -  (offsetWidth/2.0));
			int tly = (int)(node.getY() -  (offsetHeight/2.0));
			
			
			super.add(nodeContent,
					 tlx,
					 tly);
			
		}

		/*
		for (PositionalNode node : list1) {
			
			Label nodeContent =	(Label) node.getContent();						
			super.add(nodeContent,(int)node.getX(),(int)node.getY());
		}
		for (PositionalNode node : list2a) {
			
			Label nodeContent =	(Label) node.getContent();						
			super.add(nodeContent,(int)node.getX(),(int)node.getY());
		}
		
		for (PositionalNode node : listabcd) {
			
			Label nodeContent =	(Label) node.getContent();						
			super.add(nodeContent,(int)node.getX(),(int)node.getY());
		}*/
		
	}

	
	private RadialTreeEndNode generatePos(Label newnode) {		
		RadialTreeEndNode node = new RadialTreeEndNode("sublist",newnode); //position at last node in list1
		
		
		int offsetHeight = newnode.getOffsetHeight();		
		int offsetWidth = newnode.getOffsetWidth();
		
		Log.info("size:"+offsetHeight+","+offsetWidth);
		
		
		node.setContentsSize(offsetWidth,offsetHeight);
		
		
		return node;
	}
	
	
	private SpiffyRadialTreeSiblingList generateARandomBranch(String name, String colour, int numOfItems){
		
		//new centerpoint
		Label newcenter = generateLabel(name, colour);		
		
		//create new list
		SpiffyRadialTreeSiblingList newlist = new SpiffyRadialTreeSiblingList(name,newcenter); //not positioned till attached 
		
		//populate
		for (int i = 0; i < numOfItems; i++) {
			
			Label newnode = generateLabel(name+"_"+i,colour);			
			RadialTreeEndNode newNodePos = generatePos(newnode);	
			newlist.addPositionable(newNodePos,false);			

		}
		
		
		
		return newlist;
		
	}
	
	

	private Label generateLabel(String nodeString,String backcolour) {
		Label newnode = new Label(nodeString);
		
		//---
		//newnode.setWidth("30px");
		//newnode.setHeight("20px");	
		
		newnode.getElement().getStyle().setBackgroundColor(backcolour);
		
		//attach to temp loc so the size is known
		super.add(newnode,0,0);
		
		
		return newnode;
	}
	

}
