package net.anvisys.xpen.fragments;


import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import net.anvisys.xpen.ActivityActivity;
import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.R;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends Fragment{

    private RecyclerView recyclerView;
    private ActivityAdapter mAdapter;
    LinkedHashMap<Integer, ActivityData> activityList = new LinkedHashMap<Integer, ActivityData>();
    List<ActivityData> activityData;
    public ListenerInterface listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        recyclerView = view.findViewById(R.id.recycleProject);
        DataAccess da = new DataAccess(getContext());
        da.open();
        activityList =da.GetServerActivities();
        da.close();


        ActivityData act = new ActivityData();
        act.ActivityID =0;
        act.ActivityName = "Add";
        act.ProjectName = "Activity";
        activityList.put(act.ActivityID,act);

        try {
            mAdapter = new ActivityAdapter(activityList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);
        }
        catch (Exception ex)
        {
            int a=1;
            a++;
        }

        return view;
    }



    class ActivityViewHolder extends RecyclerView.ViewHolder {
        public TextView ActivityID,ActivityName, ProjectName,ActivityStatus;
        public ViewGroup viewData, viewActivity;
        public ActivityViewHolder(View view) {
            super(view);

            ActivityID = view.findViewById(R.id.txtID);
            ActivityName =  view.findViewById(R.id.txtInfo1);
            ProjectName =  view.findViewById(R.id.txtInfo2);
            ActivityStatus =  view.findViewById(R.id.txtInfo3);

            viewData = view.findViewById(R.id.viewData);
            viewActivity= view.findViewById(R.id.viewActivity);
        }
    }

    class ActivityAdapter extends RecyclerView.Adapter<ActivityViewHolder> {

        private LinkedHashMap<Integer,ActivityData> activityList;

        public ActivityAdapter(LinkedHashMap<Integer,ActivityData> actList) {
            this.activityList = actList;
        }

        @Override
        public ActivityViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_fragment, parent, false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        TextView idView = itemView.findViewById(R.id.txtID);

                        int newID = Integer.parseInt(idView.getText().toString());
                        if(newID ==0)



                        {
                            Intent prjIntent = new Intent(getActivity(), ActivityActivity.class);
                            prjIntent.putExtra("isResult", true);
                            startActivityForResult(prjIntent, APP_CONST.REQUEST_ACTIVITY_CODE);
                            return;
                        }

                        View prev = (View)parent.getTag();
                        if(prev != null)
                        {
                            synchronized(prev){

                                prev.setBackground(null);
                            }

                            TextView prevIdView = prev.findViewById(R.id.txtID);
                            int prevID =Integer.parseInt (prevIdView.getText().toString());

                            if(newID != prevID) {
                                parent.setTag(v);
                                SetSelected(itemView,newID);
                            }
                            else{
                                parent.setTag(null);
                                if (listener != null) {

                                    listener.OnDeSelect();
                                }
                            }
                        }
                        else
                        {
                            parent.setTag(v);
                            SetSelected(itemView,newID);
                        }
                    }
                    catch (Exception ex)
                    {
                        Snackbar.make(getView(),"Error Reading Project",Snackbar.LENGTH_LONG);
                    }
                }
            });

            return new ActivityViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ActivityViewHolder holder, int position) {
            try {
                ActivityData act = (ActivityData) activityList.values().toArray()[position];
                if(act.ActivityID>0) {
                    holder.viewData.setVisibility(View.VISIBLE);
                    holder.viewActivity.setVisibility(View.GONE);
                    holder.ActivityID.setText(Integer.toString(act.ActivityID));
                    holder.ActivityName.setText(act.ActivityName);
                    holder.ProjectName.setText(act.ProjectName);
                    holder.ActivityStatus.setText(act.ActivityStatus);
                }
                else
                {
                    holder.ActivityID.setText(Integer.toString(act.ActivityID));
                    holder.viewData.setVisibility(View.GONE);
                    holder.viewActivity.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception ex)
            {
                int a=1;
            }
        }

        @Override
        public int getItemCount() {
            return activityList.size();
        }

        private void SetSelected(View itemView, int prjId)
        {

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                itemView.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.background_selected));
            } else {
                itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_selected));
            }
            ActivityData activity =  activityList.get(prjId);
            if (listener != null) {
                listener.OnActivitySelect(activity);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == APP_CONST.REQUEST_ACTIVITY_CODE) {
                if (data != null) {
                    ActivityData act = data.getParcelableExtra("Activity");

                    DataAccess da = new DataAccess(getContext());
                    da.open();
                    da.InsertServerActivity(act);
                    da.close();
                    activityList.put(act.ActivityID, act);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception ex)
        {

        }
    }

}
