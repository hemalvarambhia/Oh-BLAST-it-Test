package com.bioinformaticsapp.test.widget;

import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.TextView;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.widget.BLASTQueryAdapter;

public class BLASTQueryAdapterTest extends InstrumentationTestCase {
	
	private BLASTQuery[] queries;
	
	private BLASTQueryAdapter adapter;
	
	private BLASTQuery query;
	
	private String sampleJobIdentifier = "ncbiblast-R20120418-133731-0240-81389354-pg"; 
	
	public void setUp() throws Exception {
		super.setUp();
		query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setJobIdentifier(sampleJobIdentifier);
		queries = new BLASTQuery[]{query};
		adapter = new BLASTQueryAdapter(getInstrumentation().getTargetContext(), queries);
		
	}
	
	public void tearDown() throws Exception {
		adapter = null;
		queries = null;
		super.tearDown();
	}

	public void testWeShowTheJobIdentifierOfTheQuery(){
		
		View view = adapter.getView(0, null, null);
		
		TextView jobIdentifier = (TextView)view.findViewById(R.id.query_job_id_label);
		
		assertEquals("Should show the job identifier of the query", sampleJobIdentifier, jobIdentifier.getText());
		
	}
	
	public void testWeShowTheAnAppropriateTextIfNoJobIdentifier(){
		
		BLASTQuery[] queries = new BLASTQuery[]{new BLASTQuery("blastn", BLASTVendor.EMBL_EBI)};
		adapter = new BLASTQueryAdapter(getInstrumentation().getTargetContext(), queries);
		
		View view = adapter.getView(0, null, null);
		
		TextView jobIdentifier = (TextView)view.findViewById(R.id.query_job_id_label);
		
		assertEquals("Should show appropriate text if the job identifier of the query is not known", "N/A", jobIdentifier.getText());
		
	}
	
	public void testWeShowTheStatusOfTheQuery(){
		View view = adapter.getView(0, null, null);
		
		TextView queryStatus = (TextView)view.findViewById(R.id.query_job_status_label);
		
		assertEquals("Should show the status of the query", query.getStatus().toString(), queryStatus.getText());
		
	}
	
	
}
