package com.usth.jobadmin.home.fragment.supFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.usth.jobadmin.databinding.FragmentSupViewBinding


class SupViewFragment : Fragment() {
    private var _binding: FragmentSupViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SupViewFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupViewBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            val sup = args.sup
            tvUsername.text = sup.username
            tvUserEmail.text = sup.email
            profileImage.load(sup.imageUri)
            tvMobile.text = sup.mobile
            tvGender.text = sup.gender
            tvQualification.text = sup.qualification
            tvStream.text = sup.stream
            tvDob.text = sup.dob
            tvExperience.text = sup.experience
            tvBio.text = sup.biography
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}