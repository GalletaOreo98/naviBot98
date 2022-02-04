package Principal;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App 
{
    public static void main( String[] args ){
        Bot b = new Bot();
        try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(b);
            } catch (TelegramApiException e) {
            }
        System.out.println("Bot Iniciado");
    }
}
