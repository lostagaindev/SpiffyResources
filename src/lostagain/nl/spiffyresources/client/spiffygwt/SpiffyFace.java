package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyFunctions;

//import gwt.canvas.client.Canvas;

import com.google.gwt.widgetideas.graphics.client.Color;

public class SpiffyFace extends SimplePanel{

	//main canvas
	GWTCanvas faceplane = new GWTCanvas();
	 
	
	//0 = sad
	//100 = happy
	byte Happyness = 0;
	int sizeX,sizeY = 0;
	
	public SpiffyFace(int setsizeX, int setsizeY){
				
		sizeX=setsizeX;
		sizeY=setsizeY;
		
		this.add(faceplane);
		this.setPixelSize(sizeX, sizeY);
		
		faceplane.setPixelSize(sizeX, sizeY);
		faceplane.setCoordSize(sizeX, sizeY);
		
		faceplane.setLineWidth(2);
		faceplane.setBackgroundColor(GWTCanvas.TRANSPARENT);
		faceplane.setStrokeStyle(Color.BLACK);	
		//draw eyes and nose
		//drawface(sizeX, sizeY);
		
		
		
	}
	
	/**sets the happyness.<br>
	 * remember to downcast into a byte<br>
	 * eg<br>
	 * .sethappyness((byte)50) <br>**/
	public void sethappyness (byte happy){		
		
		Happyness= (byte)(happy-14); //this is picked so 50 is nutral		
		faceplane.setLineWidth(2);
		faceplane.clear();
		faceplane.setBackgroundColor(GWTCanvas.TRANSPARENT);
		faceplane.setStrokeStyle(Color.BLACK);
		
	    drawface(sizeX, sizeY);
	}

	public void drawface(int sizeX, int sizeY) {
		faceplane.beginPath();
		
		
		
		//draw eyes
		faceplane.moveTo(sizeX/3, sizeY/3);
		faceplane.lineTo(1+sizeX/3, 1+sizeY/3);
		faceplane.moveTo((sizeX/3)*2, sizeY/3);
		faceplane.lineTo(1+(sizeX/3)*2, 1+sizeY/3);
		
		/*
		Window.alert("drawing from "+(sizeX/3)+" to "+(1+sizeX/3));
		
		if (true){
			faceplane.stroke();
			return;
		}*/
		
		faceplane.moveTo(0, sizeY/2);
		int ang = 0;
		int x,y=0;
		x = (int)(Math.sin(Math.toRadians(ang))*(faceplane.getOffsetWidth()/2.1))+(faceplane.getOffsetWidth()/2);
		y =  (int)(Math.cos(Math.toRadians(ang))*(faceplane.getOffsetHeight()/2.1))+ (faceplane.getOffsetHeight()/2);			
		faceplane.moveTo(x,y);
		/*
		while (ang <= 360){
			
			x = (int)(Math.sin(Math.toRadians(ang))*(faceplane.getWidth()/2.1))+(faceplane.getWidth()/2);
			y =  (int)(Math.cos(Math.toRadians(ang))*(faceplane.getHeight()/2.1))+ (faceplane.getHeight()/2);
			
			faceplane.lineTo( x,y);
			
			//System.out.print("-----"+x+","+y);
			ang=ang+5;
			
		} 
		*/
		// New! Precisely set colour using Dom
		SpiffyFunctions.color_tripple RGB_data = new SpiffyFunctions.color_tripple(0, 0, 0);
		RGB_data = SpiffyFunctions.HSL_2_RGB(
				(Happyness / 4) + 5, 85,
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
		
		faceplane.setFillStyle(new Color(HexColour));
		faceplane.fill();

		faceplane.moveTo(sizeX/2, (sizeY/2)-6);
		faceplane.lineTo((sizeX/2)+5, (sizeY/2)+2);
			
		//now the mouth
		faceplane.moveTo((sizeX/2)-10, (sizeY/2)+(15-(Happyness/9)));
		int cmouth = (int) ((sizeY/2)+(double)(Happyness/3));
		System.out.print("\n"+cmouth);
		
		faceplane.quadraticCurveTo((sizeX/2), cmouth, (sizeX/2)+10, (sizeY/2)+(15-(Happyness/9)));
		
		
		faceplane.stroke();
		
		
	}
}
