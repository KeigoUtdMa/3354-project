//This is a class for group contact
package cn.projects.team.demo.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
//group informations will also store in database 
public class ContactGroup {
    @Id(autoincrement = true)
    //a group is object and has an id and group name
    private Long id ;
    private String groupName;
    @Generated(hash = 1960676043)
    public ContactGroup(Long id, String groupName) {
        this.id=id;
        this.groupName=groupName;
    }
    @Generated(hash = 1043149959)
    public ContactGroup() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;//set id and return id
    }
    public String getGroupName() {
        return this.groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;//set name and return name
    }
}
