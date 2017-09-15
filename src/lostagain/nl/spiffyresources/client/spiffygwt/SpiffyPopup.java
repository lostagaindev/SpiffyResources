package lostagain.nl.spiffyresources.client.spiffygwt;


import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

//broken size
public class SpiffyPopup extends DialogBox {

	Logger Log = Logger.getLogger("SpiffyGWT.SpiffyPopup");
	
		//Images (close button)
		private StandardInterfaceImages rateoholicimages = (StandardInterfaceImages) GWT
		.create(StandardInterfaceImages.class);
		
		AbsolutePanel ButtonFrame = new AbsolutePanel();
		VerticalPanel WidgetContainer = new VerticalPanel ();
		
		Boolean draggable = true;
		
		String width = "640px";
		String height = "auto";

		/** recently changed internally to take strings as size specs**/
		public SpiffyPopup(String Title,int width,int height){
			
			setup(Title,width+"px",height+"px");
			
		}
		

	
		
		
		
		/** recently changed to take strings as size specs**/
	public SpiffyPopup(String Title,String width,String height){

		
		setup(Title, width, height);

	}

	public void setAnimationToOneWayCorner(){
		super.setAnimationType(AnimationType.ONE_WAY_CORNER);
	}
	
	public void setAnimationToRollDown(){
		super.setAnimationType(AnimationType.ROLL_DOWN);
	}
	
		private void setup(String Title, String width, String height) {
			// Set the contents of the Widget
			this.setHTML("<div class=\"general_title\"> "+Title+" </div>");		
			
			super.setWidth(width);
			super.setHeight(height);
			
			

			WidgetContainer.setWidth(width);
			WidgetContainer.add(ButtonFrame);
			
			final Image closeButton = new Image(rateoholicimages.Close());
			
			closeButton.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					hide();				
				}
				
			});
			
			
			closeButton.addMouseOverHandler(new MouseOverHandler(){
				public void onMouseOver(MouseOverEvent event) {
					
				//	rateoholicimages.Close_over().applyTo(closeButton);
					closeButton.setResource(rateoholicimages.Close_over());
					
				}
				
			});
			closeButton.addMouseOutHandler(new MouseOutHandler(){
			
				public void onMouseOut(MouseOutEvent event) {
					
					//rateoholicimages.Close().applyTo(closeButton);
					closeButton.setResource(rateoholicimages.Close());
					
				}
				
			});
			
			//ButtonFrame.setWidth((width-30)+"px");   
			ButtonFrame.setWidth("100%");
			
			Log.info("adding close button");
			 ButtonFrame.add(closeButton);//,(width-30)-1,0);
			 closeButton.getElement().getStyle().setTop(0, Unit.PX);
			 closeButton.getElement().getStyle().setRight(20, Unit.PX);
			 closeButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
			 
			 Log.info("adding close button"+closeButton.isAttached());	
			 
			 setWidget(WidgetContainer);
			 Style bStyle = ButtonFrame.getElement().getStyle();
			 bStyle.setProperty("position", "absolute");
			bStyle.setProperty("top", "10px");
     //   bStyle.setProperty("left", "-20px");
     //   bStyle.setProperty("width", "20px");
     //   bStyle.setProperty("height", "20px");
			 ButtonFrame.getElement().getStyle().setProperty("overflow", "visible");
			 WidgetContainer.getElement().getStyle().setProperty("overflow", "visible");
			 
      //  getElement().getStyle().setProperty("backgroundColor", "#eef");
      //  WidgetContainer.getElement().getStyle().setProperty("backgroundColor", "#ffc");
      //  WidgetContainer.getElement().getStyle().setProperty("borderTop", "1px dotted red");
			// getElement().getStyle().setProperty("border", "1px dotted red");
			
			
			
			this.setGlassEnabled(true);
		}

	protected void beginDragging(MouseDownEvent e)
	   {
		if(!draggable){
	      e.preventDefault();
		}
		
		}
	
	public void setDragable(Boolean b)
	{
		draggable=b;
	}
	
	public void addWidgetCenter(Widget contents){

        WidgetContainer.add(contents);
	}
	

}
