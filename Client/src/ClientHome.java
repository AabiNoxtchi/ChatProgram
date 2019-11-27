import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientHome {
	
	private static InetAddress host;
	private static int PORT=9003;
	
	public static void main(String[] args) {
		try {
			host=InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			System.out.println("Host ID not found !");
			System.exit(1);
		}
		accessServer();
	
}
	private static void accessServer() {
		Socket link=null;
		Scanner input=null;
		Scanner userIntry=null;
		
		try {
			link=new Socket(host,PORT);
			input=new Scanner(link.getInputStream());
			PrintWriter output=new PrintWriter(link.getOutputStream(),true);
			userIntry=new Scanner(System.in);
			String message,response;
			do {
				System.out.println("Print msg : ");
				message=userIntry.nextLine();
				output.println(message);
				
				response=input.nextLine();
				System.out.println(response);
				
			}while(!message.equals("CLOSE"));			
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			input.close();
			try {
				link.close();
			}catch(IOException e)
			{
				System.out.println("\nUnable to close conection ");
				System.exit(1);;
			}
		}
		
	}

}
