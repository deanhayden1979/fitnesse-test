package fitnesse.com.accenture.datauploader.trade;

import java.util.Map;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.Util;
import com.google.common.collect.Maps;
//import calypsox.tk.core.DateUtil;
import fitnesse.com.accenture.datauploader.trade.DirectDataUploaderTrade;

public class SimpleTransfer extends DirectDataUploaderTrade {

	public SimpleTransfer() throws InterruptedException {
		super();
		setDayOffSet(0);
		setInputFileName("SimpleTransfer_");
	}

	@Override
	public Map<String, String> getReplacementElementMap() {
		Map<String, String> replacementMap = Maps.newHashMap();
		replacementMap.put("ExternalRefId", "%_" + Util.datetimeToString(new JDatetime(), "yyyyMMddhhmmssSSS"));
		replacementMap.put("TradeDateTime", Util.dateToString(JDate.getNow(), "yyyyMMdd"));
		replacementMap.put("StartDate", Util.dateToString(JDate.getNow(), "yyyyMMdd"));
	//	replacementMap.put("MaturityDate", Util.dateToString(DateUtil.addBusinessDays(JDate.getNow(), null, getDayOffSet()), "yyyyMMdd"));
		return replacementMap;
	}	
}