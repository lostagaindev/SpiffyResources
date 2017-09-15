package lostagain.nl.spiffyresources.client;

import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.Context2d.Repetition;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.widgetideas.graphics.client.Color;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyFunctions;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyFunctions.color_tripple;

/** All sorts of usefull image generation/manipulation utility's **/
public class SpiffyImageUtilitys {	

	static Logger Log = Logger.getLogger("SpiffyImageUtilitys");
	
	
	//data urls are magic!
	public static String smallArrowDataURL = "data:image/png;base64,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";
	public static String SmallArrowWithPlusURL = "data:image/png;base64,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";
	public static Image SmallArrow = new Image(smallArrowDataURL);
	public static Image SmallArrowWithPlus = new Image(SmallArrowWithPlusURL);
	
	public static String getSmallArrowDataURL() {
		return smallArrowDataURL;
	}
	public static String getSmallArrowWithPlusDataURL() {
		return SmallArrowWithPlusURL;
	}

	
	
	/** 
	 * converts the image to a image url.
	 * Both images must be already loaded and somewhere attached to the dom 
	 * **/	
	public static String getDataURLFromImage(Image image, int sizeX,int sizeY, Image optionalAdditionalImage) {

		Canvas canvas = Canvas.createIfSupported();

		//if no canvas cancel
		if (canvas==null){
			Log.info("no canvas supported");
			return null;
		}		
		
		canvas.setWidth((sizeX/2) + "px");
		canvas.setCoordinateSpaceWidth(sizeX);
		canvas.setHeight((sizeY/2) + "px");      
		canvas.setCoordinateSpaceHeight(sizeY);
				
		//get the drawingplane in the canvas element		
		Context2d drawplane = canvas.getContext2d();
		
		//dump the image on it
		ImageElement imageContent = ImageElement.as(image.getElement());
		if (imageContent!=null){	
			//if the image content exists, then we add it to the canvas and scale at the same time
			drawplane.drawImage(imageContent, 0,0,sizeX,sizeY);	
			
			if (optionalAdditionalImage!=null){
				ImageElement additionalImagemageContent = ImageElement.as(optionalAdditionalImage.getElement());
				if (additionalImagemageContent!=null){
					//we add the additional image
					Log.info("adding additional image");
					
					//if its bigger, resize
					if ((additionalImagemageContent.getClientHeight()> sizeY)||(additionalImagemageContent.getClientWidth()>sizeX)){
						drawplane.drawImage(additionalImagemageContent, 0,0,sizeX,sizeY);	
					} else {
						drawplane.drawImage(additionalImagemageContent, 0,0);	
					}
					
				}
			}
			
			
		} else {
			Log.info("can not get source image data....is it attached to the dom and loaded?");
			return null;
		}
		
		//get value
		String dataurl = canvas.toDataUrl("image/png");
								
		//cleanup after ourselves?
		
		
		//return data url
		return dataurl;
	}
	
	
	public static String getDataURLFromString(String text,int w ,int h ){
		
		//Log.info("drawing lettershapes w="+w+" h="+h);
		Canvas canvas = Canvas.createIfSupported();

		//if no canvas cancel
		if (canvas==null){
			Log.info("no canvas supported");
			return null;
		}	

		canvas.setPixelSize(w, h);
		canvas.setCoordinateSpaceHeight(h);
		canvas.setCoordinateSpaceWidth(w);
		
		Context2d context = canvas.getContext2d();
			
		
		
		if (text.length()<14){
			text=text+text.toUpperCase();
		}
		
		
		byte letters[] = text.getBytes();
				
		
		if (letters.length>3){
			
			for (int i = 0; (i+3) <= letters.length; i=i+3) {
						
				byte raw = letters[i];			
				int aa = getCroppedCharValue(raw);
				double a = getPreportionateValue(aa,70,100);
				//50,50
				
				//int a=i;
				
				// area is divided into a 10x10 grid
				int gridXsize = 10;
				int gridYsize = 10;
				
				//get letter value as position in grid
				int yPos = (int) Math.floor(a/gridXsize);
				int xPos = (int) (a%gridXsize);
				
				//Log.info("a = "+a+" xPos "+xPos+" yPos "+yPos);
				
				//(note, these values need multiplying to scale it to the grid
				int corner1x = xPos * (w/gridXsize);
				int corner1y = yPos * (h/gridYsize);

				
				//Log.info("corner1x "+corner1x+" corner1y "+corner1y);
				
				context.setFillStyle("blue");						
				context.setGlobalAlpha(0.5);		
			//	context.fillRect(corner1x, corner1y, 15,15);

				 raw = letters[i+1];			
				 aa = getCroppedCharValue(raw);
				 a = getPreportionateValue(aa,70,100);
				//get letter value as position in grid
				 yPos = (int) Math.floor(a/gridXsize);
				 xPos = (int) (a%gridXsize);
				
				//Log.info("a = "+a+" xPos "+xPos+" yPos "+yPos);
				
				//(note, these values need multiplying to scale it to the grid
				int corner2x = xPos * (w/gridXsize);
				int corner2y = yPos * (h/gridYsize);

				
				//Log.info("corner1x "+corner1x+" corner1y "+corner1y);
							
				context.setGlobalAlpha(0.9);		
				//context.fillRect(corner2x, corner2y, 5,5);
				
				 raw = letters[i+2];			
				 aa = getCroppedCharValue(raw);
				 a = getPreportionateValue(aa,70,100);
				//get letter value as position in grid
				 yPos = (int) Math.floor(a/gridXsize);
				 xPos = (int) (a%gridXsize);
				
				//Log.info("a = "+a+" xPos "+xPos+" yPos "+yPos);
				
				//(note, these values need multiplying to scale it to the grid
				int corner3x = xPos * (w/gridXsize);
				int corner3y = yPos * (h/gridYsize);

				
				//Log.info("corner1x "+corner1x+" corner1y "+corner1y);

				byte d = letters[i+3];			
				int hue= getCroppedCharValue(d);
				
				float color = (float) getPreportionateValue(hue,70,100);
				
				//Log.info(" HSL= " + color+","+85+","+55);
				
				color_tripple RGB_data = SpiffyFunctions.HSL_2_RGB(
						color, 85,
						55);
				
			//	Log.info(" R " + Integer.toHexString(RGB_data.v1));
			//	Log.info(" G " + Integer.toHexString(RGB_data.v2));
			//	Log.info(" B " + Integer.toHexString(RGB_data.vH));

				String Hex_Red = Integer.toHexString(RGB_data.v1);
				if (Hex_Red.length() == 1) {
					Hex_Red = "0" + Hex_Red;
				}
				String Hex_Green = Integer.toHexString(RGB_data.v2);
				if (Hex_Green.length() == 1) {
					Hex_Green = "0" + Hex_Green;
				}
				String Hex_Blue = Integer.toHexString(RGB_data.v3);
				if (Hex_Blue.length() == 1) {
					Hex_Blue = "0" + Hex_Blue;
				}

				String HexColour = new String("#" + Hex_Red + Hex_Green + Hex_Blue);
								
			//	Log.info("HexColour="+HexColour);
								
				context.setFillStyle(HexColour);
									
				context.setGlobalAlpha(0.6);		
			//	context.fillRect(corner3x, corner3y, 15,15);
				
			//	Log.info("("+corner1x+","+corner1y+") - ("+corner2x+","+corner2y+") - ("+corner3x+","+corner3y+")");
				drawTriangle(context, new Point(corner1x,corner1y),new Point(corner2x,corner2y),new Point(corner3x,corner3y));
			
				
				
			}
			

			
		}
		

		makeVerticalSymetrical(canvas);
		makeHorizontalSymetrical(canvas);
		

		
		//get value
		String dataurl = canvas.toDataUrl("image/png");
								
		//cleanup after ourselves?
		return dataurl;
	}
	
	
	
	/** creates a paturn from the text (WIP) **/
	public static String getDataURLFromString_old(String imageDATA, int sizeX,int sizeY) {
		
		Canvas canvas = Canvas.createIfSupported();

		//if no canvas cancel
		if (canvas==null){
			Log.info("no canvas supported");
			return null;
		}		
		
		canvas.setWidth((sizeX/2) + "px");
		canvas.setCoordinateSpaceWidth(sizeX);
		canvas.setHeight((sizeY/2) + "px");      
		canvas.setCoordinateSpaceHeight(sizeY);
				
		//get the drawing plane in the canvas element		
		Context2d drawplane = canvas.getContext2d();
		
		//dump the image on it
		drawplane.setFillStyle("rgb(121, 121, 255)");
		drawplane.fillRect(0, 0, sizeX, sizeY);		
		
		//get text as pattern (first letter color? second letter length?)
		int TotalLetters = imageDATA.length();
		//imageDATA = ".,-1234567890()ABCDZ~+=-*";
		
		Log.info("making image from"+imageDATA);
		
		byte letters[] = imageDATA.getBytes();
				
		
		if (letters.length>3){
			
			int i=0;
			for (int j = 0; (i+4) < letters.length; i=i+4) {
				
				//crop character number to smaller range
				byte a = letters[i];			
				int val= getCroppedCharValue(a);
			//	Log.info("letters[0] v "+val);	
				double xdis = getPreportionateValue(val,70,sizeX);
				
				byte b = letters[i+1];			
				int val2= getCroppedCharValue(b);
				double ydis = getPreportionateValue(val2,70,sizeY);
			//	Log.info("letters[1] v "+val2);

				byte c = letters[i+2];			
				int val3= getCroppedCharValue(c);
				double size = getPreportionateValue(val3,70,(sizeX/2));
			//	Log.info("letters[2] v "+val3);
				

				byte d = letters[i+3];			
				int val4= getCroppedCharValue(d);
				float color = (float) getPreportionateValue(val4,70,100);
			//	Log.info("letters[3] color= "+color);

				color_tripple RGB_data = SpiffyFunctions.HSL_2_RGB(
						(color / 4) + 5, 85,
						55);
				
				System.out.println(" R " + Integer.toHexString(RGB_data.v1));
				System.out.println(" G " + Integer.toHexString(RGB_data.v2));
				System.out.println(" B " + Integer.toHexString(RGB_data.v3));

				String Hex_Red = Integer.toHexString(RGB_data.v1);
				if (Hex_Red.length() == 1) {
					Hex_Red = "0" + Hex_Red;
				}
				String Hex_Green = Integer.toHexString(RGB_data.v2);
				if (Hex_Green.length() == 1) {
					Hex_Green = "0" + Hex_Green;
				}
				String Hex_Blue = Integer.toHexString(RGB_data.v3);
				if (Hex_Blue.length() == 1) {
					Hex_Blue = "0" + Hex_Blue;
				}

				String HexColour = new String("#" + Hex_Red + Hex_Green + Hex_Blue);
								
				//Log.info("x="+xdis+" y="+ydis+" size="+size+"  HexColour="+HexColour);
								
				drawplane.setFillStyle(HexColour);
				
				
				drawplane.setGlobalAlpha(0.3);			
				
				drawplane.fillRect(xdis, ydis, xdis+size,ydis+size);
				
			}
			
			
		
		}
		
		
		//get value
		String dataurl = canvas.toDataUrl("image/png");
								
		//cleanup after ourselves?
		return dataurl;
		
	}


	//range of 65 to 122 for azAZ
	private static int getCroppedCharValue(int val) {
		val=val-65;
		
		if (val>70){
			val=70;
		}			
		if (val<0){
			val=0;
		}	
		
		return val;
	}

	/** makes horizontal symmetrical by mirroring **/
	public static void makeHorizontalSymetrical(Canvas canvas){
		
		
		int h=canvas.getCoordinateSpaceHeight();
		int w=canvas.getCoordinateSpaceWidth();
		
		Context2d context2D = canvas.getContext2d();

		context2D.setFillStyle("red");
		context2D.fillRect(0,0, -11, 11);
		//Log.info("making semetrical h=");
				
		ImageData dat = context2D.getImageData(0, 0,(w/2),h);
		
		Canvas tempcanvas = Canvas.createIfSupported();
		Context2d tempcontext = tempcanvas.getContext2d();
		tempcanvas.setPixelSize((w/2), h);
		tempcanvas.setCoordinateSpaceHeight(h);
		tempcanvas.setCoordinateSpaceWidth((w/2));
		
		tempcontext.fillRect(0, 0, (w/2), h);

		tempcontext.putImageData(dat, 0, 0);
		 // translate context to center of canvas
		
		
        // flip context horizontally
		context2D.save();
		context2D.scale(-1, 1);		

		
			
		context2D.clearRect(-tempcanvas.getCoordinateSpaceWidth(),0, -tempcanvas.getCoordinateSpaceWidth(), tempcanvas.getCoordinateSpaceHeight());		
		context2D.drawImage(tempcanvas.getCanvasElement(), -tempcanvas.getCoordinateSpaceWidth()*2, 0);
	
		context2D.restore();
		
	}

	/** makes Vertical symmetrical by mirroring **/
	public static void makeVerticalSymetrical(Canvas canvas){
		
		
		int h=canvas.getCoordinateSpaceHeight();
		int w=canvas.getCoordinateSpaceWidth();
		
		Context2d context2D = canvas.getContext2d();

		context2D.setFillStyle("red");
		context2D.fillRect(0,0, -11, 11);
		
		ImageData dat = context2D.getImageData(0, 0,w,(h/2));
		
		Canvas tempcanvas = Canvas.createIfSupported();
		Context2d tempcontext = tempcanvas.getContext2d();
		tempcanvas.setPixelSize(w, (h/2));
		tempcanvas.setCoordinateSpaceHeight((h/2));
		tempcanvas.setCoordinateSpaceWidth(w);
		
		tempcontext.fillRect(0, 0, w, (h/2));

		tempcontext.putImageData(dat, 0, 0);
		 // translate context to center of canvas
		
		
        // flip context horizontally
		context2D.save();
		context2D.scale(1, -1);		

		
			
		context2D.clearRect(0,-tempcanvas.getCoordinateSpaceHeight(), tempcanvas.getCoordinateSpaceWidth(), -tempcanvas.getCoordinateSpaceHeight());		
		context2D.drawImage(tempcanvas.getCanvasElement(),0, -tempcanvas.getCoordinateSpaceHeight()*2);
	
	
		
		context2D.restore();
		
	}
	

	public static void drawTriangle(Context2d context2D,Point c1,Point c2,Point c3) {
		
		context2D.beginPath();
		
		context2D.moveTo(c1.getX(), c1.getY());
		context2D.lineTo(c2.getX(), c2.getY());
		context2D.lineTo(c3.getX(), c3.getY());
		context2D.fill();		
		context2D.closePath();
		
		
	}
	
	
	/**
	 * Scales the input value to the given range between the input and output values
	 * @param inputValue
	 * @param maxinput
	 * @param maxoutput
	 * @return
	 */
	private static double getPreportionateValue(double inputValue, double maxinput, double maxoutput) {
		
		double ratio = maxoutput/maxinput;

		//Log.info("maxoutput="+maxoutput+" maxinput="+maxinput+" ratio="+ratio);
		
		return inputValue*ratio;
	}

}
