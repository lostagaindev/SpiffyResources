package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;


public class SpiffySprite extends Image {


	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffySprite");
	boolean debug=false;
	
	int frametotal = 1;
	public int currentframe = 0;

	// you can assign a unique name to this icon, to help finding it later in a
	// list of icons
	public String uniqueName = "";

	enum animationDirection{
		close,open;
	}
	
	animationDirection animation_direction = animationDirection.open;
	
	public String basefilename = "";
	
	
	String originalfilename = "";

	String currentfilename = "";
	public String filenameext = "png";
	boolean close_after_open = false;
	boolean open_after_close = false;
	Timer timer;
	int timerDelay=100;
	
	// image array
	int playuntill = 100 + frametotal;

	private boolean loop = false;
	private boolean currentlyAnimating = false;
	
	Command runThisAfterOpen=null;
	Command runThisAfterClose=null;
	Command runThisOnFrameChange = null;  //used mostly for debug work, dont overdo this!
	
	//internal cache list (should be toggled off for production
	
	/** keeps a store of all urls asked for **/
	static public HashSet<String> urlList = new HashSet<String>();
	

	List<AbstractImagePrototype> Frames = new ArrayList<AbstractImagePrototype>();
    public Boolean BundleImageMode = false;    
	final SpiffySprite ThisIcon = this;
	
	/** This is a special image that supports animations **/
public SpiffySprite(AbstractImagePrototype[] SetFrames, String name){
		
		BundleImageMode = true;
	    
		Log.info("\n creating icon "+name );
		
		frametotal = (SetFrames.length -1);
		

		currentfilename = name;
		
		for (int cp = 0; cp < (frametotal+1); cp = cp + 1) {
			Frames.add(SetFrames[cp]);
		//System.out.print("\n adding"+SetFrames[cp].getUrl() );
		
		}
		
		//this.setWidget(Frames.get(0));
		Frames.get(0).applyTo(this);
		

		timerSetup();
		
	}
	/** This is a special image that supports animations **/
	public SpiffySprite(String FileZeroLocation, int NumOfFrames) {

		this.setUrl(FileZeroLocation);
		currentfilename = FileZeroLocation;
		// this.setWidth("100%");
		
		//cache monitor
		urlList.add(FileZeroLocation); //this line can be removed for production
		
		//this.setUrl(FileZeroLocation);
		frametotal = NumOfFrames - 1;
		
		//CHANGED to lastIndexOf to support IP address's in the url
		basefilename = FileZeroLocation.substring(0, (FileZeroLocation
				.lastIndexOf(".") - 1));

		originalfilename = FileZeroLocation.substring(FileZeroLocation
				.lastIndexOf("/") + 1, (FileZeroLocation.lastIndexOf(".")));

		filenameext = FileZeroLocation
				.substring(FileZeroLocation.lastIndexOf(".") + 1);

		// preload all images

		for (int cp = 0; cp <= frametotal; cp = cp + 1) {

		//	Log.info("prefetch__"+cp);
			Image.prefetch((basefilename + "" + cp + "." + filenameext));
			//cache monitor
			urlList.add((basefilename + "" + cp + "." + filenameext)); //this line can be removed for production
			
		}

		timerSetup();
	}

	public void timerSetup() {
		
		timer = new Timer() {

			@Override
			public void run() {
				
				currentlyAnimating = true;
				// first we check if the previous icon is loaded, if not, we
				// wait.

				
				
				
				
				if (BundleImageMode){
					
					if (Frames.size()>currentframe){
					Frames.get(currentframe).applyTo(ThisIcon);
					}
					
				} else {							

					currentfilename = basefilename + "" + currentframe + "."
						+ filenameext;
					
					ThisIcon.setUrl(currentfilename);
					
					urlList.add(currentfilename);
				}
				
				
				
				if (debug){
					
				//Log.info("___________________set icon url="
				//		+ basefilename + "" + currentframe + "." + filenameext);
				
			//	Log.info("is now:"+ThisIcon.getUrl());
				
				}

				if (animation_direction == animationDirection.open) {
					currentframe = currentframe + 1;

				} else if (animation_direction == animationDirection.close) {
					currentframe = currentframe - 1;

				}

				// if out of range then stop, unless loop is set

				if (currentframe < 0) {
					//Log.info ("loop:"+loop);
					if (open_after_close == true) {
						
						//Log.info("opening after close..");
						
						currentframe = 0;
						animation_direction = animationDirection.open;
						if (!loop){
							open_after_close = false;
							}
					} else if (loop) {
					//	Log.info("looping ... to"+frametotal);
						
						currentframe = frametotal;
					} else {
						currentframe = 0;
						Log.info ("animation stoped due to frame less then zero_"+loop);
						this.cancel();
						
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
				
				if ((currentframe == playuntill + 1)&&(animation_direction==animationDirection.open)) {
						Log.info ("animation openstoped due to frame more then play untill "+playuntill);
				
					currentframe = playuntill;
					playuntill = 100 + frametotal;
					this.cancel();
					if (runThisAfterOpen!=null){
						runThisAfterOpen.execute();
						}
					currentlyAnimating = false;
					
				}
				
				if ((currentframe == playuntill - 1)&&(animation_direction==animationDirection.close)) {
						Log.info ("animation closestoped due to frame more then play untill "+playuntill);
					currentframe = playuntill;
					playuntill = 100 + frametotal;
				
					this.cancel();
					if (runThisAfterOpen!=null){
						runThisAfterOpen.execute();
						}
					currentlyAnimating = false;
					
				}
				
				if (currentframe > frametotal) {
					currentframe = frametotal;

					if (close_after_open == true) {
						
						//Log.info("now closing again");
						
						animation_direction = animationDirection.close;
						if (!loop){
							//Log.info("..and not set to open again");
						close_after_open = false;
						}
						
					} else if (loop == true) {
						//Log.info("looping ... to 0");
						
						currentframe = 0;
					} else if (loop == false) {
						
						this.cancel();					
						Log.info ("animation stoped due to frame at end ("+frametotal+") and no loop set");
						currentlyAnimating = false;
						if (runThisAfterOpen!=null){
						runThisAfterOpen.execute();
						}
					

					}

				}
				// Image.prefetch(basefilename+""+currentframe+"."+filenameext);
			}
		};
	}

	public void setURL(String FileZeroLocation, int NumOfFrames) {

		
		String newbasefilename = FileZeroLocation.substring(0, (FileZeroLocation
				.lastIndexOf(".") - 1));
		
		//Log.info("::::::::::::::::::::::::setting url to "+newbasefilename);
		
		//only bother if different filename (or frames?)	
		if ((!newbasefilename.equalsIgnoreCase(basefilename))||(frametotal != (NumOfFrames - 1))){
			
		frametotal = NumOfFrames - 1;
			
		basefilename = FileZeroLocation.substring(0, (FileZeroLocation
					.lastIndexOf(".") - 1));
		filenameext = FileZeroLocation
				.substring(FileZeroLocation.lastIndexOf(".") + 1);
		
		originalfilename = FileZeroLocation.substring(FileZeroLocation
				.lastIndexOf("/") + 1, (FileZeroLocation.lastIndexOf(".")));

		
		this.setUrl(FileZeroLocation);
		
		//cache monitor
		urlList.add(FileZeroLocation); //this line can be removed for production
		
		
		// preload all images

		for (int cp = 0; cp <= frametotal; cp = cp + 1) {

		//	Log.info("prefetch2__"+cp);
			Image.prefetch((basefilename + "" + cp + "." + filenameext));

		}

		//Log.info("::::::::::::::::::::::::set url to "+newbasefilename);
		}
	}

	@Override
	public String getUrl() {
		
		return currentfilename;
		
	}

	public void setFrameGap(int gap){
		timerDelay=gap;
		
	}
	public void setAnimateOpen() {

		Log.info("Set setAnimateOpen");
		loop = false;
		animation_direction = animationDirection.open;
		timer.scheduleRepeating(timerDelay);
		
		playuntill = 100 + frametotal;
	}

	public void setAnimateLoop() {

		Log.info("Set setAnimate loop");
		animation_direction = animationDirection.open;
		loop = true;
		
		debug=true;
		timer.run();
		timer.scheduleRepeating(timerDelay);
		
	}

	public void setAnimateClose() {
		
		Log.info("Set Animate Close");
		
		animation_direction = animationDirection.close;
		loop = false;
		timer.scheduleRepeating(timerDelay);
		
		playuntill = 100 + frametotal;
	}

	public void setAnimateOpenThenClose() {

		Log.info("Set setAnimateOpenThenClose");
		loop = false;
		animation_direction = animationDirection.open;
		close_after_open = true;
		timer.scheduleRepeating(timerDelay);
		playuntill = 100 + frametotal;

	}

	public void setAnimateOpenThenCloseLoop() {
		
		Log.info("loop bouncing active");
		
		loop = true;
		animation_direction = animationDirection.open;
		close_after_open = true;
		open_after_close = true;
		timer.cancel();		
		timer.scheduleRepeating(timerDelay);
		playuntill = 100 + frametotal;

	}
	
	public void playUntill(int frame) {
		Log.info("playUntill active");
		
		loop = false;
		playuntill = frame;
		animation_direction = animationDirection.open;
		timer.scheduleRepeating(timerDelay);
	}
	
	public void playBackUntill(int frame) {
		Log.info("playBackUntill active");
		
		loop = false;
		playuntill = frame;
		animation_direction = animationDirection.close;
		timer.scheduleRepeating(timerDelay);
	}
	public void playForwardXframes(int frames) {
		Log.info("playForwardXframes active");
		if (currentlyAnimating == false) {
			loop = true;
			playuntill = currentframe + frames;
			if (playuntill > frametotal) {
				playuntill = playuntill - (frametotal + 1);
			}
			close_after_open = false;
			animation_direction = animationDirection.open;

			timer.scheduleRepeating(timerDelay);
		}

	}

	//doesnt yet support bundles
	public void nextFrameLoop() {

		currentframe = currentframe + 1;
		if (currentframe > frametotal) {
			currentframe = 0;
		}
		this.setFrame(currentframe);
//		if (BundleImageMode){
//			if (Frames.size()>currentframe){
//			Frames.get(currentframe).applyTo(ThisIcon);
//			}
//		} else {		
//		currentfilename = basefilename + "" + currentframe + "." + filenameext;
//		this.setUrl(currentfilename);
//		// MyApplication.DebugWindow.addText("set frame to:"+currentfilename);
//		System.out.print(basefilename + "" + currentframe + "." + filenameext);
//		}
	}
	//doesnt yet support bundles
	public void prevFrameLoop() {

		currentframe = currentframe - 1;
		if (currentframe < 0) {
			currentframe = frametotal;
		}
		
		this.setFrame(currentframe);
		
//		if (BundleImageMode){
//			if (Frames.size()>currentframe){
//			Frames.get(currentframe).applyTo(ThisIcon);
//			}
//		} else {
//		currentfilename = basefilename + "" + currentframe + "." + filenameext;
//		this.setUrl(currentfilename);
//		System.out.print(basefilename + "" + currentframe + "." + filenameext);
//		}
	}

	public void nextFrame() {

		currentframe = currentframe + 1;
		if (currentframe > frametotal) {
			currentframe = frametotal;
		}
		this.setFrame(currentframe);
		
		//currentfilename = basefilename + "" + currentframe + "." + filenameext;
	//	this.setUrl(currentfilename);
		//System.out.print(basefilename + "" + currentframe + "." + filenameext);
	}

	
	
	/** bundle image mode not supported here yet, but its a easy fix (should use internal setFrame) **/
	public void gotoFrame(int newframe) {

		Log.info("setting frame to "+newframe+" total="+frametotal);
		
		if (newframe<=frametotal){
		currentframe = newframe;
		this.setFrame(currentframe);
		//currentfilename = basefilename + "" + currentframe + "." + filenameext;
		//this.setUrl(currentfilename);
		}
		
	}
	
	private void setFrame(int frame){
		
		currentframe=frame;
		
		if (BundleImageMode){
			if (Frames.size()>currentframe){
			Frames.get(currentframe).applyTo(ThisIcon);
			} 
		} else {
			currentfilename = basefilename + "" + currentframe + "." + filenameext;
			this.setUrl(currentfilename);
			Log.info("New current frame is :"+basefilename + "" + currentframe + "." + filenameext);
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
	
	
	/** serialises the current animation state to a small string of the format;
	 *  currentframe_animation_direction_loop_playuntill
	 *  ie  5_1_0_8  (1 being true or open,  0 being false or close)
	 * **/
	public String serialiseAnimationState(){
		
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
		
		String serialised = currentframe +"_"
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
		
		currentframe = Integer.parseInt(statearray[0]);
		
		
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

		Log.info("currentframe:"+currentframe+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+frametotal);
		
		
	}
	@Override
	public void setUrl(String url){		
		super.setUrl(url);
		
		if (runThisOnFrameChange!=null){
			runThisOnFrameChange.execute();
		}
		
	}
	
	public void resumeAnimation(){
		Log.info("resuming animation after:"+timerDelay);

		Log.info("currentframe:"+currentframe+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+frametotal);
	
		timer.scheduleRepeating(timerDelay);
		
	}
	
	public void pauseAnimation(){
		
		Log.info("animation stopped");
		
		currentlyAnimating=false;
		timer.cancel();
	}
	public void setCommandToRunAfterFrameChange(Command command) {
		
		runThisOnFrameChange = command;
	}
}

