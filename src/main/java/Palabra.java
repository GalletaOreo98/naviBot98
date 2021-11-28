import java.io.Serializable;

public class Palabra implements Serializable{
    private String etiqueta;
    private String palabra;
    private int frecuencia;

    public Palabra(String etiqueta, String palabra, int frecuencia) {
        this.etiqueta = etiqueta;
        this.palabra = palabra;
        this.frecuencia = frecuencia;
    }
    public String getEtiqueta() {
        return etiqueta;
    }
    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
    public String getPalabra() {
        return palabra;
    }
    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }
    public int getFrecuencia() {
        return frecuencia;
    }
    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

}
