package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Manages SpiffyGenericCommands that allow the SpiffyScriptHelper to work
 *
 */
public class SpiffyCommandManagement {	

	static Logger Log = Logger.getLogger("SpiffyCore.SpiffyCommandManagement");
	

	/**
	 * A list of integers representing known scriptMarkStarts NativeKeyCode
	 * These mark the starts of commands and we can quickly check for these when typeing.
	 * This helps build autocomplete text-editors to make scripts.
	 * 
	 * NOTE: if a start mark has a few characters this is only the code of the FIRST.
	 * Extra checks should be done if you want to make sure the command is correct.
	 * The goal of this hashset just to provide a quick as possible check that "oh a command might be being typed"
	 * and not a definitive check.
	 */
	private static HashMap<Character,Character> knownScriptMarkCodes = null;


	private static SpiffyGenericCommand[] AllKnownCommands;
	
	
	
	/**
	 * For this to work you need to supply a database of enum commands
	 * @param anExampleCommand
	 */
	public SpiffyCommandManagement(SpiffyGenericCommand[] commandDatabase) {
		
		AllKnownCommands = commandDatabase;
		
		
		
	}
	
	public SpiffyGenericCommand[] getAllCommands(){
		return AllKnownCommands;		
	}

	/**
	 * We detect if the current NativeKeyCode matches a command open mark
	 * ie
	 * < or [
	 * 
	 * @param NativeKeyCode
	 * @return
	 */
	public static boolean testForOpenCommandCharacter(Character charCode) {
		
		//first ensure our list of open marks has been setup
		if (knownScriptMarkCodes == null){
			setupKnownStartMarks();
		}
			
		return knownScriptMarkCodes.keySet().contains(charCode);
	}

	private static void setupKnownStartMarks() {
		knownScriptMarkCodes = new HashMap<Character,Character>(); //arg probably needs to be String,Sring now due to multi-character start codes :(
		
		
		//loop over all script values adding the start marks to the hashset. (Hashsets automatically prevent duplicates)
		//This means all we need to do is convert the startmarks to their keycodes 
		for (SpiffyGenericCommand command : AllKnownCommands) {
			String startMark = command.getScriptMarkStart();
			Character startMarkCharcter = startMark.charAt(0);
			String endMark = command.getScriptMarkEnd();
			Character endMarkCharacter = endMark.charAt(endMark.length()-1); //note we get the LAST character in the end mark as it might have a few. eg :>
			
			knownScriptMarkCodes.put(startMarkCharcter,endMarkCharacter);
			
			Log.info("added to command starts:"+startMarkCharcter+" end mark:"+knownScriptMarkCodes.get(startMarkCharcter));
			
		};
		
	}

	
	
	public static Character getEndMarkForStartMark(Character keyCode) {
		return knownScriptMarkCodes.get(keyCode);
	}

}
