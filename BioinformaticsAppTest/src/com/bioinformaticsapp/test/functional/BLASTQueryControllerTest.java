package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;

import java.util.List;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryControllerTest extends InstrumentationTestCase {

	private BLASTQueryController controller;
	
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		controller = new BLASTQueryController(getInstrumentation().getTargetContext());
	}
	
	protected void tearDown() throws Exception {
		controller.close();
		super.tearDown();	
	}
	
	public void testWeCanSaveABLASTQuery(){
		BLASTQuery draftQuery = aBLASTQuery();
		
		long primaryKeyId = controller.save(draftQuery);
		
		assertThat("Should be able to save a BLASTQuery", primaryKeyId > 0);
	}
	
	public void testWeCanRetrieveABLASTQueryByPrimaryKey(){
		BLASTQuery queryInDatabase = savedQuery();
		
		BLASTQuery retrievedFromDatabase = controller.findBLASTQueryById(queryInDatabase.getPrimaryKey());
		
		assertThat("Should be able to retrive a query by its ID", retrievedFromDatabase, is(queryInDatabase));
	}
	
	public void testWeCanRetrieveABLASTQueryWithDRAFTStatus(){
		aBLASTQueryWithStatus(Status.DRAFT);
		
		List<BLASTQuery> draftQueries = controller.findBLASTQueriesByStatus(Status.DRAFT);
		
		assertContainsOnly(Status.DRAFT, draftQueries);
	}
	
	public void testWeCanRetrieveABLASTQueryByStatus(){
		aBLASTQueryWithStatus(Status.DRAFT);
		aBLASTQueryWithStatus(Status.SUBMITTED);
		
		List<BLASTQuery> submittedQueries = controller.findBLASTQueriesByStatus(Status.SUBMITTED);
		
		assertContainsOnly(Status.SUBMITTED, submittedQueries);
	}
	
	public void testWeCanRetrieveSubmittedBLASTQueries(){
		aBLASTQueryWithStatus(Status.DRAFT);
		aBLASTQueryWithStatus(Status.SUBMITTED);
		
		List<BLASTQuery> runningAndSubmittedQueries = controller.getSubmittedBLASTQueries();
		
		assertContainsOnly(Status.SUBMITTED, runningAndSubmittedQueries);
	}
	
	public void testWeCanUpdateAQuery(){
		BLASTQuery draftQuery = savedQuery();
		draftQuery.setSequence("CTAGTTTT");
		
		int noUpdated = controller.update(draftQuery);
		
		assertThat("Should be able to update a BLASTQuery", noUpdated, is(1));
		BLASTQuery retrieved = controller.findBLASTQueryById(draftQuery.getPrimaryKey());
		assertThat(retrieved, is(draftQuery));
	}
	
	public void testWeCanDeleteAQuery(){
		BLASTQuery query = aBLASTQuery();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		long id = helper.save(query);
		
		controller.delete(id);
		
		assertThat("Deleting a query", controller.findBLASTQueryById(id), is(nullValue()));
	}
	
	private void assertContainsOnly(Status expectedStatus, List<BLASTQuery> queries){
		for(BLASTQuery query: queries)
			assertThat("Should be able to retrieve a query by its status", query.getStatus(), is(expectedStatus));
	}
	
	private BLASTQuery savedQuery(){
		BLASTQuery queryInDatabase = aBLASTQuery();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		long primaryKeyId = helper.save(queryInDatabase);
		queryInDatabase.setPrimaryKeyId(primaryKeyId);
		
		return queryInDatabase;				
	}
	
	private void aBLASTQueryWithStatus(BLASTQuery.Status status){
		BLASTQuery blastQuery = aBLASTQuery();
		blastQuery.setStatus(status);
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.save(blastQuery);
	}
}
