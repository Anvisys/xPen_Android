package net.anvisys.xpen.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.anvisys.xpen.R;

import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseTypeFragment extends Fragment {
    RecyclerView recycleExpenseItem;
    ExpenseItemAdapter eiAdapter;
    LinkedHashMap<Integer,EI_Item> eiList;
    public ExpenseTypeListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_expense_type, container, false);

        recycleExpenseItem = view.findViewById(R.id.recycleExpenseItem);
        setEIList();
        eiAdapter = new ExpenseItemAdapter(eiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recycleExpenseItem.setLayoutManager(mLayoutManager);
        recycleExpenseItem.setItemAnimator(new DefaultItemAnimator());
        recycleExpenseItem.setAdapter(eiAdapter);
        return view;
    }

    public class EIViewHolder extends RecyclerView.ViewHolder {
        public TextView eiTitle;
        public ImageView eiImage;
        public EIViewHolder(View view) {
            super(view);
            eiImage = view.findViewById(R.id.eiImage);
            eiTitle = view.findViewById(R.id.eiTitle);
        }
    }


    public class ExpenseItemAdapter extends RecyclerView.Adapter<EIViewHolder> {

        private LinkedHashMap<Integer,EI_Item> eiList;

        public ExpenseItemAdapter(LinkedHashMap<Integer,EI_Item> EIList) {
            this.eiList = EIList;
        }


        @Override
        public EIViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_expense_type, parent, false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        View prev = (View)parent.getTag();
                        if(prev != null)
                        {
                            synchronized(prev){
                                // notify() is being called here when the thread and
                                // synchronized block does not own the lock on the object.
                                prev.setBackground(null);
                            }
                        }

                        parent.setTag(v);
                        final int sdk = android.os.Build.VERSION.SDK_INT;
                        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            itemView.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.background_border) );
                        } else {
                            itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_border));
                        }
                        ConstraintLayout view = (ConstraintLayout) v;
                        TextView ei = (TextView) view.getChildAt(1);

                        if(listener != null)
                        {
                            listener.OnExpenseTypeSelect(1, ei.getText().toString());
                        }
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getContext(),"Error Reading Project", Toast.LENGTH_SHORT);
                    }
                }
            });

            return new EIViewHolder(itemView);
        }




        @Override
        public void onBindViewHolder(EIViewHolder holder, int position) {
            try {
                EI_Item prj = (EI_Item) eiList.values().toArray()[position];
                holder.eiTitle.setText(prj.title);
                holder.eiImage.setImageResource(prj.ImageID);

            }
            catch (Exception ex)
            {
                int a=1;
            }

        }

        @Override
        public int getItemCount() {
            return eiList.size();
        }
    }

    private class EI_Item
    {
        String title, ImageSource;
        int ImageID;
    }

    private void setEIList()
    {
        eiList = new LinkedHashMap<>(4);

        EI_Item temp = new EI_Item();
        temp.title = "Fuel";
        temp.ImageID = R.drawable.fuel;
        eiList.put(1,temp);

        temp = new EI_Item();
        temp.title = "Food";
        temp.ImageID = R.drawable.food;
        eiList.put(2,temp);

        temp = new EI_Item();
        temp.title = "Medicine";
        temp.ImageID = R.drawable.medicine;
        eiList.put(3,temp);

        temp = new EI_Item();
        temp.title = "Local Travel";
        temp.ImageID = R.drawable.local;
        eiList.put(4,temp);

        temp = new EI_Item();
        temp.title = "Travel";
        temp.ImageID = R.drawable.travel;
        eiList.put(5,temp);


        temp = new EI_Item();
        temp.title = "Hotel";
        temp.ImageID = R.drawable.hotel;
        eiList.put(6,temp);

        temp = new EI_Item();
        temp.title = "Tea";
        temp.ImageID = R.drawable.tea;
        eiList.put(7,temp);

        temp = new EI_Item();
        temp.title = "Repair";
        temp.ImageID = R.drawable.repair;
        eiList.put(8,temp);

    }




}
