package com.stephen.aiassistant.data.uistate

import com.stephen.aiassistant.data.bean.Food


data class CalorieCalState(
    val foodList: List<Food>? = null,
    val loading: Boolean = false,
    val errorMessage: String = "",
) {
    fun toUiState() = CalorieCalState(foodList, loading, errorMessage)
}
