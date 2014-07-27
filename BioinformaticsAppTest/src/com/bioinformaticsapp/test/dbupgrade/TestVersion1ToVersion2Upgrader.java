package com.bioinformaticsapp.test.dbupgrade;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.SearchParameter;
import com.bioinformaticsapp.domain.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.persistence.DatabaseHelper;
import com.bioinformaticsapp.persistence.SearchParameterController;
import com.bioinformaticsapp.persistence.Version1ToVersion2Upgrader;

public class TestVersion1ToVersion2Upgrader extends InstrumentationTestCase {

	
	
	private static final String TAG = "TestVersion1ToVersion2Upgrader";
	private SQLiteDatabase db;
	private Version1ToVersion2Upgrader migrator;
	private Context targetContext;
	
	protected void setUp() throws Exception {
		String createVersion1DatabaseCommand =
		        "create table "+BLASTQuery.BLAST_QUERY_TABLE+
		        " ("+BLASTJob.COLUMN_NAME_PRIMARY_KEY+" integer primary key autoincrement, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID+" text not null, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE+" text,"
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" text not null, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM+" text, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE+" text, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE+" text, "
		        
		        + BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD+" text"
		        
		        
		        +")";
		
		targetContext = getInstrumentation().getTargetContext();
		targetContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
		db = targetContext.openOrCreateDatabase(DatabaseHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
		db.execSQL(createVersion1DatabaseCommand);
		migrator = new Version1ToVersion2Upgrader(db);
	}
	
	protected void tearDown(){
		targetContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
		
	}
	
	private boolean tableExists(String tableName){
		String[] tableNameColumn = new String[]{"name"};
		String where = "type = ? AND name = ?";
		String[] typeAndName = new String[]{"table", tableName};
		Cursor c = db.query("sqlite_master", tableNameColumn, where, typeAndName, null, null, null);
		
		boolean tableExists = c.moveToFirst();
		c.close();
		
		return tableExists;
		
	}
	
	public void testWeCanRenameTheOldDataBaseToQueries_V1(){
		migrator.doSchemaMigration();
		
		boolean oldTableRenamed = tableExists(BLASTQuery.BLAST_QUERY_TABLE+"_v1");
		
		assertTrue("The version 1 table was not renamed", oldTableRenamed);
	}
	
	public void testWeCanCreateTheNewDataBaseTables(){
		migrator.doSchemaMigration();

		boolean newQueriesTableCreated = tableExists(BLASTQuery.BLAST_QUERY_TABLE);
		
		assertTrue("New Table for Queries could not be created", newQueriesTableCreated);
		
		boolean parametersTableCreated = tableExists(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE);
		
		assertTrue("Table of search parameters was not created", parametersTableCreated);
		
	}
	
	private void createSampleData(){
		ContentValues firstSample = new ContentValues();
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, "ncbi-blast-20120622-0956-45984-py");
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, "blastn");
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, Status.SUBMITTED.toString());
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE, "em_rel");
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD, "10");
		firstSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE, "50");
		
		ContentValues secondSample = new ContentValues();
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, "");
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, "blastn");
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, "");
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, Status.DRAFT.toString());
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE, "em_rel");
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD, "10");
		secondSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE, "50");
		
		ContentValues thirdSample = new ContentValues();
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, "");
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, "blastn");
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, "CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, Status.DRAFT.toString());
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE, "em_rel");
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD, "10");
		thirdSample.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE, "50");
		
		
		ContentValues[] sampleData = new ContentValues[]{firstSample, secondSample, thirdSample};
		
		for(ContentValues values: sampleData){
			
			db.insert(BLASTQuery.BLAST_QUERY_TABLE, null, values);
		
		}
		
	}
	
	public void testWeCanMoveVersion1DataToNewTables(){
		
		createSampleData();
		
		migrator.doSchemaMigration();
		migrator.moveQueriesToNewTables();
		
		//Check we have the same number of queries in old and new tables
		String oldTable = BLASTQuery.BLAST_QUERY_TABLE+"_v1";
		Cursor allQueries = db.query(oldTable, BLASTQuery.BLASTJob.FULL_PROJECTIONS, null, null, null, null, null);
		int expectedNoOfRows = allQueries.getCount();
		
		Cursor allQueriesInNewTable = db.query(BLASTQuery.BLAST_QUERY_TABLE, BLASTQuery.BLASTJob.PARENT_TABLE_FULL_PROJECTIONS, null, null, null, null, null);
		int actualNoOfRows = allQueriesInNewTable.getCount();
		
		allQueries.close();
		allQueriesInNewTable.close();

		assertEquals("Not all the queries were moved to the new table", expectedNoOfRows, actualNoOfRows);
		
		//Check that we have the correct number of search parameters
		Cursor dataInParamsTable = db.query(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, BLASTQuery.BLASTJob.OPTIONAL_PARAMETER_FULL_PROJECTION, null, null, null, null, null);
		int noOfRowsInParamsTable = dataInParamsTable.getCount();
		
		int expectedNoOfParams =  3 * expectedNoOfRows;
		
		dataInParamsTable.close();
		
		assertEquals(expectedNoOfParams, noOfRowsInParamsTable);
		
	}
	
	private List<BLASTQuery> getQueriesFromOldTable(){
		List<BLASTQuery> queriesInOldTable = new ArrayList<BLASTQuery>();
		String oldTable = BLASTQuery.BLAST_QUERY_TABLE+"_v1";
		Cursor allQueries = db.query(oldTable, BLASTQuery.BLASTJob.FULL_PROJECTIONS, null, null, null, null, BLASTJob.COLUMN_NAME_PRIMARY_KEY);
		
		
		while(allQueries.moveToNext()){
			BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
			
			int pkIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_PRIMARY_KEY);
			long pk = allQueries.getLong(pkIndex);
			query.setPrimaryKeyId(pk);
			
			int blastJobIDIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID);
			String blastJobID = allQueries.getString(blastJobIDIndex);
			query.setJobIdentifier(blastJobID);
			
			
			int programIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM);
			String program = allQueries.getString(programIndex);
			query.setBLASTProgram(program);
			
			int statusIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS);
			Status queryStatus = Status.valueOf(allQueries.getString(statusIndex));
			query.setStatus(queryStatus);
			
			int sequenceIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE);
			String sequence = allQueries.getString(sequenceIndex);
			query.setSequence(sequence);
			
			int databaseIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE);
			String database = allQueries.getString(databaseIndex);
			query.setSearchParameter("database", database);
			
			int scoreIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE);
			String score = allQueries.getString(scoreIndex);
			query.setSearchParameter("score", score);
			
			int expThresholdIndex = allQueries.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD);
			String expThreshold = allQueries.getString(expThresholdIndex);
			query.setSearchParameter("exp_threshold", expThreshold);
			
			queriesInOldTable.add(query);
			
		}
		
		allQueries.close();
		
		return queriesInOldTable;
		
	}
	
	private List<BLASTQuery> getQueriesFromNewTables(){
		Cursor allQueriesInNewTable = db.query(BLASTQuery.BLAST_QUERY_TABLE, new String[]{BLASTJob.COLUMN_NAME_PRIMARY_KEY}, null, null, null, null, BLASTJob.COLUMN_NAME_PRIMARY_KEY);
		BLASTQueryController controller = new BLASTQueryController(getInstrumentation().getTargetContext());
		SearchParameterController optionalParametersController = new SearchParameterController(getInstrumentation().getTargetContext());
		List<BLASTQuery> queriesInNewTable = new ArrayList<BLASTQuery>();
		
		while(allQueriesInNewTable.moveToNext()){
			BLASTQuery query = controller.findBLASTQueryById(allQueriesInNewTable.getLong(0));
			List<SearchParameter> parameters = optionalParametersController.getParametersForQuery(query.getPrimaryKey());
			query.updateAllParameters(parameters);
			queriesInNewTable.add(query);
		}
		
		allQueriesInNewTable.close();
		
		return queriesInNewTable;
	}
	 
	public void testWeDoNotLoseInformationDuringMigration(){
		
		createSampleData();
		
		migrator.doSchemaMigration();
		migrator.moveQueriesToNewTables();
		
		List<BLASTQuery> queriesInOldTable = getQueriesFromOldTable();
		
		List<BLASTQuery> queriesInNewTable = getQueriesFromNewTables();
		
		for(int i = 0; i < queriesInNewTable.size(); i++){
			BLASTQuery queryInOldTable = queriesInOldTable.get(i);
			BLASTQuery queryInNewTable = queriesInNewTable.get(i);
			
			assertEquals("Query primary key", queryInOldTable.getPrimaryKey(), queryInNewTable.getPrimaryKey());
			assertEquals("Query BLAST program", queryInOldTable.getBLASTProgram(), queryInNewTable.getBLASTProgram());
			assertEquals("BLAST job ID generated by webservice when query was submitted", queryInOldTable.getJobIdentifier(), queryInNewTable.getJobIdentifier());
			assertEquals("", queryInOldTable.getSequence(), queryInNewTable.getSequence());
			assertEquals(queryInOldTable.getStatus(), queryInNewTable.getStatus());
			
			assertEquals(queryInOldTable.getSearchParameter("database").getValue(), queryInNewTable.getSearchParameter("database").getValue());
			assertEquals(queryInOldTable.getSearchParameter("score").getValue(), queryInNewTable.getSearchParameter("score").getValue());
			assertEquals(queryInOldTable.getSearchParameter("exp_threshold").getValue(), queryInNewTable.getSearchParameter("exp_threshold").getValue());
			
		}
		
		
	}
	
}
