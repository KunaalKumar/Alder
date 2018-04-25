package com.ashiana.zlifno.alder.Activity.add;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashiana.zlifno.alder.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AddImageNoteActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_image);
    }

}
