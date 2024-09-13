package network.ermis.ui.common.utils

import network.ermis.ui.common.helper.StorageHelper.Companion.FILE_NAME_PREFIX

internal object StringUtils {

    fun removeTimePrefix(attachmentName: String?, usedDateFormat: String): String? {
        val dataFormatSize = usedDateFormat.length + 1
        val regex = "${FILE_NAME_PREFIX}\\S{$dataFormatSize}".toRegex()

        return if (attachmentName?.contains(regex) == true) {
            attachmentName.replaceFirst(regex, "")
        } else {
            attachmentName
        }
    }
}
