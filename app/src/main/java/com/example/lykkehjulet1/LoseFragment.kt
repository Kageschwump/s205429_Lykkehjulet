package com.example.lykkehjulet1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation


class LoseFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lose, container, false)

        val button : Button = view.findViewById(R.id.returnToMainMenuButton2)
        button.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateToStartFragmentFromLose) }

        return view
    }
}

