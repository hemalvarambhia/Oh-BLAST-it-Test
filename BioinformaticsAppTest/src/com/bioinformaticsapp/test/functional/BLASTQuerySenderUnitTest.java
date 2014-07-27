package com.bioinformaticsapp.test.functional;

import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.aBLASTQueryWithStatus;
import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.aValidPendingBLASTQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.bioinformaticsapp.test.testhelpers.StubbedBLASTSearchEngine;

public class BLASTQuerySenderUnitTest extends InstrumentationTestCase {

	private BLASTQuerySender sender;
	
	public void setUp() throws Exception {
		super.setUp();
		BLASTSearchEngine ncbiBLASTService = new StubbedBLASTSearchEngine();
		Context context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		sender = new BLASTQuerySender(context, ncbiBLASTService);
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
