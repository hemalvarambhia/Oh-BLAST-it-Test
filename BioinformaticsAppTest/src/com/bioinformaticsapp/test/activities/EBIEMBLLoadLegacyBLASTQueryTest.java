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
import com.bioinformaticsapp.R;
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
		legacy.setJobIdentifier("TODO");
		legacy.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
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
		EMBLEBISetUpQueryActivity activity = getActivity();
		Spinner programSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_program_spinner);
		Spinner databaseSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_database_spinner);
		Spinner thresholdSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_expthreshold_spinner);
		Spinner scoreSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_score_spinner);
		EditText sequenceEditorView = (EditText)activity.findViewById(R.id.embl_sequence_editor);
		assertEquals(legacy.getBLASTProgram(), programSpinner.getSelectedItem());
		assertEquals(legacy.getSearchParameter("database").getValue(), databaseSpinner.getSelectedItem());
		assertEquals(legacy.getSearchParameter("exp_threshold").getValue(), thresholdSpinner.getSelectedItem());
		assertEquals(legacy.getSearchParameter("score").getValue(), scoreSpinner.getSelectedItem());
		assertEquals(legacy.getSequence(), sequenceEditorView.getText().toString());
		
	}
	
	public void testLegacyQueryHasTheNewParameters(){
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", legacy);
		setActivityIntent(intentWithLegacyQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		Intent activityIntent = getActivity().getIntent();
		BLASTQuery legacyQuery = (BLASTQuery)activityIntent.getSerializableExtra("query");
		
		SearchParameter matchMismatchScore = legacyQuery.getSearchParameter("match_mismatch_score");
		assertNotNull(matchMismatchScore);
		assertNotNull(matchMismatchScore.getValue());
		
		SearchParameter email = legacyQuery.getSearchParameter("email");
		assertNotNull(email);
		assertNotNull(email.getValue());
	}
	
	public void testLegacyDraftJobIdentifierCleared(){
		Intent intentWithLegacyQuery = new Intent();
		intentWithLegacyQuery.putExtra("query", legacy);
		setActivityIntent(intentWithLegacyQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		Intent activityIntent = getActivity().getIntent();
		BLASTQuery legacyQuery = (BLASTQuery)activityIntent.getSerializableExtra("query");
		
		assertNull("Draft legacy query should not have a job identifier", legacyQuery.getJobIdentifier());
		
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
	
	public void testWeCanEditTheExponentialThresholdOfADraftLegacyQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", legacy);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(2, 3);
		getInstrumentation().waitForIdleSync();
		Spinner expThresholdSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_expthreshold_spinner);
		BLASTQuery legacyQuery = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(expThresholdSpinner.getSelectedItem().toString(), legacyQuery.getSearchParameter("exp_threshold").getValue());
		
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
		solo.clickOnActionBarItem(R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
		Intent activityIntent = getActivity().getIntent();
		BLASTQuery legacyQuery = (BLASTQuery)activityIntent.getSerializableExtra("query");
		
		SearchParameter matchMismatchScore = legacyQuery.getSearchParameter("match_mismatch_score");
		assertNotNull(matchMismatchScore.getPrimaryKey());
		assertNotNull(matchMismatchScore.getBlastQueryId());
		
	}
	
	public void testWeCanEditTheScoreOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", legacy);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(2, -2);
		getInstrumentation().waitForIdleSync();
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_score_spinner);
		BLASTQuery legacyQuery = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), legacyQuery.getSearchParameter("score").getValue());
		
	}
	
}
