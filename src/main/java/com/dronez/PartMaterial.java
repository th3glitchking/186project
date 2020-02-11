package com.dronez;

public class PartMaterial {

    public static final byte IRON = 1, GOLD = 2, DIAMOND = 3;

    private byte material;

    public PartMaterial(byte material){
        this.material = material;
    }

    public byte getValue() {
        return material;
    }
    public String getMaterial() {
        switch (material) {
            case 1:
                return "iron";
            case 2:
                return "gold";
            case 3:
                return "diamond";
            default:
                return null;
        }
    }
}
