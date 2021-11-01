package com.patrick.delivery.services;

import com.patrick.delivery.entities.AppRoles;
import com.patrick.delivery.entities.UserGroups;
import com.patrick.delivery.repositories.api.AppRoleRepository;
import com.patrick.delivery.repositories.api.UserGroupsRepository;
import com.patrick.delivery.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author patrick on 8/1/20
 * @project sprintel-delivery
 */
@Service
public class AppRoleService {

    @Autowired
    private AppRoleRepository appRoleRepository;
    @Autowired
    private UserGroupsRepository userGroupsRepository;

    public List<AppRoles> fetchList() {
        return appRoleRepository.findAllByStatus(AppConstant.STATUS_ACTIVE_RECORD);
    }

    public Map<String ,Object> groupPermissions(String index){
        Long id= Long.valueOf(index);
        UserGroups userGroup=userGroupsRepository.findFirstById(id);
        if (null==userGroup)
            return null;
        Map<String, Object> map=new HashMap<>();
        map.put("groupActions",userGroup.getPermissions());
        map.put("entity",userGroup);
        return map;
    }
}
