package android.saied.com.backend.model

import android.saied.com.backend.di.jsonModule
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest

internal class OmdbSearchTest : KoinTest {
    @Test
    fun `is parsed correctly`() {
        startKoin(listOf(jsonModule))
        val content = javaClass.classLoader.getResource("searchResult.json").readText()
        val objectMapper: ObjectMapper = get()
        val res = objectMapper.readValue(content, OmdbSearch::class.java)

        assertEquals(10, res.search.size)
        assertEquals(SearchResType.MOVIE, res.search[0].type)
        assertEquals(SearchResType.SERIES, res.search[1].type)
        stopKoin()
    }
}