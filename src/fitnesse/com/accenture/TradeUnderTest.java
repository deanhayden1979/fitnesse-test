package com.accenture.fitnesse;

import com.calypso.tk.product.Bond;

public class BondUnderTest {
	private String csv;
	private Bond bond;

	public void setCSV(String csv) {
		this.csv = csv;
		
		// upload to Calypso, set status, add transfers etc
	}
	
	public String getCSV() {
		return csv;
	}	
	
	public int getTradeId() {
		return 1212;
	}
	
	public String getTradeStatus() {
		return "Verified";
	}

	public int getNumberOfKeywords() {
		return 8;	
	}
	
	public String getTransferStatus() {
		return "Verified";
	}
	
	public String getMessageType() {
		return "SWIFT";
	}
	
	public String getMessageStatus() {
		return "Verified";
	}
}
