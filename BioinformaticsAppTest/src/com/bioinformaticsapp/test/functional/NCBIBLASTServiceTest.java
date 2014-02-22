/**
 * 
 */
package com.bioinformaticsapp.test.functional;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
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
		String jobIdentifier = null;
		try {
			jobIdentifier = ncbiBLASTService.submit(query);
		} catch (IllegalBLASTQueryException e) {
			fail("Unable to send a valid query");
		}
		
		String requestIdRegexPattern = "[A-Z0-9]{11}";
		boolean validRequestId = jobIdentifier.matches(requestIdRegexPattern);
		assertTrue(validRequestId);
	}
	
	public void testWeCanSendAQueryWithNoExponentialThreshold(){
		query.setSearchParameter("exp_threshold", "");
		String jobIdentifier = null;
		try {
			jobIdentifier = ncbiBLASTService.submit(query);
		} catch (IllegalBLASTQueryException e) {
			fail("Unable to send a query with a blank exponential threshold");
		}
		
		String requestIdRegexPattern = "[A-Z0-9]{11}";
		boolean validRequestId = jobIdentifier.matches(requestIdRegexPattern);
		assertTrue(validRequestId);
	}

	/**
	 * Test method for {@link com.bioinformaticsapp.blastservices.NCBIBLASTService#pollQuery(java.lang.String)}.
	 */
	public void testWeCanPollAValidQuery() {
		String jobIdentifier = null;
		
		try {
			jobIdentifier = ncbiBLASTService.submit(query);
		} catch (IllegalBLASTQueryException e) {
			fail("Could not send a valid Query");
		}
		
		SearchStatus statusOfQuery = ncbiBLASTService.pollQuery(jobIdentifier);
		
		SearchStatus[] allStatuses = new SearchStatus[]{SearchStatus.RUNNING, SearchStatus.FINISHED};
		List<SearchStatus> listOfAllStatuses = Arrays.asList(allStatuses);
		
		boolean isAValidStatus = listOfAllStatuses.contains(statusOfQuery);
		assertTrue(isAValidStatus);
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
	
	public void testWeCannotSendAQueryThatDoesNotHaveASequence(){
		BLASTQuery blastQueryWithoutSequence = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		try{
			ncbiBLASTService.submit(blastQueryWithoutSequence);
			fail("Should not be able to send a query that does not have a sequence");
		}catch(IllegalBLASTQueryException e){
			
		}
	}
	
	public void testWeCannotSendAQueryThatDoesNotHaveAValidSequence(){
		BLASTQuery blastQueryWithInvalidSequence = new BLASTQuery("blastn", BLASTVendor.NCBI);
		blastQueryWithInvalidSequence.setSequence("VADSUC");
		
		try{
			ncbiBLASTService.submit(blastQueryWithInvalidSequence);
			
			fail("Should not be able to send a query that does not have a sequence");
		}catch(IllegalBLASTQueryException e){
			
		}
	}
		
}
