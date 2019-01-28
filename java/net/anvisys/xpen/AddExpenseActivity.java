package net.anvisys.xpen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Expense;
import net.anvisys.xpen.Object.ProjectData;
import net.anvisys.xpen.fragments.ExpenseTypeFragment;
import net.anvisys.xpen.fragments.ExpenseTypeListener;
import net.anvisys.xpen.fragments.ListenerInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddExpenseActivity extends AppCompatActivity
        implements ExpenseTypeListener
{


    TextView txtType,txtField1,txtField2;
    EditText expenseItem,editAmount,editRemarks;
    ExpenseTypeFragment expenseTypeFragment;
    Button btnSave;
    int ActivityID, ProjectID,ExpenseID;
    String ExpensePlan,ActivityName,ProjectName;
    AddExpenseListener listener;
    TextView txtExpenseDate, txtExpenseTime;
    private android.app.DatePickerDialog DatePickerDialog;

    String strExpenseDateTime;
    Calendar calSelDateTime=Calendar.getInstance();
    ActivityData selActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        try {
            Toolbar toolbar =  findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Add Expense");
            actionBar.show();

            txtType = findViewById(R.id.txtType);
            txtField1 = findViewById(R.id.txtField1);

            // txtField1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_camera_alt_black_24dp, 0);
            txtField2 = findViewById(R.id.txtField2);
            expenseItem = findViewById(R.id.expenseType);
            editAmount = findViewById(R.id.editAmount);
            editRemarks = findViewById(R.id.editRemarks);
            btnSave = findViewById(R.id.btnSave);
            txtExpenseDate = findViewById(R.id.txtExpenseDate);
            strExpenseDateTime = Utility.GetCurrentDateTimeLocal();
            txtExpenseDate.setText("Date\n"+Utility.ChangeToDateOnlyDisplayFormat(strExpenseDateTime));
            txtExpenseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePicker();
                }
            });

            txtExpenseTime = findViewById(R.id.txtExpenseTime);
            txtExpenseTime.setText("Time\n"+ Utility.ChangeToTimeOnly(strExpenseDateTime) );
            txtExpenseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PickTime();
                }
            });

            FrameLayout frame = findViewById(R.id.expenseTypeViewPager);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            expenseTypeFragment = new ExpenseTypeFragment();
            expenseTypeFragment.listener = AddExpenseActivity.this;
            ft.add(R.id.expenseTypeViewPager, expenseTypeFragment);
            ft.commit();

            Intent intent = getIntent();
            ExpensePlan = intent.getStringExtra("Type");
            if (ExpensePlan.matches("Activity")) {
                selActivity = intent.getParcelableExtra("Activity");
                ActivityID = intent.getIntExtra("ActivityID", 0);
                ActivityName = intent.getStringExtra("ActivityName");
                frame.setVisibility(View.VISIBLE);
               // txtType.setText("Add Expense to Activity");
                txtField1.setText("Activity : " + selActivity.ActivityName);
                txtField2.setText("Project Name : " + selActivity.ProjectName);
                actionBar.setTitle("Expense to Activity");
            }

            else if(ExpensePlan.matches("Project"))
            {
                frame.setVisibility(View.VISIBLE);
                ProjectID = intent.getIntExtra("ProjectID", 0);
                ProjectName = intent.getStringExtra("ProjectName");

               // txtType.setText("Quick Expense to Project");
                txtField1.setText("Project Name : " + ProjectName);
                txtField2.setVisibility(View.GONE);
                actionBar.setTitle("Expense to Project");
            }
            else if(ExpensePlan.matches("Personal"))
            {

                frame.setVisibility(View.VISIBLE);
                int ExpenseID = intent.getIntExtra("ExpenseID", 0);
                String ExpenseName = intent.getStringExtra("ExpenseName");
                //txtType.setText("Add Expense Personal");
                txtField1.setText("Expense Type : " + ExpenseName);
                txtField2.setVisibility(View.GONE);
                expenseItem.setText(ExpenseName);
                editRemarks.setText(ExpenseName);
            }
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void OnExpenseTypeSelect(int ExpenseTypeID, String ExpenseType) {
        expenseItem.setText(ExpenseType);
        editRemarks.setText(ExpenseType);
    }

    public void SaveExpense(View v) {
        btnSave.setEnabled(false);
        try {
            if (editAmount.getText().toString().matches("") || editRemarks.getText().toString().matches("")) {
                Snackbar.make(v, "Fill Required Fields", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else {
                Expense ei = new Expense();
                ei.ExpenseType = ExpensePlan;
                ei.ActivityId = ActivityID;
                ei.ActivityName = ActivityName;
                ei.ProjectID = 0;
                ei.ProjectName = "";
                ei.expense_item = expenseItem.getText().toString();
                ei.expense = Integer.parseInt(editAmount.getText().toString());
                ei.Remarks = editRemarks.getText().toString();
                ei.receive =0;
                ei.ExpenseDate = strExpenseDateTime;

                if(ExpensePlan.matches("Activity"))
                {
                    ei.ActivityId = ActivityID;
                    ei.ActivityName = ActivityName;
                    ei.ProjectID = 0;
                    ei.ProjectName = ProjectName;

                }
                else if (ExpensePlan.matches("Project")) {
                    ei.ActivityId = ProjectID;
                    ei.ActivityName = ProjectName;
                    ei.ProjectID = ProjectID;
                    ei.ProjectName = ProjectName;

                } else if (ExpensePlan.matches("Personal")){


                    ei.ActivityId = ActivityID;
                    ei.ActivityName = ActivityName;
                    ei.ProjectID = 0;
                    ei.ProjectName = "Personal";
                }
                DataAccess da = new DataAccess(this);
                da.open();
                long result = da.InsertExpense(ei);
                if (result <= 0) {
                    Snackbar.make(getCurrentFocus(), "Failed to Update", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    expenseItem.setText("");
                    editAmount.setText("");
                    editRemarks.setText("");
                    Snackbar.make(getCurrentFocus(), "Updated Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (listener != null)
                    {
                        listener.onExpenseAdded();
                    }
                    AddExpenseActivity.this.finish();
                }
            }
            btnSave.setEnabled(true);

        }
        catch (Exception ex)
        {

        }
    }

    interface AddExpenseListener
    {
        public void onExpenseAdded();
    }

    private void DatePicker()
    {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calSelDateTime.set(year, monthOfYear, dayOfMonth);

                strExpenseDateTime = Utility.GetDateToString(calSelDateTime.getTime());
                txtExpenseDate.setText("Date\n"+ Utility.ChangeToDateOnlyDisplayFormat(strExpenseDateTime));

            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }

    private void PickTime()
    {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {


                    calSelDateTime.set(Calendar.HOUR,i);
                    calSelDateTime.set(Calendar.MINUTE, i1);

                    strExpenseDateTime = Utility.GetDateToString(calSelDateTime.getTime());

                    txtExpenseTime.setText("Time\n"+Utility.ChangeToTimeOnly(strExpenseDateTime));

               /*     TimeZone timeZone = TimeZone.getDefault();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMM,yyyy HH:mm",  Locale.US);
                    simpleDateFormat.setTimeZone(timeZone);
                    txtExpenseTime.setText(simpleDateFormat.format(calSelDateTime.getTime()));
                    TimeZone UTCtimeZone = TimeZone.getTimeZone("UTC");

                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
                    newDateFormat.setTimeZone(UTCtimeZone);
                    strExpenseDateTime = newDateFormat.format(calSelDateTime.getTime());*/
                }
            },0, 0, true);
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    public void AddAttachment(View v)
    {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, APP_CONST.CAMERA_PIC_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_CONST.CAMERA_PIC_REQUEST) {
            try {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                ImageView imgPreview =  findViewById(R.id.imgPreview); //sets imageview as the bitmap
                imgPreview.setImageBitmap(image);
                imgPreview.setVisibility(View.VISIBLE);

            }
            catch (Exception ex)
            {

            }
        }
    }

}
