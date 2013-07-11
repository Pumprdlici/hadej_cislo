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
 * @version 1.1
 * @since 1.1
 * @author Kal Ahmed 
 * @author Tomi Suuronen
*/
public interface JSomProgressListener {
    
    /**
     * Method invoked when some batch process begins.
     *
     * @param batchName a displayable string for the process
     */
    public void startBatch(String batchName);
    
    /**
     * Method invoked for each step in a batch process.
     *
     * @param count the progress meter count
     * @param max the target for the count to reach when the process completes.
     */
    public void batchProgress(int count, int max);

    /**
     * Method invoked when the current batch process ends.
     *
     * @param batchName a displayable string for the process. 
     *                  This parameter value should be the same
     *                  as was passed in to the matching 
     *                  startBatch() method.
     */
    public void endBatch(String batchName);
}
