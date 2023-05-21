package com.example.notes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.adapter.NoteAdapter
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentHomeBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel


        setUpRecyclerView()

        binding.cardView.setOnClickListener {
            addDemoNote()
        }

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_homeFragment_to_newNoteFragment
            )
        }
    }

    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter()

        binding.recyclerView.apply{
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL,
            )
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner
            ) { note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }

    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if(note.isNotEmpty()){
                binding.cardView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }else{
                binding.cardView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        // searchNote(query)
        return false
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query"
        notesViewModel.searchNote(searchQuery).observe(
            this
        ) { list -> noteAdapter.differ.submitList(list) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addDemoNote() {
        val note1 = Note(0, "Hello", "This is my first note.")
        val note2 = Note(0, "Kotlin", "This app is built with Kotlin" +
                " and Android Studio. It lets you save notes with ease and provide minimal" +
                " UI with all the features.")
        val note3 = Note(0, "Coroutines",
            "I have to learn coroutines now and after that I will be moving to" +
                    " API calling and Firebase.")
        val note4 = Note(0, "Info", "This app implements Recycler View," +
                "Room Database, MVVM Architecture with navigation components and much more")
        val note5 = Note(0, "Update and Delete",
            "Update and Delete features are fixed now.")
        val note6 = Note(0, "Dark mode", "Dark Mode is working like" +
                " a charm on my phone but I am not sure if it will work the" +
                " same in all other phones as it had some text color issue on my emulator.")
        val note7 = Note(0, "Updates", "Updates to this app won't be" +
                " coming any soon as I have no plan to do any further modifications" +
                " on this app. I barely managed to understand the" +
                " mechanism and codes of this app for now.")
        val note8 = Note(0, "Welcome", "Start adding new Notes now...")


        notesViewModel.addNote(note1); notesViewModel.addNote(note2);
        notesViewModel.addNote(note3); notesViewModel.addNote(note4);
        notesViewModel.addNote(note5); notesViewModel.addNote(note6);
        notesViewModel.addNote(note7); notesViewModel.addNote(note8);
    }
}