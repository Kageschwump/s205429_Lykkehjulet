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


    /**
     * In this fragment viewbinding is used in order to easily handle the view.
     * Recycler view is being used to display the hidden word
     */
    private var _binding: FragmentGameBinding? = null
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

        /**
         * Attaching a layoutmanager to the recyclerview.
         * This linearlayoutmanager is responsible for deciding how the recyclerview is gonna be organized.
         */
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        /**
         * Generating the word, hiding it and updating the view with the data of the User.
         */
        generateWord()
        updateUser()
        viewModel.hiddenWord = hideWord()

        /**
         * initializing the adapter, and attaching it to the recyclerview.
         */
        wordAdapter = WordAdapter(viewModel.hiddenWord.toString())
        binding.recyclerView.adapter = wordAdapter

        binding.status.text = "Press the wheel to get started!"

        /**
         * if available, spin the wheel
         */
        binding.wheel.setOnClickListener {
            if(available)
            spinWheel(view)
        }
        return view
    }


    /**
     * This function generates a word from the WordList. two random ints are picked and that
     * determines which word is being picked.
     */
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

    /**
     * Updates the users data in the view
     */
    fun updateUser(){
        binding.lives.text = "Lives: " + viewModel.user.lives
        binding.cash.text = "Cash: " + viewModel.user.cash
    }


    /**
     * This function hides the word.
     * In other words the word is being replaced with x amount of '_'
     */
    fun hideWord(): String{
        val builder = StringBuilder()
        for(i in viewModel.hiddenWord!!.indices){
            builder.append('_')

        }
        return builder.toString()
    }


    /**
     * This function handles what happens if you spin the wheel. A random number
     * is picked between 1 and 10. If the number is either 1,2 or 3 then the User
     * goes bankrupt, loses a life or gains a life, else a random amount of points is
     * chosen, and the user has to guess a letter in the word.
     */
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
            binding.status.text = "you rolled $roll. Guess a letter in the word"
            binding.guessButton.setOnClickListener {
                handleUserGuess(roll, view)
            }
        }
    }


    /**
     * This function handles the User guess. It takes the input from the user and compares it to
     * each letter in the hidden word. If no occurences of the guessed letter is in the word
     * the user loses a life. Else the user gets the number of occurences times the
     * amount of cash that was rolled when the user spun the wheel. this function also
     * handles if the game is either won or lost.
     */
    fun handleUserGuess(roll: Int, view: View){
        var occurences: Int

        if(binding.inputFromUser.text.isNotEmpty()) {
            binding.inputFromUser.text.toString().lowercase()

            if (binding.inputFromUser.text.toString().toCharArray()[0] !in viewModel.guessedLetters) {
                val guessedLetter: CharArray =
                    binding.inputFromUser.text.toString().toCharArray()
                occurences = checkForLetter(guessedLetter, 0)

                if (occurences == 0) {
                    binding.status.text =
                        "Wrong! \n That letter is not in the word. Spin again"
                    viewModel.user.subtractLife()
                    viewModel.guessedLetters!!.add(guessedLetter[0])
                    hideGuess()
                    updateUI()
                    isGameOver(view)

                } else {
                    binding.status.text =
                        "Correct!! \n That letter was present $occurences times. Spin again"
                    viewModel.user.cash += roll * occurences
                    viewModel.guessedLetters!!.add(guessedLetter[0])
                    isGameOver(view)
                    updateUI()
                    hideGuess()
                }
            }
        }
    }

    /**
     * Updates the view
     */
    fun updateUI(){
        updateUser()
        showGuessedLetters()
    }

    /**
     * This function is responsible for comparing the guessed letter to each of the letters
     * in the hidden word. This function also uses Stringbuilder to rebuild the hiddenword
     * with the guessed letter if the letter occurs in the hidden word. it then notifies the
     * adapter class with the new string, so the recyclerview updates. Lastly it returns the
     * amount of times the letter occured in the word.
     */
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

    /**
     * Checks if the game is either won or lost
     */
    fun isGameOver(view: View){
        if (viewModel.user.lives <= 0) {
            Navigation.findNavController(view)
                .navigate(R.id.navigateToLoseFragment)
        } else if (viewModel.word.equals(viewModel.hiddenWord)) {
            updateUser()
            Navigation.findNavController(view)
                .navigate(R.id.navigateToWinFragment)
        }
    }

    /**
     * This function disables the guess button and hides the text input field
     */
    fun hideGuess() {
        binding.guessButton.isEnabled = false
        binding.inputFromUser.visibility = INVISIBLE
        available = true
    }

    /**
     * This function enables the guess button and shows the text input field
     */
    fun showGuess(){
        binding.guessButton.isEnabled = true
        binding.inputFromUser.visibility = VISIBLE
        available = false
    }

    /**
     * This function is responsible for updating the guessed letters text field.
     * it is called after every guess made by the user
     */
    fun showGuessedLetters(){
        binding.guessedLetters.text = "Guessed letters:\n"
                for(i in viewModel.guessedLetters){
                    binding.guessedLetters.text = binding.guessedLetters.text.toString() + " $i"
                }
    }


    /**
     * This is the adapter for the recyclerview implemented in the app
     * The function update() updates the word that the recyclerview uses. The recycler view is
     * used to show the hidden word on the screen.
     *
     * The recyclerview holds textviews with the 'unchecked checkbox' drawable as a background
     * The unchecked checbox is found in @android:drawable. a letter from the hidden word is
     * then taken and put inside a checkbox.
     */
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

    /**
     * The Viewholder is responsible for holding the template of how each element in
     * the recycler is supposed to look.
     */
    inner class WordViewHolder(textView: View) : RecyclerView.ViewHolder(textView){
        var letterBox : TextView = textView.findViewById(R.id.letterBox)

    }
}

