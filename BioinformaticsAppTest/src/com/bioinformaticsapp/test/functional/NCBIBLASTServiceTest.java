/**
 * 
 */
package com.bioinformaticsapp.test.functional;

import junit.framework.TestCase;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

/**
 * @author Hemal N Varambhia
 *
 */
public class NCBIBLASTServiceTest extends TestCase {

	private BLASTQuery query;
	
	private BLASTSearchEngine ncbiBLASTService;
	
	public void setUp() throws Exception {
		super.setUp();
		ncbiBLASTService = new NCBIBLASTService();
		query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
		ncbiBLASTService.close();
		ncbiBLASTService = null;
		query = null;
	}
	/**
	 * Test method for {@link com.bioinformaticsapp.blastservices.NCBIBLASTService#submit(com.bioinformaticsapp.models.BLASTQuery)}.
	 */
	public void testWeCanSubmitAValidBLASTQueryToNCBI() {
		String jobIdentifier = ncbiBLASTService.submit(query);
		
		String requestIdRegexPattern = "[A-Z0-9]{11}";
		boolean validRequestId = jobIdentifier.matches(requestIdRegexPattern);
		assertTrue(validRequestId);
	}
	
	public void testWeCanSendAQueryWithNoExponentialThreshold(){
		query.setSearchParameter("exp_threshold", "");
		String jobIdentifier = ncbiBLASTService.submit(query);
		
		
		String requestIdRegexPattern = "[A-Z0-9]{11}";
		boolean validRequestId = jobIdentifier.matches(requestIdRegexPattern);
		assertTrue(validRequestId);
	}

	public void testWeGetNotFoundForNonExistentQuery(){
		String nonExistentJobIdentifier = "NONEXISTENT123";
		SearchStatus theStatus = SearchStatus.UNSURE;
		theStatus = ncbiBLASTService.pollQuery(nonExistentJobIdentifier);
		
		assertEquals(SearchStatus.NOT_FOUND, theStatus);
	}

	/**
	 * Test method for {@link com.bioinformaticsapp.blastservices.NCBIBLASTService#retrieveBLASTResults(java.lang.String, java.lang.String)}.
	 */
	public void testWeCannotPollAQueryWhoseJobIDIsNull() {
		String jobIdentifier = null;
		
		try{
			ncbiBLASTService.pollQuery(jobIdentifier);
		}catch(IllegalArgumentException e){
			
		}catch(Exception e){
			fail();
		}
		
	}	
}
