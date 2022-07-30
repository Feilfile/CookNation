package com.ema.cooknation

import com.ema.cooknation.validator.RecipeInputValidator
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecipeInputValidatorTest {

    @Test
    fun `title has to have at least 5 digits` () {
        val result = RecipeInputValidator.validateInputs(null,
            "Eis",
        "123456789012345678901234567890",
        "123456789012345678901234567890",
            true
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `directions must have at least 30 digits` () {
        val result = RecipeInputValidator.validateInputs(null,
            "12345",
            "12345678901234567890123456789",
            "123456789012345678901234567890",
            true
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `ingredients must have at least 5 digits` () {
        val result = RecipeInputValidator.validateInputs(null,
            "12345",
            "123456789012345678901234567890",
            "1234",
            true
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `a picture has to be initialized` () {
        val result = RecipeInputValidator.validateInputs(null,
            "12345",
            "123456789012345678901234567890",
            "123456789012345678901234567890",
            false
        )
        assertThat(result).isFalse()
    }
}