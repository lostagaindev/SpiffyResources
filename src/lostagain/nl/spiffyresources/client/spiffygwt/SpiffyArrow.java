package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.SimplePanel;


public class SpiffyArrow extends SimplePanel {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyArrow");
	//canvas
	Canvas canvas = Canvas.createIfSupported();
	Context2d drawplane;
	
	int ArrowSizeX = 100;
	int ArrowSizeY = 100;
	
	int Angle = 315;
	
	public SpiffyArrow(int SizeX, int SizeY){

		if (canvas==null){
			Log.info("no canvas supported for clock");
			return;
		}		
		
		
		//make a basic arrow pointing to the top left by default
		
		//set size
		ArrowSizeX= SizeX;
		ArrowSizeY= SizeY;	
		
		
		this.setSize(ArrowSizeX+"px", ArrowSizeY+"px");
		
		canvas.setWidth((ArrowSizeX) + "px");
		canvas.setCoordinateSpaceWidth(ArrowSizeX);
		canvas.setHeight((ArrowSizeY) + "px");      
		canvas.setCoordinateSpaceHeight(ArrowSizeY);
		
		this.setWidget(canvas);
		
		
		drawplane = canvas.getContext2d();
		
		//draw line from center to topleft

		//drawplane.setBackgroundColor(Canvas.TRANSPARENT);
		drawplane.beginPath();
		drawplane.setStrokeStyle("#900");
		drawplane.moveTo(ArrowSizeX, ArrowSizeY);
		drawplane.setLineWidth(3);
		drawplane.lineTo(0,0);
		drawplane.lineTo(12,0);
		drawplane.moveTo(0,0);
		drawplane.lineTo(0,12);
		
		drawplane.stroke();
		
	}
   public void SetArrowTopLeft(){
	   drawplane.clearRect(0, 0, ArrowSizeX, ArrowSizeY);
	   
	drawplane.beginPath();
	drawplane.setStrokeStyle("#900");
	drawplane.moveTo(ArrowSizeX, ArrowSizeY);
	drawplane.setLineWidth(3);
	drawplane.lineTo(0,0);
	drawplane.lineTo(12,0);
	drawplane.moveTo(0,0);
	drawplane.lineTo(0,12);
	
	drawplane.stroke();
	}
   
   
   public void SetArrowTopRight(){
	   
	   drawplane.clearRect(0, 0, ArrowSizeX, ArrowSizeY);
	   
		drawplane.beginPath();
		drawplane.setStrokeStyle("#900");
		drawplane.moveTo(0, ArrowSizeY);
		drawplane.setLineWidth(3);
		drawplane.lineTo(ArrowSizeX,0);
		drawplane.lineTo(ArrowSizeX-12,0);
		drawplane.moveTo(ArrowSizeX,0);
		drawplane.lineTo(ArrowSizeX,12);
		
		drawplane.stroke();
		}
   
   /*
	public void SetArrowAngle(int Degrees){
		
	}
	public void drawArrowHead(boolean state){
		
	}
	public void setThickness(int pixels){
		
	}
	*/

}
