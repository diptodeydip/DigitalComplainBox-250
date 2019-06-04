package com.example.dipde.digitalcomplainbox;

public class Upload {
    String date,des,imageUrl,key,name,time,cat,status,dept,id;

    public Upload() {
    }



    public String getDes() {
        return des;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

