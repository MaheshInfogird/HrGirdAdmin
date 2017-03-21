package com.hrgirdowner;

/**
 * Created by adminsitrator on 15/02/2017.
 */
public class get_set_AttListing 
{
    private String firstName, lastName, inTime, outTime, status, cId, Dept, WorkHr, fullName;
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstname){
        this.firstName = firstname;
    }
    
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastname){
        this.lastName = lastname;
    }
    
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    
    public String getInTime() {
        return this.inTime;
    }

    public void setInTime(String intime){
        this.inTime = intime;
    }
    
    public String getOutTime() {
        return this.outTime;
    }

    public void setOutTime(String outtime){
        this.outTime = outtime;
    }
    
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
    }
    
    public String getCid() {
        return this.cId;
    }

    public void setCid(String cid){
        this.cId = cid;
    }
    
    public String getDept() {
        return this.Dept;
    }

    public void setDept(String dept){
        this.Dept = dept;
    }
    
    public String getWorkHour() {
        return this.WorkHr;
    }

    public void setWorkHour(String workHr){
        this.WorkHr = workHr;
    }
}
