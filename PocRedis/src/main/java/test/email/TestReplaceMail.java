package test.email;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * Created by HUANGYE2 on 9/6/2017.
 * <p>
 * Test case for search mails by MsgID(s)
 */
public class TestReplaceMail {

    public static void main(String[] args) {

        String host = "hkgcas";
        String userName = "oocldm\\ooclsagcsi";
        String password = "jKpE8AKh";

        boolean debugMode = false;   // ON/OFF Debug Mode

        IMAPStore store = null;
        IMAPFolder inbox = null;

        /* Use a suitable FetchProfile */
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);

        // Use TestListAllMails.java to view the MsgID of mails.
        String msgID = "<HK2PR02MB1331396DD2AB9AE8227D024DE5970@HK2PR02MB1331.apcprd02.prod.outlook.com>";

        try {
            // 1. Connect
            store = connect(host, userName, password, debugMode);

            // 2. Open folder
            inbox = openFolder("INBOX", store);
            System.out.println("No. of Unread Messages : " + inbox.getUnreadMessageCount());
            System.out.println("No. of Messages : " + inbox.getMessageCount());

            // 3. Fetch mails
            IMAPMessage mail = searchMailByMsgID(inbox, msgID ,fp);
            if(mail != null) {
                System.out.println(mail.getMessageNumber() + "  -  " +  mail.getSubject() + "  -  " +  mail.getMessageID());
                // Replace the mail and get the MsgID of new one!
                String newMailMsgID = cloneAndReplaceMail(inbox, mail);
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
     * Fetch mail from folder by MsgID
     * @param folder
     * @param fp
     * @return
     * @throws MessagingException
     */
    public static IMAPMessage searchMailByMsgID(IMAPFolder folder, String msgID, FetchProfile fp) throws MessagingException {
        long startTime = System.currentTimeMillis();
        // Search by MsgID
        Message[] originalMails = folder.search(new MessageIDTerm(msgID));
        if(originalMails != null && originalMails.length > 0) {
            IMAPMessage[] imapMails = Arrays.copyOf(originalMails, originalMails.length, IMAPMessage[].class);
            folder.fetch(imapMails, fp);
            long endTime = System.currentTimeMillis();
            System.out.println("Fetch " + imapMails.length + " mails in " + (endTime - startTime) + " ms.");
            return imapMails[0];
        }
        else {
            long endTime = System.currentTimeMillis();
            System.out.println("Fetch No mails in " + (endTime - startTime) + " ms.");
            return null;
        }
    }

    /**
     * Clone an old mail and replace it.
     * @param folder
     * @param source
     * @return
     * @throws MessagingException
     * @throws ParseException
     */
    public static String cloneAndReplaceMail(IMAPFolder folder, IMAPMessage source) throws MessagingException, ParseException {
        String oldMailMsgID = source.getMessageID();
        long startTime = System.currentTimeMillis();

        MimeMessage newMail =  new MimeMessage(source);

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
        Date date = df.parse( "2017-09-06 11:11:11.1" );

        newMail.setSentDate(date);
        newMail.setSubject(source.getSubject() + "_X");
        newMail.setFlag(Flags.Flag.SEEN, false);
        newMail.saveChanges();
        folder.appendMessages(new Message[]{newMail});

        source.setFlag(Flags.Flag.DELETED,true);
        folder.expunge();
        long endTime = System.currentTimeMillis();
        System.out.println("Replaced 1 mail in " + (endTime - startTime) + " ms.");
        System.out.println("Old Mail MsgID: " + oldMailMsgID);
        System.out.println("New Mail MsgID: " + newMail.getMessageID());
        return newMail.getMessageID();
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
