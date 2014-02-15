package com.bioinformaticsapp.test.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.PendingQueriesActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class PendingQueriesActivityTest extends
		ActivityInstrumentationTestCase2<PendingQueriesActivity> {

	
	private static final String TAG = "PendingQueriesActivityTest";
	
	private Solo solo;
	
	public PendingQueriesActivityTest(){
		super(PendingQueriesActivity.class);
	}

	public void setUp() throws Exception{
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		
	}
	
	public void tearDown() throws Exception {
		
		solo.finishOpenedActivities();
		
		super.tearDown();
	}
	
	public void testThatLongTappingAPendingQueryShowsUserAViewParametersOption(){
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setStatus(Status.SUBMITTED);
		query.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");		
		query.setSearchParameter("email", "example@email.com");
		saveQuery(query);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		boolean menuItemShown = solo.searchText(viewParametersOption);
		
		assertTrue("Context menu with item allowing user to see parameters", menuItemShown);
	
	}
	
	public void testThatWeCanSeeTheParametersOfSelectedEMBLQuery(){	
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(Status.SUBMITTED);
		emblQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");		
		emblQuery.setSearchParameter("email", "example@email.com");
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Shows the search parameters activity", BLASTQuerySearchParametersActivity.class);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(emblQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
		
			
	}
	
	public void testThatWeCanSeeTheParametersOfSelectedNCBIQuery(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setStatus(Status.SUBMITTED);
		ncbiQuery.setJobIdentifier("YAMJ8623016");		
		saveQuery(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Shows the search parameters activity", BLASTQuerySearchParametersActivity.class);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(ncbiQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
			
	}
	
	public void testWeCanDeleteABLASTQuery() throws IOException{
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setStatus(Status.SUBMITTED);
		query.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");		
		query.setSearchParameter("email", "example@email.com");
		saveQuery(query);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		solo.clickOnText("OK");
		solo.waitForView(ListView.class);
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		for(ListView listView : listViews){
			
			assertEquals("Should be able to delete a query", 0, listView.getAdapter().getCount());
			
		}
	}
	
	public void testWeCanCancelADelete() throws IOException{
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setStatus(Status.SUBMITTED);
		query.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");		
		query.setSearchParameter("email", "example@email.com");
		saveQuery(query);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		solo.clickOnText("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		List<BLASTQuery> finishedQueries = queryController.getSubmittedBLASTQueries();
		queryController.close();
		
		for(ListView listView : listViews){
			
			assertEquals("Cancellation should not delete the query", finishedQueries.size(), listView.getAdapter().getCount());
			
		}
		
	}

	private long saveQuery(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		BLASTQuery saved = labBook.save(query);
		return saved.getPrimaryKey();
	}
	
}
