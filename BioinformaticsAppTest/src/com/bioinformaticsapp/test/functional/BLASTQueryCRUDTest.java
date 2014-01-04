package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.List;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryCRUDTest extends InstrumentationTestCase {

	private static final String TAG = "BLASTQueryCRUDTest";
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
		super.tearDown();	
	}
		
	private BLASTQuery queryAsInsertedInDatebase(){
		BLASTQuery queryInDatabase = BLASTQuery.emblBLASTQuery("blastn");
		long primaryKeyId = controller.save(queryInDatabase);
		queryInDatabase.setPrimaryKeyId(primaryKeyId);

		return queryInDatabase;				
	}

	
	public void testWeCanSaveABLASTQueryToDatabase(){
		BLASTQuery draftQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		long primaryKeyId = controller.save(draftQuery);
		
		assertThat("Should be able to save a BLASTQuery", primaryKeyId > 0);
	}
	
	public void testWeCanSaveTheSearchParametersOfAQuery(){
		long blastQueryId = 1l;
		SearchParameter parameter = new SearchParameter("email", "h.n.varambhia@gmail.com");
		
		searchParameterController.saveFor(blastQueryId, parameter);
		
		SearchParameter parameterFromDatastore = searchParameterController.getParametersForQuery(blastQueryId).get(0);
		assertThat("Should be able store a SearchParameter in datastore", parameterFromDatastore, is(parameter));
	}

	public void testWeCanRetrieveTheOptionalParametersOfAQuery(){
		BLASTQuery query = queryAsInsertedInDatebase();
		
		List<SearchParameter> parameters = searchParameterController.getParametersForQuery(query.getPrimaryKey());
		
		List<SearchParameter> expectedParameters = query.getAllParameters();
		boolean sameSize = (parameters.size() == expectedParameters.size());
		assertTrue(parameters.containsAll(expectedParameters) && sameSize);
	}
	
	public void testWeCanRetrieveABLASTQueryByPrimaryKey(){
		BLASTQuery queryInDatabase = queryAsInsertedInDatebase();
		
		BLASTQuery retrievedFromDatabase = controller.findBLASTQueryById(queryInDatabase.getPrimaryKey());
		assertThat("Should be able to retrive a query by its ID", retrievedFromDatabase, is(queryInDatabase));
	
	}
	
	public void testWeCanRetrieveABLASTQueryWithDRAFTStatus(){
		//Default status is DRAFT so no need to explicitly set status to DRAFT
		BLASTQuery draft = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		long primaryKey = controller.save(draft);
		
		List<BLASTQuery> draftQueries = controller.findBLASTQueriesByStatus(Status.DRAFT);
		BLASTQuery firstDraftQuery = draftQueries.get(0);
		
		assertEquals("Expected primary key", primaryKey, firstDraftQuery.getPrimaryKey().longValue());
		
	}
	
	public void testWeCanRetrieveABLASTQueryByStatus(){
		//Default status is DRAFT so no need to explicitly set status to DRAFT
		BLASTQuery draft = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		controller.save(draft);
		
		BLASTQuery finished = new BLASTQuery("blastn", BLASTVendor.NCBI);
		finished.setStatus(Status.FINISHED);
		controller.save(finished);
		
		BLASTQuery submitted = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		submitted.setStatus(Status.SUBMITTED);
		long expectedPrimaryKey = controller.save(submitted);
		
		List<BLASTQuery> submittedQueries = controller.findBLASTQueriesByStatus(Status.SUBMITTED);
		BLASTQuery firstSubmittedQuery = submittedQueries.get(0);
		assertTrue(submittedQueries.size() == 1);
		
		assertEquals(expectedPrimaryKey, firstSubmittedQuery.getPrimaryKey().longValue());
		
	}
	
	private void insertBLASTQueryWithStatus(BLASTQuery.Status status){
		BLASTQuery sampleQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		sampleQuery.setStatus(status);
		
		long blastQueryId = controller.save(sampleQuery);
		for(SearchParameter parameter: sampleQuery.getAllParameters()){
			parameter.setBlastQueryId(blastQueryId);			
			searchParameterController.save(parameter);
		}
		
		//controller is closed in the tearDown message so no need to do it here
		
	}
	
	public void testWeCanRetrieveSubmittedBLASTQueries(){
		insertBLASTQueryWithStatus(Status.DRAFT);
		insertBLASTQueryWithStatus(Status.SUBMITTED);
		
		List<BLASTQuery> runningAndSubmittedQueries = controller.getSubmittedBLASTQueries();
		
		assertEquals(1, runningAndSubmittedQueries.size());
		
	}
	
	public void testWeCanUpdateAQuery(){
		BLASTQuery draftQuery = queryAsInsertedInDatebase();
		BLASTQuery retrieved = controller.findBLASTQueryById(draftQuery.getPrimaryKey());
		List<SearchParameter> parameters = searchParameterController.getParametersForQuery(retrieved.getPrimaryKey());
		retrieved.updateAllParameters(parameters);
		retrieved.setSearchParameter("match_mismatch_score", "1, -1");
		retrieved.setSearchParameter("exp_threshold", "0.1");
		
		int noUpdated = controller.update(retrieved.getPrimaryKey(), retrieved);
		
		for(SearchParameter parameter: retrieved.getAllParameters()){
			noUpdated += searchParameterController.update(parameter.getPrimaryKey(), parameter);
		}
		
		assertEquals(retrieved.getAllParameters().size() + 1, noUpdated);
		
	}
	
	public void testWeCanDeleteAQuery(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		
		long id = helper.save(query);
		
		controller.delete(id);
		
		assertTrue("Deleting a query", controller.findBLASTQueryById(id) == null);
		
		searchParameterController.deleteParametersFor(id);
		
		assertTrue("No search parameters for the query", searchParameterController.getParametersForQuery(id) == null);
		
		
	}
	
}
