package com.example.smilejobportal.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smilejobportal.R

class AboutFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val descTxt = view.findViewById<TextView>(R.id.descriptionTxt)
        descTxt.text = arguments?.getString("description","")?:""

        val aboutTxt=view.findViewById<TextView>(R.id.aboutTxt)
        aboutTxt.text =arguments?.getString("about","")?:""
    }

}