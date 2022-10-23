package com.toharifqi.instageram.common

import org.junit.Assert

infix fun <T : Any> T?.shouldBe(expectation: T?) {
    Assert.assertEquals(expectation, this)
}

fun Any?.notNull() {
    Assert.assertNotNull(this)
}
