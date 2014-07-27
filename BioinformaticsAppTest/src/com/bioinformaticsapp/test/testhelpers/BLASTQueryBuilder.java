package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.BLASTQuery.Status;

public class BLASTQueryBuilder {

	public static BLASTQuery aBLASTQuery(){
		return BLASTQuery.emblBLASTQuery("blastn");
	}
	
	public static BLASTQuery aBLASTQueryWithQueryID(){
		BLASTQuery aBLASTQuery = aBLASTQuery();
		aBLASTQuery.setJobIdentifier("GHJBCNKR014");
		return aBLASTQuery;
	}
	
	public static BLASTQuery aBLASTQueryWithStatus(Status status){
		BLASTQuery blastQuery = aBLASTQuery();
		blastQuery.setStatus(status);
		return blastQuery;
	}
	
	public static BLASTQuery aValidPendingBLASTQuery() {
		BLASTQuery aBLASTQuery = aBLASTQueryWithStatus(Status.PENDING);
		aBLASTQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		switch(aBLASTQuery.getVendorID()){
		case BLASTVendor.EMBL_EBI:
			aBLASTQuery.setSearchParameter("email", "user@email.com");
			break;
		}
		
		return aBLASTQuery;
	}
	
	public static BLASTQuery validPendingEMBLBLASTQuery(){
		BLASTQuery emblQuery = BLASTQuery.emblBLASTQuery("blastn");
		emblQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		emblQuery.setSearchParameter("email", "h.n.varambhia@gmail.com");
		emblQuery.setStatus(Status.PENDING);
		return emblQuery;
	}
	
	public static BLASTQuery validPendingNCBIBLASTQuery(){
		BLASTQuery ncbiQuery = BLASTQuery.ncbiBLASTQuery("blastn");
		ncbiQuery.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		ncbiQuery.setStatus(Status.PENDING);
		return ncbiQuery;
	}
}
