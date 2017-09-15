package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.logging.Logger;

import org.matheclipse.parser.client.eval.DoubleEvaluator;





/**SpiffyCalculator will take a string input of a calculation and return its numerical result 
 * Example calculations would be;<br>
 * "5*6"<br>
 * "3-5-1"<br>
 * ((5^2)*(5-4))/2 <br>
 * calculations follow BODMAS ordering, so should be bracketed if you want a different order to apply.
 * 
 *  Notes; Sadly Sin/Cos/PI and other functions arnt supported.
 *  The libary used is outdated and a newer version thats gwt compatible could not be found :(
 *  **/
public class SpiffyCalculator {

	static Logger Log = Logger.getLogger("SpiffyCore.SpiffyCalculator");
	
	public void calculator(){
		
	}
	
	public static boolean isCalculation(String input){
		boolean state=true;
		
		//has at least one opp
		state= input.matches(".*[0-9]+[\\+\\-\\*\\/]+[0-9]+.*");
				
		return state;
		
	}
	
	public static boolean isBasicCalculation(String input){
		boolean state=true;
		//remove spaces
		input = input.replaceAll(" ", "");
		
		//split by operator
		String parts[] = input.split("\\+|-|/|\\*");
		
		//should be two parts
		if (parts.length!=2){
			state=false;
		}
		
		
		return state;
	}
	/** will take a string input of a calculation and return its numerical result 
	 * Example calculations would be;<br>
	 * "5*6"<br>
	 * "3-5-1"<br>
	 * ((5^2)*(5-4))/2 <br>
	 * calculations follow BODMAS ordering, so should be bracketed if you want a different order to apply.
	 * 
	 *  Notes; Sadly Sin/Cos/PI and other functions arnt supported.
	 *  The libary used is outdated and a newer version thats gwt compatible could not be found :(
	 *  **/
	public static double AdvanceCalculation(String input){
		
		//maybe we could do sin/cos ourselves?
		//1. look for presence of function
		//2. if found look for maths within it (eg, sin(0.1+0.1)  )
		//3. iterate by calling AdvanceCalculation again till its just a number
		//4. Work out Sin(0.2) etc, then replace it in the input
		//5. run AdvancedCalc again on result?
		
		//ok, we need to search for, say, Sin,Cos,Tan,ArTan and maybe a few others
		//we could use indexOf multiple times
		//but maybe theres a regex solution?(often gwt compatibility problems with that, however)
		//bonus; maybe also include random()? or is that out of scope. The JAM does do a extra loop just for random though. would be nice to get rid of
		
		
			try {
	            DoubleEvaluator engine = new DoubleEvaluator();
	            
	            double d = engine.evaluate(input);
	            return d;
	           
			} catch (Exception e) {
    	
				
				Log.severe("exception:"+e.getMessage());
				Log.severe("did calclation contained non-numbers?:"+input);
				Log.severe("This might be because your calculation contained a double negative (1--1), if so please put one of them in brackets (1-(0-1)):"+input);
				
    	
				e.printStackTrace();
            
            return -1;
			}
}

		
		
		
		//return the value
		
	
	
	public static float BasicCalculation(String input){
		input = input.replaceAll(" ", "");
		
		//split by operator
		String parts[] = input.split("\\+|-|/|\\*");
		
		String opp = input.substring(parts[0].length(), parts[0].length()+1);
		
		//each part should be a number
		
		float num1 = Float.parseFloat(parts[0]);
		float num2 = Float.parseFloat(parts[1]);
		
		return calculate_chunk(num1,opp,num2);
	}
	public static float calculate_chunk (float num1,String opp, float num2){
		float result =0;
		
		if (opp.compareTo("+")==0){
			result = num1 + num2; 
		} else if (opp.equals("-")){
			result = num1  - num2; 
		} else if (opp.compareTo("*")==0){
			result = num1 * num2; 
		} else if (opp.compareTo("/")==0){
			result = (float)(num1 / num2); 
			
		} 
		
		
		
		return result; 
	}
	
	
	
	/**returns the smallest non-negative number
	 * useful for finding the earliest position of something in a string...if its present at all */
	public static int smallestNonZero(int... Numbers){
		
		int smallest = Integer.MAX_VALUE; //everything should be smaller then this!
		
		 for ( int num : Numbers )           
		 {
			 if (num!=-1 && num<smallest){
				 smallest=num;
			 }
			 			 
		 }
		
		 if (smallest==Integer.MAX_VALUE){
			 smallest=-1;
		 }
		
		return smallest;
		
	}
	
public static int biggestNonZero(int A, int B, int C, int D){
		
		int R = 0;
		
		//set all values to stupidly low if under 0
		if (A<0){
			A=-100000;
		}
		if (B<0){
			B=-100000;
		}
		if (C<0){
			C=-100000;
		}
		if (D<0){
			D=-100000;
		}
		
		R = Math.max((Math.max(D, C)),(Math.max(A, B)));
		
		
		return R;
	}



/**
 * returns a random number based on a string requesting one.<br>
 * eg "4-88" (between 4 and 88)<br>
 * <br>
 * If the string is just a number, it will return a int between that number and 0
 *  * 
 * @param nval - string representing the max number
 * @return
 */
static public int getRandomFromRange(String nval) {

	int result = 0;
	//Log.info("getting random number from range:"+nval);	
//	Log.info("ERROR: "+nval+" not recognised as random range");	
	
	
	if (!nval.contains("-")){
		int MaxVal = Integer.parseInt(nval.trim());			
		result = (int) Math.round(Math.random()*MaxVal);
	} else {
		String MinString = nval.split("\\-")[0];
		String MaxString = nval.split("\\-")[1];

	//	Log.info("From:"+MinString);	
	//	Log.info("To:"+MaxString);

		int MinVal = Integer.parseInt(MinString.trim());
		int MaxVal = Integer.parseInt(MaxString.trim());
		int resultNum = (int) (MinVal+Math.round(Math.random()*(MaxVal-MinVal)));

		result = resultNum;
	}

	return result;
}


}
