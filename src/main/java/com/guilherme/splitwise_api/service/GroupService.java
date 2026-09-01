package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.exception.ResourceNotFoundException;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.GroupRepository;
import com.guilherme.splitwise_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        Optional<Group> resultado = groupRepository.findById(id);
        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new ResourceNotFoundException("Group not found with id:" + id);
        }
    }

    public Group updateGroup(Long id, Group updateGroup) {
        Optional<Group> resultado = groupRepository.findById(id);
        if (resultado.isPresent()) {
            Group group = resultado.get();
            group.setName(updateGroup.getName());
            group.setExpenses(updateGroup.getExpenses());
            group.setMembers(updateGroup.getMembers());
            return groupRepository.save(group);
        } else {
            throw new ResourceNotFoundException("Group not found with id:" + id);
        }
    }

    public void deleteGroup(Long id) {
        Optional<Group> resultado = groupRepository.findById(id);
        if (resultado.isPresent()) {
            groupRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Group with id" + id + "does not exist.");
        }
    }
}