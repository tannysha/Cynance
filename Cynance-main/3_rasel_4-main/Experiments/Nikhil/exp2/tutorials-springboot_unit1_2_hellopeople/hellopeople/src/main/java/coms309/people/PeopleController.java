package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class PeopleController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Person> peopleList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public  HashMap<String,Person> getAllPersons() {
        return peopleList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // in this case because of @ResponseBody
    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public  String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        return "New person "+ person.getFirstName() + " Saved";
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // in this case because of @ResponseBody
    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // in this case because of @ResponseBody
    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public Person updatePerson(@PathVariable String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // in this case because of @ResponseBody
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }

    //trying to implement a simple registration page
    @PutMapping ("/people/register/{firstName}/{userName}/{password}")
    public HashMap<String,Person> register(@PathVariable String firstName, @PathVariable String userName, @PathVariable String password) {
        Person p = peopleList.get(firstName);
        if(p==null) {
            System.out.println("Person not found please create a user");
                return null;
        }
       // p.setUserName("");
       // p.setPassword("");
        //this will not work as their is no way to get the input from the user

        setUserName(userName, p);
        setPassword(password,p);

        return peopleList;
    }

    @PutMapping("/people/register")
    public void setUserName(@PathVariable String userName, @RequestBody Person p) {
        p.setUserName(userName);
    }
    @PutMapping("/people/register/password")
    public void setPassword(@PathVariable String userName, @RequestBody Person p) {
        p.setPassword(userName);
    }


    //this is a very simple method of implementing a register page, a login page similarly can be implemented by taking the username as an input, but
    //it would probably be easier using the firstName as that is a key of the hash table
    //however this was a good experience in trying to implement the same.
    // tried using @RequestParam, but was unsucseful
    //login page can be made similarly, but since the key in this case is the firstName, was unsuccesful
}

