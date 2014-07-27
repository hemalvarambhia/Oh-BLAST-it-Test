package com.bioinformaticsapp.test.functional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.io.BLASTHitsLoadingTask;

public class TestBLASTHitsLoadingTask extends InstrumentationTestCase {

	private BLASTHitsLoadingTask loader;
	
	private AssetManager assetManager;
	
	public void setUp() throws Exception {
		super.setUp();
		assetManager = getInstrumentation().getContext().getResources().getAssets();
	}
	
	public void testWeCanLoadBLASTHitsFromNCBI() throws IOException{
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		ncbiQuery.setJobIdentifier("YAMJ8623016");
		
		loader = new BLASTHitsLoadingTask(ncbiQuery.getVendorID());
		InputStream blastHitsFile = assetManager.open(ncbiQuery.getJobIdentifier()+".xml");
		
		loader.execute(blastHitsFile);
		
		List<Map<String, String>> mapOfBLASTHits = null;
		try {
			mapOfBLASTHits = loader.get();
		} catch (InterruptedException e) {
			fail("There was a problem getting the BLAST Hits from the file");
		} catch (ExecutionException e) {
			fail("There was a problem getting the BLAST Hits from the file");
		}
		
		assertEquals("Number of Hits", 100, mapOfBLASTHits.size());
		
	}
	
	public void testWeCanLoadBLASTHitsFromEMBLEBI() throws IOException{
		BLASTQuery ebiemblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		ebiemblQuery.setJobIdentifier("ncbiblast-R20120418-133731-0240-81389354-pg");
		
		loader = new BLASTHitsLoadingTask(ebiemblQuery.getVendorID());
		InputStream blastHitsFile = assetManager.open(ebiemblQuery.getJobIdentifier()+".xml");
		
		loader.execute(blastHitsFile);
		
		List<Map<String, String>> mapOfBLASTHits = null;
		try {
			mapOfBLASTHits = loader.get();
		} catch (InterruptedException e) {
			fail("There was a problem getting the BLAST Hits from the file");
		} catch (ExecutionException e) {
			fail("There was a problem getting the BLAST Hits from the file");
		}
		
		assertEquals("Number of Hits", 144, mapOfBLASTHits.size());
		
	}
	
	public void testWeGetNoHitsIfTheFileDoesNotExist(){
		
		FileInputStream fileDoesNotExist = null;
		
		loader = new BLASTHitsLoadingTask(BLASTVendor.EMBL_EBI);
		
		loader.execute(fileDoesNotExist);
		List<Map<String, String>> noHits = null;
		try {
			noHits = loader.get();
		} catch (InterruptedException e) {
			fail("The task was interrupted");
		} catch (ExecutionException e) {
			fail("There was an unexpected exception thrown while performing the task");
		}
		
		assertNotNull(noHits);
		
		assertTrue(noHits.isEmpty());
		
	}
}
