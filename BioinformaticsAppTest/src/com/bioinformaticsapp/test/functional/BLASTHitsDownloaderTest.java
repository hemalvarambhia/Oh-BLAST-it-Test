package com.bioinformaticsapp.test.functional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bioinformaticsapp.blastservices.BLASTHitsDownloadingTask;
import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class BLASTHitsDownloaderTest extends InstrumentationTestCase {

	private static final String TAG = "BLASTHitsDownloaderTest";
	private BLASTHitsDownloadingTask downloader;
	private Context context;
	private BLASTQuery query;
	
	public void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		downloader = new BLASTHitsDownloadingTask(context);
		query = BLASTQuery.ncbiBLASTQuery("blastn");
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setStatus(BLASTQuery.Status.PENDING);
		
		save(query);
		
	}
	
	protected void tearDown() throws Exception {
		if(query.getJobIdentifier() != null){
			if(context.deleteFile(query.getJobIdentifier()+".xml")){
				Log.i(TAG, "BLAST hits file deleted");
			}
		}
		query = null;
		super.tearDown();
	}
	
	public void testWeCannotDownloadResultsIfThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		downloader = new BLASTHitsDownloadingTask(context){
			protected boolean connectedToWeb(){
				return false;
			}
		};
		downloader.execute(query);
		String nameOfFile = downloader.get();
		
		assertNull("Name of file with BLAST hits", nameOfFile);
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDNCBIBLASTQuery() throws InterruptedException, ExecutionException{
		waitUntilSent(query);
		waitUntilFinished(query);
		
		downloader.execute(query);
		String nameOfFile = downloader.get();
		
		assertNotNull("Name of file with BLAST hits being non-nulled: "+nameOfFile, nameOfFile);
		assertFileOnDisk(String.format("%s.xml", query.getJobIdentifier()));
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDEMBLQuery() throws InterruptedException, ExecutionException{
		BLASTQuery emblQuery = BLASTQuery.emblBLASTQuery("blastn");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		save(emblQuery);
		waitUntilSent(emblQuery);
		waitUntilFinished(emblQuery);
		
		downloader.execute(emblQuery);
		
		String nameOfFile = downloader.get();
		assertNotNull("Name of file with BLAST hits being non-nulled: "+nameOfFile, nameOfFile);
		assertFileOnDisk(String.format("%s.xml", emblQuery.getJobIdentifier()));
		if(context.deleteFile(emblQuery.getJobIdentifier()+".xml")){
			Log.i(TAG, "BLAST hits file deleted");
		}
	}
	
	private void assertFileOnDisk(String nameOfFile){
		try {
			//Check the file physically exists in the data directory of the app
			FileInputStream in = context.openFileInput(nameOfFile);
			
		} catch (FileNotFoundException e) {
			String failureMessage = String.format("Could not find file %s", nameOfFile);
			fail(failureMessage);
		}
	}
	
	private void save(BLASTQuery query){
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.save(query);
	}
	
	
	private void waitUntilSent(BLASTQuery query) throws InterruptedException, ExecutionException{
		BLASTQuerySender sender = new BLASTQuerySender(context);
		sender.execute(query);
		sender.get();
	}
	
	private void waitUntilFinished(BLASTQuery query) throws InterruptedException, ExecutionException{
		StatusTranslator translator = new StatusTranslator();
		BLASTSearchEngine service = getServiceFor(query.getVendorID());
		SearchStatus current = service.pollQuery(query.getJobIdentifier());
		query.setStatus(translator.translate(current));
		while(current.equals(SearchStatus.RUNNING)){
			current = service.pollQuery(query.getJobIdentifier());
			query.setStatus(translator.translate(current));
			Log.i(TAG, current.toString());
		}
		Log.i(TAG, "BLAST search finished. Status of query is "+query.getStatus());
	}
	
	private BLASTSearchEngine getServiceFor(int vendor){
		switch(vendor){
		case BLASTVendor.NCBI:
			return new NCBIBLASTService();
		case BLASTVendor.EMBL_EBI:
			return new EMBLEBIBLASTService();
		default:
			return null;
		}
	}
	
}
