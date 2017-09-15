package lostagain.nl.spiffyresources.client.spiffycore;

import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;



/**
 * non-gwt colour manipulation
 * 
 * @author Tom
 *
 */
public class SpiffyColourFunctions {

	static Logger Log = Logger.getLogger("SpiffyCore.SpiffyColourFunctions");
	
	public enum cssColor {		
		aliceblue("#f0f8ff"),
		antiquewhite("#faebd7"),
		aqua("#00ffff"),
		aquamarine("#7fffd4"),
		azure("#f0ffff"),
		beige("#f5f5dc"),
		bisque("#ffe4c4"),
		black("#000000"),
		blanchedalmond("#ffebcd"),
		blue("#0000ff"),
		blueviolet("#8a2be2"),
		brown("#a52a2a"),
		burlywood("#deb887"),
		cadetblue("#5f9ea0"),
		chartreuse("#7fff00"),
		chocolate("#d2691e"),
		coral("#ff7f50"),
		cornflowerblue("#6495ed"),
		cornsilk("#fff8dc"),
		crimson("#dc143c"),
		cyan("#00ffff"),
		darkblue("#00008b"),
		darkcyan("#008b8b"),
		darkgoldenrod("#b8860b"),
		darkgray("#a9a9a9"),
		darkgreen("#006400"),
		darkkhaki("#bdb76b"),
		darkmagenta("#8b008b"),
		darkolivegreen("#556b2f"),
		darkorange("#ff8c00"),
		darkorchid("#9932cc"),
		darkred("#8b0000"),
		darksalmon("#e9967a"),
		darkseagreen("#8fbc8f"),
		darkslateblue("#483d8b"),
		darkslategray("#2f4f4f"),
		darkturquoise("#00ced1"),
		darkviolet("#9400d3"),
		deeppink("#ff1493"),
		deepskyblue("#00bfff"),
		dimgray("#696969"),
		dodgerblue("#1e90ff"),
		firebrick("#b22222"),
		floralwhite("#fffaf0"),
		forestgreen("#228b22"),
		fuchsia("#ff00ff"),
		gainsboro("#dcdcdc"),
		ghostwhite("#f8f8ff"),
		gold("#ffd700"),
		goldenrod("#daa520"),
		gray("#808080"),
		green("#008000"),
		greenyellow("#adff2f"),
		honeydew("#f0fff0"),
		hotpink("#ff69b4"),
		indianred ("#cd5c5c"),
		indigo("#4b0082"),
		ivory("#fffff0"),
		khaki("#f0e68c"),
		lavender("#e6e6fa"),
		lavenderblush("#fff0f5"),
		lawngreen("#7cfc00"),
		lemonchiffon("#fffacd"),
		lightblue("#add8e6"),
		lightcoral("#f08080"),
		lightcyan("#e0ffff"),
		lightgoldenrodyellow("#fafad2"),
		lightgrey("#d3d3d3"),
		lightgreen("#90ee90"),
		lightpink("#ffb6c1"),
		lightsalmon("#ffa07a"),
		lightseagreen("#20b2aa"),
		lightskyblue("#87cefa"),
		lightslategray("#778899"),
		lightsteelblue("#b0c4de"),
		lightyellow("#ffffe0"),
		lime("#00ff00"),
		limegreen("#32cd32"),
		linen("#faf0e6"),
		magenta("#ff00ff"),
		maroon("#800000"),
		mediumaquamarine("#66cdaa"),
		mediumblue("#0000cd"),
		mediumorchid("#ba55d3"),
		mediumpurple("#9370d8"),
		mediumseagreen("#3cb371"),
		mediumslateblue("#7b68ee"),
		mediumspringgreen("#00fa9a"),
		mediumturquoise("#48d1cc"),
		mediumvioletred("#c71585"),
		midnightblue("#191970"),
		mintcream("#f5fffa"),
		mistyrose("#ffe4e1"),
		moccasin("#ffe4b5"),
		navajowhite("#ffdead"),
		navy("#000080"),
		oldlace("#fdf5e6"),
		olive("#808000"),
		olivedrab("#6b8e23"),
		orange("#ffa500"),
		orangered("#ff4500"),
		orchid("#da70d6"),
		palegoldenrod("#eee8aa"),
		palegreen("#98fb98"),
		paleturquoise("#afeeee"),
		palevioletred("#d87093"),
		papayawhip("#ffefd5"),
		peachpuff("#ffdab9"),
		peru("#cd853f"),
		pink("#ffc0cb"),
		plum("#dda0dd"),
		powderblue("#b0e0e6"),
		purple("#800080"),
		red("#ff0000"),
		rosybrown("#bc8f8f"),
		royalblue("#4169e1"),
		saddlebrown("#8b4513"),
		salmon("#fa8072"),
		sandybrown("#f4a460"),
		seagreen("#2e8b57"),
		seashell("#fff5ee"),
		sienna("#a0522d"),
		silver("#c0c0c0"),
		skyblue("#87ceeb"),
		slateblue("#6a5acd"),
		slategray("#708090"),
		snow("#fffafa"),
		springgreen("#00ff7f"),
		steelblue("#4682b4"),
		tan("#d2b48c"),
		teal("#008080"),
		thistle("#d8bfd8"),
		tomato("#ff6347"),
		turquoise("#40e0d0"),
		violet("#ee82ee"),
	    wheat("#f5deb3"),
		white("#ffffff"),
		whitesmoke("#f5f5f5"),
		yellow("#ffff00"),
		yellowgreen("#9acd32");

		String HexCode = "";
		public int R = 0;
		public int G = 0;
		public int B = 0;
		
		
		cssColor(String hexCode){
			HexCode = hexCode;
			
			int RGB[] = HexToRGB(hexCode);
			R=RGB[0];
			G=RGB[1];
			B=RGB[2];
			
		}


		public int[] getAsRGBarray() {
			
			return new int[]{R,G,B};
		}
		
	}
	
	
	
	
	
	/**
	 * Returns the brightness from a hex string
	 * Useful as a quick way to check if a color is visible against another
	 * 
	 * @param hex
	 * @return
	 **/
	static public float LuminanceFromHex(String hex){
		
		int RGB[] = HexToRGB( hex);
		
		float[] HSB = rgbToHsl ( RGB[0],RGB[1],RGB[2]  );
		
		return HSB[2];
	}

	
	static public boolean areColoursSimilar(String col1css, String col2css ,int tolerance)
	{

		int colour1RGB[] = HexToRGB(col1css);
		int colour2RGB[] = HexToRGB(col2css);	
		
		return areColoursSimilar(colour1RGB,colour2RGB, tolerance);
		
	}
	/**
	 * compares if two colors are similar.
	 * Works out the differences between R1 and R2, G1 and G2 etc
	 * Adds them all up.
	 * If thats less then the tolerance, returns true.
	 * 
	 * 
	 * @param RGB
	 * @param RGB2
	 * @param tolerance
	 * @return
	 */
	static public boolean areColoursSimilar(int colour1RGB[],int colour2RGB[],int tolerance)
	{
		int RD = colour1RGB[0] - colour2RGB[0]; //red diff
		int GD = colour1RGB[1] - colour2RGB[1]; //green diff
		int BD = colour1RGB[2] - colour2RGB[2]; //blue diff
		
		//make sure they are all positive
		RD = Math.abs(RD);
		GD = Math.abs(GD);
		BD = Math.abs(BD);
		
		int totalDif = RD+GD+BD;
		
		if (totalDif<tolerance){
			return true;
		} else {
			return false;
		}
		
		
	}
	
	/**
	 * Converts css rgb strings to double array
	 * the range of numbers is uneffected, so its not colour space specific
	 * (ie. 0-255 in, 0-255 out)
	 * 
	 * 
	 * @param rgbstring
	 * @return
	 */
	static public double[] StringRGBAtoRGBA(String rgbstring){
		
		rgbstring = rgbstring.toLowerCase().trim();
		
		//strip rgba( if present and its matching )
		if (rgbstring.startsWith("rgba(")){
			rgbstring = rgbstring.substring(4, rgbstring.length()-1).trim();			
		} else if (rgbstring.startsWith("rgb(")){
			rgbstring = rgbstring.substring(3, rgbstring.length()-1).trim();
		}
		
		Log.info("converting string:"+rgbstring);
		String bits[] = rgbstring.split(",");
		
		double r = Double.parseDouble( bits[0] );
		double g = Double.parseDouble( bits[1] );
		double b = Double.parseDouble( bits[2] );
		
		if (bits.length==4){
			double a = Double.parseDouble( bits[3] );
			double RGBA[] = {r,g,b,a};	
			return RGBA;
		} else {
			double RGB[] = {r,g,b};		
			return RGB;
		}
		
		
	}
	
	/**
	 * supply a #FFF #FFFFFF or FFFFFF formated hex colour string
	 * returns a RGB int array in 0-255 range
	 * 
	 * @param hex
	 * @return
	 */
	static public int[] HexToRGB(String hex){
		Log.info("converting hex:"+hex);
		
		//crop of # if present
		if (hex.startsWith("#")){
			hex=hex.substring(1);
		}
		
		if (hex.length() < 6) {
			//expand to 6 characters
			hex = hex.substring(0, 1)
					+ hex.substring(0, 1)
					+ hex.substring(1, 2)
					+ hex.substring(1, 2)
					+ hex.substring(2, 3)
					+ hex.substring(2, 3);

			//hex = hex[0]+hex[0]+hex[1]+hex[1]+hex[2]+hex[2];


		}
		

		// convert to decimal and change luminosity

		int r = Integer.parseInt(hex.substring(0,2), 16);
		int g = Integer.parseInt(hex.substring(2,4), 16);
		int b = Integer.parseInt(hex.substring(4,6), 16);

		int RGB[] = {r,g,b};

		return RGB;
	}



	/**
	 * Converts an HSL color value to RGB. Conversion formula
	 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
	 * Assumes h, s, and l are contained in the set [0, 1] and
	 * returns r, g, and b in the set [0, 255].
	 *
	 * @param   Number  h       The hue (0-1)
	 * @param   Number  s       The saturation (0-1)
	 * @param   Number  l       The lightness (0-1)
	 * @return  Array           The RGB representation [0-255,0-255,0-255]
	 */
	static public int[] hslToRgb(float h, float s, float l){

		float r=0;
		float g=0;
		float b=0;

		if(s == 0){

			r = g = b = l; // achromatic

		} else{

			float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
			float p = 2 * l - q;

			r = hue2rgb(p, q, h + 1/3);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1/3);
		}

		int RGB[] = {Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)};

		return RGB;

	}




	private static float hue2rgb(float p, float q, float t){
		if(t < 0) t += 1;
		if(t > 1) t -= 1;
		if(t < 1/6) return p + (q - p) * 6 * t;
		if(t < 1/2) return q;
		if(t < 2/3) return p + (q - p) * (2/3 - t) * 6;

		return p;
	}

	/**
	 * Converts an RGB color value to HSL. Conversion formula
	 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
	 * Assumes r, g, and b are contained in the set [0, 255] and
	 * returns h, s, and l in the set [0, 1].
	 *
	 * @param   r - [0-255]
	 * @param   g - [0-255]
	 * @param   b - [0-255]
	 * @return  HSL  Array  [0-1,0-1,0-1]
	 */
	static public float[] rgbToHsl(float r, float g, float b){

		//r /= 255, g /= 255, b /= 255;
		r=r/255;
		g=g/255;
		b=b/255;
		
		float maxValue = Math.max(r, Math.max(g, b)); //as min and max can only take two parameters
		float minValue = Math.min(r, Math.min(g, b)); //we need to use each twice to find the smallest and biggest value given

		
		//var h, s, l = (maxValue + minValue) / 2;
		float h = 0;
		float s = 0;
		float l = (maxValue + minValue) / 2;

		if(maxValue == minValue){
			h = s = 0; // achromatic
		} 
		else
		{
			float d = maxValue - minValue;

			s = l > 0.5 ? d / (2 - maxValue - minValue) : d / (maxValue + minValue);

			if (maxValue == r) {
				h = (g - b) / d + (g < b ? 6 : 0);
			} else if (maxValue == g) {
				h = (b - r) / d + 2;
			} else if (maxValue == b) {
				h = (r - g) / d + 4;
			}

			h = h/6;
		}
		float hsl[] = {h,s,l};

		return hsl;
	}


	/**
	 * converts the array to the supplied range 
	 * 
	 * for example, convert a 0-255 color array to 0-1
	 * limitin = 255
	 * limitout = 1
	 * 
	 * @param rgbint
	 * @param i - range limit in 
	 * @param j - range limit out
	 * @return
	 */
	public static float[] convertRange(int[] rgbint, float limitin, float limitout) {
		
		float[] result = new float[rgbint.length];
		
		for (int i = 0; i < rgbint.length; i++) {
			
			result[i] = (rgbint[i] / limitin) * limitout;
			
		}
		
		
		return result;
	}

}
