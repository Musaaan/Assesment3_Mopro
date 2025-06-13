package com.musaan0129.assesment3.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.musaan0129.assesment3.R
import com.musaan0129.assesment3.model.Desain

@Composable
fun DeleteDialog(
    desain: Desain,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.hapus_hewan_title))
        },
        text = {
            Text(text = stringResource(R.string.hapus_hewan_body, desain.nama))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.hapus))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.batal))
            }
        }
    )
}