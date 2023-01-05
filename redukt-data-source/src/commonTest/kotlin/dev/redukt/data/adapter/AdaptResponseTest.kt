package dev.redukt.data.adapter

import dev.redukt.data.DataSourceMock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class AdaptResponseTest {

    @Test
    fun shouldReturnDataSourceThatTransformsResponseProperly() = runTest {
        val ds = DataSourceMock(String::toFloat)
            .adaptResponse(Float::toInt)
        assertEquals(1, ds.call("1.0"))
        assertEquals(2, ds.call("2.2"))
    }
}