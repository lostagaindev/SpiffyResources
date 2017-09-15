package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/** a box for storing a side-by-side list of stats or data,
 * eg
 * Name -
Type -She Hulk Volume..
Genre -comic Action Comedy
**/
public class SpiffyDataBox extends FlexTable {

	
	Logger logger = Logger.getLogger("SpiffyGWT.SpiffyDataBox");
	String fcstyle =""; /** first col style **/
	String centralstyle =""; /** first col style **/
	
	public SpiffyDataBox(int X,int Y){
		super.setPixelSize(X, Y);
		
		
	}
	
	public SpiffyDataBox(){
		
	}
	
	/**
	 * same as addrow(widget)
	 */
	public void add(Widget widget){
		addrow(widget);
	}

	/** at a row with a label in each column filled with the following strings**/
	public void addrow(String name, String data){

		addrow(new Label(name),new Label(data));
		
	}
	/** at a row with a widget and then a  label **/
	public void addrow(Widget namew, String data){

		addrow(namew,new Label(data));
		
	}
	/** at a row with a label and then a  widget **/
	public void addrow(String name, Widget data){

		addrow(new Label(name),data);
		
	}
	
	public void setFirstColStyle(String style){
		this.fcstyle=style;
	}
	/** set the css style for any rows with only one widget **/
	public void setCenterColStyle(String style){
		this.centralstyle=style;
	}
	
	/** add a row with a single label centralized **/
	public void addrow(String name){
		addrow(new Label(name));
		
	}
	
	
	
	/** add a row with a single widget  centralized **/
	public void addrow(Widget widget){
		
		int rows = super.getRowCount();
		
		if (centralstyle.length()>2){
			widget.addStyleName(centralstyle);
			}
		
		super.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		super.setWidget(rows, 0, widget);
		super.getFlexCellFormatter().setColSpan(rows, 0, 2); //in future '2' should be the max number of columns
		
		
	}
	
	/** 
	 * At a row with a unlimited number of widgets - the first goes in column 0, the rest
	 * go vertically in column 1 
	 ***/
	public void addrow(Widget namew, Widget... widgets){
		int rows = super.getRowCount();
		 insertAtRow(rows,namew, widgets);
	}
	
	/** 
	 * insert a row at a existing location with a unlimited number of widgets - the first goes in column 0, the rest
	 * go vertically in column 1 
	 ***/
	public void insertrow(int before,Widget namew, Widget... widgets){
		int rownum = super.insertRow(before);		
		insertAtRow(rownum,namew, widgets);
	}
	
	/** 
	 * At a row with a unlimited number of widgets - the first goes in column 0, the rest
	 * go vertically in column 1 
	 * 
	 * @param rownum - the row to place these widgets at. If it doesn't exist, it will be created 
	 ***/
	private void insertAtRow(int rownum, Widget namew, Widget... widgets){
				
		//super.resizeRows(super.getRowCount()+1);
		//int rows = super.getRowCount();
		//add new data
		super.setWidget(rownum, 0, namew);
		if (fcstyle.length()>2){
			namew.addStyleName(fcstyle);
		}
		
		super.getCellFormatter().setHorizontalAlignment(rownum, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		super.getCellFormatter().setVerticalAlignment(rownum, 0, HasVerticalAlignment.ALIGN_TOP);
		VerticalPanel dataContainer = new VerticalPanel();
		
		//loop for each data widget ONLY IF THERE IS MORE THEN ONE OF THEM!
		if (widgets.length>1){
			for (Widget widgetn : widgets) {			
				dataContainer.add(widgetn);			
			}
			super.setWidget(rownum, 1, dataContainer);
		} else {
			super.setWidget(rownum, 1, widgets[0]);
		}
		
	
		super.getCellFormatter().setHorizontalAlignment(rownum, 1, HasHorizontalAlignment.ALIGN_LEFT);
		
		
	}

	public void addrow(Widget namew, ArrayList<Widget> wids){
		int rows = super.getRowCount();
//		super.resizeRows(super.getRowCount()+1);
		
		logger.info("filling widgets:"+wids.size());
		//add new data
		super.setWidget(rows, 0, namew);
		
		super.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		
		super.getCellFormatter().setVerticalAlignment(rows, 0, HasVerticalAlignment.ALIGN_TOP);
	
		VerticalPanel dataContainer = new VerticalPanel();
		
		logger.info("filling widgets");
		
		//loop for each data widget
		Iterator<Widget> widit = wids.iterator();
				
		while (widit.hasNext()) {
			
			Widget widgetn = (Widget) widit.next();
			dataContainer.add(widgetn);
			
		}
			
			
		
		super.setWidget(rows, 1, dataContainer);
		super.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		
		
	}
	
	/**
	 * Returns a ArrayList of ArrayLists with the all the retrievable text content of all the cells<br>
	 * <br>
	 * The outer ArrayList is an array of all the lines<br>
	 * The inner ArrayList is the contents of each specific line<br>
	 * <br>
	 * A specific line will have a empty string if no content was found, or the cell was not a TextBox or Label<br>
	 * <br>
	 * @return ArrayList<ArrayList<String>><br>
	 */
	public ArrayList<ArrayList<String>> getContentsAs2DArray(){
		
		ArrayList<ArrayList<String>> arrayOfAllLines = new ArrayList<ArrayList<String>>();
		
		int i =0;
		while (i<this.getRowCount()){
			
			ArrayList<String> specificLinesContents = new ArrayList<String>();
			arrayOfAllLines.add(specificLinesContents);
			
			int rowcount = this.getCellCount(i);
			int c = 0;
			
			while (c<rowcount){
				String stringofcell = "";
				
				Widget cellcontents = this.getWidget(i, c);
				
				//now we have the widget we get its cell contents as a string, assuming its a type with a string we can get!
				if (cellcontents.getClass().equals(Label.class)||cellcontents.getClass().equals(TextBox.class)){
					//if (HasText.class.isAssignableFrom(contents.getClass())){
					stringofcell = ((HasText)cellcontents).getText();
				} else if (cellcontents.getClass().equals(VerticalPanel.class)){
						logger.info("type is a vertical panel, so we do stuff here umm...when I get around to it");
						//loop here for each element in vertical panel
				} else {

						logger.info("not a label error");
						logger.info("type was:"+cellcontents.getClass());
				}
					
					c++;
				
				
				specificLinesContents.add(stringofcell);
			}
			
			i++;
		}
		
		
		return arrayOfAllLines;
		
	}
	
	/** 
	 * Returns an array of lines, each one being of the form 
	 * Col1,Col2,Col3
	 * This ONLY WORKS for Textboxs and Labels *
	 * If noCommaAtFirst is set to true there is no comma added to the first result.
	 * eg;
	 * Col1Col2,Col3
	 * */
	public String[] getLinesAsArray(Boolean noCommaAtFirst){
		
		String[] lines = new String[this.getRowCount()];
		
				
		int i =0;
		while (i<this.getRowCount()){
			
			int rowcount = this.getCellCount(i);
			int c = 0;
			String newline = "";
			while (c<rowcount){
				
				Widget contents = this.getWidget(i, c);
				
				if (contents.getClass().equals(Label.class)||contents.getClass().equals(TextBox.class)){
				//if (HasText.class.isAssignableFrom(contents.getClass())){
					if (c==0 && noCommaAtFirst){
						newline = newline + ((HasText)contents).getText();
					} else {
						newline = newline + ((HasText)contents).getText()+",";						
					}
					
				} else if (contents.getClass().equals(VerticalPanel.class)){
					logger.info("type is a vertical panel, so we do stuff here umm...when I get around to it");
					//loop here for each element in vertical panel
					
				} else {

					logger.info("not a label error");
					logger.info("type was:"+contents.getClass());
				}
				
				c++;
			}
			
			//remove any commas at the end
			newline=newline.trim();
			if (newline.endsWith(",")){				
				newline=newline.substring(0,newline.length()-1);				
			}
			
			
			lines[i] = newline;
			i++;
		
		
		}
		
		return lines;
	}

	
	/** Replaces the contents of a row with stuff!
	 * 
	 * @param rownumber - row # to replace
	 * @param namew - widget for row 0
	 * @param widgets - any number of any widget you like to insert. They will be combined as a vertical panel and insert at col1 
	 */
	public void editrow(int rownumber, Widget namew, Widget... widgets) {
		
		insertAtRow(rownumber,  namew, widgets);
		
		
	}
	/** Replaces the contents of a row (rownumber) with the specified widget followed be a string
	 * which will automatically be made into a label
	 * 
	 * @param rownumber - what row # to replace
	 * @param name -  string to make a label from to insert at col 0
	 * @param data - string to make a label from to insert at col 1
	 */
	public void editrow(int rownumber,String name, String data){

		insertAtRow(rownumber,new Label(name),new Label(data));
		
	}
	
	
	/** Replaces the contents of a row (rownumber) with the specified widget followed be a string
	 * which will automaticaly be made into a label
	 * 
	 * @param rownumber - what row # to replace
	 * @param namew - widget to insert at col 0
	 * @param data - string to make a label from to insert at col 1
	 */
	public void editrow(int rownumber,Widget namew, String data){

		insertAtRow(rownumber,namew,new Label(data));
		
	}

	
	/**loops over the first column to find the row containing the specified widget
	 * -1 if not found 
	 * @param ml - the widget to search for
	 * @return - the row number of the widget or -1 if not found
	 */
	public int getRowContaining(Widget ml) {
		
		int i =0;
		while (i<this.getRowCount()){
			Widget test = this.getWidget(i, 0);
			if (test==ml){
				return i;
			}
			i++;
			
		}		
		
		return -1;
	}
}
