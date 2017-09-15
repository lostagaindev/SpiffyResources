package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpiffyListBox extends SimplePanel  {
	
	
	private static String Default_Label_Style = "gwt-Button1";
	
	
	public DisclosurePanel DropDownContainer = new DisclosurePanel();
	public VerticalPanel ListContainer = new VerticalPanel();
	public ScrollPanel ScrollContainer = new ScrollPanel();
	
	
	public ArrayList<String> FieldNames  = new ArrayList<String>();
	ArrayList<String> FieldValues = new ArrayList<String>();
	ArrayList<Label>  FieldLabels = new ArrayList<Label>();
	
		
	public FocusPanel mouseCatcher = new FocusPanel();
	 
	 
	 public Label CurrentSelectedLab = new Label("  ");
     int CurrentSelectNum =0;
	private int visibleitems=1;
	private ChangeHandler onSelected;
	
	public String hoverStyleName = "SpiffylistHoverStyle";

	SpiffyListBox thisbox=this;
	Widget header= CurrentSelectedLab;


	private Boolean allowDuplicates=false;
	
	public SpiffyListBox(){
		
		super.setWidget(DropDownContainer);
					
		DropDownContainer.setHeader(header);
		header.setStylePrimaryName("TitleLabel");
		
		ScrollContainer.add(ListContainer);
		mouseCatcher.add(ScrollContainer);
		
		
		DropDownContainer.setContent(mouseCatcher);
		
		//default style
		DropDownContainer.setStylePrimaryName("SpiffyTextBox");
		
		DropDownContainer.setAnimationEnabled(true);
		
	}
	
	/** widget must have text **/
	public void setHeader(Widget header){
		DropDownContainer.setHeader(header);
		this.header=header;
		
	}
	
	public void allowDuplicates(Boolean state){
		this.allowDuplicates=state;
		
	}

	public void addItem (String name){
		addItem(name, null);
	}
	public void addItem(String name, String value){
		
		//ignore if no duplicates and existing
		if (!allowDuplicates){
			if (FieldNames.contains(name)){
				return;
			}
		}
		
		if (FieldNames.size()==0){
			((HasText) header).setText(name);
			
			CurrentSelectNum=0;
		}
		
		selectableLabel newLabel = new selectableLabel(name);
		newLabel.setStylePrimaryName(Default_Label_Style);
		
		FieldNames.add(name);
		FieldValues.add(value);
		FieldLabels.add(newLabel);
		
		ListContainer.add(newLabel);
		
		
	}
	
	public void setOpen(boolean status){

		DropDownContainer.setOpen(status);
		
	}
	
	public void setAnimationEnabled(boolean status){

		DropDownContainer.setAnimationEnabled(status);
		
	}
	
	/** note; you can set it to 1, or more then 1, but more then 1 currently acts as infinite 
	 * 1 by default**/
	public void setVisibleItemCount(int num){
		visibleitems = num;
		if (num>1){
			
			DropDownContainer.setOpen(true);
			//dont show the header when on this mode.
			header.setVisible(false);
			//remove disclosure style
			DropDownContainer.removeStyleName("content");
			
		}
	}
	
	private class selectableLabel extends Label {
		private final class SelectableLabelHandeler implements
				ClickHandler {		
			
			public void onClick(ClickEvent event) {
				//get sender
				Label current = (Label) event.getSource();					
				int Position = FieldLabels.indexOf(current);
						
				
				
				
				
				//Set current selected
				((HasText) header).setText(current.getText());
				CurrentSelectNum=Position;
				
				//autoclose
				if (visibleitems==1){
				DropDownContainer.setOpen(false);
				}
				
				//trigger change
				if (onSelected!=null){
				onSelected.onChange(null);
				}
				
			}
		}
		private final class SelectableLabelMouseOverHandeler implements
		MouseOverHandler {			
			public void onMouseOver(MouseOverEvent event) {
				Label current = (Label) event.getSource();	
				//Set style to ollover
				current.addStyleName(hoverStyleName);
		}
		}
			private final class SelectableLabelMouseOutHandeler implements
			MouseOutHandler {			
				public void onMouseOut(MouseOutEvent event) {
					Label current = (Label) event.getSource();	
					//Set style to ollover
					current.removeStyleName(hoverStyleName);
			}
	}


		public selectableLabel(String name){
			
			super.setText(name);
						
			super.addClickHandler(new SelectableLabelHandeler());
			super.addMouseOverHandler(new SelectableLabelMouseOverHandeler());
			super.addMouseOutHandler(new SelectableLabelMouseOutHandeler());
			
		}
	}
	
	@Override	
	public void clear() {
		FieldNames.clear();
		FieldValues.clear();
		FieldLabels.clear();		
		ListContainer.clear();
		CurrentSelectNum=0;
		
		  }
	
	public int getSelectedIndex(){
		return CurrentSelectNum;
	}
	
	public String getSelectedItem(){
		return getItemText(CurrentSelectNum);
	}
	public String getItemText(int Item){
		return FieldNames.get(Item);
	}
	public String getValue(int Item){
		return FieldValues.get(Item);
	}

	public void setFocus(boolean value){
		//
	}
	
	
	/**
	 * alas of setSelectedIndex
	 * @param ItemNum
	 */
	public void setSelectedIndex(int ItemNum){
		setItemSelected(ItemNum);
	}
	/**
	 * sets the selected item, the boolean does nothing
	 * @param ItemNum
	 */
	public void setItemSelected(int ItemNum, boolean dosntEffectThisBox){
		setItemSelected(ItemNum);
	}
	
	public void setItemSelected(int ItemNum){
		CurrentSelectNum=ItemNum;
		
	//	Window.alert("selecting item"+CurrentSelectNum);
		
		
		
		if (visibleitems==1){
			((HasText) header).setText( FieldNames.get(CurrentSelectNum) );
		}
		
	}
	/** Just supply a file location of a text file containing
		 *  elements for the box separated by a newline
		 */
	public void loadSuggestionsFromFile(String filenamelocation){
		
		
		FieldNames.clear();
		getSuggestions(filenamelocation);
			
		
		
	}
	
	public void addAll(Collection<String> suggestions){
		
		
		for (String string : suggestions) {
			
			this.addItem(string);
		}
		
		
		
	}
	

	private void getSuggestions(String filenamelocation){
		//final ArrayList<String> Suggestions = new ArrayList<String>();
		
		//request the file
		RequestBuilder suggestionlistFile = new RequestBuilder(RequestBuilder.GET,
				filenamelocation);
		
		System.out.println("get list");
		
		try {
			suggestionlistFile.sendRequest("", new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	System.out.println("failed to get data");
		        }

		        public void onResponseReceived(Request request, Response response) {
		        	
		        	String SuggestionsString=response.getText();
		        	
		        	 String[] stringlist = SuggestionsString.split("\r|\n|\r\n");
		        	Collection<String> test = Arrays.asList(stringlist);
		        	 
		        	thisbox.addAll(test);
		        	
		        }
		      });
		    } catch (RequestException ex) {
		    
		    	System.out.println("get request list failed");
		    }	
				
		
		return;		
	}
	
	
	public int getItemCount(){
		return FieldNames.size();
	}

	public void setWidth(String width){
		super.setWidth(width);
		ListContainer.setWidth(width);
		header.setWidth(width);
	}
	
	public void addChangeHandler(ChangeHandler changeHandler) {
	
		onSelected = changeHandler;
		
	}
	
	public void setHoverStyle(String hoverstyle){
		hoverStyleName = hoverstyle;
	}
	
	public void setDefaultStyle(String style){

		Default_Label_Style =style;
		
	}
}
