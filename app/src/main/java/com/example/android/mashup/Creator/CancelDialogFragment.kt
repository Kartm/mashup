package com.example.android.mashup.Creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentCancelDialogBinding



class CancelDialogFragment : DialogFragment() {

    private var _binding: FragmentCancelDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCancelDialogBinding.inflate(inflater, container, false)

        binding.cancelDialogDiscardButton.setOnClickListener {
            findNavController().navigate(R.id.action_cancelDialogFragment_to_FirstFragment2)
        }

        binding.cancelDialogCancelButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}