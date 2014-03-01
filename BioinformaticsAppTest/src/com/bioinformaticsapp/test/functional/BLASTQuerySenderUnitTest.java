package com.bioinformaticsapp.test.functional;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNull.*;

import java.util.concurrent.ExecutionException;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.StubbedEMBLService;
import com.bioinformaticsapp.test.testhelpers.StubbedNCBIService;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class BLASTQuerySenderUnitTest extends InstrumentationTestCase {

	private BLASTQuerySender sender;
	
	public void setUp() throws Exception {
		super.setUp();
		BLASTSearchEngine ncbiBLASTService = new StubbedNCBIService();
		BLASTSearchEngine emblBLASTService = new StubbedEMBLService();
		Context context = getInstrumentation().getTargetContext();
		sender = new BLASTQuerySender(context, ncbiBLASTService, emblBLASTService);
	}
	
	public void testSenderSetsTheQuerysServiceGeneratedIdentifier() throws InterruptedException, ExecutionException {
		BLASTQuery query = aBLASTQuery();
		
		sender.execute(query);
		
		Integer numberSent = sender.get();
		assertThat("Expected the number of queries sent to be more than 1", numberSent, is(1));
		assertThat("The BLAST query's identifier should be set", query.getJobIdentifier(), is(not(nullValue())));
	}
	
	private BLASTQuery aBLASTQuery() {
		return BLASTQuery.emblBLASTQuery("blastn");
	}
}
