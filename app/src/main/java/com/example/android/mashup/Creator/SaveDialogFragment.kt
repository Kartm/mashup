package com.example.android.mashup.Creator

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentSaveDialogBinding
import java.io.File
import java.io.FileOutputStream

class SaveDialogFragment : DialogFragment() {
    private var _binding: FragmentSaveDialogBinding? = null
    private val binding get() = _binding!!

    private val args: SaveDialogFragmentArgs by navArgs()

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
            // todo copy


            if (args.resultPath != null) {
                val dir1 = File(
                    requireContext().applicationInfo.dataDir, "results"
                )
                if (!dir1.exists()) {
                    dir1.mkdirs()
                }

                val file = File(dir1, "${binding.inputTitle.text.toString()}.mp4")

                if (file.exists()) {
                    file.delete()
                    file.createNewFile()
                } else {
                    file.createNewFile()
                }

                val originalBytes = (requireContext().contentResolver.openInputStream(Uri.parse(args.resultPath)))?.readBytes()

                val fos = FileOutputStream(file!!.path)
                fos.write(originalBytes)
                fos.close()

                Log.v("[me]", args.resultPath)
            }

            findNavController().navigate(R.id.action_saveDialogFragment_to_FirstFragment)
        }

        if (args.resultPath != null) {
//            CreatorFragment.videoUri = Uri.parse(args.videoUri);
            val resultUri = Uri.parse(args.resultPath)
            Log.v("[me]", args.resultPath)
        }

        return binding.root
    }
}