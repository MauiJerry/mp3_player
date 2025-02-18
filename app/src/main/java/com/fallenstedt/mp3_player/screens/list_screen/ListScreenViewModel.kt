package com.fallenstedt.mp3_player.screens.list_screen

import androidx.lifecycle.ViewModel
import com.fallenstedt.mp3_player.services.FileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListScreenViewModel: ViewModel() {
  private val fileService = FileService()

  private val _uiState = MutableStateFlow(ListScreenUIState())
  val uiState: StateFlow<ListScreenUIState> = _uiState.asStateFlow()

  fun updateListWithFiles(path: String = "") {
    
  }

  fun updateListItems(listItems: List<ListScreenListItem>) {
    _uiState.update { currentState ->
      currentState.copy(
        listItems = listItems
      )
    }
  }


}