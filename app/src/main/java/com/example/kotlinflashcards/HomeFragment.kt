package com.example.kotlinflashcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kotlinflashcards.adapter.FlashcardAdapter
import com.example.kotlinflashcards.bdd.FlashcardDatabase
import com.example.kotlinflashcards.clases.Flashcard
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_rv_card.view.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    var arrFlashcards = ArrayList<Flashcard>()
    var adapter = FlashcardAdapter()

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_recyclerView.setHasFixedSize(true)
        rv_recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                var cards = FlashcardDatabase.getDatabase(it).flashcardDao().getAllCards()
                adapter!!.setData(cards)
                arrFlashcards = cards as ArrayList<Flashcard>
                rv_recyclerView.adapter = adapter
            }
        }

        adapter!!.setOnClickListener(onClicked)
        adapter!!.setOnLongClickListener(onLongClicked)

        // button listener
        fab_createNote.setOnClickListener {
            replaceFragment(CreateCardFragment.newInstance(),false)
        }

        // searchview listener
        sv_search.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                var tempArr = ArrayList<Flashcard>()

                for (arr in arrFlashcards){
                    if (arr.cardTitle!!.toLowerCase(Locale.getDefault()).contains(query.toString())){
                        tempArr.add(arr)
                    }
                }

                adapter.setData(tempArr)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }

    // Transició entre pregunta i resposta al clicar a sobre de la flashcard
    private val onClicked = object : FlashcardAdapter.OnItemClickListener {
        override fun onClicked(notesId: Int, view: View) {
            if (view.tv_cardTitle.visibility == View.VISIBLE){
                view.tv_cardDetails.visibility = View.VISIBLE
                view.tv_cardTitle.visibility = View.INVISIBLE
            } else {
                view.tv_cardDetails.visibility = View.INVISIBLE
                view.tv_cardTitle.visibility = View.VISIBLE
            }
        }
    }

    // Quan mantens apretat a sobre d'una flashcard
    private val onLongClicked = object : FlashcardAdapter.OnItemLongClickListener{
        override fun onLongClicked(notesId: Int): Boolean {
            var fragment : Fragment
            var bundle = Bundle()
            bundle.putInt("cardId", notesId)
            fragment = CreateCardFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment,false)

            return true
        }
    }

    fun replaceFragment(fragment: Fragment, isTransition: Boolean){
        val fragmentTransition = activity!!.supportFragmentManager.beginTransaction()

        if (isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }
}