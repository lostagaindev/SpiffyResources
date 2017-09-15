package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Widget;

interface LoadingIconVisualisation {

	

	void setPixelSize(int x, int y);

	void reset();


	void updateToRatio(double ratioComplete);

	Widget getWidget();

	void setStrokeColor(CssColor make);

	
	void setFillColor(CssColor colorFromCSS);

	

	
}