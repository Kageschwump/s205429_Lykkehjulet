package com.example.lykkehjulet1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lykkehjulet1.databinding.FragmentGameBinding
import kotlin.random.Random


class GameFragment : Fragment() {


    private var _binding: FragmentGameBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: GameFragmentViewModel by viewModels()
    private var word: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        generateWord()
        updateUser()
        var wordArray: CharArray = word.toCharArray()
        var hiddenWordArray: CharArray = word.toCharArray()
        for(i in hiddenWordArray.indices){
            hiddenWordArray[i] = '_'
        }

        var wordAdapter = WordAdapter(hiddenWordArray)
        binding.recyclerView.adapter = wordAdapter

        binding.status.text = "Press the wheel to get started!"

        binding.wheel.setOnClickListener {
            spinWheel()
            updateUser()
        }
        return view
    }


    fun updateUser(){
        binding.lives.text = "Lives: " + viewModel.user.lives
        binding.cash.text = "Cash: " + viewModel.user.cash
    }

    fun generateWord(){
        var hiddenWord: String = ""

        var catNumber: Int
        var wordNumber: Int

        catNumber = (1..3).random()
        wordNumber = (0..2).random()

        if(catNumber == 1){
            hiddenWord = WordList.Birds.cat[wordNumber]
            binding.category.text = "Birds"
        }
        if(catNumber == 2){
            hiddenWord = WordList.Tools.cat[wordNumber]
            binding.category.text = "Tools"
        }
        if(catNumber == 3){
            hiddenWord = WordList.Mammals.cat[wordNumber]
            binding.category.text = "Mammals"
        }
        this.word = hiddenWord

    }

    fun spinWheel(){
        var nr: Int = (1..10).random()

        if(nr == 1){
            viewModel.user.bankrupt()
            binding.status.text = "You went bankrupt! :-( \n" +
                    "press the wheel to spin again"
        } else if(nr == 2){
            viewModel.user.addLife()
            binding.status.text = "You gained a life! :-) \n" +
                    "press the wheel to spin again"

        } else if(nr == 3){
            viewModel.user.subtractLife()
            binding.status.text = "You lost a life :-( \n" +
                    "press the wheel to spin again"

        }else {
            var roll = (1..20).random()*100
            binding.status.text = "you rolled $roll. Guess a letter in the word"
        }

    }


    inner class WordAdapter(var arr: CharArray): RecyclerView.Adapter<GameFragment.WordViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.letterbox, parent, false)

            return WordViewHolder(view)
        }

        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            holder.letterBox.text = arr[position].toString()
        }

        override fun getItemCount(): Int {
            return arr.size
        }
    }

    inner class WordViewHolder(textView: View) : RecyclerView.ViewHolder(textView){
        var letterBox : TextView = textView.findViewById(R.id.letterBox)

    }
}

