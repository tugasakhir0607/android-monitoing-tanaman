package com.example.monitoringtanaman.menuutama.informasi;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.example.monitoringtanaman.R;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class InformasiFragment extends Fragment {
    TextView tv_informasi;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_informasi, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_informasi = view.findViewById(R.id.tv_informasi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tv_informasi.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}