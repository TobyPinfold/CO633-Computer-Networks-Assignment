// MessageSender.java - PARTIAL IMPLEMENTATION

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class implements the sender side of the data link layer.
 * <P>
 * The source code supplied here only contains a partial implementation. 
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the sendMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 */

public class MessageSender
{
    // Fields ----------------------------------------------------------

    private int mtu;                    // maximum transfer unit (frame length limit)
    private FrameSender physicalLayer;  // physical layer object
    private TerminalStream terminal;    // terminal stream manager

    // DO NOT ADD ANY MORE INSTANCE VARIABLES
    // but it's okay to define constants here

    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new MessageSender.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageSender(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameSender();
        this.terminal = new TerminalStream("MessageSender");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a single message - THIS IS THE ONLY METHOD YOU NEED TO MODIFY
     * @param message the message to be sent.  The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if, and only if, the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol including the MTU
     */

    public void sendMessage(String message) throws ProtocolException
    {
        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        // YOUR CODE SHOULD START HERE ---------------------------------
        // No changes are needed to the statements above

        try {

            message = message.replaceAll(":", "::");
            terminal.printlnDiag("  sendMessage starting (message = \"" + message + "\")");

            // The following statement shows how the frame sender is invoked.
            // At the moment it just passes a fixed string.
            // sendMessage should split large messages into several smaller
            // segments.  Each segment must be encoded as a frame in the
            // format specified.  sendFrame will need to be called separately
            // for each frame in turn.  See the coursework specification
            // and other class documentation for further info.
            int reservedFrameSpace = 8;

            ArrayList<String> frames = new ArrayList<>();

            int dataExceedFrameByAmount = mtu - (message.length() + reservedFrameSpace);
            boolean dataOverflowsFrame = dataExceedFrameByAmount < 0;

            if (dataOverflowsFrame) {

                List<String> splitMessages = splitMessageByFrameSize(message, reservedFrameSpace, mtu);

                System.out.println(splitMessages.size());

                for (int i = 0; i < splitMessages.size(); i++) {

                    System.out.println(splitMessages.get(i));

                    boolean isEnd = i == (splitMessages.size() - 1);

                    frames.add(createFrame(splitMessages.get(i), isEnd));
                }

            } else {

                frames.add(createFrame(message, true));

            }


            for (String frame : frames) {
                physicalLayer.sendFrame(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // YOUR CODE SHOULD FINISH HERE --------------------------------
        // No changes are needed to the statements below

        // Report completion of task

        terminal.printlnDiag("  sendMessage finished");

    } // end of method sendMessage



    private String createFrame(String message, boolean isEnd) {

        boolean isMessageEmpty = message.isEmpty();

        String checksum = !isMessageEmpty ? generateChecksum(message) : "000";
        String EoMFlag = isEnd ? "." : "+";
        String frame = "(" + message + ":" + checksum + ":" + EoMFlag + ")";

        return frame;
    }



    private String generateChecksum(String message) {

        char[] messageAsArray = message.replaceAll("::", ":").toCharArray();

        int sum = 0;

        for(int i =0; i < messageAsArray.length; i++) {
            sum += (int)messageAsArray[i];
        }

        String sumAsString = "" + sum;

        if(sumAsString.length() > 3) {
            sumAsString.substring(sumAsString.length()-3);
        }

        if(sumAsString.length() < 3) {
            int length = sumAsString.length();
            String padding = "";
            for (int i = 3 -length; i > 0; i--) {
                padding += "0";
            }

            sumAsString = padding + sumAsString;
        }
        return sumAsString;
    }



    private List<String> splitMessageByFrameSize(String message, int reservedFrameSpace, int mtu) {

        List<String> messages = new ArrayList<>();

        int sizeAllowed = (mtu - reservedFrameSpace);


        for(int i = 0; i < message.length(); i += sizeAllowed ? ) {


            //TODO fix issue whereby a colon is skipped at the end due to the indexing

            boolean islastIndex = message.length() < i + sizeAllowed;

            int endRange = islastIndex ? message.length() : i+sizeAllowed;

            boolean isEndColonAPair = message.substring(endRange-1, endRange).equals("::");

            if(!isEndColonAPair) {
                endRange--;
            }

            String splitMessage = message.substring(i, endRange);

            messages.add(splitMessage);
        }

        System.out.println("sizeOfMessages = " + messages.size());

        return messages;
    }



    // You may add private methods if you wish


} // end of class MessageSender

