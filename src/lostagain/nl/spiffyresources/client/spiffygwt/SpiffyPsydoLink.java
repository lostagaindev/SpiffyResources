package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;

public class SpiffyPsydoLink extends Label {
	
	Label PsydoLink = this;
	boolean enabled = true;
	
	public SpiffyPsydoLink(String text){
		
		super.setText(text);
		
		//set style and rollovers

		PsydoLink.setStylePrimaryName("spiffypsydolink");
		
		PsydoLink.addMouseOverHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				if (enabled){
				PsydoLink.addStyleName("spiffypsydolinkHover");					
				}
			}			
		});
		
		PsydoLink.addMouseOutHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent event) {
				if (enabled){
				PsydoLink.removeStyleName("spiffypsydolinkHover");	
				}
				
			}
			
		});
		
		// Done!
		
	}
	
	public void setRolloverEnabled(boolean state){
		enabled=state;
		PsydoLink.removeStyleName("spiffypsydolinkHover");			
	}

	
}
