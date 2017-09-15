package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.Timer;
import java.util.TimerTask;


public class JavaDeltaController extends DeltaTimerController {

	static TimerTask tickTimerTask;
	static Timer currentTimer;
	static long lastUpdateTime = 0;

	@Override
	protected void startFixedTimerIMPL() {

		if (currentTimer==null){
			currentTimer = new Timer();
			if (tickTimerTask==null){
				tickTimerTask = new TimerTask() {
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
		}

		currentTimer.scheduleAtFixedRate(tickTimerTask, FRAMEPERIOD, FRAMEPERIOD);

		//tickTimer.scheduleRepeating(FRAMEPERIOD);


		lastUpdateTime= System.currentTimeMillis();
		TimerRunning = true;
	}

	@Override
	protected void stopFixedTimerIMPL() {

		if (currentTimer!=null){
			currentTimer.cancel();
		}
	}

}
