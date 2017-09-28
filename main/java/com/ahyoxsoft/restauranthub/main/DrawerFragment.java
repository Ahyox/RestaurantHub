package com.ahyoxsoft.restauranthub.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahyoxsoft.restauranthub.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerFragment extends Fragment {
    private Dashboard dashboard;


    public DrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_drawer, container, false);

        //Discover
        row.findViewById(R.id.discover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(1);
            }
        });

        //Food order history
        row.findViewById(R.id.order_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(2);
            }
        });

        row.findViewById(R.id.fund_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(3);
            }
        });

        row.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(4);
            }
        });

        row.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(5);
            }
        });

        row.findViewById(R.id.rate_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(6);
            }
        });

        row.findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard.closeDrawer(7);
            }
        });

        return row;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dashboard = (Dashboard) context;
    }


}
