package icp.application.classification.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Training set parser
 * to read epochs and target classes from
 * text streams - for testing purposes only
 * 
 * @author Lukas Vareka
 *
 */
public class TrainingSetParser {
	private final List<double[][]> epochs;
	private final int NUMBER_OF_CHANNELS = 19;
	
	
	public TrainingSetParser() {
		this.epochs = new ArrayList<double[][]>();
	}

	/** Read a line of data from the underlying input stream
	  * 
	  * 
	  *  @return a line stripped of line terminators
	  */
	private String readLine(InputStream is) throws IOException {
	  StringBuffer sb = new StringBuffer("");

	  // while not the end of the stream comes
	  while (is.available() > 0)   {
	      char nextChar = (char) is.read();
	      if (nextChar == '\n') {
	        return new String(sb);
	      } else {
	        sb.append(nextChar);
	      }
	  }

      // end of the stream
	  return null;

	}
	
	public  List<double[]> readEpochs(InputStream is) throws IOException {
		List<double[]> epochs = new ArrayList<double[]>();
		String strLine;
	    while  ((strLine = this.readLine(is)) != null) {
	    	strLine = strLine.trim();
	        
	        // splits with every whitespace character
	        String[] epochLine = strLine.split("\\s+");
	        double[] epoch = new double[epochLine.length];
	        
	        
	        for (int i = 0; i < epochLine.length; i++) {
	           	try {
	        		double sample = Double.parseDouble(epochLine[i]);
	        		epoch[i] = sample;
	        	} catch (NumberFormatException e) {
	        		System.out.println(e.getMessage());
	        	}
	        }
	        epochs.add(epoch);
	        if (epochs.size() % 10 == 0)
	        	System.out.println(epochs.size() );
	    } // end while
		return epochs;
		
	}
	
	public List<Double> readTargets(InputStream is) throws IOException {
		String strLine;
		List<Double> targets = new ArrayList<Double>();
		while  ((strLine = this.readLine(is)) != null) {
	        strLine.trim();
	        String[] epochLine = strLine.split("\\s+");
	        //if (epochLine.length != 1)
	        //	throw new IOException("Target file: Unexpected data format! Array size should be 1, not " + epochLine.length + "! " + epochLine );
	        double targetClass = Double.parseDouble(epochLine[1]);
	        targets.add(targetClass);
		}
		return targets;
	}
	
	public void join(List<double[]> singleChannelEpochs, int channelIndex) {
		if (epochs.size() != 0 && singleChannelEpochs.size() != this.epochs.size())
			throw new IllegalArgumentException("Unexpected number of epochs. In all channels: " +
					this.epochs.size() + ", in the selected channel: " + singleChannelEpochs.size() );
		if (epochs.size() == 0) {
			
			for (int i = 0; i < singleChannelEpochs.size(); i++) {
				double[][] channelData = new double[NUMBER_OF_CHANNELS][];
				channelData[channelIndex - 1] = singleChannelEpochs.get(i);
				epochs.add(channelData);
			}
			
		} else {
			double[][] channelData =  null;;
			for (int i = 0; i < singleChannelEpochs.size(); i++) {
				channelData = this.epochs.get(i);
				channelData[channelIndex - 1] = singleChannelEpochs.get(i);
				epochs.set(i, channelData);
			}
		}
		
	}

	public List<double[][]> getEpochs() {
		return epochs;
	}
	
	

}
