package lostagain.nl.spiffyresources.client.spiffygwt;






import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Uses a LoadingIconVisualisation to visualize game loading progress
 * 
 * Current Types of visuals:
 * 
 * 
 * 
 * 
 * 
 * @author darkflame
 *
 */
public class SpiffyLoadingIcon extends VerticalPanel {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyLoadingIcon");

	enum IconModes {
		PieChart,
		Image
	}
	IconModes mode = IconModes.PieChart;

	/**
	 * we support any visulisatioin with the LoadingIconVisualisation interface
	 */
	LoadingIconVisualisation visualisationUsed;

	int width = 100; //used to be 70
	int height = 100;

	int xpadding = 220;
	int ypadding = 70; //used to be 3

	Timer auto_clock;
	boolean autoRun = true;

	int TotalUnitsToLoad = 0;
	double StepSize = 0;


	int currentStep = 0;

	Label progressLab = new Label("");

	boolean debugData=true;
	Label LoadingTime = new Label("Loading loading time...");
	long startTime=0;

	public long currentTime=0;

	public String[] loadingMessages;


	double ratio;

	Label LoadingMessage = new Label("...");

	/**
	 * defaults to canvas piechart
	 * @param autoRun
	 */
	public SpiffyLoadingIcon(boolean autoRun) {
		this(autoRun,IconModes.PieChart,null);
	}
	
	public SpiffyLoadingIcon(boolean autoRun,AbstractImagePrototype[] frames) {
		this(autoRun,IconModes.Image,frames);
	}

	public SpiffyLoadingIcon(boolean autoRun, IconModes mode,AbstractImagePrototype[] SetFrames) {
		this.autoRun = autoRun;

		//if (icon == null) {
		//	return;
		//}
		this.setSize((width + xpadding) + "px", (height + ypadding) + "px");



		if (mode==IconModes.PieChart) {
			//create pie chart
			visualisationUsed = new CanvasLoadingClock(width,height);
		} else if (mode==IconModes.Image){
			visualisationUsed = new ImageLoadingClock(SetFrames);

		}



		this.setHorizontalAlignment(ALIGN_CENTER);
		this.add(visualisationUsed.getWidget());
		this.add(progressLab);
		this.add(LoadingTime);
		this.add(LoadingMessage);

		startTime = System.currentTimeMillis();


		progressLab.setVisible(false);

		auto_clock = new Timer() {

			@Override
			public void run() {

				
				//ANG = ANG + 0.1;
				ratio = ratio + 0.05;//20 steps
				
				//updateClockToAngle(ratio);
				updateClockToRatio(ratio);
				



				if (debugData){
					currentTime= System.currentTimeMillis()-startTime;				
					LoadingTime.setText(":"+currentTime+":");
				}




			}

		};

	}


	/** sets the loading message that appears if we arnt using **/
	public void setDefaultLoadingMessage(String text){

		LoadingTime.setText(text);

	}

	@Override
	public void setPixelSize(int x,int y){

		width = x;
		height = y;
		setSize((width + xpadding) + "px", (height + ypadding) + "px");

		visualisationUsed.setPixelSize(x, y);

		//	canvasicon.setWidth(width + "px");
		//	canvasicon.setHeight(height + "px");
		//	canvasicon.setCoordinateSpaceWidth(width);
		//	canvasicon.setCoordinateSpaceHeight(height);

	}

	@Override
	public void onAttach() {

		//ANG = -Math.PI / 2;
		
		// clock.run();
		if (autoRun) {
			auto_clock.scheduleRepeating(200);
		}
		super.onAttach();
		startTime = System.currentTimeMillis();
	}

	public void stopAnimation() {
		auto_clock.cancel();
	}

	public void reset() {
	//	ANG = -Math.PI / 2;
		
		ratio =0;
		
		startTime = System.currentTimeMillis();

		this.visualisationUsed.reset();

	}

	public void startAnimation() {
		//ANG = -Math.PI / 2;
		
		ratio =0;
		
		auto_clock.cancel();
		auto_clock.scheduleRepeating(200);
	}

	/** sets the total "units" to be loaded, ie, number of images, files etc **/
	public void setTotalUnits(int setTotalUnitsToLoad) {
		TotalUnitsToLoad = setTotalUnitsToLoad;
		
	//	Log.severe("total loading units set to:"+setTotalUnitsToLoad);

		// calculate the step needed when advancing
		//StepSize = (Math.PI * 2) / TotalUnitsToLoad;
		StepSize = 1 / TotalUnitsToLoad;
		

		//update 
		updateVisuals();

	}

	/** adds one to total "units" to be loaded, ie, number of images, files etc **/
	public void addToTotalUnits(int addThis) {
		TotalUnitsToLoad = TotalUnitsToLoad+addThis;

		// calculate the step needed when advancing
		//StepSize = (Math.PI * 2) / TotalUnitsToLoad;
		
		StepSize = 1 / TotalUnitsToLoad;
		

	}
	public void setProgressLabelVisible(boolean status){

		progressLab.setVisible(status);
	}

	public void setProgressLabelToolTip(String tooltip){
		progressLab.setTitle(tooltip);
	}



	public void stepClockForward() {
		currentStep++;
		//update 
		updateVisuals();
	}
	
	
	public void setProgressTotal(int progrss) {
		currentStep=progrss;
		//update 
		updateVisuals();
	}
	
	Double previousRatio = -1.0;
	/**
	 * If true, means the visualiser of loading will never go backwards in progress, even if the total units to load goes up
	 * and the currently completed stands still
	 */
	boolean dontGoBackwards = true;
	
	private void updateVisuals() {
		progressLab.setText(":"+currentStep+" / "+TotalUnitsToLoad+":");

		// ANG = ANG + StepSize;

		// work out percentage complete
		Double ratioComplete = ((currentStep * 1.0) / (TotalUnitsToLoad*1.0)); 

		if (dontGoBackwards && ratioComplete<previousRatio){
			ratioComplete=previousRatio;
		} else {
			previousRatio=ratioComplete;
		}
		
		
		updateClockToRatio(ratioComplete);

		if (debugData){
			currentTime= System.currentTimeMillis()-startTime;				
			LoadingTime.setText("Time:"+currentTime+"");
		}

		if (loadingMessages!=null){
			//should be reused from earlier
			int current_message_number = (int) (Math.floor(ratioComplete*loadingMessages.length));

			String currentMessage = loadingMessages[current_message_number];

			LoadingMessage.setText(currentMessage);

		}
	}

	/**
	 * 
	 * @param ratioComplete
	 */
	private void updateClockToRatio(Double ratioComplete) {

		Log.info("New Loading Ratio is:"+ratioComplete);	
		if (ratioComplete<0 || ratioComplete>1){
			Log.warning("Ratio out of range");
			return;
		}
		
		this.visualisationUsed.updateToRatio(ratioComplete);
		
		
		if (ratioComplete >= 1.0 ) {
			// clear and restart
			//ANG = 0; //-Math.PI / 2;
			ratio=0;			
			reset();
			currentStep = 0;
			
		}

		

	}



	public void addStyleNameToLoadingMessage(String style) {

		LoadingMessage.addStyleName(style);
		LoadingTime.addStyleName(style);
		progressLab.addStyleName(style);

	}
	public void setStrokecolor(int R, int G, int B) {
		this.visualisationUsed.setStrokeColor( CssColor.make(R,G,B) );
	}

	public void setFillcolor(int R, int G, int B) {
		this.visualisationUsed.setFillColor ( CssColor.make(R,G,B) );
	}


	/** sets the color by taking it from  the "Color" setting in the class of the specified name.
	 * This lets css controll the style of the canvas
	 * Note; takes a fraction longer as a temp element needs to be created in order to look at the style from it**/
	public void setStrokecolor(String classname) {
		this.visualisationUsed.setStrokeColor ( getColorFromCSS(classname) );
	}

	/** sets the color by taking it from the "Color" setting in the class of the specified name.
	 * This lets css controll the style of the canvas
	 * Note; takes a fraction longer as a temp element needs to be created in order to look at the style from it**/
	public void setFillcolor(String classname) {

		this.visualisationUsed.setFillColor( getColorFromCSS(classname) );
	}

	private CssColor getColorFromCSS(String stylename){

		//create hidden element
		SimplePanel tempDiv = new SimplePanel();
		//ok, we add the style to an element
		tempDiv.setStylePrimaryName(stylename);
		//add to page
		RootPanel.get().add(tempDiv,0,0);


		String color = getStyleProperty(tempDiv.getElement(), "color");


		//get the computed css property 			
		CssColor colorFound = CssColor.make(color);

		tempDiv.removeFromParent();
		
		

		return colorFound;

	}

	public static native String getStyleProperty(Element el, String prop)  /*-{ 
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

	public void setLoadingMessages(String[] loadingMessages) {
		this.loadingMessages = loadingMessages;
	}

}
