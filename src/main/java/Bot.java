import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import java.io.*;
import java.util.ArrayList;
import io.github.cdimascio.dotenv.Dotenv;

public class Bot extends TelegramLongPollingBot {

    ArrayList<Palabra> arrayPalabras = GestorPalabrasRespuesta.getPalabrasEnData();

    // En la carpeta raiz se agrega un file .env para acceder a claves como
    // bot_token con dotenv class
    private Dotenv dotenv = Dotenv.configure().load();
    private String BOT_TOKEN = dotenv.get("MY_BOT_TOKEN");

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        boolean isReply = false;

        if (update.getMessage().isReply()) {
            isReply = true;
        }

        String[] partesDelComando = new String[2];
        String comandoPrincipal = new String();
        String restoDelMensaje = new String();

        partesDelComando = command.split(" ", 2);
        if (partesDelComando.length == 1) {
            comandoPrincipal = command;
        } else {
            comandoPrincipal = partesDelComando[0];
            restoDelMensaje = partesDelComando[1];
        }

        comandoPrincipal = comandoPrincipal.toLowerCase();

        String chatId = String.valueOf(update.getMessage().getChatId());
        message.setChatId(chatId);

        if (isReply) {
            Message mensajeReferenciado = update.getMessage().getReplyToMessage();
            Thread trabajadorDeImagen = new TrabajadorDeImagen(this, mensajeReferenciado, chatId,
                    comandoPrincipal);
            trabajadorDeImagen.start();
        } else {
            switch (comandoPrincipal) {
                case "nr":
                case "numrand":
                    String[] partsV = restoDelMensaje.split(" ");
                    int x1 = Integer.parseInt(partsV[0]);
                    int x2 = Integer.parseInt(partsV[1]);
                    message.setText("" + Sorteador.generarNumeroAleatorio(x1, x2));
                    break;
                case "ee":
                case "elegirentre":
                    message.setText("Elijo: " + Sorteador.elegirEntre(restoDelMensaje));
                    break;
                case "mz":
                case "mezclar":
                    String mezcla = Sorteador.darFormatoSalidaLista(Sorteador.mezclar(restoDelMensaje));
                    message.setText(mezcla);
                    break;
                case "re":
                case "repartirentre":
                    message.setText(Sorteador.repartirEntre(restoDelMensaje));
                    break;
                case "/help":
                    try {
                        enviarDocumento(new InputFile(new File("data/informacionComandos/Guia_NaviBot.pdf")), chatId);
                    } catch (Exception e) {
                        System.out.println("Error enviando Guia_NaviBot.pdf " + e);
                    }
                    break;
                case "dibujartexto":
                    Thread trabajadorDeImagen = new TrabajadorDeImagen(this, chatId,
                            command);
                    trabajadorDeImagen.start();
                    break;
                case "temporizador":
                case "temp":
                case "recordar":
                    String[] partT = restoDelMensaje.split(" - ");
                    String[] partD = partT[1].split("d ");
                    String[] partH = partD[1].split("h ");
                    String[] partM = partH[1].split("m");

                    Thread t = new Temporizador(partT[0], Integer.parseInt(partM[0]), Integer.parseInt(partH[0]),
                            Integer.parseInt(partD[0]), this, message, chatId);
                    Thread.currentThread();
                    System.out.println("Los hilos activos son: " + Thread.activeCount());
                    message.setText("¡Lo tengo!");
                    t.start();
                    break;
                case "ppt":
                case "jugar":
                    this.jugarPPT(restoDelMensaje, chatId);
                    break;
                case "navi":
                    String etiqueta = GestorPalabrasRespuesta.localizarEtiqueta(arrayPalabras,
                            restoDelMensaje.toLowerCase());

                    String palabraRespuesta = GestorPalabrasRespuesta.getPalabraRespuesta(arrayPalabras, etiqueta);
                    palabraRespuesta = palabraRespuesta.toUpperCase().charAt(0)
                            + palabraRespuesta.substring(1, palabraRespuesta.length()).toLowerCase();

                    message.setText(palabraRespuesta);
                    break;
                default:

            }

        }

        enviarMensaje(message);
    }

    @Override
    public String getBotUsername() {
        return "OfficialNavi_bot";
    }

    public void enviarDocumento(InputFile documento, String chatId) throws Exception {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(documento);
        sendDocument.setChatId(chatId);
        execute(sendDocument);
    }

    public void enviarImagen(InputFile imagen, String chatId) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(imagen);
            sendPhoto.setChatId(chatId);
            execute(sendPhoto);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    public void enviarMensaje(SendMessage message) {
        try {
            if (!message.getText().isEmpty()) {
                execute(message);
            }
        } catch (TelegramApiException ex) {
            System.out.println(ex);
        }

    }

    public void jugarPPT(String restoDelMensaje, String chatId) {
        int eleccionUsuario = 0; // piedra = 1, papel = 2, tijera = 3

                    if (restoDelMensaje.toLowerCase().equals("piedra")) {
                        eleccionUsuario = 1;
                    } else {
                        if (restoDelMensaje.toLowerCase().equals("papel")) {
                            eleccionUsuario = 2;
                        } else {
                            if (restoDelMensaje.toLowerCase().equals("tijera")
                                    || restoDelMensaje.toLowerCase().equals("tijeras")) {
                                eleccionUsuario = 3;
                            }
                        }
                    }
                    int eleccionBot = Sorteador.generarNumeroAleatorio(1, 3);
                    String emoticonEleccionBot = new String();
                    switch (eleccionBot) {
                        case 1:
                            emoticonEleccionBot = "👊🏼";
                            break;
                        case 2:
                            emoticonEleccionBot = "✋🏼";
                            break;
                        case 3:
                            emoticonEleccionBot = "✌🏼";
                            break;
                        default:
                            break;
                    }
                    if (eleccionBot == eleccionUsuario) {
                        enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                        enviarMensaje(new SendMessage(chatId, "Hemos elegido lo mismo. ^^"));
                    } else {
                        if (eleccionUsuario == 1 && eleccionBot == 2) {
                            enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                            enviarMensaje(new SendMessage(chatId, "Es hora de documentar tu derrota. ^^"));
                        } else {
                            if (eleccionUsuario == 2 && eleccionBot == 3) {
                                enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                                enviarMensaje(new SendMessage(chatId,
                                        "A que he cortado tu racha, espera... ¿Siquiera tenias una?"));
                            } else {
                                if (eleccionUsuario == 3 && eleccionBot == 1) {
                                    enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                                    enviarMensaje(new SendMessage(chatId, "Una dura derrota ¿no? ^^"));
                                } else {
                                    if (eleccionUsuario < 1 || eleccionUsuario > 3) {
                                        String respuestaAleatoria = "";
                                        for (int i = 0; i < 10; i++) {
                                            respuestaAleatoria += "" + Sorteador.generarNumeroAleatorio(0, 1);
                                        }
                                        enviarMensaje(new SendMessage(chatId,
                                                "No entiendo tu elección, así que también te daré algo lo cual no puedas entender: "
                                                        + respuestaAleatoria));
                                    } else {
                                        enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                                        enviarMensaje(new SendMessage(chatId, "Tu ganas."));
                                    }
                                }
                            }
                        }
                    }
    }

}
