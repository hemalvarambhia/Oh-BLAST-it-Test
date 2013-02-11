package com.bioinformaticsapp.test.functional;

import java.util.List;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

import android.test.InstrumentationTestCase;

public class RetrievingLegacyQueriesTest extends InstrumentationTestCase {

	private BLASTQueryController controller;
	private SearchParameterController searchParameterController;
	
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		
		controller = new BLASTQueryController(getInstrumentation().getTargetContext());
		searchParameterController = new SearchParameterController(getInstrumentation().getTargetContext());
		
	}
	
	protected void tearDown() throws Exception {
		searchParameterController.close();
		controller.close();
		searchParameterController = null;
		controller = null;
		super.tearDown();
		
		
	}
	
	public void testLegacyRunningQueriesAreReturned(){
		insertBLASTQueryWithStatus(Status.RUNNING);
		
		List<BLASTQuery> runningQueries = controller.getSubmittedBLASTQueries();
		
		assertEquals("Should return 'RUNNING' queries as 'SUBMITTED' ones", 1, runningQueries.size());
		
		assertEquals("RUNNING queries should become SUBMITTED", Status.SUBMITTED, runningQueries.get(0).getStatus());
		
	}
	
	private void insertBLASTQueryWithStatus(BLASTQuery.Status status){
		BLASTQuery sampleQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		sampleQuery.setStatus(status);
		
		switch(status){
		case RUNNING:
			sampleQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		}
		
		long blastQueryId = controller.save(sampleQuery);
		for(SearchParameter parameter: sampleQuery.getAllParameters()){
			parameter.setBlastQueryId(blastQueryId);			
			searchParameterController.save(parameter);
		}
		
		//controller is closed in the tearDown message so no need to do it here
		
	}
	
}
