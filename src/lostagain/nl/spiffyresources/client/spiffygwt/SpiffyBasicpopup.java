package lostagain.nl.spiffyresources.client.spiffygwt;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

// For usefull functions used in rateoholic java stuff

public class SpiffyBasicpopup extends Grid {
	
	
	String sizeX = "200px";
	String sizeY = "100px";
	String PopUpTitle = "Title Goes Here";	
	Label ReviewTitle = new Label (PopUpTitle);
	HorizontalPanel titleBar = new HorizontalPanel();
	boolean showCloseBox = false;
	Widget closeWidget;
	
	
	public void SetTitleMiddleStyle(String stylename){
	   
		this.getCellFormatter().setStyleName(0, 1, stylename);
		this.getCellFormatter().setStyleName(0, 2, stylename);
		
	}
	public void SetMiddleStyle(String stylename){
		this.getCellFormatter().setStyleName(1, 1, stylename);
	}
	public void SetRightStyle(String stylename){
		this.getCellFormatter().setStyleName(1, 2, stylename);
	}
	
	/** you can construct a popup size x/y with a title, contents and a widget for a close button (please code the closing system yourself **/
	public SpiffyBasicpopup(String X,String Y,
			String title,Widget Contents, Widget CloseButton) {	
		
		showCloseBox=true;
		closeWidget=CloseButton;
		constructTitle(X, Y, title, Contents);
				
	}
	
	public SpiffyBasicpopup(String X,String Y,
			String title,Widget Contents) {		
		constructTitle(X, Y, title, Contents);
				
	}
	private void constructTitle(String X, String Y, String title,
			Widget Contents) {
		sizeX = X;
		sizeY = Y;
		
		
		ReviewTitle.setText(title);
		ReviewTitle.setStyleName("bold");
		
		this.setSize(sizeX, sizeY);
		
		this.resize(3, 3);
		//-------------
		this.setCellPadding(0);
		this.setCellSpacing(0);
		this.setSize(sizeX, sizeY);
		
		//set border shadows for title
		//this.getCellFormatter().setStyleName(0, 0, "ShadowTopLeft");
		
		this.getCellFormatter().setWidth(0, 0, "8px");
		this.getCellFormatter().setHeight(0, 0, "26px");
		

		titleBar.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		titleBar.add(ReviewTitle);
		if (showCloseBox){			
			
		//	Label closeBox = new Label("X");			
			//closeBox.setWidth("20px");
		//	//Log.info("adding close widget = "+((Label)closeWidget).getText());
			
		//	titleBar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);			
		//	titleBar.add(closeWidget);
		//	int w = closeWidget.getOffsetWidth();
		//	if (w<20){
		//		w=20;
		//	}
			//titleBar.setCellWidth(closeWidget,w+"px");
			
			this.setWidget(0, 2, closeWidget);
			
		}
		
		titleBar.setWidth("100%");
		this.setWidget(0, 1, titleBar);
		
		this.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		this.getCellFormatter().setStyleName(0, 1, "PopUpHeader");
		this.getCellFormatter().setHeight(0, 1, "26px");
		this.getCellFormatter().setStyleName(0, 2, "PopUpHeader");
		this.getCellFormatter().setHeight(0, 2, "26px");
				
		this.getCellFormatter().setWidth(1, 0, "8px");
	//	this.getCellFormatter().setStyleName(1, 0, "ShadowLeft");
		
		// load review into HTML
		this.setWidget(1, 1, Contents);
	//	System.out.println("co");

		this.getCellFormatter().setStyleName(1, 1, "Backstyle");

		this.getCellFormatter().setStyleName(1, 2, "Backstyle");

		this.getCellFormatter().setWidth(2, 0, "8px");
		this.getCellFormatter().setHeight(2, 0, "8px");
	//	this.getCellFormatter().setStyleName(2, 0, "ShadowCorner");
		

		this.getCellFormatter().setHeight(2, 1, "8px");
	//	this.getCellFormatter().setStyleName(2, 1, "ShadowLower");
		
		this.getCellFormatter().setWidth(2, 2, "16px");
		this.getCellFormatter().setHeight(2, 2, "8px");
	//this.getCellFormatter().setStyleName(2, 2, "ShadowBottomRight");
		
		//System.out.println("co");
	}

	}

