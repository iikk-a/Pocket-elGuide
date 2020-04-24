package com.example.gigantti_toolbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iikkag.gigantti_toolbox.R;


public class SettingsFragment extends Fragment implements CountrySelectDialog.CountrySelectDialogListener {
    private static final String SHARED_PREF = "sharedPref";


    private OnFragmentInteractionListener mListener;

    private TextView countrySelectTextView;
    private ImageButton countrySelectImgButton;
    private Button countrySelectTransparentButton;

    private TextView imageFetchingTextView;
    private ImageButton imageFetchingImgButton;
    private Button imageFetchingTransparentButton;

    private TextView developerTextView;
    private ImageButton developerImgButton;
    private Button developerTransparentButton;


    private static ConstraintLayout mLayout;
    private static FloatingActionButton fButton;
    private static Context mContext;

    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(Context context, ConstraintLayout layout, FloatingActionButton button) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        mContext = context;
        mLayout = layout;
        fButton = button;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        countrySelectImgButton = view.findViewById(R.id.countrySelectButton);
        countrySelectTextView = view.findViewById(R.id.countrySelectText);
        countrySelectTransparentButton = view.findViewById(R.id.countrySelectTransparentButton);

        imageFetchingTextView = view.findViewById(R.id.imageFetchingTextView);
        imageFetchingImgButton = view.findViewById(R.id.imageFetchingImageButton);
        imageFetchingTransparentButton = view.findViewById(R.id.imageFetchingTransparentButton);

        developerTextView = view.findViewById(R.id.developerText);
        developerImgButton = view.findViewById(R.id.developerImageButton);
        developerTransparentButton = view.findViewById(R.id.developerTransparentButton);


        countrySelectImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountry();
            }
        });
        countrySelectTransparentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountry();
            }
        });

        imageFetchingTransparentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleImageFetching();
            }
        });

        developerImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerActivity();
            }
        });
        developerTransparentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerActivity();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Integer id, Integer value) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id, value);
        }
    }

    @Override
    public void passCountryData(Integer id, Integer value) {
        mListener.onFragmentInteraction(id, value);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            fButton.setVisibility(View.GONE);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mLayout.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
        fButton.setVisibility(View.VISIBLE);
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Integer id, Integer value);
    }

    public void selectCountry() {
        CountrySelectDialog dialog = new CountrySelectDialog();
        dialog.show(getFragmentManager(), "Country Select Dialog");
    }

    public void developerActivity() {
        /*Toast.makeText(getActivity(), "Developer Mode Activated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), DeveloperActivity.class);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }

        startActivity(intent);*/
    }

    private void toggleImageFetching() {
        mListener.onFragmentInteraction(1, 0);
    }



}
