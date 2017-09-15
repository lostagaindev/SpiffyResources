package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.user.client.ui.Label;

public class SpiffyAutoField extends SpiffyTextField {

	
	Label fieldLabel = new Label();
	
	/**  This is like a spiffy text field, only for data not entered by the user directly **/
	public SpiffyAutoField(String StartingText){
		
		//First we replace the suggest field with a rich text box
		int pos = this.getWidgetIndex(suggestField);
		this.remove(suggestField);
		this.insert(fieldLabel, pos);
		fieldLabel.setText(StartingText);
		fieldLabel.setWidth("100%");
		fieldLabel.setHeight("100%");
		
		
	}
	@Override
	public void setText(String string){
		fieldLabel.setText(string);
	}
	@Override
	public String getText(){
		return fieldLabel.getText();
	}
	
	@Override
	public void setStyleName(String style){
		fieldLabel.setStyleName( style);
	}
	@Override
	public void removeStyleName(String style){
		fieldLabel.removeStyleName( style);
	}

}
