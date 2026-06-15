import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportCountryItem
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun PassportCountryScreen(
    countries: List<PassportCountry>,
    selectedType: DocumentType,
    onTypeClick: (DocumentType) -> Unit,
    onBackClick: () -> Unit,
    onCountryClick: (PassportCountry, DocumentType) -> Unit,
    onSearchChange: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(PrimaryColor, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    tint = WhiteColor
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = stringResource(R.string.select_document_type),
                fontFamily = SfProDisplayBold,
                fontSize = 20.sp,
                color = TextColor
            )
        }

        Spacer(Modifier.height(28.dp))

        TextField(
            value = search,
            onValueChange = {
                search = it
                onSearchChange(it)
            },
            placeholder = {
                Text(text = stringResource(R.string.search_hint_passport))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = WhiteColor,
                unfocusedContainerColor = WhiteColor,
                disabledContainerColor = WhiteColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        )

        Spacer(Modifier.height(20.dp))

        val tabs = listOf(
            DocumentType.ALL to R.string.all,
            DocumentType.PASSPORT to R.string.passport,
            DocumentType.VISA to R.string.visa,
            DocumentType.STANDARD to R.string.standard
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tabs.forEach { item ->
                val type = item.first
                val title = item.second
                val selected = selectedType == type

                Text(
                    text = stringResource(title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(30.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = if (selected) PrimaryColor else WhiteColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onTypeClick(type) }

                        .padding(horizontal = (16.5).dp, vertical = 3.dp),

                    color = if (selected) WhiteColor else TextColor,
                    fontFamily = SfProDisplayBold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = countries,
                key = { it.id }
            ) { country ->
                PassportCountryItem(
                    country = country,
                    selectedType = selectedType,
                    onClick = {
                        onCountryClick(country, selectedType)
                    }
                )
            }
        }
    }
}