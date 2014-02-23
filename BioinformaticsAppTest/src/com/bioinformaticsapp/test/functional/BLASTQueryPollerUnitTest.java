package com.bioinformaticsapp.test.functional;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;


import java.util.concurrent.ExecutionException;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
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
	
	public void testPollerShouldUpdateTheStatusOfAQuery() throws InterruptedException, ExecutionException{
		BLASTQuery query = aBLASTQuery();
		
		queryPoller.execute(query);
		
		queryPoller.get();
		assertThat("The status of the query should be updated", query.getStatus(), is(not(Status.DRAFT)));
	}

	public void testPollerDoesNotUpdateStatusWhenThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		BLASTQuery query = aBLASTQuery();
		BLASTSearchEngine ncbiService = new StubbedNCBIService();
		BLASTSearchEngine emblService = new StubbedEMBLService();
		Context context = getInstrumentation().getTargetContext();
		queryPoller = new BLASTQueryPoller(context, ncbiService, emblService){
			protected boolean connectedToWeb(){
				return false;
			}
		};

		queryPoller.execute(query);
		
		queryPoller.get();
		assertThat("The status of the query shouldn't be updated", query.getStatus(), is(Status.DRAFT));
	}
	
	private BLASTQuery aBLASTQuery() {
		return BLASTQuery.emblBLASTQuery("blastn");
	}
	
}
