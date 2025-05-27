package com.example.smilejobportal.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smilejobportal.R

class ReviewFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        val aboutTxt: TextView = view.findViewById(R.id.aboutTxt)
        val benefits = getString(R.string.benefit_text).split("\n")

        val bulletList = benefits.joinToString("\n") { "• $it" }

        aboutTxt.text = bulletList


        return view
    }


}