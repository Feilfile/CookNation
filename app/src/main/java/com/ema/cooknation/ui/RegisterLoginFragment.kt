package com.ema.cooknation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ema.cooknation.R

class RegisterLoginFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogin = getView()?.findViewById<Button>(R.id.btnLoginWelcomeScreen)
        val btnRegister = getView()?.findViewById<Button>(R.id.btnRegisterWelcomeScreen)

        btnLogin?.setOnClickListener{
            (activity as MainActivity).openLoginActivity()
        }

        btnRegister?.setOnClickListener{
            (activity as MainActivity).openRegisterActivity()
        }

    }

    companion object {
        fun newInstance() = RegisterLoginFragment().apply {}
    }
}