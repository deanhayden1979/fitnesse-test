package com.accenture.fitnesse;

public class BOTransferFixture {
	private BondUnderTest bondUnderTest;
	private int tradeId;
	
	public BOTransferFixture() {
		bondUnderTest = StaticBondUnderTest.bond;
	}
	
	public void setTradeId(int tradeId){
		this.tradeId = tradeId;
	}
	
	public String transferStatus() {
		return bondUnderTest.getTransferStatus();
	}
}
