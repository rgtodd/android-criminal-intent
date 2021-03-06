package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        Log.v(TAG, "crimeListViewModel::lazy")
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var crimeRecyclerView: RecyclerView

    private var crimeAdapter: CrimeAdapter = CrimeAdapter(emptyList())

    //
    // LIFECYCLE - CREATED
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate($savedInstanceState)")

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.v(TAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = crimeAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.v(TAG, "onViewCreated")

        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }

    // onViewStateRestored

    //
    // LIFECYCLE - CREATED -> STARTED
    //

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
    }

    //
    // LIFECYCLE - STARTED -> RESUMED
    //

    // onResume

    //
    // LIFECYCLE - RESUMED -> STARTED
    //

    // onPause

    //
    // LIFECYCLE - STARTED -> CREATED
    //

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
    }

    // onSaveInstanceState

    // onDestroyView

    //
    // LIFECYCLE - CREATED -> DESTROYED
    //

    // onDestroy

    //
    // FRAGMENT MANAGER
    //

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v(TAG, "onAttach")

        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "onDetach")

        callbacks = null
    }

    //
    // MISC CALLBACKS
    //

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(): CrimeListFragment {

            Log.v(TAG, "newInstance")

            return CrimeListFragment()
        }
    }

    private fun updateUI(crimes: List<Crime>) {

        Log.v(TAG, "updateUI")

        crimeAdapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = crimeAdapter
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

            Log.v(TAG, "CrimeAdapter::onCreateViewHolder")

            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {

            Log.v(TAG, "CrimeAdapter::onBindViewHolder")

            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {

            Log.v(TAG, "CrimeAdapter::getItemCount")

            return crimes.size
        }

    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {

            Log.v(TAG, "CrimeHolder::init")

            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {

            Log.v(TAG, "CrimeHolder::bind($crime)")

            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {

            Log.v(TAG, "CrimeHolder::onClick")

            //Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
        }

    }
}