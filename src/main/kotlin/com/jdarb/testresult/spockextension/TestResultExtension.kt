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
    lateinit var startTime: Instant
    lateinit var resultFile: File
        private set

    override fun start() {
        startTime = Instant.now()
    }

    override fun visitSpec(spec: SpecInfo) {
        spec.addListener(SpecInfoListener { tests += it })
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
        resultFile = File("$outputDirectory$outputFilename")

        resultFile.writeText(text = runAsJson)

        println("Test result file written to ${resultFile.absolutePath}")
    }

}