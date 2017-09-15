package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.allen_sauer.gwt.log.client.Log;
public class SpiffyVideoBox extends SimplePanel {

	String rawEmbed="";
	HTML processedHTML = new HTML();
	
	final static String BlipTVTemplate = "<iframe src=\"___URLGOESHERE___\" width=\"___sizeX___\" height=\"___sizeY___\" frameborder=\"0\" allowfullscreen></iframe><embed type=\"application/x-shockwave-flash\" src=\"http://a.blip.tv/api.swf#___BlipCode___\" style=\"display:none\"></embed>";
	final static String YoutubeTemplate = "<iframe width=\"___sizeX___\" height=\"___sizeY___\" src=\"___URLGOESHERE___\" frameborder=\"0\" allowfullscreen></iframe>";
		
	String currentURL="";
	String currentID="";
	
	String sizeX="640";
	String sizeY="360";
	
	enum sourceType {
		youtube,blip
	}
	sourceType currentSource;
	
	public SpiffyVideoBox(){
	//for now, just a html box that contains an embed code
		super.setWidget(processedHTML);
		super.setSize(sizeX, sizeY);
		
	}
	
	public void setEmbedCode(String rawEmbedd){
		this.rawEmbed=rawEmbedd;
		processedHTML.setHTML("");
			
		Log.info("processing rawEmbed"+rawEmbed);
		
		//detect if preserialised
		if (rawEmbedd.startsWith("SVB:")){
			loadFromSpiffyVideoCode(rawEmbed);
			return;
		}
		
		//detect if youtube
		if (isYouTubeEmbedCode(rawEmbed)){
	
			setAsYouTubeEmbed(rawEmbed);
			return;
			
		}
		//detect if blip
		if (isBlipTvEmbedCode(rawEmbed)){
			
			setAsBlipTVEmbed(rawEmbed);
			return;
		}
		
	}
	
	/**should return a special database-safe code to the current video **/
	public String getAsSpiffyVideoCode(){
		
		//first the safety checks
		if (isSafeURL(currentURL)!=true){
			Log.info("sanity check not passed on url");
			return null;
		}
		if (isSafeCode(currentID)!=true){
			Log.info("sanity check not passed on code");
			return null;			
		}
		
		String  SVBCode = "SVB:";
			
		switch (currentSource){
				case blip:
					SVBCode=SVBCode+currentSource.name()+","+currentURL+","+currentID;
				break;
				case youtube:
					SVBCode=SVBCode+currentSource.name()+","+currentURL+",na";					
				break;
		}
		
		return SVBCode;
		
	}
	
	public void loadFromSpiffyVideoCode(String spiffyVideoCode){
		
		processedHTML.setHTML("");
		//should start with SVB:
		if (!spiffyVideoCode.startsWith("SVB:")){
			Log.info("Not valid video code");
			return;
		}
		
		//split off bits
		spiffyVideoCode=spiffyVideoCode.substring(4);
		
		String type = spiffyVideoCode.split(",")[0];
		currentURL = spiffyVideoCode.split(",")[1];
		currentID = spiffyVideoCode.split(",")[3];
		
		currentSource = sourceType.valueOf(type);
		
		Log.info("type="+type);
		Log.info("currentURL="+currentURL);
		Log.info("currentID="+currentID);
		
		setUpVideo(currentSource, currentURL, currentID);
		
		
		
	}

	private boolean isSafeCode(String id) {
		//should not contain "'s or <>
		if (id.contains("\"")){
			return false;
		}
		if (id.contains(">")){
			return false;
		}
		if (id.contains("<")){
			return false;
		}
		if (id.contains("=")){
			return false;
		}
		if (id.contains(" ")){
			return false;
		}
		
		return true;
	}

	private boolean isSafeURL(String url) {
		return  UriUtils.isSafeUri(url);
	}

	private boolean isBlipTvEmbedCode(String rawEmbed) {
		
		rawEmbed = rawEmbed.toLowerCase().trim();
		
		if (rawEmbed.contains("src=\"http://blip.tv")){
			return true;
		}
		
		return false;
	}

	@Override
	public void clear(){
		processedHTML.setHTML("");
	}
	
	private boolean isYouTubeEmbedCode(String rawEmbed) {
		rawEmbed = rawEmbed.toLowerCase().trim();
		
		if (rawEmbed.contains("www.youtube.com")){
			return true;
		}
		
		return false;
	}

	private void setAsBlipTVEmbed(String embed) {
		embed = embed.trim();
		
		//extract the URL
		int startOfURL = embed.indexOf("src=\"")+5;
		String BlipURL = embed.substring(startOfURL);
		int endOfURL = BlipURL.indexOf("\"",startOfURL+1);
		BlipURL = BlipURL.substring(0, endOfURL);
		Log.info("url="+BlipURL);
		//ensure there's no other quotes
		if (BlipURL.contains("\"")){
			Log.info("error! url contains too many quotes");
			return;
		}
		currentURL=BlipURL;
	
		
		
		//extract the ID
		// " style="
		int startOfCode = embed.indexOf("src=\"http://a.blip.tv/api.swf#")+30;
		int endOfCode = embed.indexOf("\" style=",startOfCode);
		String BlipCode = embed.substring(startOfCode,endOfCode);
		currentID=BlipCode;
		Log.info("code="+currentID);
		
		currentSource=sourceType.blip;
		
		setUpVideo(currentSource, currentURL, BlipCode);
		
	}

	private void setUpVideo(sourceType currentSource2, String URL, String Code) {
		
		//sanity check URL
		if (!isSafeURL(URL)){
			return;
		}
		//sanity check code
		if (currentSource!=sourceType.youtube){
			if (!isSafeCode(Code)){
				return;
			}
		}
		String code="";
		if (currentSource==sourceType.blip){		
		code = BlipTVTemplate.replaceAll("___URLGOESHERE___", URL);
		code = code.replaceAll("___BlipCode___", Code);
		}
		
		if (currentSource==sourceType.youtube){	
		code = YoutubeTemplate.replaceAll("___URLGOESHERE___", URL);
		}
		
		//replace the size
		code = code.replaceAll("___sizeX___", sizeX);
		code = code.replaceAll("___sizeY___", sizeY);
		
		processedHTML.setHTML(code);
		
		
	}

	public void setSize(String x, String y){
		sizeX=x;
		sizeY=y;
	}
	
	private void setAsYouTubeEmbed(String embed) {
		
		embed = embed.trim();
		int startOfCode = embed.indexOf("src=\"")+5;
		int endOfCode = embed.indexOf("\"",startOfCode);
		String YouTubeURL =   embed.substring(startOfCode, endOfCode) ;
		
		//add http
		if (YouTubeURL.startsWith("www")){
			YouTubeURL="http://"+YouTubeURL;
			
		}
		
		currentURL=YouTubeURL;
		Log.info("url="+currentURL);
		
		currentSource=sourceType.youtube;

		setUpVideo(currentSource, currentURL, null);
		
	}

	private boolean isBlipTVURL(String url) {
		url = url.toLowerCase();
		
		if (url.startsWith("http://blip.tv/")){
			return true;
		}
		
		return false;
	}

	private boolean isYouTubeURL(String url) {
		
		url = url.toLowerCase();
		
		if (url.startsWith("http://www.youtube.com/")){
			return true;
		}
		if (url.startsWith("www.youtube.com/")){
			return true;
		}
		if (url.startsWith("http://youtu.be/")){
			return true;
		}
		
		
		return false;
	}
	

	public boolean isValidVideo() {
		
		if (isSafeURL(currentURL)!=true){
			Log.info("sanity check not passed on url");
			return false;
		}
		if (isSafeCode(currentID)!=true){
			Log.info("sanity check not passed on code");
			return false;			
		}
		 if (processedHTML.getHTML().length()>5){
			 return true;
		 }
		
		return false;
	}

}
