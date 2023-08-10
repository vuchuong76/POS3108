package com.example.pos1

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pos1.Models.CurrentWeather
import com.example.pos1.databinding.FragmentAdminAccessBinding
import com.example.test.Utilites.ApiUtilites
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAccessFragment : Fragment() {
    private lateinit var binding: FragmentAdminAccessBinding
    companion object {
        val q = "35.676919,139.6503106"
        val key = "40ceed1e8cfe4391b1f11312230308"
        val days = "10"
        val dt = "2023-08-20"
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminAccessBinding.inflate(inflater, container, false)

        binding.staffList.setOnClickListener {
            findNavController().navigate(R.id.action_adminAccessFragment_to_staffListFragment)
        }
        binding.table.setOnClickListener {
            findNavController().navigate(R.id.action_adminAccessFragment_to_tableFragment)
        }
        binding.menuEdit.setOnClickListener {
            findNavController().navigate(R.id.action_adminAccessFragment_to_menuListFragment)
        }
        binding.schedule.setOnClickListener {
            findNavController().navigate(R.id.action_adminAccessFragment_to_scheduleFragment)
        }

        setHasOptionsMenu(true)
        return binding.root
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // rest api weather current device
        ApiUtilites.getApiInterface()?.getCurrentFuture(
            key
            ,q, days,dt)?.enqueue(object : Callback<CurrentWeather> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                if (response.isSuccessful){
                    val weatherCurrent = response.body()
                    binding.tvTemperature.text = weatherCurrent?.current?.temp_c.toString()
                    binding.tvWeatherDescription.text = weatherCurrent?.current?.condition?.text
                    val urlImage = "http:" + weatherCurrent?.current?.condition?.icon
                    Glide.with(requireContext())
                        .load(urlImage).error(R.drawable.back)
                        .into(binding.imageWeather)
                }
                else{
                    Log.d("ERROR",response.body().toString())
                }

            }
            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.d("ERROR",t.toString())
            }
        })

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                    R.id.logout -> {
                        val action = AdminAccessFragmentDirections.actionAdminAccessFragmentToLoginFragment()
                        findNavController().navigate(action)
                       true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                    true
                }
                R.id.chart -> {
                        val action = AdminAccessFragmentDirections.actionAdminAccessFragmentToDashboardFragment()
                        findNavController().navigate(action)
                       true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                    true
                }
                R.id.coupon -> {
                        val action = AdminAccessFragmentDirections.actionAdminAccessFragmentToCouponFragment()
                        findNavController().navigate(action)
                       true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                    true
                }
                else -> false
            }
        }
    }




}