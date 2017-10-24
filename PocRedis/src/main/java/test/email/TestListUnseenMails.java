package test.email;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by HUANGYE2 on 9/6/2017.
 * <p>
 * Test case for list all unseen mails in a folder
 */
public class TestListUnseenMails {

    public static void main(String[] args) {

        String host = "hkgcas";
        String userName = "oocldm\\ooclsagcsi";
        String password = "jKpE8AKh";

        boolean debugMode = true;   // ON/OFF Debug Mode

        IMAPStore store = null;
        IMAPFolder inbox = null;

        /* Use a suitable FetchProfile */
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);

        try {
            // 1. Connect
            store = connect(host, userName, password, debugMode);

            // 2. Open folder
            inbox = openFolder("INBOX", store);
            System.out.println("No. of Unread Messages : " + inbox.getUnreadMessageCount());
            System.out.println("No. of Messages : " + inbox.getMessageCount());

            // 3. Fetch unseen mails
            IMAPMessage[] mails = getUnseenMails(inbox, fp);
            for (IMAPMessage mail : mails) {
                System.out.println(mail.getMessageNumber() + "  -  " +  mail.getSubject() + "  -  " +  mail.getMessageID());
            }
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

    /**
     * Fetch unseen mails from folder
     * @param folder
     * @param fp
     * @return
     * @throws MessagingException
     */
    public static IMAPMessage[] getUnseenMails(IMAPFolder folder, FetchProfile fp) throws MessagingException {
        long startTime = System.currentTimeMillis();
        // Search UNSEEN mails by searching by flags
        Message[] originalMails = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        IMAPMessage[] imapMails = Arrays.copyOf(originalMails, originalMails.length, IMAPMessage[].class);
        folder.fetch(imapMails, fp);
        long endTime = System.currentTimeMillis();
        System.out.println("Fetch " + imapMails.length + " mails in " + (endTime - startTime) + " ms.");
        return imapMails;
    }

    public static Properties getProperties() {
        Properties p = System.getProperties();
        p.setProperty("mail.store.protocol", "imap");
        p.setProperty("mail.imap.timeout", "300000");// 5 mins
        p.setProperty("mail.imap.connectiontimeout", "5000");// 10 mins
//        p.setProperty("mail.imap.fetchsize", "2097152");  // No use if partialfetch = false
        p.setProperty("mail.imap.partialfetch", "false");

        return p;
    }
}
