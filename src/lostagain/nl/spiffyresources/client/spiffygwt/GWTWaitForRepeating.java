package lostagain.nl.spiffyresources.client.spiffygwt;

import com.darkflame.client.interfaces.GenericWaitForRepeating;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/** Much like "wait for" this is to allow interface or other updates needed 
 * while the semantic engine is processing.
 * In this case, its designed for tasks that repeat within the engine.
 * If your using GWT, you can think of this as a scheduleIncremental **/
public class GWTWaitForRepeating implements GenericWaitForRepeating {

	public void scheduleAfter(final MyRepeatingCommand runAfter) {		
		
		Scheduler.get().scheduleIncremental(new RepeatingCommand(){
			
			@Override
			public boolean execute() {	
				
				return runAfter.execute();
			}
			
		});
		
	}
	
	
	

}
