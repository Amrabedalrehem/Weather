package com.example.presentation.detailsfavourites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.entity.FavouriteLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.flow.stateIn

class DetailsFavoritesViewModel(val repository: Repository, private val locationId: Int) : ViewModel() {

    val itemFavourite: StateFlow<FavouriteLocation?> = repository.getFavouriteById(locationId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}

class DetailsViewModelFactory(private val repository: Repository, private val locationId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsFavoritesViewModel(repository, locationId) as T
    }
}