package com.bioinformaticsapp.test.services;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.bioinformaticsapp.SubmitQueryService;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class SubmitQueryServiceTest extends ServiceTestCase<SubmitQueryService> {

	public SubmitQueryServiceTest(Class<SubmitQueryService> serviceClass) {
		super(serviceClass);	
	}
	
	public SubmitQueryServiceTest(){
		super(SubmitQueryService.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getContext());
		helper.cleanDatabase();
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setStatus(BLASTQuery.Status.PENDING);	
		helper.save(query);
	}
	
	public void testWeCanStart(){
		Intent intent = new Intent(getContext(), SubmitQueryService.class);
		startService(intent);
	}
	
}
