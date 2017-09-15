package lostagain.nl.spiffyresources.client.spiffygwt;

import com.darkflame.client.interfaces.GenericWaitFor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class GWTWaitForBrowserToUpdate implements GenericWaitFor {

	@Override
	public void scheduleAfter(final Runnable runAfter) {		
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {			
			@Override
			public void execute() {
				runAfter.run();
				
				
			}
		});
		
	}

}
