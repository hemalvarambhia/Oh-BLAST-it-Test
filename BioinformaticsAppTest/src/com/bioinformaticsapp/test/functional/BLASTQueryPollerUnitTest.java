package com.bioinformaticsapp.test.functional;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.test.testhelpers.StubbedEMBLService;
import com.bioinformaticsapp.test.testhelpers.StubbedNCBIService;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class BLASTQueryPollerUnitTest extends InstrumentationTestCase {

	private BLASTQueryPoller queryPoller;
	
	public void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getTargetContext();
		BLASTSearchEngine stubbedNCBIService = new StubbedNCBIService();
		BLASTSearchEngine stubbedEMBLService = new StubbedEMBLService();
		queryPoller = new BLASTQueryPoller(context, stubbedNCBIService, stubbedEMBLService);
		
	}
	
}
