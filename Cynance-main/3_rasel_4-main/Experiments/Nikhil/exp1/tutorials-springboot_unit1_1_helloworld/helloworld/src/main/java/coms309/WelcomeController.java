package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalTime;


@RestController
class WelcomeController {

    private static int numVisits=0;

    @GetMapping("/")
    public String welcome() {
        numVisits++;
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/helloWorld")
   public String helloWorld() {
        numVisits++;
        return "Hello World!";}

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        numVisits++;
        return "Hello and welcome to COMS 309: " + name;
    }

    // trying to implement the time interface in java as well as familiarize myself with the getmapping of the rest controller, as well the @pathvaraible annotation
    //easy implementation of the get request of the http methods.

    @GetMapping("/meal/{name}")
    public String meal(@PathVariable String name) {
        numVisits++;
        LocalTime now = LocalTime.now();
        String returnMessage="";
        if(now.isBefore(LocalTime.of(12, 0))){
            returnMessage = "It is " + now+" and it is time to eat breakfast ";
        }
        else if(now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(16, 30))){
            returnMessage="It is " + now+" and it is time to eat lunch ";
        }
        else if(now.isAfter(LocalTime.of(16, 30))){
            returnMessage="It is " + now+" and it is time to eat dinner ";
        }
        returnMessage+=name;
        return returnMessage;
    }


//this method to find alternatives to @path variable, of which i have picked the @Requestparam tag and how it works
    //unlike @pathvarible in which the variable is populated in the path of which it is enacted. this follows the format of /hello?name={}
    //the pathvariable notation could be uesd for having data stored about a user, whereas the requestparam can be used for user authentication.
    //can be accessed using postman as well
    @GetMapping("/hello")
            public String hell(@RequestParam String name) {
        numVisits++;
        return "Hello and welcome to CYNANCE: " + name;
    }


    //static variable to track the number of visits as long as the server is up
    @GetMapping("/getNumVisits")
    public String getNumVisits() {

        return "Number of visits: " + numVisits;
    }
}
