package com.daftmobile.redukt.data.resolver

import com.daftmobile.redukt.data.DataSourceKey
import com.daftmobile.redukt.data.DataSourceMock
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.Test

internal class DataSourceResolverTest {

    private object IntToString : DataSourceKey<Int, String>
    private object FloatToString : DataSourceKey<Float, String>
    private object DoubleToString : DataSourceKey<Double, String>

    private object AltFloatToString : DataSourceKey<Float, String>

    private val intToStringSource = DataSourceMock(Int::toString)
    private val floatToStringSource = DataSourceMock(Float::toString)
    private val altFloatToStringSource = DataSourceMock(Float::toString)
    private val doubleToStringSource = DataSourceMock(Double::toString)

    @Test
    fun shouldReturnProperlyDataSourcesWhenMultipleKeys() {
        val resolver = DataSourceResolver {
            IntToString resolveBy { intToStringSource }
            FloatToString resolveBy { floatToStringSource }
            DoubleToString resolveBy { doubleToStringSource }
        }
        resolver.resolve(IntToString) shouldBe intToStringSource
        resolver.resolve(FloatToString) shouldBe floatToStringSource
        resolver.resolve(DoubleToString) shouldBe doubleToStringSource
    }

    @Test
    fun shouldReturnDataSourcesProperlyWhenMultipleDataSourcesWithTheSameSignature() {
        val resolver = DataSourceResolver {
            FloatToString resolveBy { floatToStringSource }
            AltFloatToString resolveBy { altFloatToStringSource }
        }
        resolver.resolve(FloatToString) shouldBe floatToStringSource
        resolver.resolve(AltFloatToString) shouldBe altFloatToStringSource
    }

    @Test
    fun shouldCallProviderOnEachResolve() {
        val resolver = DataSourceResolver {
            IntToString resolveBy { DataSourceMock(Int::toString) }
        }
        resolver.resolve(IntToString) shouldNotBeSameInstanceAs resolver.resolve(IntToString)
    }
}