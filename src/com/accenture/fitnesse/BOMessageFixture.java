package com.accenture.fitnesse;

public class BOMessageFixture {
	private BondUnderTest bondUnderTest;
	private int tradeId;
	
	public BOMessageFixture() {
		bondUnderTest = StaticBondUnderTest.bond;
	}
	
	public void setTradeId(int tradeId){
		this.tradeId = tradeId;
	}
	
	public String messageType() {
		return bondUnderTest.getMessageType();
	}
	
	public String messageStatus() {
		return bondUnderTest.getMessageStatus();
	}
}
