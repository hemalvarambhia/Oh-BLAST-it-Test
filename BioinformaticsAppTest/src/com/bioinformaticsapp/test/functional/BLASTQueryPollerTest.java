package com.bioinformaticsapp.test.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.bioinformaticsapp.web.BLASTQueryPoller;
import com.bioinformaticsapp.web.BLASTQuerySender;

public class BLASTQueryPollerTest extends InstrumentationTestCase {

	private BLASTQuery emblQuery;
	private BLASTQuery ncbiQuery;
	private Context context;
	private BLASTQuerySender sender;
	private BLASTQueryPoller poller;
	private final static String TAG = "BLASTQueryPollerTest";
	
	protected void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		
		save(emblQuery);
		
		ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		ncbiQuery.setStatus(BLASTQuery.Status.PENDING);
		
		save(ncbiQuery);
		
		sender = new BLASTQuerySender(context);
		poller = new BLASTQueryPoller(context);
		
	}
	
	protected void tearDown() throws Exception {
		sender = null;
		poller = null;
		ncbiQuery = null;
		emblQuery = null;
		context = null;
		super.tearDown();
	}

	private void save(BLASTQuery query){
		BLASTQueryController queryController = new BLASTQueryController(context);
		SearchParameterController parameterController = new SearchParameterController(context);
		long queryPrimaryKey = queryController.save(query);
		query.setPrimaryKeyId(queryPrimaryKey);
		List<SearchParameter> parameters = new ArrayList<SearchParameter>();
		for(SearchParameter parameter: query.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			long parameterPrimaryKey = parameterController.save(parameter);
			parameter.setPrimaryKey(parameterPrimaryKey);
			parameters.add(parameter);
		}
		
		query.updateAllParameters(parameters);
		
		parameterController.close();
		queryController.close();
	}
	
	private void waitUntilSent(BLASTQuery... queries) throws InterruptedException, ExecutionException{
		sender.execute(queries);
		
		sender.get();
		
		
	}
	
	public void testWeCanPollASUBMITTEDEMBLQuery() throws InterruptedException, ExecutionException{
		
		waitUntilSent(emblQuery);
		
		poller.execute(emblQuery);
		
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("There was a problem in poller the query");
		}
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, emblQuery.getStatus()) > -1;
		assertTrue(pollingYieldsValidStatus);
		
	}
	
	public void testWeCanPollASUBMITTEDNCBIQuery() throws InterruptedException, ExecutionException{
		
		waitUntilSent(ncbiQuery);
		
		poller.execute(ncbiQuery);
		
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("There was a problem in polling the query");
		}
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, ncbiQuery.getStatus()) > -1;
		assertTrue(pollingYieldsValidStatus);
		
	}
	
	public void testWeCanPollMoreThanOneBLASTQuery() throws InterruptedException, ExecutionException{
		
		BLASTQuery[] queries = new BLASTQuery[2];
		
		for(int i = 0; i < 2; i++){
			queries[i] = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
			queries[i].setSearchParameter("email", "h.n.varambhia@gmail.com");
			queries[i].setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
			queries[i].setStatus(BLASTQuery.Status.PENDING);
			
			save(queries[i]);
			
			
		}
		
		waitUntilSent(queries);
		
		
		poller.execute(queries);
		
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("There was a problem in poller the query");
		}
		BLASTQuery.Status[] possibilities = new BLASTQuery.Status[] { BLASTQuery.Status.SUBMITTED, BLASTQuery.Status.FINISHED };
		
		for(BLASTQuery query: queries){
			boolean pollingYieldsValidStatus = Arrays.binarySearch(possibilities, query.getStatus()) > -1;
			assertTrue("query does not have a status of SUBMITTED or FINISHED; it has a status of "+query.getStatus(), pollingYieldsValidStatus);
		} 
		
	}
	
	public void testWeDoNotPollWhenThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		
		waitUntilSent(emblQuery);
		
		final BLASTQueryPoller poller = new BLASTQueryPoller(context){
			protected boolean connectedToWeb(){
				return false;
			}
		};
		
		
		poller.execute(emblQuery);
		
		try {
			poller.get();
		} catch (InterruptedException e) {
			fail("Task was interrupted unexpectedly");
		} catch (ExecutionException e) {
			fail("Task errored out unexpectedly");
		}
		
		assertEquals("Expected query's status to be unchanged from when it was submitted", BLASTQuery.Status.SUBMITTED, emblQuery.getStatus());
		
	}
	
	
}
