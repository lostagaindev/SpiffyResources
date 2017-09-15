package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * renamed from SpiffyFunctions, to split into GWT and non-GWT componants
 * 
 * @author darkflame
 *
 */
public class SpiffyFunctionsGWT {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyFunctionsGWT");
	
	public static native void reload() /*-{ 
	    $wnd.location.reload(); 
	   }-*/;
	
	
	/** returns the current userAgent strip as lower case
	 * used for browser-specific code (which as much as possible should be avoided) **/
	public static native String getUserAgent() /*-{
	return navigator.userAgent.toLowerCase();
	}-*/;	
	
	/** gets all the elements with a certain class  **/
	public static native NodeList<Element> getAllElementsWithClass(String classname) /*-{
		return $doc.getElementsByClassName(classname);
	}-*/;	
	 
	
	
//	public static class GotoURL implements ClickListener {
//		
//		String URL = "temp";
//		public GotoURL(String GotoHereWhenClicked)
//		{
//			URL = GotoHereWhenClicked;
//		}
//		public void onClick(Widget sender) {
//			System.out.println(URL);
//			if (URL.compareTo("SELF")==0){
//			
//				reload();
//				
//				
//			} else {
//			Window.open(URL,"_self", "");
//			}
//		}
//	}
	
	public static class GotoURL implements ClickHandler {
	
	String URL = "";
	String NewToken="-";
	String target = "_self";
	
	public GotoURL(String GotoHereWhenClicked)
	{
		URL = GotoHereWhenClicked;
	}
	public GotoURL(String GotoHereWhenClicked, String NewWindow)
	{
		URL = GotoHereWhenClicked;
		target = NewWindow;
	}
	public GotoURL(String GotoHereWhenClicked, String NewWindow, String Token)
	{
		URL = GotoHereWhenClicked;
		target = NewWindow;
		NewToken = Token;
	
		//Window.alert("token="+NewToken);
		
		
	}

	public void onClick(ClickEvent event) {

	//	Window.alert("token="+NewToken);
		if (NewToken.length()>0){
			
			//Window.alert("triggered:"+NewToken);
			History.newItem(NewToken);
			
			
		}
		
		if (URL.compareTo("SELF")==0){
		
			reload();
			
			
		} else {
			
			if (URL.length()>4){
				
					Window.open(URL,target, "");
					
			}
		
		
		}
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
public static class FrameGotoURL implements ClickListener {
		
		String URL = "temp";
		Frame thisframe;
		
		public FrameGotoURL(String GotoHereWhenClicked, Frame frame)
		{
			URL = GotoHereWhenClicked;
			thisframe = frame;
		}
		public void onClick(Widget sender) {
			thisframe.setUrl(URL);
		}
	}



	
	
	/**
	 * BROKEN: Used "SpiffyTextUti.splitNotWithinBrackets" instead with " as the starting and end bracket.
	 *  
	 *  Used to parse over a string into a array, ignoring things in quotes.
	 * 
	 * eg;
	 * "Test","test,test,test","testy"
	 * Becomes a array with elements:
	 * Test
	 * test,test,test
	 * testy
	 ***/
	public ArrayList<String> parseToArrayList_broken(String source,char seperator, String QuoteCharacter){
		
		ArrayList<String> arrayToReturn = new ArrayList<String>();
		
		int len = source.length();
		int i = 0;
		boolean inQuotes = false;
		String lastword="";
		while (i<len){
			
			char currentCharacter = source.charAt(i);
			i++;
			
			if (currentCharacter=='"'){
				inQuotes=!inQuotes;
			}
			
			if (inQuotes){
				lastword = lastword+currentCharacter;
				continue;
			}
			
						
			//else we look for a split
			if (currentCharacter==seperator){
				
				if (lastword.length()>0){
				arrayToReturn.add(lastword);
				}
				lastword = "";
				
			} 
			
			
			
			
			
		}
		
		
		
		return arrayToReturn;
		
		
		
		
	}
	
}
