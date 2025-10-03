package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.Group;
import com.coms309.Cynance.model.GroupChat;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.model.UserDTO;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/createWithUsernames")
    public ResponseEntity<Map<String, Object>> createGroupWithUsernames(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        List<String> usernames = (List<String>) body.get("usernames");

        List<User> users = userRepository.findByUsernameIn(usernames);

        if (users.size() != usernames.size()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Some usernames not found",
                    "provided", usernames,
                    "found", users.stream().map(User::getUsername).toList()
            ));
        }

        Group group = new Group();
        group.setName(name);
        group.setMembers(new HashSet<>(users)); // for join table group_members
        groupRepository.save(group);

        return ResponseEntity.ok(Map.of(
                "message", "Group created successfully",
                "groupName", group.getName(),
                "memberUsernames", usernames
        ));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Map<String, Object>>> getGroupsForUser(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        User user = optionalUser.get();
        List<Group> groups = groupRepository.findAll();  // or better: create a query by user in join

        List<Map<String, Object>> userGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembers().contains(user)) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", group.getId());
                map.put("name", group.getName());
                map.put("members", group.getMembers().stream().map(User::getUsername).toList());
                userGroups.add(map);
            }
        }

        return ResponseEntity.ok(userGroups);
    }

    @GetMapping("/name/{groupName}/users")
    public ResponseEntity<Map<String, Object>> getUsersByGroupName(@PathVariable String groupName) {
        Optional<Group> optionalGroup = groupRepository.findByName(groupName);

        if (optionalGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Group not found"));
        }

        Group group = optionalGroup.get();

        List<Map<String, Object>> members = new ArrayList<>();
        for (User user : group.getMembers()) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            members.add(userMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("groupName", group.getName());
        response.put("members", members);

        return ResponseEntity.ok(response);
    }

}
