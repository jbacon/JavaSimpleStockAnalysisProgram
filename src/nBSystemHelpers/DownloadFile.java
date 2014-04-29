package nBSystemHelpers;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import javax.swing.JTextArea;

public class DownloadFile implements Runnable{
	private String symbol;
    private String url;
    private JTextArea txtAreaDownload = null;
    //Runnable method for Thread
	public void run() {
		DataInputStream in = null;
		FileOutputStream fOut = null;
		DataOutputStream out = null;
		try {
			SyncronizationCounter.getInstance().incCounter(); //Inc counter
			URL remoteFile = new URL(url);
			URLConnection fileStream = remoteFile.openConnection(); //Changed
			in = new DataInputStream(new BufferedInputStream(fileStream.getInputStream()));
			//Open the input streams for the remote file
			fOut = new FileOutputStream(symbol+".csv");
			//Open the output streams for saving this file on disk
			out = new DataOutputStream(new BufferedOutputStream(fOut));
			//Read the remote on save save the file
			int data;
			while((data = in.read()) != -1) {
				out.write(data);
			}
			txtAreaDownload.append("ThreadId: "+Thread.currentThread().getId()+" Downloaded: "+symbol+ "\n");
			//System.out.println("ThreadId: "+Thread.currentThread().getId()+" Downloaded: "+symbol);
			SyncronizationCounter.getInstance().decCounter(); //Dec Counter
		} catch(MalformedURLException e){
			txtAreaDownload.append("Check the URL: "+ e.toString()+"\n");
			System.exit(0);
		} catch (ConnectException e) {
			txtAreaDownload.append(symbol+":failed! Connection Error!\n");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				if (fOut != null) fOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	//Allows url String and symbol String to be passed into the Runnable method
	//through the private instance variables of the overall class
	public DownloadFile(String url, String symbol, JTextArea txtAreaDownload) {
		this.symbol = symbol;
		this.url = url;
		this.txtAreaDownload = txtAreaDownload;
	}
}