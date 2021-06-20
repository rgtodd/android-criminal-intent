package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
const val DIALOG_DATE = "DialogDate"
const val REQUEST_DATE = "RequestDate"

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        Log.v(TAG, "crimeDetailViewModel::lazy")
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    //
    // LIFECYCLE - CREATED
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate($savedInstanceState)")

        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
//        Log.d(TAG, "args bundle crime ID : $crimeId")
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated")

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )

        setFragmentResultListener(REQUEST_DATE) { _, bundle ->
            val date = bundle.getSerializable(DIALOG_DATE) as Date
            crime.date = date
            updateUI()
        }

//        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
    }

    // onViewStateRestored

    //
    // LIFECYCLE - CREATED -> STARTED
    //

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not required
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // Not required
            }

        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }
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

        crimeDetailViewModel.saveCrime(crime)
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

        // No business logic required
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "onDetach")
    }

    private fun updateUI() {
        Log.v(TAG, "updateUI")

        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            Log.v(TAG, "newInstance($crimeId)")

            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

//    override fun onFragmentResult(requestKey: String, result: Bundle) {
//        when (requestKey) {
//            REQUEST_DATE -> {
//                val date = result.getSerializable(DIALOG_DATE) as Date
//                crime.date = date
//                updateUI()
//            }
//        }
//    }
}