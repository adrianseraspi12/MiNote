package com.suzei.minote

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

open class BaseUnitTest {

    inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)
    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
    fun <T : Any> safeEq(value: T): T = eq(value) ?: value
    protected fun <T> safeAny(type: Class<T>): T = Mockito.any<T>(type)


}