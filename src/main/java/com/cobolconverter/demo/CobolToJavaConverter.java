package com.cobolconverter.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class CobolToJavaConverter {
	
	

	static int WS_TOTAL_CNT = 0;
	static int WS_TOTAL_DTL=0;
	static int WS_SAVE_ITEM=0;
	static int WS_VZPP_ORB_PMT_ITEMS = 0;

	static float WS_VZPP_ORB_PMT_AMT = 0;
	static float WS_TOTAL_AMT = 0;
	static int WS_VZPP_ORB_RTN_ITEMS = 0;
	static float WS_VZPP_ORB_RTN_AMT = 0;
	final static DecimalFormat decimalFormat = new DecimalFormat("000000");
	final static DecimalFormat totadecimalFormat = new DecimalFormat("0000000000");
	static String DATE_FORMAT = "yyMMdd";
	static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	static Calendar c1 = Calendar.getInstance(); // today
	static Date date = new Date();
	static DateFormat format = new SimpleDateFormat("HHmm");

	private static String inputFile="E:\\NTF4RPV_RPVF3053_G0325.txt";
	
	private static String outputRPVFPO0A="E:\\RPVFPO0A.txt";
	
	private static String outputRPVFPO0Z="E:\\RPVFPO0Z.txt";

	public static void convertCobolToJava() throws IOException {
		

		
		InputStream input = null;
		BufferedReader br = null;
		FileReader fr = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		BufferedWriter bwvzppTot = null;
		FileWriter fwbwvzppTot = null;

		try {
			fw = new FileWriter(outputRPVFPO0A);
			bw = new BufferedWriter(fw);
			fwbwvzppTot = new FileWriter(outputRPVFPO0Z);
			bwvzppTot = new BufferedWriter(fwbwvzppTot);
			ArrayList<RPVI3053> arrRPVI3053 = new ArrayList<RPVI3053>();
			ArrayList<RPVIPKEY> arrRPVI3Key = new ArrayList<RPVIPKEY>();
			fr = new FileReader(inputFile);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {

				arrRPVI3Key.add(convertRPVI3Key(sCurrentLine));
				arrRPVI3053.add(convertRPVI3053(sCurrentLine));
			}
			processOutput(arrRPVI3Key, arrRPVI3053, bw, bwvzppTot);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			
			bw.close();
				bwvzppTot.close();

			
		}
	}

	/**
	 * 
	 * This method is used to process output for every line in input file
	 * 
	 * @param arrRPVI3Key
	 * @param arrRPVI3053
	 * @param bw
	 * @param bwvzppTot
	 * @throws IOException
	 */
	private static void processOutput(ArrayList<RPVIPKEY> arrRPVI3Key, ArrayList<RPVI3053> arrRPVI3053,
			BufferedWriter bw, BufferedWriter bwvzppTot) throws IOException {
		// TODO Auto-generated method stub

		int j = 0;
		// final DecimalFormat decimalFormatWSTOTAL-CNT = new
		// DecimalFormat("000000000");
		while (arrRPVI3Key.size() > j) {
			RPVIPKEY rpvipkey = arrRPVI3Key.get(j);
			RPVI3053 rpvi3053 = arrRPVI3053.get(j);
			System.out.println(rpvi3053.toString());
			if (j == 0) {
				writeHeader(bw, rpvi3053);

			}
			writeDetail(bw, rpvi3053);
			accumulateVZPPTotals(rpvi3053);
			writeVZPPTotals(bwvzppTot, rpvipkey);
			if (j == arrRPVI3Key.size() - 1) {
				writeTrailer(bw, rpvi3053);

			}
			j++;
		}
		System.out.println("Program Completed Succesfully");

	}

	/**
	 * 
	 * This program is used to write Header information in the output file
	 * 
	 * @param bw
	 * @param rpvi3053
	 * @throws IOException
	 */
	private static void writeHeader(BufferedWriter bw, RPVI3053 rpvi3053) throws IOException {
		StringBuffer header = new StringBuffer();

		// Header Record
		/*
		 * SET ORB-HDR-TAPE OF ORBIT-POSTING-RECORD TO TRUE 034400 034500 034600
		 * INITIALIZE ORB-HEADER-TRANSACTION OF ORBIT-POSTING-RECORD 034700
		 * 034800 SET ORB-HDR-TRANS OF ORBIT-POSTING-RECORD TO TRUE 034900 /*
		 * MOVE WS-CURRENT-DATE-YYMMDD TO ORB-CREATE-YYMMDD OF
		 * ORBIT-POSTING-RECORD MOVE WS-CURRENT-TIME-HHMM TO ORB-CREATE-HHMN OF
		 * ORBIT-POSTING-RECORD MOVE 000000 TO ORB-ITEM-NUM OF
		 * ORBIT-POSTING-RECORD
		 */
		header.append(rpvi3053.getORB_HDR_TAPE());
		header.append(sdf.format(c1.getTime()));
		header.append(format.format(date));
		rpvi3053.setORB_ITEM_NUM(0);
		header.append("000000");
		/*
		 * MOVE WS-BANK-SOURCE-CODE TO ORB-BANK-ID OF ORBIT-POSTING-RECORD
		 * ORB-BANK-ACCT-NUM OF ORBIT-POSTING-RECORD ADD 1 TO WS-TOTAL-CNT WRITE
		 * RPVFPO0A-REC FROM ORBIT-POSTING-RECORD
		 */
		header.append("02010398101");
		rpvi3053.setORB_BANK_ID("02010398101");
		rpvi3053.setORB_BANK_ACCT_NUM("02010398101");
		header.append("02010398101");
		WS_TOTAL_CNT++;
		bw.write(header.toString());
		bw.newLine();

		/*
		 * MOVE WS-CURRENT-DATE-YYMMDD TO ORB-CREATE-YYMMDD OF
		 * ORBIT-POSTING-RECORD MOVE WS-CURRENT-TIME-HHMM TO ORB-CREATE-HHMN OF
		 * ORBIT-POSTING-RECORD MOVE 000000 TO ORB-ITEM-NUM OF
		 * ORBIT-POSTING-RECORD MOVE '080' TO ORB-LRECL OF ORBIT-POSTING-RECORD
		 * MOVE '0800' TO ORB-BLKSIZE OF ORBIT-POSTING-RECORD ADD 1 TO
		 * WS-TOTAL-CNT WRITE RPVFPO0A-REC FROM ORBIT-POSTING-RECORD.
		 */
		header=null;
		StringBuffer headertxn = new StringBuffer();
		headertxn.append(rpvi3053.getORB_HDR_TAPE());
		headertxn.append(sdf.format(c1.getTime()));
		headertxn.append(format.format(date));
		headertxn.append("000000");
		rpvi3053.setORB_ITEM_NUM(0);
		rpvi3053.setORB_LRECL("080");
		headertxn.append(rpvi3053.getORB_LRECL());
		rpvi3053.setORB_BLKSIZE("0800");
		headertxn.append(rpvi3053.getORB_BLKSIZE());
		bw.write(headertxn.toString());
		bw.newLine();
		headertxn=null;
	}

	/**
	 * This method is used to write Detail information in output file
	 * 
	 * @param bw
	 * @param rpvi3053
	 * @throws IOException
	 */
	private static void writeDetail(BufferedWriter bw, RPVI3053 rpvi3053) throws IOException {
		StringBuffer sb = new StringBuffer();
		final int i = 0;

		/*
		 * // Write Detail Record WRITE-DETAIL-REC.
		 * 
		 * INITIALIZE ORB-DETAIL OF ORBIT-POSTING-RECORD
		 * 
		 * SET ORB-DTL OF ORBIT-POSTING-RECORD TO TRUE
		 * 
		 * MOVE WS-CURRENT-DATE-YYMMDD TO ORB-CREATE-YYMMDD OF
		 * ORBIT-POSTING-RECORD MOVE WS-CURRENT-TIME-HHMM TO ORB-CREATE-HHMN OF
		 * ORBIT-POSTING-RECORD MOVE WS-SAVE-ITEM-NUM TO ORB-ITEM-NUM OF
		 * ORBIT-POSTING-RECORD MOVE ORB-CUST-ACCT-NUM OF RPVC3053-REC TO
		 * ORB-CUST-ACCT-NUM OF ORBIT-POSTING-RECORD MOVE ORB-PMT-DATE OF
		 * RPVC3053-REC TO ORB-PMT-DATE OF ORBIT-POSTING-RECORD MOVE
		 * ORB-AMT-PAID OF RPVC3053-REC TO ORB-AMT-PAID OF ORBIT-POSTING-RECORD
		 * MOVE ORB-TRACKING-NUM OF RPVC3053-REC TO ORB-TRACKING-NUM OF
		 * ORBIT-POSTING-RECORD MOVE ORB-PMT-TYPE OF RPVC3053-REC TO
		 * ORB-PMT-TYPE OF ORBIT-POSTING-RECORD MOVE ORB-NSF-INDR OF
		 * RPVC3053-REC TO ORB-NSF-INDR OF ORBIT-POSTING-RECORD MOVE
		 * ORB-NSF-REASON-CODE OF RPVC3053-REC TO ORB-NSF-REASON-CODE OF
		 * ORBIT-POSTING-RECORD MOVE ORB-PMT-PROCESS-TYPE OF RPVC3053-REC
		 * F1052063 TO ORB-PMT-PROCESS-TYPE OF ORBIT-POSTING-RECORD F1052063
		 * MOVE ORB-PMT-CONFIRMATION-NO OF RPVC3053-REC F1059951 TO
		 * ORB-PMT-CONFIRMATION-NO OF ORBIT-POSTING-RECORD F1059951 MOVE
		 * ORB-V5-VOD-INDR OF RPVC3053-REC F1059951 TO WS-V5-VOD-INDR PERFORM
		 * 80000-ACCUM-VZPP-TOTALS
		 * 
		 * ADD 1 TO WS-TOTAL-CNT WS-TOTAL-DTL F8331805 WS-SAVE-ITEM-NUM
		 * 
		 * WRITE RPVFPO0A-REC FROM ORBIT-POSTING-RECORD.
		 */

		sb.append(rpvi3053.getORB_HDR_TAPE());
		sb.append(sdf.format(c1.getTime()));
		sb.append(format.format(date));
		sb.append(decimalFormat.format(i));
		sb.append(rpvi3053.getORB_CUST_ACCT_NUM());
		sb.append(rpvi3053.getORB_PMT_DATE_YY());
		sb.append(rpvi3053.getORB_PMT_DATE_MM());
		sb.append(rpvi3053.getORB_PMT_DATE_DD());
		String amtPaid=rpvi3053.getORB_AMT_PAID().toString().replace(".", "");
		sb.append(totadecimalFormat.format(rpvi3053.getORB_AMT_PAID()));
		sb.append(rpvi3053.getORB_TRACK_NUM_PRI());
		sb.append(rpvi3053.getORB_TRACK_NUM_SEC());
		sb.append(rpvi3053.getORB_PMT_TYPE());
		sb.append(rpvi3053.getORB_NSF_INDR());
		sb.append(rpvi3053.getORB_NSF_REASON_CODE());
		sb.append(rpvi3053.getORB_ProcessType());
		sb.append(rpvi3053.getORB_PMT_CONFIRMATION_NO());
		sb.append(rpvi3053.getORB_V5INDR());
		bw.write(sb.toString());
		bw.newLine();
		sb = null;
		WS_TOTAL_DTL++;
		WS_TOTAL_CNT++;
		WS_SAVE_ITEM++;
	}

	/**
	 * This method is used to write Trailer information in output file
	 * 
	 * @param bw
	 * @param rpvi3053
	 * @throws IOException
	 */
	private static void writeTrailer(BufferedWriter bw, RPVI3053 rpvi3053) throws IOException {
		StringBuffer tailer = new StringBuffer();
		/*
		 * WRITE-TRAILER-RECS. INITIALIZE ORB-TRAILER-TRANSACTION OF
		 * ORBIT-POSTING-RECORD SET ORB-TRL-TRANS OF ORBIT-POSTING-RECORD TO
		 * TRUE
		 * 
		 * ADD 1 TO WS-TOTAL-CNT
		 * 
		 * MOVE WS-CURRENT-DATE-YYMMDD TO ORB-CREATE-YYMMDD OF
		 * ORBIT-POSTING-RECORD MOVE WS-CURRENT-TIME-HHMM TO ORB-CREATE-HHMN OF
		 * ORBIT-POSTING-RECORD MOVE 999999 TO ORB-ITEM-NUM OF
		 * ORBIT-POSTING-RECORD MOVE WS-TOTAL-DTL F8331805 TO ORB-TRANS-COUNT OF
		 * ORBIT-POSTING-RECORD MOVE WS-TOTAL-AMT TO ORB-TRANS-AMT OF
		 * ORBIT-POSTING-RECORD WRITE RPVFPO0A-REC FROM ORBIT-POSTING-RECORD
		 * INITIALIZE ORB-TRAILER-TAPE OF ORBIT-POSTING-RECORD SET ORB-TRL-TAPE
		 * OF ORBIT-POSTING-RECORD TO TRUE ADD 1 TO WS-TOTAL-CNT MOVE
		 * WS-CURRENT-DATE-YYMMDD TO ORB-CREATE-YYMMDD OF ORBIT-POSTING-RECORD
		 * MOVE WS-CURRENT-TIME-HHMM TO ORB-CREATE-HHMN OF ORBIT-POSTING-RECORD
		 * MOVE 999999 TO ORB-ITEM-NUM OF ORBIT-POSTING-RECORD MOVE WS-TOTAL-CNT
		 * TO ORB-TAPE-COUNT OF ORBIT-POSTING-RECORD MOVE WS-TOTAL-AMT TO
		 * ORB-TAPE-AMT OF ORBIT-POSTING-RECORD WRITE RPVFPO0A-REC FROM
		 * ORBIT-POSTING-RECORD.
		 */

		WS_TOTAL_CNT++;
		tailer.append(rpvi3053.getORB_HDR_TAPE());
		tailer.append(sdf.format(c1.getTime()));
		tailer.append(format.format(date));
		rpvi3053.setORB_ITEM_NUM(999999);
		tailer.append(rpvi3053.getORB_ITEM_NUM());
		rpvi3053.setORB_TAPE_COUNT(WS_TOTAL_DTL);
		tailer.append(decimalFormat.format(WS_TOTAL_DTL));
		rpvi3053.setORB_TAPE_AMT(WS_TOTAL_AMT);
		String totAmt=String.format("%.2f", rpvi3053.getORB_TAPE_AMT()).replace(".", "");
		Integer temp=new Integer(totAmt);
		tailer.append(totadecimalFormat.format(temp));
		bw.write(tailer.toString());
		bw.newLine();
		WS_TOTAL_CNT++;
		tailer.delete(0, tailer.length());
		tailer.append(rpvi3053.getORB_HDR_TAPE());
		tailer.append(sdf.format(c1.getTime()));
		tailer.append(format.format(date));
		rpvi3053.setORB_ITEM_NUM(999999);
		tailer.append(rpvi3053.getORB_ITEM_NUM());
		rpvi3053.setORB_TAPE_COUNT(WS_TOTAL_CNT);
		tailer.append(decimalFormat.format(WS_TOTAL_CNT));
		rpvi3053.setORB_TAPE_AMT(WS_TOTAL_AMT);
		totAmt=String.format("%.2f", rpvi3053.getORB_TAPE_AMT()).replace(".", "");
		temp=new Integer(totAmt);
		tailer.append(totadecimalFormat.format(temp));	
		bw.write(tailer.toString());
		bw.newLine();
		tailer = null;
		/*
		 * rpvi3053.setORB_LRECL("080"); tailer.append(rpvi3053.getORB_LRECL());
		 * rpvi3053.setORB_BLKSIZE("0800");
		 * tailer.append(rpvi3053.getORB_BLKSIZE());
		 * 
		 * WS_TOTAL_CNT++;
		 */
	}

	/**
	 * This method is used to convert input file to Java datastructure RPVI3053
	 * 
	 * @param sCurrentLine
	 * @return
	 */
	private static RPVI3053 convertRPVI3053(String sCurrentLine) {
		RPVI3053 cpRPVI3053 = new RPVI3053();

		cpRPVI3053.setORB_HDR_TAPE(sCurrentLine.substring(64, 65).charAt(0));
		cpRPVI3053.setORB_CREATE_YYMMDD(sCurrentLine.substring(65, 71));
		cpRPVI3053.setORB_CREATE_HHMN(sCurrentLine.substring(71, 75));
		cpRPVI3053.setORB_ITEM_NUM(new Integer(sCurrentLine.substring(75, 81)));
		cpRPVI3053.setORB_CUST_ACCT_NUM(sCurrentLine.substring(81, 94));
		cpRPVI3053.setORB_PMT_DATE_YY(sCurrentLine.substring(94, 96));
		cpRPVI3053.setORB_PMT_DATE_MM(sCurrentLine.substring(96, 98));
		cpRPVI3053.setORB_PMT_DATE_DD(sCurrentLine.substring(98, 100));
		String wORB_AMT_PAID = sCurrentLine.substring(100, 108);
		String dORB_AMT_PAID = sCurrentLine.substring(108, 110);
		wORB_AMT_PAID = wORB_AMT_PAID + "." + dORB_AMT_PAID;

		cpRPVI3053.setORB_AMT_PAID(Float.valueOf(wORB_AMT_PAID));
		cpRPVI3053.setORB_TRACK_NUM_PRI(sCurrentLine.substring(110, 133));
		cpRPVI3053.setORB_TRACK_NUM_SEC(sCurrentLine.substring(133, 138));

		cpRPVI3053.setORB_PMT_TYPE(sCurrentLine.substring(138, 140));
		cpRPVI3053.setORB_NSF_INDR(sCurrentLine.substring(140, 141));
		cpRPVI3053.setORB_NSF_REASON_CODE(sCurrentLine.substring(141, 144));
		cpRPVI3053.setORB_ProcessType(sCurrentLine.substring(144, 149));
		cpRPVI3053.setORB_PMT_CONFIRMATION_NO(sCurrentLine.substring(149, 161));
		cpRPVI3053.setORB_V5INDR(sCurrentLine.substring(183, 184));

		return cpRPVI3053;
	}

	/**
	 * This method is used to convert input file to Java datastructure RPVIPKEY
	 * 
	 * @param sCurrentLine
	 * @return
	 */
	private static RPVIPKEY convertRPVI3Key(String sCurrentLine) {
		RPVIPKEY cpRPVIPKEY = new RPVIPKEY();
		cpRPVIPKEY.setPAY_DB_TEL_LINE(sCurrentLine.substring(0, 4));
		cpRPVIPKEY.setPAY_DB_TEL_NPA(sCurrentLine.substring(4, 7));
		cpRPVIPKEY.setPAY_DB_TEL_NXX(sCurrentLine.substring(7, 10));
		cpRPVIPKEY.setPAY_DB_AUDIT_ID_PRI1(sCurrentLine.substring(10, 33));
		cpRPVIPKEY.setPAY_DB_AUDIT_ID_PRI2(sCurrentLine.substring(33, 38));
		cpRPVIPKEY.setPAY_DB_STATUS_YEAR(sCurrentLine.substring(38, 42));
		cpRPVIPKEY.setPAY_DB_STATUS_MONTH(sCurrentLine.substring(43, 45));
		cpRPVIPKEY.setPAY_DB_STATUS_DAY(sCurrentLine.substring(46, 48));
		cpRPVIPKEY.setPAY_DB_STATUS_HOUR(sCurrentLine.substring(49, 51));
		cpRPVIPKEY.setPAY_DB_STATUS_MIN(sCurrentLine.substring(52, 54));
		cpRPVIPKEY.setPAY_DB_STATUS_SEC(sCurrentLine.substring(55, 57));
		cpRPVIPKEY.setPAY_DB_STATUS_MSEC(sCurrentLine.substring(58, 64));

		return cpRPVIPKEY;
	}

	/**
	 * This method is used to perform reporting function
	 * 
	 * @param rpvi3053
	 */
	private static void accumulateVZPPTotals(RPVI3053 rpvi3053) {
		if (rpvi3053.getORB_NSF_REASON_CODE() == null || rpvi3053.getORB_NSF_REASON_CODE() == "") {
			WS_VZPP_ORB_PMT_ITEMS++;
			WS_TOTAL_DTL++;
			WS_VZPP_ORB_PMT_AMT = WS_VZPP_ORB_PMT_AMT + rpvi3053.getORB_AMT_PAID();
			WS_TOTAL_AMT = WS_TOTAL_AMT + rpvi3053.getORB_AMT_PAID();
		} else {
			WS_VZPP_ORB_RTN_ITEMS++;
			WS_VZPP_ORB_RTN_AMT = WS_VZPP_ORB_RTN_AMT + rpvi3053.getORB_AMT_PAID();
			WS_TOTAL_AMT = WS_TOTAL_AMT + rpvi3053.getORB_AMT_PAID();

		}

	}

	/**
	 * This method is used to print report as output
	 * 
	 * @param bwvzppTot
	 * @param rpvipkey
	 * @throws IOException
	 */
	private static void writeVZPPTotals(BufferedWriter bwvzppTot, RPVIPKEY rpvipkey) throws IOException {
		RPVI8001 rpvi8001 = new RPVI8001();
		StringBuffer vzppTot = new StringBuffer();
		rpvi8001.setVZPP_DATE_RECEIVED(new Integer(sdf.format(c1.getTime())));
		rpvi8001.setVZPP_DATE_SENT(new Integer(sdf.format(c1.getTime())));
		rpvi8001.setVZPP_TIME_RECEIVED(new Integer(sdf.format(c1.getTime())));
		rpvi8001.setVZPP_TIME_SENT(new Integer(format.format(date)));
		vzppTot.append(sdf.format((date)));
		rpvi8001.setVZPP_PAYMENT_AMOUNT(BigDecimal.valueOf(WS_VZPP_ORB_PMT_AMT));
		vzppTot.append(String.format("%.2f", WS_VZPP_ORB_PMT_AMT).replace(".", ""));
		rpvi8001.setVZPP_PAYMENT_ITEMS((Integer)WS_VZPP_ORB_PMT_ITEMS);
		vzppTot.append(decimalFormat.format(WS_VZPP_ORB_PMT_ITEMS).replace(".", ""));
		rpvi8001.setVZPP_RETURN_AMOUNT(BigDecimal.valueOf(WS_VZPP_ORB_RTN_AMT));

		vzppTot.append(String.format("%.2f", WS_VZPP_ORB_RTN_AMT).replace(".", ""));
		rpvi8001.setVZPP_RETURN_ITEMS((Integer) WS_VZPP_ORB_RTN_ITEMS);
		vzppTot.append(decimalFormat.format(WS_VZPP_ORB_RTN_ITEMS).replace(".", ""));
		rpvi8001.setVZPP_BILLING_SYSTEM("05");
		vzppTot.append("05");
		bwvzppTot.write(vzppTot.toString());
		bwvzppTot.newLine();

	}
	private static String unpackData(byte[] packedData, int decimalPointLocation) {
	    String unpackedData = "";

	    final int negativeSign = 13;
	    for (int currentCharIndex = 0; currentCharIndex < packedData.length; currentCharIndex++) {
	        byte firstDigit = (byte) ((packedData[currentCharIndex] >>> 4) & 0x0F);
	        byte secondDigit = (byte) (packedData[currentCharIndex] & 0x0F);
	        unpackedData += String.valueOf(firstDigit);
	        if (currentCharIndex == (packedData.length - 1)) {
	            if (secondDigit == negativeSign) {
	                unpackedData = "-" + unpackedData;
	            }
	        } else {
	            unpackedData += String.valueOf(secondDigit);
	        }
	    }

	    if (decimalPointLocation > 0) {
	        int position = unpackedData.length() - decimalPointLocation;
	        unpackedData = unpackedData.substring(0, position) + "." + unpackedData.substring(position);
	    }
	    return unpackedData;
	}
	
	private static byte[] packData(String unpackedData) {
	    int unpackedDataLength = unpackedData.length();
	    final int negativeSign = 13;
	    final int positiveSign = 12;
	    if (unpackedData.charAt(0)=='-'){
	        unpackedDataLength--;
	    }

	    if (unpackedData.contains(".")){
	        unpackedDataLength--;
	    }
	    int packedLength = unpackedDataLength/2+1;

	    byte[] packed = new byte[packedLength];
	    int countPacked = 0;
	    boolean firstHex = (packedLength*2-1 == unpackedDataLength);
	    for (int i=0;i<unpackedData.length();i++){
	        if (unpackedData.charAt(i)!='-' && unpackedData.charAt(i)!='.'){
	            byte digit = Byte.valueOf(unpackedData.substring(i,i+1)); 
	            if (firstHex){
	                packed[countPacked]=(byte) (digit<<4);
	            }else{
	                packed[countPacked]=(byte) (packed[countPacked] | digit );
	                countPacked++;
	            }
	            firstHex=!firstHex;
	        }
	    }
	    if (unpackedData.charAt(0)=='-'){
	        packed[countPacked]=(byte) (packed[countPacked] | negativeSign );
	    }else{
	        packed[countPacked]=(byte) (packed[countPacked] | positiveSign );
	    }
	    return packed;
	}

}