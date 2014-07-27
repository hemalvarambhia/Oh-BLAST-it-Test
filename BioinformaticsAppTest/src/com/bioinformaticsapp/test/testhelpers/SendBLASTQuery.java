package com.bioinformaticsapp.test.testhelpers;

import java.util.concurrent.ExecutionException;

import android.content.Context;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.domain.BLASTQuery;

public class SendBLASTQuery {

	public static void sendToNCBI(Context context, BLASTQuery... queries) throws InterruptedException, ExecutionException{
		BLASTQuerySender sender = new BLASTQuerySender(context, new NCBIBLASTService());
		sender.execute(queries);
		sender.get();
	}
	
	public static void sendToEBIEMBL(Context context, BLASTQuery... queries) throws InterruptedException, ExecutionException{
		BLASTQuerySender sender = new BLASTQuerySender(context, new EMBLEBIBLASTService());
		sender.execute(queries);
		sender.get();
	}
	
}
