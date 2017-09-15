package lostagain.nl.spiffyresources.client;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.SimplePanel;

public class SpiffyLoadingIconold extends SimplePanel {

	Canvas icon;
	int width = 250;
	int height = 250;
	
	public SpiffyLoadingIconold(){
		
		//icon = Canvas.createIfSupported();
		if (icon==null){
			return;
		}
		this.setSize(width+"px", height+"px");
		icon.setWidth(width + "px");
		icon.setHeight(height + "px");
		icon.setCoordinateSpaceWidth(width);
		icon.setCoordinateSpaceHeight(height);
		
	//	Context2d drawplane = icon.getContext2d();
		
		//icon.setFillStyle( Color.BLUE );		
		icon.getContext2d().beginPath();
		icon.getContext2d().arc(width/2, height/2, width/2, 0, Math.PI * 2.0, true);
		icon.getContext2d().closePath();
		icon.getContext2d().fill();
		
		super.setElement(icon.getElement());
		
	}
}
