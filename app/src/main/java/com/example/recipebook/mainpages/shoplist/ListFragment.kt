package com.example.recipebook.mainpages.shoplist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.APP
import com.example.recipebook.mainpages.shoplist.database.ShopListDB
import com.example.recipebook.mainpages.shoplist.adapter.ShopListItemAdapter
import com.example.recipebook.databinding.FragmentListBinding
import com.example.recipebook.mainpages.shoplist.bottomsheet.ShopItemViewModel
import com.example.recipebook.mainpages.shoplist.bottomsheet.BottomSheetFragment
import com.example.recipebook.mainpages.shoplist.bottomsheet.EditItemFragment
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private var adapter = ShopListItemAdapter(ShopItemViewModel())
    private lateinit var database: ShopListDB
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        fabClick()
    }

    private fun init(){
        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        viewModel.initDatabase()
        recyclerView = binding.recyclerViewListPage
        adapter = ShopListItemAdapter(ShopItemViewModel())
        recyclerView.adapter = adapter
        viewModel.getAllItems().observe(viewLifecycleOwner) {listShopItems ->
            adapter.setList(listShopItems.asReversed()) //заполнение рв / новые элементы списка будут появляться на верху
            if (recyclerView.adapter?.itemCount != 0 ) {
                binding.lineaLayoutNothingFoundListPage.visibility = View.GONE
            }
            else{
                binding.lineaLayoutNothingFoundListPage.visibility = View.VISIBLE
            }
        }
    }

    private fun fabClick(){
        val bottomSheetFragment = BottomSheetFragment()
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        binding.floatingActionButtonAddListPage.setOnClickListener {
            fragmentManager.let { bottomSheetFragment.show(it, BottomSheetFragment.CREATE_TAG) }
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()

        fun clickItem(shopListEntity: ShopListEntity){
            val bundle = Bundle()
            bundle.putSerializable("item", shopListEntity)
            val editItemFragment = EditItemFragment(ShopItemViewModel())
            val fragmentManager = (APP as FragmentActivity).supportFragmentManager
            editItemFragment.arguments = bundle
            fragmentManager.let { editItemFragment.show(it, EditItemFragment.EDIT_TAG) }
        }
    }
}