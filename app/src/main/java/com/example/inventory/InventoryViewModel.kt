//Auteur : SILVANA ESQUIVEL
//Date : 15.03.2023
/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory

import androidx.lifecycle.*
import androidx.room.util.copy
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch



/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems:LiveData<List<Item>> = itemDao.getItems().asLiveData()
    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    //fonction pour récupérer les détals des éléments de la bdd en fonction de l'élément id
    fun retrieveItem(id : Int): LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }

    //FONCTON QUI Reçoit une instance de la classe d'entité item
    private fun updateItem(item :Item){
        //appel de la méthode
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    //fonction qui reçoit une instance de la classe d'entités item
    fun sellItem(item: Item){
        //condition pour vérifier si la valeur est sup...
        if (item.quantityInStock>0){
            val  newItem = item.copy(quantityInStock =  item.quantityInStock -1)
            updateItem(newItem)
        }
    }
    //
    fun isStockAvailable(item: Item): Boolean{
        return item.quantityInStock >0
    }

    //fonction supprimer
    fun deleteItem(item: Item){
        viewModelScope.launch{
            itemDao.delete(item)
        }
    }

    //fonction ppur mettre à jour la modification
    private fun getUpdateitemEntry(
        itemId : Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item{
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }
    fun updateItem(
        itemId :Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ){
        val updateItem = getUpdateitemEntry(itemId,itemName,itemPrice,itemCount)
        updateItem(updateItem)
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
