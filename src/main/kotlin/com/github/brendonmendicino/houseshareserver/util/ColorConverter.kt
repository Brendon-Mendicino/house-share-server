package com.github.brendonmendicino.houseshareserver.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import org.springframework.boot.ansi.AnsiColor
import org.springframework.boot.ansi.AnsiElement
import org.springframework.boot.ansi.AnsiOutput
import org.springframework.boot.ansi.AnsiStyle

@Suppress("unused")
/**
 * Logback [CompositeConverter] to color output using the [AnsiOutput] class.
 * A single 'color' option can be provided to the converter, or if not specified color
 * will be picked based on the logging level.
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
class ColorConverter : CompositeConverter<ILoggingEvent?>() {
    override fun transform(event: ILoggingEvent?, `in`: String?): String {
        val color = ELEMENTS[firstOption]
            ?: LEVELS[event!!.level.toInteger()]
            ?: AnsiColor.WHITE

        return toAnsiString(`in`, color)
    }

    fun toAnsiString(`in`: String?, element: AnsiElement): String {
        return AnsiOutput.toString(element, `in`)
    }

    companion object {
        private val ELEMENTS: Map<String, AnsiElement>

        init {
            val ansiElements = AnsiColor.entries
                .filter { color -> color != AnsiColor.DEFAULT }
                .associate { color -> color.name.lowercase() to color as AnsiElement }
                .toMutableMap()

            ansiElements["faint"] = AnsiStyle.FAINT

            ELEMENTS = ansiElements.toMap()
        }

        private val LEVELS: Map<Int, AnsiElement> = mapOf(
            Level.ERROR_INTEGER to AnsiColor.RED,
            Level.WARN_INTEGER to AnsiColor.YELLOW,
            Level.INFO_INTEGER to AnsiColor.GREEN,
            Level.DEBUG_INTEGER to AnsiColor.WHITE,
        )

        fun getName(element: AnsiElement?): String {
            return ELEMENTS.asSequence()
                .firstOrNull { it.value == element }
                ?.key
                ?: throw NoSuchElementException("Element $element not found")
        }
    }
}

