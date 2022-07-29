package com.ema.cooknation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegisterValidatorTest {

    @Test
    fun `email cannot be empty` () {
        val result = RegisterValidator.validateInputs(null,
            "",
            "newname123",
            "test1234"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password must have at least 8 digits` () {
        val result = RegisterValidator.validateInputs(null,
            "test123@gmail.com",
            "newname123",
            "test123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password must have at least 1 letter` () {
        val result = RegisterValidator.validateInputs(null,
            "test123@gmail.com",
            "newname123",
            "12345678"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password must have at least 1 number` () {
        val result = RegisterValidator.validateInputs(null,
            "test123@gmail.com",
            "newname123",
            "testtest"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `username must have at least 3 digits` () {
        val result = RegisterValidator.validateInputs(null,
            "test123@gmail.com",
            "pq",
            "testtest"
        )
        assertThat(result).isFalse()
    }
}