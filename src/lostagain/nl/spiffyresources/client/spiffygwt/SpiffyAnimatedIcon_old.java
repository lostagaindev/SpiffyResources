package lostagain.nl.spiffyresources.client.spiffygwt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;
import lostagain.nl.spiffyresources.client.spiffycore.FramedAnimationManager;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;

/** Note; class currently needs a DeltaTimerController class setup to operate.
 *  In future we might uncomment the internal timer to provide it as a optional (worse) alternative
 * 
 * An animated icon is an image that can store an animation and play it back either forward
 * or backward.
 * This is used as the base for the SceneSprite object, amongst other things.
 * It has many display modes to do animation in different ways
 * 

 * Note; We can optimize the flipbook mode by not using a deckpanel of deckpanels, but instead 1 deckpanel and simply remember when each frameset starts within it (ie, 0-5 would be one animation 6-9 another etc)
 */
public class SpiffyAnimatedIcon_old extends Composite implements HasDeltaUpdate { // Image {

	static Logger Log = Logger.getLogger("SpiffyAnimatedIcon");
	
	//TODO: use a framedanimationcontroller to remove a lot of this classes code
	FramedAnimationManager animation = new FramedAnimationManager(){
		@Override
		public void setFrame(int currentframe) {
			SpiffyAnimatedIcon_old.this.setFrame(currentframe);		
		}		
	};
	
	
	boolean debug             = false;
	boolean internalPreloader = true;

	/** The content of this panel in normal or bundle modes */
	Image imageContents = new Image();

	/** The contents will be a DeckPanel in flipBook mode*/
	FocusPanel flipBookContainer = new FocusPanel();
	DeckPanel flipBook = new DeckPanel();
	ArrayList<Image> flipbookImageList = new ArrayList<Image>();
	/**maintains a cache of all known images DeckPanelfor ready re-use*/
	HashMap<String,DeckPanel> cacheOfAllLists = new HashMap<String,DeckPanel>();
	DeckPanel currentFlipBook;
	Image lastImageAddedToDeck;

	/*** 
	 * The widget that controls click events - normally this is the same as imageContents.
	 * IMPORTANT: THIS WIDGET MUST ONLY EVER BE SET TO AN IMAGE OR DECKPANEL
	 * This is because its casted later, and the expectation is it can do everything those two types can do. 
	 ***/
	Widget contents = imageContents;

	public void setInternalPreloader(boolean internalPreloader) {
		this.internalPreloader = internalPreloader;
	}

	private int frametotal   = 1;
	private int currentframe = 0;
	

	// you can assign a unique name to this icon, to help finding it later in a
	// list of icons
	public String uniqueName = "";

	enum animationDirection{
		close,open;
	}

	animationDirection animation_direction = animationDirection.open;

	public String basefilename = "";


	public String originalfilename = "";
	

	String currentfilename = "";
	public String filenameext = "png";
	boolean close_after_open = false;
	boolean open_after_close = false;
	Timer timer;
	int timerDelay=100;

	//in order for the new delta based time system to work with frames we need to track how long since the last frame change
	double timeSinceLastUpdate = 0;
			
	// image array
	int playuntill = 100 + getFrametotal();

	private boolean loop = false;
	private boolean currentlyAnimating = false;

	Command runThisAfterOpen     = null;
	Command runThisAfterClose    = null;
	Command runThisOnFrameChange = null;  //used mostly for debug work, don't overdo this!

	//internal cache list (should be toggled off for production

	/** keeps a store of all urls asked for **/
	static public HashSet<String> urlList = new HashSet<String>();


	List<AbstractImagePrototype> Frames = new ArrayList<AbstractImagePrototype>();
	//public Boolean BundleImageMode = false;

	
	
	public enum SpriteMode{
		/** normal - we use setURL and rely on the browser cache for the speed of the images being set. **/
		normal,
		/** bundle - uses image strips made at compile time **/
		bundle,
		/** flipbook - uses a deckpanel with all the images preloaded into it. The correct panel in the stack is simply picked to animate **/
		flipbook,
		/** staticimage - no animation its just one single image! WOW! (not yet implemented)**
		 * */
		staticimage
	}

	/** the current method this sprite uses to animate */
	public SpriteMode mode = SpriteMode.normal;


	//current handlers (we need to track  them so we can remove them)
	HandlerRegistration currentClickHandler;
	HandlerRegistration currentMouseDownHandler;
	HandlerRegistration currentMouseOverHandler;
	HandlerRegistration currentMouseOutHandler;
	HandlerRegistration currentLoadHandler;
	HandlerRegistration currentLoadErrorHandler;

	final SpiffyAnimatedIcon_old ThisIcon = this;

	public SpiffyAnimatedIcon_old(AbstractImagePrototype[] SetFrames){
		imageContents = SetFrames[0].createImage();
		contents = imageContents;
		initWidget(imageContents);
		//BundleImageMode = true;
		mode = SpriteMode.bundle;


	//	Log.info("Initialized for bundle mode.");

		System.out.print("\n adding icons:" );

		currentfilename = "";


		//note the lower bit can be replaced with a setFrame call
		setFrametotalVar((SetFrames.length -1));



		for (int cp = 0; cp < (getFrametotal()+1); cp = cp + 1) {
			Frames.add(SetFrames[cp]);
			//System.out.print("\n adding"+SetFrames[cp].getUrl() );

		}
		//this.setWidget(Frames.get(0));
		Frames.get(0).applyTo(imageContents);
		//----------------------------

		timerSetup();
	}

	/**
	 * Sets an internal animation based on the frames you give it.
	 * framecount is set from the size of the framearray
	 * 
	 * @param SetFrames
	 * @param name
	 */
	public SpiffyAnimatedIcon_old(AbstractImagePrototype[] SetFrames, String name){
		//super();
		bundledBasedSetup(SetFrames, name);

	}

	
	protected void bundledBasedSetup(AbstractImagePrototype[] SetFrames, String name) {
		imageContents = SetFrames[0].createImage();
		contents = imageContents;
		initWidget(imageContents);
		Log.info("initialised "+name+" for bundle mode.");
		mode = SpriteMode.bundle;


		currentfilename = name;


		//note the lower bit can be replaced with a setFrame call
		setFrametotalVar((SetFrames.length -1));

		Log.info("adding bundles frames:"+(getFrametotal()+1) );

		for (int cp = 0; cp < (getFrametotal()+1); cp = cp + 1) {
			Frames.add(SetFrames[cp]);
			//System.out.print("\n adding"+SetFrames[cp].getUrl() );

		}

		//this.setWidget(Frames.get(0));
		Frames.get(0).applyTo(imageContents);
		//----------------------------

		timerSetup();
	}

	/** This is a special image that supports animations to be triggered on it.
	 * **/
	public SpiffyAnimatedIcon_old(String FileZeroLocation, int NumOfFrames,boolean flipbook) {

		if (NumOfFrames == 1){
			Log.info("static image mode used for sprite");
			mode = SpriteMode.staticimage;
			staticImageSetup(FileZeroLocation);
			return;

		} 
		
		if (flipbook){
			flipbookUrlBasedSetup(FileZeroLocation, NumOfFrames);
		} else {
			standardUrlBasedSetup(FileZeroLocation, NumOfFrames);
		}
	}
	
	public SpiffyAnimatedIcon_old(String FileZeroLocation, int NumOfFrames) {

		if (NumOfFrames ==1){
			Log.info("static image mode used for sprite");
			mode = SpriteMode.staticimage;
			staticImageSetup(FileZeroLocation);
			return;

		} else 	{	
			
			//else we assume standardUrlBasedsetup
			standardUrlBasedSetup(FileZeroLocation, NumOfFrames);
			
			//assume default based on if the game is running on local mode or not
			//if (JAM.LocalFolderLocation.length()>3){
			///	flipbookUrlBasedSetup(FileZeroLocation, NumOfFrames);
			//} else {
			//	standardUrlBasedSetup(FileZeroLocation, NumOfFrames);

			//}
		
		}
		/*
		//super();
		initWidget(imageContents);
		this.setUrl(FileZeroLocation);

		setupVariables(FileZeroLocation, NumOfFrames);

		// preload all images

		prefetchImages();

		timerSetup();*/
	}
	
	/**
	 * empty constructor to help subclass's pick the right setup type manually
	 */
	protected SpiffyAnimatedIcon_old() {
	}

	public void flipbookUrlBasedSetup(String FileZeroLocation, int NumOfFrames) {

		//	if (flipbook){
		mode = SpriteMode.flipbook;



		Log.info("flip book made set");

		//} else {
		//	standardUrlBasedSetup(FileZeroLocation, NumOfFrames);
		//	return;
		//}

		//setup variables
		setupVariables(FileZeroLocation, NumOfFrames);

		//add the deck 
		flipBookContainer.add(flipBook);		
		initWidget(flipBookContainer);

		contents = flipBookContainer;


		//standard dom stuff;
		applyStyleAndAttributes(flipBookContainer.getElement());
		 
		//fill it and show newly filled frameset
		fillDeckPanel();

		timerSetup();

		runOnFrameChangeActions();
		//set first frame
		//flipBook.showWidget(0);

		Log.info("flip book made setup");




	}

	private void fillDeckPanel() {

		flipbookImageList.clear();

		//flipBook.clear();

		//check if its already cached
		if (cacheOfAllLists.get(basefilename) != null){

			Log.info("creating from cache");

			DeckPanel frameset = cacheOfAllLists.get(basefilename);
			int index = flipBook.getWidgetIndex(frameset);

			flipBook.showWidget(index);
			frameset.showWidget(0);//first frame
			setCurrentframeVariable(0);

			currentFlipBook = frameset;

			//copy over current image list data?
			for (Widget widget : frameset) {

				//	String pwidth = super.getElement().getStyle().getWidth();
				//String pheight = super.getElement().getStyle().getHeight();
				//	Log.info("parent size = "+pwidth+" "+pheight);
				//	if ( (pwidth!="" && pwidth!="100%") || (pheight!="" && pheight!="100%") ){
				////		Log.info("parent size specified, so image set to 100%.");					
				//		widget.setSize("100%", "100%");
				//	} else {
				widget.setWidth(""); //without this they will be 100% and thus fill up the container rather then take their natural image size
				widget.setHeight("");
				//	}

				//add default styles
				//widget.getElement().setDraggable(Element.DRAGGABLE_FALSE);
				applyStyleAndAttributes(widget.getElement());
				
				flipbookImageList.add( (Image)widget );	

			}



			return;

		}

		DeckPanel newFlipBook = new DeckPanel();
		flipBook.add(newFlipBook);

		Log.info("showing new deckpanel"+flipBook.getWidgetCount());

		//show the newly added set
		flipBook.showWidget(flipBook.getWidgetIndex(newFlipBook));


		//fill it
		for (int cp = 0; cp <= getFrametotal(); cp = cp + 1) {

			String url = (basefilename + "" + cp + "." + filenameext);
			Image newframe = new Image(url);

			//we should only set the size to 100% of the parent object has a set size
			//else the whole Animated Icon should take the size of the image its loaded
			//	String pwidth = super.getElement().getStyle().getWidth();
			//	String pheight = super.getElement().getStyle().getHeight();
			///	Log.info("parent size = "+pwidth+" "+pheight);
			//if ( (pwidth!="" && pwidth!="100%") || (pheight!="" && pheight!="100%") ){

			//	Log.info("parent size specified, so image set to 100%");				
			//newframe.setSize("100%", "100%");

			//} else {
			//	Log.info("no parent size specified, so using default size");			
			//;

			//}

			flipbookImageList.add(newframe);
			newFlipBook.add(newframe);

			//flipBook.add(newframe);
			newframe.setWidth(""); //not sure why we had these here?
			newframe.setHeight("");

			//add default styles
			//newframe.getElement().setDraggable(Element.DRAGGABLE_FALSE);
			applyStyleAndAttributes(newframe.getElement());
			
			lastImageAddedToDeck=newframe;
		}


		newFlipBook.showWidget(0);//first frame
		setCurrentframeVariable(0);	
		newFlipBook.setWidth("");
		newFlipBook.setHeight("");


		//once its full up, we add it to the collection
		cacheOfAllLists.put(basefilename, newFlipBook);
		currentFlipBook = newFlipBook;

		//trigger frame range
		//runOnFrameChangeActions();
	}

	protected void standardUrlBasedSetup(String FileZeroLocation, int NumOfFrames) {

		initWidget(imageContents);
		this.setUrl(FileZeroLocation);			
		setupVariables(FileZeroLocation, NumOfFrames);
		applyStyleAndAttributes(imageContents.getElement());
		// preload all images
		prefetchImages();
		timerSetup();
		
	}

	
	
	/** note if we later change the url we should re-check if its static or not **/
	protected void staticImageSetup(String FileZeroLocation) {

		initWidget(imageContents);
		this.setUrl(FileZeroLocation);	

		setFrametotalVar(0);
		setupVariables(FileZeroLocation, 1);
		
		// preload all images
		//prefetchImages();
		//timerSetup();
		
		applyStyleAndAttributes(imageContents.getElement());
		
	}

	/**
	 * Anything that applies to all modes that effects the html should go here.
	 * eg, style information or attributes 
	 **/
	private void applyStyleAndAttributes(Element ell){
		
		if (imageContents!=null){
			
			//super.getElement().setDraggable(Element.DRAGGABLE_FALSE);

			//super.getElement().setId("testid");
			//if we are using a image element we set the draggable attribute to false
		//	ell.setDraggable(Element.DRAGGABLE_FALSE); //Dragable false doesnt work
			//'draggable', false
			ell.setAttribute("draggable", "false");
		//	ell.setPropertyBoolean("draggable", false);
			//ell.setId("testid");
			
			//Log.severe("set spriteID...."+imageContents.getElement().getId());
			
		} else {
			Log.severe("null contents on sprite");
			
		}
		
	}

	private void setupVariables(String FileZeroLocation, int NumOfFrames) {
		currentfilename = FileZeroLocation;
		// this.setWidth("100%");

		//cache monitor
		//urlList.add(FileZeroLocation); //this line can be removed for production

		//this.setUrl(FileZeroLocation);
		setFrametotalVar(NumOfFrames - 1);

		//CHANGED to lastIndexOf to support IP address's in the url
		basefilename = FileZeroLocation.substring(0, (FileZeroLocation
				.lastIndexOf(".") - 1)); //upto the dot

		originalfilename = FileZeroLocation.substring(FileZeroLocation
				.lastIndexOf("/") + 1, (FileZeroLocation.lastIndexOf("."))); //between the slash and the dot

		filenameext = FileZeroLocation
				.substring(FileZeroLocation.lastIndexOf(".") + 1); //after the dot

		Log.info("basefilename = "+basefilename+" originalfilename="+originalfilename);


	}

	private void prefetchImages() {

		if(internalPreloader){


			for (int cp = 0; cp <= getFrametotal(); cp = cp + 1) {

			//	Log.info("prefetch_____________________"+basefilename + "" + cp + "." + filenameext);
				Image.prefetch((basefilename + "" + cp + "." + filenameext));
				//cache monitor
				//urlList.add((basefilename + "" + cp + "." + filenameext)); //this line can be removed for production

			}

		}



	}

	/** sets the current frames to the image prototype array specified */
	public void setFrames(AbstractImagePrototype[] SetFrames) {

		Frames.clear();
		setFrametotalVar(SetFrames.length-1);

		for (int cp = 0; cp < (getFrametotal()+1); cp = cp + 1) {
			//AbstractImagePrototype frame = SetFrames[cp];

			Frames.add(SetFrames[cp]);

			//System.out.print("\n adding"+SetFrames[cp].getUrl() );		
		}

		Frames.get(0).applyTo(imageContents);
		Log.info("just set up an image bundle");

		//set first size?

		//System.out.print("\n Current="+((Image)this.getWidget()).getUrl() );


	}


	public void timerSetup() {

		timer = new Timer() {

			@Override
			public void run() {

				checkAnimationFrame();
			}
		};
	}

	public void setURL(String FileZeroLocation, int NumOfFrames) {


		String newbasefilename = FileZeroLocation.substring(0, (FileZeroLocation.lastIndexOf(".") - 1));

		Log.info("::::::::::::::::::::::::setting url to "+newbasefilename+" with frames "+NumOfFrames);

		//only bother if different filename (or frames?)	
		if ((!newbasefilename.equalsIgnoreCase(basefilename))||(getFrametotal() != (NumOfFrames - 1))){

			/*

			frametotal = NumOfFrames - 1;

			basefilename = FileZeroLocation.substring(0, (FileZeroLocation
					.lastIndexOf(".") - 1));

			originalfilename = FileZeroLocation.substring(FileZeroLocation
					.lastIndexOf("/") + 1, (FileZeroLocation.lastIndexOf(".")));

			filenameext = FileZeroLocation
					.substring(FileZeroLocation.lastIndexOf(".") + 1);
			 */


			setupVariables(FileZeroLocation, NumOfFrames);

			//If frames over 1 then ensure timer is setup
			if (NumOfFrames>1){
				
				
				if (timer==null){					
					Log.info("setting up timer");					
					timerSetup();
				}			
				
				//if we were previously on still image we switch modes to normal (else we cant change urls/animate in future)
				if (mode==SpriteMode.staticimage){		
					mode=SpriteMode.normal; //note; we really should have an option to change this "assumed default" 
					
				}
				
			}
			

			if (mode==SpriteMode.normal || mode==SpriteMode.staticimage ){
				
				Log.info(":setting new url to "+newbasefilename+" then prefetching its frames");
				
				this.setUrl(FileZeroLocation);

				// preload all images
				prefetchImages();

			} else if (mode==SpriteMode.flipbook){

				//add to stack!
				fillDeckPanel();		

				Log.info(":::::::::::::::::::::::panel filled running frame change actions:"+newbasefilename);

				runOnFrameChangeActions();
				//set to first frame
				//flipBook.showWidget(0);
			}
			//cache monitor
			//urlList.add(FileZeroLocation); //this line can be removed for production


			/*
		for (int cp = 0; cp <= frametotal; cp = cp + 1) {

		//	Log.info("prefetch2__"+cp);
			Image.prefetch((basefilename + "" + cp + "." + filenameext));

		}*/

			//Log.info("::::::::::::::::::::::::set url to "+newbasefilename);
		}
	}

	//@Override
	public String getUrl() {

		if (mode==SpriteMode.flipbook){

			//get widget for current frame
			//int frameNumber = flipBook.getVisibleWidget();
			String url = ((Image) currentFlipBook.getWidget(getCurrentframe())).getUrl();
			//Log.info("getting url"+url);

			return url;			
			//note; we could also us

		}

		return currentfilename;

	}

	/** sets the time in miliseconds between frames.
	 * eg. 0 (missisipi) 1 (missisipi) 2...**/
	public void setFrameGap(int gap){
		timerDelay=gap;

	}

	/** Play the current animation forward.
	 * eg; 0123456 */
	public void setPlayForward() {

		Log.info("Set setAnimateOpen on "+this.basefilename);
		loop = false;
		animation_direction = animationDirection.open;


		startFrameUpdates();

		playuntill = 100 + getFrametotal();
	}

	/** Play the current animation forward and loop
	 * eg; 0123456012345601234560123456..... */
	public void setPlayLoop() {

		Log.info("Set setAnimate loop "+this.basefilename);
		animation_direction = animationDirection.open;
		loop = true;

		debug=true;
		//timer.run(); //change to update?
		checkAnimationFrame(); //emulates above run directly
		startFrameUpdates();

	}
	/** Play the current animation backward from the frame its currently on
	 * eg; 6543210 */
	public void setPlayBack() {

		Log.info("Set Animate Close "+this.basefilename);

		animation_direction = animationDirection.close;
		loop = false;
		
		
		startFrameUpdates();

		playuntill = 100 + getFrametotal();
	}
	/** Play the current animation forward and back
	 * eg; 0123456543210..... */
	public void setPlayForwardThenBack() {

		Log.info("Set setAnimateOpenThenClose");
		loop = false;
		animation_direction = animationDirection.open;
		close_after_open = true;
		startFrameUpdates();
		playuntill = 100 + getFrametotal();

	}
	/** Play the current animation forward and back continuously
	 * eg; 0123456543210123456543210..... */
	public void setPlayForwardThenBackLoop() {

		Log.info("loop bouncing active");

		loop = true;
		animation_direction = animationDirection.open;
		close_after_open = true;
		open_after_close = true;
		//timer.cancel();		
		//timer.scheduleRepeating(timerDelay);
		//cancelFrameUpdates(); //we used to cancel first, why?
		startFrameUpdates();
		
		playuntill = 100 + getFrametotal();

	}

	/** Play the current animation forward until the specified frame
	 * eg; 01234 */
	public void playUntill(int frame) {
		Log.info("playUntill active");

		loop = false;
		playuntill = frame;
		animation_direction = animationDirection.open;
		startFrameUpdates();
	}

	public void playBackUntill(int frame) {
		Log.info("playBackUntill active");

		loop = false;
		playuntill = frame;
		animation_direction = animationDirection.close;
		startFrameUpdates();
	}
	
	public void playForwardXframes(int frames) {
		Log.info("playForwardXframes active");
		if (currentlyAnimating == false) {
			loop = true;
			playuntill = getCurrentframe() + frames;
			if (playuntill > getFrametotal()) {
				playuntill = playuntill - (getFrametotal() + 1);
			}
			close_after_open = false;
			animation_direction = animationDirection.open;

			startFrameUpdates();
		}

	}

	public void nextFrameLoop() {

		setCurrentframeVariable(getCurrentframe() + 1);
		if (getCurrentframe() > getFrametotal()) {
			setCurrentframeVariable(0);
		}
		this.setFrame(getCurrentframe());

	}

	public void prevFrameLoop() {

		setCurrentframeVariable(getCurrentframe() - 1);
		if (getCurrentframe() < 0) {
			setCurrentframeVariable(getFrametotal());
		}

		this.setFrame(getCurrentframe());

	}

	public void nextFrame() {

		setCurrentframeVariable(getCurrentframe() + 1);
		
		if (getCurrentframe() > getFrametotal()) {
			setCurrentframeVariable(getFrametotal());
			if (runThisAfterOpen!=null){
				runThisAfterOpen.execute();
			}
		}
		
		this.setFrame(getCurrentframe());

	}
	
	public void prevFrame() {
		setCurrentframeVariable(getCurrentframe() - 1);
		if (getCurrentframe() < 0) {
			setCurrentframeVariable(0);
			if (runThisAfterClose!=null){
				runThisAfterClose.execute();
			}
		}
		this.setFrame(getCurrentframe());
	}

	public void gotoFrame(int newframe) {

		Log.info("setting frame to "+newframe+" total="+getFrametotal());

		if (newframe<=getFrametotal()){
			setCurrentframeVariable(newframe);
			this.setFrame(getCurrentframe());
		}

	}

	private void setFrame(int frame){

		setCurrentframeVariable(frame);

		if (mode == SpriteMode.bundle){

			if (Frames.size()>getCurrentframe()){
				//	Log.info("setting bundled image to frame:"+currentframe);
				Frames.get(getCurrentframe()).applyTo(imageContents);
				//runOnFrameChangeActions();
			} else {
				Log.info("tried setting bundledimage to frame:"+getCurrentframe()+" but its out of range");

			}

		} else if (mode == SpriteMode.flipbook){
			//used to be outer flipbook, now current inner one.
			if (currentFlipBook.getWidgetCount() >getCurrentframe()){
				//used to be outer flipbook, now current inner one.
				currentFlipBook.showWidget(getCurrentframe());
				
				//ensure dragable false is set
				//Element currentEl= currentFlipBook.getWidget(currentframe).getElement();				
				//applyStyleAndAttributes(currentEl);
				
				runOnFrameChangeActions();

			} else {
				Log.info("tried setting flipbook to frame::"+getCurrentframe()+" but its out of range");
				Log.info(":range was"+flipBook.getWidgetCount());

			}

		} else {
			currentfilename = basefilename + "" + getCurrentframe() + "." + filenameext;
			this.setUrl(currentfilename);
			//	Log.info("New current frame is :"+basefilename + "" + currentframe + "." + filenameext);
		}
	}

	/** goes to the first frame **/
	public void firstFrame() {

		setFrame(0);


	}

	public void setCommandToRunAfterOpen(Command newcommand){

		runThisAfterOpen = newcommand;

	}
	public void setCommandToRunAfterClose(Command newcommand){

		runThisAfterClose = newcommand;

	}
	public void clearRunthisAfterClose(){
		runThisAfterClose=null;
	};
	public void clearRunthisAfterOpen(){
		runThisAfterOpen=null;
	};

	public boolean isAnimating(){
		return currentlyAnimating;
	}


	/** Serializes the current animation state to a small string of the format;
	 *  currentframe_animation_direction_loop_playuntill
	 *  ie  5_1_0_8  (1 being true or open,  0 being false or close)
	 *  <br><br><br>
	 *  	String serialised = <br>
	 *           currentframe +"_"<br>
				+animation_direction.ordinal() +"_"<br>
				+loopstring +"_"<br>
				+close_after_openstring +"_"<br>
				+open_after_closestring +"_"<br>
				+playuntill;<br>
	 * **/
	public String serialiseAnimationState(){
		//we used to return nothing (not even the frame) for no animation
		//if (!isAnimating()){
		//	return ""; //empty string for no animation (should we just have the current frame instead?)
		//}
		
		if (!isAnimating()){			
			return ""+getCurrentframe(); //if theres no animation we return just that frame
		}
		
		String loopstring;

		if (loop){
			loopstring="1";
		} else {
			loopstring="0";
		}
		String close_after_openstring;

		if (close_after_open){
			close_after_openstring="1";
		} else {
			close_after_openstring="0";
		}

		String open_after_closestring;
		if (open_after_close){
			open_after_closestring="1";
		} else {
			open_after_closestring="0";
		}

		String serialised = getCurrentframe() +"_"
				+animation_direction.ordinal() +"_"
				+loopstring +"_"
				+close_after_openstring +"_"
				+open_after_closestring +"_"
				+playuntill;

		return serialised;

	}

	public void loadSerialisedAnimationState(String state){

		this.pauseAnimation();

		Log.info("___________________Loading state:"+state);

		Log.info("___________________Icom is animating atm:"+this.isAnimating());

		String statearray[] = state.split("_");

		setCurrentframeVariable(Integer.parseInt(statearray[0]));

		//if nothing else in the array then exit after setting the frame
		if (statearray.length==1){
			this.gotoFrame(getCurrentframe());			
			return;
		}

		if (statearray[1]=="0"){
			animation_direction = animationDirection.close ;
		} else {
			animation_direction = animationDirection.open ;
		}

		if (statearray[2]=="0"){
			loop=false;
		} else {
			loop=true;
		}


		if (statearray[3]=="0"){
			close_after_open=false;
		} else {
			close_after_open=true;
		}
		if (statearray[4]=="0"){
			open_after_close=false;
		} else {
			open_after_close=true;
		}
		playuntill = Integer.parseInt(statearray[5]);

		Log.info("currentframe:"+getCurrentframe()+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+getFrametotal());


	}
	//@Override (used to be public)
	private void setUrl(String url){		
		//super.setUrl(url);
		imageContents.setUrl(url);
		runOnFrameChangeActions();

	}

	private void runOnFrameChangeActions() {
		if (runThisOnFrameChange!=null){
			runThisOnFrameChange.execute();
		}
	}

	public void resumeAnimation(){
		
		Log.info("resuming animation after:"+timerDelay);
		Log.info("currentframe:"+getCurrentframe()+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+getFrametotal());

		//ensure timer exists
		startFrameUpdates();

	}

	public void pauseAnimation(){

		Log.info("animation stopped");

		currentlyAnimating=false;
		
		//cancel the timer if one exists
		cancelFrameUpdates();
		
	}
	public void setCommandToRunAfterFrameChange(Command command) {

		runThisOnFrameChange = command;
	}


	//The following methods are pass-throughs to the inner image	
	public HandlerRegistration addLoadHandler(LoadHandler loadHandler) {

		if (currentLoadHandler!=null){
			Log.info(" Load Handler removed");
			currentLoadHandler.removeHandler();
		}

		if (mode==SpriteMode.flipbook){			
			//currently we add the handler to the first image in the stack,
			//as we assume this will be whats needed first when the page is displayed.
			//in future we might want to (somehow?) only trigger the error or load handlers
			//when everything is done.
			//but then how would we return them to remove them? 
			//Apply the same handler to each image maybe? then have a "remove all handlers" function?
			//yup, go for that!

			//perhapes have an object that stores the "lastimageadded" and the handler goes to that?
			//or maybe the first frame of the last image set added?

			Image applyHandlerToThis = lastImageAddedToDeck; //used to be flipbookImageList.get(0);

			if (applyHandlerToThis==null){
				Log.info("WARNING: Load Handler set before image exists");

			}
			if (applyHandlerToThis.getParent()==null){
				Log.info("WARNING: Load Handler set when image has no parent");			
			}

			currentLoadHandler = applyHandlerToThis.addLoadHandler(loadHandler);


			return currentLoadHandler;

		}
		currentLoadHandler = imageContents.addLoadHandler(loadHandler);
		return currentLoadHandler;
	}

	public HandlerRegistration addErrorHandler(ErrorHandler errorHandler) {

		if (currentLoadErrorHandler!=null){
			Log.info(" error  Handler removeing existing");
			currentLoadErrorHandler.removeHandler();
		}

		if (mode==SpriteMode.flipbook){		
			//currently we add the handler to the first image in the stack,
			//as we assume this will be whats needed first when the page is displayed.
			//in future we might want to (somehow?) only trigger the error or load handlers
			//when everything is done.
			//but then how would we return them to remove them? 
			//Apply the same handler to each image maybe? then have a "remove all handlers" function?
			//yup, go for that!

			Image applyHandlerToThis = lastImageAddedToDeck; //used to be flipbookImageList.get(0);

			if (applyHandlerToThis==null){
				Log.info("WARNING: Error Handler set before image exists");

			}
			if (applyHandlerToThis.getParent()==null){
				Log.info("WARNING: Error Handler set when image has no parent");			
			}

			currentLoadErrorHandler = applyHandlerToThis.addErrorHandler(errorHandler);

			return currentLoadErrorHandler;

		}

		currentLoadErrorHandler = imageContents.addErrorHandler(errorHandler);
		return currentLoadErrorHandler;
	}



	public HandlerRegistration addMouseOverHandler(MouseOverHandler Handler) {
		if (currentMouseOverHandler!=null){
			currentMouseOverHandler.removeHandler();
		}

		currentMouseOverHandler = ((HasAllMouseHandlers) contents).addMouseOverHandler(Handler);
		return currentMouseOverHandler;

	}
	

	public HandlerRegistration addMouseOutHandler(MouseOutHandler Handler) {
		if (currentMouseOutHandler!=null){
			currentMouseOutHandler.removeHandler();
		}

		currentMouseOutHandler = ((HasAllMouseHandlers) contents).addMouseOutHandler(Handler);
		return currentMouseOutHandler;


	}

	public HandlerRegistration addClickHandler(ClickHandler Handler) {

		//alternative? apply to all images seperately
		//loop over all deckpanels, loop within applying.
		//if flickbook
		//loop over cacheOfAllLists
		//loop over all elements
		//apply the handler


		if (currentClickHandler!=null){
			currentClickHandler.removeHandler();
		}
		currentClickHandler = ((HasClickHandlers) contents).addClickHandler(Handler);



		return currentClickHandler;


	}


	public HandlerRegistration addMouseDownHandler(MouseDownHandler Handler) {


		if (currentMouseDownHandler!=null){
			currentMouseDownHandler.removeHandler();
		}

		currentMouseDownHandler = ((HasAllMouseHandlers) contents).addMouseDownHandler(Handler);

		return currentMouseDownHandler;		

	}
	public int getHeight(){				

		return contents.getOffsetHeight();
	}
	public int getWidth(){
		return contents.getOffsetWidth();
	}

	public boolean isOnBundleImageMode(){
		if (mode==SpriteMode.bundle){
			return true;
		}else {
			return false;
		}
	}
	public boolean isOnStaticImageMode(){
		if (mode==SpriteMode.staticimage){
			return true;
		}else {
			return false;
		}
	}
	/** 
	 * gets an image from the specific frame.
	 * THIS IMAGE SHOULD NOT BE MODIFIED
	 * In Bundle mode or mode, it is a copy of the current frame.
	 * In Flipbook it is the actual image of the frame.
	 * In normal (url) mode its the actual image if we are on the correct frame, else its a copy.
	 * Note; If its requesting a url that hasnt yet been preloaded the image wont have data in it yet!
	 * The image should also be cleared up after  
	 * **/
	public Image getImageAtFrame(int i) {

		if (mode == SpriteMode.bundle){

			if (Frames.size()>	i){

				return Frames.get(i).createImage();

			} else {
				Log.info("tried getting image from frame:"+getCurrentframe()+" in a bundledimage but its out of range");

			}

		} else if (mode == SpriteMode.flipbook){
			if (currentFlipBook.getWidgetCount() >i){

				return ((Image)currentFlipBook.getWidget(i)); //note we are typecasting here. As long as we only ever put images into the frame this should be safe

			} else {
				Log.info("tried getting frame ::"+i+" from flipbook but its out of range");
				Log.info(":range was"+flipBook.getWidgetCount());

			}

		} else {

			//if current frame is the one asked for return it;
			if (i==getCurrentframe()){
				return imageContents;

			} else {

				//need to load the correct frame else where
				//this should still have been preloaded so in theory should be instant. 
				//if not this whole thing needs to be done callback style
				Image temp = new Image();
				currentfilename = basefilename + "" + getCurrentframe() + "." + filenameext;
				temp.setUrl(currentfilename);
				temp.getElement().setId("_TEMP_IMAGE_");
				RootPanel.get().add(temp, 0, 0); //should be -1000 , -1000 to ensure it can be seen. Its only at 0,0 during testing


				return temp;


			}

			//	Log.info("New current frame is :"+basefilename + "" + currentframe + "." + filenameext);
		}

		return null;
	}

	public Image getCurrentImage() {


		return getImageAtFrame(getCurrentframe());
	}


	@Override
	public void setSize(String width,String height ){

		if (width!="" || height !=""){
			//as a size is being specified, the internal images must now fill
			//the space rather then setting the size as they wish
			changeInternalImagesTooFillContainer();
		} else {
			//if width or hieght is being cleared we remove those 100s
			changeInternalImagesTooNOTFillContainer();
		}

		super.setSize(width, height);

	}

	@Override
	public void setPixelSize(int width,int height){

		changeInternalImagesTooFillContainer(); 		
		super.setPixelSize(width, height);

	}
	private void changeInternalImagesTooNOTFillContainer() {
		Log.info("setting images to not fill container");

		//internally all image sizes must now be set too 100% to correctly fill the specified size
		if (mode ==SpriteMode.staticimage || mode ==SpriteMode.normal || mode == SpriteMode.bundle){
			imageContents.setSize("", "");
		} else if (mode == SpriteMode.flipbook){

			for (Image imageinbook : flipbookImageList) {
				imageinbook.setSize("", "");

			}

		}
	}
	private void changeInternalImagesTooFillContainer() {
		Log.info("setting images to fill container");

		//internally all image sizes must now be set too 100% to correctly fill the specified size
		if (mode ==SpriteMode.staticimage || mode ==SpriteMode.normal || mode == SpriteMode.bundle){
			imageContents.setSize("100%", "100%");
		} else if (mode == SpriteMode.flipbook){

			for (Image imageinbook : flipbookImageList) {
				imageinbook.setSize("100%", "100%");

			}

		}
	}
	
	@Override
	public void update(float delta) {
		
		timeSinceLastUpdate=timeSinceLastUpdate+ delta;
		
		//Log.info("timeSinceLastUpdate="+timeSinceLastUpdate +" timerDelay="+timerDelay);
		
		
		if (timeSinceLastUpdate>timerDelay){
			
			
			timeSinceLastUpdate = timeSinceLastUpdate % timerDelay; //time since last update is the remainder 
			//after its been divided by timerDelay as much as possible.
			//(That is the time "owed", how far we are behind)
			
			//if (x % 2 == 0){
			//	
			//}
			//timeSinceLastUpdate = 0;
			
			//update the frame
			//NB: This doesn't skip frames if the delta was more then one timerDelay, it probably should
			//we can do that by seeing how many times timerDelay goes into timeSinceLastUpdate
			checkAnimationFrame();
		}
				
	}
	

	private void startFrameUpdates(){
		DeltaTimerController.addObjectToUpdateOnFrame(this);
		
		
		//if (timer==null){
		//	timerSetup();
		//}		
		//timer.scheduleRepeating(timerDelay); //old method of timer
	}
	
	private void cancelFrameUpdates(){
		DeltaTimerController.removeObjectToUpdateOnFrame(this);
		
		
		///if (timer!=null){
		//	timer.cancel();
		//} //old method of timer
		
		
	}
	/**
	 * advance the current frame of the animation and test for loops/bounces/ends of animation
	 * setting the correct frame number if needed.
	 * This should only be fired if the appropriate time has elipsed since the last frame
	 */
	private void checkAnimationFrame() {
		currentlyAnimating = true;
		// first we check if the previous icon is loaded, if not, we
		// wait.



		setFrame(getCurrentframe());
		/*
		if (BundleImageMode){

			if (Frames.size()>currentframe){
				Frames.get(currentframe).applyTo(ThisIcon);
			}

		} else {							

			currentfilename = basefilename + "" + currentframe + "."
					+ filenameext;

			ThisIcon.setUrl(currentfilename);

			urlList.add(currentfilename);
		}*/



		if (debug){

			//Log.info("___________________set icon url="
			//		+ basefilename + "" + currentframe + "." + filenameext);

			//	Log.info("is now:"+ThisIcon.getUrl());

		}

		if (animation_direction == animationDirection.open) {
			setCurrentframeVariable(getCurrentframe() + 1);

		} else if (animation_direction == animationDirection.close) {
			setCurrentframeVariable(getCurrentframe() - 1);

		}

		// if out of range then stop, unless loop is set

		if (getCurrentframe() < 0) {
			//Log.info ("loop:"+loop);
			if (open_after_close == true) {

				//Log.info("opening after close..");

				setCurrentframeVariable(0);
				animation_direction = animationDirection.open;
				if (!loop){
					open_after_close = false;
				}
			} else if (loop) {
				//	Log.info("looping ... to"+frametotal);

				setCurrentframeVariable(getFrametotal());
			} else {
				setCurrentframeVariable(0);
				Log.info ("animation stoped due to frame less then zero_"+loop);
				cancelFrameUpdates();

				//if (runThisAfter!=null){
				//	runThisAfter.execute();
				//	}
				currentlyAnimating = false;

				if (runThisAfterClose!=null){
					Log.info("runThisAfterClose go!");
					runThisAfterClose.execute();
				}

				return;
			}

		}

		if ((getCurrentframe() == playuntill + 1)&&(animation_direction==animationDirection.open)) {
			Log.info ("animation openstoped due to frame more then play untill "+playuntill);

			setCurrentframeVariable(playuntill);
			playuntill = 100 + getFrametotal();
			cancelFrameUpdates();
			if (runThisAfterOpen!=null){
				runThisAfterOpen.execute();
			}
			currentlyAnimating = false;

		}

		if ((getCurrentframe() == playuntill - 1)&&(animation_direction==animationDirection.close)) {
			Log.info ("animation closestoped due to frame more then play untill "+playuntill);
			setCurrentframeVariable(playuntill);
			playuntill = 100 + getFrametotal();

			cancelFrameUpdates();
			if (runThisAfterOpen!=null){
				runThisAfterOpen.execute();
			}
			currentlyAnimating = false;

		}

		if (getCurrentframe() > getFrametotal()) {
			setCurrentframeVariable(getFrametotal());

			if (close_after_open == true) {

				//Log.info("now closing again");

				animation_direction = animationDirection.close;
				if (!loop){
					//Log.info("..and not set to open again");
					close_after_open = false;
				}

			} else if (loop == true) {
				//Log.info("looping ... to 0");

				setCurrentframeVariable(0);
			} else if (loop == false) {

				cancelFrameUpdates();					
				Log.info ("animation stoped due to frame at end ("+getFrametotal()+") and no loop set");
				currentlyAnimating = false;
				
				if (runThisAfterOpen!=null){
					runThisAfterOpen.execute();
				}


			}

		}
		// Image.prefetch(basefilename+""+currentframe+"."+filenameext);
	}

	/** method to return the url of the first frame of any animation
	 * 
	 * ie :Game Scenes/beforechurch/Objects/archflag/archflag13.png
	should become :Game Scenes/beforechurch/Objects/archflag/archflag0.png
	
	*if its an internal animation (eg  &lt;fire> its returned without change )*/
	public static String getFirstFrameURL(String URL) {
		
		//if its a internal animation we just return it without any change
		if (URL.startsWith("<")){
			return URL;
		}
		
		//strip the extension off
		String ext = URL.substring(URL.lastIndexOf(".") + 1); //after the dot
		String beforeext =  URL.substring(0,URL.lastIndexOf(".") + 1);
		
		//we loop back over the last digits to find the first thats not a number
		char currentCharacter = beforeext.charAt(beforeext.length()-2);
		//Log.info("start char="+currentCharacter);
		
		int i=beforeext.length()-2; 
		
		while (Character.isDigit(currentCharacter))
		{		
			i=i-1;	
			//get next character and repeat from while
			currentCharacter = beforeext.charAt(i);		
			
		}
		//Log.info("last char="+beforeext.charAt(i));
		
		//now we have the last number from the right we crop too just before it
		String newstart = beforeext.substring(0, i+1);
		//Log.info("new url char="+newstart);
		return newstart+"0."+ext;
	}

	
	/**
	 * @return the currentframe
	 */
	public int getCurrentframe() {
		return currentframe;
	}

	/**
	 * @param currentframe the currentframe to set
	 */
	public void setCurrentframeVariable(int currentframe) {
		this.currentframe = currentframe;
	}

	/**
	 * @return the frametotal
	 */
	public int getFrametotal() {
		return frametotal;
	}

	/**
	 * @param frametotal the frametotal to set
	 */
	public void setFrametotalVar(int frametotal) {
		this.frametotal = frametotal;
	}

	


}

