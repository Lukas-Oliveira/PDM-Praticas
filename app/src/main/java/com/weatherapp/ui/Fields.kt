package com.weatherapp.ui

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun DataField(
    value: String,
    onChange: (newValue: String) -> Unit,
    label: String,
    modifier: Modifier
) {

    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        onValueChange = onChange,
        modifier = modifier
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (newValue: String) -> Unit,
    label: String,
    modifier: Modifier
) {

    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        onValueChange = onChange,
        modifier = modifier,
        visualTransformation = PasswordVisualTransformation()
    )
}
