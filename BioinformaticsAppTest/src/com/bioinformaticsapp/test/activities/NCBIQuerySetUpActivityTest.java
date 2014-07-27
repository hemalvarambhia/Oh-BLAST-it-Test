package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.SearchParameter;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.BLASTQueryLabBook;
import com.bioinformaticsapp.persistence.DatabaseHelper;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class NCBIQuerySetUpActivityTest extends ActivityInstrumentationTestCase2<NCBIQuerySetUpActivity> {

	private BLASTQuery exampleNCBIQuery;
	private Solo solo;
	private static final int SENDING_DIALOG_TIMEOUT = 95000;
	
	public NCBIQuerySetUpActivityTest() {
		super("com.bioinformaticsapp", NCBIQuerySetUpActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		exampleNCBIQuery = BLASTQuery.ncbiBLASTQuery("blastn");
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
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		getInstrumentation().waitForIdleSync();
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("Sequence in editor", "VADSUC", sequenceEditor.getEditableText().toString());
	}
	
	public void testUIComponentsSetToQueryDefaults(){
		setUpActivityWith(exampleNCBIQuery);
		NCBIQuerySetUpActivity activity = getActivity();
		
		assertCorrectDefaultsDisplayedIn(activity);
	}
		
	public void testSequenceEditTextViewIsSetToValueInQuery(){
		exampleNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		setUpActivityWith(exampleNCBIQuery);
		
		NCBIQuerySetUpActivity activity = getActivity();
		EditText sequenceEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		assertEquals("Sequence in Edit text", exampleNCBIQuery.getSequence(), sequenceEditor.getEditableText().toString());
	}

	public void testWeCanEditTheSequence(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		getInstrumentation().waitForIdleSync();
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(sequenceEditor.getText().toString(), q.getSequence());
	}
	
	public void testWeCanChangeTheBLASTProgram(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		Spinner programSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_program_spinner);
		solo.pressSpinnerItem(0, 1);
		getInstrumentation().waitForIdleSync();
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(programSpinner.getSelectedItem().toString(), q.getBLASTProgram());
	}
	
	public void testWeCanChangeDatabase(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		solo.pressSpinnerItem(1, 1);
		getInstrumentation().waitForIdleSync();
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
	}
	
	public void testWeCanChangeTheWordSize(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		Spinner wordsizeSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_wordsize_spinner);
		solo.pressSpinnerItem(2, 3);
		getInstrumentation().waitForIdleSync();
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(wordsizeSpinner.getSelectedItem().toString(), q.getSearchParameter("word_size").getValue());
	}
	
	public void testWeCanChangeExpThreshold(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText expThresholdEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_exp_threshold_edittext);
		solo.clearEditText(expThresholdEditor);
		solo.typeText(expThresholdEditor, "100.0");
		getInstrumentation().waitForIdleSync();
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(expThresholdEditor.getText().toString(), q.getSearchParameter("exp_threshold").getValue());
	}
	
	public void testWeCanChangeTheMatchMisMatchScore(){
		setUpActivityWith(exampleNCBIQuery);
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		Spinner mismatchSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_match_mismatch_spinner);
		solo.pressSpinnerItem(3, 3);
		
		getInstrumentation().waitForIdleSync();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals(mismatchSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
	}
	
	public void testWeCanSaveADraftQuery(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
        assertSaved();
	}
	
	public void testOnPauseDoesNotSaveSubmittedQuery(){
		exampleNCBIQuery.setStatus(Status.SUBMITTED);
		setUpActivityWith(exampleNCBIQuery);
		Activity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		
		getInstrumentation().callActivityOnPause(setupQueryActivity);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertNull("Primary Key should be null. Instead got "+q.getPrimaryKey(), q.getPrimaryKey());
	}
	
	public void testWeCanUpdateAnExistingBLASTQuery(){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		exampleNCBIQuery = labBook.save(exampleNCBIQuery);
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG"); //Add a sequence
		solo.pressSpinnerItem(1, 1); //Change the database parameter
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		getInstrumentation().waitForIdleSync();
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery query = labBook.findQueryById(exampleNCBIQuery.getPrimaryKey());
		assertEquals(sequenceEditor.getText().toString(), query.getSequence());
		assertEquals(databaseSpinner.getSelectedItem().toString(), query.getSearchParameter("database").getValue());
	}
	
	public void testWeCanSendAValidQuery(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.PENDING, q.getStatus());   
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, "INVALID SEQUENCE");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		NCBIQuerySetUpActivity setupQueryActivity = (NCBIQuerySetUpActivity)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.DRAFT, q.getStatus());
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}
	
	private void assertSaved(){
		DatabaseHelper helper = new DatabaseHelper(getActivity());
		String countCommand = String.format("SELECT COUNT(*) FROM %s", BLASTQuery.BLAST_QUERY_TABLE);
		Cursor countCursor = helper.getReadableDatabase().rawQuery(countCommand, null);
		countCursor.moveToFirst();
		int count = countCursor.getInt(0);
		countCursor.close();
		helper.close();
		assertThat("Number of BLASTQueries in database", count > 0);
	}
	
	private void assertCorrectDefaultsDisplayedIn(NCBIQuerySetUpActivity activity){
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
	
	private void setUpActivityWith(BLASTQuery query){
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
	}
}
