package com.jdarb.testresult.spockextension.listeners

import com.jdarb.testresult.model.State
import com.jdarb.testresult.model.Test
import org.junit.runner.Description
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class SpecInfoListenerSpec extends Specification {

  def 'beforeSpec only starts the run'() {
    given: 'a callback to return the specification Test instance to'
      Optional<Test> specification = Optional.empty()
      Closure callback = { Test test -> specification = Optional.of(test) }

    and: 'a SpecInfoListener'
      SpecInfoListener specInfoListener = new SpecInfoListener(callback)

    and: 'a test SpecInfo'
      SpecInfo specInfo = new SpecInfo()
      specInfo.name = 'name'
      specInfo.narrative = 'narrative'
      specInfo.description = Description.createSuiteDescription('suite description', [])

    when: 'beforeSpec runs'
      Instant earliestStartTime = Instant.now()
      specInfoListener.beforeSpec(specInfo)
      Instant latestStartTime = Instant.now()

    and: 'afterSpec runs'
      Instant earliestEndTime = Instant.now()
      specInfoListener.afterSpec(specInfo)
      Instant latestEndTime = Instant.now()

    then: 'a specification was returned'
      specification.isPresent()
      Test returned = specification.get()

    and: 'the specification reflects the specInfo'
      returned.name == 'suite description'
      returned.startTime == earliestStartTime || returned.startTime.isAfter(earliestStartTime)
      returned.startTime == latestStartTime   || returned.startTime.isBefore(latestStartTime)
      returned.endTime == earliestEndTime     || returned.endTime.isAfter(earliestEndTime)
      returned.endTime == latestEndTime       || returned.endTime.isBefore(latestEndTime)
      returned.children == []
      returned.logMessages == []
      returned.state == State.PASS
  }

  @Unroll
  def 'test status can be derived based on children'() {
    when:
    State state = SpecInfoListenerKt.getStateBasedOnChildren(test)

    then:
    state == expected

    where:
    test                                                                              | expected
    new Test('t', 't', Instant.now(), Instant.now(), State.PASS, [], [passedTest])    | State.PASS
    new Test('t', 't', Instant.now(), Instant.now(), State.PASS, [], [failedTest])    | State.FAIL
    new Test('t', 't', Instant.now(), Instant.now(), State.PASS, [], [skippedTest])   | State.PASS
  }

  @Shared Test failedTest = new Test('failed', 'failed', Instant.now(), Instant.now(), State.FAIL, [], [])
  @Shared Test passedTest = new Test('passed', 'passed', Instant.now(), Instant.now(), State.PASS, [], [])
  @Shared Test skippedTest = new Test('skipped', 'skipped', Instant.now(), Instant.now(), State.SKIP, [], [])

}
