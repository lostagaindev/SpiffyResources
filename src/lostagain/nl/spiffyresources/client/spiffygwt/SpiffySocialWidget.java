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

public class SpiffySocialWidget extends FlowPanel {
	
	String app_id = "254049558078629";

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	private StandardInterfaceImages standardicons = (StandardInterfaceImages) GWT
	.create(StandardInterfaceImages.class);
	
	final Image TumblrShareButton = new Image(standardicons.TumblrPostImage());
	
	final Image FacebookShareButton = new Image(standardicons.FaceBookShareImage());
	final Image DigShareButton = new Image(standardicons.DigShareImage());
	final Image DelShareButton = new Image(standardicons.DeliciousShareImage());
	
	final Image FacebookShareButtonOver = new Image(standardicons.FaceBookShareImageOver());
	final Image DigShareButtonOver= new Image(standardicons.DigShareImageOver());
	final Image DelShareButtonOver = new Image(standardicons.DeliciousShareImageOver());
	
		final Image TwitterShareButton= new Image(standardicons.TweetImage());
	final Image TwitterShareButtonOver = new Image(standardicons.TweetImageOver());
	
	//needs images
	final Image GooglePlusShareButton= new Image(standardicons.GooglePlusImage());
	final Image GooglePlusShareButtonOver = new Image(standardicons.GooglePlusImageOver());
	
	//link for twitter;
	//<a href="https://twitter.com/share?url=https%3A%2F%2Fdev.twitter.com%2Fpages%2Ftweet-button" target="_blank">Tweet</a>
	
	
	Label lab_ShareThis = new Label("Share this page on:");
	Logger logger = Logger.getLogger("SpiffyGWT.SocialSharingWidget");
	
	String url;
	String ImageURL="";
	String contentDiscription="";
	String contentTitle="";
	
	//tumblr container
	//InlineHTML TumblrContainer = new InlineHTML();
	
	
	//google plus container html
	InlineHTML googlePlusContainer = new InlineHTML();


	private String GooglePlusStatic="";
	
	

	public void setImageURL(final String imageURL){
		ImageURL =imageURL;
	}
	
	public SpiffySocialWidget(String urlToShare,final String imageURL,String discription,String contentTitle){
		super();
		ImageURL =imageURL;
		contentDiscription = discription;
		this.contentTitle=contentTitle;
		createSSWidget(urlToShare);
		
	}
	
	public SpiffySocialWidget(String urlToShare,final String imageURL ){
		super();
		ImageURL =imageURL;
		createSSWidget(urlToShare);
		
	}
	public SpiffySocialWidget(String urlToShare){
		super();
		
		createSSWidget(urlToShare);

		
		
	}
	@Override
	public void onAttach(){
		super.onAttach();

		//url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html?DisplayReview="+ReviewID;
		
		//this.createGooglePlus(url);
		//createSSWidget(ReviewID);
		
		
		
		
		return;	
	}
	private void createSSWidget(final String urlToShare) {
		url = urlToShare;
		
		
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
	
		//String urlToShare = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html?DisplayReview=";
		
		FacebookShareButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				lab_ShareThis.setTitle("--FSBtest--");
				//String url = "http://www.rateoholic.co.uk/main/Rateoholic_Frame.html#!DisplayReview="+ReviewID;
				//Window.open("http://www.facebook.com/sharer.php?u="+url, "_blank","");
			
				
				
				String url ="";
				if (ImageURL.length()>3){
					url = "http://www.facebook.com/sharer.php?s=100"+"&p[url]="+urlToShare+"";
					if ((ImageURL.length()>3)||((contentTitle.length()>3))){
						url=url+"&p[images][0]="+ImageURL+"&p[title]="+contentTitle;
					}
					if ((contentTitle.length()>3)){
						url=url+"&p[title]="+contentTitle;
					}
					
					
				} else {
					url = "http://www.facebook.com/sharer.php?u="+urlToShare;
				}
				
				if (app_id!=""){
                    url = "https://www.facebook.com/dialog/feed?"
			         + "app_id="+app_id
					 + "&display=popup&caption=An%20example%20caption"
					 + "&link="+urlToShare
					 + "&picture="+ImageURL
					 + "&caption="+contentTitle
					 + "&description"+contentTitle
					 + "&redirect_uri=http://www.fanficmaker.com/";
				}
				
				Window.open(url, "_blank","");
				
			}			
		});
		
		FacebookShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
				lab_ShareThis.setTitle("--FB mouse over--");
				FacebookShareButton.setResource(standardicons.FaceBookShareImageOver());
				
			//	rateoholicimages.FaceBookShareImageOver().applyTo(FacebookShareButton);
				
			}			
		});
		FacebookShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
				FacebookShareButton.setResource(standardicons.FaceBookShareImage());
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
				DigShareButton.setResource(standardicons.DigShareImageOver());
				
			}			
		});
		DigShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
			//	rateoholicimages.DigShareImage().applyTo(DigShareButton);
				DigShareButton.setResource(standardicons.DigShareImage());
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
					DelShareButton.setResource(standardicons.DeliciousShareImageOver());
		//		rateoholicimages.DeliciousShareImageOver().applyTo(DelShareButton);
				
			}			
		});
		DelShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				DelShareButton.setResource(standardicons.DeliciousShareImage());
			//	rateoholicimages.DeliciousShareImage().applyTo(DelShareButton);
				
			}			
		});
		this.add(DelShareButton);
		
		TwitterShareButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				//<a href="https://twitter.com/share?url=https%3A%2F%2Fdev.twitter.com%2Fpages%2Ftweet-button" target="_blank">Tweet</a>
				Window.open("https://twitter.com/share?url="+url+"&text="+contentTitle, "_blank","");	
			}			
		});
		TwitterShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
			//	rateoholicimages.DigShareImageOver().applyTo(DigShareButton);
				TwitterShareButton.setResource(standardicons.TweetImageOver());
				
			}			
		});
		TwitterShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
			//	rateoholicimages.DigShareImage().applyTo(DigShareButton);
				TwitterShareButton.setResource(standardicons.TweetImage());
			}			
		});
		this.add(TwitterShareButton);
		
		GooglePlusShareButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				//<a href="https://twitter.com/share?url=https%3A%2F%2Fdev.twitter.com%2Fpages%2Ftweet-button" target="_blank">Tweet</a>
				if (GooglePlusStatic==""){
					Window.open("https://plus.google.com/share?url="+url, "_blank","");
				} else {
					Window.open("https://plus.google.com/share?url="+GooglePlusStatic, "_blank","");
				}
			}			
		});
		
		GooglePlusShareButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
			//	rateoholicimages.DigShareImageOver().applyTo(DigShareButton);
				GooglePlusShareButton.setResource(standardicons.GooglePlusImageOver());
				
			}			
		});
		GooglePlusShareButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
			//	rateoholicimages.DigShareImage().applyTo(DigShareButton);
				GooglePlusShareButton.setResource(standardicons.GooglePlusImage());
			}			
		});
		this.add(GooglePlusShareButton);
		
		//https://www.tumblr.com/docs/en/share_button
		//Build Tumblr String
				
		//String TrumblyHref = "https://www.tumblr.com/share";		
		//String TrumblrString = "<a class=\"tumblr-share-button\" href=\""+TrumblyHref+"\"></a>\r\n" + 
			//	"<script id=\"tumblr-js\" async src=\"https://assets.tumblr.com/share-button.js\"></script>";
		
		//TumblrContainer.setHTML(TrumblrString);
	    
		
		//https://www.tumblr.com/widgets/share/tool
		TumblrShareButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				//<a href="https://twitter.com/share?url=https%3A%2F%2Fdev.twitter.com%2Fpages%2Ftweet-button" target="_blank">Tweet</a>
				//ImageURL
				//https://www.tumblr.com/widgets/share/tool?posttype=link&content=URL&title=TEXT&caption=TEXT
				String params = "posttype=photo&content="+ImageURL
							   +"&canonicalUrl=http://fanficmaker.com" //" works
							   +"&caption="+contentTitle+"...<br><a href=\""+url+"\">(Full Story Here)" //no need for  target=\"_blank\"
							   +"&shareSource=tumblr_share_button";
				
				Window.open("https://www.tumblr.com/widgets/share/tool?"+params, "_blank","width=540,height=600");
				
			}			
		});
	    this.add(TumblrShareButton); 
		
		
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
	
	/** This creates the G+ button dynamicly when the widget is displayed 
	 * Probably a better way to do it then this.*/
	 
	private void createGooglePlus(String url){		
		this.remove(googlePlusContainer);
		
		// <script src="https://apis.google.com/js/plusone.js"></script>
		// <g:plus action="share"></g:plus>
		//
		//Google Plus
		
		String s = "<script src=\"https://apis.google.com/js/plusone.js\"></script>"
				   +" <g:plus action=\"share\"></g:plus>";
		
	    googlePlusContainer.setHTML(s);
	    
	    this.add(googlePlusContainer);
	  
		
		/*
	    String s = "<g:plusone size=\"medium\" annotation=\"none\" href=\""+url+"\"></g:plusone>";
	    googlePlusContainer.setHTML(s);
	    
	    this.add(googlePlusContainer);
	  
	   Document doc = Document.get();
	   ScriptElement script = doc.createScriptElement();
	  script.setSrc("https://apis.google.com/js/plusone.js");
	  script.setType("text/javascript");
	   script.setLang("javascript");
	   doc.getBody().appendChild(script);*/
	}

	
	public void setGooglePlusStaticURL(String GplusURL) {
		
		GooglePlusStatic = GplusURL;		
		
	}
	
	
	
}
