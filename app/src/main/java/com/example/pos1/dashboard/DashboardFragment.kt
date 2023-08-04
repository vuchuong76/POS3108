package com.example.pos1.dashboard

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Half.toFloat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentDashboardBinding
import com.example.pos1.databinding.FragmentOrderListBinding
import com.example.pos1.entity.Orderlist
import com.example.pos1.order.CheckOutFragmentDirections
import com.example.pos1.orlist.DashboardViewModel
import com.example.pos1.orlist.DashboardViewModelFactory
import com.example.pos1.orlist.OrderListFragmentDirections
import com.example.pos1.orlist.OrderListViewModelFactory
import com.example.pos1.orlist.OrderlistAdapter
import com.example.pos1.orlist.OrderlistViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by activityViewModels() {
        DashboardViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
        )
    }
    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentDashboardBinding.inflate(inflater, container, false)


        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                    val action = DashboardFragmentDirections.actionDashboardFragmentToAdminAccessFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
        dashboardViewModel.allItems1.observe(this.viewLifecycleOwner) { items ->
            items.let {
                val listData: MutableList<ThongKeNgay> = ArrayList()
                var hashSet = HashSet<String>()
                for (ob in it) {
                    hashSet.add(ob.date)
                }

                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val result = hashSet.sortedBy {
                    LocalDate.parse(it, dateTimeFormatter)
                }

                var key = 0;
                for (ob in result) {

                    var amount : Float = 0.0F;
                    for (obj in it) {
                        if (obj.date.equals(ob)) {
                            amount = amount + obj.amount;
                        }
                    }
                    listData.add(ThongKeNgay(key,ob,amount ))
                    key++
                }
                binding.lineChart.isDragEnabled = true
                binding.lineChart.setScaleEnabled(true)
                val yValues: ArrayList<Entry> = ArrayList()
                for (ob in listData) {
                    yValues.add(Entry(ob.key!!.toFloat(),ob.amount!!.toFloat()))
                }
//
                val set1 = LineDataSet(yValues, "ĐƠN VỊ $")
                set1.fillAlpha = 110
                val datasets: ArrayList<ILineDataSet> = ArrayList()
                datasets.add(set1)
                val linedata = LineData(datasets)
                binding.lineChart.data = linedata
                set1.color = Color.RED
                set1.mode = LineDataSet.Mode.CUBIC_BEZIER
                binding.lineChart.description.text = "Date"
                binding.lineChart.legend.isEnabled = false
                binding.lineChart.invalidate()
                binding.lineChart.axisRight.isEnabled = false
                binding.lineChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                binding.lineChart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//
//
                val xAxis = binding.lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.labelCount = 4
                xAxis.granularity = 1f
                xAxis.isGranularityEnabled = true
                val xValsDateLabel = ArrayList<String>()
                for (ob in listData) {
                    ob.date?.let { it1 -> xValsDateLabel.add(it1) }
                }
//
                xAxis.valueFormatter = (MyAxits(xValsDateLabel))

            }
        }
    }
}

class MyAxits(private val otherStrings: ArrayList<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        if (value.toInt() >= 0 && value.toInt() <= otherStrings.size - 1) {
            return otherStrings[value.toInt()]
        } else {
            return ("").toString()
        }
    }
}

data class ThongKeNgay (
    val key : Int?,
    val date : String?,
    val amount: Float?
)