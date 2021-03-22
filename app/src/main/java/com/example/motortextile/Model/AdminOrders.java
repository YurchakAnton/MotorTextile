package com.example.motortextile.Model;

public class AdminOrders
{
    private String name, phone, city,email, department, state, date, time, totalPrice, pay, mid;

    public AdminOrders() {
    }

    public AdminOrders(String name, String phone, String city, String email, String department, String state, String date, String time, String totalPrice, String pay, String mid) {
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.email = email;
        this.department = department;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalPrice = totalPrice;
        this.pay = pay;
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
