package com.test.tabscontentloadingtest.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.test.tabscontentloadingtest.R
import com.test.tabscontentloadingtest.domain.Constants.LOG_PREFIX
import com.test.tabscontentloadingtest.domain.entity.GetTextResult

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private val sectionNumber: Int
        get() = requireNotNull(arguments?.getInt(ARG_SECTION_NUMBER)) { "No section number provided" }

    private lateinit var pageViewModel: PageViewModel

    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "Instance created: #$sectionNumber")
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            pageNumber = sectionNumber
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false).apply {
        textView = findViewById(R.id.section_label)
        progressBar = findViewById(R.id.section_progress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pageViewModel.getTextResult.observe(this, Observer<GetTextResult> {
            textView.text = it.toString()
        })
        pageViewModel.isLoadingInProgress.observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "#$sectionNumber: onStart()")
        pageViewModel.requestContentLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "Instance destroyed: #$sectionNumber")
    }

    fun onSelected() {
        Log.d(LOG_TAG, "#$sectionNumber: onSelected()")
        pageViewModel.requestContentLoading()
    }

    companion object {
        private const val LOG_TAG = "$LOG_PREFIX PlaceholderFragment"

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment = PlaceholderFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, sectionNumber)
            }
        }
    }
}
