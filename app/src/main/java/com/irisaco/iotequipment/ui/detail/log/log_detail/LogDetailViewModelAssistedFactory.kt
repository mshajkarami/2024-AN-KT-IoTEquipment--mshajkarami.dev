package com.irisaco.iotequipment.ui.detail.log.log_detail

import dagger.assisted.AssistedFactory
@AssistedFactory
interface LogDetailViewModelAssistedFactory {
    fun create(equipmentId: Int): LogDetailViewModel
}