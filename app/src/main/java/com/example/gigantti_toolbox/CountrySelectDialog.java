package com.example.gigantti_toolbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.iikkag.gigantti_toolbox.R;

public class CountrySelectDialog extends AppCompatDialogFragment {

    private CountrySelectDialogListener listener;
    private int value;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.country_select_layout, null);

        final RadioGroup radioGroup = view.findViewById(R.id.countryRadioGroup);


        builder.setTitle("Pick a Country")
                .setItems(R.array.Countries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.countryFinlandButton:
                                value = 1;
                                break;
                            case R.id.countrySwedenButton:
                                value = 2;
                                break;
                            case R.id.countryNorwayButton:
                                value = 3;
                                break;
                            default:
                                value = 1;
                                break;
                        }*/

                        value = which;

                        listener.passCountryData(0, value);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CountrySelectDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement CountrySelectDialogListener");
        }
    }

    public interface CountrySelectDialogListener{
        void passCountryData(Integer id, Integer value);
    }
}
