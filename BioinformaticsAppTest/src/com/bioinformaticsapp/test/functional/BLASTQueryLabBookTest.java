package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.notNullValue;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTQueryLabBookTest extends InstrumentationTestCase {

	private BLASTQuery aQuery;
	private BLASTQueryLabBook labBook;
	
	public void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		aQuery = BLASTQuery.ncbiBLASTQuery("blastn");
		labBook = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
	}
	
	public void testWeCanSaveTheBLASTQueryToTheLabBook(){
		BLASTQuery query = labBook.save(aQuery);
		
		assertThat("Saved BLAST query must have a primary key",
				query.getPrimaryKey(), is(notNullValue()));
		BLASTQuery expected = (BLASTQuery)aQuery.clone();
		expected.setPrimaryKeyId(query.getPrimaryKey());
	}
	
	public void testWeFindRetrieveAQueryByIdentifier(){
		BLASTQuery query = labBook.save(aQuery);
		
		BLASTQuery fromStorage= labBook.findQueryById(query.getPrimaryKey());
		
		assertThat("Should be able to find the right query by ID", fromStorage, is(equalTo(query)));
	}
	
}
