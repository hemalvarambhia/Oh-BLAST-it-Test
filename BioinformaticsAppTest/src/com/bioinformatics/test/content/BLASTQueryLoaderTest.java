package com.bioinformatics.test.content;

import android.test.LoaderTestCase;

import com.bioinformaticsapp.content.BLASTQueryLoader;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
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
	}
	
	public void testWeCanLoadQueriesByStatus(){
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
	
}
