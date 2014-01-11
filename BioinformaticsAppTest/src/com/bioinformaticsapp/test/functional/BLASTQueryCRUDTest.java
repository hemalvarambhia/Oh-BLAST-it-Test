package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.List;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryCRUDTest extends InstrumentationTestCase {

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
		
	private BLASTQuery queryAsInsertedInDatebase(){
		BLASTQuery queryInDatabase = BLASTQuery.emblBLASTQuery("blastn");
		long primaryKeyId = controller.save(queryInDatabase);
		queryInDatabase.setPrimaryKeyId(primaryKeyId);

		return queryInDatabase;				
	}
	
	public void testWeCanSaveABLASTQueryToDatabase(){
		BLASTQuery draftQuery = BLASTQuery.emblBLASTQuery("blastn");
		
		long primaryKeyId = controller.save(draftQuery);
		
		assertThat("Should be able to save a BLASTQuery", primaryKeyId > 0);
	}
	
	public void testWeCanRetrieveABLASTQueryByPrimaryKey(){
		BLASTQuery queryInDatabase = queryAsInsertedInDatebase();
		
		BLASTQuery retrievedFromDatabase = controller.findBLASTQueryById(queryInDatabase.getPrimaryKey());
		
		assertThat("Should be able to retrive a query by its ID", retrievedFromDatabase, is(queryInDatabase));
	}
	
	public void testWeCanRetrieveABLASTQueryWithDRAFTStatus(){
		BLASTQuery draft = BLASTQuery.emblBLASTQuery("blastn");
		controller.save(draft);
		
		List<BLASTQuery> draftQueries = controller.findBLASTQueriesByStatus(Status.DRAFT);
		
		BLASTQuery firstDraftQuery = draftQueries.get(0);
		assertThat("Expected a draft status",  firstDraftQuery.getStatus(), is(Status.DRAFT));
	}
	
	public void testWeCanRetrieveABLASTQueryByStatus(){
		//Default status is DRAFT
		BLASTQuery draft = BLASTQuery.emblBLASTQuery("blastn");
		controller.save(draft);
		BLASTQuery submitted = BLASTQuery.ncbiBLASTQuery("blastn");
		submitted.setStatus(Status.SUBMITTED);
		controller.save(submitted);
		
		List<BLASTQuery> submittedQueries = controller.findBLASTQueriesByStatus(Status.SUBMITTED);
		
		assertThat(submittedQueries.size(), is(1));
		BLASTQuery submittedQuery = submittedQueries.get(0);
		assertThat("Should be able to retrieve a query by its status", submittedQuery.getStatus(), is(BLASTQuery.Status.SUBMITTED));
	}
	
	private void insertBLASTQueryWithStatus(BLASTQuery.Status status){
		BLASTQuery sampleQuery = BLASTQuery.emblBLASTQuery("blastn");
		sampleQuery.setStatus(status);
		
		controller.save(sampleQuery);
	}
	
	public void testWeCanRetrieveSubmittedBLASTQueries(){
		insertBLASTQueryWithStatus(Status.DRAFT);
		insertBLASTQueryWithStatus(Status.SUBMITTED);
		
		List<BLASTQuery> runningAndSubmittedQueries = controller.getSubmittedBLASTQueries();
		
		assertThat(runningAndSubmittedQueries.size(), is(1));	
	}
	
	public void testWeCanUpdateAQuery(){
		BLASTQuery draftQuery = queryAsInsertedInDatebase();
		draftQuery.setSequence("CTAGTTTT");
		int noUpdated = controller.update(draftQuery);
		
		assertThat("Should be able to update a BLASTQuery", noUpdated, is(1));
		BLASTQuery retrieved = controller.findBLASTQueryById(draftQuery.getPrimaryKey());
		assertThat(retrieved, is(draftQuery));
	}
	
	public void testWeCanDeleteAQuery(){
		BLASTQuery query = BLASTQuery.ncbiBLASTQuery("blastn");
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		long id = helper.save(query);
		
		controller.delete(id);
		
		assertThat("Deleting a query", controller.findBLASTQueryById(id), is(nullValue()));
	}
	
}
