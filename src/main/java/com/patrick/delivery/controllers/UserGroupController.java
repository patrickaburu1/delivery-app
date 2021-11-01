package com.patrick.delivery.controllers;

import com.patrick.delivery.services.AppRoleService;
import com.patrick.delivery.utils.db.MakerCheckerFormatter;
import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author patrick on 8/1/20
 * @project sprintel-delivery
 */
@RestController
public class UserGroupController extends AbstractController {

    @Autowired
    private DataTableNewInterface datatable;
    @Autowired
    private AppRoleService appRoleService;

    @RequestMapping("/user-groups")
    public ModelAndView userGroups(HttpServletRequest request) {
        View view = new View("app/users-groups");

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            String action = request.getParameter("action");
            if ("fetch".equals(action))
                return viewRecord(request, view);

            // When fetching a record from the db
            return view.sendJSON(fetchTableInfo(request));
        }

        // Show the home page
        return view

               .addAttribute("roleList", appRoleService.fetchList())
                //.addAttribute("reasoncodes", reasoncodesService.fetchRecords())
                .getView();
    }

    private ModelAndView viewRecord(HttpServletRequest request, View view) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            String index = request.getParameter("index");
            String status = request.getParameter("status");
            map.put("entity", appRoleService.groupPermissions(index));

            if (null == status) status = "";

            if ("update".equals(status))
                map.put("entityCopy", appRoleService.groupPermissions(index));
            else if ("new".equals(status))
                map.put("viewTitle", view.getMessage("makerChk.newRecordTitle"));
            else if ("deactivate".equals(status))
                map.put("viewTitle", view.getMessage("makerChk.deactivateRecordTitle"));
            else if ("activate".equals(status))
                map.put("viewTitle", view.getMessage("makerChk.activateRecordTitle"));
            map.put("status", true);
        } catch (Exception ex) {
            map.put("msg", view.getMessage("ajax.error"));
            map.put("status", false);

            ex.printStackTrace();
        }

        return view.sendJSON(map);
    }

    private Object fetchTableInfo(HttpServletRequest request) {
        // When fetching the table info
        Long userNo = (Long) request.getSession().getAttribute("_userNo");
        String flag = request.getParameter("0[fetch-table]");
        List<Map<String, Object>> filters = datatable.filtersExist(request);
        if (filters.size() > 0) {
            for (Map<String, Object> map : filters) {
                if (map.get("value") != "") {
                    datatable.where("u." + map.get("name") + " = :filter").setParameter("filter", map.get("value"));
                }
            }
        }
        datatable.setFormatter(new MakerCheckerFormatter(request, null));


        datatable.select("GROUP_NAME, DESCRIPTION, CREATED_BY_ID, DATE_FORMAT(creation_date; '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(last_modified_date; '%Y-%m-%d %H:%i:%s'), last_modified_by_id, ID");

        datatable.from("user_groups u")
                .where(" u.flag =:flag").setParameter("flag", flag)
//                .where(" u.id <> :groupId").setParameter("groupId", groupId)
                .nativeSQL(true);
        logger.info("Usergroups Q: {} ", datatable.getSQL(""));
        return datatable.showTable();
    }
}
