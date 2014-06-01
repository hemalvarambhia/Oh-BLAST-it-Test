package com.bioinformaticsapp.test.functional;

import java.util.List;

import android.test.InstrumentationTestCase;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.persistence.BLASTQueryLabBook;

import static com.bioinformaticsapp.models.BLASTQuery.Status.*;
import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;
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
		assertThat("Should return 'RUNNING' queries as 'SUBMITTED' ones", runningQueries.size(), is(1));
		assertThat("RUNNING queries should become SUBMITTED", runningQueries.get(0).getStatus(), is(SUBMITTED));
	}
	
	private void insertRunningBLASTQuery(){
		BLASTQuery sampleQuery = aBLASTQuery();
		sampleQuery.setStatus(RUNNING);
		sampleQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		labBook.save(sampleQuery);
	}
	
}
