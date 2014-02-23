package com.bioinformaticsapp.test.functional;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.StubbedEMBLService;
import com.bioinformaticsapp.test.testhelpers.StubbedNCBIService;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class BLASTQuerySenderUnitTest extends InstrumentationTestCase {

	private BLASTQuerySender sender;
	
	public void setUp() throws Exception {
		super.setUp();
		BLASTSearchEngine ncbiBLASTService = new StubbedNCBIService();
		BLASTSearchEngine emblBLASTService = new StubbedEMBLService();
		Context context = getInstrumentation().getTargetContext();
		sender = new BLASTQuerySender(context);
	}
	
	public void testSenderSetsTheQuerysServiceGeneratedIdentifier() {
		BLASTQuery query = aBLASTQuery();
		
		sender.execute(query);
		
	}
	
	private BLASTQuery aBLASTQuery() {
		return BLASTQuery.ncbiBLASTQuery("blastn");
	}

	public void testSenderUpdatesTheQueriesStatus() {
		
	}
}
