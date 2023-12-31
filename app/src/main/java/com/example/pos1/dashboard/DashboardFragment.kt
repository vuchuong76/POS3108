package com.example.pos1.dashboard

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentDashboardBinding
import com.example.pos1.entity.Orderlist
import com.example.pos1.orlist.DashboardViewModel
import com.example.pos1.orlist.DashboardViewModelFactory
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Đây là một Fragment dùng để hiển thị một bảng thông kê.
@Suppress("DEPRECATION")
class DashboardFragment : Fragment() {
    // ViewModel giúp lấy dữ liệu và giữ dữ liệu khi xoay màn hình hoặc khi có sự thay đổi cấu hình.
    private val dashboardViewModel: DashboardViewModel by activityViewModels {
        DashboardViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
        )
    }

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Khởi tạo binding và gán layout cho fragment.
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Đặt sự kiện click cho các item trên toolbar.
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }

        dashboardViewModel.allItems1.observe(this.viewLifecycleOwner) { items ->
            updateChartData(items)
        }

        binding.week.setOnClickListener {
            displayLastSevenDaysData()
        }
        binding.month.setOnClickListener {
            displayLastMonthData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateChartData(items: List<Orderlist>) { // Thay YourItemType bằng kiểu dữ liệu thật sự của bạn
        val listData: MutableList<ThongKeNgay> = ArrayList()
        val hashSet = HashSet<String>()

        for (ob in items) {
            hashSet.add(ob.date)
        }

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        //sortedByDescending để sắp xếp ngược lại
        val sortedDates = hashSet.sortedBy {
            LocalDate.parse(it, dateTimeFormatter)
        }

        var key = 0
        for (date in sortedDates) {
            var amount = 0.0
            for (item in items) {
                if (item.date == date) {
                    amount += item.amount!!
                }
            }
            listData.add(ThongKeNgay(key, date, amount))
            key++
        }

        setUpChart(listData)
    }
    override fun onResume() {
        super.onResume()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE // Ẩn hoặc hiển thị dựa vào điều kiện cụ thể của bạn
    }

    override fun onPause() {
        super.onPause()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE // Đảm bảo nó được hiển thị trở lại khi rời khỏi Fragment (nếu cần)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayLastSevenDaysData() {
        val items = dashboardViewModel.allItems1.value
        items?.let {
            val listData: MutableList<ThongKeNgay> = ArrayList()
            val hashSet = HashSet<String>()

            for (ob in it) {
                hashSet.add(ob.date)
            }

            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val sortedDates = hashSet.sortedBy {
                LocalDate.parse(it, dateTimeFormatter)
            }

            // Lọc ra 7 ngày gần nhất từ danh sách đã sắp xếp:
            val recentSevenDaysDates = if (sortedDates.size > 7) {
                sortedDates.takeLast(7)
            } else {
                sortedDates
            }

            var key = 0
            for (date in recentSevenDaysDates) {
                var amount = 0.0
                for (item in it) {
                    if (item.date == date) {
                        amount += item.amount
                    }
                }
                listData.add(ThongKeNgay(key, date, amount))
                key++
            }

            setUpChart(listData)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayLastMonthData() {
        val items = dashboardViewModel.allItems1.value
        items?.let {
            val listData: MutableList<ThongKeNgay> = ArrayList()
            val hashSet = HashSet<String>()

            for (ob in it) {
                hashSet.add(ob.date)
            }

            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val sortedDates = hashSet.sortedBy {
                LocalDate.parse(it, dateTimeFormatter)
            }

            // Lọc ra 7 ngày gần nhất từ danh sách đã sắp xếp:
            val recentSevenDaysDates = if (sortedDates.size > 30) {
                sortedDates.takeLast(30)
            } else {
                sortedDates
            }

            var key = 0
            for (date in recentSevenDaysDates) {
                var amount = 0.0
                for (item in it) {
                    if (item.date == date) {
                        amount += item.amount
                    }
                }
                listData.add(ThongKeNgay(key, date, amount))
                key++
            }

            setUpChart(listData)
        }
    }

    // Hàm cấu hình và cập nhật dữ liệu cho biểu đồ.
    private fun setUpChart(listData: List<ThongKeNgay>) {
        binding.lineChart.apply {
            isDragEnabled = true
            setScaleEnabled(true)
            description.text = "Date"
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        }
//Tạo một danh sách yValues chứa các điểm dữ liệu.
// Mỗi điểm dữ liệu được lấy từ listData, với trục X là giá trị key và trục Y
// là giá trị amount của mỗi đối tượng ThongKeNgay trong listData.
        val yValues: ArrayList<Entry> = ArrayList()
        for (data in listData) {
            yValues.add(Entry(data.key!!.toFloat(), data.amount!!.toFloat()))
        }

        //Mỗi LineDataSet biểu diễn một chuỗi dữ liệu trên biểu đồ đường.
        //Đặt màu, chế độ vẽ, và các thuộc tính khác cho chuỗi dữ liệu.
        val set1 = LineDataSet(yValues, "ĐƠN VỊ $").apply {
            fillAlpha = 110
            color = Color.RED
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        val datasets: ArrayList<ILineDataSet> = ArrayList()
        datasets.add(set1)
        binding.lineChart.data = LineData(datasets)
        binding.lineChart.invalidate()

        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelCount = 8
            granularity = 1f
            isGranularityEnabled = true
            valueFormatter = MyAxits(listData.map { it.date ?: "" } as ArrayList<String>)
        }
    }
}

// Lớp dùng để format giá trị trên trục x của biểu đồ.
class MyAxits(private val dates: ArrayList<String>) : ValueFormatter() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    private val outputFormatter = DateTimeFormatter.ofPattern("dd-MM")
//Được ghi đè từ lớp ValueFormatter và trả về một chuỗi biểu diễn giá trị Float dưới dạng chuỗi.
    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    //xác định cách các ngày được hiển thị trên trục X của biểu đồ.
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        //Nếu giá trị đầu vào (biểu diễn bằng Float) nằm trong phạm vi của danh sách dates,
        // nó sẽ lấy ra chuỗi ngày tương ứng, chuyển đổi nó sang LocalDate và sau đó định dạng
        // lại chuỗi ngày theo dạng mong muốn (ví dụ: từ "2023-08-23" thành "23-08").
        return if (value.toInt() in 0 until dates.size) {
            val localDate = LocalDate.parse(dates[value.toInt()], inputFormatter)
            outputFormatter.format(localDate)
        } else {
            ""
        }
    }
}

// Lớp dùng để biểu diễn dữ liệu cho mỗi ngày.
data class ThongKeNgay(
    val key: Int?,
    val date: String?,
    val amount: Double?
)
