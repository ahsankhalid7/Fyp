package com.example.uclone;

public class getdoctorordriver {
    private String name,img,address,phonenumber,hospitalassociation,specialization,id,userfor;

    public getdoctorordriver(String address,
                             String clientname,
                             String iamge,
                             String latlng,
                             String status){
        this.address = address;
        this.name = clientname;
        this.img = iamge;
        this.id = latlng;
        this.phonenumber = status;


    }
    public getdoctorordriver(String name, String img, String address, String phonenumber,
                             String hospitalassociation, String specialization,String id,String userfor) {
        this.name = name;
        this.img = img;
        this.address = address;
        this.phonenumber = phonenumber;
        this.hospitalassociation = hospitalassociation;
        this.specialization = specialization;
        this.id = id;
        this.userfor = userfor;
    }
    public getdoctorordriver(String name, String img, String address, String phonenumber,
                            String id,String userfor) {
        this.name = name;
        this.img = img;
        this.address = address;
        this.phonenumber = phonenumber;
        this.id = id;
        this.userfor = userfor;
    }

    public String getUserfor() {
        return userfor;
    }

    public void setUserfor(String userfor) {
        this.userfor = userfor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getHospitalassociation() {
        return hospitalassociation;
    }

    public void setHospitalassociation(String hospitalassociation) {
        this.hospitalassociation = hospitalassociation;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
