/**
 * 
 */
package com.bioinformaticsapp.test.functional;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.BLASTSearchEngine;
import com.bioinformaticsapp.web.NCBIBLASTService;
import com.bioinformaticsapp.web.SearchStatus;

/**
 * @author Hemal N Varambhia
 *
 */
public class NCBIBLASTServiceTest extends TestCase {

	
	private static final String TAG = "NCBIBLASTServiceTest";
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
	 * Test method for {@link com.bioinformaticsapp.web.NCBIBLASTService#submit(com.bioinformaticsapp.models.BLASTQuery)}.
	 */
	public void testWeCanSubmitAValidBLASTQueryToNCBI() {
		String jobIdentifier = null;
		try {
			jobIdentifier = ncbiBLASTService.submit(query);
		} catch (IllegalBLASTQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String requestIdRegexPattern = "[A-Z0-9]{11}";
		boolean validRequestId = jobIdentifier.matches(requestIdRegexPattern);
		assertTrue(validRequestId);
		
	}

	/**
	 * Test method for {@link com.bioinformaticsapp.web.NCBIBLASTService#pollQuery(java.lang.String)}.
	 */
	public void testWeCanPollAValidQuery() {
		
		String jobIdentifier = null;
		
		try {
			jobIdentifier = ncbiBLASTService.submit(query);
		} catch (IllegalBLASTQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * Test method for {@link com.bioinformaticsapp.web.NCBIBLASTService#retrieveBLASTResults(java.lang.String, java.lang.String)}.
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
