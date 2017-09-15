package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Logger;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.user.client.Timer;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;

/**
 * Implementation of DeltaTimerController for GWT
 * Remember to call setup() to make it work
 * 
 * @author Tom
 *
 */
public class SpiffyGWTDeltaController extends DeltaTimerController {
	
	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyGWTDeltaController");
	
	/**
	 * the code that handles frame updates (can be prepared in advance, but it just fires updates on the loop
	 */
	private static AnimationCallback frameUpdateCode;
	
	/**
	 * holds the animation handler, letting the frame updates be canceled even before they fire.
	 */
	private static AnimationHandle animationRunner;
	
	

	/**
	 * Must be run before any timers or frame updates will work!
	 * Put this near the start of your code.
	 * 
	 * (Internally it makes a instance of JAMTimer. There should only ever be one, which is why its handled here
	 * and not in a new JAMTimer statement)
	 * @return 
	 * 
	 */
	public static void setup(){
		Log.info("DeltaTimerController setup");
		//create instance of ourselves
		DeltaTimerController.setup(new SpiffyGWTDeltaController()); //This gives a copy of this to DeltaTimerController superclass so it can call 
		//public startFromUpdates and stopFrameUpdates from static methods
		
	}
	
	
	//Here we override the start from updates function to trigger how GWT/webapps should be updated
		//That is, each time the browser thinks a animation from is available.
		@Override
		public void startFrameUpdates() {
			Log.info("starting animation frame updates");
			

			frameUpdateCode = new AnimationCallback() {
				long lastUpdate=System.currentTimeMillis();

				@Override
				public void execute(double timestamp) {

					if (!isFrameUpdatesRunning()) {
						return;					
					}	

					//work out the time since the last update
					long currentTime = System.currentTimeMillis();
					long currentDelta =  currentTime - lastUpdate;
					lastUpdate = currentTime;
					
					//Log.info("delta="+currentDelta+" (currenttime="+currentTime+" - lastUpdate"+lastUpdate+")");
									
					updateAllFrameObjects(currentDelta);

					//reschedule the update
					if (isFrameUpdatesRunning()){
						AnimationScheduler.get().requestAnimationFrame(this);
					}

				}
			};
			
			super.startFrameUpdates();
			//frameUpdatesRunning = true;
			animationRunner = AnimationScheduler.get().requestAnimationFrame(frameUpdateCode);
			

		}
		
		
		@Override
		protected void stopFrameUpdates() {
			if (animationRunner!=null){
				animationRunner.cancel();
			}

			super.stopFrameUpdates();
			//frameUpdatesRunning = false;
		}
		
		

		//-----------------
		//Fixed timer handleing;
		//---------------------
		
		static Timer tickTimer;
		//all timing subject to change this is at the moment just a temp implementation to get the interfacing right
			static long lastUpdateTime = 0;

		@Override
		protected void startFixedTimerIMPL() {

			if (tickTimer==null){
				tickTimer = new Timer() {
					@Override
					public void run() {

						//work out the time since the last update
						long currentTime = System.currentTimeMillis();
						long currentDelta =  currentTime - lastUpdateTime;
						lastUpdateTime = currentTime;


						DeltaTimerController.updateAllTickObjects(currentDelta);		
					}
				};
			}

			tickTimer.scheduleRepeating(FRAMEPERIOD);
			lastUpdateTime= System.currentTimeMillis();
			TimerRunning = true;
		}

		@Override
		protected void stopFixedTimerIMPL() {

			if (tickTimer!=null){
				tickTimer.cancel();
			}
		}

}
