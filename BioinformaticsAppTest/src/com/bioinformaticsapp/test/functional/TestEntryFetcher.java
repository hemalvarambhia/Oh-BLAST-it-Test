package com.bioinformaticsapp.test.functional;

import java.util.Map;

import junit.framework.TestCase;

import com.bioinformaticsapp.web.EntryFetcher;

public class TestEntryFetcher extends TestCase {

	private static final String TAG = "TestEntryFetcher";
	private EntryFetcher fetcher;
	protected void setUp() throws Exception {
		super.setUp();
		fetcher = new EntryFetcher("AY666596.1");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		fetcher.closeHttpConnection();
		fetcher = null;
	}

	public void testFetchEntryThrowsExceptionWhenAccessionNumberEmpty() {
		String emptyAccessionNumber = "";
		boolean threwException = false;
		try{
			EntryFetcher invalidEntryFetcher = new EntryFetcher(emptyAccessionNumber);
			
		}catch(Exception e){
			threwException = true;
		}
		
		assertTrue(threwException);
	}
	

	public void testWeCanFetchAnnotations(){
		
		Map<String, String> entry = fetcher.getOrganism();
		
		String expectedScientificName = "Carpodacus mexicanus";
		String obtainedScientificName = entry.get("scientificName");
		assertEquals(expectedScientificName, obtainedScientificName);
	}
	
	public void testWeCanFetchAnnotationsForAccessionNumberFromNCBIResults(){
		//HQ998156 was yielded from an NCBI webservice run:
		EntryFetcher entryFetcher = new EntryFetcher("HQ998156");
		Map<String, String> entry = entryFetcher.getOrganism();
		
		String expectedScientificName = "Ardeola ralloides";
		String obtainedScientificName = entry.get("scientificName");
		assertEquals(expectedScientificName, obtainedScientificName);
	}
	
}
