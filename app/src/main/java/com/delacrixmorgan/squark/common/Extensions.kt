package com.delacrixmorgan.squark.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.delacrixmorgan.squark.R
import org.json.JSONObject
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import java.io.BufferedReader
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * Common
 */

fun View.performHapticContextClick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    } else {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}

fun Float.roundUp() = (this * 10F).roundToInt() / 10F

fun Int.compatColor(context: Context?): Int {
    return if (context == null) {
        0
    } else {
        ContextCompat.getColor(context, this)
    }
}

fun Fragment.launchWebsite(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)

    intent.data = Uri.parse(url)
    startActivity(intent)
}

/**
 * Date
 */

fun Date.toStringFormat(): String {
    val calendar = Calendar.getInstance().apply { time = this@toStringFormat }
    val zonedDateTime = DateTimeUtils.toZonedDateTime(calendar)

    return DateTimeFormatter.ISO_INSTANT.format(zonedDateTime)
}

fun String.toDateFormat(): Date {
    val dateInstant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(this))
    return DateTimeUtils.toDate(dateInstant)
}

/**
 * Context
 */
fun Fragment.launchPlayStore(packageName: String) {
    val url = "https://play.google.com/store/apps/details?id=$packageName"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Context.shareAppIntent(message: String) {
    val intent = Intent(Intent.ACTION_SEND)

    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, message)

    startActivity(
        Intent.createChooser(
            intent,
            getString(R.string.fragment_support_settings_list_share)
        )
    )
}

fun Context.getJsonMap(rawFile: Int, key: String): Map<String, String> {
    val inputStream = resources.openRawResource(rawFile)
    val responseObject = inputStream.bufferedReader().use(BufferedReader::readText)

    val map = HashMap<String, String>()
    val jsonObject = JSONObject(responseObject).optJSONObject(key)

    jsonObject?.keys()?.forEach {
        map[it] = "${jsonObject[it]}"
    }

    return map
}

/**
 * CalculationQuantifier, CalculationResult
 */
fun calculateRowQuantifier(multiplier: Double, position: Int): String {
    val quantifier = (multiplier * (position + 1))
    val bigDecimal = BigDecimal(quantifier).setScale(2, BigDecimal.ROUND_HALF_UP)

    return getNumberFormatType(bigDecimal)
}

fun calculateRowResult(multiplier: Double, position: Int, conversionRate: Double): String {
    val quantifier = (multiplier * (position + 1))
    val result = quantifier * conversionRate
    val bigDecimal = BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP)

    return getNumberFormatType(bigDecimal)
}

fun calculateExpandQuantifier(expandQuantifier: Int, multiplier: Double, position: Int): String {
    val quantifier = (expandQuantifier + 1) * multiplier + (multiplier / 10 * position)
    val bigDecimal = BigDecimal(quantifier).setScale(2, BigDecimal.ROUND_HALF_UP)

    return getNumberFormatType(bigDecimal)
}

fun calculateExpandResult(
    expandQuantifier: Int,
    multiplier: Double,
    position: Int,
    conversionRate: Double
): String {
    val quantifier = (expandQuantifier + 1) * multiplier + (multiplier / 10 * position)
    val result = quantifier * conversionRate
    val bigDecimal = BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP)

    return getNumberFormatType(bigDecimal)
}

fun getNumberFormatType(bigDecimal: BigDecimal): String {
    val roundedBigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).precision()
    return when {
        roundedBigDecimal >= 16 -> NumberFormatTypes.QUADRILLIONTH.decimal.format(
            bigDecimal.movePointLeft(
                15
            )
        )
        roundedBigDecimal >= 13 -> NumberFormatTypes.TRILLIONTH.decimal.format(
            bigDecimal.movePointLeft(
                12
            )
        )
        roundedBigDecimal >= 10 -> NumberFormatTypes.BILLIONTH.decimal.format(
            bigDecimal.movePointLeft(
                9
            )
        )
        roundedBigDecimal >= 7 -> NumberFormatTypes.MILLIONTH.decimal.format(
            bigDecimal.movePointLeft(
                6
            )
        )
        else -> NumberFormatTypes.HUNDREDTH.decimal.format(bigDecimal)
    }
}