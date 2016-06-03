package com.jdarb.testresult.spockextension.listeners

import com.jdarb.testresult.model.State
import com.jdarb.testresult.model.Test
import org.spockframework.runtime.IRunListener
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import java.time.Instant

//TODO: Flesh it out!
class SpecInfoListener(val callback: (Test) -> Unit) : IRunListener {
    override fun afterIteration(iteration: IterationInfo?) {

    }

    override fun beforeFeature(feature: FeatureInfo?) {

    }

    override fun afterSpec(spec: SpecInfo?) {

    }

    override fun error(error: ErrorInfo?) {

    }

    override fun afterFeature(feature: FeatureInfo?) {

    }

    override fun beforeSpec(spec: SpecInfo?) {
        val test = Test(
                description = "a description",
                startTime = Instant.now(),
                endTime = Instant.now(),
                name = spec!!.name,
                status = State.PASS
        )
        callback(test)
    }

    override fun beforeIteration(iteration: IterationInfo?) {

    }

    override fun featureSkipped(feature: FeatureInfo?) {

    }

    override fun specSkipped(spec: SpecInfo?) {

    }

}
