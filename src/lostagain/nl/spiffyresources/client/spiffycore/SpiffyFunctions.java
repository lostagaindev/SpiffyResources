package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.ArrayList;
import java.util.logging.Logger;

public class SpiffyFunctions {

	static Logger Log = Logger.getLogger("SpiffyCore.SpiffyFunctions");
	public boolean isEven(int Number){
		
		if (Number/2==Math.round(Number/2)){
			return true;
		} else {
			return false;
		}
				
		
	}
	
	

	
	/**
	  * Licensed to the Apache Software Foundation (ASF) under one
	  * or more contributor license agreements.  See the NOTICE file
	  * distributed with this work for additional information
	  * regarding copyright ownership.  The ASF licenses this file
	  * to you under the Apache License, Version 2.0 (the
	  * "License"); you may not use this file except in compliance
	  * with the License.  You may obtain a copy of the License at
	*
	 *    http://www.apache.org/licenses/LICENSE-2.0
	*
	  * Unless required by applicable law or agreed to in writing,
	 *  software distributed under the License is distributed on an
	 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	 *  KIND, either express or implied.  See the License for the
	 *  specific language governing permissions and limitations
	 *  under the License.    
	
		   * Parse a line of text in CSV format and returns array of Strings
		   * Implementation of parsing is extracted from open-csv.
		   * http://opencsv.sourceforge.net/
		   * 
		   * @param csvLine
		   * @param separator
		   * @param quotechar
		   * @param escape
		   * @param strictQuotes
		   * @return
		   * @throws IOException
		   */
		    public static ArrayList<String> parseCsvLine(String csvLine, 
		                      char separator, char quotechar, 
		                      char escape, boolean strictQuotes) {
		      
		    	ArrayList<String>tokensOnThisLine = new ArrayList<String>();
		        StringBuilder sb = new StringBuilder(50);
		        boolean inQuotes = false;
		        for (int i = 0; i < csvLine.length(); i++) {
		          char c = csvLine.charAt(i);
		          if (c == escape) {
		            boolean isNextCharEscapable = inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
		                            && csvLine.length() > (i+1)  // there is indeed another character to check.
		                            && ( csvLine.charAt(i+1) == quotechar || csvLine.charAt(i+1) == escape);
	
		            if( isNextCharEscapable ){
		              sb.append(csvLine.charAt(i+1));
		              i++;
		            } 
		          } else if (c == quotechar) {
		            boolean isNextCharEscapedQuote = inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
		                && csvLine.length() > (i+1)  // there is indeed another character to check.
		                && csvLine.charAt(i+1) == quotechar;
		            if( isNextCharEscapedQuote ){
		              sb.append(csvLine.charAt(i+1));
		              i++;
		            }else{
		              inQuotes = !inQuotes;
		              // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
		                    if (!strictQuotes) {
		                        if(i>2 //not on the beginning of the line
		                                && csvLine.charAt(i-1) != separator //not at the beginning of an escape sequence
		                                && csvLine.length()>(i+1) &&
		                                csvLine.charAt(i+1) != separator //not at the  end of an escape sequence
		                        ){
		                            sb.append(c);
		                        }
		                    }
		            }
		          } else if (c == separator && !inQuotes) {
		            tokensOnThisLine.add(sb.toString());
		            sb = new StringBuilder(50); // start work on next token
		          } else {
		                if (!strictQuotes || inQuotes)
		                    sb.append(c);
		          }
		        }
		        // line is done - check status
		        if (inQuotes) {
		        //  _log.warn("Un-terminated quoted field at end of CSV line. \n ["+csvLine+"]");
		        }
		        if (sb != null) {
		          tokensOnThisLine.add(sb.toString());
		        }
		        return tokensOnThisLine;
		    }




	public static String StripCSSfromNumber(String value){
			
		if (value.endsWith("px")){			
			return value.substring(0, value.length()-2);			
		} else if (value.endsWith("em")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("ex")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("%")) {
			return value.substring(0, value.length()-1);
		} else if (value.endsWith("px")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("mm")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("cm")){
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("in")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("pt")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("pc")){
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("ch")) {
			return value.substring(0, value.length()-2);
		} else if (value.endsWith("rem")){
			return value.substring(0, value.length()-3);
		} else {
			
			//Log.warning("can not resolve "+value+" as a css size spec, returning as is");
						
			return value;
		}
		
		
		
		
		
	}
	
	/** stores 3 ints representing a color */
	public static class color_tripple {
		public int v1;
		public int v2;
		public int v3;

		public color_tripple(int a, int b, int c) {
			v1 = a;
			v2 = b;
			v3 = c;

		}
	}
	
	
	public static color_tripple HSL_2_RGB(float H,float S, float L) {
		// hues values should be between 0 and 1
		H = H/100;
		S = S/100;
		L = L/100;
		System.out.println("sat is"+S);
		System.out.println("hue is"+H);
		
		color_tripple RGB_data = new color_tripple(50, 50, 50);
		float R = 0, G = 0, B = 0;
		float var_2 = 0;

		if (S == 0) {
			R = L * 255;
			G = L * 255;
			B = L * 255;
		} else {
			if (L < 0.5) {
				var_2 = L * (1 + S);
				//System.out.println("L less then 0.5 "+var_2);
				
			} else {
				var_2 = (L + S) - (S * L);
				//System.out.println("L more= then 0.5 "+var_2);
			}
			float var_1 = 2 * L - var_2;
			
			System.out.println("red H= "+(H+(0.33333)));
			
			R = Math.round(255 * Hue_2_RGB(var_1, var_2, (H + (0.333333))));
			
			G = Math.round(255 * Hue_2_RGB(var_1, var_2, H));
			//System.out.println("H= "+(H));
			
			B = Math.round(255 * Hue_2_RGB(var_1, var_2, (H - (0.333333))));

		}

		RGB_data.v1 = Math.round(Math.round(R));
		RGB_data.v2 = Math.round(Math.round(G));
		RGB_data.v3 = Math.round(Math.round(B));
		return RGB_data;
	}
	
	

	public static double Hue_2_RGB(double v1, double v2, double vH) {
		System.out.println("vH= "+vH);
		if (vH < 0) {
			vH += 1;
		}
		if (vH > 1) {
			vH -= 1;
		}
		if ((6 * vH) < 1) {
			System.out.println("blah-"+(v1 + (v2 - v1) * 6 * vH));
			return (v1 + (v2 - v1) * 6 * vH);
		}
		if ((2 * vH) < 1) {
			return v2;
		}
		if ((3 * vH) < 2) {
			System.out.println("2 /3 = "+(2 / (double)3));
			return (v1 + (v2 - v1) * ((2 / (double)3) - vH) * 6);
		}
		return v1;

	}
	
	
	
	
	
}
