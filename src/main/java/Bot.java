import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import java.io.*;

public class Bot extends TelegramLongPollingBot{

    String[] comandosInfo = { "numeroRandom n1,n2\nDevuelve un numero aleatorio entre x1 y x2",
            "elegirEntre e1,e2,e3,...en\nElije un elemento entre la lista de elementos que se proporcione",
            "mezclar e1,e2,e3,...en\nMezcla aleatoriamente los elementos de la lista proporcionada y la devuelve",
            "repartirEntre s1,s2,s3,...sn-e1,e2,e3,...en\nReparte entre los n sujetos los n elementos de manera igualitaria y aleatoria y devuelve la lista",
            "HelloImage\nDevuelve una imagen",
            "Hello\nDevuelve un sticker",
            "Voice\nDevuelve un Audio de voz",
            "Adios\nDevuelve una despedida" 
    };

    @Override
    public String getBotToken() {
        return "1505442251:AAEZfr09JbOhFz0v5iRL_oa3aCEPHmgef9Q";
    }

    @Override
    public void onUpdateReceived(Update update) {

        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();

        String[] partesDelComando = new String[2];
        String comandoPrincipal = new String();
        String restoDelMensaje = new String();



        partesDelComando = command.split(" ");
        if (partesDelComando.length == 1) {
            comandoPrincipal = command;
        } else {
            comandoPrincipal = partesDelComando[0];
            restoDelMensaje = partesDelComando[1];
        }

        String chatId = String.valueOf(update.getMessage().getChatId());
        message.setChatId(chatId);

        switch (comandoPrincipal) {
        case "numeroRandom":
            String[] partsV = restoDelMensaje.split(",");
            int x1 = Integer.parseInt(partsV[0]);
            int x2 = Integer.parseInt(partsV[1]);
            message.setText("" + Sorteador.generarNumeroAleatorio(x1, x2));
            break;
        case "elegirEntre":
            message.setText("Elijo: " + Sorteador.elegirEntre(restoDelMensaje));
            break;
        case "mezclar":
            String mezcla = Sorteador.darFormatoSalidaLista(Sorteador.mezclar(restoDelMensaje));
            message.setText(mezcla);
            break;
        case "repartirEntre":
            message.setText(Sorteador.repartirEntre(restoDelMensaje));
            break;
        case "HelloImage":
            enviarFoto("imagenes/helloWorld.png", chatId);
            String ruta = getClass().getClassLoader().getResource("helloWorld.png").getPath();
            System.out.println(ruta);
            break;
        case "Hello":
            enviarSticker("stickers/helloSticker.webp", chatId);
            break;
        case "Voice":
            enviarVoice("Voice/db.mp3", chatId);
            break;
        case "Adios":
            message.setText("Adios UwU");
            break;
        case "Help":
            message.setText(getInfoComados());
            break;
        default:
            break;
        }

        try {
            if (!message.getText().isEmpty()) {
                execute(message);
            }
        } catch (TelegramApiException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public String getBotUsername() {
        return "OfficialNavi_bot";
    }

    private InputFile getImageAsInpuFile(String rutaImagen) {
        File image = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            image = new File(classLoader.getResource(rutaImagen).getFile());
        } catch (Exception e) {
            System.out.println("Error al acceder a la imagen: " + image.toString());
        }
        return new InputFile(image);
    }

    private void enviarFoto(String rutaImagen, String chatId) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(getImageAsInpuFile(rutaImagen));
            sendPhoto.setChatId(chatId);
            execute(sendPhoto);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    private void enviarSticker(String rutaSticker, String chatId) {
        try {
            SendSticker sendSticker = new SendSticker();
            sendSticker.setSticker(getImageAsInpuFile(rutaSticker));
            sendSticker.setChatId(chatId);
            execute(sendSticker);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    private void enviarVoice(String rutaAudio, String chatId) {
        try {
            SendVoice sendVoice = new SendVoice();
            sendVoice.setVoice(getImageAsInpuFile(rutaAudio));
            sendVoice.setChatId(chatId);
            execute(sendVoice);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    private String getInfoComados(){
        String comandosString="";
        for (int i = 0; i < comandosInfo.length; i++) {
            comandosString=comandosString+(i+1)+". "+comandosInfo[i]+"\n";
        }
        return comandosString;
    }

    private void retraso(int milisegundos){
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
