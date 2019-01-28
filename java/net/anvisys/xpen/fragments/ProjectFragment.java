package net.anvisys.xpen.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.APP_VARIABLES;
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Object.ProjectData;
import net.anvisys.xpen.ProjectActivity;
import net.anvisys.xpen.R;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter mAdapter;
    LinkedHashMap<Integer, ProjectData> projectList = new LinkedHashMap<Integer, ProjectData>();
    List<ProjectData> activityData;
    public ListenerInterface listener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_project, container, false);

        recyclerView = view.findViewById(R.id.recycleProjects );
        // SetProjectData();

        DataAccess da = new DataAccess(getContext());
        da.open();
        projectList =da.GetProjects();
        ProjectData prj = new ProjectData();
        prj.ProjectID =0;
        prj.ProjectName = "Add";
        prj.ClientName = "Project";
        projectList.put(prj.ProjectID,prj);
        da.close();
        try {
            mAdapter = new ProjectAdapter(projectList);
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

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        public TextView ProjectID, projectName, clientName, ProjectStatus;
        public ViewGroup viewData, viewProject;

        public ProjectViewHolder(View view) {
            super(view);

            ProjectID = view.findViewById(R.id.txtID);
            projectName = view.findViewById(R.id.txtInfo1);
            clientName = view.findViewById(R.id.txtInfo2);
            ProjectStatus = view.findViewById(R.id.txtInfo3);
            viewData = view.findViewById(R.id.viewData);
            viewProject= view.findViewById(R.id.viewProject);
        }
    }

    class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHolder> {

        private LinkedHashMap<Integer,ProjectData> projectList;
        private int selectedPos = RecyclerView.NO_POSITION;

        public ProjectAdapter(LinkedHashMap<Integer,ProjectData> projectList) {
            this.projectList = projectList;
        }


        @Override
        public ProjectViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_fragment, parent, false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        TextView idView =  itemView.findViewById(R.id.txtID);

                        int newID =Integer.parseInt (idView.getText().toString());

                        if(newID ==0)
                        {
                            if(APP_VARIABLES.NETWORK_STATUS) {
                                Intent prjIntent = new Intent(getActivity(), ProjectActivity.class);
                                prjIntent.putExtra("isResult", true);
                                startActivityForResult(prjIntent, APP_CONST.REQUEST_PROJECT_CODE);
                                return;
                            }
                            else
                            {
                                Snackbar.make(getView(),"Working Offline",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                            }
                        }

                        View prev = (View)parent.getTag();

                        if(prev != null)
                        {
                            synchronized(prev){
                                // notify() is being called here when the thread and
                                // synchronized block does not own the lock on the object.
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
                        Toast.makeText(getContext(),"Error Reading Project", Toast.LENGTH_SHORT);
                    }
                }
            });
            return new ProjectViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProjectViewHolder holder, int position) {
            try {

                ProjectData prj = (ProjectData) projectList.values().toArray()[position];
                if(prj.ProjectID>0) {
                    holder.viewData.setVisibility(View.VISIBLE);
                    holder.viewProject.setVisibility(View.GONE);
                    holder.ProjectID.setText(Integer.toString(prj.ProjectID));
                    holder.projectName.setText(prj.ProjectName);
                    holder.ProjectStatus.setText(prj.Status);
                    holder.clientName.setText(prj.ClientName);
                }
                else
                {
                    holder.ProjectID.setText(Integer.toString(prj.ProjectID));
                    holder.viewData.setVisibility(View.GONE);
                    holder.viewProject.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception ex)
            {
                int a=1;
            }

        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }

        private void SetSelected(View itemView, int prjId)
        {

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                itemView.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.background_selected));
               // ViewCompat.setElevation(itemView, 3);
            } else {
                itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_selected));
               // ViewCompat.setElevation(itemView, 3);
            }
           // ViewCompat.setElevation(itemView, 3);

            ProjectData project = projectList.get(prjId);
            if (listener != null) {
                listener.OnProjectSelect(project);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_CONST.REQUEST_PROJECT_CODE)
        {
            if(data!= null) {
                ProjectData prj = data.getParcelableExtra("Project");

                DataAccess da = new DataAccess(getContext());
                da.open();
                da.InsertProject(prj);
                da.close();
                projectList.put(prj.ProjectID, prj);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
