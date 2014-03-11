package com.bioinformaticsapp.test.functional;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
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
	
	public void testWeCanPollASUBMITTEDEMBLQuery() throws InterruptedException, ExecutionException{
		emblQuery = createPendingEMBLBLASTQuery();
		SendBLASTQuery.sendToEBIEMBL(context, emblQuery);
		
		poller.execute(emblQuery);
		
		waitFor(poller);
		
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, emblQuery.getStatus()) > -1;
		assertTrue(pollingYieldsValidStatus);
	}
	
	public void testWeCanPollASUBMITTEDNCBIQuery() throws InterruptedException, ExecutionException{
		ncbiQuery = createPendingNCBIBLASTQuery();
		SendBLASTQuery.sendToNCBI(context, ncbiQuery);
		
		poller.execute(ncbiQuery);
		
		waitFor(poller);
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, ncbiQuery.getStatus()) > -1;
		assertTrue(pollingYieldsValidStatus);
	}
	
	public void testWeCanPollMoreThanOneBLASTQuery() throws InterruptedException, ExecutionException{
		BLASTQuery[] queries = new BLASTQuery[2];
		for(int i = 0; i < 2; i++){
			queries[i] = createPendingEMBLBLASTQuery();
		}	
		SendBLASTQuery.sendToEBIEMBL(context, queries);
	
		poller.execute(queries);
		
		waitFor(poller);
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		for(BLASTQuery query: queries){
			boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, query.getStatus()) > -1;
			assertTrue("query does not have a status of SUBMITTED or FINISHED; it has a status of "+query.getStatus(), pollingYieldsValidStatus);
		} 
	}
	
	public void testWeDoNotPollWhenThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		emblQuery = createPendingEMBLBLASTQuery();
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
	
	private void waitFor(BLASTQueryPoller poller){
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("Task errored out unexpectedly");
		}
	}
	
	private BLASTQuery createPendingEMBLBLASTQuery(){
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		return emblQuery;
	}
	
	private BLASTQuery createPendingNCBIBLASTQuery(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		ncbiQuery.setStatus(BLASTQuery.Status.PENDING);
		return ncbiQuery;
	}
}
