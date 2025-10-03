package coms309;

/**
 * Controller used to showcase what happens when an exception is thrown
 *
 * @author Vivek Bengre
 */

import coms309.people.Person;
import org.springframework.web.bind.annotation.*;

@RestController
class ExceptionController {

    @RequestMapping(method = RequestMethod.GET, path = "/oops")
    public String triggerException() {
        throw new RuntimeException("Check to see what happens when an exception is thrown");
    }
    //using requestmapping instead of the postmapping to see how to handle errors


    @RequestMapping(method = RequestMethod.POST, path="/why")
    public String triggerException2() {
        throw new RuntimeException("Check to see what happens when an exception is thrown");
    }

    //error controller made for the register page
//    @PutMapping("/people/register/{firstName}")
//    public String registerError(@PathVariable String firstName) {
//        if(Person.get(firstName)==null)
//
//            return "Error";
//
//        return "no problem";
//    }

    //implementation was not succesful but as not sure how to pull out the information from this class. Would be an easier implementaion from the People Controller class which has a list of the people.
    //but am sure there is an implementation this way
}
