package com.coms309.Cynance;

import com.coms309.Cynance.controller.GroupController;
import com.coms309.Cynance.model.Group;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;
    private User user1, user2;
    private Group group;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");

        group = new Group("FunGroup");
        group.setId(101L);
        group.setMembers(new HashSet<>(List.of(user1, user2)));
    }

    @Test
    void testCreateGroupWithUsernames_success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "FunGroup");
        request.put("usernames", List.of("alice", "bob"));

        when(userRepository.findByUsernameIn(List.of("alice", "bob")))
                .thenReturn(List.of(user1, user2));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        mockMvc.perform(post("/group/createWithUsernames")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group created successfully"))
                .andExpect(jsonPath("$.groupName").value("FunGroup"))
                .andExpect(jsonPath("$.memberUsernames", hasSize(2)))
                .andExpect(jsonPath("$.memberUsernames", containsInAnyOrder("alice", "bob")));
    }

    @Test
    void testCreateGroupWithUsernames_userNotFound() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "FunGroup");
        request.put("usernames", List.of("alice", "bob"));

        when(userRepository.findByUsernameIn(List.of("alice", "bob"))).thenReturn(List.of(user1)); // bob missing

        mockMvc.perform(post("/group/createWithUsernames")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Some usernames not found"))
                .andExpect(jsonPath("$.provided", hasSize(2)))
                .andExpect(jsonPath("$.found", hasSize(1)))
                .andExpect(jsonPath("$.found[0]").value("alice"));
    }

    @Test
    void testGetGroupsForUser_success() throws Exception {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user1));
        when(groupRepository.findAll()).thenReturn(List.of(group));

        mockMvc.perform(get("/group/user/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].name").value("FunGroup"))
                .andExpect(jsonPath("$[0].members", containsInAnyOrder("alice", "bob")));
    }

    @Test
    void testGetGroupsForUser_userNotFound() throws Exception {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        mockMvc.perform(get("/group/user/ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUsersByGroupName_success() throws Exception {
        when(groupRepository.findByName("FunGroup")).thenReturn(Optional.of(group));

        mockMvc.perform(get("/group/name/FunGroup/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("FunGroup"))
                .andExpect(jsonPath("$.members", hasSize(2)))
                .andExpect(jsonPath("$.members[*].username", containsInAnyOrder("alice", "bob")));
    }

    @Test
    void testGetUsersByGroupName_groupNotFound() throws Exception {
        when(groupRepository.findByName("NonExistentGroup")).thenReturn(Optional.empty());

        mockMvc.perform(get("/group/name/NonExistentGroup/users"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Group not found"));
    }
}

