package com.daftmobile.redukt.data.adapter

import com.daftmobile.redukt.data.DataSourceMock
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AdaptRequestTest {

    @Test
    fun shouldReturnDataSourceThatTransformsRequestProperly() = runTest {
        val ds = DataSourceMock(String::reversed)
            .adaptRequest(Float::toString)
        ds.call(1.2f) shouldBe "2.1"
        ds.call(1.3f) shouldBe "3.1"
    }
}