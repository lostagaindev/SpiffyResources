package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SpiffyLabel extends Composite {

	
	Label textContents = new Label(); //Control the text on this one
	SimplePanel backgroundPanel; //Control the background on this one
	
	/** Very simple widget for independent text and background styles.
	 *  Also puts a helpIcon next to it **/
	public SpiffyLabel(String Text,HelpIcon helpIcon){
		
		HorizontalPanel overallContainer = new HorizontalPanel();
				
		textContents.setText(Text);
		textContents.setSize("100%", "100%");
		backgroundPanel = makeTextWithBackground(textContents);
		
		overallContainer.setSize("auto", "auto");
		overallContainer.add(backgroundPanel);
		overallContainer.add(helpIcon);
		
		//give them default styles
		backgroundPanel.setStylePrimaryName("SpiffyLabelBackground");
		textContents.setStylePrimaryName("SpiffyLabelText");
		
		
		initWidget(overallContainer);
		
		
		
	}
	
	private SimplePanel makeTextWithBackground(Label textContents){
		
		SimplePanel container = new SimplePanel();
		container.setSize("100%", "100%");
		container.setWidget(textContents);
	
		return container;
		
	}
	
	
	
	
	/** Very simple widget for independent text and background styles (old method generation) **/
	public SpiffyLabel(String Text, String CSSBackStyle, String CSSTextStyle){

		initWidget(new SimplePanel()); //temp
		
		this.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		this.getElement().getStyle().setProperty("height", "auto");				
		this.getElement().setInnerHTML("<div  style=\"width:100%\"><div class=\""+CSSBackStyle+"\"></div><div class="+CSSTextStyle+">"+Text+"</div></div>");
						
	}

	/** Very simple widget for independent widget and background styles (old method generation)  **/
	public SpiffyLabel(Widget widget, String CSSBackStyle, String CSSTextStyle){

		initWidget(new SimplePanel()); //temp
		
		this.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		this.getElement().getStyle().setProperty("height", "auto");				
		//this.setStylePrimaryName(CSSBackStyle);
		
		this.setWidget(widget);
		widget.setStylePrimaryName(CSSTextStyle);
		//widget.getElement().getStyle().setZIndex(2);
		//widget.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		
		this.getElement().setInnerHTML("<div style=\"width:100%\"><div class=\""+CSSBackStyle+"\" Style=\"height: 100%;left: 0px;position: absolute;top: 0px;width: 100%;\"></div><div class=\""+CSSTextStyle+"\" style=\"position: relative;z-index: 2;\">"+widget.getElement().getInnerHTML()+"</div></div>");
		
	}
	
}
