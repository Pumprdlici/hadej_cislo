package icp.aplication;

import icp.data.*;

import java.util.*;


/**
 * Tøída pro uchovávání dat o vıbìru epoch a artefaktù.
 * 
 * @author Petr - Soukal
 *
 */
public class SignalsSegmentation
{	
	private int EPOCH_CENTER = 1, EPOCH_SPACE = -1;
	private int LEFT_ARTEFACT = -1, RIGHT_ARTEFACT = 1;
	private SessionManager appCore;
	private Header header;
	
	private int startEpoch,  endEpoch;//interval kolik framu vzit pøed støedem a za støedem epochy
    private ArrayList<Epoch> epochs;
    private ArrayList<Artefact> artefacts;
    private int[] epochsDraw;
    private boolean[] artefactsDraw;
    
    /**
     * Vytváøí objekt tøídy a instancuje appCore.
     * 
     * @param appCore - instance objektu hlavní øídící tøídy aplikaèní vrstvy.
     */
	public SignalsSegmentation(SessionManager appCore)
	{	
		this.appCore = appCore;			
	}
	
	/** 
     * Rozhoduje o tom zda se mùe epocha vloit na dané místo, pokud ano tak epochu vloí.
     * 
     * @param xAxis - parametr udávající hodnotu framu pøi kliknutí na drawingComponentu, kam
     * by se mìla epocha vloit.
     * @return true - pokud lze epochu na danné místo vloit.<br/>
     * false - pokud nelze epochu na danné místo vloit.     * 
     */
    public boolean selectEpoch(int xAxis)
    {
    	if (header == null) {
            return false;
        }
    	
    	int frameCenterEpoch = xAxis;

		if(frameCenterEpoch >= startEpoch &&	frameCenterEpoch < header.getNumberOfSamples() - endEpoch)
		{
			if(epochsDraw[frameCenterEpoch] == 0 && epochsDraw[frameCenterEpoch + endEpoch] == 0
					&& epochsDraw[frameCenterEpoch - startEpoch] == 0)
			{
				epochsDraw[frameCenterEpoch] = EPOCH_CENTER;
				epochs.add(new Epoch(header.getNumberOfChannels(), frameCenterEpoch));
				
				for(int i = frameCenterEpoch - startEpoch; i < frameCenterEpoch;i++)
				{
					epochsDraw[i] = EPOCH_SPACE;
				}
				
				for(int i = frameCenterEpoch +1; i <= frameCenterEpoch+endEpoch;i++)
				{
					epochsDraw[i] = EPOCH_SPACE;
				}
				
				return true;
			}
        }	
		
		return false;
    }
    
    /** 
     * Mae epochu z danného místa.
     * 
     * @param xAxis - parametr udávající hodnotu framu pøi kliknutí na drawingComponentu, kde 
     * by se mìla epocha smazat.
     * @return true - pokud se epocha na daném místì nachází a byla smazána.<br/>
     * false - pokud se epocha na daném místì nenachází a proto nebyla smazána.
     */
    public boolean unselectEpoch(int xAxis)
    {
    	if (header == null) {
            return false;
        }
						
		if(epochsDraw[xAxis] != 0)	
		{
			int indexCenterEpoch = searchingIndexCenterEpoch(xAxis);
			
			Collections.sort(epochs);
			int foundIndex = Collections.binarySearch(epochs, new Epoch(header.getNumberOfChannels(), indexCenterEpoch));
			epochs.remove(foundIndex);
			
			for(int i = indexCenterEpoch - startEpoch; i <= (indexCenterEpoch + endEpoch); i++)
                        {
				epochsDraw[i] = 0;
                        }
			
			return true;
		}
		else
                {
			return false;
                }
    }
    
    /** 
     * Hledá støed epochy pøekrıvající danı frame.
     * 
     * @param xAxis - parametr udávající hodnotu framu pøi kliknutí na drawingComponentu.
     * @return index - index støedu epochy, která zasahuje do daného framu. 
     */
    private int searchingIndexCenterEpoch(int xAxis)
    {
    	int index;    	
    	int terminal = xAxis + startEpoch;
    	
    	if(terminal >= header.getNumberOfSamples())
        {
    		terminal = (int)header.getNumberOfSamples() - 1;
        }
			
		for(index = xAxis; index <= terminal; index++)
		{
			if(epochsDraw[index] == EPOCH_CENTER)
                        {
				return index;
                        }
		}
		
		for(index = xAxis - endEpoch; index < xAxis; index++)
		{
			if(epochsDraw[index] == EPOCH_CENTER)
                        {
				break;
                        }
		}	
		
		return index;
    }
    
    /** 
     * Vkládá hodnotu true do pole pro vykreslení artefaktu v intervalu startArtefact, endArtefact.
     * 
     * @param startArtefact - poèáteèní hodnota artefaktu.
     * @param endArtefact - koneèná hodnota artefaktu.
     */
    public void selectArtefact(int startArtefact, int endArtefact)
    {
    	if (header == null) {
            return;
        }
    	
    	if(startArtefact < 0)
        {
    		startArtefact = 0;
        }
    	if(endArtefact >= header.getNumberOfSamples())
        {
    		endArtefact = (int) (header.getNumberOfSamples() - 1);
        }
    	
    	for(int i = startArtefact; i <= endArtefact; i++)
    	{
    		artefactsDraw[i] = true;
    	}
    	
    	insertArtefactInArrayList(startArtefact, endArtefact);
    }
    
    /** 
     * Vkládá hodnotu false do pole pro vykreslení artefaktu v intervalu start, end.
     * 
     * @param start - poèáteèní hodnota mazání artefaktu.
     * @param end - koneèná hodnota mazání artefaktu.
     */
    public void unselectArtefact(int start, int end)
    {
    	if (header == null) {
            return;
        }
    	
    	if(start < 0)
        {
    		start = 0;
        }
    	if(end >= header.getNumberOfSamples())
        {
    		end = (int) (header.getNumberOfSamples() - 1);
        }
    	
    	for(int i = start; i <= end; i++)
    	{
    		artefactsDraw[i] = false;
    	}
    	
    	deleteArtefactFromArrayList(start, end);
    }
    
    /** 
     * Vkládá artefakty do ArrayListu pro ukládání dat v projektu.
     * 
     * @param startArtefact - poèáteèní hodnota mazání artefaktu.
     * @param endArtefact - koneèná hodnota mazání artefaktu.
     */
    private void insertArtefactInArrayList(int startArtefact, int endArtefact)
    {
    	if(artefacts.size() > 0)
    	{
        	int index = 0;
        	
	    	for(; index < artefacts.size(); index++)//vkládání a øazení artefaktu 
	    	{
	    		if(startArtefact <= artefacts.get(index).getArtefactStart()  )
	    		{
	    			artefacts.add(index, new Artefact(startArtefact, endArtefact));
	    			break;
	    		}    		
	    	}
	    		    	
	    	if(index == artefacts.size())//pokud nebyl artefakt zaøazen
                {
	    		artefacts.add(new Artefact(startArtefact, endArtefact));
                }
	    		    	
	    	for(int j = index+1; j < artefacts.size(); j++)
	    	{
	    		if(startArtefact <= artefacts.get(j).getArtefactStart()//smazání všech artefaktù v daném intervalu
	    				&& endArtefact >= artefacts.get(j).getArtefactEnd())
	    		{	    			
	    			artefacts.remove(j);
	    			j--;
	    		}
	    		else
                        {
	    			break;
                        }
	    	}
	    	
	    	
	    	if(index > 0)//spojení aktuálního artefaktu a levého artefaktu
	    	{
	    		if(artefacts.get(index - 1).getArtefactEnd() + 1 >= startArtefact && 
	    				artefacts.get(index - 1).getArtefactEnd() + 1 <= endArtefact)
	    		{
	    			artefacts.get(index).setArtefactStart(artefacts.get(index - 1).getArtefactStart());
	    			artefacts.remove(index - 1);  	
	    			index--;
	    		}
	    		else if(artefacts.get(index - 1).getArtefactEnd() + 1 >= startArtefact && 
	    				artefacts.get(index - 1).getArtefactEnd() + 1 >= endArtefact)
	    		{
	    			artefacts.remove(index);
	    			return;
	    		}
	    	}    		
	    	
	    	if(index < artefacts.size() - 1)//spojení aktuálního artefaktu a pravého artefaktu
	    	{
	    		if(artefacts.get(index + 1).getArtefactStart() - 1 <= endArtefact)
	    		{
	    			artefacts.get(index).setArtefactEnd(artefacts.get(index + 1).getArtefactEnd());
	    			artefacts.remove(index + 1);
	    		}    		
	    	}
    	}
    	else//vloení prvního artefaktu
        {
    		artefacts.add(new Artefact(startArtefact, endArtefact));
        }
    			
    }
    
    /** 
     * Mae, nebo zkracuje artefakty z ArrayListu pro ukládání dat v projektu.
     * 
     * @param startArtefact - poèáteèní hodnota mazání artefaktu.
     * @param endArtefact - koneèná hodnota mazání artefaktu.
     */
    private void deleteArtefactFromArrayList(int start, int end)
    {
    	if(artefacts.size() > 0)
    	{
    		for(int i = 0; i < artefacts.size(); i++)
	    	{
	    		if(start <= artefacts.get(i).getArtefactStart()//smazání všech artefaktù v daném intervalu
	    				&& end >= artefacts.get(i).getArtefactEnd())
	    		{	    			
	    			artefacts.remove(i);
	    			i--;
	    		}
	    	}
	    	
    		int index = 0;
    		int spaceArtefact = 0;
    		
    		for(; index < artefacts.size(); index++)//hledání pravého nebo levého artefaktu, ovlivòující danı interval
	    	{
	    		if(start > artefacts.get(index).getArtefactStart() &&
	    				start <= artefacts.get(index).getArtefactEnd())
	    		{
	    			spaceArtefact = LEFT_ARTEFACT;
	    			break;
	    		}
	    		else if(start <= artefacts.get(index).getArtefactStart() &&
	    				end >= artefacts.get(index).getArtefactStart())
	    		{
	    			spaceArtefact = RIGHT_ARTEFACT;
	    			break;
	    		}
	    	}
	    	
	    	if(spaceArtefact == LEFT_ARTEFACT)
	    	{
	    		if(end < artefacts.get(index).getArtefactEnd())//mazání støedu artefaktu a rozdìlení na dva
	    		{
	    			Artefact artefact1 = new Artefact(artefacts.get(index).getArtefactStart(), start - 1);
	    			Artefact artefact2 = new Artefact(end+1, artefacts.get(index).getArtefactEnd());

	    			artefacts.remove(index);
	    			artefacts.add(index, artefact2);
	    			artefacts.add(index, artefact1);
	    			index++;
	    		}
	    		else//zkrácení artefaktu na konci
                        {
	    			artefacts.get(index).setArtefactEnd(start-1);
                        }
	    		
	    		if(index < artefacts.size() - 1)//zda existuje pravı artefakt
	    		{
	    			if(end >= artefacts.get(index + 1).getArtefactStart())//zda je ovlivnìn danım intervalem
                                {
	    				artefacts.get(index + 1).setArtefactStart(end+1);//zkrácení artefaktu na zaèátku
                                }
	    				
	    		}
	    	} 
	    	else if(spaceArtefact == RIGHT_ARTEFACT)
                {
	    		artefacts.get(index).setArtefactStart(end+1);//zkrácení artefaktu na zaèátku
                }
    	}    	
    }
	
    /**
     * Vytváøí ArrayListy a nastavuje pole podle údajù o velikosti mìøenıch dat ze souboru.
     */
	public void setSegmentArrays()
	{
            if ((header = appCore.getHeader()) == null) {
                return;
            }
		
        startEpoch = 0;
    	endEpoch = 0;
		epochs = new ArrayList<Epoch>();
		artefacts = new ArrayList<Artefact>();
		epochsDraw = new int[(int) (header.getNumberOfSamples())];
		artefactsDraw = new boolean[(int) (header.getNumberOfSamples())];
	}
	
	/**
     * Metoda nastavující pøední rozmezí epoch.
     * 
     * @param start - pøední rozmezí epochy.
     * @return true - pokud lze nastavit rozmezí epoch.<br/>
     *false - pokud rozmezí nejde na danı paramtr nastavit.
     */
	public boolean setLeftEpochBorder(int start)
	{
    //    	úsek zjišování, zda se vùbec mùe rozmezí epoch zvìtšit aby se nepøekrıvali
            boolean collision = false;

            if(epochs.size() != 0)
            {
                    Collections.sort(epochs);

                    if(epochs.get(0).getPosition() < start)
                    {
                            collision = true;
                    }

                    for(int i = 0; i < epochs.size() - 1; i++)
                    {
                            if((epochs.get(i+1).getPosition() - epochs.get(i).getPosition()) <= (start+endEpoch))
                            {
                                    collision = true;
                                    break;
                            }
                    }    		    	
            }

            if(!collision)
            {
                    if(start < startEpoch)
                    {
                            for(Epoch centerEp: epochs)
                            {
                                    for (int i = (int)centerEp.getPosition() - startEpoch; i < centerEp.getPosition() - start; i++)
                                    {
                                            epochsDraw[i] = 0;
                                    }
                            }
                    }
                    else if (start > startEpoch)
                    {
                            for(Epoch centerEp: epochs)
                            {
                                    for(int i = (int)centerEp.getPosition() - start; i < centerEp.getPosition() - startEpoch; i++)
                                    {
                                            epochsDraw[i] = -1;
                                    }
                            }    			
                    }

                    startEpoch = start;

                    return true;
            }
            else
            {
                    return false;  	
            }
	}
	
	/**
     * Metoda nastavující zadní rozmezí epoch.
     * 
     * @param end - zadní rozmezí epochy.
     * @return true - pokud lze nastavit rozmezí epoch.<br/>
     *false - pokud rozmezí nejde na danı paramtr nastavit.
     */
	public boolean setRightEpochBorder(int end)
	{
    //    	úsek zjišování, zda se vùbec mùe rozmezí epoch zvìtšit aby se nepøekrıvali
            boolean collision = false;

            if(epochs.size() != 0)
            {
                    Collections.sort(epochs);

                    if(epochs.get(epochs.size()-1).getPosition() + end >= header.getNumberOfSamples())
                    {
                            collision = true;
                    }

                    for(int i = 0; i < epochs.size() - 1; i++)
                    {
                            if((epochs.get(i+1).getPosition() - epochs.get(i).getPosition()) <= (startEpoch+end))
                            {
                                    collision = true;
                                    break;
                            }
                    }
            }

            if(!collision)
            {    		
                    if(end < endEpoch)
                    {
                            for(Epoch centerEp: epochs)
                            {
                                    for(int i = (int)centerEp.getPosition() + end +1; i <= centerEp.getPosition() + endEpoch; i++)
                                    {
                                            epochsDraw[i] = 0;
                                    }
                            }
                    }
                    else if(end > endEpoch)
                    {
                            for(Epoch centerEp: epochs)
                            {
                                    for(int i = (int)centerEp.getPosition() + endEpoch + 1; i <= centerEp.getPosition() + end; i++)
                                    {
                                            epochsDraw[i] = -1;
                                    }
                            }
                    }
                    endEpoch = end;

                    return true;
            }
            else
            {
                    return false;
            }
    	   	
	}
		
	/** 
     * Pøidává artefakty vyhledané automatickım oznaèováním artefaktù.
     * 
     * @param newArtefacts - arrayList novì nalezenıch artefaktù.
     */
	public void addArtefacts(ArrayList<Artefact> newArtefacts)
	{
		for(Artefact art: newArtefacts)
                {
			selectArtefact(art.getArtefactStart(), art.getArtefactEnd());
                }
	}
	
	/** 
     * Zjišuje zda je moné povolit tlaèítko pro oznaèování epoch v popup-menu.
     * 
     * @param frame - frame na kterı se kliklo v drawingComponentì.
     * @return true - pokud se mùe epocha oznaèit.<br>
     * false - pokud se epocha nemùe oznaèit.
     */
	public boolean getEnabledSelEpoch(long frame)
	{
		if((frame-startEpoch) >= 0 && (frame+endEpoch) < epochsDraw.length)
		{
			if(epochsDraw[(int)frame + endEpoch] == 0	&& epochsDraw[(int)frame- startEpoch] == 0)
                        {
				return true;
                        }
			else
                        {
				return false;			
                        }
		}
		else
                {
			return false;
                }
	}
	
	/** 
     * Zjišuje zda je moné povolit tlaèítko pro odznaèování epoch v popup-menu.
     * 
     * @param frame - frame na kterı se kliklo v drawingComponentì.
     * @return true - pokud se na daném místì vyskytuje epocha<br>
     * false - pokud se epocha na daném místì nevyskytuje.
     */
	public boolean getEnabledUnselEpoch(long frame)
	{
		if(epochsDraw[(int)frame] != 0)
                {
			return true;
                }
		else
                {
			return false;
                }
	}
	
	/** 
     * Zjišuje zda je moné povolit tlaèítko pro odznaèování artefaktu v popup-menu.
     * 
     * @param frame - frame na kterı se kliklo v drawingComponentì.
     * @return true - pokud se na daném místì vyskytuje artefakt.<br>
     * false - pokud se na daném místì artefakt nevyskytuje.
     */
	public boolean getEnabledUnselArtefact(long frame)
	{
		if(artefactsDraw[(int)frame])
                {
			return true;
                }
		else
                {
			return false;
                }
	}
	
	/** 
     * Zjišuje zda je moné povolit tlaèítko pro ozdnaèování všech epoch.
     * 
     * @return true - pokud je oznaèená aspoò jedna epocha.<br>
     * false - pokud neni oznaèená ádná epocha.
     */
	public boolean getEnabledUnselAllEpochs()
	{
		if(epochs.size() != 0)
                {
			return true;
                }
		else
                {
			return false;
                }
	}
	
	/** 
     * Zjišuje zda je moné povolit tlaèítko pro ozdnaèování všech artefaktù.
     * 
     * @return true - pokud je oznaèenı aspoò jeden artefakt.<br>
     * false - pokud neni oznaèenı ádnı artefakt.
     */
	public boolean getEnabledUnselAllArtefacts()
	{
		if(artefacts.size() != 0)
                {
			return true;
                }
		else
                {
			return false;
                }
	}
	
	/** 
     * Smae artefakt v celém rozsahu, kterı se vyskytuje na daném místì.
     * 
     * @param frame - frame na kterı se kliklo v drawingComponentì.
     */
	public void unselectionConcreteArtefact(long frame)
	{
		for(int i = 0; i < artefacts.size(); i++)
    	{
    		if(frame >= artefacts.get(i).getArtefactStart()
    				&& frame <= artefacts.get(i).getArtefactEnd())
    		{	    			
    			artefacts.remove(i);
    			break;
    		}
    	}
		
		for(int i = (int)frame; i < artefactsDraw.length; i++)
		{			
			if(artefactsDraw[i])
                        {
				artefactsDraw[i] = false;
                        }
			else
                        {
				break;
                        }
		}
		
		if(frame > 0)
		{
			for(int i = (int)frame - 1; i >= 0; i--)
			{			
				if(artefactsDraw[i])
                                {
					artefactsDraw[i] = false;
                                }
				else
                                {
					break;
                                }
			}
		}
	}
	
	/** 
     * Smae všechny artefakty.
     */
	public void unselectionAllArtefacts()
	{
		artefacts = new ArrayList<Artefact>();
		artefactsDraw = new boolean[(int) (header.getNumberOfSamples())];
	}
	
	/** 
     * Smae všechny epochy.
     */
	public void unselectionAllEpochs()
	{
		epochs = new ArrayList<Epoch>();
		epochsDraw = new int[(int) (header.getNumberOfSamples())];
	}
	
	
	
	/**
     * @return pole epoch pro vykreslení.
     */
	public int[] getEpochsDraw()
	{
		return epochsDraw;
	}
	
	/**
     * @return pole artefaktù pro vykreslení.
     */
	public boolean[] getArtefactsDraw()
	{
		return artefactsDraw;
	}
		
	/**
	 * Nastavuje ArrayList artefaktù a podle tìchto artefaktù se oznaèují oblasti
	 * v poli pro vykreslení.
	 * 
	 * @param artefacts - ArrayList objektù tøídy Artefact.
	 */
	public void setArtefacts(ArrayList<Artefact> artefacts)
	{
		this.artefacts = artefacts;
		
		for(Artefact art: artefacts)
		{
			for(int i = art.getArtefactStart(); i <= art.getArtefactEnd();i++)
                        {
				artefactsDraw[i] = true;
                        }
		}
	}
	
	/**
	 * @return ArrayList artefaktù.
	 */
	public ArrayList<Artefact> getArtefacts()
	{
		return artefacts;
	}
	
	/**
	 * Nastavuje ArrayList epoch a podle tìchto epoch se oznaèují oblasti
	 * v poli pro vykreslení.
	 *  
	 * @param epochs - ArrayList objektù tøídy Epoch.
	 */
	public void setEpochs(ArrayList<Epoch> epochs)
	{
		this.epochs = epochs;
		
		for(Epoch epoch: epochs)
                {
				epochsDraw[(int)epoch.getPosition()] = EPOCH_CENTER;
                }
	}
	
	/**
     * @return arrayList všech epoch.
     */
	public ArrayList<Epoch> getEpochs()
	{
		return epochs;
	}
	
	public int getStartEpoch()
	{
		return startEpoch;
	}
	
	public int getEndEpoch()
	{
		return endEpoch;
	}
	
	/**
     * @return ArrayList støedù epoch pro prùmìrování.
     */
	public ArrayList<Integer> getIndicesEpochsForAveraging()
	{
		Collections.sort(epochs);
		ArrayList<Integer> indicesEpochForAveraging = new ArrayList<Integer>();
		boolean foundArtefact;
		
		for(int i = 0; i < epochs.size(); i++)
		{
			foundArtefact = false;
			
			for(int j = (int)epochs.get(i).getPosition() - startEpoch;j <= epochs.get(i).getPosition() + endEpoch;j++)
			{
				if(artefactsDraw[j])
				{
					foundArtefact = true;
					break;
				}
			}
			
			if(!foundArtefact)
                        {
				indicesEpochForAveraging.add(i);
                        }
		}
		
		return indicesEpochForAveraging;
	}
}
