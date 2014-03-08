package com.bioinformaticsapp.test.functional;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

/**
 * Here we will set the status of the queries to <code>PENDING</code>
 * as the BLAST query sender will send pending query provided from 
 * elsewhere
 * @author Hemal N Varambhia
 *
 */

public class BLASTQuerySenderTest extends InstrumentationTestCase {

	private BLASTQuery query; //We use this for some GENERAL testing before going into specifics
	private BLASTQuery emblQuery;
	
	private Context context;
	private final static String TAG = "BLASTQuerySenderTest";
	
	protected void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		query = BLASTQueryBuilder.aValidPendingBLASTQuery();
		save(query);
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		
		save(emblQuery);
		
	}
	
	private void save(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(context);
		labBook.save(query);
	}
	
	public void testBLASTQueryHasAJobIdentifierWhenSentSuccessfully(){
		
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(new BLASTQuery[]{query});
					
				}
			});
		} catch (Throwable e) {
			fail();
		}
		
		try {
			sender.get();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		}
		
		assertNotNull("was expecting a job identifier", query.getJobIdentifier());
		assertTrue( !query.getJobIdentifier().isEmpty() );		
	}
	
	public void testQueriesNotSentWhenThereIsNoWebConnection(){
		
		final BLASTQuerySender sender = new BLASTQuerySender(context){
			protected boolean connectedToWeb(){
				return false;
			}
		};
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(new BLASTQuery[]{query});					
				}
			});
		} catch (Throwable e) {
			fail();
		}
		
		Integer numberOfQueries = null;
		try {
			numberOfQueries = sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail();
		}
		
		assertEquals("Query should be pending", BLASTQuery.Status.PENDING, query.getStatus());
		assertEquals("Query should not be sent", 0, numberOfQueries.intValue());
		assertNull("Query status should be PENDING when it is valid but there is no web connection", query.getJobIdentifier());

	}
	
	public void testWeCanSendAnNCBIQuery(){
		
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(new BLASTQuery[]{query});					
				}
			});
		} catch (Throwable e) {
			fail();
		}
		
		Integer numberSent = null;
		try {
			numberSent = sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail();
		}
		Log.i(TAG, query.getJobIdentifier());
		assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
		assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
		assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
		assertEquals("Query should have been sent", 1, numberSent.intValue());
	}
	
	public void testWeCanSendAnEBI_EMBLQuery(){
		
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(new BLASTQuery[]{emblQuery});					
				}
			});
		} catch (Throwable e) {
			fail();
		}
		
		try {
			sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail(e.getMessage());
		}
		
		assertNotNull("Query was not assigned a job identifier by the service", emblQuery.getJobIdentifier());
		assertFalse("Job identifier was found to be an empty string", emblQuery.getJobIdentifier().isEmpty());
		Log.i(TAG, emblQuery.getJobIdentifier());
		assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, emblQuery.getStatus());
		
	}
	
	public void testWeCanSendMoreThanOneQuery(){
		
		final BLASTQuery[] pendingBlastQueries = manyValidPendingBLASTQueries();
		
		
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(pendingBlastQueries);					
				}
			});
		} catch (Throwable e) {
			fail();
		}
		
		Integer numberOfQueriesSent = null;
		try {
			numberOfQueriesSent = sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail(e.getMessage());
		}
		
		assertEquals("All valid queries should be sent", pendingBlastQueries.length, numberOfQueriesSent.intValue());
		
		for(BLASTQuery query : pendingBlastQueries){
			assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
			assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
			Log.i(TAG, query.getJobIdentifier());
			assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
		}		
	}
	
	private BLASTQuery[] manyValidPendingBLASTQueries(){
		BLASTQuery[] pendingBlastQueries = new BLASTQuery[2];
		
		for(int i = 0; i < pendingBlastQueries.length; i++){
			pendingBlastQueries[i] = BLASTQueryBuilder.aValidPendingBLASTQuery();
			save(pendingBlastQueries[i]);
		}
		
		return pendingBlastQueries;
	}
}
