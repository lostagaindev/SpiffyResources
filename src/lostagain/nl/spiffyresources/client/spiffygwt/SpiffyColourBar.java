package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyColourFunctions;

/**
 * A flow panel containing lots of blocks of pretty colors to select
 * @author Tom
 *
 */
public class SpiffyColourBar extends FlowPanel {

	int ColourBoxHeight = 30;
	int ColourBoxWidth  = 30;
	ColourSelectedHandler ColourSelectedHandler = null;
	
	ColourBox currentlySelected = null;
	
	ArrayList<ColourBox> ColourBoxs = new ArrayList<ColourBox>();
	
	
	/**
	 * Any number of CSS Colours, each will make a box
	 * @param CSSColours
	 */
	public SpiffyColourBar(String... CSSColours){
		//100% by default
		super.setWidth("100%");
		super.setHeight("100%");
				
		for (String csscolour : CSSColours) {
			
			addNewColourBox(csscolour,null);
			
		}
		
	}
	
	
	
	
	/**
	 * Create colours via immutable map. Note; The KEYS are the colours the VALUES are the name
	 * @param tShirtColours
	 */
	public SpiffyColourBar(ImmutableMap<String, String> tShirtColours) {
		super.setWidth("100%");
		super.setHeight("100%");
				
		for (String csscolour : tShirtColours.keySet()) {
			
			addNewColourBox(csscolour,tShirtColours.get(csscolour));
			
			
		}
		
	}
	
	public class orderByHue implements Comparator<ColourBox> {	
		@Override
		public int compare(ColourBox arg0, ColourBox arg1) {
			
			String col1 = arg0.colour;
			String col2 = arg1.colour;
			
			int RGB[] = SpiffyColourFunctions.HexToRGB(col1);
			int RGB2[] = SpiffyColourFunctions.HexToRGB(col2);
			
			float HSL[]  = SpiffyColourFunctions.rgbToHsl(RGB[0], RGB[1], RGB[2]);
			float HSL2[] = SpiffyColourFunctions.rgbToHsl(RGB2[0], RGB2[1], RGB2[2]);
			
			int hue  = (int) (HSL[0]*360);
			int hue2 = (int) (HSL[1]*360);
			
			return hue-hue2;
		}
	}
	
	public void reOrderByHue(){
		
		Collections.sort(ColourBoxs, new orderByHue());
		
		super.clear();
		for (ColourBox box : ColourBoxs) {
			super.add(box);			
		}
		
		
	}
	
	private void addNewColourBox(String csscolour, String colourName) {
		
		ColourBox newBox = new ColourBox(csscolour,colourName,ColourBoxWidth,ColourBoxHeight);
		ColourBoxs.add(newBox);
		super.add(newBox);
	
	}



	public void setColourBoxSize(int w,int h){
		ColourBoxHeight = w;
		ColourBoxWidth  = h;
		for (ColourBox box : ColourBoxs) {
			box.setPixelSize(w, h);		
		}
	}
	
	public void addColourSelectedHandler(ColourSelectedHandler handler){
		ColourSelectedHandler = handler;
	}
	
	
	public interface ColourSelectedHandler {
		public void onSelected(String colourSelected);			
	}
	
	
	/**
	 * defaults to white if no color selected
	 * @return
	 */
	public String getCurrentColour() {	
		if (currentlySelected==null){
			return "#FFFFFF";
		}
		return currentlySelected.colour;
	}
	
	/**
	 * Return the name thats been associated with this colour value
	 * 
	 * (note; internally the colour name is the VALUE of a hashmap with the colourCSS as the NAME)
	 * @return
	 */
	public String getCurrentColourName() {
		
		if (currentlySelected==null){
			return "";
		}		
				
		return currentlySelected.colourname;
	}
	
	
	public void setToNamed(String string) {
		
	for (ColourBox box : ColourBoxs) {
			
			if (box.colourname.equalsIgnoreCase(string)){
				box.select(false);
			}
			
			
		}
	}



	
	
	class ColourBox extends FocusPanel implements ClickHandler {
		
		String colour;
		String colourname;
		
		public ColourBox(String colour,String colourname,int w, int h){
			this.colour = colour;
			this.colourname = colourname;
			
			super.setTitle(colourname);
			
			super.getElement().getStyle().setBackgroundColor(colour);
			super.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);			
			super.setPixelSize(w, h);				
			super.addClickHandler(this);
			
			
		}
		public void select(boolean fireHandler) {		
			this.setSelectedStyle();			
			if (currentlySelected!=null){
				currentlySelected.setUnselectedStyle();				
			}
			currentlySelected=this;
			
			if (ColourSelectedHandler!=null && fireHandler){
				ColourSelectedHandler.onSelected(colour);
			}
		}
		@Override
		public void onClick(ClickEvent event) {		
			 select(true); 
			 
		}
		private void setSelectedStyle() {
			
			super.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			super.getElement().getStyle().setBorderColor("#FF0000");			
			
		}
		
		private void setUnselectedStyle() {
			
			super.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			super.getElement().getStyle().setBorderColor("#0000FF");			
			
		}
		
	}




	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
