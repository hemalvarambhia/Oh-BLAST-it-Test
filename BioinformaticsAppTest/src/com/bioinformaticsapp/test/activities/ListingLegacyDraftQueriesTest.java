package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.DraftBLASTQueriesActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

import android.content.Context;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

public class ListingLegacyDraftQueriesTest extends
		ActivityInstrumentationTestCase2<DraftBLASTQueriesActivity> {

	private BLASTQuery legacy;
	private Solo solo;
	private Context context;
	
	public ListingLegacyDraftQueriesTest(){
		super(DraftBLASTQueriesActivity.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		legacy = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		legacy.setJobIdentifier("TODO");
		legacy.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		List<SearchParameter> legacyParameters = new ArrayList<SearchParameter>();
		Resources resources = getInstrumentation().getTargetContext().getResources();
		String[] databases = resources.getStringArray(com.bioinformaticsapp.R.array.blast_database_options);
		legacyParameters.add(new SearchParameter("database", databases[0]));
		legacyParameters.add(new SearchParameter("exp_threshold", "1e-200"));
		legacyParameters.add(new SearchParameter("score", "5"));
		legacy.updateAllParameters(legacyParameters);
		
		helper.save(legacy);
		
	}
	
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		legacy = null;
		super.tearDown();
	}
	
	public void testWeCanDeleteABLASTQuery(){
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Confirm deletion
		solo.clickOnButton("OK");
		solo.waitForView(ListView.class);
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		for(ListView listView : listViews){
			
			assertEquals("Should be able to delete a query", 0, listView.getAdapter().getCount());
			
		}
		
	}

	public void testWeCanCancelADeleteQueryAction(){
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Cancel deletion
		solo.clickOnButton("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(context);
		List<BLASTQuery> draftQueries = queryController.findBLASTQueriesByStatus(Status.DRAFT);
		queryController.close();
		for(ListView listView : listViews){
			
			assertEquals("Should be able to cancel delete a query", draftQueries.size(), listView.getAdapter().getCount());
			
		}
	}
	
	public void testWeCanViewTheParametersOfAnEMBLQuery(){
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Search parameters activity should show", BLASTQuerySearchParametersActivity.class);

	}

	
	
}
