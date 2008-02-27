/*
  File: CyNodeView.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.view;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import cytoscape.Cytoscape;

import java.awt.Dimension;

/**
 *
  */
public class CyDesktopManager  {
	
	public static enum Arrange {
		GRID,CASCADE,HORIZONTAL,VERTICAL
	}
	public static int MINIMUM_WIN_WIDTH = 200;
	public static int MINIMUM_WIN_HEIGHT = 200;
		
	protected static JDesktopPane desktop;
	private CyDesktopManager() {
		desktop = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane();
	}
			
	//Closes all open windows
	public  void closeAllWindows() {
		JInternalFrame[] allFrames = desktop.getAllFrames();
		for (int i= allFrames.length -1; i>=0; i--) {
			allFrames[i].dispose();			
		}
	}
		
	// Implementation of grid layout algorithm
	// gridLayout -- an int array-- int[i] holds the number of row for column i 
	private static void getGridLayout(final int pTotal, final int pCol, final int pRow, int[] gridLayout) {
		if (pTotal > pRow) {
			int row = -1;
			if (pTotal%pCol == 0) {
				row = pTotal/pCol;
				gridLayout[pCol-1] = row;
			}
			else {
				row = pRow;				
				gridLayout[pCol-1] = pRow;
			}
			getGridLayout(pTotal-row, pCol-1,row, gridLayout);
		}
		else {
			gridLayout[0] = pTotal;
		}		
	}
	
	
	// Arrange all windows in the desktop according to the given style
	public static void arrangeFrames(Arrange pStyle) {
		if (desktop == null) {
			new CyDesktopManager();
		}
		
		Dimension desktopSize = desktop.getSize();
		
		JInternalFrame[] allFrames = desktop.getAllFrames();
		
		int frameCount = allFrames.length; 
		if ( frameCount == 0) {
			return;
		}

		if (pStyle == Arrange.CASCADE) {
			int delta_x = 20;
			int delta_y = 20;
			int delta_block = 50;
						
			int[] x = new int[frameCount];
			int[] y = new int[frameCount];
			int[] w = new int[frameCount];
			int[] h = new int[frameCount];
			x[0] = 0;
			y[0] = 0;
			w[0] = allFrames[0].getWidth();
			h[0] =allFrames[0].getHeight();

			boolean multiBlock = false;
			int blockSize =0;
			for (int i=1; i<frameCount; i++) {
				blockSize++;
				x[i] = x[i-1] + delta_x;
				y[i] = y[i-1] + delta_y;

				if (desktopSize.height - y[i]<MINIMUM_WIN_HEIGHT) {
					y[i] =0;
					multiBlock = true;
				}
				if (desktopSize.width - x[i]<MINIMUM_WIN_WIDTH && !multiBlock) {
					x[i] = x[i-1];
				}
				
				// Determine the w,h for the previous block and start of another block 
				if (y[i]==0 && multiBlock) {
										
					for (int j=0; j< blockSize; j++) {
						if (i-blockSize>0) { //use the same (w, h) as previous block
							w[i-j-1] = w[i-blockSize];
							h[i-j-1] = h[i-blockSize];							
						}
						else {
							w[i-j-1] = desktopSize.width - x[i-1];
							h[i-j-1] = desktopSize.height - y[i-1];							
						}
					}									
					//start of another block
					x[i] = x[i-blockSize] + delta_block; 
					if (x[i] > (desktopSize.width - delta_x * blockSize)) {
						x[i] = x[i-blockSize];
					}
					blockSize =1;	
				}
			}

			// Handle the last block
			if (!multiBlock) { // single block
				for (int i = 0; i < frameCount; i++) {
					w[frameCount-1-i] = desktopSize.width - x[frameCount - 1];
					h[frameCount-1-i] = desktopSize.height - y[frameCount - 1];					
				}
			}
			else { //case for multiBlock
				for (int i = 0; i < blockSize; i++) {
					//use the same (w, h) as previous block
					w[frameCount-1-i] = w[frameCount - blockSize-1];
					h[frameCount-1-i] = h[frameCount - blockSize-1];
					// If w is too wider to fit to the screen, adjust it
					if (w[frameCount-1-i] > desktopSize.width - x[frameCount - 1]) {
						w[frameCount-1-i] = desktopSize.width - x[frameCount - 1];
					}
				}				
			}
			
			if (desktopSize.height - MINIMUM_WIN_HEIGHT < delta_y ) { // WinHeight is too small, This is a special case
				double delta_x1 = ((double)(desktopSize.width - MINIMUM_WIN_WIDTH))/(frameCount-1);
				for (int i = 0; i < frameCount; i++) {
					x[i] = (int) Math.ceil( i * delta_x1);
					y[i] =0;
					w[i] = MINIMUM_WIN_WIDTH;
					h[i] = MINIMUM_WIN_HEIGHT;
				}
			}
			
			//Arrange all frames on the screen
			for (int i=0; i<frameCount; i++) {
				allFrames[frameCount-1-i].setBounds(x[i], y[i], w[i], h[i]);
			}
		}
		else if (pStyle == Arrange.GRID) {
			// Determine the max_col and max_row for grid layout 
			int maxCol = (new Double(Math.ceil(Math.sqrt(frameCount)))).intValue();
			int maxRow = maxCol;
			while (true) {
				if (frameCount <= maxCol*(maxRow -1)) {
					maxRow--;
					continue;
				}
				break;
			}

			// Calculate frame layout on the screen, i.e. the number of frames for each column 
			int[] gridLayout = new int[maxCol];
			getGridLayout(frameCount, maxCol, maxRow, gridLayout);
			
			// Apply the layout on screen
			int w = desktopSize.width/maxCol;
			int curFrame = frameCount -1;
			for (int col=maxCol-1; col>=0; col--) {
				int h = desktopSize.height/gridLayout[col];
				
				for (int i=0; i< gridLayout[col]; i++) {
					int x = col * w;
					int y = (gridLayout[col]-i-1)* h;					
					allFrames[curFrame--].setBounds(x, y, w, h);
				}				
			}
		}
		else if (pStyle == Arrange.HORIZONTAL) {
			int x = 0;
			int y = 0;
			int w = desktopSize.width;
			int h = desktopSize.height/frameCount;
			if (h < MINIMUM_WIN_HEIGHT ) {
				h = MINIMUM_WIN_HEIGHT;
			}
			
			double delta_h = 0;
			if (frameCount > 1) {
				delta_h = ((double)(desktopSize.height - MINIMUM_WIN_HEIGHT))/(frameCount-1);	
			}
			
			for (int i=0; i< frameCount; i++) {
				y = (int)(delta_h * i);
				if (y> desktopSize.height - MINIMUM_WIN_HEIGHT) {
					y = desktopSize.height - MINIMUM_WIN_HEIGHT;
				}
				allFrames[frameCount-i-1].setBounds(x, y, w, h);
			}
		}
		else if (pStyle == Arrange.VERTICAL) {
			int x = 0;
			int y = 0;
			int w = desktopSize.width/frameCount;
			int h = desktopSize.height;
			
			if (w < MINIMUM_WIN_WIDTH) {
				w = MINIMUM_WIN_WIDTH;
			}

			double delta_w = 0;
			if (frameCount > 1) {
				delta_w = ((double)(desktopSize.width - MINIMUM_WIN_WIDTH))/(frameCount-1);	
			}
			
			for (int i=0; i< frameCount; i++) {
				x = (int)(delta_w * i);
				if (x > desktopSize.width - MINIMUM_WIN_WIDTH) {
					x = desktopSize.width - MINIMUM_WIN_WIDTH;
				}
				allFrames[frameCount-i-1].setBounds(x, y, w, h);
			}
		}
	}
	
}

