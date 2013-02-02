package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class NCBIQuerySetUpActivityTest extends ActivityInstrumentationTestCase2<NCBIQuerySetUpActivity> {

	private BLASTQuery exampleNCBIQuery;
	
	private static final String TAG = "NCBIQuerySetUpActivityTest";
	private Solo solo;

	
	
	private static final int SENDING_DIALOG_TIMEOUT = 40000;
	
	public NCBIQuerySetUpActivityTest() {
		super("com.bioinformaticsapp", NCBIQuerySetUpActivity.class);
		
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		exampleNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		
		helper.cleanDatabase();
		
	}
	
	protected void tearDown() throws Exception {
		exampleNCBIQuery = null;
		if(solo != null){
			solo.finishOpenedActivities();
		}
		
		super.tearDown();
		
	}

	public void testWeCanOnlyInputValidDNASymbolsIntoTheSequenceEditText(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		
		NCBIQuerySetUpActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		getInstrumentation().waitForIdleSync();
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("Sequence in editor", "VADSUC", sequenceEditor.getEditableText().toString());
		
	}
	
	public void testUIComponentsSetToQueryDefaults(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		
		NCBIQuerySetUpActivity activity = getActivity();
		
		Spinner programSpinner = (Spinner)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_program_spinner);
		assertEquals("Currently selected item of program spinner", exampleNCBIQuery.getBLASTProgram(), programSpinner.getSelectedItem());
		
		Spinner databaseSpinner = (Spinner)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		assertEquals("Currently selected item of database spinner", exampleNCBIQuery.getSearchParameter("database").getValue(), databaseSpinner.getSelectedItem());
		
		Spinner wordsizeSpinner = (Spinner)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_wordsize_spinner);
		assertEquals("Currently selected item of word size spinner", exampleNCBIQuery.getSearchParameter("word_size").getValue(), wordsizeSpinner.getSelectedItem());
		
		EditText expThresholdEditText = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_exp_threshold_edittext);
		String textInEditTextView = expThresholdEditText.getText().toString();
		assertEquals("Text in the exp threshold edit text view", exampleNCBIQuery.getSearchParameter("exp_threshold").getValue(), textInEditTextView);
		
		Spinner matchmismatchSpinner = (Spinner)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_match_mismatch_spinner);
		assertEquals("Default value of mismatchspinner", exampleNCBIQuery.getSearchParameter("match_mismatch_score").getValue(), matchmismatchSpinner.getSelectedItem());
		
		EditText sequenceEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		assertEquals("Enter a sequence", sequenceEditor.getHint().toString());
	}
		
	public void testSequenceEditTextViewIsSetToValueInQuery(){
		Intent intent = new Intent();
		exampleNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		intent.putExtra("query", exampleNCBIQuery);
		
		setActivityIntent(intent);
		
		NCBIQuerySetUpActivity activity = getActivity();
		
		EditText sequenceEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		assertEquals("Sequence in Edit text", exampleNCBIQuery.getSequence(), sequenceEditor.getEditableText().toString());
		
	}

	public void testWeCanEditTheSequence(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(sequenceEditor.getText().toString(), q.getSequence());
		
	}
	
	public void testWeCanChangeTheBLASTProgram(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		Spinner programSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_program_spinner);
		solo.pressSpinnerItem(0, 1);
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(programSpinner.getSelectedItem().toString(), q.getBLASTProgram());
		
	}
	
	public void testWeCanChangeDatabase(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		solo.pressSpinnerItem(1, 1);
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
		
	}
	
	public void testWeCanChangeExpThreshold(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		EditText expThresholdEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_exp_threshold_edittext);
		solo.clearEditText(expThresholdEditor);
		solo.typeText(expThresholdEditor, "100.0");
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(expThresholdEditor.getText().toString(), q.getSearchParameter("exp_threshold").getValue());
		
	}
	
	public void testWeCanChangeTheMatchMisMatchScore(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		Spinner mismatchSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_match_mismatch_spinner);
		solo.pressSpinnerItem(3, 3);
		
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(mismatchSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
		
	}
	
	public void testWeCanChangeTheWordSize(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		Spinner wordsizeSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_wordsize_spinner);
		solo.pressSpinnerItem(2, 3);
		
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(wordsizeSpinner.getSelectedItem().toString(), q.getSearchParameter("word_size").getValue());
		
	}
	
	public void testWeCanSaveADraftQuery(){
		
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		
		//ActivityMonitor am = getInstrumentation().addMonitor(NCBIQuerySetUpActivity.class.getName(), null, false);
		NCBIQuerySetUpActivity setupActivity = (NCBIQuerySetUpActivity)getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
        BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		
		assertTrue("Expected query primary key to be more than 0, but got "+q.getPrimaryKey(), q.getPrimaryKey() > 0);
		List<SearchParameter> parameters = q.getAllParameters();
		for(int i = 0; i < q.getAllParameters().size(); i++){
			SearchParameter parameter = parameters.get(i);
			assertTrue("Expected parameter primary key to be more than 0, but got "+parameter.getPrimaryKey(), parameter.getPrimaryKey() > 0);
			
			//Check the query ID foreign key in the parameters table is equal to the primary key
			//of its parent query
			assertEquals(q.getPrimaryKey().longValue(), parameter.getBlastQueryId().longValue());
		}
		
		
	}
	
	public void testOnPauseDoesNotSaveSubmittedQuery(){
		Intent intent = new Intent();
		exampleNCBIQuery.setStatus(Status.SUBMITTED);
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		Activity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		getInstrumentation().callActivityOnPause(setupQueryActivity);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		
		assertNull("Primary Key should be null. Instead got "+q.getPrimaryKey(), q.getPrimaryKey());
		List<SearchParameter> parameters = q.getAllParameters();
		for(int i = 0; i < q.getAllParameters().size(); i++){
			assertNull(parameters.get(i).getPrimaryKey());
			
		}
	}
	
	private long saveQuery(){
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		long primaryKey = queryController.save(exampleNCBIQuery);
		exampleNCBIQuery.setPrimaryKeyId(primaryKey);
		List<SearchParameter> parameters = exampleNCBIQuery.getAllParameters();
		List<SearchParameter> newSetOfParameters = new ArrayList<SearchParameter>();
		SearchParameterController parametersControllers = new SearchParameterController(getInstrumentation().getTargetContext());
		for(SearchParameter parameter: parameters){
			parameter.setBlastQueryId(exampleNCBIQuery.getPrimaryKey());
			long parameterPrimaryKey = parametersControllers.save(parameter);
			parameter.setPrimaryKey(parameterPrimaryKey);
			newSetOfParameters.add(parameter);
		}
		
		exampleNCBIQuery.updateAllParameters(newSetOfParameters);
		parametersControllers.close();
		queryController.close();
		return primaryKey;
	}
	
	public void testWeCanUpdateAQueryFromDatabase(){
		
		long pk = saveQuery();
		
		//Get the query we loaded in the database
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		SearchParameterController parametersController = new SearchParameterController(getInstrumentation().getTargetContext());
		
		BLASTQuery query = queryController.findBLASTQueryById(pk);
		List<SearchParameter> parameters = parametersController.getParametersForQuery(pk);
		query.updateAllParameters(parameters);
		
		Intent intent = new Intent();
		intent.putExtra("query", query);
		
		setActivityIntent(intent);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		//Change a couple of the query parameters
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG"); //Add a sequence
		
		solo.pressSpinnerItem(1, 1); //Change the database parameter
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		getInstrumentation().waitForIdleSync();
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery queryFromDatabase = queryController.findBLASTQueryById(pk);
		List<SearchParameter> parametersFromDatabase = parametersController.getParametersForQuery(exampleNCBIQuery.getPrimaryKey());
		queryFromDatabase.updateAllParameters(parametersFromDatabase);
		
		parametersController.close();
		queryController.close();
		
		assertEquals(sequenceEditor.getText().toString(), queryFromDatabase.getSequence());
		
		assertEquals(databaseSpinner.getSelectedItem().toString(), queryFromDatabase.getSearchParameter("database").getValue());
	}
	
	public void testWeCanSendAValidQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		
		assertEquals( "Expected query to be ready for sending", Status.PENDING, q.getStatus());
		
        
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		
		solo.typeText(sequenceEditor, "INVALID SEQUENCE");
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		
		assertEquals( "Expected query to be ready for sending", Status.DRAFT, q.getStatus());
		
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){

		Intent intent = new Intent();
		
		intent.putExtra("query", exampleNCBIQuery);
		
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}
	
}
