package com.ashiana.zlifno.alder.Activity;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.ashiana.zlifno.alder.Fragment.ListFragment;
import com.ashiana.zlifno.alder.R;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash {
    @Override
    public void initSplash(ConfigSplash configSplash) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        configSplash.setBackgroundColor(R.color.colorPrimary);
        configSplash.setAnimCircularRevealDuration(1000);
        configSplash.setRevealFlagX(Flags.WITH_LOGO);
        configSplash.setRevealFlagY(Flags.WITH_LOGO);
        configSplash.setLogoSplash(R.drawable.alder_icon);
        configSplash.setAnimLogoSplashDuration(500);
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn);
        configSplash.setTitleSplash(getString(R.string.app_name));
        configSplash.setTitleTextColor(R.color.text_shadow_white);
        configSplash.setTitleTextSize(30f);
        configSplash.setTitleFont("fonts/Sansation_Bold.ttf");
        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(SplashActivity.this, ListActivity.class));
    }
}
