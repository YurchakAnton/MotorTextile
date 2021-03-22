package com.example.motortextile.MailSend;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.motortextile.Admin.AdminNewOrdersActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.sql.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.sql.DataSource;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {

    private Context context;

    private Session session;
    private String email, subject, userName, userPhone, userCity, userDepartment, userVarPay, mid;

    public JavaMailAPI(Context context, String email, String subject, String userName, String userPhone, String userCity, String userDepartment, String userVarPay, String mid) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userCity = userCity;
        this.userDepartment = userDepartment;
        this.userVarPay = userVarPay;
        this.mid = mid;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConstants.SENDER_EMAIL, MailConstants.SENDER_PASSWORD);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(MailConstants.SENDER_EMAIL));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);

            Multipart _multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            InputStream is = context.getAssets().open("user_profile.html");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String str = new String(buffer);
            str =str.replace("$$name$$", userName);
            str =str.replace("$$headermessage$$","Ви оформили замовлення.");
            str=str.replace("$$username$$", userName);
            str=str.replace("$$email$$", email);
            str=str.replace("$$phone$$", userPhone);
            str=str.replace("$$city$$",userCity);
            str=str.replace("$$number_np$$",userDepartment);
            str=str.replace("$$var_pay$$", userVarPay);
            messageBodyPart.setContent(str,"text/html; charset=utf-8");
            BodyPart messagePart = new MimeBodyPart();
            String filepath = "/storage/emulated/0/Android/data/mt.example.motortextile/files/"+mid+".pdf";
            FileDataSource source = new FileDataSource(filepath);
            messagePart.setDataHandler(new DataHandler(source));
            messagePart.setFileName(filepath);
            _multipart.addBodyPart(messageBodyPart);
            _multipart.addBodyPart(messagePart);
            mimeMessage.setContent(_multipart);

            Transport.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}