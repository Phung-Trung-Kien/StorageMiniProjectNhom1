package com.example.storageminiprojectcodebase2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shoppingapp.R;

public class HomeSectionFragment extends Fragment {

    private static final String ARG_BADGE = "badge";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_PRIMARY = "primary";
    private static final String ARG_SECONDARY = "secondary";
    private static final String ARG_SUPPORTING = "supporting";

    public static HomeSectionFragment newInstance(
            String badge,
            String title,
            String description,
            String primary,
            String secondary,
            String supporting
    ) {
        Bundle args = new Bundle();
        args.putString(ARG_BADGE, badge);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_PRIMARY, primary);
        args.putString(ARG_SECONDARY, secondary);
        args.putString(ARG_SUPPORTING, supporting);

        HomeSectionFragment fragment = new HomeSectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        setText(view, R.id.tv_section_badge, args.getString(ARG_BADGE));
        setText(view, R.id.tv_section_title, args.getString(ARG_TITLE));
        setText(view, R.id.tv_section_description, args.getString(ARG_DESCRIPTION));
        setText(view, R.id.tv_primary_value, args.getString(ARG_PRIMARY));
        setText(view, R.id.tv_secondary_value, args.getString(ARG_SECONDARY));
        setText(view, R.id.tv_supporting_copy, args.getString(ARG_SUPPORTING));
    }

    private void setText(View root, int viewId, @Nullable String text) {
        TextView textView = root.findViewById(viewId);
        textView.setText(text);
    }
}