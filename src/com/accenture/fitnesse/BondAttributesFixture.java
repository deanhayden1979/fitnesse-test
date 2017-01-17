package com.accenture.fitnesse;

public class BondAttributesFixture {
	private BondUnderTest bondUnderTest;
	private int tradeId;
	
	public BondAttributesFixture() {
		bondUnderTest = StaticBondUnderTest.bond;
	}
	
	public void setTradeId(int tradeId){
		this.tradeId = tradeId;
	}
	
	public String tradeStatus() {
		return bondUnderTest.getTradeStatus();
	}
	
	public int noOfKeywords() {
		return bondUnderTest.getNumberOfKeywords();
	}
}
