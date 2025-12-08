package org.example.inzynierka

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class EnvironmentSpec extends Specification {

    def "Spring Boot + Groovy + Spock environment works correctly"() {
        expect:
        1 + 1 == 2
    }
}
