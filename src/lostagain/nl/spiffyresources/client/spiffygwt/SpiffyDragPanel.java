package lostagain.nl.spiffyresources.client.spiffygwt;

//new----------
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
//import com.google.gwt.user.client.Element;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;
import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyFunctions;

/**
 * Essentially this is an absolute panel that can be dragged about by the mouse,
 * and have elements added too it with full muse actions.
 * <br>
 * Elements added will be wrapped in FocusPanels, if they arnt already themselves directly focuspanels.<br>
 * If they are focus panels, some handlers will be added to assist with the various drag options <br>
 * <br>
 * Additionally there is functionality designed to help edit widgets by dragging them around the panel while the panel is fixed <br>
 * This might help make game editors, such the the JAM engine uses when on debug mode <br>
 * <br>
 * 
 * @author  Thomas Wrobel
 **/

public class SpiffyDragPanel extends FocusPanel implements MouseWheelHandler,
MouseOverHandler, MouseOutHandler, FocusHandler, TouchCancelHandler, HasDeltaUpdate {

	public static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyDragPanel");
	
	/**
	 * simple interface used for defining code that fires when a css transition ends
	 * @author darkflame
	 */
	public interface SpiffyTransitionEndEvent {
		public void OnTransitionEnd();
	}

	/**
	 * The panels id, mostly just to help debug on the html
	 */
	String panelID = "ID Not Set";



	//CSS based pan animation settings
	/**
	 * This means position animation is done via css transforms
	 * this is normally smoother then the javascript method, but might not work on older browser (IE10 and below, for example)
	 * It also might not well if you plan to let the user drag the panel about (Default) rather then controlling it yourself
	 * So, in other words, be careful when turning this on!
	 */
	boolean CSSBasedTransitions = false; 
	boolean dragpanelsCSSsetup = false; //becomes true after this specific panel has the css handler and style setup correctly.
	boolean currentlyDoingACSSTransition = false; //used to prevent two CSSTransitions trying to run at once

	private Element createdStyleTag; //holds the style information for the animation. This controlls the speed and is unique per panel

	double oldduration = 100; //used to track the old duration. If a new transition is triggered we should use the old duration as the previous transition set the duration not the current one

	Timer onEndAnimation;
	//--------




	static boolean draging = false;

	static int EdgePaddingForRestrictToScreen = 20;

	// frame update time
	static final int FRAME_RATE_UPDATE_TIME = 30; // 50 is safe

	/** used to scale movement by update gap, helps smoothness **/
	long TimeOfLastUpdate =0;

	static boolean justDragged= false;

	/**
	 * We keep a record of all widgets on the panel, together with some information to help manage them.
	 * The management information specifically contains;<br>
	 * 1) Did we add a containing focuspanel to the widget?<br>
	 * 2) A List of all handlers we added to the widget.<br>
	 * Both pieces of information are needed to completely remove a widget from this panel, ready for use elsewhere.
	 */
	public HashMap<Widget,WidgetInformation> allObjectsOnPanel = new HashMap<Widget,WidgetInformation>(); //used to be FocusPanel



	AbsolutePanel Container = new AbsolutePanel();

	public int getContainerSizeX() {
		return ContainerSizeX;
	}

	public int getContainerSizeY() {
		return ContainerSizeY;
	}

	int ContainerSizeX = 0;
	int ContainerSizeY = 0;

	private Widget currentlyDraggingWidget = null;

	// for debugging
	DragPanelDatabar databar = new DragPanelDatabar();

	public AbsolutePanel dragableContents = new AbsolutePanel();
	/**
	 * relative to the item clicked on
	 */
	int DragDisX=0,DragDisY=0;
	public Widget dragOnlyThis = null;
	long dragstart = System.currentTimeMillis();
	// --
	int dragStartX = 0, dragStartY = 0;
	SpiffyOverlay dynamicOverlayContents = new SpiffyOverlay();

	private boolean editMode = false;
	private boolean hardStop = false;
	Boolean isCoasting = false;
	Boolean isMoving = false;
	private SpiffyLoadingIcon LoadingIcon;

	VerticalPanel loadingMessage = new VerticalPanel();





	// loading widget overlay
	SimplePanel loadingOverlay = new SimplePanel();
	int locationdownx = 0;
	int locationdowny = 0;
	private int Max_Height = -10000;// default (these arnt used to really check
	// the height, as the container size has to
	// be taken into account)
	private int Max_Width = -10000;// default

	private int MaxXMovement = 0;
	private int MaxYMovement = 0;
	Label messageLabel;
	private int MinXMovement = 0;

	private int MinYMovement = 0;
	private double MotionDisX = 0;
	private double MotionDisY = 0;

	Timer motionflow;
	private String OldWidgetBorder="";

	//controls panning the view when not coasting (ie, for transitions)
	float currentPanPosX = 0;
	float currentPanPosY = 0;
	float endPanPosX = 0 ;
	float endPanPosY = 0 ;
	float PanDisplacementX = 0;
	float PanDisplacementY = 0;

	/**we can in some cases shake the view a bit, if this flag is true every update of position has a random factor to it in x or y**/
	boolean randomShakeX = false;
	/**we can in some cases shake the view a bit, if this flag is true every update of position has a random factor to it in x or y**/
	boolean randomShakeY = false;
	int randomShakeDistance = 30;

	Timer panTimer;
	//------------




	// for editing stuff
	private Runnable OnFinishedEditingWidget;
	boolean progressLabelOn = false;


	//Controls fading in/out
	private enum fadeState {
		NoFade,Delay,FadeIn,FadeOut
	}
	fadeState currentFadeState = fadeState.NoFade;	
	double timeIntoCurrentFadeState = 0;
	double fadeDelay = 0 ; //the pause before the fade (if any)
	double fadeInDuration = 500; //time taken to fade in
	double fadeInStepPerMS = 1/fadeInDuration;
	double fadeOutDuration = 500; //time taken to fade out	 (previously was 500)
	double fadeOutStepPerMS = 1/fadeOutDuration;
	double currentOpacity = 1; //current opacity 0-1


	//Timer quickfade; //old method, phaseing out

	SpiffyOverlay staticOverlayContents = new SpiffyOverlay();
	SpiffyDragPanel thisDragPanel = this;
	int top = 0, left = 0;
	
	/** if internally the x dragging movement is disabled */
	private boolean XMOVEMENTDISABLED = false;
	/** if internally the y dragging movement is disabled */
	private boolean YMOVEMENTDISABLED = false;
	
	/**
	 * if a outside source requested the xmovement disabled 
	 * (this lets us ensure it _stays_ disabled if requested, if its not requested
	 * to be disabled XMOVEMENTDISABLED may changed from true/false based on things like screen size)
	 */
	private boolean XMovementDisabledRequested = false;
	
	/**
	 * if a outside source requested the ymovement disabled 
	 * (this lets us ensure it _stays_ disabled if requested, if its not requested
	 * to be disabled YMOVEMENTDISABLED may changed from true/false based on things like screen size)
	 */	
	private boolean YMovementDisabledRequested = false;

	/**
	 * requests the scene "drag" scroll to be enabled/disabled
	 * @param xMovementDisabledRequested
	 * @param yMovementDisabledRequested
	 */
	public void setScrollDisabled(boolean xMovementDisabledRequested,boolean yMovementDisabledRequested) {
		XMovementDisabledRequested = xMovementDisabledRequested;
		YMovementDisabledRequested = yMovementDisabledRequested;
		
		XMOVEMENTDISABLED = xMovementDisabledRequested;
		YMOVEMENTDISABLED = yMovementDisabledRequested;
	}

	

	int PIXAL_DRAG_MINIMUM = 15;


	public enum DragRestriction {
		None,Vert,Hor
	}
	/**
	 * when editing a widgets position, you can restrict the direction you can drag it.
	 * (Useful for editing the appearance of height, by restricting in Y)
	 */
	private DragRestriction currentEditingDragRestriction = DragRestriction.None;



	/**
	 * A special object to catch clicks/touches on the ""background"".<br>
	 *This object would be added first under everything<br>
	 *This object would go in a focus panel like everything else, but unlike the real background<br>
	 *it would be a sibling element, not a parent<br>
	 * This would ensure events can never propergate  too it - it would need to be really clicked to fire anything.<br>
	 * <br>
	 * Note; this element is not designed to be styled. Style the dragpanel directly if you want a visual background.**/

	FocusPanel backgroundWidget = new FocusPanel(); //will be sized 100% of dragpanel area at all times 
	//This object is really no different to manually added scene objects, and is more of a convience to let the user hockup stuff to 
	//a guaranteed object at the back
	//hmm...as this object blocks the whole back, do we need default events on the realback at all?

	/**a special object to catch clicks/touches on the ""background"".<br>
	 *This object is added first under everything<br>
	 *This object goes in a focus panel like everything else, but unlike the real background<br>
	 *it would be a sibling element, not a parent<br>
	 *This would ensure events can never propergate too it - it would need to be really clicked to fire anything.<br>
	 * <br>
	 * Note; this element is not designed to be styled. Style the dragpanel directly if you want a visual background.<br>
	 */
	public FocusPanel getBackgroundWidget() {
		return backgroundWidget;
	}

	/**
	 * Essentially this is an absolute panel that can be dragged about by the mouse,
	 * and have elements added too it with full mouse actions.
	 * Elements added can flag if they want browser native events disabled or not.
	 * By default, they are disabled (preventing right click actions like "copy image"
	 * but also preventing textboxs being selected to place the cursor there.
	 * 
	 * @author Thomas Wrobel	
	 * 
	 * @param panelID - the name of the dragable element this creates. Used to identify it in the DOM. Anything unique will do
	 * @param CSSMode - true to use css based animations (expiremental) false for javascript ones (works for sure)
	 */
	public SpiffyDragPanel(String panelID,boolean CSSMode) {

		super.setWidget(Container);
		super.getElement().setId(panelID); //purely to help identify it in the browsers DOM

		Container.add(dragableContents, 0, 0);
		this.panelID = panelID;
		CSSBasedTransitions = CSSMode;

		// add the static overlay
		// staticOverlayContents.setVisibility(false);
		staticOverlayContents.setMoveOverEventsEnabled(true);
		//setup the dynamic overlay
		dynamicOverlayContents.setMoveOverEventsEnabled(true);
		// has to be readded after any other add operation in order to keep it
		// on top.
		// (this could be done with a zindex, but we dont want to hardcore a
		// topmost value)
		//add some IDs to make the overlays easier to identify
		staticOverlayContents.getElement().setId("StaticOverlay");
		dynamicOverlayContents.getElement().setId("DynamicOverlay");


		Container.setSize("100%", "100%");
		dragableContents.setSize("100%", "100%");
		dragableContents.getElement().setId(panelID);

		this.addMouseDownHandler(new SpiffyDragMouseDownHandler(false, null,true));
		this.addMouseMoveHandler(new SpiffyDragMouseMoveHandler(false));
		this.addMouseUpHandler(new SpiffyDragMouseUpHandler(false,true));

		this.addMouseOutHandler(this);
		this.addMouseOverHandler(this);
		this.addFocusHandler(this);
		this.addMouseWheelHandler(this);

		this.addTouchStartHandler(new SpiffyDragTouchDownHandler(false, null,true));
		this.addTouchMoveHandler(new SpiffyDragTouchMoveHandler(false));
		this.addTouchEndHandler(new SpiffyDragTouchEndHandler(false));

		dragableContents.addDomHandler(new ContextMenuHandler() {

			@Override public void onContextMenu(ContextMenuEvent event) {


				if (!databar.isAttached()){
					event.preventDefault();
					event.stopPropagation();
				} 

			}
		}, ContextMenuEvent.getType());



		//setup background object
		backgroundWidget.getElement().setId("__DragPanelBackgroundeventCatcher__"); //temp, helps debug
		backgroundWidget.getElement().getStyle().setZIndex(0); //shouldnt be needed, but lets ensure its behind everything!
		backgroundWidget.setSize("100%", "100%"); //actually useless, needs absolute size

		//add standard backgroundobject
		this.addWidget(backgroundWidget, 0, 0,true,false,false); //false at the end as we pretend its not from a item


		//addWidget(Widget widget, int x, int y,final boolean disableFocus,final boolean TransparentToClicks, boolean fromItem)





		//		
		//		dragableContents.addDomHandler(new ClickHandler(){
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				//prevent a right click if the debug is not open
		//				if (!databar.isAttached()){
		//					event.preventDefault();
		//				} else {
		//					Log.info("databar not attached 2");
		//				}
		//			}
		//			
		//		}, ClickEvent.getType());
		//		

		// quick fadeout
		//quickfade = new Timer() {
		//	int o = 100;

		//	@Override
		//	public void run() {
		//		o = o - 10;

		//		loadingOverlay.getElement().getStyle().setOpacity(o / 100.0);

		//		if (o < 10) {
		//			Container.remove(loadingOverlay);

		//			loadingOverlay.clear();
		//			loadingMessage.clear();
		//			this.cancel();
		//		}
		//	}
		//};

		//setup a timer for the pan
		panTimer = new Timer(){


			@Override
			public void run() {

				// delta represents the difference in time between this update and the last
				long delta =  System.currentTimeMillis()-TimeOfLastUpdate; //should never be negative unless the speed of the system clock has exceeded C between updates.
				TimeOfLastUpdate = System.currentTimeMillis();

				Log.info("delta="+delta);
				//FRAME_RATE_UPDATE_TIME is the "expected" time when this timer should fire
				//however, it might lag behind, so we work out the difference between this and the delta
				float mul = (float)delta / ((float)FRAME_RATE_UPDATE_TIME);

				//This gives a multiplying factor to adjust the speed by
				//If framerate is perfect this will be 1;
				// 50 / 50 = 1
				// but if its lagging behind it might be;
				// 200/50 = 4 
				// (ie, the frame took 4 times longer to arrive, so we should move 4 times further)

				Log.info("mul=="+mul);


				currentPanPosX = currentPanPosX + (PanDisplacementX*mul);
				currentPanPosY = currentPanPosY + (PanDisplacementY*mul); //the mul multiples the requested speed per update by the number of updates that should have happened

				//All of the above means that even if FRAME_RATE_UPDATE_TIME is far too optermistic, updates should all  be at the correct place



				//Log.info(" pos is now  "+currentPanPosX+","+currentPanPosY); 

				Boolean atX = false;
				Boolean atY=false;

				int stopdistancex = (int) (Math.abs(PanDisplacementX*mul)+2); //the gap between where the pan is and when it should stop. This HAS to be bigger then the gap between updates else it can overshot the end point
				int stopdistancey = (int) (Math.abs(PanDisplacementY*mul)+2); //the gap between where the pan is and when it should stop. This HAS to be bigger then the gap between updates else it can overshot the end point

				Log.info("stopdistancex=="+stopdistancex);

				if (Math.abs(currentPanPosX-endPanPosX)<(stopdistancex)){

					currentPanPosX=endPanPosX;
					atX = true;
				}
				if (Math.abs(currentPanPosY-endPanPosY)<(stopdistancey)){

					currentPanPosY=endPanPosY;
					atY = true;
				}

				left = (int)-currentPanPosX;
				top =  (int)currentPanPosY;

				int ranX = 0;
				int ranY = 0;

				if (randomShakeX){
					ranX = (int) (Math.random()*randomShakeDistance*2)-(randomShakeDistance);
					Log.info("ranX="+ranX);
					atX = false; //we dont allow the movement to stop during the shaking
				}				
				if (randomShakeY){
					ranY = (int) (Math.random()*randomShakeDistance*2)-(randomShakeDistance);
					Log.info("ranY="+ranY);
					atY=false; //we dont allow the movement to stop during the shaking
				}

				setPositionInternalCoOrdinates(left+ranX,top+ranY);

				//cancel if at both correct X and correct Y location
				if (atX && atY){
					cancel();
					runPostScrollActions();				}

			}

		};


		//setup the motionflow timer
		//This controls all the main "grab and move" mouse coasting
		//we dont use pan as pan works absolutely, and this works relatively
		motionflow = new MotionFlowTimer();

		//ensure feedback is at top
		//clickFeedbackImage.getElement().getStyle().setZIndex(9999999);


	}


	/**
	 * Stores all the information needed for a widget on this dragpanel.
	 * Specifically this is needed to remove all added handlers if the widget is later removed.
	 * 
	 * @author darkflame	 *
	 */
	class WidgetInformation {

		/**
		 * If its in a container.
		 * If so, we dont need to know the handlers, as they will be dumped with the container on removal		 * 
		 */
		boolean isInContainerPanel = false;
		/**
		 * All the handlers this SpiffyDragPanel has added to the widget.
		 * This is here so they can be removed when the widget is detached from the panel.
		 */
		HandlerRegistration[] handlers;

		public WidgetInformation(boolean isInContainerPanel,HandlerRegistration... handlers) {
			super();
			this.isInContainerPanel = isInContainerPanel;
			this.handlers = handlers;

			Log.info("WidgetInformation  in container:"+isInContainerPanel+" with handlers:"+handlers.length);

		}
		public WidgetInformation(boolean isInContainerPanel) {
			super();
			this.isInContainerPanel = isInContainerPanel;
			this.handlers = null;

			Log.info("WidgetInformation in container:"+isInContainerPanel+" with no handlers");
		}
		/**
		 * removes the registration of all the handlers , assuming there is any.
		 * This is a conveyance method to clean up widgets removed from the dragpanel
		 */
		public void removeAllHandlers() {
			if (handlers!=null){
				for (HandlerRegistration handle : handlers) {
					handle.removeHandler();				
				}
			}
		}


	}

	//----------------------------

	/** adds a widget to the top left of the drag panel 
	 * **/
	@Override	
	public void add(Widget widgetToAddAtTopLeft){
		addWidget(widgetToAddAtTopLeft, 0, 0);
	}

	/** adds a widget to the position x,y in the drag panel
	 * the widget is first added to a container if its not a focus panel, however, to ensure clicks and drags are handled correctly 
	 * You can specify if focus is disabled or not. By default its true, which means images <br>
	 * wont be selected and textboxs wont work **/
	public void addWidget(Widget widget, int x, int y) {
		addWidget(widget, x, y,true,false);
	}

	/** adds a widget to the position x,y in the drag panel - note; this widget can't be interacted with.
	 * If you wish the user to drag this panel about its strongly recommended you dont add widgets with this
	 * because a drag cant start on them **/
	public void addUnclickableWidget(Widget widget, int x, int y,final boolean disableFocus,final boolean TransparentToClicks) {

		dragableContents.add(widget, x, y);

		if (TransparentToClicks){
			widget.getElement().getStyle().setProperty("pointerEvents", "none");
		}

		allObjectsOnPanel.put(widget, new WidgetInformation(false));

	}


	/**
	 * 
	 * @param widget
	 * @param x
	 * @param y
	 * @param disableFocus
	 * @param TransparentToClicks
	 */
	public void addWidget(Widget widget, int x, int y,final boolean disableFocus,final boolean TransparentToClicks) {		
		addWidget(widget,  x,  y, disableFocus, TransparentToClicks, true ); 		
	}


	/** adds a widget to the position x,y in the drag panel
	 * the widget is first added to a container, however, to ensure clicks and drags are handled correctly 
	 * You can specify if focus is disabled or not. By default its true, which means images <br>
	 * wont be selected and textboxs wont work 
	 * 
	 * @param widget
	 * @param x
	 * @param y
	 * @param disableFocus (stops it being selectable by the browser)
	 * @param TransparentToClicks - adds "pointer-events:none" to the style so clicks pass though (note; will prevent editing!)
	 * @param fromItem - almost always true. only false on background
	 */
	private void addWidget(Widget widget, int x, int y,final boolean disableFocus,final boolean TransparentToClicks, boolean fromItem) {

		//
		// Two possibilities;
		//
		// a) If widget is already a FocusPanel we add the handlers directly  to it.
		// We have to be extra careful to remove them though if its removed.
		//(Additional;
		//Events are handled in the order they are assigned if on the same element. The widget thus might have its own events that fire first - or later - depending
		//if they are set after this statement. )
		//
		// b) If the widget is not a FocusPanel, we create a new one and wrap it in it.
		//(Inner events always fired first, then if not blocked, bubble to parent panel)
		//

		//add a container widget if needed (ne, previously we always did)

		FocusPanel widgetToAddHandlersTo;
		boolean inContainer=false;

		if (widget instanceof FocusPanel) {

			Log.info("Widget "+widget.getElement().getId()+" is already focus panel , so adding directly to dragpanel.");


			widgetToAddHandlersTo = (FocusPanel) widget;		//Experiment. We will need to track and remove handlers manually if we use this
			// (that is, remove the handlers if the widget is removed from the panel)

			//storing the handlers will take effort though, as theres quite a lot.
			//guess make a widgetInformation object, which contains a handler array? Then a remove function that iterates over them all.
			//The information object could also just state if a object has a focus panel container or not, so we dont have to test.

			inContainer = false;
		} else {

			FocusPanel containerWidget = new FocusPanel();
			containerWidget.getElement().setId("__SDP_FocusPanelContainerWidget__");//helps debugging of where events came from. When dragging objects about when editing, the events should come from this focus panel, not the dragpanel itself
			containerWidget.add(widget);
			widgetToAddHandlersTo = containerWidget;

			inContainer=true;
		}

		//
		//we now add the handlers
		//




		//Log.info("widget added at:"+x+","+y);
		//Log.info("widget added at:"+containerWidget.getElement().getStyle().getLeft()+","+containerWidget.getElement().getStyle().getTop());

		// we have to add our own handlers to the widget to deal with dragging
		// correctly
		if (TransparentToClicks){
			widgetToAddHandlersTo.getElement().getStyle().setProperty("pointerEvents", "none");
			//Note...possibly dont add any handlers below as none will fire anyway?
			//Unless we want to re-enabled editing in future on objects like pointerEvents none?
			//hmm..
			//I guess to do that will would need a internal list of objects whos poinerEvents are disabled
			//then when cntrl is held down to edit a object, it quickly removes all the disable propertys to allow the editing (ie, ctrl click to select
			//and drag to move)
		}

		HandlerRegistration mouseDownHandlerReg = widgetToAddHandlersTo.addMouseDownHandler(new SpiffyDragMouseDownHandler(fromItem, widget, disableFocus));
		HandlerRegistration mouseMoveHandlerReg = widgetToAddHandlersTo.addMouseMoveHandler(new SpiffyDragMouseMoveHandler(fromItem));
		HandlerRegistration mouseUpHandlerReg   = widgetToAddHandlersTo.addMouseUpHandler(new SpiffyDragMouseUpHandler(fromItem, disableFocus));
		HandlerRegistration mouseOverHandlerReg = widgetToAddHandlersTo.addMouseOverHandler(this);
		HandlerRegistration mouseOutHandlerReg  = widgetToAddHandlersTo.addMouseOutHandler(this);


		HandlerRegistration touchStartHandlerReg = widgetToAddHandlersTo.addTouchStartHandler(new SpiffyDragTouchDownHandler(fromItem, widget,disableFocus));
		HandlerRegistration touchMovewnHandlerReg = widgetToAddHandlersTo.addTouchMoveHandler(new SpiffyDragTouchMoveHandler(fromItem));
		HandlerRegistration touchEndHandlerReg     = widgetToAddHandlersTo.addTouchEndHandler(new SpiffyDragTouchEndHandler(fromItem));
		HandlerRegistration touchCancelHandlerReg = widgetToAddHandlersTo.addTouchCancelHandler(this);


		HandlerRegistration focusHandlerReg = widgetToAddHandlersTo.addFocusHandler(this);
		HandlerRegistration mouseWheelHandlerReg =widgetToAddHandlersTo.addMouseWheelHandler(this);


		HandlerRegistration clickHandlerReg = widgetToAddHandlersTo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Log.info("onclick from container / preventing propergating");
				// This will stop the event from being
				// propagated
				event.stopPropagation();

				if (disableFocus){
					event.preventDefault();
				}
			}

		});

		dragableContents.add(widgetToAddHandlersTo, x, y);


		//
		//Phwee....look at all these handlers we have to remember!
		//
		WidgetInformation widgetInfo = new WidgetInformation(inContainer,
				mouseDownHandlerReg,
				mouseMoveHandlerReg,
				mouseUpHandlerReg,
				mouseOverHandlerReg,
				mouseOutHandlerReg,
				touchStartHandlerReg,
				touchMovewnHandlerReg,
				touchEndHandlerReg,
				touchCancelHandlerReg,
				focusHandlerReg,
				mouseWheelHandlerReg,
				clickHandlerReg
				);


		allObjectsOnPanel.put(widgetToAddHandlersTo,widgetInfo);

	}
	@Override
	/** for this panel to work, widgets go on a subpanel within it. We dont want this subpanel removed, hence the clear command
	 * just redirects to the clearAllWidgets command, that removes the inner widgets only**/
	public void clear(){
		clearAllWidgets();
	}
	public void clearAllWidgets(){


		for (Widget widgetOnPanel : allObjectsOnPanel.keySet()) {

			widgetOnPanel.removeFromParent();

			//remove handlers too if needed
			WidgetInformation info = allObjectsOnPanel.get(widgetOnPanel);
			info.removeAllHandlers();

		}

		allObjectsOnPanel.clear();

	}

	public void clearJustDraggedFlag() {
		Log.info("just draged set to false.");
		justDragged=false;
	}



	private void displacebyInternalCoOrdinates(int disX, int disY) {

		//TEST: replaced with ensureXisSafe		
		// make sure X/Y isn't outside boundary's
		//if ((left > -MinXMovement) && (!XMOVEMENTDISABLED)) {
		//	left = -MinXMovement;
		//}		;

		if ((top > -MinYMovement) && (!YMOVEMENTDISABLED)) {
			top = -MinYMovement;
		}
		;

		// stop movement if disabled
		if (XMOVEMENTDISABLED) {
			// Log.info("______movement disabled in X");
			disX = 0; // no displacement
		}
		if (YMOVEMENTDISABLED) {
			// Log.info("______movement disabled in Y");
			disY = 0;// no displacement
		}

		// if both disabled then we just stop

		// stop movement at bottom right limits
		if ((top < -MaxYMovement) && (!YMOVEMENTDISABLED)) {
			// Log.info("hit height:" + Max_Height);
			top = -MaxYMovement;
		}
		;

		//TEST: replaced with ensureXisSafe
		//if ((left < -MaxXMovement) && (!XMOVEMENTDISABLED)) {
		// Log.info("hit width" + Max_Width);
		//left = -MaxXMovement;
		//}		;





		// get new co-ordinates based on old ones
		left = left + disX;
		top = top + disY;

		
		if (!XMOVEMENTDISABLED) {
			left = (int)ensureXisSafe(left);
		}
		//new; check y as well (we forgot earlier :P)
		if (!YMOVEMENTDISABLED) {
			top = (int)ensureYisSafe(top);
		}
		

		// Log.info("set co-ordinates to " + left + " " + top);
		setPositionInternalCoOrdinates(left, top);

	}

	public void DisplayDebugBar(boolean b) {

		if (b) {

			Log.info("__________________>>>>>>>>>>>  adding debug ");

			Container.add(databar, 0, 0);

		} else {
			Container.remove(databar);
		}

	}


	/**
	 * sets the edit mode on or off Edit mode allows the objects on this
	 * dragpanel to be moved around. When a object is released from being
	 * dragged "OnFinishedEditingWidget" is triggered. This can be set from the
	 * "setOnFinishedEditingWidget" function
	 * 
	 * It also only edits the specific widget, or null if you want all editable at once
	 **/

	public void EditMode(boolean state,Widget EditOnlyThis) {

		Log.info("setting edit mode:"+ Boolean.toString(state));

		if (state) {

			editMode = true;

			databar.showRelativeMousePositionLabel();

			//make sure EditOnlyThis is correct widget.
			//Sometimes it will be the parent sometimes not
			//This is because the widget may or may not be in a focuspanel THEN this dragpanel
			if (dragableContents.getWidgetIndex(EditOnlyThis.getParent())>-1){
				EditOnlyThis=EditOnlyThis.getParent();
			}

			dragOnlyThis = EditOnlyThis;

			//currentlyDraggingWidget = dragOnlyThis;
		} else {
			editMode = false;
			dragOnlyThis= null;


			databar.hideRelativeMousePositionLabel();

			if (currentlyDraggingWidget!=null){

				cancelDraggingAndWidgetAndReleaseCapture();
				/*
				Log.info("Releasing capture on "+currentlyDraggingWidget.getParent().getClass()+"which contains a widget with ID:"+currentlyDraggingWidget.getElement().getId());
				DOM.releaseCapture(currentlyDraggingWidget.getParent().getElement()); //When dragging a widget its events are captured. This means mouse or touch actions wont work elsewhere.

				//unhighlight
				currentlyDraggingWidget.getElement().getStyle().setBorderColor(OldWidgetBorder);
				currentlyDraggingWidget = null;*/
			}

		}
	}

	public int getCurrentPanelAbsoluteX(){
		return dragableContents.getAbsoluteLeft();
	}

	public int getCurrentPanelAbsoluteY(){
		return dragableContents.getAbsoluteTop();
	}

	public long getLoadingTime(){
		return LoadingIcon.currentTime;
	}


	public void setEditingDragRestriction(DragRestriction restriction) {
		Log.info("drag restriction set to:"+restriction.name());
		currentEditingDragRestriction = restriction;

	}


	/** gets the LEFT setting on the CSS **/
	public int getWidgetLeft(Widget widget){

		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
		FocusPanel containerWidget = (FocusPanel) widget.getParent();
		//get the left
		String leftAsString = containerWidget.getElement().getStyle().getLeft();
		leftAsString = SpiffyFunctions.StripCSSfromNumber(leftAsString);

		//convert to number
		return Integer.parseInt(leftAsString); 

	}



	/** gets the TOP setting on the CSS **/
	public int getWidgetTop(Widget widget){

		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
		FocusPanel containerWidget = (FocusPanel) widget.getParent();
		//get the top
		String topAsString = containerWidget.getElement().getStyle().getTop();
		topAsString = SpiffyFunctions.StripCSSfromNumber(topAsString);
		//convert to number
		return Integer.parseInt(topAsString); 

	}



	/**
	 * Tests if the either the widgets parent or the widget are on the panel
	 * @param widget
	 * @return
	 */
	public boolean isOnPanel(Widget widget){

		
		//first we try getting parent, as all widgets with mouse handleing added to the panel should be contained in a containerWidget (a FocusPanel)

		int wi = dragableContents.getWidgetIndex(widget.getParent());		
		//if its greater then -1, then its contained on this panel
		if (wi>-1){
			return true;
		}

		//then we try the widget itself, for things that are attached directly (typically things which dont use click events)
		wi = dragableContents.getWidgetIndex(widget);		
		//if its greater then -1, then its contained on this panel
		if (wi>-1){
			return true;
		}


		//else it isnt
		return false;


	}

	private void motionflow() {

		// Log.info("set motionflow");

		int displacementX = left - locationdownx;
		int displacementY = top - locationdowny;
		long period = System.currentTimeMillis() - dragstart;
		// System.out.print("\n drag displacement time:"+period);

		// displacement per unit of time;
		MotionDisX = ((double) displacementX / (double) period) * 50;
		MotionDisY = ((double) displacementY / (double) period) * 50;

		// Log.info("\n drag displacement:" + MotionDisX + " " + MotionDisY);

		// motionflow.cancel();
		// only start if not already running
		if (!(isCoasting)) {
			motionflow.scheduleRepeating(FRAME_RATE_UPDATE_TIME);
		} else {
			Log.info("\n already coasting, so no new motion flow needed!");
		}

	}

	@Override
	public void onFocus(FocusEvent event) {
		event.preventDefault();
	}

	@Override
	public void onLoad() {

		ContainerSizeX = Container.getOffsetWidth();
		ContainerSizeY = Container.getOffsetHeight();

		Log.info("_containersizeX = " + ContainerSizeX);
		Log.info("_containersizeY = " + ContainerSizeY);

		MaxXMovement = Max_Width - ContainerSizeX;
		MaxYMovement = Max_Height - ContainerSizeY;

		super.onLoad();

	}

	/**
	 * Returns true if the widget specified was put into a container FocusPanel before being placed on this dragpanel.
	 * (this is done if the widget isn't already a focus panel)
	 * 
	 * @param wiget
	 */
	public boolean isWidgetInContainer(Widget widget){

		WidgetInformation widgetInformation = allObjectsOnPanel.get(widget);
		if (widgetInformation==null){
			Log.info("(widget to test not on dragpanel)");
			return false;
		}

		return widgetInformation.isInContainerPanel;


	}

	private void onMouseOrTouchDown(int x, int y, int dx, int dy,boolean fromItem,Widget sourceWidget) {

		//Log.info("just draged set to false");
		justDragged=false; //false until movement starts

		// test if anything is under mouse
		//Log.info("drag start a " + x + " y=" + y);
		//Log.info("mouse down..");

		dragStartX = x - left;
		dragStartY = y - top;

		draging = true;

		if (fromItem) {

			//probably should cancel any click actions the item has if we enter a drag?? (how?)
			//might have to wait for a mouseup to tell the difference

			Log.info("MouseDown came from item "+sourceWidget.getElement().getId());

			// if edit mode on, we start dragging this one about!
			if ((editMode) && (sourceWidget != null)) {

				stuffToDoWhenTheMouseIsDownWhileEditingStuff(dx,dy,
						sourceWidget);
			}
		}

		locationdownx = left;
		locationdowny = top;
		dragstart = System.currentTimeMillis();

	}

	private void onMouseOrTouchMove(int x, int y) {


		//update debug
		databar.setCurrentMousePositionLabel(x - left, y - top);
		databar.setCurrentDragState(draging,dragStartX,dragStartY);
		//--


		if (editMode) {

			int leftWid = this.dragOnlyThis.getAbsoluteLeft();
			int topWid  = this.dragOnlyThis.getAbsoluteTop();

			int rx = (x) - leftWid; // - left
			int ry = (y)  - topWid; // - top


			databar.setCurrentRelativeMousePositionLabel(rx, ry);


			stuffToDoWhenTheMouseMoveWhileEditingStuff(x,y);
			return;
		}

		if (draging == true) {
			
			if (XMOVEMENTDISABLED && YMOVEMENTDISABLED){
				//We do no action as movement is fully disabled
				//(We also dont cancel any current scene pans))
				return;
			}


			Log.info("_____Move while held down Event Detected:"+x+","+y);

			//diff in pos of mouse 
			int dfx = Math.abs((x - left)-dragStartX);
			int dfy = Math.abs((y - top) -dragStartY);

			//if we have moved a bit, then its a real drag so we trigger this
			if ((dfx+dfy)>PIXAL_DRAG_MINIMUM){


				if (!justDragged){
					justDragged=true; //used to determine for other code if this mousemove up was from a new drag
					Log.info("___________started to drag panel:"+justDragged+" (capturing)");
					
					//stop any active css animation
					stopCurrentCSSAnimation();

					//---------------------------------------------------------------------------------------------------------------------------------
					//When we start dragging, we tell the browser to just focus on ourselves (that is, this dragpanel widget), and ignore mouse/touch events on other elements.
					//Its important to release capture on MouseUp,MouseOut, and TouchEnd so the dragging doesn't get "stuck"
					DOM.setCapture(SpiffyDragPanel.this.getElement());//ensure capture. we only want to drag and do nothing else!
					//----------------------------------------------------------------------------------------------------------------------------------

					Log.info("difference in x since dragstart:"+dfx);
					Log.info("difference in y since dragstart:"+dfy);
				}

			};

			if (!XMOVEMENTDISABLED) {
				left = x - dragStartX;
			};
			if (!YMOVEMENTDISABLED) {
				top = y - dragStartY;
			};

			// make sure X/Y isn't outside boundaries
			//TEMP: REMOVED, now dealt with by special check function
			//if ((left > 0) && (!XMOVEMENTDISABLED)) {
			//	Log.info("___________left outside range"+left);
			//		left = 0;
			//};

			if ((top > 0) && (!YMOVEMENTDISABLED)) {
				top = 0;
			};


			// make sure X/Y isn't outside specified boundaries

			//TEMP: REMOVED, now dealt with by special check function			
			//if ((left < -MaxXMovement) && (!XMOVEMENTDISABLED)) {

			////	Log.info("____________left outside range 2");
			//	left = -MaxXMovement;
			//}
			if (!XMOVEMENTDISABLED) {
				left = (int)ensureXisSafe(left);
			}

			//TEMP: REMOVED, now dealt with by special check function		
			//if ((top < -MaxYMovement) && (!YMOVEMENTDISABLED)) {
			//	top = -MaxYMovement;
			//};
			if (!YMOVEMENTDISABLED) {
				top = (int)ensureYisSafe(top);
			}

			// Log.info("setting co-ordinates");
			setPositionInternalCoOrdinates(left, top);

			// int x = event.getRelativeX(Container.getElement());
			// int y = event.getRelativeY(Container.getElement());

			// left = x - dragStartX;
			// top = y - dragStartY;

			// Log.info("setting co-ordinates");
			// Container.setWidgetPosition(dragableContents, left, top);

			// setPositionInternalCoOrdinates(left, top);
		}
	}

	private void onMouseOrTouchUp() {
		//Log.info("mouse up..");

		if (draging == true) {
			draging = false;

			Log.info("___________stopped drag");
			//justDragged=false;
			// motion flow
			motionflow();
		}

		//--
		DOM.releaseCapture(SpiffyDragPanel.this.getElement());//ensure release of any captures on the dragpanel panel . This means the drag wont get stuck on, even if the mouse moves outside the bounds of outselves
		//note; we might still be dragging a widget though in which case it does nothing

		//update debug
		databar.setCurrentDragState(draging,dragStartX,dragStartY);
		//--

		// regardless of where it came from, on mouseup, we stop dragging things
		if (editMode) {
			stuffToDoWhenTheMouseUpWhileEditingStuff();
		}


	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.preventDefault();
		// Log.info("mouse leaving");

	//	Log.info("onMouseOut");

		if (draging == true) {
			draging = false;
			//justDragged=false;
			// motion flow
			motionflow();
		}		

		//--
		DOM.releaseCapture(SpiffyDragPanel.this.getElement());//ensure release of any captures on the dragpanel panel . This means the drag wont get stuck on, even if the mouse moves outside the bounds of outselves
		//note; we might still be dragging a widget though in which case it does nothing

		//update drag debug
		databar.setCurrentDragState(draging,dragStartX,dragStartY);
		//--

		//if dragging object, ensure we update its position
		//normally this is dealt with the onMouseMove, but a quick movement might have moved the mouse outside the object
		//Aside from the obvious problem of the object not moving, it would also prevent the mouseup event being detected, and thus the widget dragging would never end!
		if (editMode && currentlyDraggingWidget!=null) {
			Log.info("onMouseOut while editing, thus updating position of widget we are moving to new mouse location ");

			int x = event.getRelativeX(Container.getElement());
			int y = event.getRelativeY(Container.getElement());
			int leftWid = this.dragOnlyThis.getAbsoluteLeft();
			int topWid  = this.dragOnlyThis.getAbsoluteTop();			
			int rx = (x) - leftWid; // - left
			int ry = (y)  - topWid; // - top
			databar.setCurrentRelativeMousePositionLabel(rx, ry);
			stuffToDoWhenTheMouseMoveWhileEditingStuff(x,y);
			return;
		}




	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		draging = false;

	//	Log.info("onMouseOver");
		//justDragged=false;
		event.preventDefault();

	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		event.preventDefault();
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		event.preventDefault();
		Log.info("touch drag canceled");

		onMouseOrTouchUp();

	}

	///needs updating for things without focus parent
	public boolean removeWidget(Widget widget){

		Log.info("removing from drag panel!!");

		//ensure it has parent else the next line will crash
		//else we return fail
		if (widget==null){
			Log.severe("attempted to remove non-existant widget");
			return false;
		}

		//
		//get information (this tells us if we have to remove a container or not, a well as giving us the handlers to remove)
		//
		WidgetInformation info = allObjectsOnPanel.get(widget);

		if (info==null){
			Log.severe("attempted to remove widget not registered on this panel. index is:"+this.dragableContents.getWidgetIndex(widget));			
			return false;
		}

		boolean isContained = info.isInContainerPanel;

		if (isContained){
			//its in a focus panel container we added, so we remove its parent
			boolean success =  dragableContents.remove(widget.getParent());			
			allObjectsOnPanel.remove(widget.getParent());
			Log.info("_removing(p)_"+success+" which was a "+widget.getParent().getClass().getName()+" (if thats not a focus panel, then theres probably a mistake, this log only should trigger when removing widgets automatically given focus panel parents");
			//------
			//(no need to remove handlers, as they are applied to our focus panel, which we are dumping all reference too anyway)
			return success;

		}  else {
			//its attached directly 			
			boolean success =  dragableContents.remove(widget);
			allObjectsOnPanel.remove(widget);
			Log.info("_removing object from spiffyDragPanel which currently has "+dragableContents.getWidgetCount());

			Log.info("_info.handlers "+info.handlers);

			//------
			//remove all handlers if we have any
			info.removeAllHandlers();

			//---

			return success;
		}



		//old;
		/*
		//First we try to remove the parent and if that fails we try to remove directly
		//We do this because the widget might be in a container focus panel

		//first test if parent is on it and remove it if thats the case
		if (dragableContents.getWidgetIndex(widget.getParent())>-1){

			boolean success =  dragableContents.remove(widget.getParent());			
			allObjectsOnPanel.remove(widget.getParent());

			Log.info("_removing(p)_"+success+" which was a "+widget.getParent().getClass().getName()+" (if thats not a focus panel, then theres probably a mistake, this log only should trigger when removing widgets automatically given focus panel parents");

			return success;

		}


		//first test if parent is on it and remove it if thats the case
		if (dragableContents.getWidgetIndex(widget)>-1){
			Log.info("_removing object from spiffyDragPanel which currently has "+dragableContents.getWidgetCount());

			boolean success =  dragableContents.remove(widget);
			allObjectsOnPanel.remove(widget);

			Log.info("_removing_"+success);

			return success;
		}
		 */
		//todo:remove handlers too if any

		//return false;
	}

	/** sets the overall container background color **/
	public void setBackgroundColour(String css) {


		Log.info("css = "+css);
		Container.getElement().getStyle().setBackgroundColor(css);


	}

	/** sets the css background **/
	public void setDraggableBackground(String url) {
		Log.info("url(\"" + url + "\")");
		dragableContents.getElement().getStyle()
		.setBackgroundImage("url(\"" + url + "\")");

	}
	/** sets the css background repeat
	 * valid values
	 * "repeat-x"
	 * "repeat-y"
	 * "no repeat"
	 * "repeat" (both x and y)**/
	public void setDraggableBackgroundRepeat(String repeatmode ) {
		Log.info("repeatmode =" + repeatmode + "");

		dragableContents.getElement().getStyle().setProperty("backgroundRepeat", repeatmode);


	}
	/**
	 * change the css of the staticOverlayContents if the CSS string is called
	 * "OFF" it will disable the contents
	 **/
	public void setDynamicOverlayCSS(String css) {

		Log.info("adding overlay:" + css);

		if (css != null) {
			if (css.equalsIgnoreCase("OFF")) {

				// staticOverlayContents.setVisibility(false);
				dragableContents.remove(dynamicOverlayContents);

			} else {
				if (!dragableContents.isAttached()) {

					Log.info("attaching dynamic overlay:");
					dragableContents.add(dynamicOverlayContents, 0, 0);
				}
				// staticOverlayContents.setVisibility(true);
				dynamicOverlayContents.setCSS(css);
			}
		}
	}

	public void setInternalSize(int x, int y) {

		dragableContents.setSize(x + "px", y + "px");

		//and the background click catcher!
		backgroundWidget.setSize(x + "px", y + "px");


		// if the container size is bigger then the contents, then centre and
		// disable movement in that direction
		updateDragableSize();
		/*
		 * Max_Height = y; Log.info("Max_Height= " + Max_Height + "\"");
		 * 
		 * Max_Width = x; Log.info("Max_Width= " + Max_Width + "\"");
		 * 
		 * ContainerSizeX = Container.getOffsetWidth(); ContainerSizeY =
		 * Container.getOffsetHeight();
		 * 
		 * Log.info("_containersizeX = " + ContainerSizeX);
		 * Log.info("_containersizeY = " + ContainerSizeY);
		 * 
		 * MaxXMovement = Max_Width - ContainerSizeX; MaxYMovement = Max_Height
		 * - ContainerSizeY;
		 */

	}

	/** sets the loading overlay background **/
	public void setLoadingBackground(String ImageURL) {
		if (ImageURL.length()>2){
			loadingOverlay.getElement().getStyle().setBackgroundImage(ImageURL);
		}
	}

	

	/** sets the loading overlay on/off **/
	public void setLoading(boolean status, String Message,  AbstractImagePrototype[] optionalLoadingImages) {

		messageLabel = new Label(Message);

		if (status) {

			Container.add(loadingOverlay, 0, 0);

			//ensure loading overlay is cleaned
			if (loadingMessage.isAttached()){
				Log.warning("This is the second time ever setLoading has been triggered on this SpiffyDragPanel. Did you mean to do that?");
				loadingOverlay.clear();
			}

			loadingOverlay.setStylePrimaryName("loadingOverlay");
			loadingOverlay.getElement().getStyle().setBackgroundColor("#000");
			loadingOverlay.getElement().getStyle().setZIndex(99999);
			loadingOverlay.getElement().getStyle().setColor("#FFF");

			loadingOverlay.setSize("100%", "100%");


			if (optionalLoadingImages!=null){
				LoadingIcon = new SpiffyLoadingIcon(false,optionalLoadingImages);
			} else {
				LoadingIcon = new SpiffyLoadingIcon(false);
			}

			LoadingIcon.setFillcolor("LoadingFillStyle");
			LoadingIcon.setStrokecolor("LoadingFillStyle");
			LoadingIcon.addStyleNameToLoadingMessage("LoadingMessageStyle");

			LoadingIcon.setProgressLabelVisible(true);

			//CLEAR CURRENT CONTENTS FIRST IN CASE THIS IS RUN FIRST
			loadingMessage.clear();

			loadingMessage.getElement().getStyle().setColor("#FFF");
			loadingMessage.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			loadingMessage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			loadingMessage.setSpacing(7);
			loadingMessage.add(messageLabel);
			loadingMessage.add(LoadingIcon);

			loadingMessage.setSize("100%", "100%");
			loadingMessage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			loadingOverlay.add(loadingMessage);

		} else {

			LoadingIcon.stopAnimation();
			LoadingIcon.clear();

			//quickfade.scheduleRepeating(50);
			startFadeIn();

		}

	}



	public void setLoadingCounter(boolean status) {
		// set loading data
		progressLabelOn = status;

		LoadingIcon.setProgressLabelVisible(progressLabelOn);

	}	


	public void setLoadingTotal(int T) {
		LoadingIcon.setTotalUnits(T);
	}
	
	public void setLoadingProgress(int T) {
		LoadingIcon.setProgressTotal(T);
	}


	public void setLoadingMessages(String[] messages) {
		LoadingIcon.setLoadingMessages(messages);
	}

	public void setLoadingIconDefaultMessage(String string){

		LoadingIcon.setDefaultLoadingMessage(string);

	}


	public void setMouseOverEventsOnOverlays(boolean status) {

		staticOverlayContents.setMoveOverEventsEnabled(status);
		dynamicOverlayContents.setMoveOverEventsEnabled(status);
		Log.info("set overlay mouse movement event sensing to:" + status);
	}
	/**
	 * sets the movement limits for the panning imagine it as the small box
	 * inside the bigger box...the user wont be able to move outside the small
	 * box, but might see a little outside it when the movement bounces at the
	 * edges
	 * **/
	public void setMovementLimits(int StartX, int StartY, int endX, int endY) {


		// set bottom right
		Max_Height = endY;
		Log.info("Max_Height= " + Max_Height + "");

		Max_Width = endX;
		Log.info("Max_Width= " + Max_Width + "");

		ContainerSizeX = Container.getOffsetWidth();
		ContainerSizeY = Container.getOffsetHeight();

		Log.info("_containersizeX = " + ContainerSizeX);
		Log.info("_containersizeY = " + ContainerSizeY);

		MaxXMovement = Max_Width - ContainerSizeX;
		MaxYMovement = Max_Height - ContainerSizeY;

		// set top left

		MinXMovement = StartX;
		MinYMovement = StartY;

		updateDragableSize();
	}

	public void setOnFinishedEditingWidget(Runnable OnFinishedEditingWidget) {

		this.OnFinishedEditingWidget = OnFinishedEditingWidget;

	}

	private void setPositionInternalCoOrdinates(int setX, int setY) {



		//Move the layer
		Container.setWidgetPosition(dragableContents, setX, setY);

		// Log.info("coordinates set");
		databar.setCurrentPositionLabel(-setX, -setY);

	}

	/**
	 * Disables/stops the current CSS movement
	 * but does not set the sceneposition to match is end.
	 * This is because if we are interrupting a animation to set a position we dont want to set it here and then set it to the new position
	 */
	private void stopCssMovement() {
		//disable current css movement with bits of;

		currentPanPosX = endPanPosX;
		currentPanPosY = endPanPosY;
		Log.info("_____________setting currentpan position too:"+currentPanPosX+","+currentPanPosY);	

		dragableContents.getElement().getStyle().setProperty("transform", "translate(0px, 0px)");
		dragableContents.getElement().getStyle().setProperty("transitionDuration", "0ms");

		currentlyDoingACSSTransition = false; //allows a new transition to be set to run

	}

	/**
	 * change the css of the staticOverlayContents if the CSS string is called
	 * "OFF" it will disable the contents
	 **/
	public void setStaticOverlayCSS(String css) {

		Log.info("adding overlay:" + css);

		if (css != null) {
			if (css.equalsIgnoreCase("OFF")) {

				// staticOverlayContents.setVisibility(false);
				Container.remove(staticOverlayContents);

			} else {

				// add it if not attached
				if (!staticOverlayContents.isAttached()) {
					Container.add(staticOverlayContents, 0, 0);
				}
				// staticOverlayContents.setVisibility(true);
				staticOverlayContents.setCSS(css);
			}
		}
	}

	/** sets the view to the bottom left position **/

	public void setViewToBottomLeft() {
		//Stop any CSS movement currently playing if we are using css based transitions
		if (CSSBasedTransitions && currentlyDoingACSSTransition){
			stopCssMovement();
		}
		//	resetCSSPosition();
		Log.info("::::::::::::setting view to bottom left");

		if (!XMOVEMENTDISABLED) {
			left = -MinXMovement;
		}
		if (!YMOVEMENTDISABLED) {
			top = -MaxYMovement;
		}
		setPositionInternalCoOrdinates(left, top);
	}




	public void setViewToCenter(Boolean overrideMOVEMENTDISABLED) {
		//Stop any CSS movement currently playing if we are using css based transitions
		if (CSSBasedTransitions && currentlyDoingACSSTransition){
			stopCssMovement();
		}

		Log.info("::::::::::::setting view to center x/y");

		//if allowed too, we set the position to the center of our movement limits
		if (!XMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
			left = -(MinXMovement + MaxXMovement) / 2; 
			
			Log.info("::::::::::::-("+MinXMovement+" + "+MaxXMovement+")/ 2");
		}
		if (!YMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
			top = -(MaxYMovement + MinYMovement) / 2;

		}

		Log.info("::::::::::::setting view to " + left + ", " + top);

		setPositionInternalCoOrdinates(left, top);
	}


	public void shakeFor(int durationms, int distance)
	{

		randomShakeX = true;
		randomShakeY = true;

		randomShakeDistance = distance;

		//final float endtime = System.currentTimeMillis()+durationms;
		Timer endShake = new Timer(){			

			@Override
			public void run() {		
				randomShakeX = false;
				randomShakeY = false;
				Log.info(" stopping shake::: setting back to  "+left+","+top);
				//ensure scene is at location without random shake factors
				setPositionInternalCoOrdinates(left,top);

			}			
		};
		Log.info(" durationms: "+durationms);
		endShake.schedule(durationms);

		//ensure Timer panTimer is running
		if (!panTimer.isRunning()){

			//get top left position
			currentPanPosX = -this.getCurrentPanelAbsoluteX();
			currentPanPosY = this.getCurrentPanelAbsoluteY();

			//convert the requested center position to top left position
			endPanPosX = currentPanPosX;
			endPanPosY = currentPanPosY; //(if the timer isnt running we are just moving on the spot, so we start and end at the same placE)

			PanDisplacementX = 0;//values dont matter, as the randomness added to them automaticaly is the "real" movement for the shake
			PanDisplacementY = 0;

			TimeOfLastUpdate = System.currentTimeMillis();
			panTimer.scheduleRepeating(FRAME_RATE_UPDATE_TIME);

		} else {
			Log.info(" timer already running ");
		}

	}

	public void setViewToPos(int X, int Y) {
		setViewToPos(X, Y,false);
	}

	public enum animationSpeedInterpretationMode {
		/**
		 * The stepsorpixels variable reflects the pixels per step the movement works at
		 * speed = (stepsOrPixels / FRAME_RATE_UPDATE_TIME); //the speed is the steps per framerate divided by the framerate
		 *   duration = distance / speed;
		 *   Note; Description may not be accurate here, sorry :-/ 
		 * 
		 */
		FixedSpeedModeOn,
		/**
		 * stepsOrPixels variable reflects total number of update steps. <br>
		 * thus it takes a fixed time regardless of distance (so the speed varies based on distance)				
		 * duration = stepsOrPixels * FRAME_RATE_UPDATE_TIME;		 
		 * Note; Description may not be accurate here, sorry :-/ 
		 * 
		 */
		FixedSpeedModeOff,

		/**
		 * The stepsorpixels reflects duration directly. (so really it isnt steps or pixels)
		 */
		DirectDurationMode,

	}

	/** animates a transition to the specified position 
	 * 	 
	 *  
	 * @param X - center pos x
	 * @param Y
	 * @param overrideMOVEMENTDISABLED - ignore movement restrictions on scene
	 * @param steps - if fixedspeedmode is false number of steps animation takes (10f)
	 *        steps - if fixedspeedmode is true its the pixel speed it moves at (both x/y is equal) 
	 * @return 
	 */
	public Simple2DPoint scrollViewToPos(int X, int Y,Boolean overrideMOVEMENTDISABLED,int stepsOrPixels, animationSpeedInterpretationMode speedmode){ //boolean fixedSpeedMode){

		//stop any current movement
		motionflow.cancel();

		final int xreq = X;
		final int yreq= Y;


		X = -X + (ContainerSizeX / 2); //convert to topleft
		X = (int) ensureXisSafe(X); //check its in limits
		endPanPosX = -X; //invert

		Y = -(Y - (ContainerSizeY / 2)); //convert to topleft based
		Y = (int) ensureYisSafe(Y);//check its in limits
		endPanPosY = Y;

		//We convert the top back to center-based positioning and store in correctedTargetPositionToReturn later
		
		int correctedX = (int) ((ContainerSizeX / 2) + endPanPosX);//((ContainerSizeX / 2) - endPanPosX);
		int correctedY = (int) ((ContainerSizeY / 2) - endPanPosY);		
		Simple2DPoint correctedTargetPosition = new Simple2DPoint(correctedX,correctedY);
		//--------------------------------------
		//-------------


		//convert the requested center position to top left position

		//	endPanPosX = X-(this.ContainerSizeX/2); //note; this is invert of setto
		//	endPanPosY = -(Y-(this.ContainerSizeY/2));



		Log.info(" top left destination requested: "+endPanPosX+","+endPanPosY+" (stepsOrPixels="+stepsOrPixels+")"); //y=463
		Log.info(" x limits: "+(-MinXMovement)+" <> "+(-MaxXMovement));
		Log.info(" y limits: "+(-MinYMovement)+" <> "+(-MaxYMovement)); //0 - -27

		// make sure X/Y isn't outside boundaries

		//ensure inside x:
		//		endPanPosX = ensureXisSafe(endPanPosX);

		//ensure inside y:
		//	endPanPosY = ensureYisSafe(endPanPosY);

		//now we have the start and end position we animate using either the css method or the javascript one, depending on setting


		if (CSSBasedTransitions){
			
			if (currentlyDoingACSSTransition){
				//abort existing then re-run after update?
				//lots can be optimised here, for example, by not testing for currentlyDoingACSSTransition all those later times
				Log.info("New pan requested while old is running. Stopping old");
				stopCurrentCSSAnimation();
				
				/*
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {				
					@Override
					public void execute() {
						Log.info("new pan requested to "+xreq+","+yreq+", old should be stopped by now");
						
						scrollViewToPos(xreq,yreq, overrideMOVEMENTDISABLED,stepsOrPixels,speedmode);
						
					}
				});*/
				Log.info("new pan requested to "+xreq+","+yreq+", old should be stopped by now");
				
				//scrollViewToPos(xreq,yreq, overrideMOVEMENTDISABLED,stepsOrPixels,speedmode);
				
				//return correctedTargetPosition;
			}
			//int temp_currentPanPosX = -this.getCurrentPanelAbsoluteX();
			//int temp_currentPanPosY = this.getCurrentPanelAbsoluteY();

			//this needs to as close as possible match the other panning method in terms of timing
			//To do this we use a secret technique called 	Maths.*
			//*do not attempt maths without training and special supervision. 

			if (speedmode == animationSpeedInterpretationMode.FixedSpeedModeOff){


				//it takes a fixed time regardless of distance (so the speed varies based on distance)				
				int duration = stepsOrPixels * FRAME_RATE_UPDATE_TIME;

				startCSSAnimation(endPanPosX,endPanPosY,duration);
				//TODO:  tidy this function up, removing redunancys
			} else if (speedmode == animationSpeedInterpretationMode.DirectDurationMode){

				
				//if theres an existing animation playing we just set the new destination and dont change anything else
				//the old duration is kept.
				if (currentlyDoingACSSTransition){

					Log.info("pan duration set by old direct duration:"+oldduration);
					startCSSAnimation(endPanPosX,endPanPosY,stepsOrPixels);
				} else {		

					Log.info("pan duration set by direct duration:"+stepsOrPixels);
					
					currentPanPosX = -this.getCurrentPanelAbsoluteX();
					currentPanPosY = this.getCurrentPanelAbsoluteY();

					startCSSAnimation(endPanPosX,endPanPosY,stepsOrPixels);
				}

			} else if (speedmode == animationSpeedInterpretationMode.FixedSpeedModeOn) {

				//it travels at a fixed speed regardless of distance (so the duration will vary based on distance)
				//------------------------

				//if theres an existing animation playing we just set the new destination and dont change anything else
				//the old duration is kept.
				if (!currentlyDoingACSSTransition) {
					//if its a new animation, we have to get the current location and work out a new duration based on the new distance
					currentPanPosX = -this.getCurrentPanelAbsoluteX();
					currentPanPosY = this.getCurrentPanelAbsoluteY();
				}	
				
					//first we get the distance
					Log.info("current positionX:"+currentPanPosX);
					Log.info("current positionY:"+currentPanPosY);

					float enddifx = endPanPosX-currentPanPosX; //NOTE: If a pan is currently running this will be inaccurate				
					float enddify = endPanPosY-currentPanPosY; //Because currentPanPos is set to the old values still. However, duration wont be set either making this whole calc have no effect. Only the destination is changed

					double distance = Math.hypot(enddifx, enddify); //inside its just pythagoras.

					//75 pixels in 30ms = 2.5 pixels per ms
					//30 pixels in 30ms = 1 pixel per ms
					//15 pixels in 30ms = 0.5 pixels per ms 
					float speed = ((float)stepsOrPixels / (float)FRAME_RATE_UPDATE_TIME); //the speed is the steps per framerate divided by the framerate

					//this gives pixels per unit of time			 
					// duration = distance / speed
					double duration = distance / speed;
					Log.info("__duration of new pan = distance / speed = "+distance+"/"+speed+" = "+duration);


					startCSSAnimation(endPanPosX,endPanPosY,duration);
				//} else {
					
				//	startCSSAnimation(endPanPosX,endPanPosY,oldduration); //used to use oldduration
					
				//}



			}



			//
			//int duration = FRAME_RATE_UPDATE_TIME*stepsOrPixels; //duration needs to either be proportional to distance or fixed


			//

		} else {

			//get top left position and set it as the start of the pan
			currentPanPosX = -this.getCurrentPanelAbsoluteX();
			currentPanPosY = this.getCurrentPanelAbsoluteY();

			//calculate the movement per Timer update
			if (speedmode == animationSpeedInterpretationMode.FixedSpeedModeOn){
				//step size fixed but in the correct direction +/-
				PanDisplacementX = stepsOrPixels*Math.signum((endPanPosX-currentPanPosX));			
				PanDisplacementY = stepsOrPixels*Math.signum((endPanPosY-currentPanPosY));
			} else if (speedmode == animationSpeedInterpretationMode.FixedSpeedModeOff) {
				//step size is set based on distance and total steps
				PanDisplacementX = (endPanPosX-currentPanPosX)/stepsOrPixels;
				PanDisplacementY = (endPanPosY-currentPanPosY)/stepsOrPixels;
			} else if (speedmode == animationSpeedInterpretationMode.DirectDurationMode){

				//step size is based on duration
				int durationms    = stepsOrPixels; //what was supplied
				//totaldistance/duration = distance per ms
				//pandisplacement is displacement per update
				//thus distance per ms * framerate

				//New; Not tested because this is legacy stuff, we should all be on deltatime by now
				PanDisplacementX = ((endPanPosX-currentPanPosX)/durationms)*FRAME_RATE_UPDATE_TIME;
				PanDisplacementY = ((endPanPosY-currentPanPosY)/durationms)*FRAME_RATE_UPDATE_TIME;

			}

			Log.info(" starting at "+currentPanPosX+","+currentPanPosY);
			Log.info(" going to "+endPanPosX+","+endPanPosY);
			Log.info(" displacing by "+PanDisplacementX+", "+PanDisplacementY);

			//ensure Timer panTimer is running
			if (!panTimer.isRunning()){

				TimeOfLastUpdate = System.currentTimeMillis();
				panTimer.scheduleRepeating(FRAME_RATE_UPDATE_TIME);

			} else {
				Log.info(" timer already running ");
			}
		}


		//return the position we are heading too, in the same center-relative co-ordinate style that was requested
		return correctedTargetPosition;
	}

	/**
	 * ensures X stays within the movement limits 
	 */
	private float ensureXisSafe(float X) {

		//if both too small and too big we set to the average
		//(this happens when no horizontal movement is possible - when the screen is smaller then the limits)
		if ( ((-MinXMovement)<(-MaxXMovement)) ) { //TODO: can be optimized easily by caching this result for centering. Also do Y the same
			Log.info("____________"+X+" Thus setting avagage x due to impossible limits ");
			X = (float) (((-MinXMovement)+(-MaxXMovement))/2.0);
			return X;
		};
		//------------------------

		if ((X < -MaxXMovement)  ) {  //&& !(X > -MinXMovement) 
			Log.info("____________X "+X+" Less then "+(-MaxXMovement)+" so setting to that");			
			X = -MaxXMovement;
		};

		if ((X > -MinXMovement) ) { //&& !(X < -MaxXMovement) 
			Log.info("____________X "+X+" More then "+(-MinXMovement)+" so setting to that");
			X = -MinXMovement;
		};

		return X;
	}

	/**
	 * ensures Y stays within the movement limits
	 */
	private float ensureYisSafe(float Y) {
		//if both we set to the average
		//(this happens when no vertical movement is possible - when the screen is smaller then the limits)
		if ((MinYMovement) > (MaxYMovement)){ //changed to -
			//	if ((Y < -MaxYMovement) && (Y > -MinYMovement)){
			Log.info("____________thus setting avagage y ");
			Y = (float) (((-MinYMovement) + (-MaxYMovement))/2.0);
			return Y;			
		}
		//---

		if ((Y < -MaxYMovement)  ) { //&& !(Y > -MinYMovement)
			Log.info("____________Y "+Y+" Less then "+(-MaxYMovement)+" so setting to that");
			Y = -MaxYMovement;

		};
		if ((Y > -MinYMovement)  ) { //&& !(Y < -MaxYMovement) 
			Log.info("____________Y "+Y+" Greater then "+(-MinYMovement)+" so setting to that");
			Y = -MinYMovement;
		};



		return Y;
	}

	/**
	 * for testing only
	 */
	public void abortCurrentPan(){
		stopCurrentCSSAnimation();
	}
	
	/**
	 * stops the current css animation and sets the top/left co-ordinates from the current transform ones
	 */
	private void stopCurrentCSSAnimation() {
		
		
		if (!currentlyDoingACSSTransition){			
			Log.info("_no current css animation_");
			return;			
		}
		Log.info("_______________________________________________stopping css animation___________________________________________");
		
		//get current top/left from computed
		//String existingTranslate = getComputedStyleProperty(dragableContents.getElement(),"transform");
		//Log.info("_existingTranslate matrix:"+existingTranslate);
		//existingTranslate matrix:matrix(1, 0, 0, 1, -399.855, 166.799)
		
		JsArrayInteger pos=getBoundingClientRect(dragableContents.getElement()); //returns current x/y in slots 0 and 1
		
	//	int t=getBoundingClientRectTop(dragableContents.getElement());		
		int l = pos.get(0);		
		int t = pos.get(1);
		Log.info("_existingTranslate bounding: "+l+","+t);
		
//	animation-play-state: paused;
		

		Style style = dragableContents.getElement().getStyle();

		/*
		style.setProperty("animation-play-state", "pause");
		pos=getBoundingClientRect(dragableContents.getElement()); //returns current x/y in slots 0 and 1
		 l = pos.get(0);		
		 t = pos.get(1);	
		Log.info("_existingTranslate bounding-2: "+l+","+t);
		*/
		
		//convert translation location to top/left
		setPositionInternalCoOrdinates(l,t);
		
		//turn transition off
		style.setProperty("transform", "translate(0px, 0px)");
		style.setProperty("transitionDuration", "0ms");
		currentlyDoingACSSTransition = false; //allows a new transition to be set to run
		Log.info("_______________________________________________stoped css animation___________________________________________");
		
	}
	
	
	/**
	 * animates the pan between two points using CSS.
	 * Currently via a stylesheet thats added to the end of the page.
	 * (In future an alternative method might be http://jsfiddle.net/russelluresti/RHhBz/2/)
	 * 
	 * Note; minimum duration is 170ms, as it takes time to add the transition and really quick durations may seem to "jump"
	 * 
	 * 
	 * @param currentPanPosX2
	 * @param currentPanPosY2
	 * @param endPanPosX2
	 * @param endPanPosY2
	 * @param duration
	 */
	private void startCSSAnimation(final float endPanPosX2, final float endPanPosY2,
			double requetedduration) {

		
		if (requetedduration<170){
			requetedduration=170;
		}
		double duration = requetedduration;
		

		Log.info("_____________new pan requested  ");	

		Log.info("_____________currentlyDoingACSSTransition =  "+currentlyDoingACSSTransition);	

		//recreate stylesheet these transitions if its not already there

		if (createdStyleTag==null){

			createdStyleTag = DOM.createElement("style");						
			RootPanel.getBodyElement().appendChild(createdStyleTag);

			//specify the animation class that defines the start and speed of the animation
			String newstyle = ".shortpan_"+panelID+" {\n";		
			newstyle=newstyle+"\n ";		
			newstyle=newstyle+"transition-property: transform; \n";
			newstyle=newstyle+"transition-timing-function: ease-out; \n";
			newstyle=newstyle+"}\n";

			dragableContents.addStyleName("shortpan_"+panelID);

			//
			//set the style
			createdStyleTag.setInnerText(newstyle);		
		}	

		//set up this specifics panels style and end listener
		if (dragpanelsCSSsetup==false)	{
			//add the transition to the element
			//dragableContents.addStyleName("shortpan_"+panelID);

			//create timer to set position at end of animation
			//Infuture we should use "webkitAnimationEnd" rather then a timer, once its supported well enough


			registerTransitionEndHandler(panelID, new SpiffyTransitionEndEvent() {

				@Override
				public void OnTransitionEnd() {
					Log.info("CSS Animation Ended On:"+panelID);
					OnTransitionEndLog();


					left = (int)-endPanPosX;
					top =  (int)endPanPosY;

					Log.info("_____________setting position too:"+left+","+top);	

					setPositionInternalCoOrdinates(left,top);

					currentPanPosX = endPanPosX;
					currentPanPosY = endPanPosY;
					Log.info("_____________setting currentpan position too:"+currentPanPosX+","+currentPanPosY);	

					//dragableContents.removeStyleName("shortpan");
					//dragableContents.removeStyleName("currenttarget");
					//dragableContents.getElement().getStyle().clearProperty("transform");
					//dragableContents.removeStyleName("shortpan_"+panelID);

					dragableContents.getElement().getStyle().setProperty("transform", "translate(0px, 0px)");
					dragableContents.getElement().getStyle().setProperty("transitionDuration", "0ms");

					//	dragableContents.getElement().getStyle().setProperty("transitionProperty", "none");   //this line doesnt need to ever change, use a class?

					currentlyDoingACSSTransition = false; //allows a new transition to be set to run

					runPostScrollActions();
					
				}
			});

			dragpanelsCSSsetup = true;
		}


		boolean existingpan=false;


		boolean durationDidntChange = false;
		if (oldduration==duration){
			durationDidntChange=true;
		}
		

		


		if (currentlyDoingACSSTransition){

			//if a CSS animation is already running we need to somehow get the current position of the panel and yet change the destination one?
			//not sure how this can be done yet
			//we could also skip to the last endpoint specified and resume from there? Would cause a noticeable cut though, but at least the end would match correctly

			//window.getComputedStyle(elem, null).getPropertyValue('-webkit-transform')

			//String existing = getComputedStyleProperty(dragableContents.getElement(),"transform");
			//	Log.info("existing position:"+existing);
			//existing = getComputedStyleProperty(dragableContents.getElement(),"translate");
			//Log.info("existing position translate:"+existing);
			//existing = getComputedStyleProperty(dragableContents.getElement(),"transform:translate");
			//	Log.info("existing position t:translate:"+existing);

			//currentPanPosX = dragableContents.getElement().getParentNode().
			//onEndAnimation.cancel();

			//for the sake of the new end, we have to take the current destination into account and assume thats where we are now
			//currentPanPosX = (int)endPanPosX;//(int)-endPanPosX;
			//currentPanPosY = (int)endPanPosY;//(int)endPanPosY;

			

			Log.info("_____________setting currentpan position tooo:"+currentPanPosX+","+currentPanPosY);

			//Log.info("currentPanPosX:"+currentPanPosX);
			//Log.info("currentPanPosY:"+currentPanPosY);
			
			//duration=oldduration; //why based on old duration? Why not new? Or at least half and half?
			//Because we are interupting one movement to do another there is no real correct duration, but its more likely the scripter wants
			//the new one surely?
			
			//duration = duration + (oldduration*0.25); //add a little bit of the old duration
			
			
			existingpan= true;
			
		} else {
			//We only set the current start if its a new pan, else it should remain the old one
			//This is because transform is messured relative to the CSS LEFT / TOP positions, which dont change till the END of ALL the css transition animations
			//So if someone hits left twice quickly, midway into the first animation they changed the destination
			//But we need that new destinition (enddifx and enddify) relative to where the first bit of the tranisition started, not the new bit


			//so we get top left position and set it as the start of the pan if theres none already in progress
			Log.info("__started a new pan from:"+currentPanPosX+","+currentPanPosY+" to "+endPanPosX+","+endPanPosY);



			//store in case later pan
			oldduration= duration;
		}
		currentlyDoingACSSTransition = true;

		//get difference in x/y to current position		
		float enddifx = -(endPanPosX-currentPanPosX);
		float enddify = (endPanPosY-currentPanPosY);




		/*
		if (!existingpan){



			//update the duration
			String newstyle = ".shortpan_"+panelID+" {\n";		
			//newstyle=newstyle+"transition:transform ease-out "+duration+"ms; \n ";		//duration
			newstyle=newstyle+"transform:translate(0px, 0px);";
			newstyle=newstyle+"}\n";

			//set the style
			createdStyleTag.setInnerText(newstyle);		

			//dragableContents.addStyleName("shortpan_"+panelID);
		}
		 */


		//currenttarget - class that specifies the end of the animation
		//newstyle=newstyle+".currenttarget {\n";		
		//newstyle=newstyle+"transform:translate("+enddifx+"px,"+enddify+"px);";
		//newstyle=newstyle+"}\n";

		/*
		 * 
		for (String vendor : vendors) {

			newstyle=newstyle+" -"+vendor+"-animation: "+name+" "+duration+"ms; \n ";

		}
		newstyle=newstyle+"}\n";

		//specify the animation keyframes themselves for each vendor
		for (String vendor : vendors) {

		newstyle=newstyle+"@-"+vendor+"-keyframes "+name+" \n {\n"+
				"from { transform: translate(0px,0px); } \n"+
		    	"to{ transform: translate("+enddifx+"px,"+enddify+"px); } \n"+
		    	"} \n";

		}
		 */



		//dragableContents.addStyleName("currenttarget");

		//transform: matrix(1, 0, 0, 1, 22, 15);
		//e.style["transform"] = "translate(0px, -" + pix + "px)";
		//dragableContents.getElement().getStyle().setProperty("transform", "matrix(1, 0, 0, 1, "+enddifx+", "+enddify+")");

		
		//notes from w3c;
		//Once the transition of a property has started, it must continue running based on the original 
		//timing function, duration, and delay, even if the ‘transition-timing-function’, ‘transition-duration’, or ‘transition-delay’ 
		//property changes before the transition is complete.
		//However, if the ‘transition-property’ property changes such that the transition would not have started, 
		//the transition must stop (and the property must immediately change to its final value).
		//....
		//, when a transition is started for a property on an element (henceforth, the new transition) that has a 
		//currently-running transition whose reversing-adjusted start value is the same as the end value of the new transition
		//(henceforth, the old transition), implementations must cancel the old transition link to definition above and 
		//adjust the new transition as follows (prior to following the rules for computing the combined duration, start time, and end time):
		
		
		
		Log.info("_____________existingpan  = "+existingpan+" duration="+duration);	
	//	if (durationDidntChange){
	//		Log.info("_____________ duration didnt change");	
	//	} else {
	
		if (!existingpan){
			dragableContents.getElement().getStyle().setProperty("transitionDuration", duration+"ms"); //maybe only if duration changes?
		} else {
			//do we need to do anything here so css knows its a interrupted animation?
			//does the transition end event fire when interupted?
			//
		}
		
	//	}
		
		//dragableContents.getElement().getStyle().setProperty("transitionProperty", "transform");   //this line doesn't need to ever change, use a class?
		//dragableContents.getElement().getStyle().setProperty("transitionTimingFunction", "ease-out"); //this line doesn't need to ever change, use a class?
		dragableContents.getElement().getStyle().setProperty("transform", "translate("+enddifx+"px, "+enddify+"px)");

		dragableContents.getElement().getStyle().setProperty("animation-play-state", "running");


		//		dragableContents.getElement().getStyle().setProperty("animation-play-state", "pause");
	}

	public void OnTransitionEndLog(){

		Log.info("__________________________TRANSITION END REACHED");
		Log.info("__________________________");
		Log.info("_________________________");
		Log.info("________________________");
		Log.info("____________________");
		Log.info("__________");
		Log.info("_______");
	}


	/** adds a endtransitionevent handler
	 * Currently this is global to the window, in future we might want to restrict it to the dragpanel in case a few transitions
	 * are running in various places
	 * 
	 * oTransitionEnd
	 * 
document.getElementById("myBtn").addEventListener("webkitTransitionEnd", myFunction,false);
document.getElementById("myBtn").addEventListener("transitionend", myFunction,false);

function myFunction() {
    document.getElementById("demo").innerHTML = "Hello Worlds";

}
	 *  **/
	private native void registerTransitionEndHandler(String panelID, SpiffyTransitionEndEvent handler) /*-{

	    console.log('registerTransitionEndHandler:'+panelID);	    

	    $doc.getElementById(panelID).addEventListener('transitionend', myFunction, false);
	    $doc.getElementById(panelID).addEventListener('webkitTransitionEnd', myFunction, false);
	    $doc.getElementById(panelID).addEventListener('mozTransitionEnd', myFunction, false);
	    $doc.getElementById(panelID).addEventListener('oTransitionEnd', myFunction, false);

	    function myFunction(){

	    	console.log('Transition complete!  This is the callback, no library needed!');

	        handler.@lostagain.nl.spiffyresources.client.spiffygwt.SpiffyDragPanel$SpiffyTransitionEndEvent::OnTransitionEnd()();

	    }

	}-*/;


	//getBoundingClientRect (temp, do this better)
	/*
	private static native int getBoundingClientRectLeft(Element obj) /*-{
		
		var rect = obj.getBoundingClientRect();		
		var left = rect.left;
		var top  = rect.top;
		
        return left;
	}-*/
	
	private static native JsArrayInteger getBoundingClientRect(Element obj) /*-{
	
	var rect = obj.getBoundingClientRect();		
	var left = rect.left;
	var top  = rect.top;
	
    return [left,top];
   }-*/;


	
	/**
	 * Native javascript function to get the computed style (that is, the actual values after everything is taken into account)
	 * @param el
	 * @param prop
	 * @return
	 */
	private static native String getComputedStyleProperty(Element el, String prop) /*-{
    var computedStyle;
    if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
        computedStyle = document.defaultView.getComputedStyle(el, null)[prop];

    } else if (el.currentStyle) { // IE older
        computedStyle = el.currentStyle[prop];

    } else { // inline style
        computedStyle = el.style[prop];
    }
    return computedStyle;

	}-*/;
	
	
	

	/**
	 * Sets the view to the requested position, adjusting it if needed to keep within limits
	 * 
	 * @param X
	 * @param Y
	 * @param overrideMOVEMENTDISABLED
	 * 
	 * @return The requested position, with adjustments if they were needed. 
	 * 
	 */
	public Simple2DPoint setViewToPos(int X, int Y,Boolean overrideMOVEMENTDISABLED) {

		//Stop any CSS movement currently playing if we are using css based transitions
		if (CSSBasedTransitions && currentlyDoingACSSTransition){
			stopCssMovement();
		}

		Log.info("::::::::::::setting view to pos x/y" + X + "," + Y);

		if (!XMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
			left = -X + (ContainerSizeX / 2);

			// make sure X/Y isn't outside boundaries
			/*
			if ((left < -MaxXMovement)) {
				Log.info("____________left outside range 2");
				left = -MaxXMovement;
			};
			 */

			//ensure inside x:
			left = (int) ensureXisSafe(left);

			//	if ((left < -MinXMovement)) {
			//		Log.info("____________left "+left+"is less then -minx:"+(-MinXMovement));
			//		left = -MinXMovement;
			//	};

		}
		if (!YMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
			top = -(Y - (ContainerSizeY / 2));

			// make sure X/Y isn't outside boundaries
			/*
			if ((top < -MaxYMovement)) {
				top = -MaxYMovement;
			};*/

			//ensure inside y:
			top = (int) ensureYisSafe(top);

		}

		//note; we need to re-add the container size as the requested position is based on the center of the screen
		//but left and top is based on the top left

		//left = -X + (ContainerSizeX / 2);
		// x =  (ContainerSizeX / 2) - left;
		//
		int correctedX = (ContainerSizeX / 2) - left;
		int correctedY = (ContainerSizeY / 2) - top;		
		Simple2DPoint correctedPosition = new Simple2DPoint(correctedX,correctedY);


		Log.info("::::::::::::setting view to " + left + ", " + top);
		Log.info("::::::::::::ContainerSizeY = " + ContainerSizeY);
		Log.info("::::::::::::ContainerSizeX = " + ContainerSizeX);

		setPositionInternalCoOrdinates(left, top);

		return correctedPosition;
	}
	public void setViewToTopCenter() {
		if (CSSBasedTransitions && currentlyDoingACSSTransition){
			stopCssMovement();
		}


		Log.info("::::::::::::setting view to center to center");
		//resetCSSPosition();
		if (!XMOVEMENTDISABLED) {
			left = -(MinXMovement + MaxXMovement) / 2;
		}
		if (!YMOVEMENTDISABLED) {
			top = -MaxYMovement;

		}

		Log.info("::::::::::::setting view to " + left + ", " + top);

		setPositionInternalCoOrdinates(left, top);
	}

	/** sets the view to the top left position **/

	public void setViewToTopLeft() {
		//resetCSSPosition();
		//Stop any CSS movement currently playing if we are using css based transitions
		if (CSSBasedTransitions && currentlyDoingACSSTransition){
			stopCssMovement();
		}
		Log.info("::::::::::::setting view to bottom left");

		if (!XMOVEMENTDISABLED) {
			left = -MinXMovement;
		}
		if (!YMOVEMENTDISABLED) {
			top = MaxYMovement;
		}
		setPositionInternalCoOrdinates(left, top);
	}

	public void setWidgetsPosition(Widget widget, int x, int y, boolean restrictToScreen) {


		//we first try positioning the widget, then the parent, as widgets might be in a focus panel
		if (dragableContents.getWidgetIndex(widget.getParent())>-1){

			widget = widget.getParent();
		}  //<---this bit added

		//make sure new position of object is fully within the screen
		if (restrictToScreen){

			//Log.info("within screen check");

			int objectSizeX = widget.getOffsetWidth();
			int objectSizeY = widget.getOffsetHeight();

			int currentXlimit = Math.abs(left)  + ContainerSizeX;	
			int currentYlimit = Math.abs(top)  + ContainerSizeY;


			//	Log.info("_____ContainerSizeX "+ContainerSizeX+" ,ContainerSizeY "+ContainerSizeY+"");
			//	Log.info("_____currentMinX "+Math.abs(left)+" ,currentMinY "+Math.abs(top)+"_____currentXlimit "+currentXlimit+" ,currentYlimit "+currentYlimit+"");


			//left out of screen check
			if (x<Math.abs(left)){				
				x=Math.abs(left)+EdgePaddingForRestrictToScreen;	


				//	Log.info("_____setWidgetPosition object at "+x+","+y+" due to left edge out of screen");
			}

			//top out of screen check
			if (y<Math.abs(top)){				
				y=Math.abs(top)+EdgePaddingForRestrictToScreen ;		


				//	Log.info("_____setWidgetPosition object at "+x+","+y+" due to top edge out of screen");
			}

			//right out of screen check
			if ((x+objectSizeX)>currentXlimit){	

				//Log.info("right out of screen");
				x = currentXlimit-objectSizeX-EdgePaddingForRestrictToScreen;	


				//	Log.info("_____setWidgetPosition object at "+x+","+y+" due to right edge out of screen");
			}
			//bottom out of screen check
			if ((y+objectSizeY)>currentYlimit){		

				//Log.info("bottom out of screen");
				y = currentYlimit-objectSizeY-EdgePaddingForRestrictToScreen;	

				//	Log.info("_____setWidgetPosition object at "+x+","+y+" due to bottom out of screen");
			}

		}


		dragableContents.setWidgetPosition(widget, x, y);

		//would a try/catch be quicker code then testing for presence?
		//no clue

		/*
		try {

			dragableContents.setWidgetPosition(widget, x, y);

		} catch (Exception e) {

			dragableContents.setWidgetPosition(widget.getParent(), x, y);
		}
		 */
	}

	public void stepLoading() {
		LoadingIcon.stepClockForward();
	}

	/**
	 * Is this function name clear?
	 * 
	 * @param event
	 * @param sourceWidget
	 **/
	private void stuffToDoWhenTheMouseIsDownWhileEditingStuff(int X, int Y, Widget sourceWidget) {


		// remember grab point
		Log.info("mouse down..... while editing at :");


		if (dragOnlyThis!=null){

			//only allow drag if matches or matches the parent
			if (dragOnlyThis==sourceWidget || dragOnlyThis==sourceWidget.getParent()){
				currentlyDraggingWidget = dragOnlyThis;
			}		

		} else {
			currentlyDraggingWidget = sourceWidget;
		}

		if (currentlyDraggingWidget!=null){

			//set the capture on the item we are dragging. (Normally we drag the panel itself, and thus capture that. When editing a item however, its the widget we set the capture on)
			//Its important to remember thus to release the capture on the item after a edit)

			Log.info("Setting capture on "+currentlyDraggingWidget.getClass()+"which contains a widget with ID:"+currentlyDraggingWidget.getElement().getId());
			DOM.setCapture(currentlyDraggingWidget.getElement()); 

			//------------------------------------------------------------------------------------

			//highlight		
			OldWidgetBorder = currentlyDraggingWidget.getElement().getStyle().getBorderColor();		
			currentlyDraggingWidget.getElement().getStyle().setBorderColor("#00F");
		}

		Log.info("mouse down..... while editing at :"+X+","+Y);

		DragDisX = X;
		DragDisY = Y;

	}

	/**
	 * Is this function name clear?
	 * 
	 * @param event
	 **/

	private void stuffToDoWhenTheMouseMoveWhileEditingStuff(int eventx,int eventy) {

		// current mouse location
		if (currentlyDraggingWidget != null) {

			//new x/y
			int x = eventx - left;
			int y = eventy - top;

			//current x/y
			int cx = dragOnlyThis.getElement().getOffsetLeft();
			int cy = dragOnlyThis.getElement().getOffsetTop();

			//only update co-ordinates if allowed by restriction
			switch(currentEditingDragRestriction){
			case Hor:
				this.setWidgetsPosition(currentlyDraggingWidget, x-DragDisX, cy,false);
				break;
			case Vert:
				this.setWidgetsPosition(currentlyDraggingWidget, cx, y-DragDisY,false);
				break;
			case None:
			default:				
				this.setWidgetsPosition(currentlyDraggingWidget, x-DragDisX, y-DragDisY,false);
				break;			
			}


			//	int x = eventx - left;
			//	int y = eventy - top;

			// remember grab point
			//	Log.info("mouse move..... while editing ");
			//currentlyDraggingWidget.getElement().getStyle().setBorderWidth(10, Unit.PX);



			//this.setWidgetsPosition(currentlyDraggingWidget, x-DragDisX, y-DragDisY,false);

		}
	}

	/**
	 * Is this function name clear?
	 **/
	private void stuffToDoWhenTheMouseUpWhileEditingStuff() {

		Log.info("mouse up..... while editing ");

		if (currentlyDraggingWidget!=null){			
			cancelDraggingAndWidgetAndReleaseCapture();		

		}



		//run post actions
		if (OnFinishedEditingWidget!=null){
			OnFinishedEditingWidget.run();
		}


	}

	/**
	 * After dragging a widget to edit its position it is VERY important this is run to allow click events
	 * to happen elsewhere again.
	 */
	protected void cancelDraggingAndWidgetAndReleaseCapture() {
		//-------------------------
		//Cancel capture of events:
		Log.info("Releasing capture on "+currentlyDraggingWidget.getClass()+"which is a widget with ID:"+currentlyDraggingWidget.getElement().getId());
		DOM.releaseCapture(currentlyDraggingWidget.getElement()); //When dragging a widget its events are captured. This means mouse or touch actions wont work elsewhere.
		//Its thus VERY important to ensure when a drag ends to cancel the capture.
		//(and also that we release the capture on the same element it started with - else this would do nothing!)

		//unhighlight
		currentlyDraggingWidget.getElement().getStyle().setBorderColor(OldWidgetBorder);
		currentlyDraggingWidget = null;
	}


	public void testForNudgeKeyUp(int keycode) {

		if (editMode && (dragOnlyThis != null)){

			//update data
			if (OnFinishedEditingWidget!=null){
				OnFinishedEditingWidget.run();
			}
		}

	}

	/**
	 * will nudge the widget about for precise editing
	 * WONT fire onFinnishedEditing by itself, fire testForNudgeKeyUp manually as well if using this to ensure the update runs
	 * 
	 * @param event
	 */
	public void testForNudgeKeyPressed(int keycode){//(NativePreviewEvent event) {

		//int charcode=event.getNativeEvent().getCharCode();

		int charcode = keycode; //event.getNativeEvent().getKeyCode(); //now work by key codes

		Log.info("key up:"+charcode);




		//nudge if editing
		//97=a
		//100=d
		//115=s
		if (editMode && (dragOnlyThis != null)){

			//Log.info("key down:"+event.getNativeEvent().getCharCode()+" while editing");

			//note we used to .getParent() here on the element first on the assumption it was always in a dragpanel
			//thats no longer the case, so .getParent() is used (only if needed) when dragOnlyThis is set to a value.
			//this ensures dragOnlyThis is always the element thats actually on the dragpanel, and not a widget it might be within

			int x = dragOnlyThis.getElement().getOffsetLeft();
			int y = dragOnlyThis.getElement().getOffsetTop();

			switch (charcode) {
			//case 97: //old char code
			case 65: //A	
			case 37: //left	
				thisDragPanel.setWidgetsPosition(dragOnlyThis, x-1,y,false);
				break;
				//case 100://old char code
			case 68: //D
			case 39: //right
				thisDragPanel.setWidgetsPosition(dragOnlyThis, x+1,y,false);
				break;
			case 83:  //s
			case 40: //down
				//case 115://old char code
				thisDragPanel.setWidgetsPosition(dragOnlyThis, x,y+1,false);
				break;				
				//case 119://old char code
			case 87: //E
			case 38: //up
				thisDragPanel.setWidgetsPosition(dragOnlyThis, x,y-1,false);
				break;
			}


		}
	}

	public void updateDragableSize() {

		Log.info("updateDragableSize");

		ContainerSizeX = Container.getOffsetWidth();
		ContainerSizeY = Container.getOffsetHeight();

		MaxXMovement = Max_Width - ContainerSizeX;
		MaxYMovement = Max_Height - ContainerSizeY;

		int x = dragableContents.getOffsetWidth();
		int y = dragableContents.getOffsetHeight();


		Log.info("::::::::::::::::::::::::draggable contents x size =" + x
				+ " container x size =" + ContainerSizeX);

		Log.info("::::::::::::::::::::::::draggable contents y size =" + y
				+ " container y size =" + ContainerSizeY);

		Log.info("________________maxX movement = " + MaxXMovement);
		Log.info("________________maxY movement = " + MaxYMovement);

		if (ContainerSizeX > x) {

			left = ((ContainerSizeX / 2) - (x / 2));

			Log.info("centering x=" + left);

			// Container.setWidgetPosition(dragableContents, left ,top);
				XMOVEMENTDISABLED = true; //there isnt room for x movement!
			
		} else {
			if (XMovementDisabledRequested){
				XMOVEMENTDISABLED = false;
			}
		}

		if (ContainerSizeY > y) {

			top = ((ContainerSizeY / 2) - (y / 2));

			Log.info("centering y " + top);
			// Container.setWidgetPosition(dragableContents, left , top);
				YMOVEMENTDISABLED = true;//there isnt room for y movement!
			
		} else {
			if (YMovementDisabledRequested){
				YMOVEMENTDISABLED = false;
			}
		}

		//check co-ordinates are still safe
		left = (int) ensureXisSafe(left);
		top  = (int) ensureYisSafe(top);


		setPositionInternalCoOrdinates(left, top);
	}

	public boolean wasJustDragged() {
		if (justDragged){

			//justDragged=false;


			return true;
		}
		return false;
	}

	/** gives usefull debugging data **/
	static class DragPanelDatabar extends HorizontalPanel {

		// current canvas position
		VerticalPanel canvasLoc = new VerticalPanel();
		Label lab_currentXpos = new Label("-");
		Label lab_currentYpos = new Label("-");

		// current mouse position
		VerticalPanel mouseLoc = new VerticalPanel();
		Label lab_currentMouseX = new Label("-");
		Label lab_currentMouseY = new Label("-");

		//current relative mouse loc
		VerticalPanel relMouseLoc = new VerticalPanel();
		Label lab_currentRelMouseX = new Label("-");
		Label lab_currentRelMouseY = new Label("-");

		//drag stuff
		VerticalPanel dragLab = new VerticalPanel();

		Label lab_currentDragState = new Label("-");
		Label lab_currentDragStartX = new Label("-");
		Label lab_currentDragStartY = new Label("-");


		public DragPanelDatabar() {
			// fill container horizontal
			//this.setWidth("20%");

			// set background to blacks/transparent			
			this.getElement().getStyle().setBackgroundColor("rgba(17, 29, 29, 0.37)");
			this.getElement().getStyle().setColor("WHITE");
			this.getElement().getStyle().setProperty("textShadow", "rgb(0, 0, 0) 1px 1px 5px");
			this.getElement().getStyle().setZIndex(900000);
			this.getElement().getStyle().setProperty("pointer-events", "none");

			this.getElement().getStyle().setMarginLeft(5, Unit.PX);
			this.getElement().getStyle().setMarginTop(5, Unit.PX);
			this.getElement().getStyle().setPadding(5, Unit.PX);
			this.setSpacing(10);



			addMouseFeedback();
		}

		public void setCurrentDragState(boolean draging, int dragStartX, int dragStartY) {

			//get the current capture elemments ID (if any). All touch and mouse events should only appl;y to this
			Element captureElement = DOM.getCaptureElement();
			String captureID = "";
			if (captureElement!=null){
				captureID = "ID:"+captureElement.getId()+"";
			}
			//----------------------------			

			//--
			if (draging){				

				lab_currentDragState.setText("_dragging("+captureID+")");			
				lab_currentRelMouseX.setText("startx:"+dragStartX);
				lab_currentRelMouseY.setText("starty:"+dragStartY);
			} else {
				lab_currentDragState.setText("_no drag("+captureID+")");			
				lab_currentRelMouseX.setText("startx:");
				lab_currentRelMouseY.setText("starty:");

			}
			//--
		}

		public void hideRelativeMousePositionLabel() {
			relMouseLoc.setVisible(false);			
		}
		public void showRelativeMousePositionLabel() {
			relMouseLoc.setVisible(true);			
		}

		private void addMouseFeedback() {
			// add mouse feedback


			mouseLoc.add(lab_currentMouseX);
			mouseLoc.add(lab_currentMouseY);

			lab_currentMouseX.setStylePrimaryName("FeedbackLabel");
			lab_currentMouseY.setStylePrimaryName("FeedbackLabel");

			mouseLoc.setWidth("80px");


			canvasLoc.add(lab_currentXpos);
			canvasLoc.add(lab_currentYpos);
			canvasLoc.setWidth("60px");

			lab_currentXpos.setStylePrimaryName("FeedbackLabel");
			lab_currentYpos.setStylePrimaryName("FeedbackLabel");


			relMouseLoc.add(lab_currentRelMouseX);
			relMouseLoc.add(lab_currentRelMouseY);
			relMouseLoc.setWidth("100px");

			lab_currentRelMouseX.setStylePrimaryName("FeedbackLabel");
			lab_currentRelMouseY.setStylePrimaryName("FeedbackLabel");

			//drag stuff:
			dragLab.add(lab_currentDragState);
			dragLab.add(lab_currentRelMouseX);
			dragLab.add(lab_currentRelMouseY);
			dragLab.setWidth("100px");

			lab_currentRelMouseX.setStylePrimaryName("FeedbackLabel");
			lab_currentRelMouseY.setStylePrimaryName("FeedbackLabel");

			//add the created canvas loc widget
			super.add(new Label("Canvas :"));		
			super.add(canvasLoc);
			super.setCellWidth(canvasLoc, "90px");

			//then the mouse loc widget
			super.add(new Label("Mouse :"));			
			super.add(mouseLoc);
			super.setCellWidth(mouseLoc, "90px");


			//then the mouse loc widget
			super.add(new Label("Mouse Object Rel :"));			
			super.add(relMouseLoc);
			super.setCellWidth(relMouseLoc, "150px");

			//then the drag widget
			super.add(new Label("Drag :"));			
			super.add(dragLab);
			super.setCellWidth(dragLab, "150px");
		}

		public void setCurrentMousePositionLabel(int X, int Y) {

			//mouse pos
			lab_currentMouseX.setText("mx = " + X + "");
			lab_currentMouseY.setText("my = " + Y + "");


		}

		public void setCurrentRelativeMousePositionLabel(int X, int Y) {
			//mouse relative
			lab_currentRelMouseX.setText("mrx = " + X + "");
			lab_currentRelMouseY.setText("mry = " + Y + "");
		}

		public void setCurrentPositionLabel(int X, int Y) {

			lab_currentXpos.setText("x = " + X + "");
			lab_currentYpos.setText("y = " + Y + "");
		}
	}

	private final class MotionFlowTimer extends Timer {
		@Override
		public void run() {

			if (hardStop) {
				Log.info("hard stopping");
				isCoasting = false;
				motionflow.cancel();
				return;
			}

			// set in motion flag;
			isCoasting = true;

			// Log.info("seting coordinates from timer:");

			displacebyInternalCoOrdinates((int) Math.round(MotionDisX),
					(int) Math.round(MotionDisY));

			// Log.info("set coordinates from timer_0");

			// slow down
			MotionDisX = (MotionDisX / 1.2);
			MotionDisY = (MotionDisY / 1.2);

			// stop
			isMoving = false;
			if ((MotionDisX < 1) && (MotionDisX > -1)) {
				MotionDisX = 0;
			} else {
				isMoving = true;
			}
			if ((MotionDisY < 1) && (MotionDisY > -1)) {
				MotionDisY = 0;
			} else {
				isMoving = true;
			}

			if (!(isMoving)) {
				this.cancel();
				Log.info("\n stoped");
				isCoasting = false;

			}
			// Log.info("set coordinates from timer_end");

		}

	}


	/**
	 * Did the last fired event come from a item (true) or the drag panel itself? (false)
	 */
	private boolean currentDragPanelEventCameFromItem = false;
	/**
	 * Did the last fired event come from a item (true) or the drag panel itself? (false)	
	 * @return
	 */
	public boolean currentDragPanelEventCameFromItem() {
		return currentDragPanelEventCameFromItem;
	}



	class SpiffyDragMouseDownHandler implements MouseDownHandler {

		private boolean fromItem = false;
		private boolean disableFocus = false;

		private Widget sourceWidget = null;


		public SpiffyDragMouseDownHandler(boolean fromItem,
				Widget widget, boolean disableFocus) {



			this.fromItem = fromItem;
			this.sourceWidget = widget;
			this.disableFocus = disableFocus;
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			//update the event flag 
			currentDragPanelEventCameFromItem = fromItem;
			//--------------------

			if (disableFocus){
				event.preventDefault();
			}

			// we currently use the ID of the widget to help debug where this came from, assuming theres a widget at all
			if (sourceWidget!=null){
				Log.info("onMouseDown from container of "+sourceWidget.getElement().getId()+" (wasfromitem:"+fromItem+") preventing propergating");
			} else {
				Log.info("onMouseDown (wasfromitem:"+fromItem+") preventing propergating and setting capture");

			}


			//	DOM.setCapture(((Widget) event.getSource()).getElement()); //temp disable experiment
			//DOM.setCapture(((Widget) event.getSource()).getElement()); //from repo
			//	DOM.setCapture(SpiffyDragPanel.this.getElement()); 


			// This will stop the event from being
			// propagated
			event.stopPropagation();
			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);


			//	Log.info("______onMouseDown");

			//int x = event.getX();
			//int y = event.getY();
			//fix?
			int x = event.getRelativeX(Container.getElement());
			int y = event.getRelativeY(Container.getElement());

			int dx = 0;
			int dy =0;

			if ((editMode) && (sourceWidget != null)) {

				dx  = event.getRelativeX(sourceWidget.getElement());
				dy  = event.getRelativeY(sourceWidget.getElement());

			}

			onMouseOrTouchDown(x, y, dx, dy,fromItem,sourceWidget);

		}


	}
	class SpiffyDragMouseMoveHandler implements MouseMoveHandler {

		private boolean cameFromItem2 = false;

		public SpiffyDragMouseMoveHandler(boolean fromItem) {

			cameFromItem2 = fromItem;
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			//update the event flag 
			currentDragPanelEventCameFromItem = cameFromItem2;

			//--------------------			
			event.preventDefault();
			event.stopPropagation();
			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);

			//int x = event.getX();
			//int y = event.getY();
			//fix?
			int x = event.getRelativeX(Container.getElement());
			int y = event.getRelativeY(Container.getElement());


			onMouseOrTouchMove(x, y);
		}

	}

	class SpiffyDragMouseUpHandler implements MouseUpHandler {

		private boolean sourceWasItem = false;
		private boolean disableFocus = false;

		public SpiffyDragMouseUpHandler(boolean fromItem, boolean disableFocus) {

			sourceWasItem = fromItem;
			this.disableFocus=disableFocus;

			//	Log.info("set up_:" + sourceWasItem + " ");



		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			//update the event flag 
			currentDragPanelEventCameFromItem = sourceWasItem;
			//--------------------



			if (disableFocus){
				event.preventDefault();
			}
			// This will stop the event from being
			// propagated
			Log.info("onMouseUp from  (wasitem:"+sourceWasItem+") / releasing capture and preventing propergating");


			DOM.releaseCapture(SpiffyDragPanel.this.getElement());//ensure release (even if we didnt capture)
			event.stopPropagation();			
			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);

			if (sourceWasItem) {
				// now checking if the mouseup still remembers where it came from
				Log.info("MouseUp came from item");

			}

			onMouseOrTouchUp();
		}

	}

	class SpiffyDragTouchDownHandler implements TouchStartHandler {

		private boolean fromItem = false;

		private Widget sourceWidget = null;
		private boolean disableFocus = false;

		public SpiffyDragTouchDownHandler(
				boolean fromItem,
				Widget widget, 
				boolean disableFocus) {


			this.disableFocus=disableFocus;
			this.fromItem = fromItem;
			this.sourceWidget = widget;

		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			//update the event flag 
			currentDragPanelEventCameFromItem = fromItem;
			//--------------------
			if (disableFocus){
				event.preventDefault();
			}
			// we currently use the ID of the widget to help debug where this came from, assuming theres a widget at all
			if (sourceWidget!=null){
				Log.info("onTouchStart from container of "+sourceWidget.getElement().getId()+" (wasfromitem:"+fromItem+") preventing propergating");
			} else {
				Log.info("onTouchStart (wasfromitem:"+fromItem+") preventing propergating and setting capture");

			}

			// This will stop the event from being
			// propagated

			event.stopPropagation();

			//DOM.setCapture(((Widget) event.getSource()).getElement());  //old from repo

			//DOM.setCapture(SpiffyDragPanel.this.getElement()); 

			//	DOM.setCapture(((Widget) event.getSource()).getElement());//temp disable expirement

			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);

			Log.info("______onTouchStart");

			//int x = event.getTouches().get(0).getRelativeX(event.getRelativeElement());    
			//int y = event.getTouches().get(0).getRelativeY(event.getRelativeElement());

			//replaced with to match mousedown;
			int x = event.getTouches().get(0).getRelativeX(Container.getElement());    
			int y = event.getTouches().get(0).getRelativeY(Container.getElement());

			int dx = 0;
			int dy = 0;

			if ((editMode) && (sourceWidget != null)) {
				dx = event.getTouches().get(0).getRelativeX(sourceWidget.getElement());
				dy = event.getTouches().get(0).getRelativeY(sourceWidget.getElement());
			}

			Log.info("______triggering onMouseOrTouchDown:"+x+","+y);

			onMouseOrTouchDown(x, y, dx, dy,fromItem,sourceWidget);

		}


	}


	class SpiffyDragTouchEndHandler implements TouchEndHandler {

		private boolean sourceWasItem = false;

		public SpiffyDragTouchEndHandler(boolean fromItem) {
			sourceWasItem = fromItem;
			//	Log.info("set up_:" + sourceWasItem + " ");

		}

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			//update the event flag 
			currentDragPanelEventCameFromItem = sourceWasItem;
			//--------------------


			event.preventDefault();

			//	DOM.releaseCapture(((Widget) event.getSource()).getElement());//ensure release (even if we didnt capture)  (old from repo)
			//now we capture/release just the main panel 
			DOM.releaseCapture(SpiffyDragPanel.this.getElement());//ensure release (even if we didn't capture)


			// This will stop the event from being
			// propagated
			event.stopPropagation();
			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);

			onMouseOrTouchUp();
		}

	}

	class SpiffyDragTouchMoveHandler implements TouchMoveHandler {

		private boolean cameFromItem2 = false;

		public SpiffyDragTouchMoveHandler(boolean fromItem) {

			cameFromItem2 = fromItem;
		}


		@Override
		public void onTouchMove(TouchMoveEvent event) {
			//Update the event flag 
			currentDragPanelEventCameFromItem = cameFromItem2;
			//--------------------

			//	History.newItem("Touch Move Event Detected!");
			//	Window.setStatus("Touch Move Event");

			event.preventDefault();
			event.stopPropagation();
			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);

			//older;
			//int x = event.getTouches().get(0).getRelativeX(event.getRelativeElement());  //why not Container?
			//int y = event.getTouches().get(0).getRelativeY(event.getRelativeElement());

			//newer (to n match mouse )
			int x = event.getTouches().get(0).getRelativeX(Container.getElement());
			int y = event.getTouches().get(0).getRelativeY(Container.getElement());

			Log.info("_____Touch Move Event Detected:"+x+","+y);
			onMouseOrTouchMove(x, y);
		}

	}



	public boolean isCSSBasedTransitions() {
		return CSSBasedTransitions;
	}
	/**
	 * This means position animation is done via css transforms
	 * this is normally smoother then the javascript method, but might not work on older browser (IE10 and below, for example)
	 * It also might not well if you plan to let the user drag the panel about (Default) rather then controlling it yourself
	 * So, in other words, be carefull when turning this on!
	 * @param cSSBasedTransitions - true to turn on
	 */
	public void setCSSBasedTransitions(boolean cSSBasedTransitions) {
		CSSBasedTransitions = cSSBasedTransitions;
	}

	public void setLoadingMessage(String string) {
		messageLabel.setText(string);
	}
	
	public void setLoadingTitleMessage(String string) {
		
		LoadingIcon.setProgressLabelToolTip(string);
		
	}
	
	/**
	 * Note; fadeing in the dragpanel actually means fadeing OUT the overlay!
	 */
	private void startFadeIn() {

		currentOpacity = 1;
		currentFadeState = fadeState.FadeOut; //fadeout overlay

		//ensure updates have started
		startDeltaUpdates();

	}

	private void startDeltaUpdates(){
		DeltaTimerController.addObjectToUpdateOnFrame(this);
	}

	private void stopDeltaUpdatesIfNothingLeftUpdating(){

		if (currentFadeState == fadeState.NoFade){
			DeltaTimerController.removeObjectToUpdateOnFrame(this);
		}
	}

	/**
	 * New update system that will eventually controll all non-CSS animations
	 * Implemented So far; Fade
	 */
	@Override
	public void update(float delta) {
		//deal with fades first
		updateOverlayFade(delta);
		//then other stuff here


	}

	private void updateOverlayFade(float delta) {

		timeIntoCurrentFadeState=timeIntoCurrentFadeState+delta;

		double changeAmount = 0;

		switch (currentFadeState) {
		case NoFade:			
			//test for removal of update
			stopDeltaUpdatesIfNothingLeftUpdating();
			return;
		case Delay:
			if (timeIntoCurrentFadeState>fadeDelay){
				currentFadeState = fadeState.FadeIn;//no break we continue straight away
				timeIntoCurrentFadeState=0;
			} else {
				return; //still waiting for the delay phase to finnish
			}
		case FadeIn:
			changeAmount = fadeInStepPerMS * delta;
			currentOpacity=currentOpacity+changeAmount;
			if (currentOpacity>=1){
				currentOpacity=1;
				currentFadeState = fadeState.FadeOut;
				timeIntoCurrentFadeState=0;
			}
			loadingOverlay.getElement().getStyle().setOpacity(currentOpacity);
			break;			

		case FadeOut:
			changeAmount = (fadeOutStepPerMS * delta);
			currentOpacity=currentOpacity-changeAmount;

			if (currentOpacity<=0.05){
				currentOpacity=0;			
				timeIntoCurrentFadeState=0;
				loadingOverlay.getElement().getStyle().setOpacity(0);
				Container.remove(loadingOverlay);

				loadingOverlay.clear();
				loadingMessage.clear();

				currentFadeState = fadeState.NoFade;
				stopDeltaUpdatesIfNothingLeftUpdating();
				return;
			}

			loadingOverlay.getElement().getStyle().setOpacity(currentOpacity);
			break;
		}
	}

	public boolean loadingOverlayAttached() {
		// TODO Auto-generated method stub
		
		return loadingOverlay.isAttached();
	}

	Runnable runAfterScroll; 
	
	/**
	 *  runs after a scroll successfully completes
	 * @param runAfterScroll
	 */
	public void setRunAfterScroll(Runnable runAfterScroll) {
		this.runAfterScroll = runAfterScroll;
	}

	public void runPostScrollActions() {
		if (runAfterScroll!=null){
			runAfterScroll.run();
		}
	}




}
