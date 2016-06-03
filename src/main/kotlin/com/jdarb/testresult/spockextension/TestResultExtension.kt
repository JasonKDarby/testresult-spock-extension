package com.jdarb.testresult.spockextension

import com.jdarb.testresult.model.Run
import com.jdarb.testresult.model.Test
import com.jdarb.testresult.spockextension.listeners.SpecInfoListener
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo
import java.io.File
import java.time.Instant

class TestResultExtension : IGlobalExtension {

    var tests = emptyList<Test>()
    //I don't actually know if there's an appreciable difference in setting startTime here or in start().
    //Also, I don't really like initializing it like this.
    var startTime: Instant = Instant.EPOCH

    override fun start() {
        startTime = Instant.now()
    }

    override fun visitSpec(spec: SpecInfo) {
        spec.addListener(SpecInfoListener { tests = tests.plus(it) })
    }

    override fun stop() {
        val run = Run(
                name = "a name",
                startTime = startTime,
                endTime = Instant.now(),
                tests = tests
        )
        val runAsJson = run.toJsonString()

        val outputDirectory = System.getProperty(
                "testresultextension.output.directory",
                "build/test-results/"
        )
        val outputFilename = System.getProperty(
                "testresultextension.output.filename",
                "${run.id}_${Instant.now().toString()}.json"
        )
        val file = File("$outputDirectory$outputFilename")

        file.writeText(text = runAsJson)

        println("Test result file written to ${file.absolutePath}")
    }

}