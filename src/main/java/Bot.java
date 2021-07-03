import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import java.io.*;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return "1505442251:AAEZfr09JbOhFz0v5iRL_oa3aCEPHmgef9Q";
    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = update.getMessage().getText();

        SendMessage message = new SendMessage();

        String[] partesDelComando = command.split(" ");
        String comandoPrincipal = partesDelComando[0];
        String restoDelMensaje = partesDelComando[1];

        String chatId = String.valueOf(update.getMessage().getChatId());
        message.setChatId(chatId);

        switch (comandoPrincipal) {
            case "numeroRandom":
                String[] partsV = restoDelMensaje.split(",");
                int x1 = Integer.parseInt(partsV[0]);
                int x2 = Integer.parseInt(partsV[1]);
                message.setText("" + Sorteador.generarNumeroAleatorio(x1, x2));
                System.out.println(command);
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
                enviarFoto("helloWorld.png", chatId);
                String ruta = getClass().getClassLoader().getResource("helloWorld.png").getPath();
                System.out.println(ruta);
                break;
            case "HelloSticker":
                enviarSticker("helloSticker.webp", chatId);
            default:
                break;
        }


        try {
            if(!message.getText().isEmpty()){
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

    private void enviarSticker(String rutaSticker, String chatId){
        try {
            SendSticker sendSticker = new SendSticker();
            sendSticker.setSticker(getImageAsInpuFile(rutaSticker));
            sendSticker.setChatId(chatId);
            execute(sendSticker);
        } catch (Exception h) {
            System.out.println(h);
        }
    }
}
