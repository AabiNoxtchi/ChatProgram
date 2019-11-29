package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import Messages.Message;
import Messages.User;

public class ClientListner extends Thread{
	
	private Socket socket;
	private ObjectInputStream input;
   // private OutputStream os;
    private ObjectOutputStream output;
   // private InputStream is;
	
	public ClientListner(Socket socket) {
		this.socket=socket;
		try {
		input = new ObjectInputStream(socket.getInputStream());
        
        output = new ObjectOutputStream(socket.getOutputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
		
            System.out.println("Inside ServerClient.run");
           Message msg=null;
           
           
             try {
				msg = (Message)input.readObject();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
          

                while (socket.isConnected()) {
						
						if (msg != null) {
		                        System.out.println(msg.getType() + " - " + msg.getUser().getUserName() + ": " + msg.getUser().getPassword());
		                        switch (msg.getType()) {
		                           case Register:
		                            	System.out.println("registering new user");
		                            	//System.out.println(msg);
		                            	System.out.println(msg.getUser().getUserName());
		                            	
		                            	boolean registered= Register(msg.getUser());
		                               
		                                break;
								default:
									break;
		                        //    case LogIn:
		                               //to do
		                           //     break;
		                            
		                        }
		                    }
						
						try {
							msg = (Message)input.readObject();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					} 
           
                	
                    
                
              
	}

	private boolean Register(User user) {
		
		if(!CheckIfExist(user)) {
			
			//to do register user
			
		}
		
		
		// TODO Auto-generated method stub
		return false;
	}

	private boolean CheckIfExist(User user) {
		// TODO Auto-generated method stub
		return false;
	}
}


