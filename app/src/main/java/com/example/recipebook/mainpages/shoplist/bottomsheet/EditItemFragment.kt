package com.example.recipebook.mainpages.shoplist.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentEditItemBinding
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditItemFragment(private val viewModel: ShopItemViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        //принятие данных
        val bundle = arguments
        val itemdata = bundle?.getSerializable("item") as ShopListEntity
        val itemID = itemdata.id
        val itemName = itemdata.name
        val itemChecked = itemdata.checked
        binding.apply {
            edItemListName.setText(itemName)
        }

        binding.btnSaveItemList.setOnClickListener {
            var newItemName = binding.edItemListName.text.toString()
            if (newItemName.isNotEmpty()) {
                viewModel.update(
                    ShopListEntity(
                        id = itemID,
                        name = newItemName,
                        checked = itemChecked
                    )
                ) {}
                dismiss()
            } else {
                Toast.makeText(requireContext(), R.string.error_empty, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.itemDelete.setOnClickListener {
            viewModel.delete(
                ShopListEntity(
                    id = itemdata.id,
                    name = itemdata.name,
                    checked = itemdata.checked
                )
            ) {}
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditItemFragment(ShopItemViewModel())
        const val EDIT_TAG = "EditItemFragment"
    }
}