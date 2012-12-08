package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import com.bioinformaticsapp.FinishedQueriesActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.OptionalParameter;
import com.jayway.android.robotium.solo.Solo;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;

public class FinishedQueriesActivityTest extends
		ActivityInstrumentationTestCase2<FinishedQueriesActivity> {

	private static final String TAG = "FinishedQueriesActivityTest";

	private Solo solo;
	
	private BLASTQuery emblQuery;
	
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
		cleanDatabase();
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier("EXAM123PLE_JOB_1D");		
		emblQuery.setSearchParameter("email", "example@email.com");
		saveQuery(emblQuery);
		
		ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setStatus(BLASTQuery.Status.FINISHED);
		ncbiQuery.setJobIdentifier("ANOTHER_EXAM123PLE_JOB_1D");		
		saveQuery(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
	}
	
	public void tearDown() throws Exception {
		
		solo.finishOpenedActivities();
		
		super.tearDown();
	}
	
	
	public void testContextMenuContainsOptionToSeeParameters(){
		
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		boolean menuItemShown = solo.searchText(viewParametersOption);
		
		assertTrue("Context menu with item allowing user to see parameters", menuItemShown);
		
	}

	public void testThatTappingOptionToSeeParametersShowsParametersOfSelectedEMBLQuery(){
		
		
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
		
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(2);
		
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
	
	
	private void cleanDatabase(){
		DatabaseHelper helper = new DatabaseHelper(getInstrumentation().getTargetContext());
		
		//Create the database if it does not exist already, or open it if it does
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if(db.delete(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" deleted");
		}else{
			Log.i(TAG, BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" already clean");
		}
		
		if(db.delete(BLASTQuery.BLAST_QUERY_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_QUERY_TABLE+" deleted");
		}else{

			Log.i(TAG, BLASTQuery.BLAST_QUERY_TABLE+" already clean");
		}
	
		db.close();
		
	}
	
	private long saveQuery(BLASTQuery query){
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		long primaryKey = queryController.save(query);
		query.setPrimaryKeyId(primaryKey);
		List<OptionalParameter> parameters = query.getAllParameters();
		List<OptionalParameter> newSetOfParameters = new ArrayList<OptionalParameter>();
		OptionalParameterController parametersControllers = new OptionalParameterController(getInstrumentation().getTargetContext());
		for(OptionalParameter parameter: parameters){
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
	
}
