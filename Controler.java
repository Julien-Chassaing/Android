package com.losrobertoshermanos.ppe;

public class Controler {
    private String commentaire;
    private String id_intervention;
    private String n_serie;
    private String temps;

    Controler(String n_serie2, String id_intervention2, String temps2, String commentaire2) {
        this.n_serie = n_serie2;
        this.id_intervention = id_intervention2;
        this.temps = temps2;
        this.commentaire = commentaire2;
    }

    public void setN_serie(String n_serie2) {
        this.n_serie = n_serie2;
    }

    public void setId_intervention(String id_intervention2) {
        this.id_intervention = id_intervention2;
    }

    public void setTemps(String temps2) {
        this.temps = temps2;
    }

    public void setCommentaire(String commentaire2) {
        this.commentaire = commentaire2;
    }

    public String getN_serie() {
        return this.n_serie;
    }

    public String getId_intervention() {
        return this.id_intervention;
    }

    public String getTemps() {
        return this.temps;
    }

    public String getCommentaire() {
        return this.commentaire;
    }
}
