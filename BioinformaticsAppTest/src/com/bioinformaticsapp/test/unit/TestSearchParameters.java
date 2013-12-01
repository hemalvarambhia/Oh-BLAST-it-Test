package com.bioinformaticsapp.test.unit;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;

import junit.framework.TestCase;

import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;

public class TestSearchParameters extends TestCase {

	public void testWeCanCreateDefaultOptionalParametersForEMBLEBI(){	
		List<SearchParameter> searchParameters = SearchParameter.createDefaultParametersFor(BLASTVendor.EMBL_EBI);
		List<SearchParameter> expectedOptionalParameters = new ArrayList<SearchParameter>();
		expectedOptionalParameters.add(new SearchParameter("database", "em_rel_fun"));
		expectedOptionalParameters.add(new SearchParameter("exp_threshold", "10"));
		expectedOptionalParameters.add(new SearchParameter("score", "50"));
		expectedOptionalParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
		expectedOptionalParameters.add(new SearchParameter("email", ""));
		
		MatcherAssert.assertThat(searchParameters, is(equalTo(expectedOptionalParameters)));
	}
	
	public void testWeCanCreateDefaultOptionalParametersForNCBI(){
		List<SearchParameter> searchParameters = SearchParameter.createDefaultParametersFor(BLASTVendor.NCBI);
		List<SearchParameter> expectedOptionalParameters = new ArrayList<SearchParameter>();
		
		expectedOptionalParameters.add(new SearchParameter("database", "nr"));
		expectedOptionalParameters.add(new SearchParameter("word_size", "28"));
		expectedOptionalParameters.add(new SearchParameter("exp_threshold", "10"));
		expectedOptionalParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
		
		assertEquals(expectedOptionalParameters, searchParameters);
	}
}
