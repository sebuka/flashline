package com.example.myapplication;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;

public class SettingsDialogFragment extends DialogFragment {

    private String levelSeed;

    public SettingsDialogFragment(String levelSeed) {
        this.levelSeed = levelSeed;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings, container, false);

        Button btnMainMenu = view.findViewById(R.id.btn_main_menu);
        Button btnContinue = view.findViewById(R.id.btn_continue);
        TextView seedText = view.findViewById(R.id.seed_text);

        seedText.setText(levelSeed);

        btnMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        btnContinue.setOnClickListener(v -> dismiss());

        seedText.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("level_seed", seedText.getText().toString());
            clipboard.setPrimaryClip(clip);
        });

        return view;
    }
}
