package lostagain.nl.spiffyresources.client.spiffygwt;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class SpiffyFavorateIcon extends ToggleButton {
	
	private StandardInterfaceImages rateoholicimages = (StandardInterfaceImages) GWT.create(StandardInterfaceImages.class);
	
	//ToggleButton watchReviewer = new ToggleButton(rateoholicimages.Star_Unselected().createImage(),rateoholicimages.Star_Selected().createImage());
	
	ToggleButton Icon = this;

	private boolean enableUrlUpdateing = false;
	HandlerRegistration updateOnClick;
	private String stringUpdateUrl = "";
	String favorateName = "";
	
	
	public SpiffyFavorateIcon(){
		
		super.getDownFace().setImage(new Image(rateoholicimages.Star_Selected()));
		super.getUpFace().setImage(new Image(rateoholicimages.Star_Unselected()));
						
	}

	/** sets to selected/unselected based on a URL supplied and a name **/
	/** if the URL returns "Yes", then its selected **/	
	/** If it returns "No", then its disabled **/
	/** If it returns "Disable", then its hidden **/
	
	public void checkFavorated(String nameToCheck, String URL){
		
		RequestBuilder checkFavStatus = new RequestBuilder(RequestBuilder.POST,URL);
		
		try {
			checkFavStatus.sendRequest("name="+nameToCheck, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					
				}

				public void onResponseReceived(Request request, Response response) {
										
					if (response.getText().endsWith("yes")){
						Icon.setDown(true);
					} else if (response.getText().endsWith("no")) {
						Icon.setDown(false);
					} else {
						Icon.setVisible(false);
					}
					Icon.setEnabled(true);
					
					
					
					
				}
				
				
				
				
			});
		} catch (RequestException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	/** when this is set up, every click of the fav icon will send a string to a url **/
	/** This string will be in the format "stringForFavorate:yes" or "stringForFavorate:no" **/
	public void setUpdateUrl(String stringForFavorate, String URLtosendtoo){
		enableUrlUpdateing = true;
		favorateName= stringForFavorate;
		stringUpdateUrl = URLtosendtoo;
		
		updateOnClick = super.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				
				if (enableUrlUpdateing){
				
				if (Icon.isDown()){
					sendUpdate("yes");
				} else {
					sendUpdate("no");
				}
				
				//disable till reply
				Icon.setEnabled(false);
				}
				
			}
			
		});
		
	}
	
	
	public void removeAutoUpdate(){
		
		enableUrlUpdateing = false;
		updateOnClick.removeHandler();
		
	}
	
	
	private void sendUpdate(String update){
		
	
	RequestBuilder checkFavStatus = new RequestBuilder(RequestBuilder.POST,stringUpdateUrl);
		
		try {
			checkFavStatus.sendRequest("name="+favorateName+"&favorate="+update, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					
				}

				public void onResponseReceived(Request request, Response response) {
					
					Icon.setEnabled(true);
					
					
				}
				
				
				
				
			});
		} catch (RequestException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
};
