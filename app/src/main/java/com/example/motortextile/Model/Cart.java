package com.example.motortextile.Model;

public class Cart
{
    private  String mid, mname, price, amount, discount;

    public Cart()
    {
    }

    public Cart(String mid, String mname, String price, String amount, String discount) {
        this.mid = mid;
        this.mname = mname;
        this.price = price;
        this.amount = amount;
        this.discount = discount;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
