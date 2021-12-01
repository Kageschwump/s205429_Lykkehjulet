package com.example.lykkehjulet1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lykkehjulet1.databinding.FragmentGameBinding
import java.lang.StringBuilder
import kotlin.random.Random


class GameFragment : Fragment() {


    private var _binding: FragmentGameBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: GameFragmentViewModel by viewModels()
    private lateinit var wordAdapter: WordAdapter
    private var available = true


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
        viewModel.hiddenWord = hideWord()

        wordAdapter = WordAdapter(viewModel.hiddenWord.toString())
        binding.recyclerView.adapter = wordAdapter

        binding.status.text = "Press the wheel to get started!"

        binding.wheel.setOnClickListener {
            if(available)
            spinWheel(view)
        }
        return view
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
        viewModel.hiddenWord = hiddenWord
        viewModel.word = hiddenWord

    }

    fun updateUser(){
        binding.lives.text = "Lives: " + viewModel.user.lives
        binding.cash.text = "Cash: " + viewModel.user.cash
    }


    fun hideWord(): String{
        val builder = StringBuilder()
        for(i in viewModel.hiddenWord!!.indices){
            builder.append('_')

        }
        return builder.toString()
    }

    fun spinWheel(view: View){
        var nr: Int = (1..10).random()

        if(nr == 1){
            viewModel.user.bankrupt()
            updateUser()
            binding.status.text = "You went bankrupt! :-( \n" +
                    "press the wheel to spin again"
        } else if(nr == 2){
            viewModel.user.addLife()
            updateUser()
            binding.status.text = "You gained a life! :-) \n" +
                    "press the wheel to spin again"

        } else if(nr == 3){
            viewModel.user.subtractLife()
            updateUser()
            binding.status.text = "You lost a life :-( \n" +
                    "press the wheel to spin again"
            if(viewModel.user.lives <= 0){
                Navigation.findNavController(view).navigate(R.id.navigateToLoseFragment)
            }

        }else {
            showGuess()
            var roll = (1..20).random()*100
            var occurences: Int

            binding.status.text = "you rolled $roll. Guess a letter in the word"
            binding.guessButton.setOnClickListener {
                if(binding.inputFromUser.text.isNotEmpty()) {

                    binding.inputFromUser.text.toString().lowercase()

                    if (binding.inputFromUser.text.toString().toCharArray()[0] !in viewModel.guessedLetters) {
                        val guessedLetter: CharArray =
                            binding.inputFromUser.text.toString().toCharArray()

                        occurences = checkForLetter(guessedLetter, 0)

                        if (occurences == 0) {
                            viewModel.user.subtractLife()
                            viewModel.guessedLetters!!.add(guessedLetter[0])
                            showGuessedLetters()
                            hideGuess()
                            updateUser()
                            binding.status.text =
                                "Wrong! \n That letter is not in the word. Spin again"
                            if (viewModel.user.lives <= 0) {
                                Navigation.findNavController(view)
                                    .navigate(R.id.navigateToLoseFragment)
                            }
                        } else {
                            binding.status.text =
                                "Correct!! \n That letter was present $occurences times. Spin again"
                            viewModel.user.cash += roll * occurences
                            viewModel.guessedLetters!!.add(guessedLetter[0])
                            showGuessedLetters()
                            if (viewModel.word.equals(viewModel.hiddenWord)) {
                                updateUser()
                                Navigation.findNavController(view)
                                    .navigate(R.id.navigateToWinFragment)
                            }
                            updateUser()
                            hideGuess()
                        }
                    }
                }
            }
        }
    }


    fun checkForLetter(char: CharArray, fixedPos: Int): Int{
        var counter: Int = 0
        val builder = StringBuilder()


            for (i in viewModel.word!!.indices) {
                if (char[fixedPos] == viewModel.word!![i]) {
                    if (char[fixedPos] != viewModel.hiddenWord!![i]) {
                        builder.append(char[fixedPos])
                        counter++
                    } else {
                        builder.append(char[fixedPos])
                    }
                }
                else if( viewModel.word!![i] == viewModel.hiddenWord!![i]){
                    builder.append(viewModel.hiddenWord!![i])
                } else
                    builder.append('_')
            }

        viewModel.hiddenWord = builder.toString()
        wordAdapter.update(viewModel.hiddenWord.toString())
        for(i in viewModel.hiddenWord!!.indices)
            wordAdapter.notifyItemChanged(i)

        return counter
    }

    fun hideGuess() {
        binding.guessButton.isEnabled = false
        available = true
    }

    fun showGuess(){
        binding.guessButton.isEnabled = true
        available = false
    }

    fun showGuessedLetters(){
        binding.guessedLetters.text = "Guessed letters:\n"
                for(i in viewModel.guessedLetters){
                    binding.guessedLetters.text = binding.guessedLetters.text.toString() + " $i"
                }
    }


    inner class WordAdapter(var string: String): RecyclerView.Adapter<GameFragment.WordViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.letterbox, parent, false)

            return WordViewHolder(view)
        }

        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            holder.letterBox.text = string[position].toString()
        }

        override fun getItemCount(): Int {
            return string.length
        }

        fun update(hiddenWord: String){
            string = hiddenWord
        }
    }

    inner class WordViewHolder(textView: View) : RecyclerView.ViewHolder(textView){
        var letterBox : TextView = textView.findViewById(R.id.letterBox)

    }
}

