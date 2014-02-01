package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class EMBLEBIQuerySetUpActivityTest extends ActivityInstrumentationTestCase2<EMBLEBISetUpQueryActivity> {

	private BLASTQuery blastQuery;
	
	private Solo solo;
	private static final int SENDING_DIALOG_TIMEOUT = 35000;
	
	public EMBLEBIQuerySetUpActivityTest() {
		super("com.bioinformaticsapp", EMBLEBISetUpQueryActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		blastQuery = BLASTQuery.emblBLASTQuery("blastn");
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
	}
	
	@Override
	protected void tearDown() throws Exception {
		blastQuery = null;
		if(solo != null){
			solo.finishOpenedActivities();
		}
		super.tearDown();
	}
	
	public void testUIComponentsSetToQueryDefaults(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity activity = this.getActivity();
		
		assertDefaultsDisplayed(activity);
	}
	
	public void testSequenceEditorViewIsSetToValueInQuery(){
		Intent intent = new Intent();
		blastQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity activity = getActivity();	
		EditText sequenceEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.embl_sequence_editor);
		assertThat("Sequence in Edit text", sequenceEditor.getEditableText().toString(), is(blastQuery.getSequence()));
	}
	
	public void testEmailEditorViewIsSetToValueInQuery(){
		Intent intent = new Intent();
		blastQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity activity = getActivity();
		EditText emailEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.embl_send_to_email);
		assertThat("Sequence in Edit text", emailEditor.getEditableText().toString(), is(blastQuery.getSearchParameter("email").getValue()));
	}
	
	public void testWeCanSaveANewlyCreatedDraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		EMBLEBISetUpQueryActivity setUpEMBLQuery = getActivity();
        
		solo = new Solo(getInstrumentation(), setUpEMBLQuery);
        solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
        solo.waitForActivity(setUpEMBLQuery.getLocalClassName(), 5000);
        
        BLASTQuery q = (BLASTQuery)solo.getCurrentActivity().getIntent().getSerializableExtra("query");
        assertSaved(q);
	}
	
	public void testWeCanLoadAndSaveDraftCreatedEarlier(){
		
		Intent intent = new Intent();
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		blastQuery = labBook.save(blastQuery);
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
        BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
        assertThat("The query should be updated", q.getPrimaryKey(), is(blastQuery.getPrimaryKey()));
        
	}
	
	public void testWeCanEditTheProgramOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(0, 1);
		Spinner programSpinner = (Spinner)solo.getView(R.id.blastqueryentry_program_spinner);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(programSpinner.getSelectedItem().toString(), q.getBLASTProgram());
	}
	
	public void testWeCanEditSequenceOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(sequenceEditor.getText().toString(), q.getSequence());
	}
	
	public void testWeCanOnlyInputDNASymbolsIntoTheSequenceTextfield(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("VADSUC", sequenceEditor.getEditableText().toString());
	}
	
	public void testWeCanEditTheDatabaseOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(1, -2);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_database_spinner);
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
		
	}

	public void testWeCanEditTheExpThresholdOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(2, 3);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		Spinner expThresholdSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_expthreshold_spinner);
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(expThresholdSpinner.getSelectedItem().toString(), q.getSearchParameter("exp_threshold").getValue());
		
	}
	
	public void testWeCanEditTheScoreOfADraftQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(3, 1);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_score_spinner);
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("score").getValue());
		
	}
	
	public void testWeCanEditMatchMisMatchScore(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		solo.pressSpinnerItem(4, -2);
		solo.waitForActivity(getActivity().getLocalClassName(), 5000);
        
		Spinner matchMismatchScoreSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ebi_match_mismatch_score_spinner);
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(matchMismatchScoreSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
		
	}
	
	
	public void testWeCanEditTheEmailAddressParameter(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		
		EMBLEBISetUpQueryActivity setupActivity = getActivity();
		solo = new Solo(getInstrumentation(), setupActivity);
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		String validEmailAddress = "h.n.varambhia@gmail.com";
		solo.typeText(emailEditor, validEmailAddress);
		getInstrumentation().waitForIdleSync();
		
		BLASTQuery query = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		
		assertEquals("Expected e-mail address where results would be sent to to be "+validEmailAddress+" " +
				"but got "+query.getSearchParameter("email"), validEmailAddress, 
				query.getSearchParameter("email").getValue());
		
	}
	
	public void testWeCanSendAValidQuery(){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
		EMBLEBISetUpQueryActivity setupQueryActivity = (EMBLEBISetUpQueryActivity)getActivity();
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		solo.typeText(emailEditor, "h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.PENDING, q.getStatus());
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		setupActivityWith(blastQuery);
		EMBLEBISetUpQueryActivity setupQueryActivity = (EMBLEBISetUpQueryActivity)getActivity();
		solo = new Solo(getInstrumentation(), setupQueryActivity);
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		solo.typeText(emailEditor, "h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.DRAFT, q.getStatus());
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}
	
	private void assertDefaultsDisplayed(EMBLEBISetUpQueryActivity activity){
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

	private void setupActivityWith(BLASTQuery query){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
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
}
