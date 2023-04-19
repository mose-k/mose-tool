package com.mose.util;

public class Goods {
    String name;
    String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Goods(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Goods() {
    }

    @Override
    public String toString() {
        return "Goods{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
