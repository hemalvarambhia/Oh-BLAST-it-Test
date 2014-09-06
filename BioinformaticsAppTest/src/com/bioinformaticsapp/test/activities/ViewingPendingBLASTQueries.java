package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

import java.io.IOException;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.ListPendingBLASTQueries;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.ViewBLASTQuerySearchParameters;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class ViewingPendingBLASTQueries extends
		ActivityInstrumentationTestCase2<ListPendingBLASTQueries> {
	
	private Solo solo;
	private BLASTQuery query;
	
	public ViewingPendingBLASTQueries(){
		super(ListPendingBLASTQueries.class);
	}

	public void setUp() throws Exception{
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();		
		query = BLASTQueryBuilder.aBLASTQueryWithStatus(Status.SUBMITTED);
		query.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");		
		helper.save(query);
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		solo.waitForView(TextView.class);
	}
	
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
	
	public void testThatLongTappingAPendingQueryShowsUserAViewParametersOption(){
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		boolean menuItemShown = solo.searchText(viewParametersOption);
		assertThat("Context menu with item allowing user to see parameters", menuItemShown);
	}
	
	public void testThatWeCanSeeTheParametersOfSelectedEMBLQuery(){	
		solo.clickLongInList(1);
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Shows the search parameters activity", ViewBLASTQuerySearchParameters.class);
		boolean jobIdIsShownAsTitle = solo.searchText(query.getJobIdentifier());
		assertThat("Title is the job identifier", jobIdIsShownAsTitle);
	}
	
	public void testWeCanDeleteABLASTQuery() throws IOException{
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnText("OK");
		solo.waitForView(ListView.class);
		
		ListView listView = listOfBLASTQueries();
		assertThat("Should be able to delete a query", listView.getAdapter().isEmpty());
	}

	public void testWeCanCancelADelete() throws IOException{
		solo.clickLongInList(1);
		String delete = "Delete";
		solo.clickOnText(delete);
		solo.clickOnText("Cancel");
		solo.waitForView(ListView.class);
		
		ListView listView = listOfBLASTQueries();
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		List<BLASTQuery> finishedQueries = labBook.findBLASTQueriesByStatus(Status.SUBMITTED);
		assertThat("Cancellation should not delete the query", listView.getAdapter().getCount(), is(finishedQueries.size()));
	}
	
	private ListView listOfBLASTQueries() {
		return solo.getCurrentListViews().get(0);
	}
}
