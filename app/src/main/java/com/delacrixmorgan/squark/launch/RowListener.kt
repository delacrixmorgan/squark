package com.delacrixmorgan.squark.launch

/**
 * Created by Delacrix Morgan on 28/01/2018.
 **/

interface RowListener {
    fun onRowExpand(position: Int)
    fun onRowCollapse(position: Int)
}