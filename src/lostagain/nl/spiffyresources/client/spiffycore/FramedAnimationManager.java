package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.logging.Logger;

import lostagain.nl.spiffyresources.client.spiffygwt.HasFrameControl;





/**
 * A class to manage the updating of any frame based animation
 * Designed to be used with 
 * 
 * @author darkflame
 *
 */
public abstract class FramedAnimationManager implements HasDeltaUpdate, HasFrameControl {
	public enum animationDirection{
		close,open;
	}

	
	
	public static Logger Log = Logger.getLogger("SpiffyCore.FramedAnimationManager");
	
	public boolean debug             = false;
	
	

	public animationDirection animation_direction = animationDirection.open;
	boolean close_after_open = false;
	boolean open_after_close = false;
	
	/**
	 * Or, more accurately, the number of the last frame
	 * If the count is from zero, this is the total - 1
	 */
	private int lastFrameNum = 1; //TODO: Rename this to something more intuative like lastframenumber (note: eclipse seems to mess up when using refractor rename here. At least version Mars did)
	public int currentframe = 0;

	//in order for the new delta based time system to work with frames we need to track how long since the last frame change
	public double timeSinceLastUpdate = 0;
			
	
	// image array
	public int playuntill = 100 + getLastFrameNumber();
	public int timerDelay = 100;
	
	public boolean loop = false;
	public boolean currentlyAnimating = false;
	//-------------------------

	

	public Runnable runThisAfterOpen     = null;
	public Runnable runThisAfterClose    = null;
	public Runnable runThisOnFrameChange = null;  //used mostly for debug work, don't overdo this!

	private void cancelFrameUpdates(){
		
		DeltaTimerController.removeObjectToUpdateOnFrame(this);	
				
	}
	
	/**
	 * advance the current frame of the animation and test for loops/bounces/ends of animation
	 * setting the correct frame number if needed.
	 * This should only be fired if the appropriate time has eclipsed since the last frame
	 */
	
	public void checkAnimationFrame() {
		currentlyAnimating = true;
	
		setFrame(currentframe);
	

		


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

				currentframe = getLastFrameNumber();
			} else {
				currentframe = 0;
				Log.info ("animation stoped due to frame less then zero_"+loop);
				cancelFrameUpdates();

				//if (runThisAfter!=null){
				//	runThisAfter.run();
				//	}
				currentlyAnimating = false;

				if (runThisAfterClose!=null){
					Log.info("runThisAfterClose go!");
					fireRunThisAfterClose();
				}

				return;
			}

		}

		if ((currentframe == playuntill + 1)&&(animation_direction==animationDirection.open)) {
			Log.info ("animation openstoped due to frame more then play untill "+playuntill);

			currentframe = playuntill;
			playuntill = 100 + getLastFrameNumber();
			cancelFrameUpdates();
			if (runThisAfterOpen!=null){
				fireRunThisAfterOpen();
			}
			currentlyAnimating = false;

		}

		if ((currentframe == playuntill - 1)&&(animation_direction==animationDirection.close)) {
			Log.info ("animation closestoped due to frame more then play untill "+playuntill);
			currentframe = playuntill;
			playuntill = 100 + getLastFrameNumber();

			cancelFrameUpdates();
			if (runThisAfterOpen!=null){
				fireRunThisAfterOpen();
			}
			currentlyAnimating = false;

		}

		if (currentframe > getLastFrameNumber()) {
			currentframe = getLastFrameNumber();

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

				cancelFrameUpdates();					
				Log.info ("animation stopped due to frame at end ("+getLastFrameNumber()+") and no loop set. actions to fire now:"+(runThisAfterOpen!=null)+" disableNextPostAnimationCommands:"+disableNextPostAnimationCommands);
				currentlyAnimating = false;
				if (runThisAfterOpen!=null){
					fireRunThisAfterOpen();
				}


			}

		}
		// Image.prefetch(basefilename+""+currentframe+"."+filenameext);
	}
	


	public void clearRunthisAfterClose(){
		runThisAfterClose=null;
	}

	public void clearRunthisAfterOpen(){
		runThisAfterOpen=null;
	}

	public void gotoFrame(int newframe) {

		Log.info("setting frame to "+newframe+" total="+getLastFrameNumber());

		if (newframe<=getLastFrameNumber()){
			currentframe = newframe;
			this.setFrame(currentframe);
		}

	}
	public boolean isAnimating(){
		return currentlyAnimating;
	}
	public void loadSerialisedAnimationState(String state){

		this.pauseAnimation();

		Log.info("___________________Loading state:"+state);

		Log.info("___________________Icom is animating atm:"+this.isAnimating());

		String statearray[] = state.split("_");

		currentframe = Integer.parseInt(statearray[0]);

		//if nothing else in the array then exit after setting the frame
		if (statearray.length==1){
			this.gotoFrame(currentframe);			
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

		Log.info("currentframe:"+currentframe+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+getLastFrameNumber());


	};
	public void nextFrame() {

		currentframe = currentframe + 1;
		
		if (currentframe > getLastFrameNumber()) {
			currentframe = getLastFrameNumber();
			if (runThisAfterOpen!=null){
				fireRunThisAfterOpen();
			}
		}
		
		this.setFrame(currentframe);

	}

	private void fireRunThisAfterOpen() {
		if(!disableNextPostAnimationCommands){
			runThisAfterOpen.run();
		}
		
		disableNextPostAnimationCommands = false;
	};

	public void nextFrameLoop() {

		currentframe = currentframe + 1;
		if (currentframe > getLastFrameNumber()) {
			currentframe = 0;
		}
		this.setFrame(currentframe);

	}

	public void pauseAnimation(){
		Log.info("animation paused");
		currentlyAnimating=false;
		
		//cancel the timer if one exists
		cancelFrameUpdates();
		
	}

	
	/**
	 * Stops animation and resets settings
	 */
	public void stopAnimation(){
		close_after_open = false;
		open_after_close = false;
		loop=false;
		
		Log.info("animation stopped");
		currentlyAnimating=false;
		
		//cancel the timer if one exists
		cancelFrameUpdates();
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
			playuntill = currentframe + frames;
			if (playuntill > getLastFrameNumber()) {
				playuntill = playuntill - (getLastFrameNumber() + 1);
			}
			close_after_open = false;
			animation_direction = animationDirection.open;

			startFrameUpdates();
		}

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
	
	public void prevFrame() {
		currentframe = currentframe - 1;
		if (currentframe < 0) {
			currentframe = 0;
			if (runThisAfterClose!=null){
				fireRunThisAfterClose();
			}
		}
		this.setFrame(currentframe);
	}

	private void fireRunThisAfterClose() {
		if (!disableNextPostAnimationCommands){
			runThisAfterClose.run();
		}
		disableNextPostAnimationCommands = false;
	}
	public void prevFrameLoop() {

		currentframe = currentframe - 1;
		if (currentframe < 0) {
			currentframe = getLastFrameNumber();
		}

		this.setFrame(currentframe);

	}
	public void resumeAnimation(){
		
		Log.info("resuming animation after:"+timerDelay);
		Log.info("currentframe:"+currentframe+"\n playuntill = "+playuntill+" loop:"+String.valueOf(loop)+" total frames="+getLastFrameNumber());
		currentlyAnimating=true;
		
		//ensure timer exists
		startFrameUpdates();

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
			return ""+currentframe; //if theres no animation we return just that frame
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

		String serialised = currentframe +"_"
				+animation_direction.ordinal() +"_"
				+loopstring +"_"
				+close_after_openstring +"_"
				+open_after_closestring +"_"
				+playuntill;

		return serialised;

	}

	public void setCommandToRunAfterClose(Runnable newcommand){

		runThisAfterClose = newcommand;
	}
	
	public void setCommandToRunAfterFrameChange(Runnable command) {

		runThisOnFrameChange = command;
	}

	public void setCommandToRunAfterOpen(Runnable newcommand){
		runThisAfterOpen = newcommand;
	}

	/**
	 * The code used to change the frame visually goes here.
	 * This code will be called very often to changed the frame, normally due to a internal delta timer passing a regular interval
	 * @param currentframe
	 */
	public abstract void setFrame(int currentframe);

	/** sets the time in miliseconds between frames.
	 * eg. 0 (missisipi) 1 (missisipi) 2...**/
	public void setFrameGap(int gap){
		timerDelay=gap;

	}
	
	/**
	 * @return the frametotal
	 */
	public int getLastFrameNumber() {
		return lastFrameNum;
	}

	/**
	 * NOT the frame total. This should be frame total-1 due to stupid error
	 * @param frametotal the frametotal to set
	 */
	public void setFrametotal(int frametotal) {
		this.lastFrameNum = frametotal;
		if (this.currentframe>this.lastFrameNum){
			currentframe=frametotal;
		}
	}
	
	/**
	 * @param frametotal the frametotal to set
	 */
	public void setFrameTotalNew(int frametotal) {
		this.lastFrameNum = frametotal-1;
		if (this.currentframe>this.lastFrameNum){
			currentframe=frametotal;
		}
	}
	/** Play the current animation backward from the frame its currently on
	 * eg; 6543210 */
	public void setPlayBack() {

	//	Log.info("Set Animate Close "+this.basefilename);

		animation_direction = animationDirection.close;
		loop = false;
		close_after_open = false;
		open_after_close = false;
	
		
		startFrameUpdates();

		playuntill = 100 + getLastFrameNumber();
	}

	/** Play the current animation forward.
	 * eg; 0123456 */
	public void setPlayForward() {

	//	Log.info("Set setAnimateOpen on "+this.basefilename);
		loop = false;
		animation_direction = animationDirection.open;
		close_after_open = false;
		open_after_close = false;
	

		startFrameUpdates();

		playuntill = 100 + getLastFrameNumber();
	}

	
	
	/** Play the current animation forward and back
	 * eg; 0123456543210..... */
	public void setPlayForwardThenBack() {

		Log.info("Set setAnimateOpenThenClose");
		loop = false;
		animation_direction = animationDirection.open;
		close_after_open = true;
		open_after_close = false;
	
		startFrameUpdates();
		playuntill = 100 + getLastFrameNumber();

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
		
		playuntill = 100 + getLastFrameNumber();

	}
	

	/** Play the current animation forward and loop
	 * eg; 0123456012345601234560123456..... */
	public void setPlayLoop() {

	//	Log.info("Set setAnimate loop "+this.basefilename);
		animation_direction = animationDirection.open;
		loop = true;
		close_after_open = false;
		open_after_close = false;
	
		debug=true;
		//timer.run(); //change to update?
		checkAnimationFrame(); //emulates above run directly
		startFrameUpdates();

	}

	private void startFrameUpdates(){
		
		
		DeltaTimerController.addObjectToUpdateOnFrame(this);
		
		
		//if (timer==null){
		//	timerSetup();
		//}		
		//timer.scheduleRepeating(timerDelay); //old method of timer
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

	boolean disableNextPostAnimationCommands=false;
	public boolean isDisableNextPostAnimationCommands() {
		return disableNextPostAnimationCommands;
	}

	public void disableNextPostAnimationCommands(boolean disable) {
		disableNextPostAnimationCommands=disable;
	}

	
	public boolean hasRunAfterOpen() {		
		if (this.runThisAfterOpen!=null){
			return true;
		}		
		return false;
	}


	public boolean hasRunAfterClose() {		
		if (this.runThisAfterClose!=null){
			return true;
		}		
		return false;
	}
}
