package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.BLASTQuery.Status;

public class BLASTQueryBuilder {

	public static BLASTQuery aBLASTQuery(){
		return BLASTQuery.emblBLASTQuery("blastn");
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
}
