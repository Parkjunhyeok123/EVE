package com.company.eve.model;

public class Station {
    private String name;
    private String space;
    private String statNm;
    private String type;
    private String typeS;
    private String Ctype; // 추가된 필드
    private String addr;
    private String bnm;
    private String busiNm;
    private int chgerId;
    private String city;
    private double lat;
    private String limit;
    private double lng;
    private String output;
    private String place;
    private String province;

    // 생성자
    public Station() {
        // 기본 생성자는 Firebase에서 데이터를 읽어올 때 필요합니다.
    }

    public Station(String name, String space, String statNm, String type, String typeS, String ctype, String addr,
                   String bnm, String busiNm, int chgerId, String city, double lat, String limit,
                   double lng, String output, String place, String province) {
        this.name = name;
        this.space = space;
        this.statNm = statNm;
        this.type = type;
        this.typeS = typeS;
        this.Ctype = ctype;
        this.addr = addr;
        this.bnm = bnm;
        this.busiNm = busiNm;
        this.chgerId = chgerId;
        this.city = city;
        this.lat = lat;
        this.limit = limit;
        this.lng = lng;
        this.output = output;
        this.place = place;
        this.province = province;
    }

    // getName() 메서드 추가
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getStatNm() {
        return statNm;
    }

    public void setStatNm(String statNm) {
        this.statNm = statNm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeS() {
        return typeS;
    }

    public void setTypeS(String typeS) {
        this.typeS = typeS;
    }

    public String getCtype() {
        return Ctype;
    }

    public void setCtype(String ctype) {
        this.Ctype = ctype;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getBnm() {
        return bnm;
    }

    public void setBnm(String bnm) {
        this.bnm = bnm;
    }

    public String getBusiNm() {
        return busiNm;
    }

    public void setBusiNm(String busiNm) {
        this.busiNm = busiNm;
    }

    public int getChgerId() {
        return chgerId;
    }

    public void setChgerId(int chgerId) {
        this.chgerId = chgerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    // Getter와 Setter 메서드는 생략되었습니다. 필요에 따라 추가할 수 있습니다.
}
