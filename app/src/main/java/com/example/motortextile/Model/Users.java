package com.example.motortextile.Model;

public class Users
{
    private String name, email, city, phone, password, adminpanel, image;

    public Users()
    {

    }

    public Users(String name, String email, String city, String phone, String password, String adminpanel, String image) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.phone = phone;
        this.password = password;
        this.adminpanel = adminpanel;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminPanel() {
        return adminpanel;
    }

    public void setAdminPanel(String adminpanel) {
        this.adminpanel = adminpanel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
