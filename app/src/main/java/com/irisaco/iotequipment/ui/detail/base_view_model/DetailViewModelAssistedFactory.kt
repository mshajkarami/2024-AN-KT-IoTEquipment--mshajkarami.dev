package com.irisaco.iotequipment.ui.detail.base_view_model

import dagger.assisted.AssistedFactory

@AssistedFactory
interface DetailViewModelAssistedFactory {
    fun create(equipmentId: Int): DetailViewModel
}