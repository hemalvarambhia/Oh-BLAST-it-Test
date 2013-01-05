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
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
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
		SearchParameterController parametersController = new SearchParameterController(ctx);
		long blastQueryId = queryController.save(sampleNCBIQuery);
		for(SearchParameter parameter: sampleNCBIQuery.getAllParameters()){
			parameter.setBlastQueryId(blastQueryId);
			parametersController.save(parameter);
		}

		parametersController.close();
		queryController.close();
	}
	
	private long saveQuery(BLASTQuery query){
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		long primaryKey = queryController.save(query);
		query.setPrimaryKeyId(primaryKey);
		List<SearchParameter> parameters = query.getAllParameters();
		List<SearchParameter> newSetOfParameters = new ArrayList<SearchParameter>();
		SearchParameterController parametersControllers = new SearchParameterController(getInstrumentation().getTargetContext());
		for(SearchParameter parameter: parameters){
			parameter.setBlastQueryId(query.getPrimaryKey());
			long parameterPrimaryKey = parametersControllers.save(parameter);
			parameter.setPrimaryKey(parameterPrimaryKey);
			newSetOfParameters.add(parameter);
		}
		
		query.updateAllParameters(newSetOfParameters);
		parametersControllers.close();
		queryController.close();
		return primaryKey;
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
	
	public void testWeCanViewTheParametersOfAnEMBLQuery(){
		
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		boolean hasATitle = solo.searchText("BLAST Query Parameters");

		assertTrue("There should be an appropriate title", hasATitle);
		
		boolean programLabelShown = solo.searchText("Program");
		
		assertTrue("Should see the program label", programLabelShown);
		
		boolean valueOfProgramShown = solo.searchText(emblQuery.getBLASTProgram());
		
		assertTrue("Should see the value of the program parameter", valueOfProgramShown);
		
		boolean databaseLabelShown = solo.searchText("Program");
		
		assertTrue("Should see the database label", databaseLabelShown);
		
		boolean valueOfDatabaseShown = solo.searchText(emblQuery.getSearchParameter("database").getValue());
		
		assertTrue("Should see the value of the database parameter", valueOfDatabaseShown);
		
		boolean expThresholdLabelShown = solo.searchText("Exp. Threshold");
		
		assertTrue("Should see the exp threshold label", expThresholdLabelShown);
		
		boolean valueOfExpThresholdShown = solo.searchText(emblQuery.getSearchParameter("exp_threshold").getValue());
		
		assertTrue("Should see the value of the exp. threshold parameter", valueOfExpThresholdShown);
		
		boolean scoreLabelShown = solo.searchText("Score");
		
		assertTrue("Should see the score label", scoreLabelShown);
		
		boolean valueOfScoreShown = solo.searchText(emblQuery.getSearchParameter("score").getValue());
		
		assertTrue("Should see the value of the score parameter", valueOfScoreShown);
		
		boolean emailLabelShown = solo.searchText("E-mail");
		
		assertTrue("Should see the email label", emailLabelShown);
		
		boolean valueOfEmailShown = solo.searchText(emblQuery.getSearchParameter("email").getValue());
		
		assertTrue("Should see the value of the e-mail parameter", valueOfEmailShown);
		
	}

	public void testThatWeCanViewTheParametersOfAnNCBIQuery(){
		
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		saveQuery(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		boolean hasATitle = solo.searchText("BLAST Query Parameters");
		
		assertTrue("Title is the job identifier", hasATitle);
		
		boolean programLabelShown = solo.searchText("Program");
		
		assertTrue("Should see the program label", programLabelShown);
		
		boolean valueOfProgramShown = solo.searchText(ncbiQuery.getBLASTProgram());
		
		assertTrue("Should see the value of the program parameter", valueOfProgramShown);
		
		boolean databaseLabelShown = solo.searchText("Database");
		
		assertTrue("Should see the database label", databaseLabelShown);
		
		boolean valueOfDatabaseShown = solo.searchText(ncbiQuery.getSearchParameter("database").getValue());
		
		assertTrue("Should see the value of the database parameter", valueOfDatabaseShown);
		
		boolean expThresholdLabelShown = solo.searchText("Exp. Threshold");
		
		assertTrue("Should see the exp threshold label", expThresholdLabelShown);
		
		boolean valueOfExpThresholdShown = solo.searchText(ncbiQuery.getSearchParameter("exp_threshold").getValue());
		
		assertTrue("Should see the value of the exp. threshold parameter", valueOfExpThresholdShown);
		
		boolean wordSizeLabelShown = solo.searchText("Word Size");
		
		assertTrue("Should see the word size label", wordSizeLabelShown);
		
		boolean valueOfWordSizeShown = solo.searchText(ncbiQuery.getSearchParameter("word_size").getValue());
		
		assertTrue("Should see the value of the word size parameter", valueOfWordSizeShown);
		
		boolean matchMismatchScoreLabelShown = solo.searchText("Match/Mis-Match Score");
		
		assertTrue("Should see the word size label", matchMismatchScoreLabelShown);
		
		boolean valueOfMatchMisMatchScoreShown = solo.searchText(ncbiQuery.getSearchParameter("match_mismatch_score").getValue());
		
		assertTrue("Should see the value of the match mis-match score parameter", valueOfMatchMisMatchScoreShown);
		
	}
}
