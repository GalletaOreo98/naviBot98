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
    public TrabajadorDeImagen(Bot bot, String chatId, String comando) {
        this.bot = bot;
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
        comandoPrincipal = comandoPrincipal.toLowerCase();

        switch (comandoPrincipal) {
            case "rz":
            case "resize":
                bot.enviarMensaje(new SendMessage(chatId, "¡Trabajando reescalado de imagen!"));
                resizeImagen(restoDelComando.toLowerCase());
                break;
            case "enmarcar":
                bot.enviarMensaje(new SendMessage(chatId, "¡Trabajando enmarcado!"));
                enmarcarImagen();
                break;
            case "dibujartexto":
            bot.enviarMensaje(new SendMessage(chatId, "¡Trabajando en la imagen con texto!"));
            dibujarImagenTexto(restoDelComando, 330, 30, 30);
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

    private void dibujarImagenTexto(String texto, int lineWidth, int x, int y) {
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

            InputFile inputFile = new InputFile();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(imagen, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            inputFile.setMedia(is, "TextoImagen");

            bot.enviarImagen(inputFile, chatId);

        } catch (Exception e) {
            System.out.println(e);
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
