package lostagain.nl.spiffyresources.client.spiffygwt;


import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class HelpIcon extends SimplePanel  {

	static Logger Log = Logger.getLogger("SpiffyGWT.HelpIcon");
	
	Boolean PopedUp = new Boolean(false);



	//NOTE: These arrays are purely here to use pass-by-reference systems
	//This is all crude and should be replaced.
	//	ArrayList<HelpPopUp> CurrentHelpPopUp_old = new ArrayList<HelpPopUp>();	
	//   ArrayList<HelpIcon> CurrentHelpIcon_old  = new ArrayList<HelpIcon>();

	static HashMap<String,HelpPopUp> CurrentHelpPopUpStore = new HashMap<String,HelpPopUp>();	
	static HashMap<String,HelpIcon>  CurrentHelpIconStore  = new HashMap<String,HelpIcon>();

	/** The GroupID of this help icon, icons and popups in the game group are linked, and will close eachother when new ones open**/
	static final String DefaultHelpIconsGroupID = "DefaultGroup";
	String HelpIconsGroupID = DefaultHelpIconsGroupID;



	String helptitle = "";
	String helptext = "";
	Widget HelpPopUpLink = null;

	HelpPopUp associatedHelpPopUp=null;

	ClickHandler newpopup; 

	Widget HelpField;


	//for arrow direction
	static boolean VerticalSet=false;
	public boolean forceVertical=false;


	static Widget currentHelpField;
	//images
	private StandardInterfaceImages standardImages = (StandardInterfaceImages) GWT.create(StandardInterfaceImages.class);

	//images
	Image help_enabled =  new Image(standardImages.helpicon());
	Image help_disabled = new Image(standardImages.helpicon_disabled());
	Image help_pressed = new Image(standardImages.helpicon_pressed());


	//private static Widget WidgetSource;

	private static HelpPopUp VisibleHelpPopUp;


	//arrow 
	//we now do one for each direction
	static GWTCanvas rArrow = new GWTCanvas();
	static boolean rArrowDrawen = false;
	static GWTCanvas lArrow = new GWTCanvas();
	static boolean lArrowDrawen = false;
	static GWTCanvas uArrow = new GWTCanvas();
	static boolean uArrowDrawen = false;
	static GWTCanvas dArrow = new GWTCanvas();
	static boolean dArrowDrawen = false;

	boolean hasCloseIcon = true;
	Widget closeIcon;
	HelpIcon thisIcon = this;

	/*
	  public void setCloseIconWidget (Widget closeicon){
		  closeIcon = closeicon;	
		  hasCloseIcon = true;
	  }*/

	/** null constructor for later use **/
	public HelpIcon(){

	}

	/** The HelpIcon is used to trigger HelpPopUps, think of them as expanded tool tips **/
	public HelpIcon(final String inc_helptext,final String inc_helptitle,
			Widget closeicon) {

		this(inc_helptext,inc_helptitle,closeicon,DefaultHelpIconsGroupID);

	}
	/** The HelpIcon is used to trigger HelpPopUps, think of them as expanded tool tips.
	 * Note; if you supply a close icon you need to handle the close yourself  **/
	public HelpIcon(final String inc_helptext,
			final String inc_helptitle,
			Widget closeicon, 
			String HelpGroupID) {	

		this.HelpIconsGroupID= HelpGroupID;

		this.closeIcon = closeicon;

		if (closeIcon==null){

			createDefaultCloseIcon();

		}

		setupAssociatedPopup(inc_helptext, inc_helptitle);


	}	  

	/** The HelpIcon is used to trigger HelpPopUps, think of them as expanded tool tips.
	 * Note; if you supply a close icon you need to handle the close yourself  **/
	public HelpIcon(final String inc_helptext,
			final String inc_helptitle,
			final ImageResource Close,
			final ImageResource CloseOver,
			String HelpGroupID) {	

		this.HelpIconsGroupID= HelpGroupID;

		if (closeIcon==null){

			createDefaultCloseIcon(Close,CloseOver);

		}

		setupAssociatedPopup(inc_helptext, inc_helptitle);


	}	 
	/** The HelpIcon is used to trigger HelpPopUps, think of them as expanded tool tips **/
	public HelpIcon(final String inc_helptext,final String inc_helptitle) {	
		this(inc_helptext,inc_helptitle,null,DefaultHelpIconsGroupID);
	}
	/** The HelpIcon is used to trigger HelpPopUps, think of them as expanded tool tips **/
	public HelpIcon(final String inc_helptext,final String inc_helptitle, String HelpGroupID) {	

		this(inc_helptext,inc_helptitle,null,HelpGroupID);
		
		/*
		this.HelpIconsGroupID= HelpGroupID;

		if (closeIcon==null){
			//  closeIcon = new Label("X");	
			// //Log.info("creating default close widget = "+((Label)closeIcon).getText());

			createDefaultCloseIcon();

		}

		setupAssociatedPopup(inc_helptext, inc_helptitle);
*/

	}
	private void createDefaultCloseIcon() {
		createDefaultCloseIcon(standardImages.Close(),standardImages.Close_over());
	}
	private void createDefaultCloseIcon(final ImageResource Close,final ImageResource CloseOver) {

		//Log.info("creating default close widget");
		//  closeIcon = new Label("X");			
		//    closeIcon.setWidth("20px");
		//  //Log.info("creating default close widget"+((Label)closeIcon).getText());

		final Image newCloseIcon =  new Image ( Close);

		//add default handler

		newCloseIcon.addMouseOverHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				//rateoholicimages.Close_over().applyTo(closeButton);

				newCloseIcon.setResource(CloseOver);
			}

		});
		newCloseIcon.addMouseOutHandler(new MouseOutHandler(){

			public void onMouseOut(MouseOutEvent event) {

				//rateoholicimages.HelpIconClose().applyTo(closeButton);

				newCloseIcon.setResource(Close);



			}

		});

		//associate it as the close widget so it can be passed to the popup help panels
		closeIcon = newCloseIcon;



	}
	protected void setupAssociatedPopup(
			final String inc_helptext,
			final String inc_helptitle			
			) {

		helptitle = inc_helptitle;
		helptext = inc_helptext;

		//	CurrentHelpPopUp_old = inc_CurrentHelpPopUp; //not used anymore
		//	 CurrentHelpIcon_old = inc_CurrentHelpIcon;

		rArrow.getElement().getStyle().setProperty("zIndex", "650");
		lArrow.getElement().getStyle().setProperty("zIndex", "650");
		//this.setUrl("helpicon.gif");
		this.setWidget(help_enabled);

		this.setPixelSize(14,14);


		if (hasCloseIcon)
		{
			//Log.info("creating popup with close widget");				
			associatedHelpPopUp = new HelpPopUp(helptitle,helptext,closeIcon);

			////Log.info("created default close widget "+((Label)closeIcon).getText());

			((HasClickHandlers) closeIcon).addClickHandler(new ClickHandler(){				
				@Override
				public void onClick(ClickEvent event) {
					PopedUp = false;				
					//Log.info("closeing help from X icon");
					//((Image)sender).setUrl("helpicon.gif");
					//((HelpIcon)sender.getParent()).setWidget(help_enabled);

					RootPanel.get().remove(associatedHelpPopUp);	
					removeArrows();
					//CurrentHelpPopUp.clear();
					CurrentHelpPopUpStore.remove(HelpIconsGroupID);

					currentHelpField=null;

					//closeIcon.fireEvent(GwtEvent<MouseOutHandler>);

					//DomEvent.fireNativeEvent( MouseOutEvent.getType(),(HasHandlers)closeIcon); 

					//MouseOutEvent.fireNativeEvent(nativeEvent, closeIcon);

					NativeEvent nativeEvent = Document.get().createMouseOutEvent(0, 10, 
							10, 10, 10, false, false, false, false, NativeEvent.BUTTON_LEFT, 
							closeIcon.getElement()); 

					DomEvent.fireNativeEvent(nativeEvent, closeIcon); 


					CurrentHelpIconStore.get(HelpIconsGroupID).setWidget(CurrentHelpIconStore.get(HelpIconsGroupID).help_enabled);

					//	CurrentHelpIcon.get(0).setWidget(CurrentHelpIcon.get(0).help_enabled);

				}



			});



		} else {	

			associatedHelpPopUp = new HelpPopUp(helptitle,helptext);

		}

		HelpPopUpLink = associatedHelpPopUp;

		newpopup = triggerPopUp(associatedHelpPopUp);

		//all images except disabled should be assigned clicklistener
		help_enabled.addClickHandler(newpopup);		
		help_pressed.addClickHandler(newpopup);
	}


	public void setHelpPopupStylePrimaryName(String style){
		if (associatedHelpPopUp!=null){
			associatedHelpPopUp.setStylePrimaryName(style);
		}

	}

	private ClickHandler triggerPopUp(final HelpPopUp HelpPopUp) {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {

				//remove existing popups
				SpiffyTextField.clearValidation();

				Widget sender = (Widget)event.getSource();

				if (forceVertical)
				{
					VerticalSet=true;
				} else {
					VerticalSet=false;
				}

				//	popup(CurrentHelpPopUp, CurrentHelpIcon, HelpPopUp, sender);
				popup(HelpPopUp, sender);


			}



		};
	}

	public void setHelpField(final Widget inc_HelpField){
		HelpField = inc_HelpField;
	}

	/** Enable the HelpIcon to make it clickable **/
	public void setEnable(Boolean enabled){

		if (enabled){
			//this.setUrl("helpicon.gif");
			//this.addClickListener(newpopup);
			this.setWidget(help_enabled);

		} else {
			//this.setUrl("helpicon_disabled.gif");
			//this.removeClickListener(newpopup);
			this.setWidget(help_disabled);

			//if we disable, this popup should close
			if (PopedUp == true){
				PopedUp=false;	
				//  RootPanel.get().remove(CurrentHelpPopUp.get(0));
				RootPanel.get().remove(CurrentHelpPopUpStore.get(HelpIconsGroupID));


				removeArrows();
				// CurrentHelpPopUp.clear();	
				CurrentHelpPopUpStore.remove(HelpIconsGroupID);
			}

		}


	}
	/** toggles this help icons popup open/close **/
	public void trigger_popup (){
		//remove existing popups
		SpiffyTextField.clearValidation();


		if (forceVertical)
		{
			VerticalSet=true;
		} else {
			VerticalSet=false;
		}
		//Log.info("trigger login popup2");
		//popup(CurrentHelpPopUp, CurrentHelpIcon, (HelpPopUp)HelpPopUpLink, this.getWidget());

		popup((HelpPopUp)HelpPopUpLink, this.getWidget());

	}
	
	public static void removeHelpPopUps(){
		removeHelpPopUps(DefaultHelpIconsGroupID);
	}
	
	public static void removeHelpPopUps(String HelpIconsGroupID){

		
		//String HelpIconsGroupID = DefaultHelpIconsGroupID;
		
		//get the current popup for this ID
		HelpPopUp popToClose= CurrentHelpPopUpStore.get(HelpIconsGroupID);
		
		//if one is found close it
		if (popToClose!=null)
		{
			//RootPanel.get().remove(CurrentHelpPopUp.get(0));	
			RootPanel.get().remove(popToClose);	

			removeArrows();	


			//CurrentHelpIcon.get(0).PopedUp=false;
			//CurrentHelpIcon.get(0).setWidget(CurrentHelpIcon.get(0).help_enabled);	
			
			//close any icons matching too
			
			CurrentHelpIconStore.get(HelpIconsGroupID).PopedUp=false;
			CurrentHelpIconStore.get(HelpIconsGroupID).setWidget(CurrentHelpIconStore.get(HelpIconsGroupID).help_enabled);	

		}
	}
	private void popup(
			//final ArrayList<HelpPopUp> CurrentHelpPopUp,
			//final ArrayList<HelpIcon> CurrentHelpIcon,
			final HelpPopUp HelpPopUp, Widget sender) 
	{

		HelpPopUp CurrentHelpPopUp =  CurrentHelpPopUpStore.get(HelpIconsGroupID);
		HelpIcon  CurrentHelpIcon  =  CurrentHelpIconStore.get(HelpIconsGroupID);




		currentHelpField = HelpField;
		VisibleHelpPopUp = HelpPopUp;

		//Log.info("triggering help popup1");
		// int Y;
		// int X;
		//int DisplacementY=0;
		if (!(currentHelpField == null)){
			//  Y = currentHelpField.getAbsoluteTop();
			//  DisplacementY = currentHelpField.getOffsetHeight()/2;
			//  X = currentHelpField.getAbsoluteLeft()+currentHelpField.getParent().getOffsetWidth();
		} else {			

			currentHelpField =  sender.getParent();
			// Y =  sender.getParent().getAbsoluteTop();
			//  DisplacementY =0;
			//  X = (int)(Window.getClientWidth()*0.74);
		}

		if (PopedUp== false)
		{
			PopedUp = true;	

			//System.out.print("num of elements="+CurrentHelpPopUp.size());

			if (CurrentHelpPopUp!=null)
			{
				//Log.info("triggering help popup2.1b");
				RootPanel.get().remove(CurrentHelpPopUp);	
				removeArrows();
				CurrentHelpIcon.PopedUp=false;
				//CurrentHelpIcon.get(0).setUrl("helpicon.gif");
				CurrentHelpIcon.setWidget(CurrentHelpIcon.help_enabled);	
			}

			//CurrentHelpPopUp.clear(); 
			//CurrentHelpPopUp.add(HelpPopUp); 

			CurrentHelpPopUpStore.remove(HelpIconsGroupID);//remove current association
			CurrentHelpPopUpStore.put(HelpIconsGroupID, HelpPopUp); //add new one

			//CurrentHelpIcon.clear();		
			//CurrentHelpIcon.add((HelpIcon)(sender.getParent()));

			CurrentHelpIconStore.remove(HelpIconsGroupID);//remove current association
			CurrentHelpIconStore.put(HelpIconsGroupID, (HelpIcon)(sender.getParent()));



			//((Image)sender).setUrl("helpicon_pressed.gif");
			((HelpIcon)sender.getParent()).setWidget(help_pressed);

			//RootPanel.get().add(HelpPopUp, X, DisplacementY+Y-(HelpPopUp.getHeight()/2));
			//RootPanel.get().setWidgetPosition(VisibleHelpPopUp, X, Y+DisplacementY-(VisibleHelpPopUp.getHeight()/2));
			int width = 18;
			int height = 10;	

			//Log.info("triggering help popup (checking arrows are drawen)");
			//if right arrow isnt yet made, then draw it.
			if (!rArrowDrawen){			
				rArrow.clear();				
				rArrow.setSize(width+"px", height+"px");
				rArrow.setCoordSize(width, height);	

				rArrow.setBackgroundColor(GWTCanvas.TRANSPARENT);
				rArrow.setLineWidth(1);		
				//right facing arrow
				rArrow.beginPath();
				rArrow.moveTo(0, height/2);
				rArrow.lineTo(3, (height/2)-3);
				rArrow.moveTo(0, height/2);
				rArrow.lineTo(3, (height/2)+3);
				rArrow.moveTo(0, height/2);
				rArrow.lineTo(width, height/2);
				rArrow.stroke();	
				//dont have to draw it again
				rArrowDrawen = true;
			}
			//-------------------------------------------------
			//if left arrow isnt yet made, then draw it.
			if (!lArrowDrawen){			
				lArrow.clear();				
				lArrow.setSize(width+"px", height+"px");
				lArrow.setCoordSize(width, height);	

				lArrow.setBackgroundColor(GWTCanvas.TRANSPARENT);
				lArrow.setLineWidth(1);		
				//left facing arrow
				lArrow.beginPath();
				lArrow.moveTo(width, height/2);
				lArrow.lineTo(width-3, (height/2)-3);
				lArrow.moveTo(width, height/2);
				lArrow.lineTo(width-3, (height/2)+3);
				lArrow.moveTo(width, height/2);
				lArrow.lineTo(0, height/2);
				lArrow.stroke();	
				//dont have to draw it again
				lArrowDrawen = true;
			}
			//-------------------------------------------------
			//if up arrow isnt yet made, then draw it.
			if (!uArrowDrawen){			
				uArrow.clear();				
				uArrow.setSize(width+"px", height+"px");
				uArrow.setCoordSize(width, height);	

				uArrow.setBackgroundColor(GWTCanvas.TRANSPARENT);
				uArrow.setLineWidth(1);		
				//left facing arrow
				uArrow.beginPath();
				uArrow.moveTo(width/2, 0);		
				uArrow.lineTo((width/2)-3, 3);		
				uArrow.moveTo(width/2, 0);		
				uArrow.lineTo((width/2)+3, 3);		
				uArrow.moveTo(width/2, 0);		
				uArrow.lineTo(width/2, height);
				uArrow.stroke();	
				//dont have to draw it again
				uArrowDrawen = true;
			}
			//-------------------------------------------------
			//-------------------------------------------------
			//if up arrow isnt yet made, then draw it.
			if (!dArrowDrawen){			
				dArrow.clear();				
				dArrow.setSize(width+"px", height+"px");
				dArrow.setCoordSize(width, height);	

				dArrow.setBackgroundColor(GWTCanvas.TRANSPARENT);
				dArrow.setLineWidth(1);		
				//left facing arrow
				dArrow.beginPath();
				dArrow.moveTo(width/2, height);		
				dArrow.lineTo((width/2)-3, height-3);		
				dArrow.moveTo(width/2, height);		
				dArrow.lineTo((width/2)+3, height-3);		
				dArrow.moveTo(width/2, height);		
				dArrow.lineTo(width/2, 0);
				dArrow.stroke();	
				//dont have to draw it again
				dArrowDrawen = true;
			}
			//-------------------------------------------------







			//RootPanel.get().add(Arrow, X-12, Y-(height/2)+DisplacementY);
			//Log.info("triggering help popup3");
			updateHelpPopupsPosition();

		} else
		{
			PopedUp = false;				
			//Log.info("closeing help popup4");
			//((Image)sender).setUrl("helpicon.gif");
			((HelpIcon)sender.getParent()).setWidget(help_enabled);

			RootPanel.get().remove(HelpPopUp);	
			removeArrows();
			//CurrentHelpPopUp.clear(); 

			CurrentHelpPopUpStore.remove(HelpIconsGroupID); //remove assocation

			currentHelpField=null;
		}
	}
	
	static public void updateCurrentHelpPopupsPosition(){
		CurrentHelpIconStore.get(DefaultHelpIconsGroupID).updateHelpPopupsPosition();
	}
	static public void updateCurrentHelpPopupsPosition(String HelpIconsGroupID){
		CurrentHelpIconStore.get(HelpIconsGroupID).updateHelpPopupsPosition();
	}
	
	public void updateHelpPopupsPosition(){

		if (!(currentHelpField == null)){
			
			final int Y = currentHelpField.getAbsoluteTop();
			//final int X = currentHelpField.getAbsoluteLeft()+currentHelpField.getOffsetWidth(); //old method taken from field
			
			final int X = this.getAbsoluteLeft()+this.getOffsetWidth()+10; //new method with position from icon. The 10pixels is hard coded from what gwt gives the text-indent style
			Log.info("test widget = "+this.getOffsetWidth());
			Log.info("X = "+X);
			
			int  DisplacementY = currentHelpField.getOffsetHeight()/2;

			//align to left or right based on screen position.
			//Window.setTitle("displaying help popup");

			removeArrows();	

			RootPanel.get().add(VisibleHelpPopUp, X+13, Y+DisplacementY-(VisibleHelpPopUp.getHeight()/2));
			RootPanel.get().add(rArrow, X, Y-5+DisplacementY);


			if ((X+13)+VisibleHelpPopUp.getOffsetWidth() > Window.getClientWidth()){
				// flip arrow
				//	Arrow.saveContext();
				//	Arrow.rotate(34);
				//	Arrow.restoreContext();		

				//Window.setTitle("flipping arrow");			
				RootPanel.get().remove(rArrow);
				RootPanel.get().add(VisibleHelpPopUp, currentHelpField.getAbsoluteLeft()-VisibleHelpPopUp.getOffsetWidth() - 31 , Y+DisplacementY-(VisibleHelpPopUp.getHeight()/2));
				RootPanel.get().add(lArrow, currentHelpField.getAbsoluteLeft() - 32, Y-5+DisplacementY);
			} else {
				//RootPanel.get().add(VisibleHelpPopUp, X+13, Y+DisplacementY-(VisibleHelpPopUp.getHeight()/2));
				//RootPanel.get().add(Arrow, X, Y-5+DisplacementY);
			}

			if (VerticalSet){		
				removeArrows();	
				//centered below
				RootPanel.get().add(VisibleHelpPopUp, X-(VisibleHelpPopUp.getOffsetWidth()/2), Y+currentHelpField.getOffsetHeight()+22);
				RootPanel.get().add(uArrow, X-17, Y+currentHelpField.getOffsetHeight()+11);					
			}


		}

	}

	private static void removeArrows() {

		//Log.info("removing arrows");

		//remove if already attached
		if (lArrow.isAttached()){
			RootPanel.get().remove(lArrow);
		}
		if (rArrow.isAttached()){
			RootPanel.get().remove(rArrow);
		}
		if (uArrow.isAttached()){
			RootPanel.get().remove(uArrow);
		}
		if (dArrow.isAttached()){
			RootPanel.get().remove(dArrow);
		}
	}

}

