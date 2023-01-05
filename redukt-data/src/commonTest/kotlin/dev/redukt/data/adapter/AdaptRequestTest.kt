package dev.redukt.data.adapter

import dev.redukt.data.DataSourceMock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class AdaptRequestTest {

    @Test
    fun shouldReturnDataSourceThatTransformsRequestProperly() = runTest {
        val ds = DataSourceMock(String::reversed)
            .adaptRequest(Float::toString)
        assertEquals("0.1", ds.call(1.0f))
        assertEquals("2.1", ds.call(1.2f))
    }
}