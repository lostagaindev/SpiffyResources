package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.ArrayList;
import java.util.logging.Logger;



/** Functions to pass information to parent or child iFrames,
 * and to attach handlers to listen for those changes  **/
public class SpiffyWormhole {
	
	static boolean nativeJavascriptSetup = false;

	Logger Log = Logger.getLogger("SpiffyGWT.SpiffyWormhole");
	
	//handler for messages
	public static interface IncomingMessageHandler {		

		void run(String mode, String data);


	}
	
	static IncomingMessageHandler currentMessageHandler = null;
	
	
	/** Sends message to the parent frame **/
	public static void sendMessageToParent(String Mode,String Data){
		
		sendFunctionToParent(Mode,Data);
	}
	

	/** Will receive messages from the parent or child frame **/
	public static void addMessageHandler(IncomingMessageHandler newMessageHandler){
		if (!nativeJavascriptSetup){
			setup();
		}
		currentMessageHandler=newMessageHandler;
		
	}


	/** Will send a message to a child frame **/
	public static void sendMessageToChild(String ChildsElementID,String Mode,String Data){
		sendFunctionToChild(ChildsElementID,Mode,Data);
	}


	private static void setup() {
		//setup the Javascript methods
		defineBridgeMethodFromParent();
		
	}

	/** This function receives data from parent frame **/
	public static native void defineBridgeMethodFromParent() /*-{
	$wnd.triggerMessageRecieved = function(mode,data)  {

	return @lostagain.nl.spiffyresources.client.spiffygwt.SpiffyWormhole::triggerMessageRecieved(Ljava/lang/String;Ljava/lang/String;)(mode,data);
	}
	}-*/;
	
	
	public static int triggerMessageRecieved(String mode, String data){
		
		if (currentMessageHandler!=null){
			currentMessageHandler.run(mode,data);
			
		}
				
		return 42;
	}

	public static native void sendFunctionToParent(String mode,String data ) /*-{

		$wnd.parent.parent.triggerMessageRecieved(mode, data);

	}-*/;
	

	public static native void sendFunctionToChild(String ChildsElementID,String mode,String data) /*-{
	try {
		$doc.getElementById(ChildsElementID).contentWindow.triggerMessageRecieved(mode,
				data);
	} catch (err) {

	}

}-*/;
}
