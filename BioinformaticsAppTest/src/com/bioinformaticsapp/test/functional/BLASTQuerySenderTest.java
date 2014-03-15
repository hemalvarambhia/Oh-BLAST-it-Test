package com.bioinformaticsapp.test.functional;

import static com.bioinformaticsapp.test.testhelpers.SendBLASTQuery.*;
import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

/**
 * Here we will set the status of the queries to <code>PENDING</code>
 * as the BLAST query sender will send pending query provided from 
 * elsewhere
 * @author Hemal N Varambhia
 *
 */

public class BLASTQuerySenderTest extends InstrumentationTestCase {

	private Context context;
	
	protected void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();	
	}
	
	
	public void testWeCanSendAnNCBIQuery() throws InterruptedException, ExecutionException{
		BLASTQuery ncbiQuery = validPendingNCBIBLASTQuery();
		
		sendToNCBI(context, new BLASTQuery[]{ncbiQuery});					
		
		assertSent(ncbiQuery);
	}
	
	public void testWeCanSendAnEBI_EMBLQuery() throws InterruptedException, ExecutionException{
		BLASTQuery ebiemblQuery = validPendingEMBLBLASTQuery();
		
		sendToEBIEMBL(context, new BLASTQuery[]{ebiemblQuery});					
		
		assertSent(ebiemblQuery);
	}
	
	private void assertSent(BLASTQuery query){
		assertThat("Query was not assigned a job identifier", query.getJobIdentifier(), is(notNullValue()));
		assertThat("Job identifier was found to be blank", !(query.getJobIdentifier().isEmpty()));
		assertThat("Query wasn't submitted", query.getStatus(), is(Status.SUBMITTED));
	}
}
