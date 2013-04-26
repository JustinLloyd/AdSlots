import java.io.*;
import java.net.*;
import java.applet.*;


public class SendMail
	{
//	private	final static	String	RCPT_TO = "jlloyd@electromedia.com" ;

	private	String	sSenderName,
					sSenderHost,
					sMailHost,
					sRecipient,
					sMessage ;


	public void SetRecipient(String sRecipient)
		{
		this.sRecipient = sRecipient ;
		}


	public void SetSenderHost(String sSenderHost)
		{
		this.sSenderHost = sSenderHost ;
		}


	public void SetSenderName(String sSenderName)
		{
		this.sSenderName = sSenderName ;
		}


	public void SetMailHost(String sMailHost)
		{
		this.sMailHost = sMailHost ;
		}


	public void SetMessage(String sMessage)
		{
		this.sMessage = sMessage + "\r\n" ;
		}


	public void AddMessage(String sMessage)
		{
		this.sMessage += sMessage ;
		}


	public void AddMessageLine(String sMessage)
		{
		this.sMessage += sMessage + "\r\n" ;
		}


	public boolean Send()
		{
		Socket          socketSMTP ;
	
		InputStream     isInput ;

		OutputStream    osOutput ;

		DataInputStream disInput ;

		PrintStream     psOutput ;

		int	nSMTPport =  25 ;

		String	sServerReply = new String(),
				sHELO = "HELO " + sSenderHost,
				sMAILFROM = "MAIL FROM:" + sSenderName + "@" + sSenderHost,
				sRCPTTO = "RCPT TO:" + sRecipient,
				sDATA = "DATA",
 				sEndMessage = "\r\n.\r\n",							// you must terminate your message string with "\r\n.\r\n" to indicate end of message.
				OKCmd = "220|250" ;									// mailhost returns either "220" or "250" to indicate everything went OK 

		// connect to the mail server 
//		System.out.println("Connecting to " + sMailHost + "...") ;
//		System.out.flush() ;

		try
			{
			socketSMTP = new Socket(sMailHost, nSMTPport) ;
			}
		
		catch (IOException e)
			{
//			System.out.println("Error opening socket.") ;
			return (false) ;
			}

		try
			{
			isInput = socketSMTP.getInputStream() ;
			disInput = new DataInputStream(isInput) ;

			osOutput= socketSMTP.getOutputStream() ;
			psOutput = new PrintStream(osOutput) ;
			}

		catch (IOException e)
			{
//			System.out.println("Error opening inputstream.") ;
			return (false) ;
			}
      
		// OK, we're connected, let's be friendly and say hello to the mail server...
		psOutput.println(sHELO) ;
		psOutput.flush() ;
//		System.out.println("Sent: " + sHELO) ;

		try
			{
			sServerReply = disInput.readLine() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error reading from socket.") ;
			return (false) ;
			}

//		System.out.println("Received: " + sServerReply) ;
   
		// let server know YOU wanna send mail...
		psOutput.println(sMAILFROM) ;
		psOutput.flush() ;
//		System.out.println("Sent: " + sMAILFROM) ;

		try
			{
			sServerReply = disInput.readLine() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error reading from socket.") ;
			return (false) ;
			}

//		System.out.println("Received: " + sServerReply) ;
  
		// let server know WHOM you're gonna send mail to...
		psOutput.println(sRCPTTO) ;
		psOutput.flush() ;
//		System.out.println("Sent: " + sRCPTTO) ;

		try
			{
			sServerReply = disInput.readLine() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error reading from socket.") ;
			return (false) ;
			}

//		System.out.println("Received: " + sServerReply) ;

		// let server know you're now gonna send the message contents...
		psOutput.println(sDATA) ;
		psOutput.flush() ;
//		System.out.println("Sent: " + sDATA) ;

		try
			{
			sServerReply = disInput.readLine() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error reading from socket.") ;
			return (false) ;
			}

//		System.out.println("Received: " + sServerReply) ;

		// finally, send the message...
		psOutput.println(sMessage + sEndMessage) ;
		psOutput.flush() ;
//		System.out.println("Sent: " + sMessage + sEndMessage) ;

		try
			{
			sServerReply = disInput.readLine() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error reading from socket.") ;
			return (false) ;
			}

//		System.out.println("Received: " + sServerReply);

		// we're done, disconnect from server
//		System.out.print("Disconnecting...");
		try
			{
			socketSMTP.close() ;
			}

		catch (IOException e)
			{
//			System.out.println("Error closing socket.") ;
			return (false) ;
			}

//		System.out.println("done.");
		return (true) ;
		}

	}

