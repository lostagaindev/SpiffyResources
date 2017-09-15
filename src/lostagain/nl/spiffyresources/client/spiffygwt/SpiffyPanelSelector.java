package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The SpiffySelectorPanel lets you contain a selection of other panels<br> 
 * and select one by pressing arrows at the left and right.
 * 
 * The height of the SpiffyPanelSelector is taken from the height of the largest widget within it<br>
 * The width however, needs to be manually specified .<br>
 * This is so you can see a few panels at once (imagine a horizontal scrolling "wheel" of panels with the middle one being the selection)<br>
 * 
 * Structure:
 * 
 * HorizontalPanel with absolute panel in the middle and arrow icons at the left and right
 * The absolute panel contains another HorizontalPanel which contains the panels the user adds.
 * 
 * We use a absolute panel so the "middle" of the outer horizontal panel can be slide about without effecting the arrows at the left
 * and right of it
 * 
 * @author Tom
 * 
 * -----|--------|-----
 *      |        |       
 * -----|--------|-----
 *
 */
public class SpiffyPanelSelector extends HorizontalPanel {
	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyPanelSelector");




	private static StandardInterfaceImages standardIconPack = (StandardInterfaceImages) GWT
			.create(StandardInterfaceImages.class);
	

	final static ImageResource DefaultPrevious = standardIconPack.LeftArrow_dark(); 	//default images
	final static ImageResource DefaultPrevious_mouseover = standardIconPack.LeftArrow_light(); 	//default images
	final static ImageResource DefaultNext =  standardIconPack.RightArrow_dark();	//default images
	final static ImageResource DefaultNext_mouseover =  standardIconPack.RightArrow_light();	//default images
	
	final static ImageResource DefaultFirst =   standardIconPack.LeftArrowEnd_dark();
	final static ImageResource DefaultFirst_over =   standardIconPack.LeftArrowEnd_light();
	final static ImageResource DefaultLast =   standardIconPack.RightArrowEnd_dark();
	final static ImageResource DefaultLast_over =   standardIconPack.RightArrowEnd_dark();
	
	/**
	 * First and Previous buttons go in here.
	 * At the moment they are under eachother in a verticalpanel.
	 * In future we might offer a choice between horizontal and vertical modes, as its normally more intuative to have them horizontal to eachother
	 * when theres space
	 */
	VerticalPanel backwardsNavigation = new VerticalPanel();
	
	Image First =  null; 	//image holder
	ImageResource First_mouseover = null; 	
	ImageResource First_mouseout = null; 	

	Image Previous =  null; 	//image holder
	ImageResource Previous_mouseover = null; 	
	ImageResource Previous_mouseout = null; 	

	/** we use a absolute panel so we have the option to slide the middle of it about **/
	AbsolutePanel contentHolder = new AbsolutePanel();

	/** placed into the contentHolder, we place things actually added to the SpiffyPanelSelector here **/
	HorizontalPanel panelHolder = new HorizontalPanel();
	
	/**
	 * Next and Last buttons go in here.
	 * At the moment they are under eachother in a verticalpanel.
	 * In future we might offer a choice between horizontal and vertical modes, as its normally more intuative to have them horizontal to eachother
	 * when theres space
	 */
	VerticalPanel forwardsNavigation = new VerticalPanel();
	
	Image Next =  null;	//image holder

	ImageResource Next_mouseout =  null;	
	ImageResource Next_mouseover =  null;	
	
	Image Last =  null; 	//image holder
	ImageResource Last_mouseover = null; 	
	ImageResource Last_mouseout = null; 	

	/** standard image width  
	 * Can be adjustable for different arrow sizes**/
	int imageWidth = 35;


	/** the index of the widget currently focused **/
	int currentWidgetIndex = 0;

	/** the tallest thing currently being stored in panelHolder, this is
	 * used to set the overall size of this widget **/
	int currentLargestHeight = -1; //defaults to 1 px; 
	
	//the add panel widget (just a button for now)		
	Button addPanelWidget = new Button(" ( + ) ");

	/** handler that runs when a item is selected (assumeing this has been set to something other then null)**/
	private OnSelectedHandler currentOnSelectedHandler = null;




	private Widget currentWidget;


	/** if set to true we add a bit + panel at the end which triggers its own handler when clicked **/
	boolean hasAddPanelPanel = false;

	/**
	 * if we have a "add panel panel" this will fire when its clicked
	 */
	private Runnable currentDoThisWhenAddPanelIsClicked;

	
	
	
	/**
	 * Note; When not supplying your own images default ones will be used. However
	 * due to the way GWT handles image strips, these cant be sized, so this will look bad on smaller interfaces.
	 * 
	 * The height is taken from the widgets added.
	 * Width must be specified
	 * @param Width
	 */
	public SpiffyPanelSelector(String Width) {		
		
		this(Width,DefaultPrevious,DefaultNext,DefaultPrevious_mouseover,DefaultNext_mouseover);
	}
	
	/**
	 * Note; When not supplying your own images default ones will be used. However
	 * due to the way GWT handles image strips, these cant be sized, so this will look bad on smaller interfaces.
	 * 
	 * The height is taken from the widgets added.
	 * Width must be specified
	 * @param Width
	 */
	public SpiffyPanelSelector(String Width,
			ImageResource leftArrow,ImageResource rightArrow,
			ImageResource leftArrowOver,ImageResource rightArrowOver) {		
		
		this(Width,leftArrow,rightArrow,
				   leftArrowOver,rightArrowOver,
				   DefaultFirst,DefaultLast,
				   DefaultFirst_over,DefaultLast_over);
	}
	/**
	 * The height is taken from the widgets added.
	 * Width must be specified
	 * 
	 * @param Width
	 * @param leftArrow
	 * @param rightArrow
	 * @param leftArrowOver
	 * @param rightArrowOver
	 */
	
	public SpiffyPanelSelector(String Width,
								ImageResource leftArrow,ImageResource rightArrow,
								ImageResource leftArrowOver,ImageResource rightArrowOver,
								ImageResource gotoStartArrow,ImageResource gotoEndArrow,
								ImageResource gotoStartArrowOver,ImageResource gotoEndArrowOver) {
		super();		
		super.setWidth(Width);
		
		//setup images from resources supplied

		First             = new Image(gotoStartArrow);
		First_mouseover   = gotoStartArrowOver;
		First_mouseout    = gotoStartArrow;
		
		Previous          = new Image(leftArrow);
		Previous_mouseover= leftArrowOver;
		Previous_mouseout = leftArrow;
		
		Next              = new Image(rightArrow);
		Next_mouseover    = rightArrowOver;
		Next_mouseout     = rightArrow;

		Last              = new Image(gotoEndArrow);
		Last_mouseover    = gotoEndArrowOver;
		Last_mouseout     = gotoEndArrow;
		
		//create the main container with arrows at either side
		
		Previous.setWidth(imageWidth+"px");
		Next.setWidth(imageWidth+"px");
		int scaledheightN = (Previous_mouseout.getWidth()/imageWidth)*Previous_mouseout.getHeight();
		Previous.setHeight(scaledheightN+"px");
		int scaledheightP = (Next_mouseout.getWidth()/imageWidth)*Next_mouseout.getHeight();
		Next.setHeight(scaledheightP+"px");


		backwardsNavigation.setHeight("100%");	
		backwardsNavigation.add(Previous);
		backwardsNavigation.add(First);		
		super.add(backwardsNavigation);
		
		super.setCellVerticalAlignment(Previous, HasVerticalAlignment.ALIGN_MIDDLE);

		contentHolder.setWidth("100%");				
		contentHolder.add(panelHolder,0,0);		
		super.setBorderWidth(1); //helps debug
		super.add(contentHolder);
		super.setCellWidth(contentHolder, "100%");
		
		forwardsNavigation.setHeight("100%");		
		forwardsNavigation.add(Next);
		forwardsNavigation.add(Last);		
		super.add(forwardsNavigation);
		
		super.setCellVerticalAlignment(Next, HasVerticalAlignment.ALIGN_MIDDLE);
		super.setCellHorizontalAlignment(Next, HasHorizontalAlignment.ALIGN_RIGHT);

		//attach handlers for left and right selection
		Next.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {

				gotoWidget(currentWidgetIndex+1);	
			}
		});

		Previous.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {

				gotoWidget(currentWidgetIndex-1);	
			}
		});
		
		First.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {

				gotoWidget(0);	
			}
		});

		Last.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {

				gotoWidget(getWidgetCount()-1);	
			}
		});
		//--------------------------------------

		Next.addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Next.setResource(Next_mouseover);		
				Next.setWidth(imageWidth+"px"); //force size setting

			}
		});

		Previous.addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Previous.setResource(Previous_mouseover);		
				Previous.setWidth(imageWidth+"px"); //force size setting

			}
		});
		First.addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				First.setResource(First_mouseover);		
				First.setWidth(imageWidth+"px"); //force size setting

			}
		});

		Last.addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Last.setResource(Last_mouseover);		


				Last.setWidth(imageWidth+"px"); //force size setting

			}
		});
		//-----------------------------------------


		Next.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				Next.setResource(Next_mouseout);	


				Next.setWidth(imageWidth+"px"); //force size setting

			}
		});		
		Previous.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				Previous.setResource(Previous_mouseout);		


				Previous.setWidth(imageWidth+"px"); //force size setting

			}
		});
		First.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				First.setResource(First_mouseout);	


				First.setWidth(imageWidth+"px"); //force size setting

			}
		});		
		Last.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				Last.setResource(Last_mouseout);		


				Last.setWidth(imageWidth+"px"); //force size setting

			}
		});

	}



	/** Jumps to the widget with the specified index **/
	public void gotoWidget(int index) {


		if (index >= 0  && index < panelHolder.getWidgetCount())
		{						
			//get location of the widget horizontally within panelHolder
			int widgetsXPositionInPanel = getHorizontalPositionOf(index);
			

			//as we want to set the panel to the middle of the contentHolders 
			//not the left edge, we need to get the total size of the holder at this moment
			int chW = contentHolder.getOffsetWidth(); 

			//find the middle
			int chMiddle = chW/2;
			
			
			Widget previousWidget =  currentWidget;
			currentWidget = panelHolder.getWidget(index);	
			
			//subtract half the width of the current widget we are focusing on			
			int positionFromLeftEdge = chMiddle - (panelHolder.getWidget(index).getOffsetWidth()/2);

			//move the absolute panel so its left side is at widgetsXPositionInPanel			
			animateToWidgetPosition(positionFromLeftEdge-widgetsXPositionInPanel, 0);


			//set it as current
			currentWidgetIndex = index;

			
			if (isOnAddPanelPanel()){
				
				currentWidget = null;
				
			} else if (currentOnSelectedHandler!=null) {
				
				//if theres a selected handler and we are not on the add panel panel	
				//then we fire the selected handler if the selection is different to what it was before
				if (previousWidget!=currentWidget) {
					Log.info(" running panels selection handler ");				
					currentOnSelectedHandler.run(currentWidgetIndex, currentWidget,previousWidget);
				}
				
			}

			Last.setVisible(true);
			Next.setVisible(true);
			Previous.setVisible(true);
			First.setVisible(true);
			
			//recheck to show/hide arrows as needed
			if (currentWidgetIndex == 0 ){
				Previous.setVisible(false);
				First.setVisible(false);
				

			} else if (currentWidgetIndex == panelHolder.getWidgetCount()) {
				Next.setVisible(false);

				Last.setVisible(false);

			}



		}


	}

	/** Adds a special panel at the end for adding other panels 
	 * 
	 * @param doThisWhenAddPanelIsClicked - fires when the add button is clicked
	 */
	public void addAddPanelPanel(Runnable doThisWhenAddPanelIsClicked){


		currentDoThisWhenAddPanelIsClicked = doThisWhenAddPanelIsClicked;


		addPanelWidget.setHeight("100px");
		addPanelWidget.setWidth("200px");


		addPanelWidget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				currentDoThisWhenAddPanelIsClicked.run();
			}
		});
		this.add(addPanelWidget);
		this.hasAddPanelPanel = true;

	}

	public boolean isOnAddPanelPanel(){
		if (hasAddPanelPanel && currentWidgetIndex==(this.getWidgetCount())){
			return true;	

		} else {
			return false;
		}
	}


	private void animateToWidgetPosition(int x,int y) {		
		
		//note co-ordinates need to be inverted as we are moving the panel, not the widgets
		Log.info("heading to widget at position:"+(-x)+","+(-y));

		//not implemented yet
		//in future we will slide smoothly between position
		contentHolder.setWidgetPosition(panelHolder, x, y);

	}


	public static interface OnSelectedHandler {	
		void run(int IndexSelected,Widget widgetSelected, Widget widgetUnSelected);
	}

	public void setOnSelectedHandler(OnSelectedHandler runthis){
		currentOnSelectedHandler = runthis;		

	}

	/** Get the horizontal pixel position of the widget at this index **/
	private int getHorizontalPositionOf(int index) {

		//If first widget its just at 0px
		if (index==0){
			return 0;
		}

		int currentPixelLength = 0;

		//0,1,2,3,4
		//Else to work this out we loop over each widget before it adding its length.
		for (int i = 0; i < index; i++) {

			currentPixelLength=currentPixelLength+panelHolder.getWidget(i).getOffsetWidth();

		}




		return currentPixelLength;
	}



	@Override
	public void add(Widget w) {

		//if we have a add panel panel we actually insert the widget before that one.
		if (hasAddPanelPanel) {
			this.insert(w, this.getWidgetCount());

		} else {
			panelHolder.add(w);
		}

		Log.info("added widget height was "+w.getOffsetHeight());
		if (w.getOffsetHeight()>currentLargestHeight){
			currentLargestHeight= w.getOffsetHeight();
			updateHeight();
		}
	}

	/**
	 * will be override if a new widget is added
	 * @param height
	 */
	public void setHeightOfTallestWidget(int height)
	{
		currentLargestHeight = height;
		updateHeight();
	}
	
	private void updateHeight() {

		Log.info("Updating height");

		super.setHeight(currentLargestHeight+"px");
		contentHolder.setHeight(currentLargestHeight+"px");
		super.setCellHeight(contentHolder, currentLargestHeight+"px");

		//update the images too (this is a massive experimental hack to try to work out a problem right now..)
		Previous.setHeight("auto");
		Next.setHeight("auto");

		Previous.setResource(Previous_mouseout);	
		Next.setResource(Next_mouseout);	
		Last.setResource(Last_mouseout);
		First.setResource(First_mouseout);
		
		Previous.setWidth(imageWidth+"px"); //force size setting
		Next.setWidth(imageWidth+"px"); //force size setting
		Last.setWidth(imageWidth+"px");
		First.setWidth(imageWidth+"px");
		
		int scaledheightN = (Previous_mouseout.getWidth()/imageWidth)*Previous_mouseout.getHeight();
		Previous.setHeight(scaledheightN+"px");
		int scaledheightP =     (Next_mouseout.getWidth()/imageWidth)*Next_mouseout.getHeight();
		Next.setHeight(scaledheightP+"px");
		
		int scaledheightF =     (First_mouseout.getWidth()/imageWidth)*First_mouseout.getHeight();
		First.setHeight(scaledheightF+"px");
		int scaledheightL =     (Last_mouseout.getWidth()/imageWidth)*Last_mouseout.getHeight();
		Last.setHeight(scaledheightL+"px");
		
	}



	@Override
	public void insert(Widget w, int beforeIndex) {
		panelHolder.insert(w, beforeIndex);

		if (w.getOffsetHeight()>currentLargestHeight){
			currentLargestHeight= w.getOffsetHeight();
			updateHeight();
		}
	}

	/** Note; currently removing widgets does not reshrink this containers height**/
	@Override
	public boolean remove(Widget w) {
		return panelHolder.remove(w);

		//TODO: recheck remaining widgets for next tallest and make that the new height
	}

	@Override
	public Widget getWidget(int index) {
		return panelHolder.getWidget(index);
	}

	@Override
	public int getWidgetCount() {
		if (hasAddPanelPanel){
			return panelHolder.getWidgetCount()-1; //we dont include the add panel in any counts
		} else {
			return panelHolder.getWidgetCount();
		}
	}
	
	@Override
	public int getWidgetIndex(Widget child) {
		return panelHolder.getWidgetIndex(child);
	}
	@Override
	public int getWidgetIndex(IsWidget child) {
		return panelHolder.getWidgetIndex(child);
	}

	@Override
	public void clear() {
		panelHolder.clear();

		//ToDo:Recheck height
	} 




	@Override
	protected void onLoad() {
		super.onLoad();

		//update size
		//to do this we need to test the size of all the attached widgets to find the max height
		currentLargestHeight = findMaxHeight();
		updateHeight();

		//refocus on current widget
		gotoWidget(currentWidgetIndex);

	}

	private int findMaxHeight(){

		//loop over all the widgets in the inner panel, testing their heights to find the largest
		int tempMaxHeight = -1;
		for (Widget w : panelHolder) {

			if (w.getOffsetHeight()>tempMaxHeight){
				tempMaxHeight = w.getOffsetHeight();
			}			

		}
		Log.info("current max height="+tempMaxHeight);

		return tempMaxHeight;
	}




	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}



	/** displays the specified widget **/
	public void gotoWidget(Widget widget) {

		int i = getWidgetIndex(widget);

		Log.info("going to widget at index:"+i);
		
		this.gotoWidget(i);



	}

	/** gets all the widgets, excluding the AddAddPanel if one has been set**/
	public ArrayList<Widget> getWidgets() {
		
		ArrayList<Widget> temp = new ArrayList<Widget>();
		
		for (Widget w : panelHolder) {
			//add to the list of widgets to return if its not the addPanelWidget
			if (w!=addPanelWidget){
				temp.add(w);
			}
			
		}
		
		
		return temp;
	}

	public void alignNavigationButtons(VerticalAlignmentConstant align) {
		
		backwardsNavigation.setCellVerticalAlignment(Previous, align);
		forwardsNavigation.setCellVerticalAlignment(Next, align);
		backwardsNavigation.setCellVerticalAlignment(First, align);
		forwardsNavigation.setCellVerticalAlignment(Last, align);
	}

}
