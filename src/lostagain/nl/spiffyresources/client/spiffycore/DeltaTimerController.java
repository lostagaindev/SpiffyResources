package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

/** 
 * Note; comment needs updating
 * Designed to be a global timer that eventually be used for all animation events in a game.
 * 
 * The goal is to eventually have this class used to
 * a) Reduce the number of separate timers going
 * b) Make it easy to retractor into non-GWT versions of the JAM.
 * 
 * We have to consider two types of timer though;
 * - Timers for animating visual stuff, which should be done with "request animation frame" (see SceneObjectVisual AnimationScheduler )
 * - Timers that will go regardless of visibility. (ie, on a regular tick)
 * 
 * The second one we will only ever need one of, the first......no clue
 * 
 * NOTE: Regardless of the above we should never need to make a instance of this class. Just static methods applying to the whole game
 * **/
public abstract class DeltaTimerController {
	

	
	public static Logger Log = Logger.getLogger("SpiffyCore.DeltaTimerController");
	
	/**
	 * objects that should be updated on a fixed interval
	 */
	static HashSet<HasDeltaUpdate> objectsToUpdateOnTick = Sets.newHashSet();
	
	/**
	 * list of objects to remove from the update list if they have been canceled
	 */
	private static HashSet<HasDeltaUpdate> objectsToUpdateOnTickPendingRemoveList = Sets.newHashSet();

	//will ScheduleAnimation update to fire updates, rather then any timer
	/**
	 * objects to update each frame (ie, as often as possible at no fixed interal)
	 */
	static HashSet<HasDeltaUpdate> objectsToUpdateOnFrame = Sets.newHashSet();
	
	/**
	 * A temp list of delta update runnables given names so they can be retrieved later
	 */
	static HashBiMap<String, DeltaRunnable> namedRunnables = HashBiMap.create();
	
	
	
	/**
	 * list of objects to remove from the update list if they have been canceled
	 */
	private static HashSet<HasDeltaUpdate> objectsToUpdateOnFramePendingRemoveList = Sets.newHashSet();


	/**
	 * list of objects to add to the update list if they have been added while its running
	 */
	private static HashSet<HasDeltaUpdate> objectsToUpdateOnFramePendingAdd = Sets.newHashSet();

	
	
	
	protected static int FRAMEPERIOD = 100; //ms test


	
	
	/**
	 * The timer that runs every FRAMEPERIOD(ish)
	 */
	protected static boolean TimerRunning = false;
	public static boolean isTimerRunning() {
		return TimerRunning;
	}
	public static boolean isManualTickUpdate() {
		return manualTickUpdate;
	}


	private static boolean manualTickUpdate=false;
	private static boolean manualFrameUpdate=false;

	public static boolean isManualFrameUpdate() {
		return manualFrameUpdate;
	}

	
	
	/**
	 * if frames are currently updating or not
	 */
	private static boolean frameUpdatesRunning = false;


	/**
	 * Instance of ourselves used to allow overriding of some methods
	 * (a little crude, done as workaround to not being able to override statics.
	 * It shouldn't make a difference mind as there should only ever be one delta timer controller in use anyway)
	 */
	protected static DeltaTimerController instanceOfDeltaTimerController = null; // should be set in the setup function
	
	
	/**
	 * must be run before this controller will work.
	 * Will create one internal instance of itself to use.
	 * If setup has already been run this does nothing
	 * @return 
	 * 
	
	protected static void setup(){
		
		//create instance of ourselves if not already
		if (instanceOfDeltaTimerController == null){
			instanceOfDeltaTimerController =  new DeltaTimerController();	
		} else {
			Log.info("already setup");
		}
				
	} */
	
	/**
	 * must be run before this controller will work.
	 * Will create one internal instance of itself using the supplied subclass
	 * will override the default implementation
	 * 
	 * @return 
	 * 
	 */
	protected static void setup(DeltaTimerController subclassOfDeltaController){
		
		//create instance of ourselves
		instanceOfDeltaTimerController = subclassOfDeltaController;	
				
	}
	/**
	 * Adds to the list of objects that will have update() fired roughly every 100ms
	 * For visual elements please use addObjectToUpdateOnFrame instead as the browser should handle
	 * that better then a fixed interval
	 * 
	 * @param object
	 */
	static public void addObjectToUpdateOnTick(HasDeltaUpdate object){
		objectsToUpdateOnTick.add(object);
		//ensure tick is running
		if (!TimerRunning &&!manualTickUpdate){
			startFixedTimer();
		}
	}
	
	static public void removeObjectToUpdateOnTick(HasDeltaUpdate object){

		//if the timers not running remove straight away
		if (TimerRunning==false){
			objectsToUpdateOnTick.remove(object);
			//check if no objects left, if so stop the timer
			if (objectsToUpdateOnTick.isEmpty()){
				stopFixedTimer();
			}
		} else {
			//else shedule it to be removed after the current update
			//(attempting to alter the UpdateOnTick list during the update loop will cause a crash)
			objectsToUpdateOnTickPendingRemoveList.add(object);
		}
	}

	/**
	 * Adds to the list of objects that will have update() fired every time the browser gets a chance
	 * Uses requestAnimationFrame internally
	 * 
	 * @param object
	 */
	static public void addObjectToUpdateOnFrame(HasDeltaUpdate object){
		//ensure its not on the objectsToUpdateOnFramePendingRemoveList list 
		//(so if someone removes it, and it gets re-added within a single update it doesn't get removed at the end of the frame updates)
		objectsToUpdateOnFramePendingRemoveList.remove(object);
		
		//if we arnt animating we add straight away, else we add to the pending add list
		if (!frameUpdatesRunning){
			//add it to the object to update list (if its already there this doesn't do anything)
			objectsToUpdateOnFrame.add(object);
		} else {
			objectsToUpdateOnFramePendingAdd.add(object);
		}
			
		
		//ensure frame is running
		if (!frameUpdatesRunning){ // && !manualUpdate
			Log.info("starting frame updates");
			
			instanceOfDeltaTimerController.startFrameUpdates();
		}
	}

	/**	 
	 * Removes the object from the list of things to update on Frame.
	 * If the update is currently running, then its added to the pending remove list to be removed at the end of the next update
	 * 
	 * @param object
	 */
	static public void removeObjectToUpdateOnFrame(HasDeltaUpdate object){
		
		//ensure its not on the objectsToUpdateOnFramePendingAdd list 
		//(so if someone adds it, and it gets removed within a single update it doesn't get added at the end of the frame updates)
		objectsToUpdateOnFramePendingAdd.remove(object);
		
		
		if (!frameUpdatesRunning){
			objectsToUpdateOnFrame.remove(object);

			//check if no objects left, if so stop the timer
			if (objectsToUpdateOnFrame.isEmpty()){
				instanceOfDeltaTimerController.stopFrameUpdates();
			}
			
		} else {
			//Schedule for removal later to avoid concurrent modification error
			objectsToUpdateOnFramePendingRemoveList.add(object);
		}
		

		DeltaTimerController.namedRunnables.inverse().remove(object);
		
	}


	/**
	 * If you have special code that needs to run to start your updates override this.
	 * 	updateAllFrameObjects(currentDelta); should be called each frame after this function is started
	 * and before stopFrameUpdates is called
	 * You can also use frameUpdatesRunning to check if 	updateAllFrameObjects(currentDelta); should be called
	 * 
	 */
	protected void startFrameUpdates() {

		//KEEP THE COMMENTED CODE FOR A REFERANCE FOR NOW;
		
		/*
		frameUpdateCode = new AnimationCallback() {
			long lastUpdate=System.currentTimeMillis();

			@Override
			public void execute(double timestamp) {

				if (!frameUpdatesRunning) {
					return;					
				}	

				//work out the time since the last update
				long currentTime = System.currentTimeMillis();
				long currentDelta =  currentTime - lastUpdate;
				lastUpdate = currentTime;
				
				//Log.info("delta="+currentDelta+" (currenttime="+currentTime+" - lastUpdate"+lastUpdate+")");
								
				updateAllFrameObjects(currentDelta);

				//reschedule the update
				if (frameUpdatesRunning){
					AnimationScheduler.get().requestAnimationFrame(this);
				}

			}
		};
		
		*/
		
		frameUpdatesRunning = true;
		//animationRunner = AnimationScheduler.get().requestAnimationFrame(frameUpdateCode);

	}

	/**
	 * Override this to supply your own start/stop optimizations.
	 * This function fires when theres nothing currently to update.
	 * If you are updating on a timer you can use it to stop the timer
	 * 
	 * Just remember to call super.stopFrameUpdates if you do that
	 */
	protected void stopFrameUpdates() {
		///if (animationRunner!=null){
		//	animationRunner.cancel();
		//}
		
		frameUpdatesRunning = false;
	}



	

	static private void stopFixedTimer(){
		instanceOfDeltaTimerController.stopFixedTimerIMPL();
		
		
		TimerRunning = false;
	}

	
	protected abstract void stopFixedTimerIMPL();
	
	
	/**
	 * stops all updating objects, clearing all lists
	 */
	static public void stopUpdatingAllObjects(){

		DeltaTimerController.stopFixedTimer();
		instanceOfDeltaTimerController.stopFrameUpdates();
		
		namedRunnables.clear();
		objectsToUpdateOnFramePendingRemoveList.clear();
		objectsToUpdateOnTick.clear();
		objectsToUpdateOnFramePendingAdd.clear();

	}
	
	/**
	 * Start the fixed interval timer.
	 * 
	 * startFixedTimerIMPL must be implemented for fixed timings to work.
	 * 
	 */
	static private void startFixedTimer(){
		
		instanceOfDeltaTimerController.startFixedTimerIMPL();
		
	}




/**
 * if you wish to also support fixed time updates, you must supply a timer implementation here that fires 
 * updateAllTickObjects(currentDelta) every FRAMEPERIOD
 * 
 */
	protected abstract void startFixedTimerIMPL();

	/**
	 * This is to fire updates  manually on the objects in the objectsToUpdateOnTick list.
	 * Once this is called all future ticks need to be manually fired
	 * 
	 * @param delta - time since last frame	 * 
	 * @return 
	 **/
	static public void updateTick(long delta){
		setManualTickUpdateMode(true);
		updateAllTickObjects(delta);		
	}

	
	/**
	 * The maximum delta update time in ms.
	 * If the delta update of a frame goes past this, it will be CAPPED to this value.
	 * 
	 * Beware a bit when using this as things can happen slower in realtime then they should, making them go out of sycn with non delta based timers
	 * (ie, if 2 seconds past, but the delta caps it at 1 second you have visually stopped such a big jump in motion, but it still took 2 real seconds for one second of motion)
	 * 
	 */
	protected static long maxDelta = 1000; 

	/**
	 * The min delta update time in ms.
	 * If the delta update of a frame is less then this, no update will happen 
	 * (defaults to 10ms, set to -1 to turn off)
	 */
	static long minDelta = 10; 
	
	/**
	 * The maximum delta update time in ms.
	 * If the delta update of a frame goes past this, it will be CAPPED to this value.
	 * 
	 * Beware a bit when using this as things can happen slower in realtime then they should, making them go out of sycn with non delta based timers
	 * (ie, if 2 seconds past, but the delta caps it at 1 second you have visually stopped such a big jump in motion, but it still took 2 real seconds for one second of motion)
	 * 
	 * Use -1 to turn off
	 */
	public static void setMaxDelta(long maxDelta) {
		DeltaTimerController.maxDelta = maxDelta;
	}
	/**
	 * The min delta update time in ms.
	 * If the delta update of a frame is less then this, no update will happen 
	 * (defaults to 10ms, set to -1 to turn off)
	 */
	public static void setMinDelta(long minDelta) {
		DeltaTimerController.minDelta = minDelta;
	}
	
	/**
	 * This is to fire updates  manually on the objects in the objectsToUpdateOnFrame list.
	 * If this is called manually update mode for Frames is set to manual controll. (ie, if you call this
	 * manually once, you have to call it for each frame)
	 * 
	 * @param delta - time since last frame	 * 
	 * @return 
	 **/
	public static void updateFrame(double delta){

		setManualFrameUpdateMode(true);
		
		if (delta<minDelta && minDelta!=-1){
		//	Log.info("(delta was too small to bother updating:"+delta);
			
			return;
		}
		
		if (delta>maxDelta && maxDelta!=-1){
			Log.warning("_______________WARNING Delta was :"+delta+" which is a tad high. maxDelta was "+maxDelta);
			
			//We loop over many updateAllFrameObjects to catch up
			//To take  a extreme case, lets say maxdelta is 500ms and the frame took 3555ms
			//3555ms / 500 = 7.11 (That is 7.11 updates should have happened in the time it took for this one)
			//round up 8   (as we cant have fractional updates)
			//3555/8=444.375  (Divide the time this frame took by the 8 updates that need to happen)
			//So we run 8 times with delta 444.375
			
			double numOfUpdatesThatShouldHaveHappened         = Math.ceil(delta/maxDelta);
			double deltaOfEachSubFrameUpdate                  = delta / numOfUpdatesThatShouldHaveHappened;
			Log.warning("_______________numOfUpdatesThatShouldHaveHappened :"+numOfUpdatesThatShouldHaveHappened+" with delta of: "+deltaOfEachSubFrameUpdate);
			Log.warning("______________ now running catchup");
			
			delta = deltaOfEachSubFrameUpdate;
			
			for (int i = 0; i < numOfUpdatesThatShouldHaveHappened; i++) {
				updateAllFrameObjects(delta);		
			}
			
			//delta=maxDelta;
			return;
			
		}
		
		
		updateAllFrameObjects(delta);		
	}



	/**
	 * if set to true no timers fire and instead update() must be called manually with the delta.
	 * This lets other implementations handle timing however they like
	 * @param b
	 */
	private static void setManualFrameUpdateMode(boolean state) {
		manualFrameUpdate = state;

	}
	
	/**
	 * if set to true no timers fire and instead update() must be called manually with the delta.
	 * This lets other implementations handle timing however they like
	 * @param b
	 */
	private static void setManualTickUpdateMode(boolean state) {
		manualTickUpdate = state;

		if (manualTickUpdate){
				stopFixedTimer();
		} else {
			//if its set to false then we ensure the timer is running if there's stuff to update
			if (!objectsToUpdateOnTick.isEmpty()){
				startFixedTimer();
			}
		}

	}
	
	static protected void updateAllTickObjects(long currentDelta){


		//Note; If there is a lot of objects in this list, or some of their updates are slow,
		//then the delta wont be accurate by the time we get to the end of it 
		for (HasDeltaUpdate object : objectsToUpdateOnTick) {
			object.update(currentDelta);
		}

		//remove anything from the objectToUpdateList if needed
		for (HasDeltaUpdate object : objectsToUpdateOnTickPendingRemoveList) {			
			objectsToUpdateOnTick.remove(object);
		}

		//clear the pending remove list
		objectsToUpdateOnTickPendingRemoveList.clear();

		//finally stop updating if nothing is list to update
		if (objectsToUpdateOnTick.isEmpty()){
			stopFixedTimer();
		}

	}

	protected static void updateAllFrameObjects(double currentDelta){
		

		//Update delta runnable first (these basically act the same as the DeltaUpdate code in HasDeltaUpdate implementing objects)

		//Note; If there is a lot of objects in this list, or some of their updates are slow,
		//then the delta wont be accurate by the time we get to the end of it 
		Iterator<HasDeltaUpdate> objectUpdateIterator = objectsToUpdateOnFrame.iterator();
		
		while (objectUpdateIterator.hasNext()) {
			HasDeltaUpdate object = (HasDeltaUpdate) objectUpdateIterator.next();
			
			if (objectsToUpdateOnFramePendingRemoveList.contains(object)){
				//if its on the removal list we remove it
				objectUpdateIterator.remove(); //we remove it via the iterator command as this is the only save way to remove from a list being used to loop over
				objectsToUpdateOnFramePendingRemoveList.remove(object); //remove from the remove list
				continue;
			}
			
			object.update((float)currentDelta); //cast is a little bad here
		}
		
		/*
		 * 
		for (HasDeltaUpdate object : objectsToUpdateOnFrame) {
			
			
			if (objectsToUpdateOnFramePendingRemoveList.contains(object)){
				continue; //NOTE Checking this here might be a bit inefficient. We might be able to use a iterator to neaten this whole thing?
			}
			
			object.update(currentDelta);
			
		}*/

		//add anything to the objectToUpdateList list if needed
		//for (HasDeltaUpdate object : objectsToUpdateOnFramePendingAdd) {
		//	objectsToUpdateOnFrame.add(object);
		//}
		objectsToUpdateOnFrame.addAll(objectsToUpdateOnFramePendingAdd);
		
		//clear the pending add list
		objectsToUpdateOnFramePendingAdd.clear();
		
		
		//remove anything from the objectToUpdateList if needed
		//for (HasDeltaUpdate object : objectsToUpdateOnFramePendingRemoveList) {			
		//	objectsToUpdateOnFrame.remove(object);
		//}

		//clear the pending remove list
		//objectsToUpdateOnFramePendingRemoveList.clear();

		//finally stop updating if nothing is list to update
		if (objectsToUpdateOnFrame.isEmpty()){
			instanceOfDeltaTimerController.stopFrameUpdates();
		}

	}
	
	/**
	 * returns the {@literal HashSet<HasDeltaUpdate>} of everything set to fire on a timer
	 * This is purely used for debugging reasons
	 * @return  {@literal HashSet<HasDeltaUpdate>}
	 */
	public static HashSet<HasDeltaUpdate> getObjectsCurrentlyUpdatedEachFrame() {		
		return objectsToUpdateOnFrame;
	}
	
	/**
	 * returns the {@literal HashSet<HasDeltaUpdate>} of everything set to fire on a tick
	 * This is purely used for debugging reasons
	 *
	 * @return {@literal HashSet<HasDeltaUpdate>}
	 */
	public static HashSet<HasDeltaUpdate> getObjectsCurrentlyUpdatedEachTick() {		
		return objectsToUpdateOnTick;
	}
	

	/**
	 * A Delta runnable is just a class that implements HasDeltaUpdate
	 * It easily lets you have inline code that JAMTimer can take to update.
	 * Thing of it as a sort of replacement for a Timer task.
	 * 
	 * @author Tom	 *
	 */
	static public abstract class DeltaRunnable implements HasDeltaUpdate {
		
		/**
		 * A optional name you can supply which helps if you want to retrieve this runnable later
		 */
		private String RunnablesName = "";
		
		/**
		 * A enum to just help with fadein/out systems
		 */
		public enum fadeState {
			delay,fadeIn,hold,fadeOut
		}	
		public fadeState currentFadeState = null;

		/**
		 * put the code you want to run each update here. The delta is the rough time since the last frame.
		 */
		@Override
		public abstract void update(float delta); //its abstract as classes using this must write their own update code!		

		/**
		 * removes this DeltaRunnable from the JAMTimer update list once the current update cycle has finished
		 * This basically lets DeltaRunnable replace a Timer more easily
		 */
		public void cancel(){
			objectsToUpdateOnFramePendingRemoveList.add(this);
			
			DeltaTimerController.namedRunnables.remove(this);
		}

		
		
		/**
		 * A optional name you can supply which helps if you want to retrieve this runnable later
		 * It will be automatically removed from internal lists once its canceled, however.
		 */
		public void setRunnablesName(String runnablesName) {
			RunnablesName = runnablesName;
			DeltaTimerController.namedRunnables.put(RunnablesName,this);
		}

		public String getRunnablesName() {
			return RunnablesName;
		}

	}


	public static boolean isFrameUpdatesRunning() {
		return frameUpdatesRunning;
	}
	
	public static DeltaRunnable getNamedRunnable(String name){
		return DeltaTimerController.namedRunnables.get(name);
	}
	


	

}



