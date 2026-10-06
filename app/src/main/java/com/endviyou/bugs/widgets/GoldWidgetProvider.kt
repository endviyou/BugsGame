package com.endviyou.bugs.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.endviyou.bugs.R
import com.endviyou.bugs.network.GoldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class GoldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        CoroutineScope(Dispatchers.IO).launch {
            val price = GoldRepository.getGoldPrice()
            val priceText = if (price > 0) String.format(Locale.getDefault(), "%.0f ₽", price) else "—"

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_gold)
                views.setTextViewText(R.id.tvWidgetPrice, priceText)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}