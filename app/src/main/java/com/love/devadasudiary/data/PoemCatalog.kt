package com.love.devadasudiary.data

import com.love.devadasudiary.data.model.Poem

/**
 * Static catalog of poems shipped with the app. Kept out of the ViewModel
 * so the UI layer can be tested without needing a full Application context.
 */
object PoemCatalog {

    val poems: List<Poem> = listOf(
        Poem(
            id = "1",
            title = "దేవదాసు డైరీ",
            subtitle = "గత క్షణాల నిశ్శబ్ద జ్ఞాపకాలు",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/41a9e59c134f6586450a3cfecfc42f14/raw/gistfile1.txt"
        ),
        Poem(
            id = "2",
            title = "ప్రేమలేఖ",
            subtitle = "మనసులో దాచుకున్న స్వీకారం",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/80ddbe213135d1dc5c9066273f1df9d4/raw/loveletter.txt"
        ),
        Poem(
            id = "3",
            title = "ఇక నుంచి...",
            subtitle = "మనసును కఠినం చేసుకున్న రోజులు",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/1c0bdf62612a1c765ee2299fcf9191eb/raw/Eternal.txt"
        )
    )

    fun findById(id: String): Poem? = poems.firstOrNull { it.id == id }

    fun firstId(): String = poems.first().id

    fun indexOf(id: String): Int = poems.indexOfFirst { it.id == id }
}
