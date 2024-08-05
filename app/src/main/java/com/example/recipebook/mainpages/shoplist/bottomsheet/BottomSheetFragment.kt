package com.example.recipebook.mainpages.shoplist.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.recipebook.R
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import com.example.recipebook.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]

        /*--------------------------запись данных в БД--------------------------*/

        binding.btnSaveItemList.setOnClickListener {
            val title = binding.edItemListName.text.toString()
            if (title.isNotEmpty()) {
                viewModel.insert(
                    ShopListEntity(
                        null,
                        name = title,
                        false
                    )
                ) {}
                binding.edItemListName.setText("")
                dismiss()

            } else {
                Toast.makeText(requireContext(), R.string.error_empty, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BottomSheetFragment()
        const val CREATE_TAG = "BottomSheetFragment"
    }
}