package com.bioinformaticsapp.test.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;
import android.util.Log;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.OptionalParameter;
import com.bioinformaticsapp.web.BLASTQuerySender;
import com.bioinformaticsapp.web.BLASTQuerySender.Report;

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
		clearDatabase();
		
		query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setStatus(BLASTQuery.Status.PENDING);
		
		save(query);
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		
		save(emblQuery);
		
	}
	
	protected void tearDown() throws Exception {
		query = null;
		context = null;
		super.tearDown();
	}
	
	private void clearDatabase(){
		DatabaseHelper helper = new DatabaseHelper(context);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if(db.delete(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" deleted");
		}else{
			Log.i(TAG, BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" already clean");
		}
		
		if(db.delete(BLASTQuery.BLAST_QUERY_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_QUERY_TABLE+" deleted");
		}else{

			Log.i(TAG, BLASTQuery.BLAST_QUERY_TABLE+" already clean");
		}
		
		db.close();
	}
	
	private void save(BLASTQuery query){
		BLASTQueryController queryController = new BLASTQueryController(context);
		OptionalParameterController parameterController = new OptionalParameterController(context);
		long queryPrimaryKey = queryController.save(query);
		query.setPrimaryKeyId(queryPrimaryKey);
		List<OptionalParameter> parameters = new ArrayList<OptionalParameter>();
		for(OptionalParameter parameter: query.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			long parameterPrimaryKey = parameterController.save(parameter);
			parameter.setPrimaryKey(parameterPrimaryKey);
			parameters.add(parameter);
		}
		
		query.updateAllParameters(parameters);
		
		parameterController.close();
		queryController.close();
	}
	
	public void testSenderSetsQueryStatusToSUBMITTEDAfterSend(){
		
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
		
		assertEquals(Status.SUBMITTED, query.getStatus());
		
		
	}
	
	public void testSenderSetsQueryJobIdentifierWhenSentSuccessfully(){
		
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
	
	public void testSenderDoesNotSendWhenWebConnectionIsNotAvailable(){
		
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
		
		try {
			sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail();
		}
		
		assertEquals("Status was modified", BLASTQuery.Status.PENDING, query.getStatus());
		
	}
	
	public void testQueryJobIdentifierIsNullWhenThereIsNoWebConnection(){
		
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
		
		try {
			sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail();
		}
		
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
		
		BLASTQuerySender.Report sendReport = null;
		try {
			sendReport = sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail();
		}
		
		assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
		assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
		assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
		assertEquals(Status.SUBMITTED, sendReport.getOutcomeFor(query));
	}
	
	public void testWeCanSendAnEBIEMBLQuery(){
		
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
		
		final BLASTQuery[] pendingBlastQueries = new BLASTQuery[2];
		
		for(int i = 0; i < pendingBlastQueries.length; i++){
			pendingBlastQueries[i] = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
			pendingBlastQueries[i].setSearchParameter("email", "h.n.varambhia@gmail.com");
			pendingBlastQueries[i].setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
			pendingBlastQueries[i].setStatus(BLASTQuery.Status.PENDING);
			save(pendingBlastQueries[i]);
		}
		
		
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
		
		try {
			sender.get();
		} catch (InterruptedException e) {
			fail("Execution of the thread was interrupted");
		} catch (ExecutionException e) {
			fail(e.getMessage());
		}
		
		for(BLASTQuery query : pendingBlastQueries){
			assertNotNull("Query was not assigned a job identifier by the service", query.getJobIdentifier());
			assertFalse("Job identifier was found to be an empty string", query.getJobIdentifier().isEmpty());
			Log.i(TAG, query.getJobIdentifier());
			assertEquals("Query status was not updated to SUBMITTED", Status.SUBMITTED, query.getStatus());
			
		}
		
	}
	
	public void testSenderSetsAnErrorneousQueryToERROR(){
		final BLASTQuery invalidQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		invalidQuery.setStatus(BLASTQuery.Status.PENDING);
		save(invalidQuery);
		final BLASTQuerySender sender = new BLASTQuerySender(context);
		
		try {
			runTestOnUiThread(new Runnable() {
				
				public void run() {
					sender.execute(new BLASTQuery[]{invalidQuery});					
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
		
		assertEquals(BLASTQuery.Status.ERROR, invalidQuery.getStatus());
	}
	
	
}
