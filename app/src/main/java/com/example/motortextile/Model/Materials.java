package com.example.motortextile.Model;

public class Materials
{
    private String mname, description, price, image, category, mid, date, time;

    public Materials()
    {

    }

    public Materials(String mname, String description, String price, String image, String category, String mid, String date, String time) {
        this.mname = mname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.mid = mid;
        this.date = date;
        this.time = time;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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
}
