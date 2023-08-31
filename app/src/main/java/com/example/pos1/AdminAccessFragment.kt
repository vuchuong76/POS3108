package com.example.pos1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pos1.weather.CurrentWeather
import com.example.pos1.databinding.FragmentAdminAccessBinding
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory
import com.example.test.Utilites.ApiUtilites
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAccessFragment : Fragment() {
    private lateinit var binding: FragmentAdminAccessBinding
    private val viewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao(),
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            fetchWeatherData()
            handler.postDelayed(this, 20_000) // Lặp lại mỗi 20 giây
        }
    }

    companion object {
        val q = "35.676919,139.6503106"
        val key = "7a32f1087fda4df59d9154311231508"
        val days = "10"
        val dt = "2023-09-03"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminAccessBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()
        val dish1TextView: TextView = view.findViewById(R.id.dish1TextView)
        val dish2TextView: TextView = view.findViewById(R.id.dish2TextView)
        val dish3TextView: TextView = view.findViewById(R.id.dish3TextView)


        ApiUtilites.getApiInterface()?.getCurrentFuture(
            key, q, days, dt
        )?.enqueue(object : Callback<CurrentWeather> {
            @RequiresApi(Build.VERSION_CODES.O)
            //Được gọi khi có một phản hồi từ server
            override fun onResponse(
                call: Call<CurrentWeather>,
                response: Response<CurrentWeather>
            ) {
                if (response.isSuccessful) {
                    val weatherCurrent = response.body()
                    binding.tvTemperature.text =
                        "Temperature : ${weatherCurrent?.current?.temp_c.toString()}°C "
                    binding.tvWeatherDescription.text =
                        "Description : ${weatherCurrent?.current?.condition?.text}"
                    val urlImage = "http:" + weatherCurrent?.current?.condition?.icon
                    Glide.with(requireContext())
                        .load(urlImage).error(R.drawable.back)
                        .into(binding.imageWeather)
                } else {
                    binding.tvTemperature.text =
                        "No connection "
                }

            }

            //Được gọi khi có một lỗi xảy ra trong quá trình gọi API, ví dụ như không có kết nối mạng.
            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.d("ERROR", t.toString())
            }
        })

        handler.post(updateRunnable)

        viewModel.topDishes.observe(viewLifecycleOwner) { dishes ->
            dishes?.let {
                if (dishes.isNotEmpty()) {
                    dish1TextView.text = "1. ${dishes[0].name}"
                    binding.dish1quantity.text = dishes[0].total_quantity.toString()
                }
                if (dishes.size > 1) {
                    dish2TextView.text = "2. ${dishes[1].name}"
                    binding.dish2quantity.text = dishes[1].total_quantity.toString()
                }
                if (dishes.size > 2) {
                    dish3TextView.text = "3. ${dishes[2].name}"
                    binding.dish3quantity.text = dishes[2].total_quantity.toString()
                }
            }
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logOutDialog()
                    true
                }

                R.id.chart -> {
                    val action =
                        AdminAccessFragmentDirections.actionAdminAccessFragmentToDashboardFragment()
                    findNavController().navigate(action)
                    true
                }

                R.id.coupon -> {
                    val action =
                        AdminAccessFragmentDirections.actionAdminAccessFragmentToCouponFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }

    private fun logOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you really want to log out?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
            .show()
    }

    private fun disableBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )
    }

    // rest api weather current device
    //Khởi tạo API call,
    // getCurrentFuture(key, q, days, dt) là lấy thông tin thời tiết hiện tại và dự báo.
    fun fetchWeatherData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }

}