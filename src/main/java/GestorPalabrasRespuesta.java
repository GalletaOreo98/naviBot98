import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class GestorPalabrasRespuesta {

    public static void sobreEscribirPalabrasEnData(ArrayList<Palabra> arrayData) {
        try {
            ObjectOutputStream escribiendoFichero = new ObjectOutputStream(
                    new FileOutputStream("data/gestorRespuestaData/palabras.dat"));
            escribiendoFichero.writeObject(arrayData);
            escribiendoFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Palabra> getPalabrasEnData() {
        try {
            ArrayList<Palabra> arrayList;
            ObjectInputStream leyendoFichero = new ObjectInputStream(
                    new FileInputStream("data/gestorRespuestaData/palabras.dat"));
            arrayList = (ArrayList<Palabra>) leyendoFichero.readObject();
            leyendoFichero.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void agregarPalabrasEnData(Palabra palabra) {
        ArrayList<Palabra> arrayData = getPalabrasEnData();
        arrayData.add(palabra);
        sobreEscribirPalabrasEnData(arrayData);
    }

    public static String getPalabraRespuesta(ArrayList<Palabra> arrayPalabras, String etiqueta) {

        ArrayList<Palabra> arrayPalabrasSeleccionadas = new ArrayList<Palabra>();

        for (int i = 0; i < arrayPalabras.size(); i++) {
            if (arrayPalabras.get(i).getEtiqueta().equals(etiqueta)) {
                arrayPalabrasSeleccionadas.add(arrayPalabras.get(i));
            }
        }
        if (!arrayPalabrasSeleccionadas.isEmpty()) {
            int frecuenciaAleatoria = Sorteador.generarNumeroAleatorio(1, 100);
            if (frecuenciaAleatoria != 1) {
                frecuenciaAleatoria = 2;
            }
            ArrayList<String> arrayPalabrasFrecuencia = new ArrayList<String>();
            for (int i = 0; i < arrayPalabrasSeleccionadas.size(); i++) {
                if (arrayPalabrasSeleccionadas.get(i).getFrecuencia() == frecuenciaAleatoria) {
                    arrayPalabrasFrecuencia.add(arrayPalabrasSeleccionadas.get(i).getPalabra());
                }
            }

            int numeroAleatorio = Sorteador.generarNumeroAleatorio(0, arrayPalabrasFrecuencia.size() - 1);
            return arrayPalabrasFrecuencia.get(numeroAleatorio);
        }else{
            return null;
        }

    }

    public static String localizarEtiqueta(ArrayList<Palabra> arrayPalabras, String palabra) {
        for (int i = 0; i < arrayPalabras.size(); i++) {
            if (arrayPalabras.get(i).getPalabra().equals(palabra)) {
                return arrayPalabras.get(i).getEtiqueta();
            }
        }
        return null;
    }
}
