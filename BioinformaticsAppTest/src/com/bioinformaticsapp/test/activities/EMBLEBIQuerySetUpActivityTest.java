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
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.DatabaseHelper;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class EMBLEBIQuerySetUpActivityTest extends ActivityInstrumentationTestCase2<SetUpEMBLEBIBLASTQuery> {

	private BLASTQuery blastQuery;
	
	private Solo solo;
	private static final int SENDING_DIALOG_TIMEOUT = 95000;
	
	public EMBLEBIQuerySetUpActivityTest() {
		super(SetUpEMBLEBIBLASTQuery.class);
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
		setupActivityWith(blastQuery);
		
		assertDefaultsDisplayed(getActivity());
	}
	
	public void testSequenceEditorViewIsSetToValueInQuery(){
		blastQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		setupActivityWith(blastQuery);
		
		EditText sequenceEditor = (EditText)getActivity().findViewById(com.bioinformaticsapp.R.id.embl_sequence_editor);
		assertThat("Sequence in Edit text", sequenceEditor.getEditableText().toString(), is(blastQuery.getSequence()));
	}
	
	public void testEmailEditorViewIsSetToValueInQuery(){
		blastQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		setupActivityWith(blastQuery);
		
		EditText emailEditor = (EditText)getActivity().findViewById(com.bioinformaticsapp.R.id.embl_send_to_email);
		assertThat("Sequence in Edit text", emailEditor.getEditableText().toString(), is(blastQuery.getSearchParameter("email").getValue()));
	}
	
	public void testWeCanSaveANewlyCreatedDraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
        
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
        waitFor(5000);
        
        BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
        assertSaved(q);
	}
	
	public void testWeCanLoadAndSaveDraftCreatedEarlier(){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		blastQuery = labBook.save(blastQuery);
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		waitFor(5000);
        
        BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
        assertThat("The query should be updated", q.getPrimaryKey(), is(blastQuery.getPrimaryKey()));   
	}
	
	public void testWeCanEditTheProgramOfADraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(0, 1);
		waitFor(5000);
        
		Spinner programSpinner = (Spinner)solo.getView(R.id.blastqueryentry_program_spinner);
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals(programSpinner.getSelectedItem().toString(), q.getBLASTProgram());
	}
	
	public void testWeCanEditSequenceOfADraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		waitFor(5000);
        
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals(sequenceEditor.getText().toString(), q.getSequence());
	}
	
	public void testWeCanOnlyInputDNASymbolsIntoTheSequenceTextfield(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		waitFor(5000);
        
		//'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'
		assertEquals("VADSUC", sequenceEditor.getEditableText().toString());
	}
	
	public void testWeCanEditTheDatabaseOfADraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(1, -2);
		waitFor(5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_database_spinner);
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("database").getValue());
	}

	public void testWeCanEditTheExpThresholdOfADraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(2, 3);
		waitFor(5000);
        
		Spinner expThresholdSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_expthreshold_spinner);
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals(expThresholdSpinner.getSelectedItem().toString(), q.getSearchParameter("exp_threshold").getValue());
	}
	
	public void testWeCanEditTheScoreOfADraftQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(3, 1);
		waitFor(5000);
        
		Spinner databaseSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.blastqueryentry_score_spinner);
		SetUpEMBLEBIBLASTQuery setupActivity = getActivity();
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(databaseSpinner.getSelectedItem().toString(), q.getSearchParameter("score").getValue());
	}
	
	public void testWeCanEditMatchMisMatchScore(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.pressSpinnerItem(4, -2);
		waitFor(5000);
		
		Spinner matchMismatchScoreSpinner = (Spinner)solo.getView(com.bioinformaticsapp.R.id.ebi_match_mismatch_score_spinner);
		SetUpEMBLEBIBLASTQuery setupActivity = getActivity();
		BLASTQuery q = (BLASTQuery)setupActivity.getIntent().getSerializableExtra("query");
		assertEquals(matchMismatchScoreSpinner.getSelectedItem().toString(), q.getSearchParameter("match_mismatch_score").getValue());
	}
	
	
	public void testWeCanEditTheEmailAddressParameter(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		String validEmailAddress = "h.n.varambhia@gmail.com";
		solo.typeText(emailEditor, validEmailAddress);
		waitFor(5000);
		
		BLASTQuery query = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals("Expected e-mail address where results would be sent to to be "+validEmailAddress+" " +
				"but got "+query.getSearchParameter("email"), validEmailAddress, 
				query.getSearchParameter("email").getValue());
	}
	
	public void testWeCanSendAValidQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		solo.typeText(emailEditor, "h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.PENDING, q.getStatus());
	}
	
	public void testWeCannotSendAnInvalidQuery(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText sequenceEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_sequence_editor);
		solo.typeText(sequenceEditor, "INVALIDSEQUENCE");
		EditText emailEditor = (EditText)solo.getView(com.bioinformaticsapp.R.id.embl_send_to_email);
		solo.typeText(emailEditor, "h.n.varambhia@gmail.com");
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.send_query);
		solo.waitForDialogToClose(SENDING_DIALOG_TIMEOUT);
		
		SetUpEMBLEBIBLASTQuery setupQueryActivity = (SetUpEMBLEBIBLASTQuery)getActivity();
		BLASTQuery q = (BLASTQuery)setupQueryActivity.getIntent().getSerializableExtra("query");
		assertEquals( "Expected query to be ready for sending", Status.DRAFT, q.getStatus());
	}
	
	public void testWeCanGoToTheApplicationPreferencesScreen(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnMenuItem("Settings");
		
		solo.assertCurrentActivity("Should be able to go to the settings screen", AppPreferences.class);
	}
	
	private void assertDefaultsDisplayed(SetUpEMBLEBIBLASTQuery activity){
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
	
	private void waitFor(int time){
		solo.waitForActivity(getActivity().getLocalClassName(), time);
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
