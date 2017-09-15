package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;

public class SocialSharingWidget extends FlowPanel {
	

	private StandardInterfaceImages rateoholicimages = (StandardInterfaceImages) GWT
	.create(StandardInterfaceImages.class);
	
	
	final Image FacebookShareButton = new Image(rateoholicimages.FaceBookShareImage());
	final Image DigShareButton = new Image(rateoholicimages.DigShareImage());
	final Image DelShareButton = new Image(rateoholicimages.DeliciousShareImage());
	
	final Image FacebookShareButtonOver = new Image(rateoholicimages.FaceBookShareImageOver());
	final Image DigShareButtonOver= new Image(rateoholicimages.DigShareImageOver());
	final Image DelShareButtonOver = new Image(rateoholicimages.DeliciousShareImageOver());
	
//	final Image BuzzShareButton= new Image(rateoholicimages.BuzzShareImage());
	//final Image BuzzShareButtonOver = new Image(rateoholicimages.BuzzShareImageOver());
	

	Label lab_ShareThis = new Label("Share this page on:");
	Logger logger = Logger.getLogger("SpiffyGWT.SocialSharingWidget");
	
	String url;
	String ImageURL;
	int ReviewID;
	
	//google plus container html
	InlineHTML googlePlusContainer = new InlineHTML();
	
	/** this will later be changed for a more general widget, at the moment its rateoholic specific 
	 * @return **/
	
	public void setReviewID(final int ReviewID){
		url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html?DisplayReview="+ReviewID;
		this.ReviewID=ReviewID;
	}
	public void setImageURL(final String imageURL){
		ImageURL =imageURL;
	}
	public SocialSharingWidget(final int ReviewID,final String imageURL ){
		super();
		ImageURL =imageURL;
		this.ReviewID=ReviewID;
		createSSWidget(ReviewID);
		
	}
	public SocialSharingWidget(final int ReviewID){
		super();
		this.ReviewID=ReviewID;
		
		createSSWidget(ReviewID);
//		
//final Image test = new Image("http://static.bbci.co.uk/frameworks/barlesque/2.8.11/desktop/3.5/img/blq-blocks_grey_alpha.png");
//	
//
//test.setTitle("meep!");
//
//logger.log(Level.INFO, "________________adding handler:" + ReviewID);
//		test.addClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				
//				test.setTitle("meeeeeeeeeeeeep!");
//				
//				
//			}
//			
//		});
//		
//
//logger.log(Level.INFO, "________________addhandler:" + ReviewID);
//		this.add(test);
		
		
	}
	@Override
	public void onAttach(){
		super.onAttach();

		url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html?DisplayReview="+ReviewID;
		
		this.createGooglePlus(url);
		//createSSWidget(ReviewID);
		return;	
	}
	private void createSSWidget(final int ReviewID) {
		
		this.getElement().getStyle().setMarginBottom(5, Unit.PX);
		
		lab_ShareThis.setWidth("100%");
		lab_ShareThis.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		lab_ShareThis.getElement().getStyle().setProperty("TextAlign", "center");
		lab_ShareThis.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		
//		final Image test = new Image("http://static.bbci.co.uk/frameworks/barlesque/2.8.11/desktop/3.5/img/blq-blocks_grey_alpha.png");
//	
//		
//		test.addClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				
//				test.setTitle("meeeeeeeeeeeeep!");
//				
//				
//			}
//			
//		});
		
		System.out.println("________________setting up social widgets");
		
		this.add(lab_ShareThis);
	
		url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html?DisplayReview="+ReviewID;
		
		FacebookShareButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				lab_ShareThis.setTitle("--FSBtest--");
				//String url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html#!DisplayReview="+ReviewID;
				Window.open("http://www.facebook.com/sharer.php?u="+url, "_blank","");	
			}			
		});
		
		FacebookShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
				lab_ShareThis.setTitle("--FB mouse over--");
				FacebookShareButton.setResource(rateoholicimages.FaceBookShareImageOver());
				
			//	rateoholicimages.FaceBookShareImageOver().applyTo(FacebookShareButton);
				
			}			
		});
		FacebookShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
				FacebookShareButton.setResource(rateoholicimages.FaceBookShareImage());
				//rateoholicimages.FaceBookShareImage().applyTo(FacebookShareButton);
				
			}
	
		});
		
		FacebookShareButton.setTitle("Share this on Facebook");
		
		this.add(FacebookShareButton);
		
		DigShareButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				

				System.out.println("________________dig triggered");
					Window.open("http://digg.com/submit?phase=2&url="+url, "_blank","");	
			}			
		});
		
		DigShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
			//	rateoholicimages.DigShareImageOver().applyTo(DigShareButton);
				DigShareButton.setResource(rateoholicimages.DigShareImageOver());
				
			}			
		});
		DigShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
			//	rateoholicimages.DigShareImage().applyTo(DigShareButton);
				DigShareButton.setResource(rateoholicimages.DigShareImage());
			}			
		});
		this.add(DigShareButton);
		
		
		DelShareButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
					Window.open("http://del.icio.us/post?v=4&amp;noui&amp;jump=close&amp;url="+url, "_blank","");	
			}			
		});
		DelShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {


					System.out.println("________________del over");
					DelShareButton.setResource(rateoholicimages.DeliciousShareImageOver());
		//		rateoholicimages.DeliciousShareImageOver().applyTo(DelShareButton);
				
			}			
		});
		DelShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				DelShareButton.setResource(rateoholicimages.DeliciousShareImage());
			//	rateoholicimages.DeliciousShareImage().applyTo(DelShareButton);
				
			}			
		});
		this.add(DelShareButton);
		
//		BuzzShareButton.addClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//					Window.open("http://www.google.com/buzz/post?url="+url+"&imageurl="+ImageURL, "_blank","");	
//			//"&imageurl=<Optional image URL>" (for image support)
//			}			
//			
//		});
//		BuzzShareButton.addMouseOverHandler(new MouseOverHandler(){
//			@Override
//			public void onMouseOver(MouseOverEvent event) {
//				
//				rateoholicimages.BuzzShareImageOver().applyTo(BuzzShareButton);
//				
//			}			
//		});
//		BuzzShareButton.addMouseOutHandler(new MouseOutHandler(){
//			@Override
//			public void onMouseOut(MouseOutEvent event) {
//				
//				rateoholicimages.BuzzShareImage().applyTo(BuzzShareButton);
//				
//			}			
//		});
//		this.add(BuzzShareButton);
		
		
	    
	   logger.log(Level.INFO, "________________url added to social widget:" + url);
		//Image FaceBookLink = RateoholicImages.
		//HTML facebookwidget = new HTML("<a  target=\"_blank\" cmImpressionSent=\"1\" href=\"http://www.facebook.com/sharer.php\"> <img  title=\"Add to Facebook\" style=\"WIDTH: 16px; HEIGHT: 16px\" alt=\"Facebook\" src=\"http://sharepoint.microsoft.com/blogs/mikeg/Lists/Photos/icons/facebook.gif\" /></a>");
		
		/** 
		 * 


<a target="_blank" cmImpressionSent="1" href="http://digg.com/submit?phase=2&amp;url=PasteMyURLHere">
<img title="Digg this" style="WIDTH: 16px; HEIGHT: 16px" alt="Digg" src="http://sharepoint.microsoft.com/blogs/mikeg/Lists/Photos/icons/digg.gif" /></a>
<a target="_blank" cmImpressionSent="1" href="http://del.icio.us/post?v=4&amp;noui&amp;jump=close&amp;url=PasteMyURLHere">
<img title="Save to del.icio.us" style="WIDTH: 10px; HEIGHT: 10px" alt="DelIcioUs" src="http://sharepoint.microsoft.com/blogs/mikeg/Lists/Photos/icons/delicious.gif" /></a>
		 * 
		 * **/
	}
	
	private void createGooglePlus(String url){		
		this.remove(googlePlusContainer);
		
		//Google Plus
	    String s = "<g:plusone size=\"medium\" annotation=\"none\" href=\""+url+"\"></g:plusone>";
	    googlePlusContainer.setHTML(s);
	    
	    this.add(googlePlusContainer);
	  
	   Document doc = Document.get();
	   ScriptElement script = doc.createScriptElement();
	  script.setSrc("https://apis.google.com/js/plusone.js");
	  script.setType("text/javascript");
	   script.setLang("javascript");
	   doc.getBody().appendChild(script);
	}
	
}
