package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.interfaces.IsSpiffyGenericLogBox;




/** simple log management when we want to separate out something from the general log**/
public class SpiffyLogBox extends SimplePanel implements IsSpiffyGenericLogBox {

	VerticalPanel Overall = new VerticalPanel();
	VerticalPanel loglist = new VerticalPanel();
	ScrollPanel logScroller = new ScrollPanel();
	HorizontalPanel controls = new HorizontalPanel();
	Button clear = new Button("clear");
	
	long TimeStart = 0;
	

	
	/**
	 * Returns a logbox if you pass true. Else it will return a dummy one
	 * @return
	*/
	static public IsSpiffyGenericLogBox createLogBox(boolean mode){
		if (mode){
			return new SpiffyLogBox();
		}else {
			return new SpiffyDummyLogBox();
		}
		
	}
	 
	
	/** Does absolutely nothing! '
	 * used when the false is passed to the create statement, so no logging happens **/
	static class SpiffyDummyLogBox implements IsSpiffyGenericLogBox {

		SimplePanel  dummydiv = new SimplePanel();
		@Override
		public void log(String logthis) {
			
		}

		@Override
		public void error(String logthis) {
			
		}

		@Override
		public void info(String logthis) {
			
		}

		@Override
		public void log(String logthis, String color) {
			
		}

		@Override
	public Widget asWidget() {
			return dummydiv;
		}

	//	@Override
	//	public Element getElement() {
	//		return dummydiv.getElement();
	//	}

		@Override
		public void setPixelSize(int i, int j) {
			dummydiv.setPixelSize(i, j);			
			
		}

		@Override
		public void addControl(IsWidget button) {
			
		}

		@Override
		public void addWidgetToList(IsWidget addThis) {
			
		}

		@Override
		public void settimer() {
			
		}

		@Override
		public void logTimer(String lab) {
			
		}

		@Override
		public void setBackgroundColour(String string) {
			
		}

		@Override
		public void clearAddedControlls() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void log(double contents) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * use ""createLogBox(true)"" instead
	 */
	private SpiffyLogBox(){
		
		controls.add(clear);
		
		Overall.add(controls);
		Overall.add(logScroller);
		
		logScroller.setSize("100%", "100%");
		logScroller.add(loglist);
		loglist.getElement().getStyle().setWhiteSpace(WhiteSpace.PRE);
		
		this.add(Overall);
		
		clear.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				loglist.clear();
			}
		});
		
	}
	
	public void addControl(IsWidget addThis){
		
		controls.add(addThis);
		
	}
	
	@Override
	public void setPixelSize(int w,int h){
				
		super.setPixelSize(w, h);
		logScroller.setPixelSize(w, h-controls.getOffsetHeight());
		
		
	}
	

	public void logTimer(String label){
		logEntry newEntry = new logEntry(label,true); //the true just ensures it adds the timer at the end
		loglist.add(newEntry);
		logScroller.scrollToBottom();
	}
	public void log(String logThis){
		logEntry newEntry = new logEntry(logThis);
		loglist.add(newEntry);
		logScroller.scrollToBottom();
	}
	public void info(String logThis){
		logEntry newEntry = new logEntry(logThis);
		
		loglist.add(newEntry);
		logScroller.scrollToBottom();
	}
	public void log(String logThis,String color){
		logEntry newEntry = new logEntry(logThis,color);
		loglist.add(newEntry);
		logScroller.scrollToBottom();
	}
	public void error(String logThis){
		logEntry newEntry = new logEntry(logThis,"#EE0000");
		
		loglist.add(newEntry);
		logScroller.scrollToBottom();
	}
	
	/**
	 * logs a number to 15 decimal digits
	 */
	@Override
	public void log(double number) {
		
		NumberFormat nf = NumberFormat.getDecimalFormat();		
	    nf.overrideFractionDigits(15);	    
		
		Label newEntry = new Label(nf.format(number));
		newEntry.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		newEntry.setWidth("400px");
		
		
		loglist.add(newEntry);
		logScroller.scrollToBottom();
		

	}

	
	public void addWidgetToList(IsWidget addThis){
		
		loglist.add(addThis);
		
	}
	
	class logEntry extends HorizontalPanel {
		
		Label contentLabel = new Label("");
		
		public logEntry(String contents) {			
			contentLabel.setText(contents);		
			this.add(contentLabel);
		}
		public logEntry(String contents,String color) {			
			contentLabel.setText(contents);		
			contentLabel.getElement().getStyle().setColor(color);
			this.add(contentLabel);
		}
		
		public String getContents(){
			return contentLabel.getText();
		}
		
		public logEntry(String label,boolean timermode) {	
			
			long timer=(System.currentTimeMillis()-TimeStart);
			
			contentLabel.setText(label+":"+timer);		
			this.add(contentLabel);
		}
		
	}

	
	/**
	 * sets the timer to zero
	 */	
	@Override
	public void settimer() {
		TimeStart = System.currentTimeMillis();
	}

	@Override
	public void setBackgroundColour(String string) {
		super.getElement().getStyle().setBackgroundColor("white");	
	}

	@Override
	public void clearAddedControlls() {
		
		controls.clear();
		controls.add(clear); //just to be clear: we are re-adding the clear button here. Got nothing to do with clearing the container on the line above.
		
	}




	
}
