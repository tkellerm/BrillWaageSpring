package de.abasgmbh.brill.config;

import javax.management.BadAttributeValueExpException;

public class Waage {

    String name;
    String ipadress;
    int port;
    Integer leuchtdauer = 5000;
    Integer textdauer = 5000;
    Boolean active;
    Double gelbGrenze;
    Double rotGrenze;

    public Waage(String name, String ipadress, int port, Integer leuchtdauer, Integer textdauer, Boolean isActive, Double gelbGrenze, Double rotGrenze) throws BadAttributeValueExpException {
        this.gelbGrenze = gelbGrenze;
        this.rotGrenze = rotGrenze;
        if (name != null & ipadress != null) {
            if (!name.isEmpty() & !ipadress.isEmpty() & port > 0) {
                this.name = name;
                this.ipadress = ipadress;
                this.port = port;
                this.active = isActive;
//				Die Leuchtdauer soll nur vom DefaultWert 5000ms geändert werden, wenn der übergebene Wert >0  ist
                if (leuchtdauer != null) {
                    if (leuchtdauer > 0) {
                        this.leuchtdauer = leuchtdauer;
                    }
                }
                if (textdauer != null) {
                    if (textdauer > 0) {
                        this.textdauer = textdauer;
                    }
                }
            }
        } else {

            throw new BadAttributeValueExpException("Der Name ist nicht gefüllt");
        }


    }

    public String getName() {
        return name;
    }

    public String getIpadress() {
        return ipadress;
    }

    public int getPort() {
        return port;
    }

    public Integer getLeuchtdauer() {
        return leuchtdauer;
    }

    public Integer getTextdauer() {
        return textdauer;
    }

    public boolean isActive() {
        return this.active;
    }

    public Double getRotGrenze() {
        return rotGrenze;
    }

    public Double getGelbGrenze() {
        return gelbGrenze;
    }

}
