package com.ashiana.zlifno.alder.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toolbar

import com.ashiana.zlifno.alder.R

import net.danlew.android.joda.JodaTimeAndroid

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
        //        Toolbar toolbar = findViewById(R.id.my_toolbar);
        //        NavController navController = Navigation.findNavController(findViewById(R.id.my_nav_host_fragment));
        //        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.my_nav_host_fragment).navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //TODO: Close fab button if open when coming back from adding note activity
    }
}
