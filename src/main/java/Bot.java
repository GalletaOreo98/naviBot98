import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import io.github.cdimascio.dotenv.Dotenv;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;

public class Bot extends TelegramLongPollingBot {

    private String[] comandosInfo = { "numRand n1 n2\nDevuelve un numero aleatorio entre x1 y x2",
            "elegirEntre e1, e2, e3,...en\nElije un elemento entre la lista de elementos que se proporcione",
            "mezclar e1, e2, e3,...en\nMezcla aleatoriamente los elementos de la lista proporcionada y la devuelve",
            "repartirEntre s1, s2, s3,...sn - e1, e2, e3,...en\nReparte entre los n sujetos los n elementos de manera igualitaria y aleatoria y devuelve la lista",
            "HelloImage\nDevuelve una imagen", "Hello\nDevuelve un sticker", "Voice\nDevuelve un Audio de voz",
            "Adios\nDevuelve una despedida" };
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
            Thread trabajdaorDeImagen = new TrabajadorDeImagen(this, mensajeReferenciado, chatId,
                    command.toLowerCase());
            trabajdaorDeImagen.start();
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
                case "voice":
                    enviarVoiceDesdeLocalServer("voice/db.mp3", chatId);
                    break;
                case "help":
                    message.setText(getInfoComados());
                    break;
                case "dibujartexto":
                    dibujarImagenTexto(chatId, restoDelMensaje, 330, 30, 30);
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
                                    enviarMensaje(new SendMessage(chatId, "Elijo: " + emoticonEleccionBot));
                                    enviarMensaje(new SendMessage(chatId, "Tu ganas."));
                                }
                            }
                        }
                    }

                default:
                    // String etiqueta = GestorPalabrasRespuesta.localizarEtiqueta(arrayPalabras,
                    // comandoPrincipal);
                    // message.setText(GestorPalabrasRespuesta.getPalabraRespuesta(arrayPalabras,
                    // etiqueta));
            }

        }

        enviarMensaje(message);
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
            System.out.println("Error al acceder a la imagen:");
        }
        return new InputFile(image);
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

    public void enviarImagenDesdeLocalServer(String rutaImagen, String chatId) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(getImageAsInpuFile(rutaImagen));
            sendPhoto.setChatId(chatId);
            execute(sendPhoto);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    public void enviarStickerDesdeLocalServer(String rutaSticker, String chatId) {
        try {
            SendSticker sendSticker = new SendSticker();
            sendSticker.setSticker(getImageAsInpuFile(rutaSticker));
            sendSticker.setChatId(chatId);
            execute(sendSticker);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    public void enviarVoiceDesdeLocalServer(String rutaAudio, String chatId) {
        try {
            SendVoice sendVoice = new SendVoice();
            sendVoice.setVoice(getImageAsInpuFile(rutaAudio));
            sendVoice.setChatId(chatId);
            execute(sendVoice);
        } catch (Exception h) {
            System.out.println(h);
        }
    }

    private String getInfoComados() {
        String comandosString = "";
        for (int i = 0; i < comandosInfo.length; i++) {
            comandosString = comandosString + (i + 1) + ". " + comandosInfo[i] + "\n";
        }
        return comandosString;
    }

    public void dibujarImagenTexto(String chatId, String texto, int lineWidth, int x, int y) {
        try {
            BufferedImage imagen = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2;

            g2 = imagen.createGraphics();
            g2.setColor(Color.white);
            g2.fillRect(0, 0, 400, 400);
            g2.setColor(Color.black);

            int aum = g2.getFontMetrics().getHeight();
            int contadorSaltos;
            String[] lineasTexto = texto.split("\n");

            for (int i = 0; i < lineasTexto.length; i++) {
                contadorSaltos = dibujarLineaTexto(lineasTexto[i], g2, lineWidth, x, y);
                y += aum * contadorSaltos;
            }

            File file = new File("prueba.png");
            ImageIO.write(imagen, "png", file);
            InputFile inputFile = new InputFile(file);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setChatId(chatId);
            execute(sendPhoto);
        } catch (Exception e) {
            System.out.println(e);
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

    private int dibujarLineaTexto(String texto, Graphics2D g2, int lineWidth, int x, int y) {
        int c = 1;
        FontMetrics m = g2.getFontMetrics();
        if (m.stringWidth(texto) < lineWidth) {
            g2.drawString(texto, x, y);
        } else {
            String[] words = texto.split(" ");
            String currentLine = words[0];
            for (int i = 1; i < words.length; i++) {
                if (m.stringWidth(currentLine + words[i]) < lineWidth) {
                    currentLine += " " + words[i];
                } else {
                    g2.drawString(currentLine, x, y);
                    y += m.getHeight();
                    currentLine = words[i];
                    c += 1;
                }
            }
            if (currentLine.trim().length() > 0) {
                g2.drawString(currentLine, x, y);
            }
        }
        return c;
    }

}
