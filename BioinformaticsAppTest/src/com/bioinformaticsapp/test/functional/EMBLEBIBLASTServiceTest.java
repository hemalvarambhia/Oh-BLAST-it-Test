package com.bioinformaticsapp.test.functional;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.util.Log;

import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.BLASTSequenceQueryingService;
import com.bioinformaticsapp.web.EMBLEBIBLASTService;


public class EMBLEBIBLASTServiceTest extends TestCase {

	private static final String TAG = "EMBLEBIBLASTServiceTest";
	private BLASTSequenceQueryingService service;
	
	protected void setUp() throws Exception {
		super.setUp();
		service = new EMBLEBIBLASTService();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		service.close();
	}

	public void testSubmitWeCanSubmitQueryToEMBL() {
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSearchParameter("email", "h.n.varambhia@gmail.com");
		//
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		boolean validId = false;
		String st = "";
		String jobIdRegex = "ncbiblast\\-[A-Z][0-9]{8}\\-[0-9]{6}\\-[0-9]{4}\\-[0-9]{7,8}\\-[a-z]{2}";
		try {
			st = service.submit(query);
		} catch (IllegalBLASTQueryException e) {			
			e.printStackTrace();
		}
		assertNotNull("The BLAST Job identifier was not generated", st);
		validId = st.matches(jobIdRegex);	
		
		Assert.assertTrue(st+" does not match regex", validId);
		
	}
	
	public void testWeCanPollStatusOfAQuery(){
		Status[] validOutcomes = Status.values();
		List<Status> outcomes=  Arrays.asList(validOutcomes);
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSearchParameter("email", "h.n.varambhia@gmail.com");
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		String jobIdentifier = "";
		try {
			jobIdentifier = service.submit(query);
			
		} catch (IllegalBLASTQueryException e) {
			fail(Arrays.toString(e.getStackTrace()));
		}
		assertNotNull("Job identifier was not generated for the query", jobIdentifier);
		
		Status status = service.pollQuery(jobIdentifier);
		
		boolean isValidStatus = outcomes.contains(status);
		
		Assert.assertTrue(isValidStatus);
	}
	
	public void testWeCannotPollQueryWhenJobIdentifierIsNull(){
		
		String jobIdentifier = null;
		try {
			@SuppressWarnings("unused")
			Status status = service.pollQuery(jobIdentifier);
			
		} catch(IllegalArgumentException e){
			
		}
		
	}
	
	public void testWeGetNotFoundForANonExistentBlastQuery(){
		String nonExistentJobIdentifier = "NONEXISTENT123";
		
		Status status = Status.UNSURE;
		status = service.pollQuery(nonExistentJobIdentifier);
		
		assertEquals(Status.NOT_FOUND, status);
		
	}
	
	public void testWeCannotSendAQueryWhichDoesNotHaveASequence(){
		BLASTQuery queryWithoutSequence = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		
		try {
			service.submit(queryWithoutSequence);
			fail("Should not be able to send a query which does not have a sequence");
		} catch (IllegalBLASTQueryException e) {
			
		}
		
	}
	
}
