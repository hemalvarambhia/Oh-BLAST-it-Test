package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.SetUpEMBLEBIBLASTQuery;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.DatabaseHelper;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class SettingUpANewEMBLEBIBLASTQuery extends ActivityInstrumentationTestCase2<SetUpEMBLEBIBLASTQuery> {

	private BLASTQuery blastQuery;
	private Solo solo;
	
	private static final int SENDING_DIALOG_TIMEOUT = 95000;
	
	public SettingUpANewEMBLEBIBLASTQuery() {
		super(SetUpEMBLEBIBLASTQuery.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		setUpActivityWithBLASTQuery();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testUIComponentsSetToQueryDefaults(){
		assertDefaultsDisplayed();
	}
	
	public void testWeCanSaveANewlyCreatedDraftQuery(){
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
        waitFor(5000);
        
        BLASTQuery q = getBLASTQueryFromActivity();
        assertSaved(q);
	}
	
	public void testWeCanEditTheProgramOfADraftQuery(){
		solo.pressSpinnerItem(0, 1);
		waitFor(5000);
        
		Spinner programSpinner = (Spinner)solo.getView(R.id.blastqueryentry_program_spinner);
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(programSpinner.getSelectedItem().toString(), q.getBLASTProgram());
	}
	
	public void testWeCanEditSequenceOfADraftQuery(){
		EditText sequenceEditor = typeSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		waitFor(5000);
        
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(sequenceEditor.getText().toString(), q.getSequence());
	}
	
	public void testWeCanOnlyInputDNASymbolsIntoTheSequenceTextfield(){
		EditText sequenceEditor = typeSequence("INVALIDSEQUENCE");
		waitFor(5000);
        
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("VADSUC", sequenceEditor.getEditableText().toString());
	}
	
	public void testWeCanEditTheDatabaseOfADraftQuery(){	
		solo.pressSpinnerItem(1, -2);
		waitFor(5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_database_spinner);
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
	}

	public void testWeCanEditTheExpThresholdOfADraftQuery(){
		solo.pressSpinnerItem(2, 3);
		waitFor(5000);
        
		Spinner expThresholdSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_expthreshold_spinner);
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(expThresholdSpinner.getSelectedItem().toString(), q.getSearchParameter("exp_threshold").getValue());
	}
	
	public void testWeCanEditTheScoreOfADraftQuery(){
		solo.pressSpinnerItem(3, 1);
		waitFor(5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_score_spinner);
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("score").getValue());
	}
	
	public void testWeCanEditMatchMisMatchScore(){
		solo.pressSpinnerItem(4, -2);
		waitFor(5000);
		
		Spinner matchMismatchScoreSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ebi_match_mismatch_score_spinner);
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals(matchMismatchScoreSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
	}
	
	public void testWeCanEditTheEmailAddressParameter(){
		String validEmailAddress = "h.n.varambhia@gmail.com";
		typeEmailAddress(validEmailAddress);
		waitFor(5000);
		
		BLASTQuery query = getBLASTQueryFromActivity();
		assertEquals("Expected e-mail address where results would be sent to to be "+validEmailAddress+" " +
				"but got "+query.getSearchParameter("email"), validEmailAddress, 
				query.getSearchParameter("email").getValue());
	}

	public void testWeCanSendAValidQuery(){
		typeSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		typeEmailAddress("h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals( "Expected query to be ready for sending", Status.PENDING, q.getStatus());
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		typeSequence("INVALIDSEQUENCE");
		typeEmailAddress("h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = getBLASTQueryFromActivity();
		assertEquals( "Expected query not to be sent", Status.DRAFT, q.getStatus());
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}
	
	private EditText typeSequence(String sequence) {
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, sequence);
		return sequenceEditor;
	}

	private void typeEmailAddress(String emailAddress) {
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		solo.typeText(emailEditor, emailAddress);
	}
	
	private void waitFor(int time){
		solo.waitForActivity(getActivity().getLocalClassName(), time);
	}
	
	private BLASTQuery getBLASTQueryFromActivity() {
		return (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
	}
	
	private void setUpActivityWithBLASTQuery(){
		Intent intent = new Intent();
		blastQuery = BLASTQuery.emblBLASTQuery("blastn");
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
	}
	
	private void assertDefaultsDisplayed(){
		SetUpEMBLEBIBLASTQuery activity = getActivity();
		Spinner programSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_program_spinner);
		assertThat("Should have an appropriate default for program", 
				programSpinner.getSelectedItem().toString(), 
				is(blastQuery.getBLASTProgram()));
		
		Spinner databaseSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_database_spinner);
		assertThat("Should have an appropriate default for the database", 
				databaseSpinner.getSelectedItem().toString(), 
				is(blastQuery.getSearchParameter("database").getValue()));
		
		Spinner thresholdSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_expthreshold_spinner);
		assertThat("Should have an appropriate default for the threshold",
				 thresholdSpinner.getSelectedItem().toString(),
				 is(blastQuery.getSearchParameter("exp_threshold").getValue()));
		
		Spinner scoreSpinner = (Spinner)activity.findViewById(R.id.blastqueryentry_score_spinner);
		assertThat("Should have an appropriate default value for the score",
				scoreSpinner.getSelectedItem().toString(),
				is(blastQuery.getSearchParameter("score").getValue()));
		
		Spinner matchMismatchScoreSpinner = (Spinner)activity.findViewById(R.id.ebi_match_mismatch_score_spinner);
		assertThat("Should have an appropriate default value for the mis-match score",
				matchMismatchScoreSpinner.getSelectedItem().toString(),
				is(blastQuery.getSearchParameter("match_mismatch_score").getValue().toString()));
		
		EditText sequenceEditorView = (EditText)activity.findViewById(R.id.embl_sequence_editor);
		assertThat("Should hint to the user to enter a sequence", 
				sequenceEditorView.getHint().toString(), is("Enter a sequence"));
	}

	private void assertSaved(BLASTQuery q){
		DatabaseHelper helper = new DatabaseHelper(getActivity());
		String countCommand = String.format("SELECT COUNT(*) FROM %s", BLASTQuery.BLAST_QUERY_TABLE);
		Cursor countCursor = helper.getReadableDatabase().rawQuery(countCommand, null);
		countCursor.moveToFirst();
		int count = countCursor.getInt(0);
		countCursor.close();
		helper.close();
		assertThat("Number of BLASTQueries in database", count > 0);
	}
	
	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
