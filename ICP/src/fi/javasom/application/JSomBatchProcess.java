package fi.javasom.application;
//
//  JSomProgressListener interface for progress following.
//
//  Copyright (C) 2001-2004  Tomi Suuronen
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

import java.util.*;

/**
 * JSomProgressListener interface for progress following.
 * <p>
 * Copyright (C) 2001-2004  Tomi Suuronen
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @version 1.0
 * @since 1.0
 * @author Tomi Suuronen
*/
public class JSomBatchProcess  extends ArrayList {
    
    /**
     * Constructor.
    */
    public JSomBatchProcess() {
	   super(10);
    }
    
    /**
     * Adds a listener.
     *
     * @param listener Listener to be added.
    */
    public void addListener(JSomProgressListener listener) {
        add(listener);
    }
    
    /**
     * Removes a listener from this process set.
     *
     * @param listener Listener to be removed.
    */
    public void removeListener(JSomProgressListener listener) {
        remove(indexOf(listener));
    }
    
    /**
     * Triggers a Batch start.
     *
     * @param info Start info of batch.
    */
    protected void fireBatchStart(String name) {
        for (int i = 0; i < size(); i++) {
            ((JSomProgressListener) get(i)).startBatch(name);
        }
    }

    /**
     * Triggers a Batch end.
     *
     * @param info End info of batch. 
    */
    protected void fireBatchEnd(String info) {
        for (int i = 0; i < size(); i++) {
            ((JSomProgressListener) get(i)).endBatch(info);
        }
    }

    /**
     *
     * @param count
     * @param max
    */
    protected void fireBatchProgress(int count, int max) {
        for (int i = 0; i < size(); i++)
        {
            ((JSomProgressListener) get(i)).batchProgress(count, max);
        }
    }
}
