package coms309;

import coms309.people.Person;
import org.springframework.web.bind.annotation.*;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {


    //checking to see what the difference is with RequestMapping and GetMapping
    @RequestMapping("/")
    public String index() {return "Hello World";}


}
