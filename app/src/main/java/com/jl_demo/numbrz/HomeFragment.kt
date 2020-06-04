package com.jl_demo.numbrz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.jl_demo.numbrz.network.NumberApiService
import com.jl_demo.numbrz.network.SuggestionApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import timber.log.Timber

class HomeFragment: Fragment() {

    var connection: Disposable? = null
    private val pullAnotherService by lazy {
        NumberApiService.ApiService.create()
    }
    private val pullAnotherSuggestion by lazy {
        SuggestionApiService.ApiService.create()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null)
            pullAnotherFact()
        view.apply {
            home_refresh_fab.setOnClickListener {
                pullAnotherFact()
            }
        }
    }

    private fun showSomethingHasGoneWrongDialog() {
        MaterialDialog(requireActivity()).show {
            title(text = "Something went wrong")
            message(text = "Would you like a suggestion of what to do while we wait for the server to come back online?")
            positiveButton(text = "Sure") { dialog ->
                suggestAlternative()
                dialog.dismiss()
            }
            negativeButton(text = "No thanks")
        }
    }

    fun suggestAlternative(){
        connection = pullAnotherSuggestion.pullAnotherSuggestion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Timber.d("JL_ Success, ${result.description} was suggested")
                    setBoredResponse(result)

                },
                { error ->
                    Timber.d("JL_ Error... something has gone wrong AGAIN $error")
                }
            )
    }


    private fun pullAnotherFact() {
        connection = pullAnotherService.pullRandomMathNum()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Timber.d("JL_ Success, ${result.found} was suggested")
                    setNumResponse(result)
                },
                { error ->
                    showSomethingHasGoneWrongDialog()
                    Timber.d("JL_ Error... something has gone wrong  $error")
                }
            )
    }

    fun setNumResponse(number: NumberApiService.NumbersResult){
        home_number.text = number.number
        home_number_desc.text = number.numberResponse
    }

    fun setBoredResponse(response: SuggestionApiService.BoredResult){
        home_number.text = "NAN"
        home_number_desc.text = response.description
    }


    override fun onPause() {
        super.onPause()
        connection?.dispose()
    }
}