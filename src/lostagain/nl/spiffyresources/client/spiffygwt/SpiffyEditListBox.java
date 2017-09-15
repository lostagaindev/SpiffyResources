package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.TextBox;

public class SpiffyEditListBox extends SpiffyListBox {

	TextBox editBox = new TextBox();
	String writableURL;
	SpiffyListBox thisWidget=this;
	
	Logger Log = Logger.getLogger("SpiffyGWT.SpiffyEditListBox");
	
	
	public SpiffyEditListBox(){
		
		super();
		//replace header
		super.setHeader(editBox);
		
		//add listener to header
		//if enter is pressed, add to list
		editBox.addKeyDownHandler(new KeyDownHandler() {			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode()==KeyCodes.KEY_ENTER){
					
					thisWidget.addItem(editBox.getText());	
					
					updateStringsDatafile();
				}
					
				
			}
		});
		
		//auto close
		mouseCatcher.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				thisWidget.DropDownContainer.setOpen(false);
			}
		});
		
	}
	
	protected void updateStringsDatafile() {
		String sendThis="Content=";
		for (String line : super.FieldNames) {
			sendThis=sendThis+line+"\n";//errr windows newline?
		};
		
		
		sendThis = URL.encode(sendThis);
		
		//request the file
		RequestBuilder suggestionlistFile = new RequestBuilder(RequestBuilder.POST,
				writableURL);
		suggestionlistFile.setHeader("Content-Type","application/x-www-form-urlencoded");
		
		System.out.println("sending list");
		
		try {
			suggestionlistFile.sendRequest(sendThis, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	System.out.println("failed to send data");
		        }

		        public void onResponseReceived(Request request, Response response) {
		        	
		        //	String SuggestionsString=response.getText();
		        //	
		        //	 String[] stringlist = SuggestionsString.split("\r|\n|\r\n");
		        //	Collection<String> test = Arrays.asList(stringlist);
		        	 
		        	//thisbox.clear();
		        	//thisbox.addAll(test);
		        	
		        }
		      });
		    } catch (RequestException ex) {
		    
		    	System.out.println("get request list failed");
		    }	
				
		
	}

	/** This url must point to a *.php file that accepts a POST function
	 * to update a text file list
	 * The message sent will simply by the key 
	 *
	 * "Content"  
	 * 
	 * And the data being the newline separated list of strings.
	 * Each strings should go on a new line.
	 * 
	 * @url the location of the file reciving this data**/
	
	public void setWritableURL(String url){
		writableURL=url;
	}
	
	
	
}
