package lostagain.nl.spiffyresources.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.darkflame.client.semantic.SSSNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.Response;

import lostagain.nl.spiffyresources.client.spiffycore.GenericFileManager;

/** Manages text files **/
public class SpiffyFileManager implements GenericFileManager {
	
	static Logger Log = Logger.getLogger("SpiffyFileManager");
	
	/** If the application is running from a local source, php is naturally not used.
	 * Because of this, files are accessed directly at this location,
	 * rather then via "textfetcher.php **/
	public static String LocalFolderLocation="";
	
	Boolean directAccessMode=false;
	
	
	public Boolean getDirectAccessMode() {
		return directAccessMode;
	}


/** sets if files are retrieved directly, or via php **/
	public void setDirectAccessMode(Boolean directAccessMode) {
		this.directAccessMode = directAccessMode;
	}



	/** The location and name of the textfetcher.php that returns requested text files **/
	public static final String textfetcher_url = GWT.getHostPageBaseURL() + "text%20fetcher.php"; //$NON-NLS-1$

	/** A cache of all known text files associated with their filename**/
	static HashMap<String,String> allCachedTextFiles = new HashMap<String,String>();
	
	/** gets a bit of text using a php file,  <br>
	 * to help hide its location, or to get around sop restrictions. <br>
	 * You can set if you want this to add to the cache or not. <br>
	 * By default, everything is cached unless you use a POST request  <br>**/
	public void getText(final String fileurl,
			final FileCallbackRunnable callback, FileCallbackError onError, boolean forcePOST) {
		
		if (!forcePOST){
			getText(fileurl,callback,  onError,  forcePOST, true);
		} else {
			getText(fileurl,callback,  onError,  forcePOST, false);
		}
		
	}
	

	
	public void getText(final String fileurl,
			final FileCallbackRunnable callback, final FileCallbackError onError, boolean forcePOST,final boolean UseCache) {

		
		
		//if useCache is on, first we search for the filename in the cache
		if (UseCache){
			
			String contents = allCachedTextFiles.get(fileurl);
			
			if (contents!=null){
				Log.info("contents found in cache");
				callback.run(contents, Response.SC_OK);
				
				
			}
			
			
		}
		
		
		//if not a cache, we create a new RequestCallback for a real sever request response
		RequestCallback responseManagment = new RequestCallback(){
			@Override
			public void onResponseReceived(Request request, Response response) {	
				
				//add to cache if OK	
				if (response.getStatusCode()==Response.SC_OK && UseCache){
					Log.info("found file, storing in cache");
					allCachedTextFiles.put(fileurl,response.getText());
				}
				
				Log.info("got file,running callback ");
				callback.run(response.getText(), response.getStatusCode());
				
			}

			@Override
			public void onError(Request request, Throwable exception) {
				
				onError.run("",exception);
				
				
			}
			
		};
		
		
		if ((LocalFolderLocation.length()<3)&&(!directAccessMode)) {
			
			RequestBuilder requestBuilder = new RequestBuilder(
					RequestBuilder.POST, textfetcher_url);

			try {
				requestBuilder.sendRequest("FileURL=" + fileurl, responseManagment);
			} catch (RequestException e) {
				e.printStackTrace();
				System.out
						.println("can not connect to file via php:" + fileurl);
			}
		} else {
			Method requestType = RequestBuilder.GET;
			if (forcePOST) {
				requestType = RequestBuilder.POST;
			}

			RequestBuilder requestBuilder = new RequestBuilder(requestType,
					LocalFolderLocation + fileurl);

			try {
				requestBuilder.sendRequest("", responseManagment);
			} catch (RequestException e) {
				System.out.println("can not connect to file:" + fileurl); //$NON-NLS-1$
				e.printStackTrace();

				// special runnable commands on error if any
				if (onError != null) {

					onError.run("could not event send request:"+e.getLocalizedMessage(),null);

				}

			}

		}

	}


	/**
	 * we dont use this file saving system yet, so its left blank
	 */
	@Override
	public boolean saveTextToFile(String location,String contents,
			FileCallbackRunnable runoncomplete, FileCallbackError runonerror) {
		runonerror.run("NO SAVE SYSTEM IMPLEMENTED",new Throwable());
		return false;
		
	}
	
	
	@Override
	public String getAbsolutePath(String relativepath) {
		
		Log.info("get full path of:"+relativepath);
		
		//if its a web address ignore it
		if (relativepath.contains("://")||relativepath.startsWith("www.")){
						
			Log.info("path is likely already absolutee");
			
			return relativepath;
			
		}
		
		//if it starts with a slash we add a dot in front to say its relative to the current directory and not route
		//if (!relativepath.startsWith("/")){
		//	relativepath="/"+relativepath;
		//}
		
		
		
		String fullpath = getabsolute(GWT.getHostPageBaseURL(),relativepath);
		
		
		return fullpath;
	}
	
	
	public String getabsolute(String base,String relative) {
		
		LinkedList<String> stack = new LinkedList<String>();

    	Log.info("base="+base);
			
		String splitstack[] = base.split("/");
	    String parts[] = relative.split("/");

    	Log.info("splitstack="+splitstack.toString());
    	
    	for (String string : splitstack) {
    		stack.addFirst(string);
			
		}
    	
	 //   stack.addAll(Arrays.asList(splitstack));
	    
	    
    	Log.info("stack="+stack.toString());
	  //  stack.pop(); // remove current file name (or empty string)
	                 // (omit if "base" is the current folder without trailing slash)
	    
	   // stack.removeFirst();
	    
	    for (int i=0; i<parts.length; i++) {
	        if (parts[i] == ".")
	            continue;
	        if (parts[i] == "..")	        	
	        	
	        	Log.info("removing:"+ stack.removeFirst());
	        
	        else
	            //stack.push(parts[i]);
	        	stack.addFirst(parts[i]);
	    }
	    
	    String url = "";
	    for (String part : stack) {
	    	
	    	Log.info("part="+part);
	    	
			
	    	url =  part+"/"+url;
	    	
		}
	    
	    //remove last slash
	    if (url.endsWith("/")){
	    	url = url.substring(0, url.length()-1);
	    }
	    
	    return url;
	}

	


}



