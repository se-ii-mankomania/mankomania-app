package com.example.mankomania.logik.stocks;

public enum StockTypes {
    BRUCHSTAHL_AG("Bruchstahl AG"),
    TROCKENOEL_AG("Trockenöl AG"),
    KURZSCHLUSS_VERSORGUNGS_AG("Kurzschluss-Versorgungs AG");

    private String name;
    StockTypes(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
