package xyz.hisname.fireflyiii.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.work.*
import xyz.hisname.fireflyiii.repository.workers.PiggyBankWorker
import xyz.hisname.fireflyiii.ui.notifications.NotificationUtils

class PiggyBankReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
        val baseUrl: String by lazy { sharedPref.getString("fireflyUrl", "") }
        val accessToken: String by lazy { sharedPref.getString("access_token","") }
        if(baseUrl.isBlank() || accessToken.isBlank()){
            val notif = NotificationUtils(context)
            notif.showNotSignedIn()
        } else {
            if(intent.action == "firefly.hisname.ADD_PIGGY_BANK"){
                val piggyData = Data.Builder()
                        .putString("name", intent.getStringExtra("name"))
                        .putString("accountId", intent.getStringExtra("accountId"))
                        .putString("targetAmount", intent.getStringExtra("targetAmount"))
                        .putString("currentAmount", intent.getStringExtra("currentAmount"))
                        .putString("startDate", intent.getStringExtra("startDate"))
                        .putString("endDate", intent.getStringExtra("endDate"))
                        .putString("notes", intent.getStringExtra("notes"))
                        .build()
                val piggybankWork = OneTimeWorkRequest.Builder(PiggyBankWorker::class.java)
                        .setInputData(piggyData)
                        .setConstraints(Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .build()
                WorkManager.getInstance().enqueue(piggybankWork)
            } else {
               // Invalid Intent
            }
        }
    }
}