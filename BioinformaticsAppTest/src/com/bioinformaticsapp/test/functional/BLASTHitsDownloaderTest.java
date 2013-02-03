package com.bioinformaticsapp.test.functional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.bioinformaticsapp.web.BLASTHitsDownloadingTask;
import com.bioinformaticsapp.web.BLASTQuerySender;
import com.bioinformaticsapp.web.BLASTSearchEngine;
import com.bioinformaticsapp.web.EMBLEBIBLASTService;
import com.bioinformaticsapp.web.NCBIBLASTService;
import com.bioinformaticsapp.web.SearchStatus;

public class BLASTHitsDownloaderTest extends InstrumentationTestCase {

	private static final String TAG = "BLASTHitsDownloaderTest";

	private BLASTQuerySender sender;
	
	private BLASTHitsDownloadingTask downloader;
	
	private Context context;
	
	private BLASTQuery query;
	
	public void setUp() throws Exception {
		context = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(context);
		helper.cleanDatabase();
		
		sender = new BLASTQuerySender(context);
		downloader = new BLASTHitsDownloadingTask(context);
		query = new BLASTQuery("blastn", BLASTVendor.NCBI);
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
	}
	
	private void save(BLASTQuery query){
		BLASTQueryController queryController = new BLASTQueryController(context);
		SearchParameterController parameterController = new SearchParameterController(context);
		long queryPrimaryKey = queryController.save(query);
		query.setPrimaryKeyId(queryPrimaryKey);
		List<SearchParameter> parameters = new ArrayList<SearchParameter>();
		for(SearchParameter parameter: query.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			long parameterPrimaryKey = parameterController.save(parameter);
			parameter.setPrimaryKey(parameterPrimaryKey);
			parameters.add(parameter);
		}
		
		query.updateAllParameters(parameters);
		
		parameterController.close();
		queryController.close();
	}
	
	
	private void waitUntilSent(BLASTQuery query) throws InterruptedException, ExecutionException{
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
		
		try {
			//Check the file physically exists in the data directory of the app
			FileInputStream in = context.openFileInput(query.getJobIdentifier()+".xml");
		} catch (FileNotFoundException e) {
			fail("Could not find file for Query");
		}
		
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDEMBLQuery() throws InterruptedException, ExecutionException{
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setStatus(BLASTQuery.Status.PENDING);
		save(emblQuery);
		waitUntilSent(emblQuery);
		waitUntilFinished(emblQuery);
		
		downloader.execute(emblQuery);
		
		String nameOfFile = downloader.get();
		
		assertNotNull("Name of file with BLAST hits being non-nulled: "+nameOfFile, nameOfFile);
		
		try {
			//Check the file physically exists in the data directory of the app
			FileInputStream in = context.openFileInput(emblQuery.getJobIdentifier()+".xml");
			
		} catch (FileNotFoundException e) {
			fail("Could not find file for Query");
		}
		
		if(context.deleteFile(emblQuery.getJobIdentifier()+".xml")){
			Log.i(TAG, "BLAST hits file deleted");
		}
		
	}
	
}
