package com.example.openwrtmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.openwrtmanager.databinding.FragmentAboutBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.icons8.setOnClickListener(View.OnClickListener {
            val uri: Uri = Uri.parse("https://icons8.com/") //设置跳转的网站
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })

        // binding.appSourceLink.setOnClickListener(View.OnClickListener {
        //     val uri: Uri = Uri.parse("https://github.com/wayne900204/opentwrt-finally") //设置跳转的网站
        //     val intent = Intent(Intent.ACTION_VIEW, uri)
        //     startActivity(intent)
        // })
        super.onViewCreated(view, savedInstanceState)
    }
}