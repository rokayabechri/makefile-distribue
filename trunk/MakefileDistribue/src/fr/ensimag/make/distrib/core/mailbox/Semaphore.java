package fr.ensimag.make.distrib.core.mailbox;

public class Semaphore
{
	private int valeur;
	private int limite;
	
	public Semaphore(int valeur, int limite)
	{
		this.valeur=valeur;
		this.limite=limite;
	}
	
	public int getValeur()
	{
		return valeur;
	}
	
	synchronized public void P()
	{
		while (valeur<1)
		{
			try 
			{
				wait();
			}
			catch(InterruptedException e)
			{
				System.out.println("Erreur de S�maphore");
			}
		}
		valeur=valeur-1;
	}
	
	synchronized public void V()
	{
		valeur=valeur+1;
		notify();
	}
}
