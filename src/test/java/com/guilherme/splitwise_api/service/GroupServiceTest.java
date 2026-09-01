package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.exception.ResourceNotFoundException;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.repository.GroupRepository;
import com.guilherme.splitwise_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    void updateGroup_shouldUpdateNameOnly_whenGroupExists() {
        Group existingGroup = new Group();
        existingGroup.setId(1L);
        existingGroup.setName("Old Name");

        Group updateData = new Group();
        updateData.setName("New Name");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(existingGroup)).thenReturn(existingGroup);

        Group result = groupService.updateGroup(1L, updateData);

        assertEquals("New Name", result.getName());
    }

    @Test
    void getGroupById_shouldThrowException_whenGroupDoesNotExist() {
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(999L));
    }
}