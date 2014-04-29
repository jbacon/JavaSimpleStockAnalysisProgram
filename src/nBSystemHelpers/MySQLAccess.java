package nBSystemHelpers;

import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
	private Connection connectDB = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public void readDataBase() throws Exception {
		try {
			//This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Setup the connection with the DB
			connectDB = DriverManager.getConnection("jdbc:mysql://ada.gonzaga.edu/cs224", "cs224","cs2241234.");
			//Statements allow to issue SQL queries to the database
			statement = connectDB.createStatement();
			//Result set get the table contents of Dow30 from the SQL query
			resultSet = statement.executeQuery("select * from Dow30");
			writeResultSet(resultSet);
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException, IOException {
		//ResultSet2 is initially before the first data set
		FileWriter fstream = new FileWriter("Dow30.csv");
		PrintWriter out = new PrintWriter(fstream);
		while (resultSet.next()) {
			//It is possible to get the columns via name
			//also possible to get the columns via the column number
			//which starts at 1
			//e.g. resultSet.getString(2);
			String company = resultSet.getString("Company");
			String sector = resultSet.getString("Sector");
			String symbol = resultSet.getString("Symbol");
			out.print(company);
			out.print(",");
			out.print(sector);
			out.print(",");
			out.println(symbol);
		}
		out.flush();
		out.close();
		fstream.close();
	}
	//Closes the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connectDB != null) {
				connectDB.close();
			}
		} catch (Exception e) {
		}
	}
}
