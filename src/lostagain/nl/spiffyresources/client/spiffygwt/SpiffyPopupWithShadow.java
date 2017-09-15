package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.Iterator;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpiffyPopupWithShadow extends PopupPanel implements TouchStartHandler,TouchEndHandler,MouseUpHandler,TouchMoveHandler,MouseMoveHandler,MouseDownHandler {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyPopUpWithShadow");

	private   String BOTTOM_RIGHT_BACKSTYLE = "ShadowBottomRight pngfix";
	private   String BOTTOM_MIDDLE_BACKSTYLE = "ShadowLower pngfix";
	private   String BOTTOM_LEFT_BACKSTYLE = "ShadowCorner pngfix";
	private   String MIDDLE_LEFT_BACKSTYLE = "ShadowLeft pngfix";
	private   String MIDDLE_RIGHT_BACKSTYLE = "ShadowRight pngfix";	
	private   String TOP_MIDDLE_BACKSTYLE = "DefaultTopBar";
	private   String TOP_LEFT_BACKSTYLE = "ShadowTopLeft pngfix";
	// 

	boolean centered = true;


	VerticalPanel verticalSplit = new VerticalPanel();

	Grid Container = new Grid(3,3);

	//set at this zindex flag
	public boolean fixed_zindex = false;
	public int fixed_zindex_value = 5000;


	public Label caption = new Label("PopUp (Drag Me)");
	private boolean dragging;
	private int dragStartX, dragStartY;

	String sizeX = "200px";
	String sizeY = "100px";
	Widget Contents = new Label("");

	// pretty colour base
	HTML overlay = new HTML("<div></div>");

	//topbar bits
	HorizontalPanel TopBar = new HorizontalPanel();

	//used to be interface icon
	private StandardInterfaceImages standardimages = (StandardInterfaceImages) GWT
			.create(StandardInterfaceImages.class);

	Image closeX = new Image(standardimages.Close());
	Image closeX_over = new Image(standardimages.Close_over());

	//initial popup z level
	final static int INITIAL_MAX_ZINDEX = 20000;
	boolean FIXEDPOSITIONMODE = false;
	
	/**
	 * Create a new popup containing "setContents"
	 * You can also use a null for the setcontents widget and set it later with .setcenterwidget(..)
	 * 
	 * @param X - width in css uits 
	 * @param Y - height in css units
	 * @param title - the title of the popup 
	 * @param SetContents - the widget you want the popup to contain
	 * @param Dragable - 
	 */
	public SpiffyPopupWithShadow(String X,String Y,String title,Widget SetContents, boolean Dragable){

		Contents=SetContents;

		sizeX = X;
		sizeY = Y;
		this.setSize(sizeX, sizeY);

		//DOM.setStyleAttribute(this.getElement(), "zIndex", "1050");

		super.getElement().getStyle().setZIndex(1050);



		this.setAnimationEnabled(true);


		setupTitleBar(title,Dragable);
		setupContainer();

	}



	private void setupContainer() {
		//set up shadows
		//set border shadows for title
		Container.setCellPadding(0);
		Container.setCellSpacing(0);
		Container.getCellFormatter().setStyleName(0, 0, TOP_LEFT_BACKSTYLE);

		Container.getCellFormatter().setWidth(0, 0, "8px");
		Container.getCellFormatter().setHeight(0, 0, "21px");

		Container.setWidget(0, 1, TopBar);

		Container.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);

		Container.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		Container.getCellFormatter().setStyleName(0, 1, TOP_MIDDLE_BACKSTYLE);
		Container.getCellFormatter().setHeight(0, 1, "21px");
		Container.getCellFormatter().setStyleName(0, 2, TOP_MIDDLE_BACKSTYLE);
		Container.getCellFormatter().setHeight(0, 2, "21px");

		Container.getCellFormatter().setWidth(1, 0, "8px");
		Container.getCellFormatter().setStyleName(1, 0, MIDDLE_LEFT_BACKSTYLE);

		//

		Container.getCellFormatter().setStyleName(1, 1, "Backstyle");

		// load contents into HTML
		if (Contents!=null){
			Container.setWidget(1, 1, Contents);

			//System.out.println("co");


			//Container.getCellFormatter().setStyleName(1, 1, "popup_border");

			if (!(Contents.getStylePrimaryName() == null))
			{		Container.getCellFormatter().setStyleName(1, 1, Contents.getStylePrimaryName()); //why did this use to be 1,2?
			}

		}
		Container.getCellFormatter().setStyleName(1, 2, MIDDLE_RIGHT_BACKSTYLE);

		Container.getCellFormatter().setWidth(2, 0, "8px");
		Container.getCellFormatter().setHeight(2, 0, "8px");
		Container.getCellFormatter().setStyleName(2, 0, BOTTOM_LEFT_BACKSTYLE);


		Container.getCellFormatter().setHeight(2, 1, "8px");
		Container.getCellFormatter().setStyleName(2, 1, BOTTOM_MIDDLE_BACKSTYLE);

		Container.getCellFormatter().setWidth(2, 2, "16px");
		Container.getCellFormatter().setHeight(2, 2, "8px");
		Container.getCellFormatter().setStyleName(2, 2, BOTTOM_RIGHT_BACKSTYLE);

		this.setWidget(Container);
	}

	private void setupTitleBar(String title,boolean draggable) {
		//this needs its own topbar set up	
		caption.setText(title);

		TopBar.add(caption);
		TopBar.add(closeX);

		closeX.setSize("21px", "19px");
		TopBar.setCellHorizontalAlignment(closeX,HasHorizontalAlignment.ALIGN_CENTER);
		TopBar.setStylePrimaryName(TOP_MIDDLE_BACKSTYLE);
		TopBar.setWidth("100%");
		TopBar.setHeight("21px");
		TopBar.add(caption);

		TopBar.setCellWidth(caption, "95%");
		TopBar.add(closeX);
		TopBar.setCellHorizontalAlignment(caption, HasHorizontalAlignment.ALIGN_CENTER);
		TopBar.setCellHorizontalAlignment(closeX, HasHorizontalAlignment.ALIGN_RIGHT);


		closeX.addMouseOverHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {

				closeX.setResource(standardimages.Close_over());

			}

		});
		closeX.addMouseOutHandler(new MouseOutHandler(){

			public void onMouseOut(MouseOutEvent event) {

				closeX.setResource(standardimages.Close());

			}

		});



		closeX.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				Log.info("closing popup");
				hide();

			}
		});
		// this.setText("- Drag Me -");
		//verticalSplit.add(TopBar);
		//this.add(verticalSplit);
		//overlay.setSize("400px", "400px");
		//overlay.setStyleName("overlay");
		//verticalSplit.add(IContents);

		//	DOM.appendChild(this.getElement(), caption.getElement());
		//adopt(caption);
		//caption.setStyleName("Caption");


		//set to dragable if draggable unless not dragable

		//if (((isPopUpType)Contents).DRAGABLE()){
		if (draggable){

			caption.addMouseMoveHandler(this);
			caption.addMouseUpHandler(this);
			caption.addMouseDownHandler(this);
			//handle touch events
			caption.addTouchStartHandler(this);
			caption.addTouchMoveHandler(this);
			caption.addTouchEndHandler(this);

		}

	}

	public void setCenterWidget(IsWidget widget){

		Container.setWidget(1, 1, widget);

		this.setSize( Container.getOffsetWidth()+16+"px", sizeY);

	}

	@Override
	public boolean onEventPreview(Event event) {
		// We need to preventDefault() on mouseDown events (outside of the
		// DialogBox content) to keep text from being selected when it
		// is dragged.
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if (DOM.isOrHasChild(caption.getElement(), DOM
					.eventGetTarget(event))) {
				DOM.eventPreventDefault(event);
			}
		}

		if (DOM.eventGetType(event) == Event.ONTOUCHSTART) {
			if (DOM.isOrHasChild(caption.getElement(), DOM
					.eventGetTarget(event))) {
				DOM.eventPreventDefault(event);
			}
		}


		return super.onEventPreview(event);
	}




	public void fixedZdepth (int setdepth)
	{
		fixed_zindex = true;
		fixed_zindex_value = setdepth;

		DOM.setStyleAttribute(this.getElement(), "z-index", ""+(fixed_zindex_value));
		DOM.setStyleAttribute(this.getElement(), "zIndex", ""+(fixed_zindex_value));


	}

	public void fixedZdepthOff ()
	{
		fixed_zindex = false;


	}


	//NOTE: The below code can be adapted for extra functionality 
	//eg. Having a internal list of all SpiffyPopups that are open, and making them automatically goto front when clicked

	/*
	public void OpenDefault() {

		   JAM.DebugWindow.addText("opening popup");
			Log.info("opening popup");

		int z=0;
		String tempz = " zdepth=first pop";
		JAM.z_depth_max=INITIAL_MAX_ZINDEX;
		//first we check over all the current open popups and their z-depth;
		Iterator<PopUpWithShadow> popupIter = JAM.overlayPopUpsOpen.iterator();
		JAM.Feedback.setTitle("a");
		if (JAM.overlayPopUpsOpen.size()>0){
			while ( popupIter.hasNext() )
			{
				PopUpWithShadow cur = popupIter.next();
				JAM.Feedback.setTitle("b");
				//z =  Integer.parseInt(cur.getElement().getStyle().getProperty("z-index"));
				if (cur.getElement().getStyle().getProperty("z-index")==null){
					//z =  Integer.parseInt(cur.getElement().getStyle().getProperty("zIndex"));
				} else {
					z =  Integer.parseInt(cur.getElement().getStyle().getProperty("z-index"));
				}

				JAM.Feedback.setTitle("cz="+z);

				if (z>JAM.z_depth_max) {
					JAM.z_depth_max=z;
				}
				tempz = tempz +"\n z depth = "+z;

			}
	//	MyApplication.Feedback.setTitle("a max depth set to "+MyApplication.z_depth_max);

	//	} 
		//MyApplication.Feedback.setText("=-=");
	//	if ((Integer.parseInt(MyApplication.fadeback.getElement().getStyle().getProperty("z-index")))>MyApplication.z_depth_max)
	//	{
	//		MyApplication.Feedback.setText("b");
		//	MyApplication.z_depth_max=Integer.parseInt(MyApplication.fadeback.getElement().getStyle().getProperty("z-index"));
//
	//		MyApplication.Feedback.setText("b max depth set to "+MyApplication.z_depth_max);


		} else {
			//we can reset the max
			JAM.z_depth_max=INITIAL_MAX_ZINDEX;
		//	MyApplication.Feedback.setText("c max depth set to "+MyApplication.z_depth_max);

		}


		//if the backfader is in front for some reason, we use thats zdepth instead
		if (centered)
		{
		this.center();
		} else {
		this.show();


		}

		try {
			((hasOpenDefault) this.Contents).OpenDefault();
		} catch (Exception e) {
			//in case its not got a openDefault

		}



		//detect if its outside the top edge of the screen, and if so, align to the top.

		int this_x = this.getAbsoluteLeft();
		int this_height = this.getOffsetHeight();
		if (this_height>Window.getClientHeight()){
			this.setPopupPosition(this_x, 0);
		};





		//set this ones zdepth to max+2 (plus1 might be reserved for the fader)
		DOM.setStyleAttribute(this.getElement(), "z-index", ""+(JAM.z_depth_max+1));
		DOM.setStyleAttribute(this.getElement(), "zIndex", ""+(JAM.z_depth_max+1));
		JAM.z_depth_max=JAM.z_depth_max+1;


		  JAM.overlayPopUpsOpen.add(this);

	}*/


	public boolean DRAGABLE() {
		return false;
	}



	public void onMouseDown(MouseDownEvent event) {

		int x = event.getX();
		int y = event.getY();

		Log.info("onMouseDown");
		onMouseOrTouchStart(x, y);
		//DOM.setStyleAttribute(this.getElement(), "Zindex", ""+(MyApplication.z_depth_max+1));

	}

	private void onMouseOrTouchStart(int x, int y) {
		dragging = true;
		DOM.setCapture(caption.getElement());
		dragStartX = x;
		dragStartY = y;

		//set to top
		//set this ones zdepth to max+1
		if (fixed_zindex == false){        
			//Log.info("setting ZIndexTop");
			//setZIndexTop(); //not implemented yet        	
		}

	}

	@Override
	public void onTouchStart(TouchStartEvent event) {

		int x=event.getChangedTouches().get(0).getRelativeX(event.getRelativeElement());
		int y=event.getChangedTouches().get(0).getRelativeY(event.getRelativeElement());

		Log.info("onTouchStart "+x+","+y);

		onMouseOrTouchStart(x, y);
	}



	//Part of the unimplemented thing mentioned above
	//To implement this mostly its just making z_depth_max a internal static variable
	/*
	public void setZIndexTop() {
		DOM.setStyleAttribute(this.getElement(), "z-index", ""+(JAM.z_depth_max+1));
		DOM.setStyleAttribute(this.getElement(), "zIndex", ""+(JAM.z_depth_max+1));
		Log.info("setZIndexTop");
		JAM.Feedback.setTitle("z set too"+DOM.getStyleAttribute(this.getElement(),  "z-index"));

		JAM.z_depth_max=JAM.z_depth_max+1;
	}
	 */


	@Override
	public void onTouchMove(TouchMoveEvent event) {

		int x=event.getChangedTouches().get(0).getRelativeX(event.getRelativeElement());
		int y=event.getChangedTouches().get(0).getRelativeY(event.getRelativeElement());


		onMouseOrTouchMove(x, y);
	}


	public void onMouseMove(MouseMoveEvent event) {

		int x=event.getX();
		int y=event.getY();


		onMouseOrTouchMove(x, y);
	}

	private void onMouseOrTouchMove(int x, int y) {
		if (dragging) {
			int absX = x + getAbsoluteLeft();
			int absY = y + getAbsoluteTop();

			if ((absY - dragStartY)<0){
				setPopupPosition(absX - dragStartX,0);
			} else 
			{
				setPopupPosition(absX - dragStartX, absY - dragStartY);
			}
		}
	}




	public void onMouseUp(MouseUpEvent event) {
		dragging = false;
		DOM.releaseCapture(caption.getElement());
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {

		Log.info("onTouchEnd");
		dragging = false;
		DOM.releaseCapture(caption.getElement());
	}


	public void clearBackgroundStyles() {

		Log.info("clearing all styles on popup");

		BOTTOM_RIGHT_BACKSTYLE = "";
		BOTTOM_MIDDLE_BACKSTYLE = "";
		BOTTOM_LEFT_BACKSTYLE = "";
		MIDDLE_LEFT_BACKSTYLE = "";
		MIDDLE_RIGHT_BACKSTYLE="";
		TOP_MIDDLE_BACKSTYLE = "";
		TOP_LEFT_BACKSTYLE = "";

		super.setStyleName("");

		setupContainer(); 



	}

	public void setTitlebBarText(String title){
		caption.setText(title);

	}



	public void addWidgetCenter(Widget SetContents) {

		Contents=SetContents;
		Container.setWidget(1, 1, Contents);

	}

	
	@Override
	public void onLoad(){
		setPositionFixed(FIXEDPOSITIONMODE);
		
	}
	

	public void setPositionFixed(boolean status) {
		FIXEDPOSITIONMODE = status;
		
		if(status){
			super.getElement().getStyle().setPosition(Position.FIXED);
		} else {
			super.getElement().getStyle().setPosition(Position.ABSOLUTE);
		}
		
		
	}


}
