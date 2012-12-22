package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.DraftBLASTQueriesActivity;
import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.OptionalParameter;
import com.bioinformaticsapp.test.helpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class DraftQueriesActivityTest extends ActivityInstrumentationTestCase2<DraftBLASTQueriesActivity> {

	private static final String TAG = "SetUpBLASTQueryActivityTest";
	private Solo solo;
	private Context ctx;
 	
	public DraftQueriesActivityTest() {
		super(DraftBLASTQueriesActivity.class);
		
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		ctx = getInstrumentation().getTargetContext();
		
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(ctx);
		helper.cleanDatabase();
		
	}
	
	protected void tearDown() throws Exception {
		
		if(solo!=null)
			solo.finishOpenedActivities();

		super.tearDown();

	}
	
	private void createSampleDraftQuery(int blastVendor){
		BLASTQuery sampleNCBIQuery = new BLASTQuery("blastn", blastVendor);
		BLASTQueryController queryController = new BLASTQueryController(ctx);
		OptionalParameterController parametersController = new OptionalParameterController(ctx);
		long blastQueryId = queryController.save(sampleNCBIQuery);
		for(OptionalParameter parameter: sampleNCBIQuery.getAllParameters()){
			parameter.setBlastQueryId(blastQueryId);
			parametersController.save(parameter);
		}

		parametersController.close();
		queryController.close();
	}
	
	public void testWeCanViewAllDraftQueries(){
		//This could be any query
		createSampleDraftQuery(BLASTVendor.EMBL_EBI);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		List<ListView> listViews = solo.getCurrentListViews();
		solo.waitForView(TextView.class);
		BLASTQueryController queryController = new BLASTQueryController(ctx);
		List<BLASTQuery> draftQueries = queryController.findBLASTQueriesByStatus(Status.DRAFT);
		for(ListView v: listViews){
			assertEquals("Expected "+draftQueries.size()+" draft queries but got "+v.getAdapter().getCount(), draftQueries.size(), v.getAdapter().getCount());
		}
		
		queryController.close();
		
	}
	
	public void testWeCanOpenAnNCBIQueryWhenTappingOnTheCorrespondingListItem(){
		createSampleDraftQuery(BLASTVendor.NCBI);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the NCBI Query Setup screen", NCBIQuerySetUpActivity.class);
	}
	
	public void testWeCanOpenAnEMBLQueryWhenTappingOnTheCorrespondingListItem(){
		createSampleDraftQuery(BLASTVendor.EMBL_EBI);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the EMBL Query Setup screen", EMBLEBISetUpQueryActivity.class);
	}
	
	public void testWeCanCreateAnEMBLQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		
		solo.clickOnActionBarItem(R.id.create_embl_query);
		
		solo.assertCurrentActivity("Expected the EMBL Query set up screen", EMBLEBISetUpQueryActivity.class);
		
	}
	
	public void testWeCanCreateAnNCBIQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		
		solo.clickOnActionBarItem(R.id.create_ncbi_query);
		
		solo.assertCurrentActivity("Expected the NCBI Query set up screen", NCBIQuerySetUpActivity.class);
	}
	
	public void testWeCanDeleteABLASTQuery(){
		
		createSampleDraftQuery(BLASTVendor.EMBL_EBI);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Confirm deletion
		solo.clickOnButton("OK");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		for(ListView listView : listViews){
			
			assertEquals("Should be able to delete a query", 0, listView.getAdapter().getCount());
			
		}
		
	}

	public void testWeCanCancelADeleteQueryAction(){
		createSampleDraftQuery(BLASTVendor.EMBL_EBI);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Cancel deletion
		solo.clickOnButton("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(ctx);
		List<BLASTQuery> draftQueries = queryController.findBLASTQueriesByStatus(Status.DRAFT);
		queryController.close();
		for(ListView listView : listViews){
			
			assertEquals("Should be able to cancel delete a query", draftQueries.size(), listView.getAdapter().getCount());
			
		}
	}
	
}
