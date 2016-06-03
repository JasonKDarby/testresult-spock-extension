import spock.lang.Specification

class BasicFooSpec extends Specification {

  def 'a basic feature'() {
    expect:
    1 == 1
  }

  def 'a basic parametrized feature'() {
    expect:
    input == expected

    where:
    input | expected
    1     | 1
    2     | 2
    3     | 3
  }

}