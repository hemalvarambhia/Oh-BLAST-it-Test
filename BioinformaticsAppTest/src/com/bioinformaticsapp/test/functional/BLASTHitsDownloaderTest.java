package com.bioinformaticsapp.test.functional;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bioinformaticsapp.blastservices.BLASTHitsDownloadingTask;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.bioinformaticsapp.test.testhelpers.SendBLASTQuery;

public class BLASTHitsDownloaderTest extends InstrumentationTestCase {

	private static final String TAG = "BLASTHitsDownloaderTest";
	private BLASTHitsDownloadingTask downloader;
	private Context context;
	
	public void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		downloader = new BLASTHitsDownloadingTask(context, new EMBLEBIBLASTService());	
	}
	
	
	public void testWeCannotDownloadResultsIfThereIsNoWebConnection() throws InterruptedException, ExecutionException{
		BLASTQuery query = BLASTQueryBuilder.aValidPendingBLASTQuery();
		downloader = new BLASTHitsDownloadingTask(context, new NCBIBLASTService()){
			protected boolean connectedToWeb(){
				return false;
			}
		};
		downloader.execute(query);
		String nameOfFile = downloader.get();
		
		assertNull("Name of file with BLAST hits", nameOfFile);
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDNCBIBLASTQuery() throws InterruptedException, ExecutionException{
		BLASTQuery ncbiQuery = BLASTQueryBuilder.validPendingNCBIBLASTQuery();
		save(ncbiQuery);
		SendBLASTQuery.sendToNCBI(context, ncbiQuery);
		waitUntilFinished(ncbiQuery);
		
		downloader.execute(ncbiQuery);
		String nameOfFile = downloader.get();
		
		assertNotNull("Name of file with BLAST hits being non-nulled: "+nameOfFile, nameOfFile);
		assertFileOnDisk(String.format("%s.xml", ncbiQuery.getJobIdentifier()));
		removeFileFromDisk(ncbiQuery);
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDEMBLQuery() throws InterruptedException, ExecutionException{
		BLASTQuery emblQuery = BLASTQueryBuilder.validPendingEMBLBLASTQuery();
		save(emblQuery);
		SendBLASTQuery.sendToEBIEMBL(context, emblQuery);
		waitUntilFinished(emblQuery);
		
		downloader.execute(emblQuery);
		
		String nameOfFile = downloader.get();
		assertNotNull("Name of file with BLAST hits being non-nulled: "+nameOfFile, nameOfFile);
		assertFileOnDisk(String.format("%s.xml", emblQuery.getJobIdentifier()));
		removeFileFromDisk(emblQuery);
	}
	
	private void assertFileOnDisk(String nameOfFile){
		try {
			//Check the file physically exists in the data directory of the app
			context.openFileInput(nameOfFile);
		} catch (FileNotFoundException e) {
			String failureMessage = String.format("Could not find file %s", nameOfFile);
			fail(failureMessage);
		}
	}
	
	private void removeFileFromDisk(BLASTQuery query){
		if(context.deleteFile(query.getJobIdentifier()+".xml")){
			Log.i(TAG, "BLAST hits file deleted");
		}
	}
	
	private void save(BLASTQuery query){
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.save(query);
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
