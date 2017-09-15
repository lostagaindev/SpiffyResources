package lostagain.nl.spiffyresources.interfaces;

import com.google.gwt.user.client.ui.IsWidget;

public interface IsSpiffyGenericLogBox extends IsWidget, IsSpiffyGenericLogger {

		//public Element getElement();
		public void setPixelSize(int i, int j);
		public void addControl(IsWidget control);
		public void addWidgetToList(IsWidget addThis);
		/**
		 * if you want to distinguish between a few logboxs, you can implement changing the background colour
		 * @param string
		 */
		public void setBackgroundColour(String string);
		public void clearAddedControlls();
		
		
		
		
	

}
