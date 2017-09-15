package lostagain.nl.spiffyresources.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


	/** This is a HTML element that can load data from a url and replace widgets
	 * Its lets us fake a frame-like functionality without using an iFrame **/
	
public class htmlFrame extends HTMLPanel{

		static Logger Log = Logger.getLogger("htmlFrame");
	


	private boolean replacement = false;
	private ArrayList <Widget> WithThis = new ArrayList<Widget>();
	private ArrayList <String> ReplaceThis = new ArrayList<String>();
	private String LastURL = "";
	public HTMLPanel container = this;
	private boolean ReplaceLinks = false;
	ArrayList<Hyperlink> URLArray = new ArrayList<Hyperlink>();	
	ArrayList<Element> cssLineArray = new ArrayList<Element>();
	
	Boolean AjaxCrawlableMode = false; //set this to add ! after the hash ! 

	/** This is a HTML element that can load data from a url and replace widgets
	 * Its lets us fake a frame-like functionality without using an iFrame **/
	public htmlFrame(String html) {
		super(html);
		 
		 super.setSize("100%", "100%");
		this.setSize("100%", "100%");
	}
	
	public void setUrl(String URL){
		
		Log.info("____loading a html frame:"+URL);
		Log.info("____AjaxCrawlableMode = "+AjaxCrawlableMode);
		Log.info("____replace links = "+ReplaceLinks);
		this.getElement().setId("HTMLFRAME_"+URL);
		
		//Window.alert("getting url"+URL);
		
		//clear previous css added
		Iterator<Element> it = cssLineArray.iterator();
		
		while(it.hasNext()){
			
		Element cssLinkBit = it.next();
		cssLinkBit.getParentElement().removeChild(cssLinkBit);			
			
		}
		//clear array
		cssLineArray.clear();
		//----------------------
		
		LastURL = URL;
		
		RequestBuilder checklogin = new RequestBuilder(RequestBuilder.GET,
				URL);
			
			try {
				checklogin.sendRequest("", new RequestCallback() {
			        public void onError(Request request, Throwable exception) {
			        	System.out.println("email of review failed");
			        }

			        public void onResponseReceived(Request request, Response response) {
			        	
			        //	Log.setCurrentLogLevel(Log.getLowestLogLevel());

			        	//  Log.setUncaughtExceptionHandler();
			        	  
			        	 Log.info ("content recieved:"+response.getText());
			        	  
			    		String contenthtml = response.getText();
			    					    		
			    		
			    		//Window.setTitle("got html:"+contenthtml);
			    		loadCSSLinksIntoBody(contenthtml);
			    		
			    		
			    		if (ReplaceLinks){
			    			
			    			//Log.info("replaceing links.");
			    			contenthtml = triggerReplaceLinks(contenthtml);
			    			//Log.info("replaced links..");
			    		}
			    		
			    		//strip to the end of the header.
			    		contenthtml = contenthtml.substring(contenthtml.indexOf("</head>"));
			    		
			    		
			    		container.getElement().setInnerHTML(contenthtml);    
			    		////Log.info("Content now starts with:"+contenthtml.subSequence(0, 500));
			    		
			    		//Log.info("replaceing bits ?");
			    		//Window.alert("Finding stuff0:");
			    		
			    		//this crashs IE?
			    		if (replacement){
			    			//Window.alert("replaceing stuff");
			    			//Log.info("replaceing bits");
			    			Iterator<String> searchfor = ReplaceThis.iterator();	
			    			Iterator<Widget> replacements = WithThis.iterator();	
			    			//Log.info("looping");
			    			while (replacements.hasNext()){
			    				
			    			//	Window.alert("Finding1:|");
			    				
			    				String Find = searchfor.next();
			    				Widget Replacement = replacements.next();
			    				
			    				//Window.alert("Finding2:"+Find+"|");
			    				//Log.info("Finding2:"+Find+"|");
			    				
			    				container.addAndReplaceElement(Replacement, Find);
			    			
			    			}
			    			
			    			//replacement= false;
			    			
			    		}
			    		
			    		
			    		if (ReplaceLinks){
			    			
			    		Log.info("___replaceing links");
			    			
			    		Iterator<Hyperlink> URLToReplace = URLArray.iterator();
			    		int i=0;
			    		while (URLToReplace.hasNext()){
			    			i++;
				    		
			    			Hyperlink Link = URLToReplace.next();
			    		
			    		
			    			Link.setStyleName("gwt-Button1");
			    			
			    			//if theres a matching element, replace it.
			    			if (container.getElementById("RateoLink"+i)!=null)
			    			{
			    			container.addAndReplaceElement(Link, "RateoLink"+i);
			    			}
			    		}
			    		}
			    		
			    		////Log.info("content set to -"+contenthtml.subSequence(0, 100)+"...-");			    		
			    		
			    		container.setSize("100%", "100%");
			    		//Log.info("done");
			    		
			        }
			      });
			    } catch (RequestException ex) {
			    	Window.alert("error loading htmlFrame");
			    }
	}
	
	/** This function lets you replace one element with another **/
	public void replaceElementAfterLoading(String MarkerString, Widget Object){
		
		replacement = true;
		ReplaceThis.add(MarkerString);
		WithThis.add(Object);
		
		
		
	}
	
	
	/** This function will replace all links with Hyperlinks handeled by GWT
	 * This gets around some IE problems with tokens
	 */
	public void replaceLinksAfterLoad(boolean replace){
		ReplaceLinks = replace;
		URLArray.clear();
		
	}
	
	
	/** replaces links with GWT versions.  __Internal__#meep becomes local #meep links **/
	
	
	public String triggerReplaceLinks(String contenthtml){
		
		//container.getElement().setInnerHTML("html set");
		//GWT.log("converting links",null);
		//first we parse the text adding IDs
		
		String HTMLContents = contenthtml;
		
		String HostURL = Window.Location.getHref();
		
		//remove hash if present
			int hashindex = HostURL.indexOf("#");
			int quoteindex = HostURL.indexOf("?");
			
			if (hashindex>-1){
				HostURL = HostURL.substring(0,hashindex);
			}

			if (quoteindex>-1){
				HostURL = HostURL.substring(0,quoteindex);
			}
			
			String HostPageName = HostURL.substring(Window.Location.getHref().lastIndexOf("/")+1,Window.Location.getHref().length());

			String HostVarientURL = "notset";
			//Variant Host name with "www" added
			if (!HostURL.startsWith("http://www.")){
				//insert www before http
				HostVarientURL = HostURL.replaceFirst("http://", "http://www."); 
						
			}
			
		
	//	Log.info("_______contents="+HTMLContents);
			//container.getElement().getInnerHTML();
	//	HTMLContents = HTMLContents+ "<br>--------------------<br>";
		
	//	HTMLContents="<td align=\"center\" width=\"180\"><a class=\"button1\" href=\"#SEARCHFOR=&amp;TYPE=book%25&amp;ORDER=hyb_down&amp;ST=0\"><font size=\"-1\">~ Book Reviews ~</font></a><br><a class=\"button1\" href=\"#SEARCHFOR=&amp;TYPE=film%25&amp;ORDER=hyb_down&amp;ST=0\"><font size=\"-1\">~�Film Reviews ~</font></a><br>";	
//	HTMLContents=HTMLContents+ "<td align=\"center\" width=\"180\"><a class=\"button1\" href=\"#SEARCHFOR=&amp;TYPE=book%25&amp;ORDER=hyb_down&amp;ST=0\"><font size=\"-1\">~ Book Reviews ~</font></a><br><a class=\"button1\" href=\"#SEARCHFOR=&amp;TYPE=xcv%25&amp;ORDER=hyb_down&amp;ST=0\"><font size=\"-1\">~�Film xcvxcvxcv ~</font></a><br>";
//	container.getElement().setInnerHTML(HTMLContents);
	
		int num=0;
		String linkID=" -test- ";
	//	int loc= 0;
		 
		//--
		int StartLoc = 0;
		int EndLoc = 0;

	//	Window.alert("starting replace loop");
		
		while (true)
		{
			num++;
			linkID="RateoLink"+num;
			
			
			//if no href, then exit
			if (HTMLContents.indexOf("href")<0){
				return HTMLContents;
			}
			
			//exclude mail too links
			String oldHTMLContents= HTMLContents;
				HTMLContents = HTMLContents.replaceFirst("<a[^<>]*(?=[ |\r\n]href[^<>]+>)", "<div ID=\""+linkID+"\"></div>");
			
				if (num>7){
					//Window.alert("testing contains:"+linkID+"_");
					
					if (HTMLContents.indexOf(linkID)>0){
						//Window.alert("_Contains:"+linkID+"_");
					} else {
						//Window.alert("does not contain:"+linkID+"_");
						
						//the following works;
					//	URLArray.clear();
					//	return oldHTMLContents;
						
					}
					
					
					//Window.alert("_"+linkID+"_");
					
					//Window.alert("_"+HTMLContents.substring(7000)+"_");
					}
				
				
			//double check, if the replacement made no difference, then we ...
			
			
		//	//Log.info(" after link replacement:"+HTMLContents);
			
		//	HTMLContents = HTMLContents.replaceFirst("<a[^<>]*href", "<div ID=\""+linkID+"\"></div>");
			
			
			//location of link just formed
			
			int LinkLocation = 0;
			
			
			
			LinkLocation = HTMLContents.indexOf(linkID);
			 
			
			
			
			
			//exit if non left
			if (LinkLocation<0){
				HTMLContents= oldHTMLContents;
				break;
			}
			
			
			
			
			//else we grab the URL that should follow the linkID
			StartLoc = LinkLocation+linkID.length()+8;
			EndLoc =HTMLContents.indexOf("\"",LinkLocation+linkID.length()+8+7);
			if (EndLoc==-1){
				EndLoc=HTMLContents.length();
			}
			
			String URL = HTMLContents.substring(StartLoc+7,EndLoc);
			
			//Window.alert("url extracted = "+URL);
			
			//if its mail to we skip it
			//if (URL.startsWith("mailto:")){
			//	continue;
			//}
			
			//Crop url # and the ?
			/**
			if ((URL.startsWith("#"))||(URL.startsWith("?"))){
				URL = URL.substring(1);
			}
			if ((URL.startsWith("#"))||(URL.startsWith("?"))){
				URL = URL.substring(1);
			}
			**/
			//------------
			
		//Log.info("___testing link:"+URL);
			
			//URL.startsWith(Window.Location.getPath()
			//String HostPageName = Window.Location.getHref().substring(Window.Location.getHref().lastIndexOf("/")+1,Window.Location.getHref().length());
		
			//#!Stats
			
			Log.info("____________Host URL = "+HostURL);	
			Log.info("____________Host Var URL = "+HostVarientURL);
			Log.info("____________URL = "+URL);	
						
			
			// internal links only (old system, shouldn't be needed now)
			if ((URL.startsWith("__INTERNAL__"))||(URL.startsWith(HostPageName))||(URL.startsWith(HostURL))||(URL.startsWith(HostVarientURL))){
								
				
				//Log.info("Removing to hash ");
				
			//if theres a query or parameter, then forget the rest of the link upto that point.
				int index_of_q = URL.indexOf("?");				
				int index_of_h = URL.indexOf("#");
				
				////Log.info("removing start q="+index_of_q+",h="+index_of_h);
				
			//int paramLoc = Math.max(URL.indexOf("#"),URL.indexOf("?"));
				//          -1>0 & -1<5 then  else 
				int paramLoc = index_of_h <= 0 || (index_of_q > 0 && index_of_q < index_of_h) ? index_of_q : index_of_h;
			////Log.info(""+paramLoc);
			//i_q > 0 && i_q < i_h ? i_q : i_h;
				
				//Log.info("Removing to hash "+paramLoc);
				
			if (paramLoc>0){
				URL = URL.substring(paramLoc+1);
			}
			
			} else if (URL.startsWith("#")){
				URL = URL.substring(1);	//because we add a new hash, this one should be removed.
			}			
			else {
			// if its a relative link, we make it absolute
			
				//note, this regex should be fixed to only allow # or ? after the extension
			if (URL.matches("^(?!www\\.|http\\:|https\\:).*(\\.html|\\.php|\\.htm).*$")){
				
				if (LastURL.matches("^(?!www\\.|http\\:|https\\:).*(\\.html|\\.php|\\.htm).*$"))
				{
					
				//if the last url is relative, we go for the hostpages;
				URL = GWT.getHostPageBaseURL()+URL;
				
				} else {
					
				//else we use it, stripping it to its first / from the end
				
				URL = LastURL.substring(0, LastURL.lastIndexOf("/")+1)+ URL;		
				//convert amps back
				URL = URL.replace("&amp;", "&");
				
				}
				
				//Log.info("_______turning relative link to full url:"+URL);
			}
				
			}
			
			
			
			
			//And get the contents of the link (ie, the text used for clicking)
			//point after the >
			int ContentsStartLoc =HTMLContents.indexOf(">",StartLoc);
			//point before the </a>
			int ContentsEndLoc =HTMLContents.indexOf("</a>",ContentsStartLoc);
		String Contents = HTMLContents.substring(ContentsStartLoc+1, ContentsEndLoc);
		
	//	System.out.print("_________"+Contents+"_____________");
		
		
		//check for newline following, if so then we move the end loc forward to remove it <br><br/>
	//	if (HTMLContents.substring(ContentsEndLoc+4, ContentsEndLoc+8).equalsIgnoreCase("<br>")){
	//		ContentsEndLoc=ContentsEndLoc+8;
	//	} else if (HTMLContents.substring(ContentsEndLoc+4, ContentsEndLoc+9).equalsIgnoreCase("<br/>")){
	//		ContentsEndLoc=ContentsEndLoc+9;
	//	}
		
		
		
		
		
			
				
			HTMLContents = HTMLContents.substring(0, StartLoc)+HTMLContents.substring(ContentsEndLoc+4);
			
			
			//make hyperlink
			Hyperlink newLink;
			if (AjaxCrawlableMode){
			 newLink = new Hyperlink("","!"+URL);
			} else {
			 newLink = new Hyperlink("",URL);				
			}
			
			
			newLink.getElement().getStyle().setProperty("display", "inline");
			
			newLink.setHTML(Contents);
			

    		Log.info("_______Making link;"+URL);
			//Add to URL list
			URLArray.add(newLink);
			
			
			/*
			NextLoc = HTMLContents.indexOf(" href",loc);
			NextEnd = HTMLContents.indexOf("\" ",NextLoc+7);
			
			int NextEndCheck = HTMLContents.indexOf(">",NextLoc+7);	
			
			if ((NextEndCheck>0)&&(NextEndCheck<NextEnd)){
				NextEnd=NextEndCheck;
			}
			if (NextEnd<0){
				NextEnd=NextEndCheck;
			}
			HTMLContents = HTMLContents.substring(0, NextLoc) +  linkID + HTMLContents.substring(NextEnd+1);
			//newContents = newContents+HTMLContents.substring(loc+1, NextLoc) +  linkID + ">";
			
			
			loc = NextEnd;
			*/
		
		
		}
		
		
		//remove all </a>s
		//HTMLContents=HTMLContents.replaceAll("</a>", "");
		//HTMLContents=HTMLContents.replaceAll("</A>", "");
		
		
		//Window.alert("replaceing"+HTMLContents);
		/*
		container.getElement().setInnerHTML(HTMLContents);

		Window.alert("fixed links"+container.getElement().getInnerHTML());
		
		//test replacement
		Hyperlink testreplacement = new Hyperlink("blah",URLArray.get(0));	
		
		testreplacement.setStyleName("gwt-Button1");
		container.addAndReplaceElement(testreplacement, "RateoLink3");
	*/

		System.out.print("_________"+HTMLContents+"_____________");
						
		return HTMLContents;
	}
	
	
	
	public void loadCSSLinksIntoBody(String HTMLsection){
		
		
			
		//exit if not links
		if (!(HTMLsection.indexOf("<link")>0)){
			
			return;
		}
		//search for a link object 
		// (<link)[^<>]*(href=")[^<>]*> would have been better -sigh-
		int pos = 0;
		while (pos<HTMLsection.length())
		{
		int LinkPos = HTMLsection.indexOf("<link",pos);
		if (LinkPos>0){			
						
			int LinkEnd = HTMLsection.indexOf(">",LinkPos)+1;
			if (LinkEnd<0){
				break;
			}
			
			String LinkLine = HTMLsection.substring(LinkPos, LinkEnd).toLowerCase();			
			//Log.info("isolated link line:"+LinkLine);
			
			//not for print			
			if (LinkLine.contains("media=\"print\"")){
				pos = LinkEnd;
				continue;
			}
			
			//make sure its css 
			if ((LinkLine.contains("type=\"text/css\""))){
			
				//
							
			//isolate rel
				int RelStart = LinkLine.indexOf("rel=\"")+5;
				int RelEnd = LinkLine.indexOf("\"",RelStart);
				String RelData = LinkLine.substring(RelStart, RelEnd);
				
			//now we isolate the url
			int URLStart = LinkLine.indexOf("href=\"")+6;
			int URLEnd = LinkLine.indexOf("\"",URLStart);
			String CssURL = LinkLine.substring(URLStart, URLEnd);
			
			//Log.info("url is:"+CssURL);
			
			//make it absolute
			if (LastURL.matches("^(?!www\\.|http\\:|https\\:).*(\\.html|\\.php|\\.htm).*$"))
			{
			//if the last url is relative, we go for the hostpages;
			//	//Log.info("adding:"+GWT.getHostPageBaseURL());
				CssURL = GWT.getHostPageBaseURL()+CssURL;
			} else {
				
			//else we use it, stripping it to its first / from the end
				////Log.info("adding:"+LastURL.substring(0, LastURL.lastIndexOf("/")+1));
				CssURL = LastURL.substring(0, LastURL.lastIndexOf("/")+1)+ CssURL;		
			//convert amps back
				CssURL = CssURL.replace("&amp;", "&");
			
			}
			
			
			////Log.info("url is now:"+CssURL);
			
			//load it to the body!
			Element cssLine = loadStyleSheet(CssURL,RelData);
			
			////Log.info("returned:"+cssLine.getInnerHTML()+" | "+cssLine.getInnerText());
			
			//add element to array			
			cssLineArray.add(cssLine);
			
			//remove  it
			//test.getParentNode().removeChild(test);
			
			
			//add it to an array (for tidying-up-purpose's)
			
			
			
			
			};
			
			pos = LinkEnd;
			
		} else {
			break;			
		}
		
		}
		
		
		
	}
	
	/** This will remove previously added style sheets **/
	public void removeStyleSheets(){
		
	}
	
    public static native Element loadStyleSheet(String styleSheet, String RelData)/*-{ 
    var fileref=$doc.createElement("link"); 
    fileref.setAttribute("rel", RelData); 
    fileref.setAttribute("type", "text/css"); 
    fileref.setAttribute("href", styleSheet); 
    $doc.getElementsByTagName("head")[0].insertBefore(fileref,$doc.getElementsByTagName("link")[0]); 
    return fileref;
}-*/;
	
	
	public String getUrl(){
		return LastURL;
	}
	
	public void clearReplacements(){
		ReplaceThis.clear();
		WithThis.clear();
		replacement= false;
		URLArray.clear();
	}
	
	
	
}
