package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.SetUpNCBIBLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.DatabaseHelper;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class SettingUpANewNCBIBLASTQuery extends ActivityInstrumentationTestCase2<SetUpNCBIBLASTQuery> {

	private BLASTQuery exampleNCBIQuery;
	private Solo solo;
	private static final int SENDING_DIALOG_TIMEOUT = 95000;
	
	public SettingUpANewNCBIBLASTQuery() {
		super(SetUpNCBIBLASTQuery.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		setUpActivityWithBLASTQuery();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testWeCanOnlyInputValidDNASymbolsIntoTheSequenceEditText(){
		EditText sequenceEditor = typeSequence("INVALIDSEQUENCE");
		getInstrumentation().waitForIdleSync();
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("Sequence in editor", "VADSUC", sequenceEditor.getEditableText().toString());
	}
	
	public void testUIComponentsSetToQueryDefaults(){
		assertCorrectDefaultsDisplayed();
	}
	
	public void testWeCanEditTheSequence(){
		EditText sequenceEditor = typeSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals(sequenceEditor.getText().toString(), query.getSequence());
	}
	
	public void testWeCanChangeTheBLASTProgram(){
		Spinner programSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_program_spinner);
		solo.pressSpinnerItem(0, 1);
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals(programSpinner.getSelectedItem().toString(), query.getBLASTProgram());
	}
	
	public void testWeCanChangeDatabase(){
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_database_spinner);
		solo.pressSpinnerItem(1, 1);
		getInstrumentation().waitForIdleSync();
		
		assertStoredAs("database", databaseSpinner.getSelectedItem());
	}
	
	public void testWeCanChangeTheWordSize(){
		Spinner wordsizeSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_wordsize_spinner);
		solo.pressSpinnerItem(2, 3);
		getInstrumentation().waitForIdleSync();
		
		assertStoredAs("word_size", wordsizeSpinner.getSelectedItem());
	}
	
	public void testWeCanChangeExpThreshold(){
		EditText expThresholdEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_exp_threshold_edittext);
		solo.clearEditText(expThresholdEditor);
		solo.typeText(expThresholdEditor, "100.0");
		getInstrumentation().waitForIdleSync();
		
		assertStoredAs("exp_threshold", expThresholdEditor.getText());
	}
	
	public void testWeCanChangeTheMatchMisMatchScore(){
		solo.pressSpinnerItem(3, 3);
		getInstrumentation().waitForIdleSync();
		
		Spinner mismatchSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ncbi_match_mismatch_spinner);
		assertStoredAs("match_mismatch_score", mismatchSpinner.getSelectedItem());
	}
	
	public void testWeCanSaveADraftQuery(){
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		getInstrumentation().waitForIdleSync();
		
        assertSaved();
	}
	
	public void testWeCanSendAValidQuery(){
		send("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals( "Expected query to be ready for sending", Status.PENDING, query.getStatus());   
	}

	private void send(String sequence) {
		typeSequence(sequence);
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		send("INVALID SEQUENCE");
		
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals( "Expected query to be ready for sending", Status.DRAFT, query.getStatus());
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}

	private EditText typeSequence(String sequence) {
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		solo.typeText(sequenceEditor, sequence);
		return sequenceEditor;
	}
	
	private void setUpActivityWithBLASTQuery(){
		Intent intent = new Intent();
		exampleNCBIQuery = BLASTQuery.ncbiBLASTQuery("blastn");
		intent.putExtra("query", exampleNCBIQuery);
		setActivityIntent(intent);
	}

	private BLASTQuery getBLASTQueryFromActivity() {
		return (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
	}
	
	private void assertStoredAs(String parameterName, Object widgetValue) {
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals(widgetValue.toString(), query.getSearchParameter(parameterName).getValue());
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
	
	private void assertCorrectDefaultsDisplayed(){
		SetUpNCBIBLASTQuery activity = getActivity();
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
	
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
