package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItem: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    fun retrieveItem(id: Int): LiveData<Item>{
            return itemDao.getItem(id).asLiveData()

    }

    private fun updateItem(item: Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun updateItem(itemId: Int, itemName: String,
                   itemPrice: String, itemCount: String){
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item{
        return Item(itemId, itemName, itemPrice.toDouble(), itemCount.toInt())
    }

    fun sellItem(item: Item){
        if (item.quantityInStock > 0){
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    private fun insertItem(item: Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }

    }
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item{
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean{
        if(itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank())
            return false
        return true
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String){
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    fun isStockAvailable(item: Item): Boolean{
        return item.quantityInStock > 0

    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if(modelClass.isAssignableFrom(InventoryViewModel::class.java)){
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }


}