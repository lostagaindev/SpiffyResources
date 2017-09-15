package lostagain.nl.spiffyresources.client.spiffygwt;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpPopUp extends SimplePanel {
	
	SpiffyBasicpopup NewPopUp;
	Label TestText = new Label();
	boolean withClosewidget= false;
	Widget CloseWidget;
	
	public HelpPopUp(String title,String Text,Widget closeIcon ){
		
		withClosewidget=true;
		CloseWidget = closeIcon;
		
		
		TestText.setText(" "+Text);	
		
		DOM.setStyleAttribute(TestText.getElement(), "whiteSpace", "pre");
					
	//	//Log.info("now adding close widget = "+((Label)CloseWidget).getText());
		
		NewPopUp = new SpiffyBasicpopup("200","130",
				title,TestText,CloseWidget); 
		 		  		
		NewPopUp.SetTitleMiddleStyle("MainbarBack MainBarBorder");
		NewPopUp.SetMiddleStyle("HelpPopUps");
		NewPopUp.SetRightStyle("HelpPopUps_right");
		NewPopUp.getElement().getStyle().setProperty("zIndex", "600");
		
		this.setStylePrimaryName("HelpPopUp_Overall");
		
		this.add(NewPopUp);
		this.getElement().getStyle().setProperty("zIndex", "600");
		
	}
	
	public HelpPopUp(String title,String Text) {	

		
		TestText.setText(" "+Text);	
		
		DOM.setStyleAttribute(TestText.getElement(), "whiteSpace", "pre");
						
		NewPopUp = new SpiffyBasicpopup("300","130",
				title,TestText); 
		 		  		
		
		NewPopUp.SetMiddleStyle("HelpPopUps");
		NewPopUp.SetRightStyle("HelpPopUps_right");
		NewPopUp.getElement().getStyle().setProperty("zIndex", "600");

		this.setStylePrimaryName("HelpPopUp_Overall");
		
		this.add(NewPopUp);
		this.getElement().getStyle().setProperty("zIndex", "600");
		
	}
	
	public int getHeight(){
	return 130;
	}
	
	}

