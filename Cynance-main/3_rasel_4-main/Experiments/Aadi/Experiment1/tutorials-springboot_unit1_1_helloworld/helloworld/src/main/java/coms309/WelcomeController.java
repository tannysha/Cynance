package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
class WelcomeController {

    private final AtomicInteger visitss = new AtomicInteger(0); // for counting visits

    @GetMapping("/")
    public String welcome() {
        int visits =  visitss.incrementAndGet(); // adding visits each time someone visits the page
        return "Hello and welcome to COMS 309 web page this site has been visited "+ visits + " times.";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
    @GetMapping("/greet") // If I add greet at the end of address it will greet me according to local time
    public String greet() {
        LocalTime time = LocalTime.now();
        if (time.isBefore(LocalTime.NOON)) {
            return "Good Morning! Coffee filled, Mind Fresh and lets start our work ?";
        } else if (time.isBefore(LocalTime.of(17, 0))) {
            return "Good Afternoon! you are doing great Keep up the good work.";
        } else {
            return "Good Evening! Tasks completed, Time to chill.";
        }
    }
}
