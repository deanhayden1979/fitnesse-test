package fitnesse.com.accenture.datauploader.trade;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.PersistenceException;
import com.calypso.tk.core.Util;
import com.calypso.tk.publish.jaxb.CalypsoAcknowledgement;
import com.calypso.tk.publish.jaxb.CalypsoTrade;
import com.calypso.tk.publish.jaxb.Error;
import com.calypso.tk.util.DataUploaderUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fit.Fixture;
import fitnesse.com.accenture.connection.ConnectToCalypsoInstance;

public abstract class DirectDataUploaderTrade {

    private String csv;
    private ConnectToCalypsoInstance instance;

    private String header               = StringUtils.EMPTY;
    private int tradeDayOffSet          = 0;
    private int dayOffSet               = 1;
    private String inputFileName        = StringUtils.EMPTY;

    private int tradeId                 = 0;
    private String externalRefKey       = StringUtils.EMPTY;

    // # of records loaded successfully
    private int uploadCount             = 0;
    // # of records processed
    private int receivedCount           = 0;
    // # of records rejected from uploader
    private int rejectedCount           = 0;

    private List<String> errors         = Lists.newArrayList();

    public DirectDataUploaderTrade() throws InterruptedException {
        setInstance(ConnectToCalypsoInstance.getInstance());
    }

    public void beginTable() {
        String header = (String)Fixture.getSymbol("HEADER");
        this.header = header;
    }

    public void reset() {
        setExternalRefKey(StringUtils.EMPTY);
        setTradeId(0);

        uploadCount   = 0;
        receivedCount = 0;
        rejectedCount = 0;
    }

    public String generateUniqueReference() {
        return Util.datetimeToString(new JDatetime(), "yyyyMMddhhmmssSSS");
    }

    public void setDayOffSet(int offSet) {
        this.dayOffSet = offSet;
    }

    public int getDayOffSet() {
        return dayOffSet;
    }
    
    public void setTradeDayOffSet(int offSet) {
        this.tradeDayOffSet = offSet;
    }

    public int getTradeDayOffSet() {
        return tradeDayOffSet;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public void setHeader(String header) {
        String symbol = (String)Fixture.getSymbol(header);
        if (symbol == null) {
        	symbol = header;
        } 
        
        this.header = symbol;
    }

    public int getUploadedCount() {
        return this.uploadCount;
    }

    public int getReceivedCount() {
        return this.receivedCount;
    }

    public int getRejectedCount() {
        return this.rejectedCount;
    }

    public String getHeader() {
        return this.header;
    }

    public int tradeId() {
        return this.tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String externalReference() {
        return this.externalRefKey;
    }

    public void setExternalRefKey(String externalRefKey) {
        this.externalRefKey = externalRefKey;
    }

    public String getCsv() {        
        return csv;
    }

    public void setCsv(String csv) {
        String symbol = (String)Fixture.getSymbol(csv);
        if (symbol == null) {
        	symbol = csv;
        }
        
        this.csv = symbol;
    }

    public ConnectToCalypsoInstance getInstance() {
        return instance;
    }

    public void setInstance(ConnectToCalypsoInstance instance) {
        this.instance = instance;
    }

    private String createRow() {
        List<String> headerList = Splitter.on(",").splitToList(header);
        String csvRow = getCsv();

        /*
         * apply any field replacements required
         */
        Map<String, String> replacementElementMap = getReplacementElementMap();
        if (!Util.isEmpty(replacementElementMap)) {
            for (Map.Entry<String, String> replacementMap : replacementElementMap.entrySet()) {
                csvRow = replaceElement(headerList, replacementMap.getKey(), csvRow, replacementMap.getValue());
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(header).append("\n");
        builder.append(csvRow).append("\n");
        return builder.toString();
    }

    public abstract Map<String, String> getReplacementElementMap();

    private String replaceElement(List<String> headerList, final String elementName, String csvRow, String replaceString) {
        int indexOf = ArrayUtils.indexOf(headerList.toArray(), elementName);
        if (indexOf == -1) {
            return csvRow;
        }

        List<String> csvRowList = Splitter.on(",").splitToList(csvRow);
        if (Util.isEmpty(csvRowList)) {
            return csvRow;
        }

        List<String> returnList = Lists.newArrayList();
        // add before element
        returnList.addAll(csvRowList.subList(0, indexOf));
        // add modified element
        returnList.add(getFormattedValue(csvRowList.get(indexOf), replaceString));
        // add the rest of the row
        returnList.addAll(csvRowList.subList(indexOf+1, csvRowList.size()));

        return Joiner.on(",").join(returnList);
    }

    private String getFormattedValue(String originalValue, String replaceString) {
        return replaceString.replace("%", originalValue);
    }

    public boolean save() throws RemoteException, PersistenceException {
        String row = createRow();

		try {
			CalypsoAcknowledgement upload = DataUploaderUtil.uploadCSV(row, inputFileName, ",", "UTF-8", "UTF-8");
		
			uploadCount   = upload.getUploaded();
		    receivedCount = upload.getReceived();
		    rejectedCount = upload.getRejected();
		
		    if (upload.getReceived() == 1 && upload.getUploaded() == 1) {
		        List<CalypsoTrade> calypsoTradeList = upload.getCalypsoTrades().getCalypsoTrade();
		        CalypsoTrade calypsoTrade = calypsoTradeList.get(0);
		        setTradeIdentifiers(calypsoTrade);
		        return true;
		    } else if (upload.getReceived() == 1 && upload.getRejected() == 1) {
		        List<CalypsoTrade> calypsoTradeList = upload.getCalypsoTrades().getCalypsoTrade();
		        CalypsoTrade calypsoTrade = calypsoTradeList.get(0);
		        List<Error> errors = calypsoTrade.getError();
		
		        for (Error error : errors) {
		            this.errors.add(error.getValue());
		        }
		    } 
		} catch (Exception e) {
			throw new RuntimeException();
		}
        
        return false;
    }   

    private void setTradeIdentifiers(CalypsoTrade calypsoTrade) {
        tradeId = calypsoTrade.getCalypsoTradeId();
        externalRefKey = calypsoTrade.getExternalRef();
    }

    public List<String> getErrorList() {
        return this.errors;
    }

    public int numOfErrors() {
    	for (String S : this.errors) {
    		System.out.println(S);
    	}
        return Iterables.size(this.errors);
    }
}


