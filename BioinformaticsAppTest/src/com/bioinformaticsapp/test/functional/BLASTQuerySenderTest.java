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
		send(new BLASTQuery[]{query});
		
		assertNotNull("was expecting a job identifier", query.getJobIdentifier());
		assertTrue( !query.getJobIdentifier().isEmpty() );		
	}
	
	public void testWeCanSendAnNCBIQuery(){
		send(new BLASTQuery[]{query});					
		
		assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
		assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
		assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
	}
	
	public void testWeCanSendAnEBI_EMBLQuery(){
		send(new BLASTQuery[]{emblQuery});					
		
		assertNotNull("Query was not assigned a job identifier by the service", emblQuery.getJobIdentifier());
		assertFalse("Job identifier was found to be an empty string", emblQuery.getJobIdentifier().isEmpty());
		Log.i(TAG, emblQuery.getJobIdentifier());
		assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, emblQuery.getStatus());
		
	}
	
	public void testWeCanSendMoreThanOneQuery(){
		
		final BLASTQuery[] pendingBlastQueries = manyValidPendingBLASTQueries();
		
		send(pendingBlastQueries);					
		
		for(BLASTQuery query : pendingBlastQueries){
			assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
			assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
			Log.i(TAG, query.getJobIdentifier());
			assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
		}		
	}
	
	private void send(final BLASTQuery[] queries){
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(queries);
					
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
