package com.example.kumandra.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kumandra.R
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.FragmentProfileBinding
import com.example.kumandra.viewmodel.MainViewModel
import com.example.kumandra.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireContext().dataStore
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), UserSession.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getUser().observe(viewLifecycleOwner) {
            binding.tvUsernameProfil.text = it.username
            binding.tvEmailProfil.text = it.email
        }


        binding.btLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("CONFIRMATION")
                setMessage("Logout of your account?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.logout()
                    requireActivity().finish()
                }
                setNegativeButton("No") { dialog, _ -> dialog.cancel() }
                create()
                show()
            }
        }
    }


}