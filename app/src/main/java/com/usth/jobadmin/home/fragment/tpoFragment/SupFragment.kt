package com.usth.jobadmin.home.fragment.supFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.usth.jobadmin.databinding.BottomSheetDeleteSupBinding
import com.usth.jobadmin.databinding.FragmentSupBinding
import com.usth.jobadmin.home.fragment.supFragment.adapter.SupAdapter
import com.usth.jobadmin.home.fragment.supFragment.viewmodel.SupViewModel
import com.usth.jobadmin.model.Sup
import com.google.android.material.bottomsheet.BottomSheetDialog


class SupFragment : Fragment() {
    private var _binding: FragmentSupBinding? = null
    private val binding get() = _binding!!
    private var _supAdapter: SupAdapter? = null
    private val supAdapter get() = _supAdapter!!
    private val supViewModel by viewModels<SupViewModel>()
    private val sups: MutableList<Sup> by lazy { mutableListOf() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupBinding.inflate(inflater, container, false)
        _supAdapter = SupAdapter(this@SupFragment)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        supViewModel.fetchSup()
        with(binding) {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterSup(text)
            }

            rvPlacementOfficer.adapter = supAdapter
            rvPlacementOfficer.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun filterSup(text: Editable?) {
        if (text.isNullOrEmpty().not()) {
            val filteredJobList = sups.filter { sup ->
                val title = sup.username.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            supAdapter.setData(newSup = filteredJobList)
        } else {
            supAdapter.setData(newSup = sups)
        }
    }

    private fun setupObserver() {
        supViewModel.supList.observe(viewLifecycleOwner) { supList ->
            supAdapter.setData(supList)
            sups.clear()
            sups.addAll(supList)
        }
    }

    fun deleteSup(sup: Sup) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val supDeleteSheetBinding = BottomSheetDeleteSupBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(supDeleteSheetBinding.root)
        with(supDeleteSheetBinding) {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveSup.setOnClickListener {
                supViewModel.deleteSup(sup)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    fun navigateToSupView(sup: Sup) {
        val directions = SupFragmentDirections.actionSupFragmentToSupViewFragment(sup)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        _binding = null
        _supAdapter = null
        super.onDestroyView()
    }

}