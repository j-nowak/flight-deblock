package org.deblock.exercise

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DeblockFlightsApplication::class,
    ],
)
class ExerciseApplicationTests {
    @Test
    fun contextLoads() {
    }
}
