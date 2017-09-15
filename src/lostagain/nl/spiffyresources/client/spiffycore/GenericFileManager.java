package lostagain.nl.spiffyresources.client.spiffycore;

import com.darkflame.client.interfaces.SSSGenericFileManager;

//use SSSGenericFileManager for now
public interface GenericFileManager extends SSSGenericFileManager {

//	void getText(String location,FileCallbackRunnable runoncomplete,  FileCallbackError runonerror , boolean forcePost, boolean useCache);
//	
//	void getText(String location,FileCallbackRunnable runoncomplete,  FileCallbackError runonerror , boolean forcePost);
//	
//	/**
//	 * Saves the text to a file.
//	 * returns true  if the file is attempting to be save
//	 * returns false if there is no save system implemented, or there is no attempt made to save for whatever reason.
//	 * (runonerror should also fire, however)
//	 * 
//	 * @param location      - target location to save too. May be a absolute path, or one relative to the directly this is run from
//	 * @param runoncomplete - run this on successful save
//	 * @param runonerror    - run this on error
//	
//	 **/
//	public boolean saveTextToFile(String location,String contents, FileCallbackRunnable runoncomplete,  FileCallbackError runonerror);
//	
//	
//	
//	/** returns the absolute path given a relative one.
//	 * If used locally this will be absolute to the running directory of this code on the hard drive
//	 * if used on a sever this will be the full url of the file including the http **/
//	public String getAbsolutePath(String relativepath);
//
//	/** callback class type designed to be used as a response when fetching text from a server 
//	 * The response code is the html response code given if this is implemented by a webapp.
//	 * If a local app, please use "200" to indicate file correctly loaded, and "404" for missing file. **/
//	public static interface FileCallbackRunnable {		
//		void run(String responseData, int responseCode);
//
//	}
//	/** callback class type designed to be used when there's an error fetching text from a server **/
//	public static interface FileCallbackError {		
//		void run(String errorData, Throwable exception);
//
//	}

}
