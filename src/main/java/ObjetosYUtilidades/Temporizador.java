package ObjetosYUtilidades;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import Principal.Bot;

public class Temporizador extends Thread{

    String recordatorio="";
    int minutos = 0;
    int horas = 0;
    int dias = 0;
    Bot bot;
    SendMessage message;
    String chatId;

   

    public Temporizador(String recordatorio, int minutos, int horas, int dias, Bot bot, SendMessage message, String chatId) {
        this.recordatorio = recordatorio;
        this.minutos = minutos;
        this.horas = horas;
        this.dias = dias;
        this.bot = bot;
        this.message = message;
        this.chatId = chatId;
    }



    public void run() {
        long tiempoEspera;
        tiempoEspera = minutos*60*1000 + horas*60*60*1000 + dias*24*60*60*1000;
        try {
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        message.setChatId(chatId);
        message.setText(recordatorio);
        bot.enviarMensaje(message);
    }
}
