package com.example.pos1.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pos1.R

class MenuTabletFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_tablet, container, false)
    }

    // Nếu bạn muốn thực hiện thêm thao tác nào đó sau khi fragment được tạo, hãy override phương thức onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Không cần thêm bất kỳ logic gì ở đây, vì mọi thao tác đã được thực hiện trong MenuFragment và OrderDetailFragment
    }
}