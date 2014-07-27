package com.bioinformaticsapp.test.services;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.bioinformaticsapp.blastservices.SubmitQueryService;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
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
		BLASTQuery query = aPendingQuery();
		helper.save(query);
	}
	
	public void testWeCanStart(){
		Intent intent = new Intent(getContext(), SubmitQueryService.class);
		startService(intent);
	}
	
	private BLASTQuery aPendingQuery(){
		BLASTQuery aPendingQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		aPendingQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		aPendingQuery.setStatus(BLASTQuery.Status.PENDING);	
		return aPendingQuery;
	}
	
}
