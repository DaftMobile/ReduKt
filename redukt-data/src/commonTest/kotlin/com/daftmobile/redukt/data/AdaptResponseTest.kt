package com.daftmobile.redukt.data

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AdaptResponseTest {

    @Test
    fun shouldReturnDataSourceThatTransformsResponseProperly() = runTest {
        val ds = DataSourceMock(String::toFloat)
            .adaptResponse(Float::toInt)
        ds.call("1.0") shouldBe 1
        ds.call("2.2") shouldBe 2
    }
}
