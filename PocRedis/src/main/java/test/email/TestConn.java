package test.email;


import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

public class TestConn {
    public static void main(String[] args) {

        Properties properties = getProperties();

        try {

            Session session = Session.getInstance(properties);

            Store store = session.getStore("imap");

            store.connect("hkgcas", 143, "oocldm\\ooclsagcsi", "jKpE8AKh");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            System.out.println(inbox);

            inbox.close(true);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Properties getProperties() {
        Properties p = System.getProperties();
        p.setProperty("mail.store.protocol", "imap");
        p.setProperty("mail.imap.timeout", "300000");// 5 mins
        p.setProperty("mail.imap.connectiontimeout", "600000");// 10 mins
        return p;
    }
}