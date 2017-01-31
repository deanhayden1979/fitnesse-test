package com.accenture.fitnesse; 

public class BondFixture {
	
	private BondUnderTest bondUnderTest;
	
	public BondFixture() {
		bondUnderTest = StaticBondUnderTest.bond;
	}

	public void setCsv(String csv) {
		bondUnderTest.setCSV(csv);
	}
	
	public int getTradeId() {
		return bondUnderTest.getTradeId();
	}	
}
