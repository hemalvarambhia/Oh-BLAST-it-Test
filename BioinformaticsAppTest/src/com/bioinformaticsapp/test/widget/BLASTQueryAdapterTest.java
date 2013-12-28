package com.bioinformaticsapp.test.widget;

import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.TextView;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.widget.BLASTQueryAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;

public class BLASTQueryAdapterTest extends InstrumentationTestCase {
	
	private BLASTQueryAdapter adapter;
	private BLASTQuery query;
	
	public void setUp() throws Exception {
		super.setUp();
		query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		BLASTQuery[] queries = new BLASTQuery[]{query};
		adapter = new BLASTQueryAdapter(getInstrumentation().getTargetContext(), queries);
	}
	
	public void testAnAppropriateTextIsDisplayedIfNoJobIdentifier(){
		BLASTQuery[] queries = new BLASTQuery[]{new BLASTQuery("blastn", BLASTVendor.EMBL_EBI)};
		BLASTQueryAdapter adapter = new BLASTQueryAdapter(getInstrumentation().getTargetContext(), queries);
		
		View view = adapter.getView(0, null, null);
		TextView jobIdentifierView = (TextView)view.findViewById(R.id.query_job_id_label);
		String jobIdentifier = jobIdentifierView.getText().toString();
		assertThat("Should show appropriate text if the job identifier of the query is not known", jobIdentifier, is("N/A"));
	}

	public void testTheJobIdentifierOfTheQueryIsDisplayed(){
		View view = adapter.getView(0, null, null);
		
		TextView jobIdentifierView = (TextView)view.findViewById(R.id.query_job_id_label);
		String jobIdentifier = jobIdentifierView.getText().toString();
		assertThat("Should show the job identifier of the query", jobIdentifier, is(query.getJobIdentifier()));
	}
	
	public void testStatusOfTheQueryIsDisplayed(){
		View view = adapter.getView(0, null, null);
		
		TextView queryStatusView = (TextView)view.findViewById(R.id.query_job_status_label);
		String expectedStatus = query.getStatus().toString();
		String statusOfQuery = queryStatusView.getText().toString();
		assertThat("Should show the status of the query", statusOfQuery, is(expectedStatus));
	}
	
	public void testTheRecipientOfTheQueryIsDisplayed(){
		View view = adapter.getView(0, null, null);
		
		TextView recipientView = (TextView)view.findViewById(R.id.to_label);
		String recipient = recipientView.getText().toString();
		assertThat("Should show the recipient of the query", recipient ,is(query.getDestination().toString()));
	}
	
	public void testTheSequenceOfTheQueryIsDisplayed(){
		View view = adapter.getView(0, null, null);
		
		TextView sequenceView = (TextView)view.findViewById(R.id.query_sequence_label);
		String expectedSequence = query.getSequence();
		String sequenceToSearch = sequenceView.getText().toString();
		assertThat("Should show the sequence we want to search", sequenceToSearch, is(expectedSequence));
	}
}
