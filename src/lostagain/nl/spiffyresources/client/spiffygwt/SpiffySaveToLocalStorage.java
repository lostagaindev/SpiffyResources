package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.client.SpiffyImageUtilitys;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.storage.client.Storage;

/** Saves and loads strings from local storage **/
public class SpiffySaveToLocalStorage extends VerticalPanel {
	private static final String SAVEKEY__PREFIX = "Save_";

	String NEW_SAVE_STRING = "+";

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffySaveToLocalStorage");
	
	/**
	 * filters visible save games by the current games name (ie, only show ones that will load!)
	 */
	private String CURRENTGAME_PREFIX = "";
	
	
	public enum interactionmode {
		savemode,loadmode
	}
	/** Determines if this dialogue is currently used for saving or loading **/
	interactionmode currentmode = interactionmode.loadmode; 
	
	/** the list of saves is stored here **/
	FlowPanel SaveIconList = new FlowPanel();
	
	HashMap<String,SaveIcon> SaveIconHashMap = new HashMap<String,SaveIcon>();
	
	///** we wrap the above in a scrollpanel. This simply makes  a scrollbar appear if we need it**/
	//ScrollPanel ScrollPanelForSaveIconList = new ScrollPanel();
	
			
	String SaveInstructions = "Note; This will save your game on this PC and Browser only";
	String LoadInstructions = "Click below to load your game from local storage;";
	
	Label Instructions = new Label(LoadInstructions);
	
	private Storage localStorage = Storage.getLocalStorageIfSupported();

	/**
	 * The key name of the save game last loaded or saved. 
	 */
	public String lastSaveName="";

	
	  
	private savegamerequest RunOnSaveRequest;
	private loadGameRequest RunOnLoadRequest;
	
	private RemoveSaveRequested RunOnRemoveSaveRequest;
	
	
	
	//unfortunately in order to have a scrollpanel we need to set a height. bah.
	//theres probably a way to do this correctly with "max-height" being specified rather then height but
	//not sure how exactly.
	int height=500;
	
	/** creates a new local storage management panel
	 * it will fill its area up with the storage list scrolling if needed to fit everything.
	 * Height cant be percentage base **/
	public SpiffySaveToLocalStorage(interactionmode saveorloadmode, int height, String gameNameFilter){
		
		this.height=height;
		this.CURRENTGAME_PREFIX = gameNameFilter;
		
		currentmode = saveorloadmode; 
		//test support
		if (localStorage==null){
			Log.severe("Local storage not supported!"); 
		}
		
		
		setupVisuals();
		populateCurrentStoredData();
	}
	
	
	public void setupVisuals()
	{

		String SaveInstructions = "Note; This will save your game on this PC and Browser only";
		String LoadInstructions = "Click below to load your game;";
		if ( currentmode == interactionmode.savemode){
			Instructions.setText(SaveInstructions);
		} else {
			Instructions.setText(LoadInstructions);
		}
		Instructions.setStylePrimaryName("LocalStoreSaveInstructions");
		
		
		
		super.setSize("100%", height+"px");
		super.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				
		super.add(Instructions);
		
		//ScrollPanelForSaveIconList.add(SaveIconList);
		//ScrollPanelForSaveIconList.setSize("100%", "100%");
		SaveIconList.setSize("100%", "auto");
		SaveIconList.getElement().getStyle().setOverflowY(Overflow.AUTO);
		
		super.add(SaveIconList);

	}
	
	boolean rePopulateOnOpen=false;
	
	/**
	 * repopulates if open, else repopulates on next open
	 */
	public void repopulateOnNextOpen(){
		if (this.isAttached()){
			this.populateCurrentStoredData();
			return;
		}
		
		rePopulateOnOpen=true;
		
	}
	
	@Override
	public void onLoad(){
		super.onLoad();
		
		//if we are flagged to repopulate then we do so
		if (rePopulateOnOpen){
			this.populateCurrentStoredData();
			rePopulateOnOpen=false;
			
		}
		
		//when attached we test parents size and use that minus a little for the inner saveiconlists size
		//unfortunately we cant do this before its on the page!
		//int Savelistheight = super.getOffsetHeight()-Instructions.getOffsetHeight()-15;		
		int Savelistheight = height-Instructions.getOffsetHeight()-15;
		SaveIconList.setSize("100%",Savelistheight+"px" );
		
		
		
	}
	
	public void setSaveInstructions(String instructions){
		SaveInstructions = instructions;
		Instructions.setText(SaveInstructions);
	}

	public void setLoadInstructions(String instructions){
		LoadInstructions  = instructions;
		Instructions.setText(LoadInstructions);
	}
	
	public String getLastSaveGameName(){
		return lastSaveName;
	}
	
	public void setLastSaveGameOutline(String newSaveName){
		
		Log.info("Setting last save to:"+newSaveName);
		
		
		if (!lastSaveName.isEmpty()){
			SaveIcon saveIcon = SaveIconHashMap.get(lastSaveName);
			saveIcon.setAsCurrentSave(false);
		}
		
		lastSaveName = newSaveName;
		
		SaveIcon saveIcon = SaveIconHashMap.get(lastSaveName);		
		saveIcon.setAsCurrentSave(true);
		
		
		return;
	}
	
	
	
	/** 
	 * repopulates the list with the current savegames in the local storage 
	 * savegames are identified by keys starting with SAVEKEY__PREFIX
	 ***/
	public void populateCurrentStoredData()
	{
		
		SaveIconList.clear();		
		SaveIconHashMap.clear();
		
		if (localStorage != null){

			Log.info("repopulating list:"+localStorage.getLength());
		    String OverallPrefix = SAVEKEY__PREFIX+CURRENTGAME_PREFIX+"_";
			   
			
			  for (int i = 0; i < localStorage.getLength(); i++){
				  				  
			    String key = localStorage.key(i);				    
			 
				if (key.startsWith(OverallPrefix)){			    	
			    	
			    	String name = key.substring(OverallPrefix.length()); //name is just the key with the savekeyprefix removed			    	
			    	SaveIcon newicon = new SaveIcon(name,key,this);			
			    	if (key.equals(lastSaveName)){
			    		newicon.setAsCurrentSave(true);
			    	}
			    	
			    	
			    	SaveIconList.add(newicon);
			    	SaveIconHashMap.put(key,newicon);
			    
			    
			    }
			  //  SaveIconList.setText(i+1, 0, localStorage.getItem(key));
			  //  SaveIconList.setWidget(i+1, 2, new Label());
			    
			    
			  }
			}
		
		
		//add one for ADD a save
		if ( currentmode == interactionmode.savemode){
			
			SaveIcon newicon = new SaveIcon(NEW_SAVE_STRING,"",this);		    
		    SaveIconList.add(newicon);
		}
		
		
	}
	
	/** used for runnable that fire when a save is requested **/
	public interface savegamerequest {
		public void save(String SaveOverThisKeyRequested);
	}
	
	/** used for runnable that fire when a load is requested **/
	public interface loadGameRequest {
		public void load(String incommingLoadData, String saveName);
	}
	
	/** used for runnable that fire when its requested to delete a save (gives a chance for confirmation) **/
	public interface RemoveSaveRequested {
		public void remove(String key);
	}
	
	
	

	/**
	 * 
	 */
	public void SaveData(String SaveGameName,String SaveGaveData,boolean Overwrite)
	{

		Log.info("Saving name:"+SaveGameName);
		Log.info("Saving data:"+SaveGaveData);
		
		//see if it already exists
		String existingData = localStorage.getItem(SaveGameName);
		
		if (!(existingData == null || existingData == "")){
			Log.info("Save data already exists");
			if (!Overwrite){
				Log.severe("Overwrite Not True, exiting");
				return;
			}
			
		}
		
		//ensure it starts with the save and gamename prefix
		 String OverallPrefix = SAVEKEY__PREFIX+CURRENTGAME_PREFIX+"_";			
		if (!SaveGameName.startsWith(OverallPrefix)){
			SaveGameName=OverallPrefix+SaveGameName;
		}
		
		//save the data		
		localStorage.setItem(SaveGameName, SaveGaveData);
				
		//update interface (in future just add the new one, don't refresh it all)
		populateCurrentStoredData();
				

		//set as last used
		Log.info("setting as outlined:"+SaveGameName );
		
		setLastSaveGameOutline(SaveGameName);
		
		
	}
	
	public void setLoadRequested(loadGameRequest runOnLoadRequested ){
		
		RunOnLoadRequest = runOnLoadRequested;
	
	}
	
	public void setSaveRequested(savegamerequest runOnSaveRequested ){
		
		RunOnSaveRequest = runOnSaveRequested;
	
	}
	
	public void setRemoveSaveRequested(RemoveSaveRequested runOnRemoveSaveRequested ){
		
		RunOnRemoveSaveRequest = runOnRemoveSaveRequested;
	
	}
	
	/** when we click on a key we either save or load depending on mode **/
	protected void clickedOnKey(String key) {
		Log.info("clicked on :"+key);
		
		//if saving
		if ( currentmode == interactionmode.savemode){
			
			RunOnSaveRequest.save(key);
			
		}
		//if loading
		if ( currentmode == interactionmode.loadmode){
			
			RunOnLoadRequest.load(localStorage.getItem(key), key);

			//set as last used
			setLastSaveGameOutline(key);
			
		}
	}
	
	/** icon representing a save game - just a label for now with a fixed size **/
	class SaveIcon extends AbsolutePanel {

		private static final String DEFAULT_SAVE_NAME_BRACKET = " - (";

		final Logger Log = Logger.getLogger("SpiffyGWT.SpiffySaveToLocalStorage.SaveIcon");
		
		final private String key;
		final private SpiffySaveToLocalStorage parent;

		VerticalPanel contents = new VerticalPanel();
		
		Label keyname = new Label("");
		Label datadetails = new Label("");
		
		Label removeIcon = new Label("X");
		
		FocusPanel clickDetectorBack = new FocusPanel();
		
		public SaveIcon(String name, String savekey,  SpiffySaveToLocalStorage saveparent) {

			this.key=savekey;			
			this.parent=saveparent;
			super.setTitle(name);
			
			//get background from name (in future use key?)
			//if the name starts with the default name scheme we remove the bit in common with the rest. This helps create more vairence in the backgrounds
			String croppedname=name; 
			if (name.startsWith(DEFAULT_SAVE_NAME_BRACKET,1)){
				croppedname = name.substring(5);
			}
			String backgroundDataURL = SpiffyImageUtilitys.getDataURLFromString(croppedname, 100, 100);
			super.getElement().getStyle().setBackgroundImage("url("+backgroundDataURL+")");
			
			
			keyname.setText(name);
			keyname.setStylePrimaryName("SaveIconLabel");
			
			datadetails.setText("");
			contents.setSize("100%", "100%");
			clickDetectorBack.setSize("100%", "100%");
			//removeIcon.setPixelSize(20, 20);
			
			contents.setHorizontalAlignment(ALIGN_CENTER);
			contents.setVerticalAlignment(ALIGN_MIDDLE);
			
			
			contents.add(keyname);
			contents.add(datadetails);
			
			//we only show the delete option if we are on save mode
			if (saveparent.currentmode==interactionmode.savemode){
				this.add(removeIcon);	
			}
			this.add(contents,0,0);		
			this.add(clickDetectorBack,0,0);
			
			removeIcon.getElement().getStyle().setZIndex(100);
			clickDetectorBack.getElement().getStyle().setZIndex(50);
			
			
			
			this.setStylePrimaryName("SaveIconBack");
			removeIcon.setStylePrimaryName("removeIcon");
			
			if (name.equals(NEW_SAVE_STRING)){
				this.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
				this.addStyleName("NewSaveBack");
				removeIcon.removeFromParent();
			} else {
				this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			
			}
			
			
			
			this.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			this.getElement().getStyle().setMargin(5, Unit.PX);			
			this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			
			this.setPixelSize(100, 100);
			
			removeIcon.addClickHandler( new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					parent.removeSaveRequested(key);
					
					
				}
			});
			
			clickDetectorBack.addClickHandler( new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					parent.clickedOnKey(key);
					
				}
			});
			
			
		}
		
		public void setAsCurrentSave(boolean state){
			
			Log.info("Setting icon style to lastsave:"+state);
			
			if (state){
				addStyleName("CurrentSaveIcon");
			} else {
				removeStyleName("CurrentSaveIcon");
			}
		}
		
				
	}


	public int getNumberOfSaves() {
				
		return SaveIconList.getWidgetCount()-1;
	}


	public void removeSave(String key) {
		
		Log.info("Removing save:"+key);
		localStorage.removeItem(key);
		
		populateCurrentStoredData();
		
		
	}
	
	private void removeSaveRequested(String key) {
		RunOnRemoveSaveRequest.remove(key);
		
		
		
	}

	
	/** checks if the key exists OR the key with the SAVE prefix before it exists **/
	public boolean keyExists(String name) {
		
		if (localStorage.getItem(name)!=null){
			return true;
		}
		
		//also ensure we are looking for something that starts with the prefix
	///	if (!name.startsWith("SAVEKEY__PREFIX")){
	//		name=SAVEKEY__PREFIX+name;
	//	}
		String OverallPrefix = SAVEKEY__PREFIX+CURRENTGAME_PREFIX+"_";			
		if (!name.startsWith(OverallPrefix)){
			name=OverallPrefix+name;
		}
		
		if (localStorage.getItem(name)!=null){
			return true;
		}
		return false;
	}


}
