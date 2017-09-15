package lostagain.nl.spiffyresources.client.spiffygwt;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesFocusEvents;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class SpiffyTextField extends Composite implements HasText, HasAllFocusHandlers,HasAllKeyHandlers,HasValueChangeHandlers<String>,HasChangeHandlers,SourcesFocusEvents,SourcesKeyboardEvents,SourcesChangeEvents{
	/**
	 * once set, this will be either a horizontal panel or vertical panel depending on the option selected
	 */
	private CellPanel container;

	public enum LayoutType {
		HorizontalMode,VerticalMode
	}
	private LayoutType LayoutMode = LayoutType.HorizontalMode;
	
	//The Label
	Label FieldLabel = new Label("");
	
	//The Help Icon
	HelpIcon helpicon;
	
	//The Suggestion box
	MultiWordSuggestOracle TextSuggestions = new MultiWordSuggestOracle();
	
	final TextBox textField = new TextBox();
	final SuggestBoxWithPasteDetection suggestField = new SuggestBoxWithPasteDetection(TextSuggestions,textField);

	//Validation bits
	String ValidationString = "";
    String ValidationErrorString = "";
	
    String InternalName = new String();

	private boolean excludeFromStringArray;

	private static Class customPopUp;
	
	
    private static boolean customPopUPisSet = false;
    //global list of fields
    static ArrayList<Widget> FieldList = new ArrayList<Widget>(); 
    
	//global list of invalids
	static ArrayList<Widget> InvalidFieldList = new ArrayList<Widget>(); ;
	  static String teset = "";
	  
	//validation popup Canvas + Box
	static GWTCanvas validationArrows = new GWTCanvas();
	static Widget validationErrors; 
	
	//global style names
	static String EnabledLabelStyle = "notset";
	static String DisabledLabelStyle = "notset";
	static String EnabledFieldStyle = "notset";
	static String DisabledFieldStyle = "notset";
	static String InvalidFieldStyle = "notset";

	private static Widget highestwidgetwitherror;

	private static Widget lowestwidgetwitherror;

	private static Widget rightmostwidgetwitherror;
	
	/** This is a class thats for when you need the user to fill in forms.
	 It contains a suggestible text field, a label, and the ability to enable/disable the
	 whole lot with set styles.	
	 When called with no extra fields, or just a default bit of text, its just the suggest box with enable/disable ability
	**/
	
	public SpiffyTextField(String defaultText){
		createAndInitWidget(LayoutType.HorizontalMode);
		
		//no label or help icon
		container.setWidth("100%");
		container.add(suggestField);
		
		container.setCellWidth(suggestField, "100%");
		suggestField.setWidth("100%");
		suggestField.setHeight("100%");
		suggestField.setText(defaultText);

		FieldList.add(this);
		
		
	}
	

	
	/** This is a class thats for when you need the user to fill in forms.
	 It contains a suggestable text field, a label, and the ability to enable/disable the
	 whole lot with set styles.	
	 When called with no extra fields, its just the suggest box with enable/disable ability
	**/
	public SpiffyTextField(){
		createAndInitWidget(LayoutType.HorizontalMode);
		//no label or help icon
		container.setWidth("100%");
		container.add(suggestField);
		
		container.setCellWidth(suggestField, "100%");
		suggestField.setWidth("100%");
		suggestField.setHeight("100%");
		

		FieldList.add(this);
	}
	
	/** This is a class thats for when you need the user to fill in forms.
	 It contains a suggestible text field and a help icon, and the ability to enable/disable the
	 whole lot with set styles.	
	**/
	public SpiffyTextField(HelpIcon HelpIcon){
		createAndInitWidget(LayoutType.HorizontalMode);
		
		helpicon = HelpIcon;		
		
		container.add(suggestField);
		container.setCellWidth(suggestField, "100%");
		suggestField.setWidth("100%");
		suggestField.setHeight("100%");
		
		FieldList.add(this);
		

		container.add(HelpIcon);		
		container.setCellWidth(HelpIcon, "22px");
		container.setCellHorizontalAlignment(HelpIcon,HasHorizontalAlignment.ALIGN_CENTER);
				
	}
	
	
	/** This is a class thats for when you need the user to fill in forms.
	 It contains a suggestible text field, a label,a help icon, and the ability to enable/disable the
	 whole lot with set styles.	
	**/
	public SpiffyTextField(String LabelText, HelpIcon HelpIcon,LayoutType isvertical){
		createAndInitWidget(isvertical);
		
		helpicon = HelpIcon;		
		FieldLabel.setText(LabelText);
		
		container.setSpacing(1);
		//container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	
		
		container.add(FieldLabel);
		FieldLabel.setWordWrap(false);
		
		if (helpicon!=null){
		
		container.add(HelpIcon);		
		container.setCellWidth(HelpIcon, "22px");
		container.setCellHorizontalAlignment(HelpIcon,HasHorizontalAlignment.ALIGN_CENTER);
				
		}
		container.add(suggestField);
		container.setCellWidth(suggestField, "100%");
		suggestField.setWidth("100%");
		suggestField.setHeight("100%");
		
		FieldList.add(this);
		//internal name defaults to field name
		InternalName = LabelText;
	}
	
	public SpiffyTextField(String LabelText, HelpIcon HelpIcon){
		this(LabelText,HelpIcon,LayoutType.HorizontalMode);
	}
	
	private void createAndInitWidget(LayoutType layoutMode) {
		this.LayoutMode = layoutMode;
		
		if (LayoutMode == LayoutType.HorizontalMode) {
			HorizontalPanel hcontainer = new HorizontalPanel();
			hcontainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			container = hcontainer;
			
		} else {
			container = new VerticalPanel();
		}
		
		initWidget(container);		
	}
	public String getText(){
		return suggestField.getText();		
		
	}

	public void setText(String text){
		suggestField.setText(text);	
		
	}
	
	public void setFieldLabel(String Text){	
		FieldLabel.setText(Text);
		//attach it if needed
		if (!FieldLabel.isAttached()){
			//container.insert(FieldLabel, 0);
			//if (LayoutMode == LayoutType.HorizontalMode) {
			//	((HorizontalPanel)container).insert(FieldLabel, 0);
			//} else {
			//	((VerticalPanel)container).insert(FieldLabel, 0);
			//}
			this.insert(FieldLabel, 0);
			FieldLabel.setWordWrap(false);
		}
			
	}
	public void setFieldTitle(String Text){	
		FieldLabel.setTitle(Text);
	}
	
	public void setFieldStyle(String Stylename){	
		FieldLabel.setStylePrimaryName(Stylename);
	}
	public boolean isEnabled(){
		return textField.isEnabled();
	}
	
	public static void setCustomPopUp(Class newPopUp){
		
		customPopUp = newPopUp;
		customPopUPisSet=true;
		
	}
	
	public void setEnabled(boolean enabled){
		
		
		if (enabled){
			
			//Log.info("_____________setting style to:"+EnabledFieldStyle);			
			suggestField.setStylePrimaryName(EnabledFieldStyle);			
			textField.setEnabled(true);
			//Log.info("enableing editbox:"+textField.isEnabled());
			
			FieldLabel.setStylePrimaryName(EnabledLabelStyle);
			if (!(helpicon==null)){
			helpicon.setEnable(true);
			}
		} else {
			
			suggestField.setStylePrimaryName(DisabledFieldStyle);
			textField.setEnabled(false);
			//Log.info("disableing editbox"+textField.isEnabled());
			FieldLabel.setStylePrimaryName(DisabledLabelStyle);
			
			if (!(helpicon==null)){
				helpicon.setEnable(false);
			}
		}
		
	
	}
	
	/** this should be called when removing a specific widget, else its fields remain in the fieldarray **/
	public void remove(){
		FieldList.remove(suggestField);
	}
	@Override
	public void removeFromParent(){
		remove();
		super.removeFromParent();
	}
/*	public void setHeight(String sheight){
		suggestField.setHeight(sheight);
	}
	public void setWidth(String swidth){
		suggestField.setWidth(swidth);
	}*/
	public void loadSuggestionsFromFile(String filenamelocation){
		/** Just supply a file location of a text file containing
		 *  elements for the box seperated by a newline
		 */
		
		TextSuggestions.clear();
		getSuggestions(filenamelocation,TextSuggestions);
			
		
		
	}
	
	public void loadSuggestions(Collection<String> suggestions){
		/** Just supply a file location of a text file containing
		 *  elements for the box seperated by a newline
		 */
		TextSuggestions.addAll(suggestions);
		
		
	}
	
	static private void getSuggestions(String filenamelocation,final MultiWordSuggestOracle instance ){
		//final ArrayList<String> Suggestions = new ArrayList<String>();
		
		//request the file
		RequestBuilder suggestionlistFile = new RequestBuilder(RequestBuilder.GET,
				filenamelocation);
		
		System.out.println("get list");
		
		try {
			suggestionlistFile.sendRequest("", new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	System.out.println("email of review failed");
		        }

		        public void onResponseReceived(Request request, Response response) {
		        	
		        	String SuggestionsString=response.getText();
		        	
		        	 String[] stringlist = SuggestionsString.split("\r|\n|\r\n");
		        	Collection<String> test = Arrays.asList(stringlist);
		        	 
		        	 instance.addAll(test);
		        	 
		        	
		        }
		      });
		    } catch (RequestException ex) {
		    
		    	System.out.println("get request list failed");
		    }	
				
		
		return;		
	}
	
	
	static public void SetDisabledStyles(String FieldStylename,String LabelStylename){
		DisabledLabelStyle=LabelStylename;
		DisabledFieldStyle=FieldStylename;
	}
	static public void SetEnabledStyles(String FieldStylename,String LabelStylename){
		EnabledLabelStyle=LabelStylename;
		EnabledFieldStyle=FieldStylename;
	}
	static public void setInvalidFieldStyle(String invalidstyle){
		InvalidFieldStyle = invalidstyle;
	}
	
	/** sets validation to be used with CheckValidation **/
	public void setValidationString(String VString){
		ValidationString=VString;
		
	}
	/** sets validation error message **/
	public void setValidationErrorMessage(String ErrorString){
		ValidationErrorString=ErrorString;
		
	}
	/** clear validation list...use this before checking validation **/
	static public void clearValidation(){
		InvalidFieldList.clear();
		
		//remove popup
		if (validationErrors != null ){
			validationErrors.removeFromParent();
		}
		if (validationArrows != null ){
		validationArrows.removeFromParent();
		}
		
	}
	
	/** Check Validation ; You should only check this when the fields are enabled. Disable fields after checking if you wish them to be disabled **/
	public boolean checkValidation(){
		boolean valid =false;		
	//	System.out.print("="+ValidationString);
		String currenttext;
		Widget checkthis = this;
		 if (this.getClass().getName().equals("com.darkflame.client.SpiffyRichTextField")){
			// currenttext = ((SpiffyRichTextField)(this)).richtextbox.getHTML();
			 currenttext = ((SpiffyRichTextField)(this)).richtextbox.getText();
			
			 checkthis =  ((SpiffyRichTextField)(this)).richtextbox;
			 
		 } else {
			 currenttext = this.getText();
			 checkthis = suggestField;
			 
		 }
		 
		if (currenttext.matches(ValidationString)){
			valid=true;
			checkthis.setStylePrimaryName(EnabledFieldStyle);
			
		}	else {	
			checkthis.setStylePrimaryName(InvalidFieldStyle);
		//set focus
			((Focusable)checkthis).setFocus(true);
			
		
		//add to invalid list
		InvalidFieldList.add(this);
		//teset = suggestField.getText();
		
		System.out.print("\n invalid:"+ValidationString+"|."+this.getText()+".| \n");
		
	    }
		//System.out.print(ValidationString+"setting style to=-"+InvalidFieldStyle);
		return valid;		
	}
	
	
	
	public void setFocus(boolean state){
		suggestField.setFocus(state);
	}
	
	@Deprecated
	public void addFocusListener(FocusListener listener) {
		suggestField.addFocusListener(listener);
		
	}
	
	public HandlerRegistration addFocusHandler(FocusHandler listener) {
		
		return suggestField.getTextBox().addFocusHandler(listener);
		
	}
	@Deprecated
	public void removeFocusListener(FocusListener listener) {
		suggestField.removeFocusListener(listener);
	}
	@Deprecated
	public void addKeyboardListener(KeyboardListener listener) {
		suggestField.addKeyboardListener(listener);
	}
	@Deprecated
	public HandlerRegistration addClickListener(ClickListener handler) {
	 suggestField.addClickListener(handler);

		return null;
	}
	public HandlerRegistration addClickHandler(ClickHandler handler) {
	 suggestField.getTextBox().addClickHandler(handler);

		return null;
	}
	@Deprecated
	public void removeKeyboardListener(KeyboardListener listener) {
		suggestField.removeKeyboardListener(listener);
	}
	@Deprecated
	public void addChangeListener(ChangeListener listener) {
		suggestField.addChangeListener(listener);
	}
	@Deprecated
	public void removeChangeListener(ChangeListener listener) {
		suggestField.removeChangeListener(listener);
	}
	
	/** Display the validation errors if there is them. Returns false if there isnt any**/
	public static boolean displayValidationErrors(){
		
	// if no errors we exit
		if (InvalidFieldList.size()==0){
			
			return false;
		}
		
		//Get highest and lowest field box point, as well as the rightmost side
		
		Iterator<Widget> it = InvalidFieldList.iterator();
		int high = 0;
		int low = 5000;
		int rightmost = 0;

		//Reset the error message string
		String ErrorMessageString = "";
		
		//if one wrong
//		if (InvalidFieldList.size()==1){
//			Widget Current = it.next();
//			//skip if invisible
//			if ((Current.isVisible()==false)){				
//				//do nothing
//			}
//			if ((Current.getAbsoluteTop()==0)){
//				//do nothing
//			}
//			
//			high = Current.getAbsoluteTop()+(Current.getOffsetHeight()/2);
//			rightmost = Current.getAbsoluteLeft()+Current.getOffsetWidth();
//			highestwidgetwitherror = Current;
//			
//			
//		} else {
		//if theres more then one wrong
		
		GWT.log("checking widgets ");
		while (it.hasNext()){
			
			Widget Current = it.next();
			
			//skip if invisible
			if ((Current.isVisible()==false)){				
				continue;
			}
			if ((Current.getAbsoluteTop()==0)){
				continue;
			}
			
		//	System.out.print("\n"+Current.getAbsoluteTop());
			
			if (Current.getAbsoluteTop() > high){
				high = Current.getAbsoluteTop()+(Current.getOffsetHeight()/2);
				highestwidgetwitherror = Current;
				
			}
			if (Current.getAbsoluteTop() < low){
				low = Current.getAbsoluteTop()+(Current.getOffsetHeight()/2);
				lowestwidgetwitherror = Current;
				
			}
			if ((Current.getAbsoluteLeft()+Current.getOffsetWidth()) > rightmost){
				rightmost = Current.getAbsoluteLeft()+Current.getOffsetWidth();
				rightmostwidgetwitherror = Current;
				
			}
			
			
			//add to ErrorMessageString too if there is one;
			if (Current.getClass().getName().equals("com.darkflame.client.SpiffyTextField")){
			ErrorMessageString=ErrorMessageString+"\n - "+((SpiffyTextField)Current).ValidationErrorString+" \n ";
			} else if (Current.getClass().getName().equals("com.darkflame.client.SpiffyRichTextField")){
				ErrorMessageString=ErrorMessageString+"\n - "+((SpiffyRichTextField)Current).ValidationErrorString+" \n ";
				
			} else if (Current.getClass().getName().equals("com.darkflame.client.SpiffyAutoField")){
				ErrorMessageString=ErrorMessageString+"\n - "+((SpiffyAutoField)Current).ValidationErrorString+" \n ";
				
			}
			else {
				System.out.print(" \n class- "+Current);
			}
		}
	//	}
		
		
		
		
		validationArrows.clear();
	//	System.out.print(" \n highlow- "+(high-low)+"px");
		
		validationArrows.setSize(100+"px", 7+(high-low)+"px");
		validationArrows.setCoordSize(100, 7+(high-low));
		    //create arrows
		
		
		int width = 100;
		int height = 7+(high-low);
		
	//	int width= validationArrows.getOffsetWidth();
		//int height = validationArrows.getOffsetWidth();
		
		validationArrows.setBackgroundColor(GWTCanvas.TRANSPARENT);
		validationArrows.setLineWidth(1);
		
		
		validationArrows.beginPath();
		validationArrows.moveTo(0, 0);
		
		// if more then one error
		if (InvalidFieldList.size()>1){
		validationArrows.cubicCurveTo(width/2,0,width/2,height/2,width,height/2);
		validationArrows.cubicCurveTo(width/2,height/2,width/2,height,0,height);
		} else {						
			validationArrows.moveTo(0, height/2);
			validationArrows.lineTo(3, (height/2)-3);
			validationArrows.moveTo(0, height/2);
			validationArrows.lineTo(3, (height/2)+3);
			validationArrows.moveTo(0, height/2);
			validationArrows.lineTo(width, height/2);
			
		}
		//validationArrows.lineTo(0,validationArrows.getHeight());
		
		validationArrows.stroke();
		
		//display arrows
		GWT.log("width:"+width+"|height:"+height,null);
		
		RootPanel.get().add(validationArrows, rightmost, low);
		
		if (customPopUPisSet){			
		
			
			validationErrors = new HelpPopUp("Validation Errors","Please correct the following errors:\n"+ErrorMessageString);
				
			
		} else {
		validationErrors = new HelpPopUp("Validation Errors","Please correct the following errors:\n"+ErrorMessageString);
	    }
		
			//display popup half way between high and low
		RootPanel.get().add(validationErrors, -100, (((high+low)/2))-(validationErrors.getOffsetHeight()/2));
	//add again to fix height
		RootPanel.get().add(validationErrors, rightmost+100, (((high+low)/2))-(validationErrors.getOffsetHeight()/2));
		   
		return true;
		
	}
	
	/** Updates the validation errors widgets again...usefull to use if tied to popup or movable panel **/
	public static void updateValidationErrorsPosition(){
		
		validationArrows.getElement().getStyle().setProperty("zIndex", "900");
		
		//if they are visiable, we update there position
		if (validationArrows.isAttached()) {
		
		
		int low = lowestwidgetwitherror.getAbsoluteTop()+(lowestwidgetwitherror.getOffsetHeight()/2);
		int high = highestwidgetwitherror.getAbsoluteTop()+(highestwidgetwitherror.getOffsetHeight()/2);
		
		int rightmost = rightmostwidgetwitherror.getAbsoluteLeft()+rightmostwidgetwitherror.getOffsetWidth();
		
		//display arrows
		RootPanel.get().add(validationArrows, rightmost, low);
		
		//display popup half way between high and low
		RootPanel.get().add(validationErrors, -100, (((high+low)/2))-(validationErrors.getOffsetHeight()/2));
	//add again to fix height
		RootPanel.get().add(validationErrors, rightmost+100, (((high+low)/2))-(validationErrors.getOffsetHeight()/2));
		} 
	}
	
	/** clears all the spiffyfields **/
	
	public static void ClearAllFields(){
		
		Iterator<Widget> it = FieldList.iterator();
		
		while (it.hasNext()){
			
			Widget field = it.next();
			((HasText)field).setText("");
			
			//Window.alert("blah");
			 if (field.getClass().getName().equals("com.darkflame.client.SpiffyRichTextField")){
				 ((SpiffyRichTextField)field).setHTML("");
				
			 }
			 
		}
	}
	
	/** this returns all the (non-null) spiffyfields as a url query string**/
	public static String GetAllFieldsAsString(boolean ReturnBlanks){
		
		
		
		String fieldsasstring="";
		
		Iterator<Widget> it = FieldList.iterator();
		
		while (it.hasNext()){
		
			
			
			Widget field = it.next();
			
			//skip if set to exclude
			if (((SpiffyTextField)field).excludeFromStringArray){
				continue;
			}
			
			if (field.getClass().getName().equals("com.darkflame.client.SpiffyTextField")){
			
				String content = ((SpiffyTextField)field).getText();
				if ((content.length()>0)||(ReturnBlanks)){
				String label = ((SpiffyTextField)field).InternalName;
				//escape
				content = content.replaceAll("&","%26").replaceAll("=","%3D");
				fieldsasstring=fieldsasstring+"&"+label.trim()+"="+content;
				}
			}
			if (field.getClass().getName().equals("com.darkflame.client.SpiffyAutoField")){
				
				String content = ((SpiffyAutoField)field).getText();
				if ((content.length()>0)||(ReturnBlanks)){
				String label = ((SpiffyAutoField)field).InternalName;
				//escape
				content = content.replaceAll("&","%26").replaceAll("=","%3D");
				fieldsasstring=fieldsasstring+"&"+label.trim()+"="+content;
				}
			}
			if (field.getClass().getName().equals("com.darkflame.client.SpiffyRichTextField")){
				
				String content = ((SpiffyRichTextField)field).richtextbox.getHTML();
				if ((content.length()>0)||(ReturnBlanks)){
				String label = ((SpiffyRichTextField)field).InternalName;
				//escape
				content = content.replaceAll("&","%26").replaceAll("=","%3D");
				fieldsasstring=fieldsasstring+"&"+label.trim()+"="+content;
				}
			}
		}
		
	return fieldsasstring;	
	}
	public void excludeFromStringArrayCollection (boolean state){
		excludeFromStringArray = state;
	}
	
	//relays standard cellpanel functions
	public int getWidgetIndex(Widget child) {
		return container.getWidgetIndex(child);
	}
	
	public boolean remove(Widget child) {
		return container.remove(child);
	}
	
	public void insert(Widget w, int beforeIndex) {
		
		if (LayoutMode == LayoutType.HorizontalMode) {
			((HorizontalPanel)container).insert(w, beforeIndex);
		} else {
			((VerticalPanel)container).insert(w, beforeIndex);
		}
		
		return; 
	}
	
	
	

	
	
	/** Note; In most cases you should set the enabled/disabled styles, this will overwrite the current but not change either
	 * so next time it gets enabled/disabled changes will be lost **/
	@Override
	public void setStyleName(String style) {
		suggestField.setStyleName(style);		
	}

	/** Note; In most cases you should set the enabled/disabled styles, this will overwrite the current but not change either
	 * so next time it gets enabled/disabled changes will be lost **/
	@Override
	public void setStylePrimaryName(String style) {
		suggestField.setStylePrimaryName(style);		
	}
	
	/** Note; In most cases you should set the enabled/disabled styles, this will overwrite the current but not change either
	 * so next time it gets enabled/disabled changes will be lost **/
	@Override
	public void addStyleName(String style) {
		suggestField.setStyleName(style, true);
    }
	
	
	public HandlerRegistration addBlurHandler(BlurHandler handler) {	 
		return  suggestField.getValueBox().addBlurHandler(handler);
	}
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		
		suggestField.getTextBox().addKeyUpHandler(handler);
		return null;
	}
	
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {				
		return textField.addKeyDownHandler(handler);
	}
	
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return textField.addKeyPressHandler(handler);
	}
	
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return suggestField.getValueBox().addValueChangeHandler(handler);
	}
	
	public HandlerRegistration addSelectionHandler(SelectionHandler<SuggestOracle.Suggestion> handler) {		
		return suggestField.addSelectionHandler(handler);
	}
	
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {		
		return suggestField.getValueBox().addChangeHandler(handler);
	}
	//public HandlerRegistration addFocusHandler(FocusHandler handler) {
		
	//	return null;
	//}
}
