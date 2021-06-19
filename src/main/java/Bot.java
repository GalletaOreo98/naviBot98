import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            default:
                break;
        }

        long i = update.getMessage().getChatId();
        String s = String.valueOf(i);
        message.setChatId(s);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
        }
    }

    @Override
    public String getBotUsername() {
        return "OfficialNavi_bot";
    }
}
