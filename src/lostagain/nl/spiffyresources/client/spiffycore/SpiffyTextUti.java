package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * Will provide useful text Uti functions for any Java code (not -GWT specific)
 * 
 * @author Tom
 *
 */
public class SpiffyTextUti {
	public static Logger Log = Logger.getLogger("SpiffyCore.SpiffyTextUti");
	
    /**
     * 
     * The goal of this function is to split a string into a array while ignoring anything within brackets.<br>
     * So if we are splitting by | as the dividing character and [ and ] are the brackets<br>
     * <br>
     *   test a | test b | [ a | b |c ] | test c <br>
     *   <br>
     *   We will get an array of 4 elements 'test a','test b','[ a | b |c ]' and 'test c' <br>
     *   <br>
     * <br>
     * 
     * TODO: seems to go wrong when there is only 1 character between open and close quotes
     * 
     * @param input 
     * @param openingBracket
     * @param closingBracket
     * @return an arraylist of the elements
     */
    public static ArrayList<String> splitNotWithinBrackets(String input, String divider, char openingBracket, char closingBracket) {
    	
    	//Log.info("(splitting:"+input+" around "+divider+")"); 
		
    	
    	ArrayList<String> results = new ArrayList<String>();
    	
    	
    	//loop till next |
    	int pos = 0; //position we are currently searching from for the next split
    	//int SplitLocation = input.indexOf(divider, pos);
    	int SplitLocation = -1;  //position of last split
    	
    	do {
    		
    		pos = SplitLocation+1; //search from the next character after the last location
    		
    		//find next end point, ignoring any endpoints in brackets
    		int NextSplitLocation   = input.indexOf(divider, pos);    		
    		int NextBracketLocation = input.indexOf(openingBracket,pos);
    		
    		//This while look is used to ensure the "NextSplitLocation" isn't inside any open brackets
    		while (NextBracketLocation!=-1 && NextBracketLocation < NextSplitLocation){
    			
    			//we need to jump forward past the bracket to find the next split end
    			Log.info("(skipping to next char "+openingBracket+" end starts at "+NextBracketLocation+")"); 
    			int endBracketLocation = findClosingBracket(input,NextBracketLocation+1,openingBracket,closingBracket);
    			if (endBracketLocation==-1){
    				Log.severe("no closing char "+closingBracket+" found in "+input);
    				NextSplitLocation=-1; //see below, we just skip to end of string as one segment
    				break;
    			}
    			
    			Log.info("(ends at "+endBracketLocation+")");
    			pos = endBracketLocation+1;
    			NextSplitLocation = input.indexOf(divider, pos);    		
        		NextBracketLocation = input.indexOf(openingBracket,pos);
    		} 
    		    		
    		//once we have the split end we can add whats inbetween to the results
    		//--
    		//if the next split location is -1 it means there isn't one, so the next result ends at the end of the string
    		if (NextSplitLocation==-1){
    			
        		String lastResult = input.substring(SplitLocation+1); //goes from current location till end
        		
        		//if (!lastResult.isEmpty()){
        			results.add(lastResult);
        		//}
        		//we can escape out of the loop here given we were at the end of the string anyway!
        		break;
    		} 
    		
    		//If there is a next split location we make the new result between the last and the next one and proceed from there

    		String newResult = input.substring(SplitLocation+1, NextSplitLocation);
    		
    		//if (!newResult.isEmpty()){
    			results.add(newResult);
    		//}
    		SplitLocation = NextSplitLocation+(divider.length()-1);
    	}  while (SplitLocation>-1);
    	    
    	
		return results;
    }
    
    
    /**
     * This function is purely a scrap function to test other functions
     * It can be changed/removed disabled as desired, as it only puts stuff on the log
     */
    public static void testRandomStuff() {
    	
    	String simpleTest = "la la la [pickrandom :: one | two |three ] la la la ";
    	//try isolating the pickrandom using the find closing bracket function
    	int isolatedStart = 10;
    	int isolatedEnd = findClosingBracket(simpleTest,isolatedStart,'[',']');
    	
    	String isolated = simpleTest.substring(isolatedStart, isolatedEnd);

    	Log.info("simpleTest:"+simpleTest);
    	Log.info("Isolated :"+isolated);
    	

    	String simpleTest2 = "la la la [if sex>90 :: blah blah ::else:: else blah]  la la la ";
    	//try isolating the pickrandom using the find closing bracket function
    	int isolatedStart2 = 10;
    	int isolatedEnd2 = findClosingBracket(simpleTest2,isolatedStart2,'[',']');
    	
    	String isolated2 = simpleTest2.substring(isolatedStart2, isolatedEnd2);
    	
    	Log.info("Isolated 2 :"+isolated2);
    	Log.info("----------------------------");
    	
    	String splitTest  = "123456::else:: la [if sex>90 :: blah blah1 ::else:: else blah2] la la la ::else:: la  ";
    	ArrayList<String> resultTest = splitNotWithinBrackets(splitTest, "::else::", '[', ']');
    	
    	

    	Log.info("Split Test1:" +resultTest.size());
    	Log.info("Split Test1:" +resultTest.toString());
    	Log.info("---------------------");
    	

    	String splitTest2 = "123456789::else::123[if sex>90 :: blah1_[if sex>90 :: ignored this ::else:: and this]_blah1 ::else:: else blah2]::else::12234::else::12345[pickthis] la la la  ";
    	ArrayList<String> resultTest2 = splitNotWithinBrackets(splitTest2, "::else::", '[', ']');
    	
    	Log.info("Split Test 2:" +resultTest2.size());
    	Log.info("Split Test 2:" +resultTest2.toString());
    	
    	Log.info("---------------------");
    	
    }
	
    /**
     * Finds the closing bracket starting from the startFrom point.
     * 
     * @param paragraph - the string to search
     * @param startFrom - the char after the opening bracket 
     * @return s - the position of the matching close bracket (ie, so substrining from the start to the end will include whats inside the brackets but not the brackets themselves)
     *				Or -1 if no matching end bracket found
     */ 
    public static int findClosingBracket(String paragraph, int startFrom, char OpenBracketChar, char CloseBacketChar) {
		
		int nb =startFrom-1;//+1 Shouldn't be needed, but game crashes else where if present
		int bracketsopen=1;
		
		boolean foundmatch=false;
		
		
		while(nb<(paragraph.length()-1)){
		
			nb++; //skips first character?
			
			if (paragraph.charAt(nb)==CloseBacketChar){
				
				if (bracketsopen==1){
					foundmatch=true;
					break;
				}
				
				bracketsopen--;
				
			}
			
			if (paragraph.charAt(nb)==OpenBracketChar){
				bracketsopen++;
				
			}
			
			
		}
		
		if (foundmatch==false){
			return -1;
		}
		
		return nb;
	}
    
    public static String RemoveFromString(String original, String removestart, String removeend) {
    	
    	int startofsnippet = original.indexOf(removestart);
    	int endofsnippet = original.indexOf(removeend,startofsnippet)+removeend.length();
    	    	
    	
    	String before = original.substring(0,startofsnippet);
    	String after = original.substring(endofsnippet,original.length());
    	
    	
    	String newtext = new String(before+after);
    	
    	return newtext;
    }
    
    public static String InsertWithinTextField(TextBoxBase textfield, String texttoinsert) {
    	
    	
    	String text = textfield.getText();
    	
    	int insertnewhere = textfield.getCursorPos();
		String before = text.substring(0,insertnewhere);
    	String after = text.substring(insertnewhere,text.length());
		
    	text = before + texttoinsert + after;
    	
    	return text;
    	
    }


	public static String RemoveCartReturns(String input_string) {
		input_string = input_string.replaceAll("\n", " ");
		input_string = input_string.replaceAll("\r", " ");
		input_string = input_string.replaceAll("\r\n", " ");
		return input_string;
	}


	public static String stripHTML(String oldString) {
	
		   String newString = oldString.replaceAll("\\<.*?>","");
		   
		   return newString;
	}


	/**
	 * strips quotes at the start and end, if any.
	 * 
	 * If there is a quote at the end but not start, it wont be removed.
	 * 
	 * @param nexttextcontents
	 * @return
	 */
	public static String stripOuterQuotes(String text) {
		
		if (text.startsWith("\"")){		
			if (text.endsWith("\"")){
				text = text.substring(1, text.length()-1);
			} else {
				text = text.substring(1);						
			}
		}
				
		return text;
	}
    
    
    
    
    
}
