package dev.redukt.data

import dev.redukt.data.resolver.DataSourceResolver
import dev.redukt.data.resolver.MissingDataSourceException
import dev.redukt.test.assertions.*
import dev.redukt.test.middleware.testJobAction
import dev.redukt.test.middleware.testJobActionIn
import dev.redukt.test.middleware.tester
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DataSourceMiddlewareTest {

    private object IntToStringDataSource : DataSourceKey<Int, String>

    private val tester = dataSourceMiddleware.tester(Unit)

    @Test
    fun shouldEmitStartOnDataSourceCallStart() = runTest {
        val resolver = DataSourceResolver {
            IntToStringDataSource resolveBy { DataSourceMock(Int::toString) }
        }
        tester.test(closure = resolver) {
            testJobAction(DataSourceCall(IntToStringDataSource, 3))
            assertActionEquals(DataSourceAction(IntToStringDataSource, DataSourcePayload.Started(3)))
            skipOneAction()
        }
    }

    @Test
    fun shouldEmitSuccessOnDataSourceCallWhenDataSourceProperlyCompletes() = runTest {
        val resolver = DataSourceResolver {
            IntToStringDataSource resolveBy { DataSourceMock(Int::toString) }
        }
        tester.test(closure = resolver) {
            testJobAction(DataSourceCall(IntToStringDataSource, 3))
            skipOneAction()
            assertActionEquals(DataSourceAction(IntToStringDataSource, DataSourcePayload.Success(3, "3")))
        }
    }

    @Test
    fun shouldEmitFailureOnDataSourceCallWheDataSourceFails() = runTest {
        val exception = Exception("Operation failed!")
        val resolver = DataSourceResolver {
            IntToStringDataSource resolveBy { DataSourceMock { throw exception }}
        }
        tester.test(closure = resolver) {
            testJobAction(DataSourceCall(IntToStringDataSource, 3))
            skipOneAction()
            assertActionEquals(DataSourceAction(IntToStringDataSource, DataSourcePayload.Failure(3, exception)))
        }
    }

    @Test
    fun shouldNotPassDataSourceCallForward() = runTest {
        val resolver = DataSourceResolver {
            IntToStringDataSource resolveBy { DataSourceMock(Int::toString) }
        }
        tester.test(closure = resolver, strict = false) {
            testJobAction(DataSourceCall(IntToStringDataSource, 3))
            verifyNext { assertNoActions() }
        }
    }

    @Test
    fun shouldResultInFailureWhenCancellationExceptionThrown() {
        val resolver = DataSourceResolver {
            IntToStringDataSource resolveBy { DataSourceMock { awaitCancellation() } }
        }
        tester.test(closure = resolver) {
            val testScope = TestScope()
            val job = testJobActionIn(testScope, DataSourceCall(IntToStringDataSource, 3))
            testScope.runCurrent()
            job.cancel()
            testScope.runCurrent()
            skipOneAction()
            assertActionOfType<DataSourceAction<Int, String>> {
                val payload = it.payload.shouldBeInstanceOf<DataSourcePayload.Failure<Int, String>>()
                payload.error.shouldBeInstanceOf<CancellationException>()
            }
        }
    }

    @Test
    fun shouldFailWhenNoDataSourceWithGivenKey() = runTest {
        shouldThrow<MissingDataSourceException> {
            tester.test(closure = DataSourceResolver { }) {
                testJobAction(DataSourceCall(IntToStringDataSource, 3))
            }
        }
    }

    @Test
    fun shouldThrowOnInitWhenNoDataSourceResolver() {
        shouldThrow<IllegalStateException> {
            tester.test { }
        }
    }
}