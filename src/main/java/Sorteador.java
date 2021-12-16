import java.util.ArrayList;
import java.util.Arrays;

public class Sorteador {
    public static int generarNumeroAleatorio(int x1, int x2) {
        int numeroAleatorio = (((int) (Math.random() * (x2 - x1 + 1) + x1)));
        return numeroAleatorio;

    }

    public static String elegirEntre(String sujetos) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(sujetos.split(", ")));
        int numeroAleatorio = (int) (Math.random() * (parts.size()) + 0);
        return parts.get(numeroAleatorio);
    }

    public static ArrayList<String> mezclar(String sujetos) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(sujetos.split(", ")));
        for (int i = 0; i < parts.size(); i++) {
            int temp1, temp2;
            String tempS1, tempS2;
            temp1 = (int) (Math.random() * parts.size());
            tempS1 = parts.get(temp1);
            temp2 = (int) (Math.random() * parts.size());
            tempS2 = parts.get(temp2);
            parts.set(temp1, tempS2);
            parts.set(temp2, tempS1);
        }
        return parts;
    }

    public static String darFormatoSalidaLista(ArrayList<String> parts) {
        String cadenaSujetos = "";
        for (int i = 0; i < parts.size(); i++) {
            cadenaSujetos = cadenaSujetos + "\n" + (i + 1) + ". " + parts.get(i);
        }
        return cadenaSujetos;
    }

    //Agregar a comando -mezclar- opcion retornar solo x elementos deseados
    public static String darFormatoSalidaLista(ArrayList<String> parts, int NElementosARetornar) {
        String cadenaSujetos = "";
        for (int i = 0; i < NElementosARetornar; i++) {
            cadenaSujetos = cadenaSujetos + "\n" + (i + 1) + ". " + parts.get(i);
        }
        return cadenaSujetos;
    }

    public static String repartirEntre(String sujetosYElementos) {
        String[] parts = sujetosYElementos.split(" - ");
        String sujetos = parts[0];
        String elementos = parts[1];
        ArrayList<String> mezclaSujetos = mezclar(sujetos);
        ArrayList<String> mezclaElementos = mezclar(elementos);
        for (int i = 0; i < mezclaSujetos.size(); i++) {
            mezclaSujetos.set(i, mezclaSujetos.get(i) + ":");
        }
        int contadorElemento = 0;
        while (contadorElemento < mezclaElementos.size()) {
            for (int i = 0; i < mezclaSujetos.size(); i++) {
                String sujetoActual = mezclaSujetos.get(i);
                sujetoActual = sujetoActual + " " + mezclaElementos.get(contadorElemento);
                mezclaSujetos.set(i, sujetoActual);
                contadorElemento++;
                if (!(contadorElemento < mezclaElementos.size())) {
                    break;
                }
            }
        }
        return darFormatoSalidaLista(mezclaSujetos);

    }
}
