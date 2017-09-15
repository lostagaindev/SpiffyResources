package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.Widget;

/**
 * A loadingclock implemented as a canvas pie-chart
 * 
 * @author darkflame
 *
 */
class CanvasLoadingClock implements LoadingIconVisualisation  {

	
	int width = 100; //used to be 70
	int height = 100;

	double ANG = -Math.PI / 2;

	Canvas canvasicon;// = Canvas.createIfSupported();
	CanvasElement maine;// = canvasicon.getCanvasElement();
	Context2d drawplane;// = maine.getContext2d();
	public CssColor strokecolor = CssColor.make(0, 0, 150);
	public CssColor fillcolor = CssColor.make(0, 0, 150);


	public CanvasLoadingClock(int width,int height) {
		super();

		canvasicon = Canvas.createIfSupported();
		maine = canvasicon.getCanvasElement();
		drawplane = maine.getContext2d();

		canvasicon.setWidth(width + "px");
		canvasicon.setHeight(height + "px");
		canvasicon.setCoordinateSpaceWidth(width);
		canvasicon.setCoordinateSpaceHeight(height);


	}

	@Override
	public void setPixelSize(int x,int y){
		width=x;
		height=y;

		canvasicon.setWidth(width + "px");
		canvasicon.setHeight(height + "px");
		canvasicon.setCoordinateSpaceWidth(width);
		canvasicon.setCoordinateSpaceHeight(height);

	}

	@Override
	public void reset() {
		drawplane.clearRect(0, 0, canvasicon.getCoordinateSpaceWidth(),
				canvasicon.getCoordinateSpaceHeight());

	}

	@Override
	public void updateToRatio(double ratioComplete) {
		
		//get angle from that ratio
		double angle = ratioComplete * (Math.PI * 2);
		updateToAngle(angle);
	
	}
	

	private void updateToAngle(double angle) {
		// TotalUnitsToLoad/current step
		//ANG =angle;

		drawplane.beginPath();

		drawplane.setFillStyle(fillcolor);
		drawplane.setStrokeStyle(strokecolor);

		drawplane.moveTo(width / 2, height / 2);
		drawplane.arc(width / 2, height / 2, width / 2, -Math.PI / 2, angle-(Math.PI / 2),
				false);
		drawplane.lineTo(width / 2, height / 2);
		// drawplane.closePath();
		drawplane.stroke();
		drawplane.fill();




	}

	@Override
	public Widget getWidget() {
		return canvasicon;
	}

	@Override
	public void setStrokeColor(CssColor colorFromCSS) {
		strokecolor=colorFromCSS;

	}

	@Override
	public void setFillColor(CssColor colorFromCSS) {
		fillcolor=colorFromCSS;
	}
}
