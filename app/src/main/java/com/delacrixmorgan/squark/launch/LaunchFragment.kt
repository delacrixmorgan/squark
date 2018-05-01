package com.delacrixmorgan.squark.launch

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.SquarkEngine
import kotlinx.android.synthetic.main.fragment_launch.*

/**
 * LaunchFragment
 * squark-android
 *
 * Created by Delacrix Morgan on 01/05/2018.
 * Copyright (c) 2018 licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.
 */

class LaunchFragment : Fragment(), RowListener {

    companion object {
        fun newInstance(): LaunchFragment {
            return LaunchFragment()
        }
    }

    private var rowList: ArrayList<TableRow> = ArrayList()
    private var expandedList: ArrayList<TableRow> = ArrayList()

    private var isExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            SquarkEngine.setupTable(
                    activity = it,
                    tableLayout = tableLayout,
                    rowList = rowList,
                    listener = this)
        }
    }

    private fun onRowExpand(selectedRow: Int) {
        rowList.forEachIndexed { index, tableRow ->
            if (index != selectedRow && index != (selectedRow + 1)) {
                tableRow.visibility = View.GONE
            } else {
                tableRow.setBackgroundColor(ContextCompat.getColor(context!!, R.color.amber))
            }
        }

        activity?.let {
            SquarkEngine.expandTable(
                    activity = it,
                    tableLayout = tableLayout,
                    expandQuantifier = selectedRow,
                    expandedList = expandedList,
                    listener = this)
        }
    }

    private fun onRowCollapse(selectedRow: Int) {
        expandedList.map {
            tableLayout.removeView(it)
        }

        rowList.map {
            it.visibility = View.VISIBLE
            it.setBackgroundColor(ContextCompat.getColor(context!!, R.color.black))
        }
    }

    override fun onSwipeLeft(position: Int) {
        if (!isExpanded) {
            SquarkEngine.updateTable(rowList)
        }
    }

    override fun onSwipeRight(position: Int) {
        if (!isExpanded) {
            SquarkEngine.updateTable(rowList)
        }
    }

    override fun onClick(position: Int) {
        if (isExpanded) {
            onRowCollapse(position)
        } else {
            onRowExpand(position)
        }
        isExpanded = !isExpanded
    }

    override fun onSwipingLeft(position: Int) = Unit

    override fun onSwipingRight(position: Int) = Unit
}
