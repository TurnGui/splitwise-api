package com.guilherme.splitwise_api.controller;

import com.guilherme.splitwise_api.dto.DebtTransaction;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.service.BalanceService;
import com.guilherme.splitwise_api.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final BalanceService balanceService;

    public GroupController(GroupService groupService, BalanceService balanceService) {
        this.groupService = groupService;
        this.balanceService = balanceService;
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        return groupService.createGroup(group);
    }

    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }

    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable Long id, @RequestBody Group updateGroup) {
        return groupService.updateGroup(id, updateGroup);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }

    @GetMapping("/{id}/balances")
    public Map<String, BigDecimal> getGroupBalances(@PathVariable Long id) {
        return balanceService.getGroupBalances(id);
    }

    @GetMapping("/{id}/simplify-debts")
    public List<DebtTransaction> simplifyDebts(@PathVariable Long id) {
        return balanceService.simplifyGroupDebts(id);
    }
}