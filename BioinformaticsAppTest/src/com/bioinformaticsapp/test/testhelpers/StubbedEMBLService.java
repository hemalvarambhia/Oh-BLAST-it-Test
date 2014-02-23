package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public class StubbedEMBLService implements BLASTSearchEngine {

	public final static String A_EMBL_JOB_IDENTIFIER = "ncbiblast-R20140222-155703-0120-63596495-oy"; 
	
	public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
		return A_EMBL_JOB_IDENTIFIER;
	}

	public SearchStatus pollQuery(String jobId) {
		return SearchStatus.RUNNING;
	}

	public String retrieveBLASTResults(String jobId, String format) {
		return "";
	}

	public void close() {
	}
}
