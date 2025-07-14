package com.irisaco.iotequipment.ui.detail

import dagger.assisted.AssistedFactory

@AssistedFactory
interface DetailViewModelAssistedFactory {
    fun create(equipmentId: Int): DetailViewModel
}
