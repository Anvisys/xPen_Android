package net.anvisys.xpen.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.ProjectData;
import net.anvisys.xpen.Object.Expense;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DataAccess {

    private  static final String DATABASE_NAME = "GAP.db";
    private static final  String EXPENSE_TABLE = "GAP_EXPENSE";
    private static final  String QUICK_EXPENSE_TABLE = "GAP_EXPENSE";
    private static final  String PROJECT_TABLE = "GAP_PROJECT";

    private static final  String SERVER_ACTIVITY_TABLE = "GAP_ACTIVITY_SERVER";
    private static  final  int DATABASE_VERSION =9;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase myDatabase;
    private final Context mCtx;

    private static final String TABLE_CREATE_EXPENSE = "CREATE TABLE IF NOT EXISTS "
            + EXPENSE_TABLE
            + "(trans_id INTEGER PRIMARY KEY AUTOINCREMENT,expense_type VARCHAR(20),activity_id INTEGER,activity_name VARCHAR(20), expense_item VARCHAR(20), expense INTEGER, "
            +" Remark VARCHAR(20), date_time DATETIME, project_id INTEGER, project_name VARCHAR(20));";

    private static final String TABLE_CREATE_PROJECT = "CREATE TABLE IF NOT EXISTS "
            + PROJECT_TABLE
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,project_id INTEGER, project_name VARCHAR(20), project_number VARCHAR(20),client_name VARCHAR(20), project_value INTEGER, "
            +"status VARCHAR(20), expense INTEGER, received INTEGER,approver VARCHAR(20),created_by INTEGER,description VARCHAR(50));";

    private static final String TABLE_CREATE_SERVER_ACTIVITIES = "CREATE TABLE IF NOT EXISTS "
            + SERVER_ACTIVITY_TABLE
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,activity_id INTEGER, activity_name VARCHAR(50), project_name VARCHAR(20),project_by VARCHAR(50), activity_status VARCHAR(20), "
            +" expense INTEGER, received INTEGER,  description VARCHAR(50));";

    private static final String TABLE_CREATE_IMAGE="CREATE TABLE IF NOT EXISTS "
            + "Table_Image"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, Img_ID INTEGER, Image BLOB);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        //region General Function

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE_EXPENSE);
            db.execSQL(TABLE_CREATE_PROJECT);
            db.execSQL(TABLE_CREATE_SERVER_ACTIVITIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
            // db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE);
            // db.execSQL("DROP TABLE IF EXISTS " + SERVER_ACTIVITY_TABLE);

            onCreate(db);

        }
        // endregion
    }


    public DataAccess(Context mCtx) {
        this.mCtx = mCtx;
    }

    public DataAccess open() throws SQLException
    {
        try {
            mDBHelper = new DatabaseHelper(mCtx);

            myDatabase = mDBHelper.getWritableDatabase();

            return this;
        }
        catch (Exception ex)
        {
            int b=1;
            return null;
        }

    }

    public boolean ClearAll()
    {
        try {
            myDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
            myDatabase.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE);
            myDatabase.execSQL("DROP TABLE IF EXISTS " + SERVER_ACTIVITY_TABLE);
            mCtx.deleteDatabase(DATABASE_NAME);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    public void close() {
        mDBHelper.close();
    }

    //region Expense Function

    public long InsertExpense(Expense trans)
    {
        try
        {
            myDatabase.execSQL(TABLE_CREATE_EXPENSE);
            ContentValues initialValues = new ContentValues();
            initialValues.put("expense_type",trans.ExpenseType);
            initialValues.put("activity_id",trans.ActivityId);
            initialValues.put("activity_name",trans.ActivityName);
            initialValues.put("expense_item",trans.expense_item);
            initialValues.put("expense",trans.expense);
            initialValues.put("Remark",trans.Remarks);
            initialValues.put("date_time",trans.ExpenseDate);
            initialValues.put("project_id",trans.ProjectID);
            initialValues.put("project_name",trans.ProjectName);

            long result  = myDatabase.insert(EXPENSE_TABLE, null,initialValues);

            return  result;
        }
        catch (Exception ex)
        {
            return 0;
        }

    }


    public List<Expense> GetAllExpenses()
    {
        List<Expense> expenseList = new ArrayList<>();
        Expense expense;

        try{
            String selectQuery = "SELECT * FROM " + EXPENSE_TABLE +" order by trans_id asc";

            Cursor c = myDatabase.rawQuery(selectQuery,null);
            if(c!=null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        expense = new Expense();
                        expense.TranId = c.getInt(c.getColumnIndex("trans_id"));
                        expense.ExpenseType = c.getString(c.getColumnIndex("expense_type"));
                        expense.ActivityId = c.getInt(c.getColumnIndex("activity_id"));
                        expense.ActivityName = c.getString(c.getColumnIndex("activity_name"));
                        expense.expense_item = c.getString(c.getColumnIndex("expense_item"));
                        expense.expense = c.getInt(c.getColumnIndex("expense"));
                        expense.Remarks = c.getString(c.getColumnIndex("Remark"));
                        expense.ExpenseDate = c.getString(c.getColumnIndex("date_time"));
                        expense.ProjectID = c.getInt(c.getColumnIndex("project_id"));
                        expense.ProjectName = c.getString(c.getColumnIndex("project_name"));
                        expense.DataSource = "Local";
                        expenseList.add(expense);
                    }
                    while (c.moveToNext());
                }
            }
        }
        catch (Exception ex)
        {
            int a=1;
        }
        return expenseList;
    }



    // endregion



    public LinkedHashMap<Integer, ActivityData> GetServerActivities()
    {
        LinkedHashMap<Integer, ActivityData> activityList = new LinkedHashMap<Integer, ActivityData>();
        try{

            String selectQuery = "SELECT * FROM " + SERVER_ACTIVITY_TABLE +" order by id asc";

            Cursor c = myDatabase.rawQuery(selectQuery,null);
            if(c!=null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        ActivityData act = new ActivityData();
                        // act.LocalID = c.getInt(c.getColumnIndex("id"));
                        act.ActivityID = c.getInt(c.getColumnIndex("activity_id"));
                        act.ActivityName = c.getString(c.getColumnIndex("activity_name"));
                        act.ProjectName = c.getString(c.getColumnIndex("project_name"));
                        act.ApproverName = c.getString(c.getColumnIndex("project_by"));
                        act.ActivityStatus = c.getString(c.getColumnIndex("activity_status"));
                        act.ActivityDescription = c.getString(c.getColumnIndex("description"));
                        act.Expenses = c.getInt(c.getColumnIndex("expense"));
                        act.Received = c.getInt(c.getColumnIndex("received"));
                        activityList.put( act.ActivityID,act);
                    }
                    while (c.moveToNext());
                }
            }
        }
        catch (Exception ex)
        {
            int a=1;
        }
        return activityList;
    }


    public boolean DeleteActivity(int id)
    {
        try{
            String delete_query = "DELETE FROM " + SERVER_ACTIVITY_TABLE + " WHERE activity_id = '" + id + "'";
            myDatabase.execSQL(delete_query);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }



    public boolean IfActivityExist(int activity_id)
    {
        String selectQuery = "SELECT * FROM " + SERVER_ACTIVITY_TABLE +" where activity_id = " + activity_id;

        Cursor mCursor = myDatabase.rawQuery(selectQuery,null);
        if(mCursor.getCount()>0)
            return true;
        else
            return false;
    }


    public String  GetImage(int ID)
    {
        String img="";

        try {
            myDatabase.execSQL(TABLE_CREATE_IMAGE);
            String selectQuery = "SELECT  Image FROM Table_Image WHERE Img_ID = "+ ID;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    img = (c.getString(c.getColumnIndex("Image")));

                } while (c.moveToNext());
            }
            return img;
        }
        catch (Exception ex)
        {
            return img;
        }
    }

    public long  insertImage(int ID, String imgString)
    {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("Img_ID", ID);
            initialValues.put("Image", imgString);
            myDatabase.execSQL(TABLE_CREATE_IMAGE);
            myDatabase.execSQL("DELETE FROM Table_Image WHERE Img_ID = "+ ID);
            long value = myDatabase.insert("Table_Image", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public long InsertServerActivity(ActivityData act)
    {
        long result =0;
        try
        {
            myDatabase.execSQL(TABLE_CREATE_SERVER_ACTIVITIES);
            ContentValues initialValues = new ContentValues();
            initialValues.put("activity_id",act.ActivityID);
            initialValues.put("activity_name",act.ActivityName);
            initialValues.put("project_name",act.ProjectName);
            initialValues.put("project_by",act.ApproverName);
            initialValues.put("activity_status",act.ActivityStatus);
            initialValues.put("description",act.ActivityDescription);
            initialValues.put("expense",act.Expenses);
            initialValues.put("received",act.Received);
            if(IfActivityExist(act.ActivityID)) {
                DeleteActivity(act.ActivityID);
            }

            result = myDatabase.insert(SERVER_ACTIVITY_TABLE, null, initialValues);
            return  result;
        }
        catch (Exception ex)
        {
            return 0;
        }

    }


    public boolean DeleteExpense(int Id)
    {
        try{
            String delete_query = "DELETE FROM " + EXPENSE_TABLE + " WHERE trans_id = '" + Id + "'";
            myDatabase.execSQL(delete_query);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    public boolean DeleteAllExpense()
    {
        try{
            String delete_query = "DELETE FROM " + EXPENSE_TABLE;
            myDatabase.execSQL(delete_query);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }



    public long InsertProject(ProjectData prj)
    {
        long result =0;
        try
        {
            myDatabase.execSQL(TABLE_CREATE_PROJECT);
            ContentValues initialValues = new ContentValues();
            initialValues.put("project_id",prj.ProjectID);
            initialValues.put("project_name",prj.ProjectName);
            initialValues.put("project_number",prj.ProjectNumber);
            initialValues.put("client_name",prj.ClientName);
            initialValues.put("project_value",prj.ProjectValue);
            initialValues.put("status",prj.Status);
            initialValues.put("expense",prj.ExpenseAmount);
            initialValues.put("received",prj.ReceiveAmount);
            initialValues.put("approver",prj.Approver);
            initialValues.put("created_by",prj.ApproverID);
            initialValues.put("description",prj.ProjectDescription);
            if(checkIfExist(prj.ProjectID)) {
                DeleteProject(prj.ProjectID);
            }
            result = myDatabase.insert(PROJECT_TABLE, null, initialValues);
            return  result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public boolean DeleteProject(int id)
    {
        try{
            String delete_query = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = '" + id + "'";
            myDatabase.execSQL(delete_query);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }



    public boolean checkIfExist(int project_id)
    {
        String selectQuery = "SELECT * FROM " + PROJECT_TABLE +" where project_id = " + project_id;

        Cursor mCursor = myDatabase.rawQuery(selectQuery,null);
        if(mCursor.getCount()>0)
            return true;
        else
            return false;
    }



    public LinkedHashMap<Integer, ProjectData> GetProjects()
    {
        LinkedHashMap<Integer, ProjectData> projectList = new LinkedHashMap<Integer, ProjectData>();
        // List<Project> projectList = new ArrayList<>();
        try{


            String selectQuery = "SELECT * FROM " + PROJECT_TABLE +" order by id asc";

            Cursor c = myDatabase.rawQuery(selectQuery,null);
            if(c!=null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        ProjectData prj = new ProjectData();
                        //prj.LocalID = c.getInt(c.getColumnIndex("id"));
                        prj.ProjectID = c.getInt(c.getColumnIndex("project_id"));
                        prj.ProjectName = c.getString(c.getColumnIndex("project_name"));
                        prj.ProjectNumber = c.getString(c.getColumnIndex("project_number"));
                        prj.ClientName = c.getString(c.getColumnIndex("client_name"));
                        prj.ProjectValue = c.getInt(c.getColumnIndex("project_value"));
                        prj.Status = c.getString(c.getColumnIndex("status"));
                        prj.ExpenseAmount = c.getInt(c.getColumnIndex("expense"));
                        prj.ReceiveAmount = c.getInt(c.getColumnIndex("received"));
                        prj.Approver = c.getString(c.getColumnIndex("approver"));
                        prj.ApproverID = c.getInt(c.getColumnIndex("created_by"));
                        prj.ProjectDescription = c.getString(c.getColumnIndex("description"));
                        projectList.put( prj.ProjectID,prj);
                    }
                    while (c.moveToNext());
                }
            }
        }
        catch (Exception ex)
        {
            int a=1;
        }
        return projectList;
    }


}
