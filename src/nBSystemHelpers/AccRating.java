package nBSystemHelpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JTextArea;
//This Class accumulates a rating for the company that is passed into
//it using an accumulating value that changes according the the 
//volume and close of the last 5 days stock information
public class AccRating implements Runnable{
	private String symbol; //Indicates which company is to be analyzed
	private JTextArea txtAreaCalc;
	//Runnable method for Thread, determines Accumulated Rating of company
	public void run() {
		Scanner fileIn = null;
		String infoLine;
		int accRating = 0;
		Double close;
		Double prevClose;
		Double volume;
		Double prevVolume;
		try {
			fileIn = new Scanner(new FileInputStream(symbol+".csv"));
		} catch (FileNotFoundException e) {
			txtAreaCalc.append("File not Found: "+ symbol+".csv\n");
			System.exit(0);
		}
		infoLine = fileIn.nextLine(); //Move past header line
		infoLine = fileIn.nextLine();
		for (int m = 0; m < 4; m++) {
			StringTokenizer infoFileLine = new StringTokenizer(infoLine, ",");
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			close = Double.parseDouble(infoFileLine.nextToken());
		    volume = Double.parseDouble(infoFileLine.nextToken());
		    infoLine = fileIn.nextLine();
		    infoFileLine = new StringTokenizer(infoLine, ",");
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			infoFileLine.nextToken();
			prevClose = Double.parseDouble(infoFileLine.nextToken());
		    prevVolume = Double.parseDouble(infoFileLine.nextToken());	
		    if (volume/prevVolume == .99 && close/prevClose == .99) {
		    	accRating = accRating + (-1);
		    }
		    else if (volume/prevVolume < .99 && close/prevClose < .99) {
		    	accRating = accRating + (-2);
		    }
		    else if (volume/prevVolume == 1.01 && close/prevClose == 1.01) {
		    	accRating = accRating + 1;
		    }
		    else if (volume/prevVolume > 1.01 && close/prevClose > 1.01) {
		    	accRating = accRating + 2;
		    }
		}
	    txtAreaCalc.append("TheardID: "+Thread.currentThread().getId()+" For "+symbol+" the accumulated rating is: "+accRating + "\n");
	    accRating = 0;
	}
	//Constructor method used to set symbol private variable for use in Runnable method for Thread
	public AccRating(String symbol, JTextArea txtAreaCalc) {
		this.symbol = symbol;
		this.txtAreaCalc = txtAreaCalc;
	}
}
