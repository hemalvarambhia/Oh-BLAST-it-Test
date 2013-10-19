package com.bioinformaticsapp.test.unit;

import junit.framework.TestCase;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;


public class TestBLASTQuery extends TestCase {

	public void testWeCanSetUpDraftNucleotideEMBLQueryWithDefaults(){
		
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		assertEquals("blastn", emblQuery.getBLASTProgram());
		assertEquals(new SearchParameter("database", "em_rel_fun"), emblQuery.getSearchParameter("database"));
		assertEquals(new SearchParameter("exp_threshold", "10"), emblQuery.getSearchParameter("exp_threshold"));
		assertEquals(new SearchParameter("score", "50"), emblQuery.getSearchParameter("score"));
		assertEquals(new SearchParameter("match_mismatch_score", "1,-2"), emblQuery.getSearchParameter("match_mismatch_score"));
		assertEquals("EMBL-EBI", emblQuery.getDestination());
	}
	
	public void testWeCanSetUpDraftNucleotideNCBIQueryWithDefaults(){
		
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		assertEquals(new SearchParameter("database", "nr"), ncbiQuery.getSearchParameter("database"));
		assertEquals(new SearchParameter("word_size", "28"), ncbiQuery.getSearchParameter("word_size"));
		assertEquals(new SearchParameter("exp_threshold", "10"), ncbiQuery.getSearchParameter("exp_threshold"));
		assertEquals(new SearchParameter("match_mismatch_score", "1,-2"), ncbiQuery.getSearchParameter("match_mismatch_score"));
		assertEquals("NCBI", ncbiQuery.getDestination());
	}
	
	public void testEqualsMethodWhenPrimaryKeysAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(2l);
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenProgramsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastp", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenBLASTJobIdsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setJobIdentifier("ABC-123");
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenSequencesAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAG");
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenJobStatusesAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		anotherNCBIQuery.setStatus(Status.ERROR);
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenVendorsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		assertFalse(ncbiQuery.equals(emblQuery));
	}
	
	public void testEqualsMethodWhenDatabaseOptionalParameterIsDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("database", "gss");
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenScoreOptionalParameterIsDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("exp_threshold", "1000");
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	public void testEqualsMethodWhenNoOfOptionalParametersIsDifferent(){
	 	BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("some_parameter", "xyz");
		
		assertFalse(ncbiQuery.equals(anotherNCBIQuery));
	}
	
	//Here we check that the equals method is reflexive (x.equals(x))
	public void testEqualsMethodBLASTQueriesAreTheSame(){
	 	BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		
		assertTrue(ncbiQuery.equals(ncbiQuery));
	}
	
	public void testQueryIsNotValidWhenDNASequenceIsNotValid(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("VADSUC");
		
		assertFalse(query.isValid());
	}
	
	public void testQueryIsValidWhenDNASequenceIsValid(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		assertTrue(query.isValid());
	}
	
	public void testQueryIsInvalidWhenSequenceIsNull(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		assertFalse(query.isValid());
	}
	
	public void testQueryIsInvalidWhenSequenceIsEmpty(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("");
		
		assertFalse(query.isValid());
	}

	public void testQueryIsInvalidIfEmailAddressIsInvalid(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setSearchParameter("email", "test@email@com");
		
		assertFalse("Query should be invalid when e-mail is invalid", query.isValid());
	}
	
	public void testQueryIsInvalidIfEmailAddressIsNull(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setSearchParameter("email", null);
		
		assertFalse("Query should be invalid when e-mail is invalid", query.isValid());
	}
	
	public void testQueryIsInvalidIfEmailAddressIsBlank(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setSearchParameter("email", "");
		
		assertFalse("Query should be invalid when e-mail is invalid", query.isValid());
	}
	
}
