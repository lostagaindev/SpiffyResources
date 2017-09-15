package lostagain.nl.spiffyresources.client;

import java.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;


import org.dellroad.lzma.client.CompressionMode;
import org.dellroad.lzma.client.LZMAByteArrayCompressor;
import org.dellroad.lzma.client.LZMAByteArrayDecompressor;
import org.dellroad.lzma.client.UTF8;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.HasText;

/** provides an easy to use interface for the GWTLzma libaury. 
 * It also divides the supplied comp / decomp strings into chunks of 200,000 characters
 * due to GWTLzma not supporting values around 250,000 in length 
 * (note, due to the split, compression ratios over that length will not be so good) **/
public class SpiffyGWTLzma {


	static Logger Log = Logger.getLogger("SpiffyGWTLzma");
	
	static final int COMPRESSION_LENGTH_LIMIT = 200000; //max characters the comp can handle
	static final String COMPRESSION_LIMIT_SEPERATOR = "~~~"; //Dont make this a regex! IDIOT! € is pretty unused right? right?
	
	
	public static interface StringResult {	
		void run(String result);
	}


	private static HasText ProgressMon;
	private static UpdateRunnable UpdateRunnable;
	
	
	/** compresses the supplied string into chunks of COMPRESSION_LENGTH_LIMIT separated by COMPRESSION_LIMIT_SEPERATOR
	 * (this limit and division is due to limitations in the used lib)
	 * @return **/
	public static void CompressString (final String compressthis, final StringResult runWhenAllDone, final UpdateRunnable updateRunnable){
		UpdateRunnable = updateRunnable;
		
		
		//first split to pieces
		final ArrayList<String> pieces = splitToBitsOfLength(compressthis,COMPRESSION_LENGTH_LIMIT);
		
	
		Log.info("will split to "+pieces.size()+" pieces, with the first bit starting with "+pieces.get(0).substring(0, 10));
		//Log.info(" and the last ending with "+pieces.get(pieces.size()-1).substring(pieces.size()-10));
		
		
		
		
		
		
		
		//when a chunk is finished this will get runned
		//it then looks to see if there's more chunks, if none then it runs "runWhenAllDone" with the finale result
		final StringResult runWhenDone = new StringResult(){
			
			String allCompressedChunks="";
			
			@Override
			public void run(String result) {				
				
				allCompressedChunks = allCompressedChunks  +result+COMPRESSION_LIMIT_SEPERATOR;
				Log.info("got result");
				
				if (pieces.size()>0){

					String chunkToCompress = pieces.get(0);
					pieces.remove(0);
					Log.info("starting next chunk");
					
					compressChunk(chunkToCompress,this);
					
				} else {
					Log.info("finnished all chunks");
					runWhenAllDone.run(allCompressedChunks);	
					
				}
				
			}			
		};
		 
		
		String chunkToCompress = pieces.get(0);
		pieces.remove(0);
        compressChunk(chunkToCompress, runWhenDone);
		
	}
	
	public static ArrayList<String> splitToBitsOfLength(String text, int csize) {
		
	    // work out num of bits needed based on text length and the size of the chunks we want
		int numOfBits = (text.length() + csize - 1) / csize;
		
		ArrayList<String> bits = new ArrayList<String>(numOfBits);
		//loop over separating off the bits from the whole and storing these pieces in an arraylist
	    for (int i = 0; i < text.length();i += csize) {
	    	String textchunk = text.substring(i, Math.min(text.length(), i + csize));
	        bits.add(textchunk);
	    }
	    return bits;
	}
	
	private static void compressChunk(final String compressthis,
			final StringResult runWhenDone) {
		Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            LZMAByteArrayCompressor c;
            @Override
            public boolean execute() {
                if (c == null) {
                	
                    c = new LZMAByteArrayCompressor(UTF8.encode(compressthis), CompressionMode.MODE_1); //Shouldn't need more then MODE_1 unless it gets REALLY big
                    Log.info("set up new compressor pcent="+(c.getProgress()));
                    return true;
                }
                if (c.execute()) {
                    int pcent = (int)(c.getProgress() * 100.0);
                   // LZMADemo.this.rightSizePanel.setWidget(new Label("Compressing... " + pcent + "%"));
                    Log.info("compresseing pcent="+(pcent));
                    updateProgress(" " + pcent  + " % ");
                    
                    return true;
                }
               // setRightData(c.getCompressedData());
                String cstring = prettyPrint(c.getCompressedData());
                
                runWhenDone.run(cstring);
                
               // updateSizes(true);
                return false;
            }
        });
	}
	
	public static String prettyPrint(byte[] data) {
			
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xff;
            buf.append(Character.forDigit(b >> 4, 16));
            buf.append(Character.forDigit(b & 0xf, 16));
            buf.append(' ');
          //  buf.append(i % 16 == 15 ? '\n' : ' ');
        }
        return buf.toString();
    }
	
	
	public interface UpdateRunnable {
		
		public void run(String updatetext);
	}
	
	/** decompresss the string compressed using CompressString
	 * @return **/
	public static void DecompressString (String string, final StringResult runWhenAllDone,final HasText progressLAbel, final UpdateRunnable updateRunnable){
		
		ProgressMon = progressLAbel;
		UpdateRunnable = updateRunnable;
		
		//first we get all the chunks by splitting around the seperator
		//we then create an array list from it		
		final String[] ccarray = string.split(COMPRESSION_LIMIT_SEPERATOR);
		//convert to an arraylist (we use an arraylist as its easier to remove stuff from it)
		final ArrayList<String> compressedChunks = new ArrayList<String>(Arrays.asList(ccarray));
		
		
		Log.info("number of chunks found in compressed string;" +compressedChunks.size());

		Log.info("chunk 0 size;" +compressedChunks.get(0).length());
		
		
		//we now decompress each chunk separately and put the result together as we go
		//when a chunk is finished this will get runned
				//it then looks to see if there's more chunks, if none then it runs "runWhenAllDone" with the finale result
				final StringResult runWhenDone = new StringResult(){
					
					String allUnCompressedChunks="";
					
					@Override
					public void run(String result) {				
						
						allUnCompressedChunks = allUnCompressedChunks+result;
						Log.info("got dec result");
						
						if (compressedChunks.size()>0){

							String chunkToDeCompress = compressedChunks.get(0);
							compressedChunks.remove(0);
							Log.info("starting to decomp next chunk");
							Log.info("next chunk="+chunkToDeCompress);
							decompressChunk(chunkToDeCompress,this);
							
						} else {
							Log.info("finnished decompressinging all chunks");
							runWhenAllDone.run(allUnCompressedChunks);	
							
						}
						
					}			
				};
		
		//decompress's as one chunk right now
		String chunkToDeCompress = compressedChunks.get(0);
		compressedChunks.remove(0);
		Log.info("decompressChunk 0");
		decompressChunk(chunkToDeCompress, runWhenDone);


		
	}

	/** decompres's a single chunk. This should be no larger then the COMPRESSION_LENGTH_LIMIT in characters **/
	private static void decompressChunk(String string, final StringResult runWhenDone) {
		
		final byte[] data = getDataToDecompress(string,true);
		
		if (data==null){
			
			Log.severe("-----------------------------(decompression error in save string see above)");
			//we really should flag and alert here
			
		}

		Log.severe("-----------------------------(starting decomp)");

        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            LZMAByteArrayDecompressor d;
            @Override
            public boolean execute() {
            	
                if (d == null) {
                    try {
                        d = new LZMAByteArrayDecompressor(data);
                    } catch (IOException e) {
                    	updateProgress("Decompression failed: " + e.getMessage());

                        Log.severe("Decompression failed: " + e.getMessage());
                        return false;
                    }
                    return true;
                }
                
                //                updateProgress("Decompressing... " + (d.getProgress())  + "||   _" + Math.random());
                
                if (d.execute()) {
                	
                	//disabled atm due to progress readout not working
                  //  int pcent = (int)(d.getProgress() * 100.0);
                   // updateProgress("Decompressing... " + pcent  + "|||   _" + Math.random());
                    
                    return true;
                }
                
                IOException ioe = d.getException();
                
                if (ioe != null) {
                	updateProgress("Decompression failed: " + ioe.getMessage());

                    Log.severe("--------Decompression failed: "+ ioe.getMessage());
                    return false;
                }
                String text;
                try {
                	
                	
                    text = UTF8.decode(d.getUncompressedData());

                	updateProgress("Decompression fragment done ");
                    
                } catch (IllegalArgumentException e) {
                	updateProgress("Decompression failed: " + e.getMessage());

                    Log.severe("--------Decompression failed: "+ e.getMessage());
                    return false;
                }
                
             //   displayAndLoadDecompressedString(text);
                
                Log.info("-----------------------------(runWhenDone)");
                runWhenDone.run(text);
                
                
                return false;
            }
        });
	}
	
	private static void updateProgress(String update){
		
		if (ProgressMon!=null){
			ProgressMon.setText(update);
		}
		
		if (UpdateRunnable!=null){
			UpdateRunnable.run(update);
			
		}
		Log.info("Decompressing... " + update + "%");
	}
	
	
	   static private byte[] getDataToDecompress(String gamedata, boolean alert) {
		   
	        ByteArrayOutputStream b = new ByteArrayOutputStream();
	        String s = gamedata;
	        boolean gothi = false;
	        int hinib = 0;
	        for (int i = 0; i < s.length(); i++) {
	            char ch = s.charAt(i);
	            if (" \t\f\n\r".indexOf(ch) != -1)
	                continue;
	            int digit = Character.digit(ch, 16);
	            if (digit == -1) {
	                if (alert)
	                    Log.severe("invalid compressed input: invalid hex character `" + ch + "'");
	                return null;
	            }
	            if (gothi)
	                b.write((hinib << 4) + digit);
	            else
	                hinib = digit;
	            gothi = !gothi;
	        }
	        if (gothi) {
	            if (alert)
	            	  Log.severe("invalid compressed input: odd number of digits");
	            return null;
	        }
	        return b.toByteArray();
	    }
	   
	
	
}


