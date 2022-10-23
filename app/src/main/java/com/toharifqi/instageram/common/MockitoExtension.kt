package com.toharifqi.instageram.common

import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito
import org.mockito.verification.VerificationMode

infix fun <T> T.returns(stubbing: T) {
    whenever(this).thenReturn(stubbing)
}

fun <T> T.verify(count: Int = 1): T = Mockito.verify(this, Mockito.times(count))

fun <T> T.verify(verificationMode: VerificationMode): T =
    Mockito.verify(this, verificationMode)
