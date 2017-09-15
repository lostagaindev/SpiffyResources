package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gonevertical.core.client.input.richtext.WiseRichTextArea;


public class SpiffyRichTextField extends SpiffyTextField {

	
	
	//final RichTextAreaWithPasteDetection richtextbox = new RichTextAreaWithPasteDetection();
	
	final WiseRichTextArea richtextbox = new WiseRichTextArea();
	
	
	
	final boolean clearFormatingOnPaste = true;

    Logger logger = Logger.getLogger("SpiffyGWT.SpiffyRichTextBoxField");
    
    @Override
    public void setHeight(String height){
    	richtextbox.getHtmlSizingPanel().setHeight(height);
		richtextbox.setHeight(height);
    }
    
	public SpiffyRichTextField(){
	
		//First we replace the suggest field with a rich text box
		int pos = this.getWidgetIndex(suggestField);
		this.remove(suggestField);
		this.insert(richtextbox, pos);
		richtextbox.setWidth("100%");
		richtextbox.setHeight("100%");
		
		
		//define the Cntr+V catcher
		/*
		KeyDownHandler CntrVCatcher = new KeyDownHandler() { 
                @Override 
                public void onKeyDown(KeyDownEvent event) { 

                	if (clearFormatingOnPaste){
                		   logger.log(Level.SEVERE, "________________clear formating on "+richtextbox.getHTML().length());
                        	
                        if (event.isControlKeyDown() && event.getNativeKeyCode() == 86) { 
                        	
                        	   logger.log(Level.SEVERE, "________________Paste"+richtextbox.getHTML().length());
                        	    
                        	   
                        } 
                        
                        
                	}
                        
                } 
        };
        
        if (clearFormatingOnPaste){
        	richtextbox.addKeyDownHandler(CntrVCatcher);
        }
        
		*/
		
		
	}
	
	
	public void setHTML(String htmlstring){
		richtextbox.setHTML(htmlstring);
	}
	public String getHTML(){
		return richtextbox.getHTML();
	}
	public void setEnabled(boolean state){
		richtextbox.setEnabled(state);
	
	}
	public void setFocus(boolean state){
		richtextbox.setFocus(state);
	}
	public void setStyleName(String style){
		richtextbox.setStyleName( style);
	}
	public void removeStyleName(String style){
		richtextbox.removeStyleName( style);
	}

}
