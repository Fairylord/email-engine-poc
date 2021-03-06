package test.email;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by HUANGYE2 on 9/6/2017.
 * <p>
 * Test case for mailbox connection
 */
public class TestConnectToMailbox {

    public static void main(String[] args) {

        String host = "hkgcas";
        String userName = "oocldm\\ooclsagcsi";
        String password = "jKpE8AKh";

        boolean debugMode = false;   // ON/OFF Debug Mode

        IMAPStore store = null;
        IMAPFolder inbox = null;

        try {
            // 1. Connect
            store = connect(host, userName, password, debugMode);

            // 2. Open folder
            inbox = openFolder("INBOX", store);
            System.out.println("No. of Unread Messages : " + inbox.getUnreadMessageCount());
            System.out.println("No. of Messages : " + inbox.getMessageCount());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shutdown
            try {
                inbox.close(true);
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connect to mailbox and authorize username/password.
     *
     * @param host
     * @param userName
     * @param password
     * @return
     * @throws MessagingException
     */
    public static IMAPStore connect(String host, String userName, String password, boolean debugMode) throws MessagingException {
        Session session = Session.getInstance(getProperties());
        session.setDebug(debugMode);   // Debug mode to print out all commands.
        IMAPStore store = (IMAPStore) session.getStore("imap");

        long startTime = System.currentTimeMillis();
        store.connect(host, userName, password);
        long endTime = System.currentTimeMillis();
        System.out.println("Connected to mailbox in " + (endTime - startTime) + " ms.");
        return store;
    }

    /**
     * Open the specified mail folder
     *
     * @param folderName
     * @param store
     * @return
     * @throws MessagingException
     */
    public static IMAPFolder openFolder(String folderName, IMAPStore store) throws MessagingException {
        long startTime = System.currentTimeMillis();
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        long endTime = System.currentTimeMillis();
        System.out.println("open folder in " + (endTime - startTime) + " ms.");
        return folder;
    }

    public static Properties getProperties() {
        Properties p = System.getProperties();
        p.setProperty("mail.store.protocol", "imap");
        p.setProperty("mail.imap.timeout", "300000");// 5 mins
        p.setProperty("mail.imap.connectiontimeout", "600000");// 10 mins
//        p.setProperty("mail.imap.fetchsize", "2097152");  // No use if partialfetch = false
        p.setProperty("mail.imap.partialfetch", "false");

        return p;
    }
}
