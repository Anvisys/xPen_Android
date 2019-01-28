package net.anvisys.xpen.Login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.anvisys.xpen.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {

    RadioGroup planRadioGroup;
    TextView textPlan,option;
    Button btnPlanSelected;
    String selPlan;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_plan, container, false);

        planRadioGroup = view.findViewById(R.id.planRadioGroup);
        textPlan = view.findViewById(R.id.textPlan);
        option=view.findViewById(R.id.instruction);
        btnPlanSelected = view.findViewById(R.id.btnPlanSelected);

        btnPlanSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("plan",selPlan);

                RegisterFragment fragment2 = new RegisterFragment();
                fragment2.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameRegister, fragment2)
                        .commit();
            }
        });

        planRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.planIndividual)
                {
                    textPlan.setText("You have selected Individual Plan\n\n" +"--> It's life time free\n" + "--> Provide Server Storage\n" + "--> One user per ragistration");
                    selPlan = "Individual";
                    btnPlanSelected.setVisibility(View.VISIBLE);
                }
                else if(checkedId ==R.id.planWeb)
                {
                    textPlan.setText("You have selected Web Plan.\n\n"+ "-->Please use a unique Organization Name.\n"+ "-->Provide Server Storage\n"+"-->One user per ragistration");
                    selPlan = "WebCorporate";
                    btnPlanSelected.setVisibility(View.VISIBLE);
                }

                else if(checkedId ==R.id.planEnterprise)
                {
                    textPlan.setText("You have selected Enterprise Plan");
                    selPlan = "Enterprise";
                    btnPlanSelected.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

}
