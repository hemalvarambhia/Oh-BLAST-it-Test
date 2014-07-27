package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bioinformaticsapp.blastservices.BLASTHitsDownloadingTask;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.StubbedBLASTSearchEngine;

public class BLASTHitsDownloaderUnitTest extends InstrumentationTestCase {

	private static final String TAG = "BLASTHitsDownloaderTest";
	private BLASTHitsDownloadingTask downloaderTask;
	private Context context;
	
	public void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		downloaderTask = new BLASTHitsDownloadingTask(context, new StubbedBLASTSearchEngine());
	}
	
	public void testWeCanDownloadResultsOfAFINISHEDNCBIBLASTQuery() throws InterruptedException, ExecutionException{
		BLASTQuery ncbiQuery = BLASTQueryBuilder.aBLASTQueryWithQueryID();
		
		downloaderTask.execute(ncbiQuery);
		downloaderTask.get();
		
		String hitsFile = String.format("%s.xml", ncbiQuery.getJobIdentifier());
		assertFileOnDisk(hitsFile);
		
		removeFileFromDisk(ncbiQuery);
	}
	
	private void assertFileOnDisk(String nameOfFile){
		assertThat("Name of file with BLAST hits is null", nameOfFile, is(notNullValue()));
		try {
			//Check the file physically exists in the data directory of the app
			FileInputStream file = context.openFileInput(nameOfFile);
			assertThat("The file has no content", file.getChannel().size() > 0l);
		} catch (FileNotFoundException e) {
			String failureMessage = String.format("Could not find file %s", nameOfFile);
			fail(failureMessage);
		} catch (IOException e) {
			fail("There was a problem reading the size of the file");
		}
	}
	
	private void removeFileFromDisk(BLASTQuery query){
		String hitsFile = String.format("%s.xml", query.getJobIdentifier());
		if(context.deleteFile(hitsFile)){
			String deletedMessage = String.format("BLAST hits file %s deleted", hitsFile);
			Log.i(TAG, deletedMessage);
		}
	}
}
