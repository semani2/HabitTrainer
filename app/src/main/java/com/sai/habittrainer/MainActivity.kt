package com.sai.habittrainer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        habits_recycler_view.setHasFixedSize(true)

        habits_recycler_view.layoutManager = LinearLayoutManager(this)
        habits_recycler_view.adapter = HabitsAdapter(getSampleHabits())
    }
}
