package ru.sebuka.flashline.dialogs;

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

import ru.sebuka.flashline.activities.MatchActivity;
import ru.sebuka.flashline.activities.MatchGameActivity;
import ru.sebuka.flashline.utils.GameLevel;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.activities.LevelActivity;

public class SettingsDialogFragment extends DialogFragment {

    private int levelSeed;

    public SettingsDialogFragment(int levelSeed) {
        this.levelSeed = levelSeed;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings, container, false);

        Button btnFinish = view.findViewById(R.id.btn_main_menu);
        Button btnContinue = view.findViewById(R.id.btn_continue);
        TextView seedText = view.findViewById(R.id.seed_text);
        TextView sub1 = view.findViewById(R.id.click_to_copy_text);
        TextView sub2= view.findViewById(R.id.sub);

        seedText.setText(String.valueOf(levelSeed));
        if(levelSeed == -1){
            seedText.setVisibility(View.GONE);
            sub1.setVisibility(View.GONE);
            sub2.setVisibility(View.GONE);
        }
        btnFinish.setOnClickListener(v -> {
            if (getActivity() instanceof LevelActivity) {
                LevelActivity levelActivity = (LevelActivity) getActivity();
                GameLevel gameLevel = levelActivity.getGameLevel();
                gameLevel.finishGame();
            }
            if (getActivity() instanceof MatchGameActivity) {
                MatchGameActivity matchGameActivity = (MatchGameActivity) getActivity();
                GameLevel gameLevel = matchGameActivity.getGameLevel();
                gameLevel.finishGame();
            }
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
