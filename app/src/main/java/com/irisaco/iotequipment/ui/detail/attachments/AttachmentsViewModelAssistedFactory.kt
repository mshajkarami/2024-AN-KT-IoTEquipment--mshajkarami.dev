package com.irisaco.iotequipment.ui.detail.attachments

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface AttachmentsViewModelAssistedFactory {
    fun create(
        @Assisted("equipmentId") equipmentId: Int?,
        @Assisted("recordId") recordId: Int?
    ): AttachmentsViewModel
}
