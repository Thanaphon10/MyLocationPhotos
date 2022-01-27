package com.example.mylocationphotos;

public class Model {
    public Model() {
    }

    private String imageUrl,Latitude,Longitude,Country,Locality,Address,ImgName;

    public Model(String imageUrl,String Latitude,String Longitude,String Country,String Locality,String Address,String ImgName){
        this.imageUrl = imageUrl;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Country = Country;
        this.Locality = Locality;
        this.Address = Address;
        this.ImgName = ImgName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getLocality() {
        return Locality;
    }

    public void setLocality(String locality) {
        Locality = locality;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
    public String getImgName() {
        return ImgName;
    }

    public void setImgName(String imgName) {
        ImgName = imgName;
    }
}
