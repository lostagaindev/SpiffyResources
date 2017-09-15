package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class SuggestBoxWithPasteDetection  extends SuggestBox {

	Logger logger = Logger.getLogger("SpiffyGWT.SuggestBoxWithPaste");
	
	public SuggestBoxWithPasteDetection(){
	super();
		
		this.sinkEvents(Event.ONPASTE);
	//	this.sinkEvents(Event.ONKEYDOWN);
	}

	
	public SuggestBoxWithPasteDetection(MultiWordSuggestOracle textSuggestions,
			TextBox textField) {
		
		super(textSuggestions,textField);
		
		this.sinkEvents(Event.ONPASTE);
	}


	/*
	public static native String setClipboardData(Event event)
		/*-{
		var input = document.getElementById ("toClipboard");
	            var textToClipboard = input.value;
	            
	            var success = true;
	            if ($wnd.clipboardData) { // Internet Explorer
	                $wnd.clipboardData.setData ("Text", textToClipboard);
	            }
	            else {
	                    // create a temporary element for the execCommand method
	                var forExecElement = CreateElementForExecCommand (textToClipboard);

	                        // Select the contents of the element 
	                         //   (the execCommand for 'copy' method works on the selection) 
	                SelectContent (forExecElement);

	                var supported = true;

	                    // UniversalXPConnect privilege is required for clipboard access in Firefox
	                try {
	                    if ($wnd.netscape && netscape.security) {
	                        netscape.security.PrivilegeManager.enablePrivilege ("UniversalXPConnect");
	                    }

	                        // Copy the selected content to the clipboard
	                        // Works in Firefox and in Safari before version 5
	                    success = document.execCommand ("copy", false, null);
	                }
	                catch (e) {
	                    success = false;
	                }
	                
	                    // remove the temporary element
	                document.body.removeChild (forExecElement);
	            }

	            if (success) {
	                alert ("The text is on the clipboard, try to paste it!");
	            }
	            else {
	                alert ("Your browser doesn't allow clipboard access!");
	            }
	            
	            }-
	            
	         */
	


		
	
	public static native String getClipboardData(Event event)
	/*-{
		var text = ".";

		// This should eventually work in Firefox: 
		// https://bugzilla.mozilla.org/show_bug.cgi?id=407983 
		if (event.clipboardData) // WebKit (Chrome/Safari) 
		{
			try {
				//text = event.clipboardData.getData("Text"); // or 'text/plain'?
				text = event.clipboardData.getData("text/plain");
				return text;
			} catch (e) {
				// Hmm, that didn't work. 
				return "paste didnt work"; 
			}
		}

		if ($wnd.clipboardData) // IE 
		{
			try {
				text = $wnd.clipboardData.getData("Text");
								
                $wnd.clipboardData.setData ("Text", text+"_data from clipboard main contain errors, post via notepad_");
            
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
			
		
			event.preventDefault();
			String text = getClipboardData(event);
			
			logger.log(Level.INFO, "________________pressed:" + text);
			logger.log(Level.INFO, "________________pressed:" + stripHTML(text));

			this.setText(getText()+stripHTML(text));
			
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
