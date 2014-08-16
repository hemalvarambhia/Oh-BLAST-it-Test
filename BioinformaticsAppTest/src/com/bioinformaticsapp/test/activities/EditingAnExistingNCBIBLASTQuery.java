package com.bioinformaticsapp.test.activities;

import com.bioinformaticsapp.SetUpNCBIBLASTQuery;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

public class EditingAnExistingNCBIBLASTQuery extends
		ActivityInstrumentationTestCase2<SetUpNCBIBLASTQuery> {

	private BLASTQuery exampleNCBIQuery;
	private Solo solo;
	
	public EditingAnExistingNCBIBLASTQuery(){
		super(SetUpNCBIBLASTQuery.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		exampleNCBIQuery = BLASTQuery.ncbiBLASTQuery("blastn");
	}
	
	public void testSequenceEditTextViewIsSetToValueInQuery(){
		exampleNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		setUpActivityWith(exampleNCBIQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		SetUpNCBIBLASTQuery activity = getActivity();
		EditText sequenceEditor = (EditText)activity.findViewById(com.bioinformaticsapp.R.id.ncbi_sequence_edittext);
		assertEquals("Sequence in Edit text", exampleNCBIQuery.getSequence(), sequenceEditor.getEditableText().toString());
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
	
	private void setUpActivityWith(BLASTQuery query){
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
	}
}
