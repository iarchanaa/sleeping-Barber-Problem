package threading.practice;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class SleepingBarber {
	public  static int barberCount; 
	public  static int customerCount; 
	public static int std1;
	public static int mean1;
	public static int std2;
	public static int mean2;
	public static int nChairs;
	
	static Integer validateInput(String input) {
		try {
			Integer i = Integer.parseInt(input);
			if(i > 0)
				return i;
			else
				return -1;
		}
		catch(Exception ex) {
			return -1;
		}
	}
	
	public static void main(String[] args) {
		/* Number of barbers */
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter Number of Barbers: ");
		barberCount = validateInput(sc.nextLine());
		/* Standard deviation of the duration of cutting hair of barbers */
		System.out.print("Enter standard deviation of Barber: ");
	    std1 = validateInput(sc.nextLine());
		/* Mean of the duration of cutting hair of barbers */ 
	    System.out.print("Enter mean of Barber: ");
	    mean1 = validateInput(sc.nextLine());
		
        
	    /* Number of customers */
	    System.out.print("Enter Number of Customers: ");
        customerCount =validateInput(sc.nextLine());
        /* Standard deviation of the arrival rate  of the customers */
        System.out.print("Customer arrive at random interval with standard deviation: ");
        std2 = validateInput(sc.nextLine());
        /* Mean of the arrival rate  of the customers */
        System.out.print("Customer arrive at random interval with mean: ");
        mean2 = validateInput(sc.nextLine());
        
        /* Enter the number of waiting chair for the customers */
        System.out.print("Enter the number waiting chairs in barber shop: ");
        nChairs = validateInput(sc.nextLine());
        
        /* Input validation */
        if(barberCount == -1 || customerCount == -1 || std1 == -1 || mean1 == -1 || std2 == -1 || mean2 == -1 || nChairs == -1)  {
        	System.out.println("Please enter valid input");
        	return;
        }
        /* Create object shop using constructor */
        Barbershop shop = new Barbershop(nChairs);
       
        for(int i=0;i<barberCount; i++) {
    		Barber barber = new Barber(shop,mean1,std1);
    		barber.setId(i);
    		Thread thbarber = new Thread(barber);
    		thbarber.start();
    		/* Create thbarber thread and start it */
    		}
        
        for(int i=0;i<customerCount ;i++) {
        	Random ran = new Random();
            Customer customer = new Customer(shop);
            Thread thcustomer = new Thread(customer);
            customer.setName("Customer Thread "+i);
            thcustomer.start();
            /* Create thcustomer thread and start it */
            try {
            	/*  Customer arrive at random intervals, with mean (mean2) and standard deviation (std2). */
				Thread.sleep(Math.abs((long) (ran.nextGaussian()*std2)+mean2)); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
      
    }
	
}
 
class Barber implements Runnable
{
    Barbershop shop;
    private Integer id;
    private Integer barberCuttingDurationMean;
    private Integer barberCuttingDurationStdDev;
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	 /* This is the constructor of the class Barber */
	public Barber(Barbershop shop,Integer barberCuttingDurationMean,Integer barberCuttingDurationStdDev)
    {
        this.shop = shop;
        this.barberCuttingDurationMean = barberCuttingDurationMean;
        this.barberCuttingDurationStdDev = barberCuttingDurationStdDev;
    }
	
    public Integer getBarberCuttingDurationMean() {
		return barberCuttingDurationMean;
	}
	public void setBarberCuttingDurationMean(Integer barberCuttingDurationMean) {
		this.barberCuttingDurationMean = barberCuttingDurationMean;
	}
	public Integer getBarberCuttingDurationStdDev() {
		return barberCuttingDurationStdDev;
	}
	public void setBarberCuttingDurationStdDev(Integer barberCuttingDurationStdDev) {
		this.barberCuttingDurationStdDev = barberCuttingDurationStdDev;
	}
	
	public void run()
    {
        System.out.println("Barber " +this.getId() + ": started.");
        while(true)
        {
            shop.cutHair(this.getId(),this.getBarberCuttingDurationMean(),this.getBarberCuttingDurationStdDev());
        }
    }
}


class Customer implements Runnable
{
    String name;
    Barbershop shop;    
    
    public Customer(Barbershop shop)
    {
        this.shop = shop;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void run()
    {
        goForHairCut();
    }
    private synchronized void goForHairCut()
    {	
        shop.add(this); /* Invoke the methods for object created by adding entry of customer, add one at a time */
    }
    
}
 
class Barbershop 
{
    List<Customer> listCustomer;
    private Integer nChair;
    /* This is the constructor of the class Barbershop */
    public Integer getnChair() {
		return nChair;
	}

	public void setnChair(Integer nChair) {
		this.nChair = nChair;
	}

	public Barbershop(Integer nChair)
    {
        listCustomer = new LinkedList<Customer>();
        this.nChair = nChair;
       /* linked list for the waiting chair to preserve the order of insertion of incoming customer. */
    }
    
    public int getInterval(int mean1, double std1) {  
		Random ran = new Random();
		return Math.abs((int) ((ran.nextGaussian()*std1)+mean1));
}
    public void cutHair(int id,int cuttingDurationMean,int cuttingDurationStdDev)    
    {
        Customer customer;
        System.out.println("Barber " +id +": waiting for lock." );
        synchronized (listCustomer)
        {
        	/* Barber gets a lock and serves a customer one at a time. */
        	
            while(listCustomer.size()==0) /* If there are no customer then barber will wait for the customer */
            {
                System.out.println("Barber " +id+": is waiting for customer.");
                try
                {
                    listCustomer.wait();
                }
                catch(InterruptedException iex)
                {
                    iex.printStackTrace();
                }
            }
            System.out.println("Barber "+id+": found a customer in the queue.");
            customer = (Customer)((LinkedList<?>)listCustomer).poll(); /* As a customer enters, barber serves the first customer from the list */
        }
        
        long duration=0;
		
        try
        {    
            System.out.println("Barber "+id+": Cuting hair of "+customer.getName());
          
            duration = getInterval(cuttingDurationMean,cuttingDurationStdDev);
            TimeUnit.SECONDS.sleep(duration);
        }
        catch(InterruptedException iex)
        {
            iex.printStackTrace();
        }
        System.out.println("Barber "+id+": Completed Cuting hair of "+customer.getName() + " in "+duration+ " seconds.");
    }
 
    public void add(Customer customer)
    {
        System.out.println(customer.getName()+ ": entering the shop");
 
        synchronized (listCustomer) /* at a time only one customer will acquire the waiting chair. */
        {
            if(listCustomer.size() == this.getnChair()) 
            {
            
                System.out.println(customer.getName()+": No chair available");
                System.out.println(customer.getName()+": Exits...");
                return ;
            }
 
            ((LinkedList<Customer>)listCustomer).offer(customer);
            System.out.println(customer.getName()+ ": got the chair.");
             
            if(listCustomer.size()==1)
                listCustomer.notify(); /* As soon as customer acquires the waiting chair barber will get notified. */
        }
    }
} 


