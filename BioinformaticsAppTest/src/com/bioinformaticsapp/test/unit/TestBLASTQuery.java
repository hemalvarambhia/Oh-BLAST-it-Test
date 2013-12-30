package com.bioinformaticsapp.test.unit;

import junit.framework.TestCase;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNot.*;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;


public class TestBLASTQuery extends TestCase {

	public void testWeCanSetUpDraftNucleotideEMBLQueryWithDefaults(){
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		assertEMBLQueryHasSensibleDefaults(emblQuery);
	}
	
	public void testWeCanSetUpDraftNucleotideNCBIQueryWithDefaults(){
		
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		assertNCBIQueryHasSensibleDefaults(ncbiQuery);
	}
	
	public void testEqualsMethodWhenPrimaryKeysAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(2l);
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenProgramsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastp", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenBLASTJobIdsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setJobIdentifier("ABC-123");
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenSequencesAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAG");
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenJobStatusesAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setPrimaryKeyId(1l);
		anotherNCBIQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		anotherNCBIQuery.setStatus(Status.ERROR);
		
		assertThat(ncbiQuery , is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenVendorsAreDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		
		assertThat(ncbiQuery, is(not(equalTo(emblQuery))));
	}
	
	public void testEqualsMethodWhenDatabaseOptionalParameterIsDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("database", "gss");
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenScoreOptionalParameterIsDifferent(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("exp_threshold", "1000");
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	public void testEqualsMethodWhenNoOfOptionalParametersIsDifferent(){
	 	BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		BLASTQuery anotherNCBIQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		anotherNCBIQuery.setSearchParameter("some_parameter", "xyz");
		
		assertThat(ncbiQuery, is(not(equalTo(anotherNCBIQuery))));
	}
	
	//Here we check that the equals method is reflexive (x.equals(x))
	public void testEqualsMethodBLASTQueriesAreTheSame(){
	 	BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setPrimaryKeyId(1l);
		
		assertThat(ncbiQuery, is(equalTo(ncbiQuery)));
	}
	
	public void testQueryIsNotValidWhenDNASequenceIsNotValid(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		query.setSequence("VADSUC");

		boolean isInvalidQuery =  !query.isValid();
		assertThat("Queries with badly formed sequences", isInvalidQuery);
	}
	
	public void testQueryIsValidWhenDNASequenceIsValid(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		
		assertThat("Query with a valid sequence should be valid", query.isValid());
	}
	
	public void testQueryIsInvalidWhenSequenceIsNull(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		
		boolean isInvalidQuery =  !query.isValid();
		
		assertThat("A query with no sequence is invalid", isInvalidQuery);
	}
	
	public void testQueryIsInvalidWhenSequenceIsEmpty(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("");
		
		boolean isInvalidQuery =  !query.isValid();
		assertThat("A query with a blank sequence is invalid", isInvalidQuery);
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
	
	public void testCloneIsTheSameAsTheOriginal() throws CloneNotSupportedException{
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setPrimaryKeyId(2l);
		query.setJobIdentifier("ncbiblast-20130923-23949sd");
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setStatus(Status.FINISHED);
		//TODO - test clone search parameters
		query.setSearchParameter("exp_threshold", "100");
		BLASTQuery clone = (BLASTQuery)query.clone();
		
		assertThat("Clone should be an exact copy of the original", clone, is(equalTo(query)));
	}
	
	private void assertEMBLQueryHasSensibleDefaults(BLASTQuery query){
		assertThat(query.getBLASTProgram(), is("blastn"));
		assertThat(query.getSearchParameter("database"), is(new SearchParameter("database", "em_rel_fun")));
		assertThat(query.getSearchParameter("exp_threshold"), is(new SearchParameter("exp_threshold", "10")));
		assertThat(query.getSearchParameter("score"), is(new SearchParameter("score", "50")));
		assertThat(query.getSearchParameter("match_mismatch_score"), is(new SearchParameter("match_mismatch_score", "1,-2")));
		assertThat(query.getDestination(), is("EMBL-EBI"));
	}
	
	private void assertNCBIQueryHasSensibleDefaults(BLASTQuery query){
		assertThat(query.getBLASTProgram(), is("blastn"));
		assertThat(query.getSearchParameter("database"), is(new SearchParameter("database", "nr")));
		assertThat(query.getSearchParameter("word_size"), is(new SearchParameter("word_size", "28")));
		assertThat(query.getSearchParameter("exp_threshold"), is(new SearchParameter("exp_threshold", "10")));
		assertThat(query.getSearchParameter("match_mismatch_score"), is(new SearchParameter("match_mismatch_score", "1,-2")));
		assertThat(query.getDestination(), is("NCBI"));
	}
	
}
