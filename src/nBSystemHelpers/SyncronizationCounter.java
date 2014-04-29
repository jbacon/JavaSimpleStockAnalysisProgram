package nBSystemHelpers;

import nBSystemHelpers.SyncronizationCounter;

//By using the Singleton Design Pattern this class
//acts as a counter for the number of threads that are actively downloading
//If the count is 0, all Threads finished, and the Main Method can 
//continue on to accumulate stock ratings for each downloaded company file
public class SyncronizationCounter {
	private static SyncronizationCounter uniqueInstance;
	private int count = 0;
	private SyncronizationCounter() {
	}
	public static SyncronizationCounter getInstance() {
		if(uniqueInstance == null) {
			uniqueInstance = new SyncronizationCounter();
		}
		return uniqueInstance;
	}
	public void decCounter() {
		count = count - 1;
	}
	public void incCounter() {
		count = count + 1;
	}
	public int getCount() {
		return(count);
	}
}