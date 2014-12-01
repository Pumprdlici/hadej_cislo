package icp.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Rozesílaè zpráv pøi použití návrhového vzoru Observer/Observable. Zjednodušuje posílání objektù mezi 
 * více tøídami, které mohou být zároveò vysílaèi (potomci tøídy <i>Observable</i>)
 * a pøijímaèi (implementují rozhraní <i>Observer</i>). Oddìdìné tøídy jsou pøekryty tak, aby pøi posílání 
 * objektù mezi dvìma instancemi této tøídy nedošlo k zacyklení. ObdrženoSvé posluchaèe si registruje 
 * buï pøi svém vzniku, kde jsou posluchaèi pøedáni jako parametry konstruktoru nebo metodou 
 * <i>addObserver</i>. Jako posluchaèe si jej registrují tøídy, které chtìjí tìmto posluchaèùm 
 * posílat objekty. 
 * 
 * @author Tomáš Øondík
 */
public final class ObjectBroadcaster extends Observable implements Observer
{	
	/*
	 * Jde v podstatì o novou implementaci tøídy Observable tak, aby nemohlo dojít k zacyklení 
	 * pøi posílání objektù mezi dvìma "ObjectBroadcastery". Jsou pøekryty všechny metody vyjma metod
	 * "clearChanged", "hasChanged", "setChanged". Ty navíc pøi pøekrytí metody "notifyObservers" ztrácejí 
	 * na významu. Observery jsou ukládány do Listu "myObservers". Pro posílání objektù observerùm se používá 
	 * privátní metoda "sendObjectToObservers", která volá pøímo metodu "update" jednotlivých observerù.
	 */
	
	
	/**
	 * Seznam posluchaèù, kterým jsou objekty pøeposílávány.
	 */
	private List<Observer> myObservers;
	/**
	 * Vytváøí instanci tøídy bez registrovaných posluchaèù.
	 */
	public ObjectBroadcaster()
	{
		super();
		myObservers = new ArrayList<Observer>();
	}
	
	/**
	 * Vytváøí instanci tøídy. Posluchaèi jsou pøedání v kolekci jako parametr.
	 * @param observers posluchaèi.
	 */
	public ObjectBroadcaster(Collection<Observer> observers)
	{
		super();
		myObservers = new ArrayList<Observer>();
		
		for (Observer observer: observers)
		{
			myObservers.add(observer);
		}
	}
	
	/**
	 * Vytváøí instanci tøídy. Jako parametr je pøedán libovolný poèet posluchaèù.
	 * @param observers libovolný poèet posluchaèù.
	 */
	public ObjectBroadcaster(Observer ... observers)
	{
		super();
		myObservers = new ArrayList<Observer>();
		
		for (Observer observer: observers)
		{
			myObservers.add(observer);
		}
	}
	
	/**
	 * Pøidává nového posluchaèe do seznamu posluchaèù.
	 * @param observer nový posluchaè.
	 */
	@Override
	public synchronized void addObserver(Observer observer)
	{
		myObservers.add(observer);
	}
	/**
	 * Pøidává všechny posluchaèe pøedané v argumentu do seznamu posluchaèù.
	 * @param observers kolekce obsahující nové posluchaèe.
	 */
	public synchronized void addObserver(Collection<Observer> observers)
	{
		for (Observer observer: observers)
		{
			myObservers.add(observer);
		}
	}
	/**
	 * Pøidává všechny posluchaèe pøedané v argumentu do seznamu posluchaèù.
	 * @param observers libovolný poèet nových posluchaèù.
	 */
	public synchronized void addObserver(Observer ...observers)
	{
		for (Observer observer: observers)
		{
			myObservers.add(observer);
		}
	}
	/**
	 * Odstraòuje všechny posluchaèe ze seznamu posluchaèù.
	 */
	@Override
	public synchronized void deleteObservers()
	{
		myObservers = new ArrayList<Observer>();
	}
	/**
	 * Odstraòuje posluchaèe pøedaného v parametru ze seznamu posluchaèù.
	 * @param observer posluchaè, který bude odstranìn ze seznamu posluchaèù.
	 */
	@Override
	public synchronized void deleteObserver(Observer observer)
	{
		myObservers.remove(observer);
	}
	/**
	 * Vrací poèet posluchaèù v seznamu posluchaèù.
	 */
	@Override
	public int countObservers()
	{
		return myObservers.size();
	}
	/**
	 * Pøekrytí metody oddìdìné od tøídy <i>Observable</i>. Internì pouze volá privátní 
	 * metodu <b>sendObjectToObservers</b> (<code>sendObjectToObservers(this, null);</code>).
	 */
	@Override
	public void notifyObservers()
	{
		sendObjectToObservers(this, null);
	}
	/**
	 * Pøekrytí metody oddìdìné od tøídy <i>Observable</i>. Internì pouze volá privátní 
	 * metodu <b>sendObjectToObservers</b> (<code>sendObjectToObservers(this, object);</code>).
	 * @param object objekt posílaný posluchaèùm.
	 */
        @Override
	public void notifyObservers(Object object)
	{
		sendObjectToObservers(this, object);
	}
	/**
	 * Realizuje posílání objektù posluchaèùm.
	 * @param observable reference na vysílaè.
	 * @param object objekt posílaný posluchaèùm.
	 */
	private void sendObjectToObservers(Observable observable, Object object)
	{
		for (Observer observer: myObservers)
		{
			if (observer instanceof Observable) // když je posluchaè zároveò vysílaèem
                        {
				if ((((Observable) observer).equals(observable))) // když by mohlo dojít k zacyklení v posílání zpráv
                                {
					continue;
                                }
                        }
			
			/*
			 * Volání metody "setChanged" není nutné, protože se volá pøímo metoda 
			 * "update".
			 */
			observer.update(this, object);
		}
	}
	
	/**
	 * Implementace metody rozhraní <i>Observer</i>. Ve svém tìle volá privátní metodu 
	 * pro posílání objektù posluchaèùm (<code>sendObjectToObservers(observable, object);</code>).
	 * @param observable reference na vysílaè.
	 * @param object objekt posílaný posluchaèùm.
	 */
	public void update(Observable observable, Object object)
	{
		sendObjectToObservers(observable, object);
	}
}
