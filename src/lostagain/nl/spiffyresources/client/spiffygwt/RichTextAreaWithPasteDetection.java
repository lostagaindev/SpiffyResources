package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;

public class RichTextAreaWithPasteDetection extends RichTextArea {

	Logger logger = Logger.getLogger("SpiffyGWT.RichTextAreaWithPaste");

	public RichTextAreaWithPasteDetection() {
		super();
		
		this.sinkEvents(Event.ONPASTE);
		this.sinkEvents(Event.ONKEYDOWN);
		
	}

	public static native String getClipboardData(Event event)
	/*-{
		var text = ".";

		// This should eventually work in Firefox: 
		// https://bugzilla.mozilla.org/show_bug.cgi?id=407983 
		if (event.clipboardData) // WebKit (Chrome/Safari) 
		{
			try {
				text = event.clipboardData.getData("Text"); // or 'text/plain'?
				text2 = event.clipboardData.getData("text/plain");
				return text+text2;
			} catch (e) {
				// Hmm, that didn't work. 
				return "paste didnt work"; 
			}
		}

		if ($wnd.clipboardData) // IE 
		{
			try {
				text = $wnd.clipboardData.getData("Text");
				return text;
			} catch (e) {
				// Hmm, that didn't work.
				return "paste didnt work2"; 
			}
		}

		return text;
	}-*/;

	@Override
	public void onBrowserEvent(Event event) {
		
		super.onBrowserEvent(event);
		if (Window.Navigator.getUserAgent().contains("Opera") )
		{
			return; //Opera doesnt need paste fixing, as it does it itself anyway.
		}
		
		switch (event.getTypeInt()) {
				
		case Event.ONPASTE: {
			
			logger.log(Level.INFO, "________________Paste");

			event.preventDefault();
			String text = getClipboardData(event);
			
			logger.log(Level.INFO, "________________Paste" + text);
			logger.log(Level.INFO, "________________V pressed:" + stripHTML(text));

			break;
		}

		case Event.ONKEYDOWN: {


			
			if (event.getCtrlKey()) {

				logger.log(Level.INFO, " cntr ");
				
				if (event.getCharCode() == 'V') {
					
					logger.log(Level.INFO, " cntr+V pressed");

					event.preventDefault();
					String text = getClipboardData(event);
					
					logger.log(Level.INFO, "________________V pressed:" + text);
					logger.log(Level.INFO, "________________V pressed:" + stripHTML(text));

				}

			}

			break;
		}

		}


	}
	
	private String stripHTML(String text){
		
		HTML text2 = new HTML(text);
		
		return text2.getText();
		
	}
}
