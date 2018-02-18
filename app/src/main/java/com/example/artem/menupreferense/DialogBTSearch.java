package com.example.artem.menupreferense;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


@SuppressLint("ValidFragment")
public class DialogBTSearch extends DialogFragment {

    static ListView listViewSearcDevice;
    TextView textView;



    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
        void onArticleSelected(int position);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View deviceDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.dialog, null);
        builder.setView(deviceDialogView); // Добавление GUI в диалоговое окно
        listViewSearcDevice = (ListView) deviceDialogView.findViewById(R.id.listView_device);
        textView = (TextView) deviceDialogView.findViewById(R.id.textViewSearch);
        builder.setNegativeButton("Отмена", null);
        listViewSearcDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallback.onArticleSelected(i);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnHeadlineSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }



    }


}
