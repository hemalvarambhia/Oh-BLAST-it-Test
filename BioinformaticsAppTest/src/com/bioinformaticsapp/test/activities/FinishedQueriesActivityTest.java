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
		saveQuery(emblQuery);
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

	public void testThatTappingOptionToSeeParametersShowsParametersDialog(){
		
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		boolean dialogShown = solo.searchText("Program");
		
		assertTrue("BLAST query parameters dialog", dialogShown);
		
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
