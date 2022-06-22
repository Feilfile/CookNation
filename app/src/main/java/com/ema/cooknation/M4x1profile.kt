package com.ema.cooknation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ema.cooknation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [M4x1profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class M4x1profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        mAuth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_m4_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment m4_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            M4x1profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSignOut = getView()?.findViewById<Button>(R.id.btnSignOut)
        val btnProfileUpload = getView()?.findViewById<Button>(R.id.btnProfileUpload)
        val tvProfileName = getView()?.findViewById<TextView>(R.id.tvProfileName)
        val ibButtonImpressum = getView()?.findViewById<ImageButton>(R.id.ibButtonImpressum)

        val db = Firebase.firestore
        //dc.document.toObject((Recipe::class.java))
        db.collection("user")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()
                tvProfileName?.text = user?.username
            }

        // Button to go to WebView
        ibButtonImpressum?.setOnClickListener {
            (activity as MainActivity).setContentView(R.layout.web_view)
        }

        btnProfileUpload?.setOnClickListener{
            (activity as MainActivity).openUploadFragment()
        }
        // Setup SignOut Button when Loading Fragment
        btnSignOut?.setOnClickListener{
            mAuth.signOut()
            (activity as MainActivity).performLogout()
        }
        super.onViewCreated(view, savedInstanceState)
    }


}