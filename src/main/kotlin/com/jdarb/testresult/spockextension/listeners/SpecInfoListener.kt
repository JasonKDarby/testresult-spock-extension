package com.jdarb.testresult.spockextension.listeners

import com.jdarb.testresult.model.*
import org.spockframework.runtime.IRunListener
import org.spockframework.runtime.model.*
import java.time.Instant

//TODO: Flesh it out!
class SpecInfoListener(val callback: (Test) -> Unit) : IRunListener {

    private var isFeatureInProgress = false
    private lateinit var featureInProgress: Test
    private lateinit var specInProgress: Test

    override fun beforeSpec(spec: SpecInfo?) {
        specInProgress = Test(
                name = spec?.description?.displayName ?: NO_NAME_PROVIDED,
                startTime = Instant.now(),
                endTime = Instant.EPOCH,
                state = State.STARTED,
                description = NO_DESCRIPTION_PROVIDED
        )
    }

    override fun beforeFeature(feature: FeatureInfo?) {
        checkIsFeatureInProgress(isFeatureInProgress)
        featureInProgress = Test(
                name = feature?.name ?: NO_NAME_PROVIDED,
                state = State.STARTED,
                description = feature?.toDescription() ?: NO_DESCRIPTION_PROVIDED,
                startTime = Instant.now(),
                endTime = Instant.EPOCH
        )
        isFeatureInProgress = true
    }

    override fun beforeIteration(iteration: IterationInfo?) {
        //Are we sure that this doesn't fire for every feature?
        //I'm not sure if this should be a child test of a feature or a message
    }

    override fun afterIteration(iteration: IterationInfo?) {
        //Are we sure that this doesn't fire for every feature?
        //I'm not sure if this should be a child test of a feature or a message
    }

    override fun afterFeature(feature: FeatureInfo?) {
        //write child test to spec
        featureInProgress = featureInProgress.copy(
                endTime = Instant.now(),
                state =
                if (feature?.isSkipped ?: false) State.SKIP
                else
                    if (featureInProgress.state != State.STARTED) featureInProgress.getStateBasedOnChildren()
                    else State.PASS
        )
        specInProgress = specInProgress.copy(children = specInProgress.children + featureInProgress)
        isFeatureInProgress = false
    }

    override fun afterSpec(spec: SpecInfo?) {
        specInProgress = specInProgress.copy(
                endTime = Instant.now(),
                state =
                if (spec?.isSkipped ?: false) State.SKIP
                else
                    if (specInProgress.state != State.STARTED) specInProgress.getStateBasedOnChildren()
                    else State.PASS
        )
        callback(specInProgress)
    }


    override fun error(error: ErrorInfo?) =
        if (isFeatureInProgress) featureInProgress = featureInProgress.copyWithErrorInfo(error)
        else specInProgress = specInProgress.copyWithErrorInfo(error)

    //Spock is considering removing featureSkipped and specSkipped because it's flagged on featureInfo/specInfo anyway
    //so I'm not implementing them.  We'll use the flag.
    override fun featureSkipped(feature: FeatureInfo?) {
        //Not implemented
    }

    override fun specSkipped(spec: SpecInfo?) {
        //Not implemented
    }

    private fun checkIsFeatureInProgress(isFeatureInProgress: Boolean): Unit =
            if (isFeatureInProgress) throw Error("""The day has come!
        A feature is running concurrently with another feature within the same specification!
        If this is true please file a request to enhance this library.
        If this is false please file a bug report.""") else Unit

}

internal const val NO_NAME_PROVIDED = "no name provided"

internal const val NO_DESCRIPTION_PROVIDED = "no description provided"

internal const val NO_BLOCK_KIND_AVAILABLE = "no block kind available"

internal const val NO_BLOCK_LABEL_AVAILABLE = "no block label available"

internal fun FeatureInfo.toDescription(): String =
        blocks.filterNotNull().map(BlockInfo::formattedString).joinToString() + "."

internal fun BlockInfo.formattedString(): String =
        "${kind?.name ?: NO_BLOCK_KIND_AVAILABLE} " + "${texts.filterNotNull().joinToString().trim().let {
            if (it.length == 0) NO_BLOCK_LABEL_AVAILABLE else it
        }}".trim()

internal fun Test.copyWithErrorInfo(errorInfo: ErrorInfo?): Test =
        copy(
                state = State.FAIL,
                logMessages =
                logMessages + LogMessage(text = errorInfo?.toString() ?: "error could not be converted to string"))

internal fun Test.getStateBasedOnChildren(): State =
        // If any child's state is FAIL then the parent's state is FAIL
        if (children.map(Test::state).contains(State.FAIL)) State.FAIL
        else state