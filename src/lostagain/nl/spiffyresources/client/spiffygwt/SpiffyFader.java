package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class SpiffyFader extends AbsolutePanel{

	public SpiffyFader (){
		this.setStyleName("fader");

       // DOM.setStyleAttribute(this.getElement(), "z-index", "1000");
		// DOM.setStyleAttribute(this.getElement(), "zIndex", "1000");
		   
        
        DOM.setStyleAttribute(this.getElement(), "width", "100%");
        DOM.setStyleAttribute(this.getElement(), "height", "100%");
        
	}
	public void hide(){

		this.removeFromParent();
	}
}
