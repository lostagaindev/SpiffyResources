package lostagain.nl.spiffyresources.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a blank project to maintain and test SpiffyFunctions for use in other projects
 */
public class SpiffyResources implements EntryPoint {
	public static Logger Log = Logger.getLogger("SpiffyResources");

	static TextArea debugBox = new TextArea();	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		/*
		//this is just stuff for testing radial layout;
		SpiffyRadialTreeTester tester = new SpiffyRadialTreeTester();
		tester.getElement().getStyle().setBackgroundColor("blue");
		
		RootPanel.get().add(tester);
		tester.setup();
		
		//debug panel	
		debugBox.getElement().getStyle().setBackgroundColor("orange");
		debugBox.setHeight("800px");
		debugBox.setWidth("700px");
		
		
		RootPanel.get().add(debugBox,1200,0);
		*/
		
		/*
		
		RootPanel.get().add(new Label("test2"));
		
		SpiffyPanelSelector testContainer = new SpiffyPanelSelector("200px");
		
		
		//make some test widgets
		final Label testLabelA = new Label("Lab A");
		final Label testLabelB = new Label("Lab B");
		final Label testLabelC = new Label("Lab C");
		final Label testLabelD = new Label("Lab D");
		testLabelC.setHeight("45px");
		testLabelA.setHeight("35px");
		
		
		Log.info("adding widgets");
		testContainer.add(testLabelA);
		testContainer.add(testLabelB);
		testContainer.add(testLabelC);
		testContainer.add(testLabelD);
		
		//add a handler as a test
		testContainer.setOnSelectedHandler(new OnSelectedHandler() {			
			@Override
			public void run(int IndexSelected, Widget widgetSelected,Widget uns) {
				
				//reset all the  colors (crude but this is just a test)
				testLabelA.getElement().getStyle().clearBackgroundColor();
				testLabelB.getElement().getStyle().clearBackgroundColor();
				testLabelC.getElement().getStyle().clearBackgroundColor();
				testLabelD.getElement().getStyle().clearBackgroundColor();
				
				widgetSelected.getElement().getStyle().setBackgroundColor("#22E");
				
				
				
			}

		});
		
		testContainer.gotoWidget(2);
		
		//testwidget.SetEnabledStyles("style", "style2");
		
		
	//	SpiffyClassInspector tester = new SpiffyClassInspector(testwidget.getClass());
		
		//SpiffyPanelSelector

		RootPanel.get().add(testContainer);
		*/
		//add them (Note; panel has to already be on the page else size wont be correct)
	//	*/
				
	
		
	}
	
	
	
	public static void screenlog(String log){
		log=debugBox.getText()+"\n"+log;
		debugBox.setText(log);
		
	}
	
}
