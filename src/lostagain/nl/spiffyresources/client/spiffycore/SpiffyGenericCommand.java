package lostagain.nl.spiffyresources.client.spiffycore;


public interface SpiffyGenericCommand {


	String getAfterCursor();
	String getBeforeCursor();
	
	/**
	 * Script Mark start is the symbols that determain the start of the command. These are what triggers the script helper<br>
	 * Ideally scripting languages should only have a few variations of these, as all have to be checked constantly when typing<br>
	 * ie.<br>
	 * {@literal <}acommand{@literal >}       - the brackets would be the script mark start and end.<br>
	 * {@literal <}anothercommand{@literal >} - the brackets would be the script mark start and end.<br>
	 * <br>
	 * @return
	 */
	String getScriptMarkStart();
	/**
	 * Script Mark end is the symbols that determain the end of the command. These are what triggers the script helper to stop<br>
	 * Ideally scripting languages should only have a few variations of these, as all have to be checked constantly when typing<br>
	 * ie.<br>
	 * {@literal <}acommand{@literal >}       - the brackets would be the script mark start and end.<br>
	 * {@literal <}anothercommand{@literal >} - the brackets would be the script mark start and end.<br>
	 * <br>
	 * @return
	 */
	String getScriptMarkEnd();
	
	/**
	 * command description. In future this might get more elaborate to support per-parameter descriptions
	 * @return
	 */
	String getDiscription();
	
	/**
	 * work in progress. Parameter separator. Typically a comma, but can be any character to split by
	 * @return
	 */
	String getParamterSeperator();
	
	//default	String getTotalText() {		
	//	return getScriptMarkStart()+getBeforeCursor()+getAfterCursor()+getScriptMarkEnd();
	//}
	
	
	
	
	
	
	
	

}
