package com.bioinformaticsapp.test.functional;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNull.*;

import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;

import java.util.concurrent.ExecutionException;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.BLASTQuery.Status;
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
		sender = new BLASTQuerySender(context, ncbiBLASTService, emblBLASTService);
	}
	
	public void testSenderDoesNotSendInvalidQuery() throws InterruptedException, ExecutionException{
		BLASTQuery query = anInvalidPendingBLASTQuery();
		
		sender.execute(query);
		
		Integer numberSent = sender.get();
		
		assertThat("Expected no query to be send", numberSent, is(0));
		assertThat("The BLAST query's status should be draft", query.getStatus(), is(Status.DRAFT));
	}
	
	public void testSenderSendsAValidQuery() throws InterruptedException, ExecutionException {
		BLASTQuery query = aValidPendingBLASTQuery();
		
		sender.execute(query);
		
		Integer numberSent = sender.get();
		assertThat("Expected the number of queries sent to be more than 1", numberSent, is(1));
		assertThat("The BLAST query's identifier should be set", query.getJobIdentifier(), is(not(nullValue())));
	}
	
	private BLASTQuery anInvalidPendingBLASTQuery() {
		BLASTQuery aBLASTQuery = aBLASTQueryWithStatus(Status.PENDING);
		aBLASTQuery.setSequence("");
		switch(aBLASTQuery.getVendorID()){
		case BLASTVendor.EMBL_EBI:
			aBLASTQuery.setSearchParameter("email", "user@@email.com");
			break;
		}
		
		return aBLASTQuery;
	}
}
