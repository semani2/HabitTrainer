package com.sai.habittrainer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_habit.view.*

/**
 * Created by sai on 12/24/17.
 */
class HabitsAdapter(val habits: List<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>(){

    override fun getItemCount() = habits.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.view.title_text_view.text = habit.title
        holder.view.description_text_view.text = habit.description
        holder.view.icon_image_view.setImageBitmap(habit.image)
    }

    class HabitViewHolder(val view: View): RecyclerView.ViewHolder(view)
}