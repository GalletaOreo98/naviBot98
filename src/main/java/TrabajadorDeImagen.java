import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import net.coobird.thumbnailator.Thumbnails;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;
import java.awt.*;
import java.io.*;

public class TrabajadorDeImagen extends Thread {
    Bot bot;
    Message mensajeReferenciado;
    String chatId;
    String comando;

    public TrabajadorDeImagen(Bot bot, Message mensajeReferenciado, String chatId, String comando) {
        this.bot = bot;
        this.mensajeReferenciado = mensajeReferenciado;
        this.chatId = chatId;
        this.comando = comando;
    }

    public void run() {
        String comandoPrincipal;
        String restoDelComando = new String();
        String[] partesDelComando = comando.split(" ", 2);
        if (partesDelComando.length == 1) {
            comandoPrincipal = comando;
        } else {
            comandoPrincipal = partesDelComando[0];
            restoDelComando = partesDelComando[1];
        }

        switch (comandoPrincipal) {
            case "rz":
            case "resize":
                bot.enviarMensaje(new SendMessage(chatId, "¡Trabajando reescalado de imagen!"));
                resizeImagen(restoDelComando);
                break;
            case "enmarcar":
                bot.enviarMensaje(new SendMessage(chatId, "¡Trabajando enmarcado!"));
                enmarcarImagen();
            default:
                break;
        }
    }

    private void enmarcarImagen() {
        try {
            if (mensajeReferenciado.hasDocument()) {
                String doc_name = mensajeReferenciado.getDocument().getFileName();

                Document document = mensajeReferenciado.getDocument();

                GetFile getFile = new GetFile();
                getFile.setFileId(document.getFileId());

                org.telegram.telegrambots.meta.api.objects.File TelegramFile = bot.execute(getFile);

                BufferedImage imBuff = ImageIO.read(bot.downloadFileAsStream(TelegramFile));
                if (imBuff != null) {
                    int width;
                    if (imBuff.getHeight() < imBuff.getWidth()) {
                        width = imBuff.getHeight();
                    } else {
                        width = imBuff.getWidth();
                    }

                    BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = circleBuffer.createGraphics();

                    g2.setClip(new Ellipse2D.Float(0, 0, width, width));
                    g2.drawImage(imBuff, 0, 0, width, width, (int) (imBuff.getWidth() * 0.5 - width * 0.5), 0,
                            (int) (imBuff.getWidth() * 0.5 + width * 0.5), width, null);

                    int r = Sorteador.generarNumeroAleatorio(0, 255);
                    int g = Sorteador.generarNumeroAleatorio(0, 255);
                    int b = Sorteador.generarNumeroAleatorio(0, 255);
                    g2.setColor(new Color(r, g, b));
                    g2.setStroke(new BasicStroke(width * 0.15f));
                    g2.drawOval(0, 0, width, width);

                    InputFile inputFile = new InputFile();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(circleBuffer, "png", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    inputFile.setMedia(is, doc_name);
                    bot.enviarDocumento(inputFile, chatId);
                } else {
                    bot.enviarMensaje(new SendMessage(chatId, "Mejor intenta con una imagen."));
                }

            } else {
                if (mensajeReferenciado.getPhoto() != null) {
                    bot.enviarMensaje(new SendMessage(chatId, "Sería mejor si la imagen esta enviada como archivo."));
                } else {
                    bot.enviarMensaje(new SendMessage(chatId, "Supongo que es una broma."));
                }
            }

        } catch (Exception e) {
            System.out.println("Algo muy inesperado " + e.getMessage());
        }
    }

    private void resizeImagen(String dimensiones) {
        try {
            String[] part = dimensiones.split(" ");
            int w = Integer.parseInt(part[0]);
            int h = Integer.parseInt(part[1]);
            if (mensajeReferenciado.hasDocument()) {
                String doc_name = mensajeReferenciado.getDocument().getFileName();

                Document document = mensajeReferenciado.getDocument();

                GetFile getFile = new GetFile();
                getFile.setFileId(document.getFileId());

                org.telegram.telegrambots.meta.api.objects.File TelegramFile = bot.execute(getFile);

                BufferedImage imBuff = ImageIO.read(bot.downloadFileAsStream(TelegramFile));
                if (imBuff != null) {
                    BufferedImage imagen = Thumbnails.of(imBuff).size(w, h).asBufferedImage();
                    InputFile inputFile = new InputFile();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(imagen, "png", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    inputFile.setMedia(is, doc_name);
                    bot.enviarDocumento(inputFile, chatId);
                } else {
                    bot.enviarMensaje(new SendMessage(chatId,
                            "No creo poder cambiar las dimeciones de eso, intenta con una imagen."));
                }

            } else {
                if (mensajeReferenciado.getPhoto() != null) {
                    bot.enviarMensaje(new SendMessage(chatId, "Sería mejor si la imagen esta enviada como archivo."));
                } else {
                    bot.enviarMensaje(new SendMessage(chatId, "Supongo que es una broma."));
                }
            }

            // downloadFile(TelegramFile, new File(getID + "_" + doc_name));
            // para guardar localmente

        } catch (Exception e) {
            System.out.println(e.getMessage());
            bot.enviarMensaje(new SendMessage(chatId, "¡Ops! Algo salio mal."));
        }
    }
}
