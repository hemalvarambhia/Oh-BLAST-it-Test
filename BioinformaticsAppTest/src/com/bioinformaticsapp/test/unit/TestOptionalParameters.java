package com.bioinformaticsapp.test.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import android.test.MoreAsserts;

import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.OptionalParameter;

public class TestOptionalParameters extends TestCase {

	private static final String TAG = "TestOptionalParameters";
	
	public void testWeCanCreateDefaultOptionalParametersForEMBLEBI(){
		
		List<OptionalParameter> optionalParameters = OptionalParameter.createDefaultParametersFor(BLASTVendor.EMBL_EBI);
		
		List<OptionalParameter> expectedOptionalParameters = new ArrayList<OptionalParameter>();
		
		expectedOptionalParameters.add(new OptionalParameter("database", "em_rel_fun"));
		expectedOptionalParameters.add(new OptionalParameter("exp_threshold", "10"));
		expectedOptionalParameters.add(new OptionalParameter("score", "50"));
		expectedOptionalParameters.add(new OptionalParameter("match_mismatch_score", "1,-2"));
		expectedOptionalParameters.add(new OptionalParameter("email", ""));
		
		
		assertEquals(expectedOptionalParameters, optionalParameters);
		
	}
	
	public void testWeCanCreateDefaultOptionalParametersForNCBI(){
		List<OptionalParameter> optionalParameters = OptionalParameter.createDefaultParametersFor(BLASTVendor.NCBI);
		
		List<OptionalParameter> expectedOptionalParameters = new ArrayList<OptionalParameter>();
		
		expectedOptionalParameters.add(new OptionalParameter("database", "nr"));
		expectedOptionalParameters.add(new OptionalParameter("word_size", "28"));
		expectedOptionalParameters.add(new OptionalParameter("exp_threshold", "10"));
		expectedOptionalParameters.add(new OptionalParameter("match_mismatch_score", "1,-2"));
		
		
		assertEquals(expectedOptionalParameters, optionalParameters);
		
	}
	
}
