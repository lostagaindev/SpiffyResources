package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

//import com.darkflame.client.JAM;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import lostagain.nl.spiffyresources.client.spiffycore.RunnableWithString;

/** (used statically)
 * Imported from code used in the JAM engine,
 * this handles loading of images and text files over http
 * 
 * Image loading happens staggered with a set maximum simultaneously..
 * Images are "loaded" by putting them on the DOM hidden offscreen (-1000,-1000 or such).
 * Its hoped this perswades the browser to add them to the cache
 * 
 * @author darkflame
 * 
 *
 */
public class SpiffyPreloader {
	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyPreloader");
	
	final static ArrayList<String> HtmlItems = new ArrayList<String>();
	final static ArrayList<String> HtmlFileNames = new ArrayList<String>();

	final static ArrayList<String> LoadingList = new ArrayList<String>();
	

	/**
	 * Maintains a hashmap of whats currently loading.
	 * This ensures we dont request the same thing to load twice
	 */
	final static HashMap<Image,String> CurrentlyLoadingList = new HashMap<Image,String>();


	final static ArrayList<Image> LoadingWidgets = new ArrayList<Image>();

	final static HashMap<String,RunnableWithString> PostCommands = new HashMap<String,RunnableWithString>();

	static LoadHandler OnLoadHandler;
	static ErrorHandler OnErrorHandler;
	//	final static Image LoadThis = new Image();

	static final int MAX_SIMULTANIOUS_LOADING = 6; //http://www.browserscope.org/?category=network    6 seems about right for now (2015), but this might change as time goes on. 

	static boolean DebugMode = false;
	

	public static void setDebugMode(boolean debugMode) {
		DebugMode = debugMode;
	}

	/** preloads game images. Simply use "addToLoading" then "preload" when everything you want
	 * is added. New images are triggered to load after each one is finished. **/
	public SpiffyPreloader(boolean DebugMode){
		SpiffyPreloader.DebugMode = DebugMode;
		/*
		//split up to array
		List<String> LoadingList = Arrays.asList(  LoadingString.split("\n")  );


		System.out.print("preloading..."+LoadingList.size());

		ArrayList<String> LoadingArrayList =new ArrayList<String>(LoadingList); 

		System.out.print("preloading..."+LoadingArrayList.size());

		Iterator<String> it = (LoadingList.iterator());

		//loop over
		while (it.hasNext()) {


			//for each image, we preload			
		String preloadthis = it.next().trim();

		if (preloadthis.toLowerCase().endsWith("jpg")){
			PreloadImage(preloadthis);	
		}

		if (preloadthis.toLowerCase().endsWith("png")){
			PreloadImage(preloadthis);
		}
		if (preloadthis.toLowerCase().endsWith("gif")){
			PreloadImage(preloadthis);
		}
		}

		 */


		setupHandlers();


	}

	public static void setupHandlers() {

		OnLoadHandler = new LoadHandler(){


			public void onLoad(LoadEvent event) {
				Log.info("onLoad done");
				Image LoadThis = (Image) event.getSource();

				String justLoaded=LoadThis.getUrl();

				Log.info("Just Loaded:"+justLoaded);

				//boolean wasRemoved = CurrentlyLoadingList.remove(justLoaded);
				String wasRemoved = CurrentlyLoadingList.remove(LoadThis);
								
				Log.info("Just removed: "+wasRemoved+", CurrentlyLoadingList now:"+CurrentlyLoadingList.size());
				//Log.info("-=-Loaded:"+currentlyLoading );

				// remove from list
				//LoadingList.remove(0);
				//if (LoadingList.remove(currentlyLoading)){
				//	Log.info("-=-removed:"+currentlyLoading );
				//} else {
				//	Log.info("-=-failed to remove:"+currentlyLoading );
				//}

				RunnableWithString Commands=PostCommands.get(justLoaded);

				//if theres commands to run when loading is done, we run them here
				if (Commands!=null){
					Commands.run(justLoaded);					
				}


				if (LoadingList.size()==0){

					if (CurrentlyLoadingList.size()==0){
						Log.info("(tidying up after preloader)");
						tidyOurMess();
					}

					Log.info("(nothing left to start loading in preloader, things currently loading ="+CurrentlyLoadingList.values().toString()+")");

					return;
				}

				//load the next;		

				String requestedURL=LoadingList.get(0);


				Log.info("-=-Loading next:"+requestedURL);
				LoadThis.setUrl(requestedURL);		
				LoadingList.remove(requestedURL);

				//JAM.Quality.equalsIgnoreCase("debug")
				if (DebugMode){					
					Log.info("Left To Start Loading:"+LoadingList.toString());		
				}

				Log.info("ABOUT TO ADD TO CURRENTLYLOADINGLIST ");				
				//CurrentlyLoadingList.add(LoadThis.getUrl());
				CurrentlyLoadingList.put(LoadThis, requestedURL);
				
				if (DebugMode){					
					Log.info("In the process of Loading:"+CurrentlyLoadingList.values().toString());
					//Log.info("This is added to CurrentlyLoadingList (take 1): "+LoadThis.getUrl());
					Log.info("This is added to CurrentlyLoadingList (take 1): "+requestedURL);
				}

				//convert url to absolute and replace references
				//LoadingList.set(LoadingList.indexOf(requestedURL), LoadThis.getUrl());
				PostCommands.put(LoadThis.getUrl(), PostCommands.get(requestedURL));
			}

		};



		OnErrorHandler = new ErrorHandler(){

			@Override
			public void onError(ErrorEvent event) {
				Image LoadThis = (Image) event.getSource();
				
				
				String justFailedToLoad=LoadThis.getUrl();

				Log.info("Just Failed to Load:"+justFailedToLoad);	
				CurrentlyLoadingList.remove(LoadThis);
				// remove from list
				//	LoadingList.remove(currentlyLoading);


				RunnableWithString Commands=PostCommands.get(justFailedToLoad);
				if (Commands!=null){					


					Commands.run(justFailedToLoad);

				}
				if (LoadingList.size()==0){
					return;
				}

				//load the next;


				String requestedURL=LoadingList.get(0);
				Log.info("-=-Started Loading this after error:"+requestedURL);

				LoadThis.setUrl(requestedURL);	
				LoadingList.remove(requestedURL);

				//JAM.Quality.equalsIgnoreCase("debug")
				if (DebugMode){
					Log.info("Left To Start Loading:"+LoadingList.toString());
				}



				Log.info("ABOUT TO ADD TO CURRENTLYLOADINGLIST ");
				//CurrentlyLoadingList.add(LoadThis.getUrl());
				CurrentlyLoadingList.put(LoadThis,requestedURL);
				Log.info("In the process of Loading:"+CurrentlyLoadingList.values().toString());	
				Log.info("This was added to the LoadingList (take2): "+requestedURL);


				//convert url to absolute and replace references
				//LoadingList.set(LoadingList.indexOf(requestedURL), LoadThis.getUrl());
				PostCommands.put(LoadThis.getUrl(), PostCommands.get(requestedURL));
			}

		};
	}

	/** returns true if successfully added, false if not**/
	static public boolean addToLoading(String URL){
		if (URL.length()<5){

			Log.severe("URL TOTALLY TOO SHORT TO BE REAL MAN:"+URL);

			return false;

		}
		//maybe LoadingList could be a "TreeSet" so duplicates arnt allowed without needing a check

		if (!LoadingList.contains(URL)){
			LoadingList.add(URL);
			Log.info("Added this URL to loading "+URL);
			return true;
		}

		return false;
		//PreloadImage(GWT.getHostPageBaseURL()+URL);

		//Window.alert("<br>-=-add image to preload"+URL );
	}
	/** returns true if successfully added, false if not**/
	static public boolean addToLoading(String URL, RunnableWithString doThisWhenLoaded){

		if (URL.length()<5){

			Log.severe("URL TOTALLY TOO SHORT TO BE REAL MAN:"+URL);

			return false;

		}

		//test we arnt currently loading this one
		if (CurrentlyLoadingList.size()>0){
			//String absoluteForComparison = FileManager.getAbsolutePathStatic(URL); //converts with spaces not %20
			
			if (CurrentlyLoadingList.values().contains(URL)){
			//if (CurrentlyLoadingList.contains(absoluteForComparison)){
				Log.warning("currently loading specified URL already ("+URL+")");
				return false;
			}

		}

		//maybe LoadingList could be a "TreeSet" so duplicates arnt allowed without needing a check

		if (!LoadingList.contains(URL)){

			LoadingList.add(URL);
			PostCommands.put(URL,doThisWhenLoaded);

			if (DebugMode){
				Log.info("Preloader currently has "+LoadingWidgets.size()+" loading widgets (if zero it means it needs to be reran for "+URL+" to be loaded");
				Log.info("(LoadingList)          Things-not-yet-loading list:"+ LoadingList.toString());         
				Log.info("(CurrentlyLoadingList)           Currently Loading:" + CurrentlyLoadingList.values().toString()); 
			}


			return true;
		} else {
			Log.warning("url was already on list of things to load ("+URL+")");

		}


		return false;


	}

	public static void preloadList(){

		Log.info("STARTING TO PRELOAD LIST ");
		Log.info("DEBUG MODE =  "+DebugMode);


		if (CurrentlyLoadingList.size()>0){

			Log.info("Still have "+CurrentlyLoadingList.size()+" things which are currently loading");
			Log.info("They are:"+CurrentlyLoadingList.toString());
			return;
		}


		Log.info("_-_-_-_-_-_ FIREFOX NEVER GETS HERE??!?!?!");

		setupHandlers();



		if (LoadingList.size()<1){
			Log.info("no images to load" );
			return;
		}


		//This loops over the preload list
		//When each entry is loaded, its removed from the list
		//and the next item loaded.
		Log.info("-loading images:" );

		int i=0;

		while (i<MAX_SIMULTANIOUS_LOADING) {

			i++;

			//	Window.alert("<br>preloading list");

			Image LoadThis = new Image();

			LoadThis.addLoadHandler(OnLoadHandler);
			LoadThis.addErrorHandler(OnErrorHandler);

			RootPanel.get().add(LoadThis, -12000, -12000);

			LoadThis.getElement().setId("_PRELOADERPANEL_");
			LoadThis.setStylePrimaryName("hiddenImagePanel");
			LoadingWidgets.add(LoadThis);

			if (LoadingList.size()==0){

				Log.info("Nothing left in loading list to start loading");

				break;
			}

			String requestedURL = LoadingList.get(0);

			LoadingList.remove(requestedURL);
			LoadThis.setUrl(requestedURL); 

			Log.info("ABOUT TO ADD TO CURRENTLYLOADINGLIST ");
			CurrentlyLoadingList.put(LoadThis,requestedURL); //adds the full url

			Log.info("In the process of Loading:"+CurrentlyLoadingList.values().toString());	
			Log.info("This is added to CurrentlyLoadingList (take 3): "+requestedURL);

			//convert url to absolute and replace references
			//LoadingList.set(LoadingList.indexOf(requestedURL), LoadThis.getUrl());		
			PostCommands.put(LoadThis.getUrl(), PostCommands.get(requestedURL));
			Log.info("-=-loading:" + requestedURL);
		}

		//		
		//		final Image LoadThis2 = new Image();
		//		LoadThis2.addLoadHandler(OnLoadHandler);		
		//		LoadThis2.addErrorHandler(OnErrorHandler);
		//		RootPanel.get().add(LoadThis2,-12000,-12000);		
		//		LoadThis2.setStylePrimaryName("hiddenImagePanel");
		//		
		//		final String requestedURL2=LoadingList.get(0);	
		//		LoadingList.remove(requestedURL2);
		//		
		//		LoadThis2.setUrl(requestedURL2);
		//		//convert url to absolute and replace references
		//		//LoadingList.set(LoadingList.indexOf(requestedURL), LoadThis.getUrl());		
		//		PostCommands.put(LoadThis2.getUrl(), PostCommands.get(requestedURL2));				
		//		Log.info("-=-loading:"+requestedURL2 );


	}

	/** this should be run once all loading is complete */
	private static void tidyOurMess() {
		//


		//detach images
		for (Image imageToRemove : LoadingWidgets) {

			if (DebugMode){
				Log.info("removing widget for image "+imageToRemove.getUrl());

			}

			imageToRemove.removeFromParent();
		}


		LoadingWidgets.clear();
	}







	/**prefetches an image right now using the Image.prefetch function of javascript */
	public static void PreloadImage(String URL){
		//base image

		Image.prefetch(URL);
		//get route name

		//String filename = URL.substring(URL.lastIndexOf("/")+1);
		//String loc = URL.substring(0, URL.lastIndexOf("/")+1);

		//JAM.DebugWindow.addText("preloaded..."+URL);		


	}


	public void AddItem(String newitem, String newname){
		HtmlItems.add(newitem);
		HtmlFileNames.add(newname);
	}
	public void RemoveItem(String removethisitem){
		//find location
		int i=0;
		int indextoremove = -1;
		for (Iterator<String>it = HtmlFileNames.iterator(); it.hasNext(); ) {
			String currentItem = it.next(); 
			if (currentItem.compareTo(removethisitem)==0){
				indextoremove=i;
			};
			i=i+1;

		}
		//if present
		if (indextoremove>-1){
			HtmlItems.remove(indextoremove);
			HtmlFileNames.remove(indextoremove);
		}
	}
	public String GetItem(String ItemName){
		String Item = "";
		//find location
		int i=0;
		int itemindex = -1;
		for (Iterator<String>it = HtmlFileNames.iterator(); it.hasNext(); ) {
			String currentItem = it.next(); 
			if (currentItem.compareTo(ItemName)==0){
				itemindex=i;
			};
			i=i+1;

		}
		//if present
		if (itemindex>-1){
			Item = HtmlItems.get(itemindex);
		} else {
			Item = "";
		}


		return Item;
	}

}
