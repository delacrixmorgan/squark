package com.delacrixmorgan.squark

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.content.ContextCompat
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.TableLayout
import android.widget.TableRow
import com.delacrixmorgan.squark.common.RowListener
import com.delacrixmorgan.squark.common.roundUp
import kotlinx.android.synthetic.main.view_row.view.*
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.absoluteValue


/**
 * SquarkEngine
 * squark-android
 *
 * Created by Delacrix Morgan on 01/05/2018.
 * Copyright (c) 2018 licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.
 */

object SquarkEngine {

    private var anchorPosition = 0F
    private var multiplier: Double = 1.0
    private var conversionRate: Double = 1.0
    private var bigResult: BigDecimal? = null
    private var bigQuantifier: BigDecimal? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("###,##0.00")

    fun updateConversionRate(baseRate: Double? = 1.0, quoteRate: Double? = 1.0) {
        this.conversionRate = quoteRate!! / baseRate!!
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupTable(
            activity: Activity,
            tableLayout: TableLayout,
            rowList: ArrayList<TableRow>,
            listener: RowListener
    ) {
        val thresholdTranslationWidth = activity.resources.displayMetrics.widthPixels / 6F
        val thresholdSwipeWidth = thresholdTranslationWidth / 1.5F
        val alphaRatio = 1F / thresholdTranslationWidth
        val gestureDetector = GestureDetector(activity, SingleTapConfirm())

        for (index in 0..9) {
            val tableRow = activity.layoutInflater.inflate(R.layout.view_row, tableLayout, false) as TableRow

            // PRESENT
            var calculateQuantifier = (this.multiplier * (index + 1))
            var calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.quantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.resultTextView.text = this.decimalFormat.format(this.bigResult).toString()


            // BEFORE
            calculateQuantifier = (this.multiplier / 10 * (index + 1))
            calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.beforeQuantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.beforeResultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            // NEXT

            calculateQuantifier = (this.multiplier * 10 * (index + 1))
            calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.nextQuantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.nextResultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            tableRow.setOnTouchListener { _, event ->
                if (gestureDetector.onTouchEvent(event)) {
                    listener.onClick(index)
                } else {
                    tableRow.onTouchEvent(event)
                    when (event.action) {
                        MotionEvent.ACTION_UP -> {
                            val currentPosition = rowList.firstOrNull()?.translationX ?: 0F

                            if (currentPosition.absoluteValue > thresholdSwipeWidth) {
                                if (currentPosition > 0) {
                                    if (this.multiplier < 1000000) {
                                        this.multiplier *= 10
                                    }
                                    listener.onSwipeLeft()
                                } else {
                                    if (this.multiplier > 0.1) {
                                        this.multiplier /= 10
                                    }
                                    listener.onSwipeRight()
                                }
                            }

                            rowList.forEach {
                                it.translationX = 0F
                                it.quantifierTextView.alpha = 1F
                                it.resultTextView.alpha = 1F

                                it.nextQuantifierTextView.alpha = 0F
                                it.nextResultTextView.alpha = 0F

                                it.beforeQuantifierTextView.alpha = 0F
                                it.beforeResultTextView.alpha = 0F
                            }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val movingPixels = event.rawX - this.anchorPosition
                            if (movingPixels.absoluteValue < thresholdTranslationWidth) {

                                val alpha = movingPixels.absoluteValue * alphaRatio
                                rowList.forEach {
                                    it.translationX = movingPixels

                                    it.quantifierTextView.alpha = (1F - alpha).roundUp()
                                    it.resultTextView.alpha = (1F - alpha).roundUp()

                                    if (movingPixels > 0) {
                                        it.nextQuantifierTextView.alpha = alpha.roundUp()
                                        it.nextResultTextView.alpha = alpha.roundUp()
                                    } else {
                                        it.beforeQuantifierTextView.alpha = alpha.roundUp()
                                        it.beforeResultTextView.alpha = alpha.roundUp()
                                    }
                                }
                            } else {
                                rowList.forEach {
                                    it.quantifierTextView.alpha = 0F
                                    it.resultTextView.alpha = 0F

                                    if (movingPixels > 0) {
                                        it.nextQuantifierTextView.alpha = 1F
                                        it.nextResultTextView.alpha = 1F
                                    } else {
                                        it.beforeQuantifierTextView.alpha = 1F
                                        it.beforeResultTextView.alpha = 1F
                                    }
                                }
                            }
                        }

                        MotionEvent.ACTION_DOWN -> this.anchorPosition = event.rawX
                    }
                }
                true
            }

            rowList.add(tableRow)
            tableLayout.addView(tableRow)
        }
    }

    fun updateTable(rowList: ArrayList<TableRow>) {
        rowList.forEachIndexed { index, tableRow ->
            // PRESENT
            var calculateQuantifier = (this.multiplier * (index + 1))
            var calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.quantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.resultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            // BEFORE
            calculateQuantifier = (this.multiplier / 10 * (index + 1))
            calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.beforeQuantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.beforeResultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            // NEXT
            calculateQuantifier = (this.multiplier * 10 * (index + 1))
            calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.nextQuantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.nextResultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            tableRow.quantifierTextView.startAnimation(AnimationUtils.loadAnimation(tableRow.context, R.anim.wobble))
            tableRow.resultTextView.startAnimation(AnimationUtils.loadAnimation(tableRow.context, R.anim.wobble))
        }
    }

    fun expandTable(
            activity: Activity,
            tableLayout: TableLayout,
            expandQuantifier: Int,
            expandedList: ArrayList<TableRow>,
            listener: RowListener
    ) {
        for (index in 1..9) {
            val tableRow = activity.layoutInflater.inflate(R.layout.view_row, tableLayout, false) as TableRow
            tableRow.setBackgroundColor(ContextCompat.getColor(activity, R.color.borderGrey))

            val calculateQuantifier = (expandQuantifier + 1) * this.multiplier + (this.multiplier / 10 * index)
            val calculateResult = calculateQuantifier * this.conversionRate

            this.bigQuantifier = BigDecimal(calculateQuantifier).setScale(2, BigDecimal.ROUND_HALF_UP)
            this.bigResult = BigDecimal(calculateResult).setScale(2, BigDecimal.ROUND_HALF_UP)

            tableRow.quantifierTextView.text = this.decimalFormat.format(this.bigQuantifier).toString()
            tableRow.resultTextView.text = this.decimalFormat.format(this.bigResult).toString()

            tableRow.setOnClickListener {
                listener.onClick(index)
            }

            expandedList.add(tableRow)
            tableLayout.addView(tableRow, (expandQuantifier + index))
        }
    }

    private class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent) = true
    }
}