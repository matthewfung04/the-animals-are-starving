package com.example.theanimalsarestarving.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.theanimalsarestarving.R
import com.example.theanimalsarestarving.network.NetworkManager.apiService
import com.example.theanimalsarestarving.repositories.HouseholdRepository
import com.example.theanimalsarestarving.repositories.MainRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)


        val householdId = HouseholdRepository.getCurrentHousehold()?._id.toString()
        Log.d("HistoryActivity", "householdId: $householdId")

        refreshHistory(householdId)


    }
    fun refreshHistory(householdId: String) {

        val historyTable = findViewById<TableLayout>(R.id.feedingsLogBox)
        val repository = MainRepository(apiService)

        repository.getLogs(householdId) { logs ->
            Log.d("HistoryActivity", logs.toString());
            historyTable.removeAllViews()

            // Initialize Table
            val headerRow = TableRow(this).apply {
                setPadding(8, 8, 8, 8)
            }
            listOf("Pet Name", "Fed By", "Time").forEach { header ->
                val headerView = TextView(this@HistoryActivity).apply {
                    text = header
                    textSize = 18f
                    setPadding(10, 10, 10, 10)
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Ensure equal spacing
                }
                headerRow.addView(headerView)
            }
            historyTable.addView(headerRow)

            if (logs != null) {
                if (logs.isEmpty()) {
                    val noticeText = TextView(this).apply {
                        text = "No Logs Available"
                        textSize = 20f
                        setPadding(10, 10, 10, 10)
                    }
                    historyTable.addView(noticeText)
                } else {

                    for (log in logs) {
                        val logRow = TableRow(this).apply {
                            setPadding(10, 10, 10, 10)
                        }

                        val petNameView = TextView(this).apply {
                            text = log.petName ?: "Unknown Pet"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }

                        val userNameView = TextView(this).apply {
                            text = log.userName ?: "Unknown User"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }

                        val timeView = TextView(this).apply {
                            text = formatTimestamp(log.timestamp)
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            gravity = Gravity.END  // Align time text to the right
                        }

                        logRow.addView(petNameView)
                        logRow.addView(userNameView)
                        logRow.addView(timeView)

                        historyTable.addView(logRow)
                    }
                }
            } else {
                alertMessage("Failed to fetch logs. Please try again.", historyTable)

            }
        }
    }

    private fun formatTimestamp(timestamp: String?): String {
        if (timestamp.isNullOrEmpty()) return "Unknown Time"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Handle Zulu time (UTC)

            val outputFormat = SimpleDateFormat("MMMM d h:mm a", Locale.getDefault())
            val date = inputFormat.parse(timestamp)

            date?.let { outputFormat.format(it) } ?: "Invalid Time"
        } catch (e: Exception) {
            "Invalid Time"
        }
    }

    private fun alertMessage(message: String, container: LinearLayout) {
        val warning = AlertDialog.Builder(this)
        warning.setTitle("Error")
        warning.setMessage(message)
        warning.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        warning.show()
    }


}

