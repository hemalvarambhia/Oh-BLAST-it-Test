package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class EBIEMBLLoadLegacyBLASTQueryTest extends
		ActivityInstrumentationTestCase2<EMBLEBISetUpQueryActivity> {

	private BLASTQuery legacy;
	
	private Solo solo;
	
	public EBIEMBLLoadLegacyBLASTQueryTest() {
		super(EMBLEBISetUpQueryActivity.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		legacy = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		List<SearchParameter> legacyParameters = new ArrayList<SearchParameter>();
		Resources resources = getInstrumentation().getTargetContext().getResources();
		String[] databases = resources.getStringArray(com.bioinformaticsapp.R.array.blast_database_options);
		legacyParameters.add(new SearchParameter("database", databases[0]));
		legacyParameters.add(new SearchParameter("exp_threshold", "1e-200"));
		legacyParameters.add(new SearchParameter("score", "5"));
		legacy.updateAllParameters(legacyParameters);
		
		
	}
	
	public void tearDown() throws Exception {
		legacy = null;
		solo.finishOpenedActivities();
		super.tearDown();
		
	}
	
	public void testWeCanLoadLegacyDraftQueries(){
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", legacy);
		setActivityIntent(intentWithLegacyQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.assertCurrentActivity("Should be able to load a legacy query", EMBLEBISetUpQueryActivity.class);
		
	}

	public void testWeCanEditTheMatchMisMatchScoreOfADraftLegacyQuery(){
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", legacy);
		setActivityIntent(intentWithLegacyQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(4, 2);
		getInstrumentation().waitForIdleSync();
		Spinner matchMismatchScoreSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ebi_match_mismatch_score_spinner);
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals(matchMismatchScoreSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
		
		
	}
	
	public void testWeCanEditTheEmailOfADraftLegacyQuery(){
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", legacy);
		setActivityIntent(intentWithLegacyQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		String validEmailAddress = "h.n.varambhia@gmail.com";
		solo.typeText(emailEditor, validEmailAddress);
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery query = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		
		assertEquals("Expected e-mail address where results would be sent to to be "+validEmailAddress+" " +
				"but got "+query.getSearchParameter("email"), validEmailAddress, 
				query.getSearchParameter("email").getValue());
		
	}
	
	public void testWeCanEditTheDatabaseOfADraftLegacyQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", legacy);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(1, 1);
		getInstrumentation().waitForIdleSync();
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_database_spinner);
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
		
	}
	
	public void testWeCanSaveALegacyDraftQuery(){
		Context context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		long id = helper.save(legacy);
		
		BLASTQueryController queryController = new BLASTQueryController(context);
		BLASTQuery theLegacyQuery = queryController.findBLASTQueryById(id);
		queryController.close();
		SearchParameterController searchParameterController = new SearchParameterController(context);
		List<SearchParameter> legacyParameters = searchParameterController.getParametersForQuery(id);
		searchParameterController.close();
		theLegacyQuery.updateAllParameters(legacyParameters);
		
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", theLegacyQuery);
		setActivityIntent(intentWithLegacyQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.assertCurrentActivity("Should be able to load a legacy query", EMBLEBISetUpQueryActivity.class);
		

		
	}
	
}
