package com.example.recipebook.mainpages.shoplist.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import com.example.recipebook.R
import com.example.recipebook.databinding.ShopListItemBinding
import com.example.recipebook.mainpages.shoplist.ListFragment
import com.example.recipebook.mainpages.shoplist.bottomsheet.ShopItemViewModel

class ShopListItemAdapter(private val viewModel: ShopItemViewModel): RecyclerView.Adapter<ShopListItemAdapter.ShopListItemViewHolder>() {
    var itemList = emptyList<ShopListEntity>()
    class ShopListItemViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ShopListItemBinding.bind(item)
        fun bind(shopListEntity: ShopListEntity) = with(binding){
            textViewItemTitle.text = shopListEntity.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_list_item, parent, false) // указываем какой layout нужно использовать
        return ShopListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ShopListItemViewHolder, position: Int) {
        holder.bind(itemList[position])

        holder.binding.apply {

            textViewItemTitle.text = itemList[position].name // задаем значение title у item'a из списка с позиции

            //проверка на галочку
            if (itemList[position].checked == true){
                checkBoxShopListItem.isChecked = true
                textViewItemTitle.paintFlags = checkBoxShopListItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else{
                checkBoxShopListItem.isChecked = false
                textViewItemTitle.paintFlags = checkBoxShopListItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            //нажатие на галочку
            var itemChecked = false
            checkBoxShopListItem.setOnClickListener{
                if (checkBoxShopListItem.isChecked){
                    textViewItemTitle.paintFlags = checkBoxShopListItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    val itemID = itemList[position].id
                    val itemName = itemList[position].name
                    itemChecked = true
                    viewModel.update(ShopListEntity(id = itemID, name = itemName, checked = itemChecked)){}

                }
                else{
                    textViewItemTitle.paintFlags = checkBoxShopListItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    val itemID = itemList[position].id
                    val itemName = itemList[position].name
                    itemChecked = false
                    viewModel.update(ShopListEntity(id = itemID, name = itemName, checked = itemChecked)){}
                }
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ShopListEntity>){
        itemList = list
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: ShopListItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            ListFragment.clickItem(itemList[holder.adapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: ShopListItemViewHolder) {
        holder.itemView.setOnClickListener(null)
    }
}