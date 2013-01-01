package com.bioinformaticsapp.test.activities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.FinishedQueriesActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class FinishedQueriesActivityTest extends
		ActivityInstrumentationTestCase2<FinishedQueriesActivity> {

	private static final String TAG = "FinishedQueriesActivityTest";

	private Solo solo;
	
	private String exampleEMBLJobId = "ncbiblast-R20120418-133731-0240-81389354-pg";
	private BLASTQuery emblQuery;
	
	private String exampleNCBIJobId = "YAMJ8623016";
	private BLASTQuery ncbiQuery;
	
	public FinishedQueriesActivityTest(
			Class<FinishedQueriesActivity> activityClass) {
		super(activityClass);
		
	}
	
	public FinishedQueriesActivityTest(){
		super(FinishedQueriesActivity.class);
	}
	
	public void setUp() throws Exception {
		
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		
	}
	
	public void tearDown() throws Exception {
		
		solo.finishOpenedActivities();
		
		super.tearDown();
	}
	
	
	public void testContextMenuContainsOptionToSeeParameters(){
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		boolean menuItemShown = solo.searchText(viewParametersOption);
		
		assertTrue("Context menu with item allowing user to see parameters", menuItemShown);
		
	}

	public void testThatTappingOptionToSeeParametersShowsParametersOfSelectedEMBLQuery(){
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(emblQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
		
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
	
	public void testThatTappingOptionToSeeParametersShowsParametersOfSelectedNCBIQuery(){
		
		ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setStatus(BLASTQuery.Status.FINISHED);
		ncbiQuery.setJobIdentifier(exampleNCBIJobId);		
		saveQuery(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(ncbiQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
		
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
	
	public void testWeCanDeleteABLASTQuery() throws IOException{
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		copyBLASTHitsFileToAppDataDir(exampleEMBLJobId+".xml");
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		solo.clickOnText("OK");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		for(ListView listView : listViews){
			
			assertEquals("Should be able to delete a query", 0, listView.getAdapter().getCount());
			
		}
		
		boolean blastHitsFileDeleted = false;
		
		try{
			getInstrumentation().getTargetContext().openFileInput(exampleEMBLJobId+".xml");
		}catch(FileNotFoundException e){
			blastHitsFileDeleted = true;
		}
		
		assertTrue("The file containing the BLAST hits should be deleted", blastHitsFileDeleted);
	}
	
	public void testWeCanCancelADeleteQueryAction() throws IOException{
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		copyBLASTHitsFileToAppDataDir(exampleEMBLJobId+".xml");
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Cancel deletion
		solo.clickOnButton("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		List<BLASTQuery> finishedQueries = queryController.findBLASTQueriesByStatus(Status.FINISHED);
		queryController.close();
		for(ListView listView : listViews){
			
			assertEquals("Cancellation should not delete the query", finishedQueries.size(), listView.getAdapter().getCount());
			
		}
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
	
	private void copyBLASTHitsFileToAppDataDir(String fileName) throws IOException {
		InputStream input = getInstrumentation().getContext().getAssets().open(fileName);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input)); 
		FileOutputStream dest = getInstrumentation().getTargetContext().openFileOutput(fileName, Context.MODE_PRIVATE);
		PrintWriter writer = new PrintWriter(dest);
		
		String line = null;
		while((line = reader.readLine()) != null){
			
			writer.println(line);
		
		}
		
		writer.close();
	}
}
