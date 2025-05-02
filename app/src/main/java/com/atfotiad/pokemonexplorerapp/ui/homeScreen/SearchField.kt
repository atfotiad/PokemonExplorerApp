package com.atfotiad.pokemonexplorerapp.ui.homeScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction

@Composable
/**
 *  [SearchField] is a composable function that displays a search field.
 *  @param text is a string that represents the current text in the search field
 *  @param onTextChange is a lambda function that is called when the text in the search field changes
 *  @param onTrailingIconClick is a lambda function that is called when the trailing icon is clicked
 *  @param onSearch is a lambda function that is called when the search button is clicked
 *  @param modifier is an instance of [Modifier]
 *  @param onFocusChanged is a lambda function that is called when the focus state of the search field changes
 * */
fun SearchField(
    text: String,
    onTextChange: (String) -> Unit,
    onTrailingIconClick: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: (FocusState) -> Unit
) {

    OutlinedTextField(
        value = text,
        onValueChange = { onTextChange(it) },
        modifier = modifier
            .onFocusChanged(onFocusChanged),
        shape = RoundedCornerShape(percent = 50),
        placeholder = { Text(text = "Search") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = {
                onTrailingIconClick()
            }) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        )
    )
}