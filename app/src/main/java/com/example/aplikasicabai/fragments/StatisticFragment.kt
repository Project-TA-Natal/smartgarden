package com.example.aplikasicabai.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.util.Pair
import com.example.aplikasicabai.LoginActivity
import com.example.aplikasicabai.R
import com.example.aplikasicabai.SplashscreenActivity
import com.example.aplikasicabai.databinding.FragmentStatisticBinding
import com.example.aplikasicabai.formatter.AxisDateFormatter
import com.example.aplikasicabai.model.History
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticFragment : Fragment() {

    private var _statisticBinding: FragmentStatisticBinding? = null
    private val statisticBinding get() = _statisticBinding!!
    private lateinit var dbRef : DatabaseReference
    private var lineDataset = LineDataSet(null, null)
    private var timeListFormatted: ValueFormatter ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _statisticBinding = FragmentStatisticBinding.inflate(inflater, container,false)
        val sharedPreferences: SharedPreferences? = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)

        val legend = statisticBinding.lineChart.legend
        statisticBinding.lineChart.setNoDataText("Tidak ada data yang tersedia")
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation= Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        with(statisticBinding) {
            btnLogout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Apakah Anda ingin keluar?")
                    .setNegativeButton("Tidak") { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Ya") { dialog, which ->
                        val editor = sharedPreferences?.edit()
                        editor?.clear()
                        editor?.apply()
                        Toast.makeText(context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    .show()
            }
            edDatePicker.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Pilih Tanggal")
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()
                dateRangePicker.show(this@StatisticFragment.requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")
                dateRangePicker.addOnPositiveButtonClickListener {
                    val first: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    val second: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    first.timeInMillis = it.first
                    second.timeInMillis = it.second
                    val format = SimpleDateFormat("dd/MM/yyyy")
                    val startDate = format.format(first.time)
                    val endDate = format.format(second.time)
                    val showDate = "$startDate-$endDate"
                    statisticBinding.edDatePicker.setText(showDate)
                }
            }
            btnSearchStats.setOnClickListener {
                val dateRange = edDatePicker.text.toString()
                if (dateRange == "") {
                    Toast.makeText(context, "Pilih Tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
                } else {
                    val configType = tvAutocomplete.text.toString()
                    val dateRangeSplit = dateRange.split("-")
                    val startAt = dateRangeSplit[0]
                    val endAt = dateRangeSplit[1]
                    println("INI START DATE $startAt")
                    println("INI END DATE $endAt")
                    println("INI KATEGORI TYPE $configType")
                    getHistoryData(startAt, endAt, configType)
                }
            }
        }

        return statisticBinding.root
    }

    override fun onResume() {
        super.onResume()
        val labelStats = resources.getStringArray(R.array.lable_stats)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, labelStats)
        // statisticBinding.tvAutocomplete.setAdapter(arrayAdapter)
        statisticBinding.apply {
            tvAutocomplete.setAdapter(arrayAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statisticBinding = null
    }

    private fun getHistoryData(start_at: String, end_at: String, config_type: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("histori_data")
        val dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kelembapanData = ArrayList<Entry>()
                val suhuData = ArrayList<Entry>()
                val phTanahData = ArrayList<Entry>()
                val dataWaktu = ArrayList<String>()
                if (snapshot.hasChildren()) {
                    var entryIndex = 0;
                    for (historySnapshot in snapshot.children) {
                        val waktu = historySnapshot.child("Time").value.toString()
                        var suhu = historySnapshot.child("Suhu_Udara").value
                        if (suhu == null) suhu = 0
                        suhu = "${suhu}f"
                        var kelembapan = historySnapshot.child("Kelembapan_tanah").value
                        if (kelembapan == null) kelembapan = 0
                        kelembapan = "${kelembapan}f"
                        var phTanah = historySnapshot.child("Ph_Tanah").value
                        if (phTanah == null) phTanah = 0
                        phTanah = "${phTanah}f"
                        val historyData = History(kelembapan, phTanah, suhu, waktu)
                        val kelembapanToFloat = historyData.kelembapan?.toFloat()
                        val suhuToFloat = historyData.suhu?.toFloat()
                        val phTanahToFloat = historyData.phTanah?.toFloat()
                        val entryIndexToFloat = "${entryIndex}f".toFloat()
                        kelembapanData.add(Entry(entryIndexToFloat, kelembapanToFloat as Float))
                        suhuData.add(Entry(entryIndexToFloat, suhuToFloat as Float))
                        phTanahData.add(Entry(entryIndexToFloat, phTanahToFloat as Float))
                        dataWaktu.add(historyData.waktu as String)
                        entryIndex += 1
                        Log.d("INI SIZE DATA WAKTU", "${dataWaktu.size}")
                        Log.d("INI SIZE DATA KELBPN", "${kelembapanData.size}")
                        Log.d("INI SIZE DATA PHTANAH", "${phTanahData.size}")
                        Log.d("INI SIZE DATA SUHU", "${suhuData.size}")
                        if (config_type == "Kelembaban Tanah") {
                            showLineChart(kelembapanData, dataWaktu, config_type)
                            Log.d("INI kelembapanData", "$kelembapanData")
                        }
                        if (config_type == "Suhu Ruang") {
                            showLineChart(suhuData, dataWaktu, config_type)
                        }
                        if (config_type == "pH Tanah") {
                            showLineChart(phTanahData, dataWaktu, config_type)
                        }
                    }
                } else {
                    statisticBinding.lineChart.apply {
                        clear()
                        invalidate()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        dbRef.addValueEventListener(dbListener)
    }

    private fun showLineChart(entry_list: ArrayList<Entry>, time_list: ArrayList<String>, config_type: String) {
        timeListFormatted = AxisDateFormatter(time_list.toArray(arrayOfNulls(time_list.size)))
        lineDataset = LineDataSet(entry_list, config_type)
        lineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataset.color = Color.BLUE
        lineDataset.circleRadius = 4f
        lineDataset.lineWidth = 2f
        lineDataset.setCircleColor(Color.BLUE)
        statisticBinding.apply {
            lineChart.description.isEnabled = true
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.data = LineData(lineDataset)
            lineChart.animateXY(100, 500)
            lineChart.xAxis?.valueFormatter = timeListFormatted
        }
    }
}



