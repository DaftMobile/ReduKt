package dev.redukt.data.resolver

import dev.redukt.data.DataSourceKey
import dev.redukt.data.DataSourceMock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.Test

internal class TypeSafeDataSourceResolverTest {

    private object IntToString : DataSourceKey<Int, String>
    private object FloatToString : DataSourceKey<Float, String>
    private object DoubleToString : DataSourceKey<Double, String>

    private object AltFloatToString : DataSourceKey<Float, String>

    private val intToStringSource = DataSourceMock(Int::toString)
    private val floatToStringSource = DataSourceMock(Float::toString)
    private val altFloatToStringSource = DataSourceMock(Float::toString)
    private val doubleToStringSource = DataSourceMock(Double::toString)


    @Test
    fun shouldFailToResolveMissingDataSource() {
        val resolver = TypeSafeDataSourceResolver { }
        shouldThrow<MissingDataSourceException> { resolver.resolve(IntToString) }
    }

    @Test
    fun shouldReturnProperlyDataSourcesWhenMultipleKeys() {
        val resolver = TypeSafeDataSourceResolver {
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
        val resolver = TypeSafeDataSourceResolver {
            FloatToString resolveBy { floatToStringSource }
            AltFloatToString resolveBy { altFloatToStringSource }
        }
        resolver.resolve(FloatToString) shouldBe floatToStringSource
        resolver.resolve(AltFloatToString) shouldBe altFloatToStringSource
    }

    @Test
    fun shouldCallProviderOnEachResolve() {
        val resolver = TypeSafeDataSourceResolver {
            IntToString resolveBy { DataSourceMock(Int::toString) }
        }
        resolver.resolve(IntToString) shouldNotBeSameInstanceAs resolver.resolve(IntToString)
    }
}