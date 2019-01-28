package net.anvisys.xpen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Common.SyncData;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.Expense;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;

public class LocalExpenseActivity extends AppCompatActivity implements SyncData.SyncListener {

    ListView listViewExpense;
    List<Expense> expenseData;
    LinkedHashMap<Integer, Expense> selectedExpenseData = new LinkedHashMap<>();
    LocalExpenseAdapter expenseAdapter;
    boolean selectionOn = false;
    ImageView imgDelete;
    NumberFormat currFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_expense);
        listViewExpense = findViewById(R.id.listViewExpense);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Local Data");
        actionBar.show();

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        expenseData = da.GetAllExpenses();

        da.close();


        expenseAdapter = new LocalExpenseAdapter(getApplicationContext(), 0, expenseData);
        listViewExpense.setAdapter(expenseAdapter);
        listViewExpense.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectionOn) {
                    selectionOn = true;
                } else {
                    selectionOn = false;
                }
                expenseAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }

    @Override
    public void OnExpenseCheckIn() {
        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        //da.DeleteAllExpense();
        expenseData.clear();
        expenseData = da.GetAllExpenses();
        da.close();

    }
    public void DeleteExpenses(View v)
    {
        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        for (Expense ex:selectedExpenseData.values()
                ) {
            da.DeleteExpense(ex.TranId);
            selectedExpenseData.remove(selectedExpenseData);
        }
        da.close();
        expenseAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(selectedExpenseData.size()>0) {
            inflater.inflate(R.menu.menu_local_expense, menu);
        }
        else
        {
            inflater.inflate(R.menu.menu_expense_item, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.menuDelete)
        {
            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            for (Expense ex:selectedExpenseData.values()
                    ) {
                da.DeleteExpense(ex.TranId);
                selectedExpenseData.remove(selectedExpenseData);
            }

            da.close();
            expenseAdapter.notifyDataSetChanged();
        }
        else if(id == R.id.menuCheckIn)
        {
            SyncData sync = new SyncData(getApplicationContext());
            sync.UpdateExpense();
            expenseAdapter.notifyDataSetChanged();
        }

        else if(id == R.id.menuDeselectAll)
        {
            selectedExpenseData.clear();
            expenseAdapter.notifyDataSetChanged();
        }
        else if(id == R.id.menuClearAll)
        {
            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            da.DeleteAllExpense();
            expenseData.clear();
            expenseData = da.GetAllExpenses();
            da.close();
            expenseAdapter.notifyDataSetChanged();
        }
        return true;
    }
    private class TableViewHolder
    {
        TextView txtDate,expenseAmount,txtRemarks,expenseName, txtActivity;
    }

    private class LocalExpenseAdapter extends ArrayAdapter<Expense>
    {
        LayoutInflater inflat;
        TableViewHolder holder;
        public LocalExpenseAdapter(@NonNull Context context, int resource, @NonNull List<Expense> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return expenseData.size();
        }

        @Nullable
        @Override
        public Expense getItem(int position) {
            return expenseData.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_expense, null);
                    holder = new TableViewHolder();
                    holder.expenseName = convertView.findViewById(R.id.expenseName);
                    holder.txtDate = convertView.findViewById(R.id.txtDate);
                    holder.expenseAmount = convertView.findViewById(R.id.expenseAmount);
                    holder.txtRemarks = convertView.findViewById(R.id.txtRemarks);
                    holder.txtActivity = convertView.findViewById(R.id.txtActivity);
                    convertView.setTag(holder);
                }

                holder=(TableViewHolder)convertView.getTag();
                final Expense act = getItem(position);

                holder.expenseName.setText(act.expense_item);
                holder.txtDate.setText(Utility.ChangeToDateTimeDisplayFormat(act.ExpenseDate));
                if(act.ExpenseType.matches("Project"))
                {
                    holder.expenseAmount.setText(currFormat.format(act.expense));
                    holder.txtActivity.setText(act.ActivityName);
                    holder.txtRemarks.setText(act.Remarks);
                }
                else if (act.ExpenseType.matches("Activity")) {
                    holder.expenseAmount.setText(currFormat.format(act.expense));
                    holder.txtActivity.setText(act.ActivityName);
                    holder.txtRemarks.setText(act.Remarks);
                }
                else if(act.ExpenseType.matches("Personal"))
                {
                    holder.txtRemarks.setText("Personal");
                }



            }
            catch (Exception ex)
            {
                int a=1;
            }
            return convertView;
        }
    }

}