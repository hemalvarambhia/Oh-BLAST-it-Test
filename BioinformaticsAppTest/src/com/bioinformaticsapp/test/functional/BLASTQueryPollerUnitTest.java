package com.bioinformaticsapp.test.functional;

import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.aBLASTQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.StubbedBLASTSearchEngine;

public class BLASTQueryPollerUnitTest extends InstrumentationTestCase {

	private BLASTQueryPoller queryPoller;
	
	public void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getTargetContext();
		BLASTSearchEngine stubbedSearchEngine = new StubbedBLASTSearchEngine();
		queryPoller = new BLASTQueryPoller(context, stubbedSearchEngine);
	}
	
	public void testPollerReturnsTheNumberOfQueriesThatFinishedSuccessfully() throws InterruptedException, ExecutionException{
		BLASTQuery query = aBLASTQuery();
		
		queryPoller.execute(query);
		
		queryPoller.get();
		assertThat("The poller should state the number of queries that finished successfully", queryPoller.get(), is(0));
	}
	
	public void testPollerShouldUpdateTheStatusOfAQuery() throws InterruptedException, ExecutionException{
		BLASTQuery query = aBLASTQuery();
		
		queryPoller.execute(query);
		
		queryPoller.get();
		assertThat("The status of the query should be updated", query.getStatus(), is(not(Status.DRAFT)));
	}

	public void testPollerDoesNotUpdateStatusWhenThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		BLASTQuery query = aBLASTQuery();
		BLASTSearchEngine stubbedSearchEngine = new StubbedBLASTSearchEngine();
		Context context = getInstrumentation().getTargetContext();
		queryPoller = new BLASTQueryPoller(context, stubbedSearchEngine){
			protected boolean connectedToWeb(){
				return false;
			}
		};

		queryPoller.execute(query);
		
		queryPoller.get();
		assertThat("The status of the query shouldn't be updated", query.getStatus(), is(Status.DRAFT));
	}
}
