package com.bignerdranch.android.criminalintent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

private const val TAG = "CrimeDetailViewModel"

class CrimeDetailViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        Log.v(TAG, "loadCrime($crimeId)")
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        Log.v(TAG, "saveCrime($crime)")
        crimeRepository.updateCrime(crime)
    }
}