package com.bioinformaticsapp.test.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.SearchParameter;

public class TestSearchParameters extends TestCase {

	public void testWeCanCreateDefaultOptionalParametersForEMBLEBI(){	
		List<SearchParameter> searchParameters = SearchParameter.createDefaultParametersFor(BLASTVendor.EMBL_EBI);
		
		assertThat(searchParameters, is(emblDefaults()));
	}
	
	public void testWeCanCreateDefaultOptionalParametersForNCBI(){
		List<SearchParameter> searchParameters = SearchParameter.createDefaultParametersFor(BLASTVendor.NCBI);
		
		assertThat(searchParameters, is(ncbiDefaults()));
	}
	
	private List<SearchParameter> ncbiDefaults(){
		List<SearchParameter> ncbiDefaultParameters = new ArrayList<SearchParameter>();
		ncbiDefaultParameters.add(new SearchParameter("database", "nr"));
		ncbiDefaultParameters.add(new SearchParameter("word_size", "28"));
		ncbiDefaultParameters.add(new SearchParameter("exp_threshold", "10"));
		ncbiDefaultParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
		
		return ncbiDefaultParameters;
	}
	
	private List<SearchParameter> emblDefaults(){
		List<SearchParameter> emblDefaultParameters = new ArrayList<SearchParameter>();
		emblDefaultParameters.add(new SearchParameter("database", "em_rel_fun"));
		emblDefaultParameters.add(new SearchParameter("exp_threshold", "10"));
		emblDefaultParameters.add(new SearchParameter("score", "50"));
		emblDefaultParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
		emblDefaultParameters.add(new SearchParameter("email", ""));
		
		return emblDefaultParameters;
	}
}
