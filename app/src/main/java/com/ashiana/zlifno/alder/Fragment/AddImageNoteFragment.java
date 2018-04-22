package com.ashiana.zlifno.alder.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashiana.zlifno.alder.R;

public class AddImageNoteFragment extends Fragment {
    View rootView;
    ImageView imageView;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_image, container, false);
        imageView = rootView.findViewById(R.id.imageView);
        return rootView;
    }

}
