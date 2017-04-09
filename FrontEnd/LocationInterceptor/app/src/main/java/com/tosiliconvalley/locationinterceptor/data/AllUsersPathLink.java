package com.tosiliconvalley.locationinterceptor.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by equipo on 3/29/2017.
 */
@SuppressWarnings("unused")
public class AllUsersPathLink {

    private Integer groupId; // 2
    private String globalLocation; // bar location
    private List<UserPathLink> usersPathLinkList;


    @SuppressWarnings("unused")
    public AllUsersPathLink(Integer groupId, String globalLocation) {
        this.groupId = groupId;
        this.globalLocation = globalLocation;
        usersPathLinkList = new LinkedList<UserPathLink>();
    }

    @SuppressWarnings("unused")
    public Integer getGroup() {
        return groupId;
    }
    @SuppressWarnings("unused")
    public void setGroup(Integer group) {
        this.groupId = groupId;
    }

    @SuppressWarnings("unused")
    public String getGlobalLocation() {
        return globalLocation;
    }
    @SuppressWarnings("unused")
    public void setGlobalLocation(String globalLocation) {
        this.globalLocation = globalLocation;
    }

    @SuppressWarnings("unused")
    public void addUserPathLink(UserPathLink userPathLink) {
        usersPathLinkList.add(userPathLink);
    }

    @SuppressWarnings("unused")
    public List<UserPathLink> getUsersPathLinkList() {
        return usersPathLinkList;
    }
    @SuppressWarnings("unused")
    public void setUsersPathLinkList(List<UserPathLink> usersPathLinkList) {
        this.usersPathLinkList = usersPathLinkList;
    }
}
