package com.endviyou.bugs.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.endviyou.bugs.MainActivity
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

                // ↓↓↓ ДОБАВЛЯЕМ ОБРАБОТКУ КЛИКА ↓↓↓
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Устанавливаем клик на весь виджет (корневой LinearLayout)
                views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)
                // ↑↑↑ КОНЕЦ ↑↑↑

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}