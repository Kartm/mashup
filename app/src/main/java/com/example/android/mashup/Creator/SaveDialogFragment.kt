package com.example.android.mashup.Creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentSaveDialogBinding

class SaveDialogFragment : DialogFragment() {
    private var _binding: FragmentSaveDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveDialogBinding.inflate(inflater, container, false)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener{
            findNavController().navigate(R.id.action_saveDialogFragment_to_FirstFragment)
        }

        return binding.root
    }
}