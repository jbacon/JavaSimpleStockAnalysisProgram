//Josh Bacon
//CPSC 224
//New Billionaire Program
//Version 3.0
//3/14/2013
//Description: Same as previous version of New Billionaire, but uses multi-threading with Runnable methods to accomplish
//			tasks in a quicker manner, such as downloading files simultaneously, and calculating 
//			accumulated ratings simultaneously, instead of sequentially.

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

import nBSystemHelpers.AccRating;
import nBSystemHelpers.DownloadFile;
import nBSystemHelpers.MySQLAccess;
import nBSystemHelpers.SyncronizationCounter;
import java.awt.Font;
import javax.swing.JTabbedPane;


public class NBSystem extends JFrame {
	private JPanel contentPane;
	private final JPanel panelSouth = new JPanel();
	private final JButton connectDBButton = new JButton("");
	private final JButton downloadButton = new JButton("");
	private final JButton calculateButton = new JButton("");
	private final JButton exitButton = new JButton("");
	private final JTextArea txtAreaConnect = new JTextArea();
	private final JTextArea txtAreaDownload = new JTextArea();
	private final JTextArea txtAreaCalc = new JTextArea();
	private final JScrollPane scrollPaneConnect = new JScrollPane(txtAreaConnect);
	private final JScrollPane scrollPaneDownload = new JScrollPane(txtAreaDownload);
	private final JScrollPane scrollPaneCalc = new JScrollPane(txtAreaCalc);
	private String[] symbols = new String[30]; //Storing the symbols for use later
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NBSystem frame = new NBSystem();
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public NBSystem() {
		initGUI();
	}
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);
		tabbedPane.addTab("Connection", scrollPaneConnect);
		tabbedPane.addTab("Downloads", scrollPaneDownload);
		tabbedPane.addTab("Calculations", scrollPaneCalc);
		txtAreaConnect.setEditable(false);
		txtAreaDownload.setEditable(false);
		txtAreaCalc.setEditable(false);
		txtAreaConnect.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		txtAreaDownload.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		txtAreaCalc.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);	


		connectDBButton.setIcon(new ImageIcon(NBSystem.class.getResource("/resources/connect_to_DB.png")));
		connectDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Reads a table in MySQL database and writes the data to a csv file
				//Note: The csv file that is created might not work with Office/OpenOffice
				//		but will work the same in terms of reading from a file with java
				MySQLAccess dao;
				try {
					dao = new MySQLAccess();
					dao.readDataBase();
				} catch (Exception e1) {
					System.exit(0);
				}
				txtAreaConnect.append("Connected to MySQL Database\n");
				txtAreaConnect.append("Host: ada.gonzaga.edu\n");
				txtAreaConnect.append("User: cs224\n");
				txtAreaConnect.append("Database: cs224\n");
			}
		});
		panelSouth.add(connectDBButton);
		
		downloadButton.setIcon(new ImageIcon(NBSystem.class.getResource("/resources/icon_download.png")));
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//This section of Main opens the dow30.csv file that specifies
				//which companies this program should download stock information for
				//and then analyze
				Scanner fileIn = null;						//fileIn
				try {
					fileIn = new Scanner(new FileInputStream("dow30.csv")); //Try fileIn "dow30.csv"
				} catch (FileNotFoundException e1) {							
					System.out.println("File not found");					//Exception error
					System.exit(0);
				}
				//This section of Main asked the user the current date in formate
				//Month/Day/Year that is then used to create url Strings
				//This section of Main creates two dates that are used to create url Strings to be used in
				//downloading company stock information
				Calendar c1 = Calendar.getInstance();     //Date to start from
				Calendar c2 = Calendar.getInstance();	    //Date to end at
				c1.set(2012, 1, 1);
				//c2.set(Integer.parseInt(date.substring(6,10)), Integer.parseInt(date.substring(0,2)), Integer.parseInt(date.substring(3,5)));
				c2.setTime(new Date());
				String line = fileIn.nextLine();
				line = fileIn.nextLine(); //The first line is a header/info line, therefore I moved to next
				StringTokenizer fileLine = new StringTokenizer(line, ",");
				String symbol = "";
				String sDate;
				String strURL;
				//This for loop creates different url Strings that are passed to a new Thread for each company 
				//in the dow30 file, The String and the company Symbol are passed on to the MultiThreaded DownloadFile Class
				//This allows multiple downloads to occur across multiple Threads to speed up the overall download process
				for(int i = 0; i < 30; i++) {
					for(int j = 0; j < 3; j++) {
						symbol = fileLine.nextToken();
					}
					symbols[i] = symbol;
					sDate = String.format("&a=%1$tm&b=%1$te&c=%1$tY&d=%2$tm&e=%2$te&f=%2$tY",c1,c2);
					strURL = "http://ichart.finance.yahoo.com/table.csv?s="+symbol+sDate+"&g=d&ignore=.csv";
					Thread t = new Thread(new DownloadFile(strURL, symbol, txtAreaDownload));
					t.start();
					if (fileIn.hasNext()) {
						line = fileIn.nextLine();
					}
					fileLine = new StringTokenizer(line, ",");
				}
				//This while loop determines whether or not every thread has finished downloading the
				//company stock information. It does this with the help of a singleton designed class counter 
				//helper, and waiting for the value to equal 0.
				while(SyncronizationCounter.getInstance().getCount() != 0) {
					try {
						Thread.sleep(500);
					} catch(InterruptedException e1) {
						System.exit(0);
					}
				}
			}
		});
		panelSouth.add(downloadButton);
		
		calculateButton.setIcon(new ImageIcon(NBSystem.class.getResource("/resources/Calculator_Icon.gif")));
		calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//This for loop creates Threads to calculate the accumulated rating for each company
				//Each Runnable method will observe the last 5 days of the company's stock and 
				//determine if the stock is doing well or not.
				for(int k = 0; k < 30; k++) {
					Thread j = new Thread(new AccRating(symbols[k], txtAreaCalc));
					j.start();
				}
			}
		});
		panelSouth.add(calculateButton);
		
		exitButton.setIcon(new ImageIcon(NBSystem.class.getResource("/resources/Exit_Icon.png")));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panelSouth.add(exitButton);
	}
	public void start() {
		setVisible(true);
	}
}