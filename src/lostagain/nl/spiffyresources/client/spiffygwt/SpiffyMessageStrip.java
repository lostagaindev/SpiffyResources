package lostagain.nl.spiffyresources.client.spiffygwt;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class SpiffyMessageStrip extends VerticalPanel {

	Label TitleTextL = new Label("");
	Label MessageTextL = new Label("");
	SpiffyMessageStrip ThisStrip = this;

	Button BackButton = new Button("Click here to go back to previous page");
	
	Timer triggerfadeout;
	Timer fadeout;
	
	PopupPanel hidethis;
	Style style = ThisStrip.getElement().getStyle();
	
	double opacity = 100;
	private int ShowAtY;
	private int ShowAtX;
	private boolean ShowAtCustom;
	static Widget fadebackobject;
	
	/** This class forms a vertical band across the center of the page telling the user a specific message**/
	public SpiffyMessageStrip(String TitleText, String MessageText, Widget Fadeback){
	
		fadebackobject = Fadeback;
		
		DOM.setStyleAttribute(MessageTextL.getElement(), "whiteSpace", "pre");
		
		
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		TitleTextL.setText(TitleText);
		TitleTextL.setStyleName("MessageTitles");
		
		this.add(TitleTextL);
		MessageTextL.setText(MessageText);
		MessageTextL.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		this.add(MessageTextL);
		this.setSpacing(4);
		
		setupstrip();
		
		
		BackButton.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				//nativeBackButton();
				
				History.back();
				History.back();
				
				fadeout.scheduleRepeating(100);
			}
			
		});
		
		
		
		
		
	}
	
	public void addBackButton(){
		
		//HTML backButton = new HTML("<A HREF=\"javascript:javascript:history.go(-1)\">Click here to go back to previous page</A> ");
		
		if (!(BackButton.isAttached())){
		this.add(BackButton);
		}
	}
	/**this class can also be set up with a widget in the center **/
	public SpiffyMessageStrip(Widget middlebit){
	
		SpiffyFader Fadeback = new SpiffyFader();
		Fadeback.setSize("100%", "100%");
		fadebackobject = Fadeback;
		
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.add(middlebit);		
		setupstrip();		
		
	}
	/**this class can also be set up with a widget in the center and a fader **/
	public SpiffyMessageStrip(Widget middlebit, SpiffyFader Fadeback){
		fadebackobject = Fadeback;
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.add(middlebit);		
		setupstrip();
		
	}
	
	public static native void nativeBackButton() /*-{ 
		
     window.history.back();
    
   }-*/;
	
	private void setupstrip(){
		this.setWidth("100%");
		this.setHeight("100px");
		
		//set propertys
		Style style = ThisStrip.getElement().getStyle();
		style.setProperty("backgroundColor", "#EEEEEE");		
		style.setProperty("zIndex", "10000");
		
		//set timers
		fadeout = new Timer(){
			
			@Override			
			public void run() {
				
				setOpacityFilter(opacity);
				System.out.print((opacity/100)+"\n");
				//ThisImage.getElement().setAttribute("style", " filter: alpha(opacity="+opacity+"); opacity: "+(opacity/100)+";");
				opacity=opacity-15;
				if (opacity<15){
					RootPanel.get().remove(ThisStrip);
					
					if (fadebackobject.isAttached()){
					RootPanel.get().remove(fadebackobject);
					}
					Style fadestyle = fadebackobject.getElement().getStyle();
					fadestyle.setProperty("zIndex", "0");
					
					if (!(hidethis==null)){
					hidethis.hide();
					}
					this.cancel();
					
				}
				
				
				
			}
		
		};
		
		triggerfadeout = new Timer(){
			@Override
			public void run() {
			//	Window.alert("triggering fadeout");
				fadeout.scheduleRepeating(100);
				this.cancel();
				
			}
		
		};
		
		//move fader up
		//Log.info("setting fader style");
		Style fadestyle =fadebackobject.getElement().getStyle();
		fadestyle.setProperty("zIndex", "9000");
		//RateoLoggedInBits.fadeback.removeFromParent();
		
	}
	
	public void setOpacity(double opa){
		opacity = opa;		
		setOpacityFilter(opacity);
	}
	
	private void setOpacityFilter(double opa){
		
		style.setProperty("filter", "alpha(opacity="+opacity+")");
		style.setProperty("opacity", ""+(opacity/100));
		
	}
	public void Hide(){
		RootPanel.get().remove(ThisStrip);
		//move fader down
		Style fadestyle = fadebackobject.getElement().getStyle();
		fadestyle.setProperty("zIndex", "0");
		RootPanel.get().remove(fadebackobject);
	}
	
	public void setXY(int x, int y){
		ShowAtX = x;		
		ShowAtY = y;
		ShowAtCustom = true;
	}
	public void Show(int Duraction){
		Show(Duraction,null);
		
	}
	/** shows the message for duraction, and hides itself and another widget after the timer runs out 
	 * if duration is zero, then it appears perminately **/

	
	public void Show(int Duraction,final PopupPanel hidethispanel){


		
		hidethis = hidethispanel;
		opacity = 100;
		setOpacityFilter(opacity);
	//	GWT.log("fade",null);
		
		RootPanel.get().add(fadebackobject,0,0);
		//move fader up
		//Style fadestyle = RateoLoggedInBits.fadeback.getElement().getStyle();
		//	fadestyle.setProperty("zIndex", "9000");
		//GWT.log("fade2",null);
		
		if (ShowAtCustom){
			RootPanel.get().add(this, ShowAtX, ShowAtY);			
		}else {		
		RootPanel.get().add(this, 0, (Window.getClientHeight()/2)-50 );
		}
		
		
		
		
		
		if (Duraction>0){
		triggerfadeout.schedule(Duraction);
		}
	}
		public void fadeOut(){
			fadeout.scheduleRepeating(100);
		}
		public void fadeOutAfter(int Duration){
		//	Window.alert("faded out schedral"+Duration);
			triggerfadeout.schedule(Duration);
			
		}
}
