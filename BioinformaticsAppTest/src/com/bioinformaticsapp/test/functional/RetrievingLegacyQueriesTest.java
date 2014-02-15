package com.bioinformaticsapp.test.functional;

import java.util.List;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class RetrievingLegacyQueriesTest extends InstrumentationTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();	
	}
	
	public void testLegacyRunningQueriesAreReturned(){
		insertRunningBLASTQuery();
		BLASTQueryController controller = new BLASTQueryController(getInstrumentation().getTargetContext());
		List<BLASTQuery> runningQueries = controller.getSubmittedBLASTQueries();
		controller.close();
		assertEquals("Should return 'RUNNING' queries as 'SUBMITTED' ones", 1, runningQueries.size());
		assertEquals("RUNNING queries should become SUBMITTED", Status.SUBMITTED, runningQueries.get(0).getStatus());
	}
	
	private void insertRunningBLASTQuery(){
		BLASTQuery sampleQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		sampleQuery.setStatus(Status.RUNNING);
		sampleQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		labBook.save(sampleQuery);
	}
	
}
