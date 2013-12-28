package com.bioinformaticsapp.test.functional;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTQueryLabBookTest extends InstrumentationTestCase {

	private BLASTQuery aQuery;
	
	public void setUp() throws Exception {
		super.setUp();
		aQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
	}
	
	public void testWeCanSaveTheBLASTQuery(){
		
	}
}
