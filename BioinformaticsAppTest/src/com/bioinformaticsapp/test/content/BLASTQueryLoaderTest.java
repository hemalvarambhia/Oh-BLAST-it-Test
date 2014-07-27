package com.bioinformaticsapp.test.content;

import android.test.LoaderTestCase;

import com.bioinformaticsapp.content.BLASTQueryLoader;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryLoaderTest extends LoaderTestCase {

	private BLASTQueryLoader mBLASTQueryloader;
	private OhBLASTItTestHelper helper;
	public void setUp() throws Exception {
		super.setUp();
		helper = new OhBLASTItTestHelper(getContext());
		helper.cleanDatabase();	
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		mBLASTQueryloader = null;
	}
	
	public void testWeCanLoadQueryByStatus(){
		BLASTQuery draftQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		BLASTQuery finished = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		finished.setStatus(Status.FINISHED);
		helper.save(draftQuery);
		helper.save(finished);
		
		mBLASTQueryloader = new BLASTQueryLoader(getContext(), Status.DRAFT);
		
		BLASTQuery[] draftQueries = getLoaderResultSynchronously(mBLASTQueryloader);
		
		assertEquals(1, draftQueries.length);
		
		for(BLASTQuery query: draftQueries){
			assertEquals("Should be able to load queries by status", Status.DRAFT, query.getStatus());
		}
		
	}
	
	public void testWeCanLoadManyQueriesByStatus(){
		BLASTQuery draftQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		BLASTQuery anotherDraftQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		BLASTQuery finished = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		finished.setStatus(Status.FINISHED);
		helper.save(draftQuery);
		helper.save(anotherDraftQuery);
		helper.save(finished);
		
		mBLASTQueryloader = new BLASTQueryLoader(getContext(), Status.DRAFT);
		
		BLASTQuery[] draftQueries = getLoaderResultSynchronously(mBLASTQueryloader);
		
		assertEquals(2, draftQueries.length);
		
		for(BLASTQuery query: draftQueries){
			assertEquals("Should be able to load many queries by status", Status.DRAFT, query.getStatus());
		}
		
	}
	
	public void testWeCanLoadSentBLASTQueriesIncludingLegacyOnes(){
		BLASTQuery sentQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		sentQuery.setJobIdentifier("ncbiblast-R20120418-133031-0240-81389354-pg");
		sentQuery.setStatus(Status.SUBMITTED);
		BLASTQuery anotherSentQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		anotherSentQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		anotherSentQuery.setStatus(Status.RUNNING); //This would represent a legacy query which has running status	
		helper.save(sentQuery);
		helper.save(anotherSentQuery);
			
		mBLASTQueryloader = new BLASTQueryLoader(getContext(), Status.SUBMITTED);
			
		BLASTQuery[] sentQueries = getLoaderResultSynchronously(mBLASTQueryloader);
			
		assertEquals(2, sentQueries.length);
			
		for(BLASTQuery query: sentQueries){
			assertTrue("Should be able to load submitted", query.getStatus().equals(Status.SUBMITTED) || query.getStatus().equals(Status.RUNNING));
		}
			
		
	}
}
