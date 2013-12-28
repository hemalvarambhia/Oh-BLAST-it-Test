package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTQueryLabBookTest extends InstrumentationTestCase {

	private BLASTQuery aQuery;
	private BLASTQueryLabBook labBook;
	
	public void setUp() throws Exception {
		super.setUp();
		aQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
	}
	
	public void testWeCanSaveTheBLASTQueryToTheLabBook(){
		boolean saved = labBook.save(aQuery);
		
		assertThat("Should be able to save a BLASTQuery to the lab book",
				saved, equalTo(true));
	}
	
	
}
