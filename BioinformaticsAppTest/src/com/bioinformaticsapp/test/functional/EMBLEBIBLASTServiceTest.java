package com.bioinformaticsapp.test.functional;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;


public class EMBLEBIBLASTServiceTest extends TestCase {

	private BLASTSearchEngine service;
	
	public void setUp() throws Exception {
		super.setUp();
		service = new EMBLEBIBLASTService();
	}

	public void tearDown() throws Exception {
		super.tearDown();
		service.close();
	}

	public void testSubmitWeCanSubmitQueryToEMBL() {
		BLASTQuery query = aValidEMBLEBIBLASTQuery();
		
		String st = service.submit(query);
		
		boolean validId = false;
		String jobIdRegex = "ncbiblast\\-[A-Z][0-9]{8}\\-[0-9]{6}\\-[0-9]{4}\\-[0-9]{7,8}\\-[a-z]{2}";
		assertNotNull("The BLAST Job identifier was not generated", st);
		validId = st.matches(jobIdRegex);	
		Assert.assertTrue(st+" does not match regex", validId);
	}
	
	public void testWeCanPollStatusOfAQuery(){
		BLASTQuery query = aValidEMBLEBIBLASTQuery();
		String jobIdentifier = service.submit(query);
		assertNotNull("Job identifier was not generated for the query", jobIdentifier);
		
		SearchStatus status = service.pollQuery(jobIdentifier);
		SearchStatus[] validOutcomes = SearchStatus.values();
		List<SearchStatus> outcomes=  Arrays.asList(validOutcomes);
		
		boolean isValidStatus = outcomes.contains(status);
		Assert.assertTrue(isValidStatus);
	}
	
	public void testWeCannotPollQueryWhenJobIdentifierIsNull(){
		
		String jobIdentifier = null;
		try {
			@SuppressWarnings("unused")
			SearchStatus status = service.pollQuery(jobIdentifier);
			
		} catch(IllegalArgumentException e){
			
		}
	}
	
	public void testWeGetNotFoundForANonExistentBlastQuery(){
		String nonExistentJobIdentifier = "NONEXISTENT123";
		SearchStatus status = SearchStatus.UNSURE;
		status = service.pollQuery(nonExistentJobIdentifier);
		
		assertEquals(SearchStatus.NOT_FOUND, status);
	}
	
	private BLASTQuery aValidEMBLEBIBLASTQuery(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSearchParameter("email", "h.n.varambhia@gmail.com");
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		return query;
	}
	
}
