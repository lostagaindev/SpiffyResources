package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * will displays images proportionately in sequence
 * @author darkflame
 *
 */
public class ImageLoadingClock implements LoadingIconVisualisation {

	AbstractImagePrototype[] frames;
	
	Image internalImage = new Image();
	int CurrentFrame=0;
	int totalImages = 0;
	
	public ImageLoadingClock(AbstractImagePrototype[] SetFrames) {
		super();
		frames=SetFrames;
		 totalImages = frames.length;
			frames[0].applyTo(internalImage);
			
	}
	
	
	@Override
	public void updateToRatio(double ratioComplete) {
		CurrentFrame = (int) Math.floor((totalImages-1) * ratioComplete);
		frames[CurrentFrame].applyTo(internalImage);
		
		
	}

	@Override
	public void setPixelSize(int x, int y) {
		internalImage.setPixelSize(x, y);
		
	}

	@Override
	public void reset() {
		CurrentFrame=0;
	}


	@Override
	public Widget getWidget() {
		return internalImage;
	}

	
	//not used:
	@Override
	public void setStrokeColor(CssColor make) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFillColor(CssColor colorFromCSS) {
		// TODO Auto-generated method stub

	}


}
