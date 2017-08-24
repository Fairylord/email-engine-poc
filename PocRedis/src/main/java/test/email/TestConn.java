package test.email;


import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import javax.mail.search.MessageIDTerm;
import java.util.Properties;

public class TestConn {
    public static void main(String[] args) {

        Properties properties = getProperties();

        try {
            Session session = Session.getInstance(properties);
//            session.setDebug(true);
            Store store = session.getStore("imap");

            // 1. Connect
            long startTime = System.currentTimeMillis();
            store.connect("hkgcas", "oocldm\\ooclsagcsi", "jKpE8AKh");
            long endTime = System.currentTimeMillis();
            System.out.println("Connect mailbox in " + (endTime - startTime) + " ms.");

            // 2. Open folder
            startTime = System.currentTimeMillis();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            endTime = System.currentTimeMillis();
            System.out.println("open mailbox in " + (endTime - startTime) + " ms.");
            System.out.println("No. of Unread Messages : " + inbox.getUnreadMessageCount());
            System.out.println("No. of Messages : " + inbox.getMessageCount());

            /* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);

            // 3. Read All mails
//            startTime = System.currentTimeMillis();
////            // Read mails
////            Message messages[] = inbox.search(new FlagTerm(new Flags(
////                    Flags.Flag.SEEN), false));
////            System.out.println("No. of Unread Messages : " + messages.length);
////
//            Message messages[] = inbox.getMessages();
//            System.out.println("No. of All Messages : " + inbox.getMessageCount());
//
//            inbox.fetch(messages, fp);
//            endTime = System.currentTimeMillis();
//            System.out.println("get All mail in " + (endTime - startTime) + " ms.");
//
//            for (Message message : messages) {
//                System.out.println(message.getMessageNumber() + "  -  " +  message.getSubject());
//            }


            // 4. Read a mail by msgNo.
//            startTime = System.currentTimeMillis();
//            Message lastMessage = inbox.getMessage(356);
//            inbox.fetch(new Message[]{lastMessage}, fp);
//            System.out.println(lastMessage.getSubject());
//            System.out.println(lastMessage.getSentDate());
//            System.out.println(lastMessage.getFlags());
//            endTime = System.currentTimeMillis();
//            System.out.println("get 1 mail in " + (endTime - startTime) + " ms.");


            // 5. Search a mail by ID
            startTime = System.currentTimeMillis();
            Message messagesByID[] = inbox.search(new MessageIDTerm("<511f41320d2c46fb977b804ae7fd38e1@HKGMAIL26.corp.oocl.com>"));
            endTime = System.currentTimeMillis();
            System.out.println("Search 1 mail by ID in " + (endTime - startTime) + " ms.");
            inbox.fetch(messagesByID, fp);
            for (Message message : messagesByID) {
                IMAPMessage imapMsg = (IMAPMessage)message;
                System.out.println(imapMsg.getMessageNumber() + "  -  " +  imapMsg.getSubject());
                System.out.println(imapMsg.getMessageID());
            }


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
        p.setProperty("mail.imap.fetchsize", "256k");

//        p.put("mail.imap.ssl", true);
//        p.put("mail.imap.starttls.enable", true);
//        p.put("mail.imap.auth", true);
//        p.put("mail.imap.ssl.trust", "*");
//        p.put("mail.imap.ssl.enable", true);
//        p.put("mail.imap.auth.plain.disable", true);
//        p.put("mail.imap.auth.ntlm.disable", true);
//        p.put("mail.imap.auth.gssapi.disable", true);

        return p;
    }
}