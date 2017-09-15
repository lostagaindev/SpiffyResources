package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * a class designed to catch events and forward them to a panel under it uses
 * the techique specified in;
 * http://www.vinylfox.com/forwarding-mouse-events-through-layers/ In future,
 * css3 might be used instead to make this whole thing more simple
 **/
public class SpiffyOverlay extends FocusPanel {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyOverlay");

	Element lastTarget = null;

	public SpiffyOverlay() {

		// set temp background
	//	this.getElement().getStyle().setBackgroundColor("blue");
	//	this.getElement().getStyle().setOpacity(0.5);

		this.setStyleName("defaultOverlayStyle");
		
		this.setSize("100%", "100%");

		sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU
				| Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONMOUSEMOVE);

	}

	public void setColor(String col) {
		this.getElement().getStyle().setBackgroundColor(col);
	}

	public void setOpacity(double opc) {
		this.getElement().getStyle().setOpacity(opc);
	}

	public void setCSS(String css) {
		Log.info("setting css to "+css);
		
		this.setStyleName(css);
	}

	public void setMoveOverEventsEnabled(boolean enabled) {
		
		Log.info("setting events ONMOUSEMOVE on/off");
		if (enabled) {

			sinkEvents(Event.ONMOUSEMOVE);

		} else {

			unsinkEvents(Event.ONMOUSEMOVE);

		}
	}

	public void setVisibility(boolean show) {
		if (!show) {
			
			this.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			
			// unsink events to speed stuff up
			unsinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK
					| Event.ONCONTEXTMENU | Event.ONCLICK | Event.ONMOUSEOVER
					| Event.ONMOUSEOUT | Event.ONMOUSEMOVE);

			
		} else {
			
			Log.info("setting to visible");
			
			this.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			
			sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU
					| Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
					| Event.ONMOUSEMOVE);

		}
	}

	// capture events
	@Override
	public void onBrowserEvent(Event event) {
//		Log.info("______________________onBrowserEvent from overlay");
		// event.cancelBubble(true);// This will stop the event from being
		// propagated
		DOM.eventCancelBubble(event, true);

		event.preventDefault();

		// switch (DOM.eventGetType(event)) {

		// case Event.ONCONTEXTMENU:

		//Log.info("________________________________________________________Type= "
		//		+ event.getType());

		int x = DOM.eventGetClientX(event);
		int y = DOM.eventGetClientY(event);

		Element clickedElement = DOM.eventGetCurrentTarget(event);

		// temporarily hide the appointment canvas
		// clickedElement.setAttribute("style", "visibility:hidden");
		clickedElement.getStyle().setVisibility(Visibility.HIDDEN);

		// use a native JavaScript method to find the element at the
		// doubleclick event
		Element elementBehind = getElementFromPoint(x, y);

		//Log.info("________________________________________________________restoring visibility ");

		// restore the appointment canvas
		clickedElement.getStyle().setVisibility(Visibility.VISIBLE);
	//	Log.info("________________________________________________________found element : "
	//			+ elementBehind.getParentElement().getOffsetWidth());

	//	Log.info("________________________________________________________firing event ");

		// if the new element is different to the last, then we have rollover
		// events to deal with
		if ((elementBehind != lastTarget) && (lastTarget != null)
				&& (elementBehind != null)) {
			// last element should have rollout fired

			NativeEvent rollout = Document.get().createMouseOutEvent(
					event.getTypeInt(), event.getScreenX(), event.getScreenY(),
					event.getClientX(), event.getClientY(), event.getCtrlKey(),
					event.getAltKey(), event.getShiftKey(), event.getMetaKey(),
					event.getButton(), lastTarget);

		//	Log.info("________________________________________________________firing rollout on "
		//			+ lastTarget.getOffsetWidth());

			lastTarget.dispatchEvent(rollout);
			lastTarget = null;
			// new element should have rolloverfired

			NativeEvent rollover = Document.get().createMouseOverEvent(
					event.getTypeInt(), event.getScreenX(), event.getScreenY(),
					event.getClientX(), event.getClientY(), event.getCtrlKey(),
					event.getAltKey(), event.getShiftKey(), event.getMetaKey(),
					event.getButton(), clickedElement);

		//	Log.info("________________________________________________________firing rollover on "
		//			+ clickedElement.getOffsetWidth());
			clickedElement.dispatchEvent(rollover);

		}
	//	Log.info("________________________________________________________firing event ");

		// / NativeEvent eventcopy = Document.get().createClickEvent(
		// / event.getTypeInt(), event.getScreenX(), event.getScreenY(),
		// event.getClientX(), event.getClientY(), event.getCtrlKey(),
		// event.getAltKey(), event.getShiftKey(), event.getMetaKey());
		//
		NativeEvent eventcopy = Document.get().createMouseEvent(
				event.getType(), true, true, event.getTypeInt(),
				event.getScreenX(), event.getScreenY(), event.getClientX(),
				event.getClientY(), event.getCtrlKey(), event.getAltKey(),
				event.getShiftKey(), event.getMetaKey(), event.getButton(),
				event.getRelatedTarget());

		if (elementBehind != null) {
			elementBehind.dispatchEvent(eventcopy);
			lastTarget = elementBehind;
		}
		// elementBehind.getParentElement()
		// .setTitle("test title from overlay");

		// }

		// hide this overlay
		// work out what it should have clicked
		// parse event to it
		// reshow this
	}

	private native Element getElementFromPoint(int x, int y) /*-{
		return $wnd.document.elementFromPoint(x, y);
	}-*/;

}
