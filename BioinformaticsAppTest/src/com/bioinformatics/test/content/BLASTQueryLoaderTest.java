package com.bioinformatics.test.content;

import android.test.LoaderTestCase;

import com.bioinformatics.content.BLASTQueryLoader;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryLoaderTest extends LoaderTestCase {

	private BLASTQueryLoader mBLASTQueryloader;

	public void setUp() throws Exception {
		super.setUp();
		BLASTQuery draftQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		BLASTQuery finished = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		finished.setStatus(Status.FINISHED);
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getContext());
		helper.cleanDatabase();
		helper.save(draftQuery);
		helper.save(finished);
		
		mBLASTQueryloader = new BLASTQueryLoader(getContext(), Status.DRAFT);
	}
	
	public void tearDown() throws Exception {
		mBLASTQueryloader.stopLoading();
		mBLASTQueryloader = null;
		super.tearDown();
	}
	
	public void testWeCanRetrieveDraftQueries(){
		BLASTQuery[] draftQueries = getLoaderResultSynchronously(mBLASTQueryloader);
		
		assertEquals(1, draftQueries.length);
	}
	
}
