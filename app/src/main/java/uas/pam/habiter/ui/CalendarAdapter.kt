package uas.pam.habiter.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uas.pam.habiter.R
import uas.pam.habiter.screen.HomeActivity
import kotlin.collections.ArrayList
import android.graphics.Typeface



class CalendarAdapter(private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var list = ArrayList<CalendarDateModel>()
    var adapterPosition = -1


    interface onItemClickListener{
        fun onItemClick(text: String, date: String, day: String)
    }

    private var mListener: onItemClickListener? = null

    fun setOnItemClickListener(listener: HomeActivity){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.date_layout,parent,false)
        return CalendarViewHolder(view)
    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val itemList = list[position]
        holder.calendarDay.text = itemList.calendarDay
        holder.calendarDate.text = itemList.calendarDate

        holder.itemView.setOnClickListener {
            itemList.isSelected = true // Mark the clicked item as selected
            listener.invoke(itemList, position)

        }

        // Highlight the current day and selected day with different colors
        if (itemList.isCurrentDay) {
            holder.calendarDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorAccent))
            holder.calendarDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorAccent))
            holder.linear.background = holder.itemView.context.getDrawable(R.drawable.bg_rectangle_fill)
        } else if (itemList.isSelected) {
            holder.calendarDay.setTypeface(null, Typeface.BOLD)
            holder.calendarDate.setTypeface(null, Typeface.BOLD)

            holder.calendarDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorSelectedText))
            holder.calendarDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorSelectedText))
            holder.linear.background = holder.itemView.context.getDrawable(R.drawable.bg_rectangle_fill)
        } else {
            holder.calendarDay.setTypeface(null, Typeface.NORMAL)
            holder.calendarDate.setTypeface(null, Typeface.NORMAL)

            holder.calendarDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorWhite))
            holder.calendarDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorWhite))
            holder.linear.background = holder.itemView.context.getDrawable(R.drawable.bg_rectangle_outline)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day)
        val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date)
        val linear = itemView.findViewById<LinearLayout>(R.id.linear_calendar)
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}