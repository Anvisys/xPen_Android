package net.anvisys.xpen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;
import net.anvisys.xpen.Object.Expense;
import net.anvisys.xpen.fragments.ActivityFragment;
import net.anvisys.xpen.fragments.ExpenseTypeFragment;
import net.anvisys.xpen.fragments.ExpenseTypeListener;
import net.anvisys.xpen.fragments.ListenerInterface;
import net.anvisys.xpen.fragments.ProjectFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements ListenerInterface, ExpenseTypeListener {

    TabLayout tab_layout;
    View viewApproval;
    View managerBar,adminBar;
    Profile myProfile;
    List<Expense> expenseData;
    ActivityData selActivity;
    View viewActivityActions,viewProjectActions,ExpManager,personal;
    ExpenseTypeTabAdapter expenseTypeadapter;
    int selPrjID,selExpenseID;
    String selPrjName;
    ProjectData selProject;
    TextView chartTitle,txtInProgress,txtApproved,txtSubmitted,txtActivity1,txtActivity2;
    private Integer ClickCount=0;
    private long prevTime = 0;
    PieChart pieChart;
    BarChart barChart;
   Button showLocalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Dashboard");
        actionBar.show();


        viewActivityActions = findViewById(R.id.viewActivityActions);     // this is inside activity_project_Bar
        viewProjectActions = findViewById(R.id.viewProjectActions);       // this is inside activity_project_Bar
        pieChart = findViewById(R.id.pie_chart);
        showLocalData = findViewById(R.id.showLocalData);

        managerBar = findViewById(R.id.action_bar_manager);

        adminBar = findViewById(R.id.action_bar_admin);
        barChart = findViewById(R.id.barchart);
        txtInProgress = findViewById(R.id.txtInProgress);
        txtApproved = findViewById(R.id.txtApproved);
        txtSubmitted = findViewById(R.id.txtSubmitted);
        txtActivity1 = findViewById(R.id.txtActivity1);
        txtActivity2 = findViewById(R.id.txtActivity2);
        ExpManager = findViewById(R.id.ExpManager);
        personal = findViewById(R.id.personal);

        myProfile = Session.GetUser(getApplicationContext());

        tab_layout = findViewById(R.id.tab_layout);

        tab_layout.addTab(tab_layout.newTab().setText("Activity"));
        tab_layout.addTab(tab_layout.newTab().setText("Project"));
        tab_layout.addTab(tab_layout.newTab().setText("Personal"));

        final ViewPager viewPager = findViewById(R.id.pager);
        expenseTypeadapter = new ExpenseTypeTabAdapter (getSupportFragmentManager(), tab_layout.getTabCount());

        viewPager.setAdapter(expenseTypeadapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                InitiateActionBar();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        InitiateActionBar();
        CreateExpenseChart();
        CreateDailyExpChart();

    }

    private void InitiateActionBar()
    {
        viewActivityActions.setVisibility(View.GONE);
        viewProjectActions.setVisibility(View.GONE);
        managerBar.setVisibility(View.GONE);

        if(myProfile.Role.matches("Admin"))
        {
            adminBar.setVisibility(View.VISIBLE);
        }
        else
        {
            adminBar.setVisibility(View.GONE);
        }

    }


    @Override
    public void OnActivitySelect(ActivityData actData) {
        selActivity = actData;


        viewActivityActions.setVisibility(View.VISIBLE);
        viewProjectActions.setVisibility(View.GONE);


        if(myProfile.Role.matches("Manager") || myProfile.Role.matches("Admin"))
        {
         managerBar.setVisibility(View.GONE);
         ExpManager.setVisibility(View.VISIBLE);
        }
        else
        {
           managerBar.setVisibility(View.GONE);
            ExpManager.setVisibility(View.GONE);
        }


    }

    @Override
    public void OnProjectSelect(ProjectData project) {

        selProject = project;
        selPrjID = project.ProjectID;
        selPrjName = project.ProjectName;

        viewActivityActions.setVisibility(View.GONE);
        viewProjectActions.setVisibility(View.VISIBLE);

        if(myProfile.Role.matches("Manager") || myProfile.Role.matches("Admin"))
        {
            managerBar.setVisibility(View.VISIBLE);

        }
        else
        {
            managerBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnDeSelect() {

    }

    @Override
    public void OnExpenseTypeSelect(int ExpenseTypeID, String ExpenseType) {

    }
    class ExpenseTypeTabAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public ExpenseTypeTabAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    ActivityFragment tab1 = new ActivityFragment();
                    tab1.listener = DashboardActivity.this;

                    return tab1;
                case 1:
                    ProjectFragment tab2 = new ProjectFragment();
                    tab2.listener = DashboardActivity.this;
                    return tab2;
                case 2:
                    ExpenseTypeFragment tab3 = new ExpenseTypeFragment();
                    tab3.listener = DashboardActivity.this;
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return mNumOfTabs;
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }
    }

    public void OnAction(View v)
    {
        int id = v.getId();
        if(id==R.id.actionAddExpense)
        {
            Intent newExpenseIntent = new Intent(DashboardActivity.this, AddExpenseActivity.class);
            newExpenseIntent.putExtra("Type", "Activity");
            newExpenseIntent.putExtra("ActivityID", selActivity.ActivityID);
            newExpenseIntent.putExtra("ActivityName", selActivity.ActivityName);
            newExpenseIntent.putExtra("Activity", selActivity);
            startActivity(newExpenseIntent);
        }

        else if(id==R.id.actionShowActivityDetails)
        {
            String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Activity/" + selActivity.ActivityID;
            Intent actIntent = new Intent(DashboardActivity.this, ReportActivity.class);
            actIntent.putExtra("Type", "Activity");
            actIntent.putExtra("Activity", selActivity);
            actIntent.putExtra("url", url);
            startActivity(actIntent);
        }

        else if(id==R.id.actionAddProjectExpense)
        {
            Intent newExpenseIntent = new Intent(DashboardActivity.this, AddExpenseActivity.class);
            newExpenseIntent.putExtra("Type", "Project");
            newExpenseIntent.putExtra("ProjectID", selProject.ProjectID);
            newExpenseIntent.putExtra("ProjectName", selProject.ProjectName);
            newExpenseIntent.putExtra("Project", selProject);
            startActivity(newExpenseIntent);
        }
        else if(id==R.id.actionProjectRemove)
        {

            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("Are you sure to remove project ?");
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    da.DeleteProject(selProject.ProjectID);
                    da.close();
                }
            });
            dialog.create();
            dialog.show();
            expenseTypeadapter.notifyDataSetChanged();
            managerBar.setVisibility(View.GONE);
            viewProjectActions.setVisibility(View.GONE);

        }
        else if(id==R.id.actionAddActivity)
        {
            Intent actIntent = new Intent(DashboardActivity.this, AddActivityActivity.class);
            actIntent.putExtra("Type", "Project");
            actIntent.putExtra("ProjectID",selProject.ProjectID);
            actIntent.putExtra("ProjectName",selProject.ProjectName);
            actIntent.putExtra("Project", selProject);
            startActivityForResult(actIntent,APP_CONST.REQUEST_ACTIVITY_CODE);
        }
        else if(id==R.id.showMyExpenses)
        {
            String url =   url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Project/" + selProject.ProjectID+"/Employee/" +myProfile.UserID ;;
            Intent actIntent = new Intent(DashboardActivity.this, ReportActivity.class);
            actIntent.putExtra("Type", "Project");
            actIntent.putExtra("Project", selProject);
            actIntent.putExtra("url", url);
            startActivity(actIntent);
        }
        else if(id==R.id.actionRemoveActivity)
        {

            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
           // dialog.setTitle("Remove Activity");
            dialog.setMessage("Are you sure to remove Activity ?");
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    da.DeleteActivity(selActivity.ActivityID);
                    da.close();
                }
            });
            dialog.create();
            dialog.show();


            expenseTypeadapter.notifyDataSetChanged();
            viewActivityActions.setVisibility(View.GONE);
        }
        else if(id==R.id.actionAddPersonalExpense)
        {
            Intent expense = new Intent(DashboardActivity.this, AddExpenseActivity.class);
            expense.putExtra("Type", "Personal");
            expense.putExtra("ExpenseID",selExpenseID);
            expense.putExtra("ExpenseName",selExpenseID);
            startActivity(expense);
        }
        else if(id==R.id.actionShowPersonalExpense)
        {
            Intent statement = new Intent(DashboardActivity.this, ReportActivity.class);
            statement.putExtra("Type", "Personal");

            startActivity(statement);
        }
        else if(id==R.id.showTransaction)
        {
            Intent transaction = new Intent(DashboardActivity.this, TransactionReportActivity.class);
            transaction.putExtra("Type", "Project");
            transaction.putExtra("Project", selProject);
            startActivity(transaction);
        }
        else if(id==R.id.actionInvoice)
        {
            Intent invoice = new Intent(DashboardActivity.this, InvoiceActivity.class);
            invoice.putExtra("Type", "Project");
            invoice.putExtra("Project", selProject);
            startActivity(invoice);
        }
        else if(id==R.id.actionPurchase)
        {
            Intent purchase = new Intent(DashboardActivity.this, PurchaseActivity.class);
            purchase.putExtra("Type", "Project");
            purchase.putExtra("Project", selProject);
            startActivity(purchase);
        }
        else if(id==R.id.actionAllProjectExpenses)
        {
            String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Project/" + selProject.ProjectID ;
            Intent actIntent = new Intent(DashboardActivity.this, ReportActivity.class);
            actIntent.putExtra("Type", "AllExpense");
            actIntent.putExtra("Project", selProject);
            actIntent.putExtra("url", url);
            startActivity(actIntent);
        }
        else if(id == R.id.showApproval)
        {
            //String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Project/" + selProject.ProjectID ;
            Intent appIntent = new Intent(DashboardActivity.this, ApprovalActivity.class);
            startActivity(appIntent);
        }
        else if(id == R.id.showAccounts)
        {
            //String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Project/" + selProject.ProjectID ;
            Intent appIntent = new Intent(DashboardActivity.this, AccountsActivity.class);
            startActivity(appIntent);
        }

        else if(id == R.id.ShowTax)
        {
            Intent review = new Intent(DashboardActivity.this, TaxActivity.class);
            startActivity(review);
        }

        else if(id == R.id.ShowAllExpenses)
        {
            String url =   url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/Project/" + selProject.ProjectID+"/Employee/" +myProfile.UserID ;;
            Intent reportIntent = new Intent(DashboardActivity.this, ReportActivity.class);
            reportIntent.putExtra("Type", "AllWork");
            reportIntent.putExtra("Project", selProject);
            reportIntent.putExtra("url", url);
            startActivity(reportIntent);
        }

        else if(id==R.id.showLocalData)
        {
            Intent statement = new Intent(DashboardActivity.this, LocalExpenseActivity.class);

            startActivity(statement);

        }
    }



    private void   SetCommonBar()
    {
        adminBar.setVisibility(View.VISIBLE);
        View ShowAllExpenses = findViewById(R.id.ShowAllExpenses);
        View ShowTax = findViewById(R.id.ShowTax);
        View showTransaction = findViewById(R.id.showTransaction);
        View showApproval = findViewById(R.id.showApproval);
        View showLocalData = findViewById(R.id.showLocalData);

        showLocalData.setVisibility(View.VISIBLE);


        if(myProfile.Role.matches("Manager"))
        {
            showApproval.setVisibility(View.VISIBLE);
            showTransaction.setVisibility(View.GONE);
            ShowTax.setVisibility(View.GONE);
            ShowAllExpenses.setVisibility(View.GONE);

            chartTitle.setText("10 Latest Expenses");
            String url = APP_CONST.APP_SERVER_URL + "api/DailyExpense/Employee/" + myProfile.UserID;
           // GetChartData(url);

        }
        else if(myProfile.Role.matches("Admin"))
        {
            showTransaction.setVisibility(View.VISIBLE);
            ShowTax.setVisibility(View.VISIBLE);
            ShowAllExpenses.setVisibility(View.VISIBLE);
            showApproval.setVisibility(View.VISIBLE);

            chartTitle.setText("10 Latest Transaction");
            String url = APP_CONST.APP_SERVER_URL + "api/Transaction/Organization/" + myProfile.OrgID +"/Day";
           // GetChartData(url);
        }
        else if(myProfile.Role.matches("Employee"))
        {
            showTransaction.setVisibility(View.GONE);
            ShowAllExpenses.setVisibility(View.GONE);
            ShowTax.setVisibility(View.GONE);
            showApproval.setVisibility(View.GONE);

            viewApproval.setVisibility(View.GONE);
            chartTitle.setText("10 Latest Expenses");
            String url = APP_CONST.APP_SERVER_URL + "api/DailyExpense/Employee/" + myProfile.UserID;
           // GetChartData(url);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logOff) {

            LogOff();

            return true;
        } else if (id == R.id.Profile) {

            Intent statement = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(statement);
            return true;
        }
        else
        {
            return false;
        }

    }

    public void LogOff()
    {

        try {

            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            expenseData = da.GetAllExpenses();
            da.close();
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setTitle("Log out");
            if (expenseData.size() > 0) {

                dialog.setMessage("You have Local Data, pending to check in!");
            } else {

                dialog.setMessage("Are you sure ?");
            }

            dialog.setCancelable(false);
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });

            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (Session.LogOff(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "Successfully LogOut", Toast.LENGTH_LONG).show();
                            DashboardActivity.this.finish();
                            DataAccess da = new DataAccess(getApplicationContext());
                            da.open();
                            da.ClearAll();
                            da.close();
                            return;
                        }
                    } catch (Exception ex) {

                    }
                }
            });
            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

            int a=1;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            long time = SystemClock.currentThreadTimeMillis();

            if (prevTime == 0) {
                prevTime = SystemClock.currentThreadTimeMillis();
            }

            if (time - prevTime > 1000) {
                prevTime = time;
                ClickCount=0;
            }
            if (time - prevTime < 1000 && time > prevTime) {
                ClickCount++;
                if (ClickCount == 2) {

                    DashboardActivity.this.finish();
                } else {
                    prevTime = time;
                    String msg = "double click to close";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }




    public void CreateDailyExpChart() {




        ArrayList Expense = new ArrayList();

        Expense.add(new BarEntry(945f, 0));
        Expense.add(new BarEntry(1040f, 1));
        Expense.add(new BarEntry(1533f, 2));
        Expense.add(new BarEntry(1240f, 3));
        Expense.add(new BarEntry(2069f, 4));
        Expense.add(new BarEntry(1487f, 5));
        Expense.add(new BarEntry(1501f, 6));

        ArrayList Day = new ArrayList();

        Day.add("Monday");
        Day.add("Tuesday");
        Day.add("Wednesday");
        Day.add("Thursday");
        Day.add("Friday");
        Day.add("Saturday");
        Day.add("Sunday");


        BarDataSet bardataset = new BarDataSet(Expense, "Daily Expense");
        barChart.animateY(5000);
        BarData data = new BarData(Day, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.getLegend().setEnabled(false);
        barChart.setFitsSystemWindows(true);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DefaultXAxisValueFormatter());
        barChart.setData(data);

    }



    public void CreateExpenseChart() {
        try {


            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Local Data");
            labels.add("In Progress");
            labels.add("submitted");
            labels.add("Approved");



            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry((float) 1200, 0));
            entries.add(new Entry((float) 3000, 1));
            entries.add(new Entry((float) 3200, 2));
            entries.add(new Entry((float) 12000, 3));

            PieDataSet dataset = new PieDataSet(entries, "# of votes");
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setSliceSpace(3f);
            dataset.setSelectionShift(5f);
            pieChart.setTouchEnabled(true);
            pieChart.setHighlightPerTapEnabled(true);
            // set the color
            PieData data = new PieData(labels, dataset); // initialize Piedata
            pieChart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            pieChart.getLegend().setEnabled(false);
            pieChart.setHoleRadius(0f);
            data.setValueTextSize(13f);


            pieChart.setData(data);


            /*
                    ChartData values = new ChartData();
                    values.setSectorValue((float)5000);
                    values.setSectorLabel("Local");
                    semiChart.addSector(values);
                    values = new ChartData();
                    values.setSectorValue((float)3200);
                    values.setSectorLabel("In Progress");
                    semiChart.addSector(values);
                    values = new ChartData();
                    values.setSectorValue((float)5000);
                    values.setSectorLabel("Submitted");
                    semiChart.addSector(values);
                    values = new ChartData();
                    values.setSectorValue((float)12000);
                    values.setSectorLabel("Approved");
                    semiChart.addSector(values);*/

        } catch (Exception e) {
            int a=1;
        }
    }

}
