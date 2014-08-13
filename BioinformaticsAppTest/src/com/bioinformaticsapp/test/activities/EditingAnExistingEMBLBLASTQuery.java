package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.bioinformaticsapp.SetUpEMBLEBIBLASTQuery;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class EditingAnExistingEMBLBLASTQuery extends
		ActivityInstrumentationTestCase2<SetUpEMBLEBIBLASTQuery> {

	private BLASTQuery blastQuery;
	private Solo solo;

	public EditingAnExistingEMBLBLASTQuery(){
		super(SetUpEMBLEBIBLASTQuery.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		blastQuery = BLASTQuery.emblBLASTQuery("blastn");
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		blastQuery = labBook.save(blastQuery);
	}
	
	public void testWeCanLoadAndSaveDraftCreatedEarlier(){
		setupActivityWith(blastQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(com.bioinformaticsapp.R.id.save_query);
		waitFor(5000);
        
        BLASTQuery q = (BLASTQuery)getActivity().getIntent().getSerializableExtra("query");
        assertThat("The query should be updated", q.getPrimaryKey(), is(blastQuery.getPrimaryKey()));   
	}

	private void waitFor(int time){
		solo.waitForActivity(getActivity().getLocalClassName(), time);
	}
	
	private void setupActivityWith(BLASTQuery query){
		Intent intent = new Intent();
		intent.putExtra("query", blastQuery);
		setActivityIntent(intent);
	}
}
