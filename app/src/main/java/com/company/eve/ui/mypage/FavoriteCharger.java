package com.company.eve.ui.mypage;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteCharger implements Parcelable {
    private Integer chargerId;
    private String chargerName;
    private String chargerAddress;
    private String city;
    private String limit;
    private String output;
    private String statNm;
    private String typeS;
    private Double lat;
    private Double lng;
    private String place;
    private String province;
    private String space;
    private String type;
    private String Ctype;
    private String bnm;
    private String busiNm;

    public FavoriteCharger() {
        // Default constructor required for Firebase
    }

    public FavoriteCharger(Integer chargerId, String chargerName, String chargerAddress, String city, String limit, String output, String statNm, String typeS, Double lat, Double lng, String place, String province, String space, String type, String Ctype, String bnm, String busiNm) {
        this.chargerId = chargerId;
        this.chargerName = chargerName;
        this.chargerAddress = chargerAddress;
        this.city = city;
        this.limit = limit;
        this.output = output;
        this.statNm = statNm;
        this.typeS = typeS;
        this.lat = lat;
        this.lng = lng;
        this.place = place;
        this.province = province;
        this.space = space;
        this.type = type;
        this.Ctype = Ctype;
        this.bnm = bnm;
        this.busiNm = busiNm;
    }

    protected FavoriteCharger(Parcel in) {
        if (in.readByte() == 0) {
            chargerId = null;
        } else {
            chargerId = in.readInt();
        }
        chargerName = in.readString();
        chargerAddress = in.readString();
        city = in.readString();
        limit = in.readString();
        output = in.readString();
        statNm = in.readString();
        typeS = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readDouble();
        }
        place = in.readString();
        province = in.readString();
        space = in.readString();
        type = in.readString();
        Ctype = in.readString();
        bnm = in.readString();
        busiNm = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (chargerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(chargerId);
        }
        dest.writeString(chargerName);
        dest.writeString(chargerAddress);
        dest.writeString(city);
        dest.writeString(limit);
        dest.writeString(output);
        dest.writeString(statNm);
        dest.writeString(typeS);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lng);
        }
        dest.writeString(place);
        dest.writeString(province);
        dest.writeString(space);
        dest.writeString(type);
        dest.writeString(Ctype);
        dest.writeString(bnm);
        dest.writeString(busiNm);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavoriteCharger> CREATOR = new Creator<FavoriteCharger>() {
        @Override
        public FavoriteCharger createFromParcel(Parcel in) {
            return new FavoriteCharger(in);
        }

        @Override
        public FavoriteCharger[] newArray(int size) {
            return new FavoriteCharger[size];
        }
    };

    public Integer getChargerId() {
        return chargerId;
    }

    public void setChargerId(Integer chargerId) {
        this.chargerId = chargerId;
    }

    public String getChargerName() {
        return chargerName;
    }

    public void setChargerName(String chargerName) {
        this.chargerName = chargerName;
    }

    public String getChargerAddress() {
        return chargerAddress;
    }

    public void setChargerAddress(String chargerAddress) {
        this.chargerAddress = chargerAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatNm() {
        return statNm;
    }

    public void setStatNm(String statNm) {
        this.statNm = statNm;
    }

    public String getTypeS() {
        return typeS;
    }

    public void setTypeS(String typeS) {
        this.typeS = typeS;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
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

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCtype() {
        return Ctype;
    }

    public void setCtype(String ctype) {
        this.Ctype = ctype;
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
}
