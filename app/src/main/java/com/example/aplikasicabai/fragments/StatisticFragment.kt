package com.example.aplikasicabai.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class StatisticFragment : Fragment() {

    private var _statisticBinding: FragmentStatisticBinding? = null
    private val statisticBinding get() = _statisticBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _statisticBinding = FragmentStatisticBinding.inflate(inflater, container,false)
        val sharedPreferences: SharedPreferences? = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)

        val sampleData = ArrayList<Entry>()
        sampleData.add(Entry(0F, 20F))
        sampleData.add(Entry(1F, 25F))
        sampleData.add(Entry(2F, 30F))
        sampleData.add(Entry(3F, 21F))
        sampleData.add(Entry(4F, 24F))
        sampleData.add(Entry(5F, 18F))
        sampleData.add(Entry(6F, 20F))
        sampleData.add(Entry(7F, 21F))
        sampleData.add(Entry(8F, 25F))
        sampleData.add(Entry(9F, 28F))

        val sampleDate = ArrayList<String>()
        sampleDate.add("01-Aug")
        sampleDate.add("02-Aug")
        sampleDate.add("03-Aug")
        sampleDate.add("04-Aug")
        sampleDate.add("05-Aug")
        sampleDate.add("06-Aug")
        sampleDate.add("07-Aug")
        sampleDate.add("08-Aug")
        sampleDate.add("09-Aug")
        sampleDate.add("10-Aug")

        val date = AxisDateFormatter(sampleDate.toArray(arrayOfNulls(sampleDate.size)))

        val sampleLineDataSet = LineDataSet(sampleData, "Sample Kelembapan")
        sampleLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        sampleLineDataSet.color = Color.BLUE
        sampleLineDataSet.circleRadius = 5f
        sampleLineDataSet.setCircleColor(Color.BLUE)

        val legend = statisticBinding.lineChart.legend
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
                    statisticBinding.edDatePicker.setText(dateRangePicker.headerText)
                }
            }

            lineChart.description.isEnabled = true
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.data = LineData(sampleLineDataSet)
            lineChart.animateXY(100, 500)
            lineChart.xAxis?.valueFormatter = date
        }

        return statisticBinding.root
    }

    override fun onResume() {
        super.onResume()
        val labelStats = resources.getStringArray(R.array.lable_stats)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, labelStats)
        statisticBinding.tvAutocomplete.setAdapter(arrayAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statisticBinding = null
    }
}



