package com.jdarb.testresult.spockextension

import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

class TestResultExtensionSpec extends Specification {

  def 'a basic specification can generate json results'() {
    given: 'a spec runner with a TestResultExtension'
    EmbeddedSpecRunner runner = new EmbeddedSpecRunner()
    runner.extensionClasses << TestResultExtension

    and: 'a demo specification'
    String specification = getClass().getResourceAsStream('/BasicFooSpec.groovy').text

    when: 'the spec runs'
    runner.run specification

    then: 'a json results file is generated'
    //TODO: validate that file was generated and can be parsed
  }

}
