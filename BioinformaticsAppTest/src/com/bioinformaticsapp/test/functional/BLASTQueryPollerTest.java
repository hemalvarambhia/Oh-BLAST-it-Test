package com.bioinformaticsapp.test.functional;

import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.bioinformaticsapp.test.testhelpers.SendBLASTQuery;

public class BLASTQueryPollerTest extends InstrumentationTestCase {

	private BLASTQuery emblQuery;
	private BLASTQuery ncbiQuery;
	private Context context;
	private BLASTQueryPoller poller;
	
	protected void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		EMBLEBIBLASTService emblService = new EMBLEBIBLASTService();
		NCBIBLASTService ncbiService = new NCBIBLASTService();
		poller = new BLASTQueryPoller(context, ncbiService, emblService);
	}
	
	public void testWeCanPollForTheCurrentStatusOfAnEMBLQuery() throws InterruptedException, ExecutionException{
		emblQuery = validPendingEMBLBLASTQuery();
		SendBLASTQuery.sendToEBIEMBL(context, emblQuery);
		
		poller.execute(emblQuery);
		
		waitFor(poller);
		assertStatusUpdated(emblQuery);
	}
	
	public void testWeCanPollForTheCurrentStatusOfAnNCBIQuery() throws InterruptedException, ExecutionException{
		ncbiQuery = validPendingNCBIBLASTQuery();
		SendBLASTQuery.sendToNCBI(context, ncbiQuery);
		
		poller.execute(ncbiQuery);
		
		waitFor(poller);
		assertStatusUpdated(ncbiQuery);
	}
	
	public void testWeCanPollMoreThanOneBLASTQuery() throws InterruptedException, ExecutionException{
		BLASTQuery[] queries = new BLASTQuery[2];
		for(int i = 0; i < 2; i++){
			queries[i] = validPendingEMBLBLASTQuery();
		}	
		SendBLASTQuery.sendToEBIEMBL(context, queries);
	
		poller.execute(queries);
		
		waitFor(poller);
		for(BLASTQuery query: queries){
			assertStatusUpdated(query);
		} 
	}
	
	public void testWeDoNotPollWhenThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		emblQuery = validPendingEMBLBLASTQuery();
		SendBLASTQuery.sendToEBIEMBL(context, emblQuery);
		NCBIBLASTService ncbiService = new NCBIBLASTService();
		EMBLEBIBLASTService emblService = new EMBLEBIBLASTService();
		final BLASTQueryPoller poller = new BLASTQueryPoller(context, ncbiService, emblService){
			protected boolean connectedToWeb(){
				return false;
			}
		};
		
		poller.execute(emblQuery);
		
		waitFor(poller);
		assertEquals("Expected query's status to be unchanged from when it was submitted", BLASTQuery.Status.SUBMITTED, emblQuery.getStatus());
	}
	
	private void assertStatusUpdated(BLASTQuery query){
		assertThat(query.getStatus(), anyOf(is(Status.FINISHED), is(Status.SUBMITTED)));
	}
	
	private void waitFor(BLASTQueryPoller poller){
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("Task errored out unexpectedly");
		}
	}
}
