package net.schwarzbaer.java.lib.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.swing.SwingUtilities;

public class BufferedComputationView extends ZoomableCanvas<BufferedComputationView.ViewState>
{
	private static final long serialVersionUID = -1482451612416191524L;
	private static final Color COLOR_AXIS = new Color(0x70000000,true);
	private ColorComputation getColor;
	private final BorderSpacing borderSpacing;
	private RenderingThread renderingThread;
	private MouseListener mouseListener;
	
	public BufferedComputationView(ColorComputation getColor)
	{
		this(getColor, false);
	}
	public BufferedComputationView(ColorComputation getColor, boolean updateRenderingWhilePan)
	{
		this(getColor, BorderSpacing.DEFAULT, updateRenderingWhilePan);
	}
	public BufferedComputationView(ColorComputation getColor, BorderSpacing borderSpacing, boolean updateRenderingWhilePan)
	{
		this.getColor = getColor;
		this.borderSpacing = borderSpacing;
		renderingThread = null;
		mouseListener = null;

		activateMapScale(COLOR_AXIS, "u", false);
		activateAxes(COLOR_AXIS, true,true,true,true);
		
		
		addPanListener(new PanListener2()
		{
			@Override public void panStarted() {}
			@Override public void panStopped() {
				restartRendering();
			}
			@Override public void panProceeded()
			{
				if (updateRenderingWhilePan)
					restartRendering();
			}
		});
		addZoomListener(new ZoomListener()
		{
			@Override public void zoomChanged() {
				restartRendering();
			}
		});
		addComponentListener(new ComponentListener()
		{
			@Override public void componentShown (ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			@Override public void componentMoved (ComponentEvent e) {}
			@Override public void componentResized(ComponentEvent e) {
				restartRendering();
			}
		});
	}
	
	public void setColorComputation(ColorComputation getColor)
	{
		this.getColor = getColor;
		restartRendering();
	}
	
	public void setMouseListener(MouseListener mouseListener)
	{
		this.mouseListener = mouseListener;
	}
	
	@Override public void mouseEntered(MouseEvent e) { notifyMouseListener(e); }
	@Override public void mouseMoved  (MouseEvent e) { notifyMouseListener(e); }
	@Override public void mouseExited (MouseEvent e) { notifyMouseListener(e); }
	
	private void notifyMouseListener(MouseEvent e)
	{
		if (mouseListener!=null && viewState.isOk())
		{
			double x = viewState.convertPos_ScreenToAngle_LongX(e.getX());
			double y = viewState.convertPos_ScreenToAngle_LatY (e.getY());
			mouseListener.mousePosChanged(x, y);
		}
	}
	
	@Override
	public void reset()
	{
		super.reset();
		restartRendering();
	}

	private void restartRendering()
	{
		if (renderingThread!=null)
			renderingThread.stopNow();
		
		int xOffset = borderSpacing.left;
		int yOffset = borderSpacing.top;
		int imageWidth  = width  - borderSpacing.left - borderSpacing.right ;
		int imageHeight = height - borderSpacing.top  - borderSpacing.bottom;
		renderingThread = new RenderingThread(viewState, xOffset, yOffset, imageWidth, imageHeight, getColor, () -> SwingUtilities.invokeLater(this::repaint));
	}

	public static void computeTestColor(double x, double y, int[] colorArr)
	{
		double v = 1-x-y;
		double r = Math.abs( x-Math.floor(x) );
		double g = Math.abs( v-Math.floor(v) );
		double b = Math.abs( y-Math.floor(y) );
		if (colorArr.length>0) colorArr[0] = (int) Math.min( Math.max( 0, Math.round(r*256)), 255);
		if (colorArr.length>1) colorArr[1] = (int) Math.min( Math.max( 0, Math.round(g*256)), 255);
		if (colorArr.length>2) colorArr[2] = (int) Math.min( Math.max( 0, Math.round(b*256)), 255);
		if (colorArr.length>3) colorArr[3] = 255;
	}
	
	@Override
	protected void paintCanvas(Graphics g, int x, int y, int width, int height)
	{
		if (g instanceof Graphics2D g2 /*&& viewState.isOk()*/)
		{
			int xOffset = borderSpacing.left;
			int yOffset = borderSpacing.top;
			int imageWidth  = this.width  - borderSpacing.left - borderSpacing.right ;
			int imageHeight = this.height - borderSpacing.top  - borderSpacing.bottom;
			
			if (imageWidth>0 && imageHeight>0)
			{
				g2.setColor(COLOR_AXIS);
				g2.drawRect(xOffset, yOffset, imageWidth-1, imageHeight-1);
			}
			
			if (renderingThread!=null && renderingThread.image!=null)
				g2.drawImage(renderingThread.image, xOffset, yOffset, null);
			
			if (viewState.isOk())
				drawMapDecoration(g2, x, y, width, height);
		}
	}

	@Override
	protected ViewState createViewState() {
		return new ViewState();
	}

	public class ViewState extends ZoomableCanvas.ViewState {
		
		ViewState() {
			super(BufferedComputationView.this, 0.1f);
			setPlainMapSurface();
			setHorizAxisRightPositive(true);
			setVertAxisDownPositive(false);
		}

		@Override
		protected void determineMinMax(MapLatLong min, MapLatLong max) {
			min.longitude_x = -2.0;
			min.latitude_y  = -2.0;
			max.longitude_x = +2.0;
			max.latitude_y  = +2.0;
		}
	}

	private static class RenderingThread
	{
		private final ViewState viewState;
		private final int xOffset;
		private final int yOffset;
		private final int width;
		private final int height;
		private final ColorComputation getColor;
		private final Runnable repaintView;
		private final BufferedImage image;
		private boolean stopNow;
	
		RenderingThread(ViewState viewState, int xOffset, int yOffset, int width, int height, ColorComputation getColor, Runnable repaintView)
		{
			this.viewState = viewState;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.width = width;
			this.height = height;
			this.getColor = getColor;
			this.repaintView = repaintView;
			image = this.width<=0 || this.height<=0 ? null : new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
			stopNow = false;
			new Thread(this::renderImage).start();
		}
		
		void stopNow()
		{
			stopNow = true;
		}
		
		private void renderImage()
		{
			WritableRaster raster   = image      ==null ? null : image.getRaster();
			SampleModel sampleModel = raster     ==null ? null : raster.getSampleModel();
			int numBands            = sampleModel==null ? -1   : sampleModel.getNumBands();
			int[] color             = numBands<=0       ? null : new int[numBands];
			
			System.out.printf("RenderingThread.renderImage( w:%d, h:%d, n:%d )%n", width, height, numBands);
			if (raster == null || color==null)
				System.out.printf("... stopped (invalid image parameters)%n");
			
			else if (!viewState.isOk())
				System.out.printf("... stopped (invalid ViewState)%n");
			
			else if (getColor == null)
				System.out.printf("... stopped (no ColorComputation)%n");
			
			else
				for (int y=0; y<height && !stopNow; y++)
				{
					double yCoord = viewState.convertPos_ScreenToAngle_LatY(y+yOffset);
					for (int x=0; x<width && !stopNow; x++)
					{
						double xCoord = viewState.convertPos_ScreenToAngle_LongX(x+xOffset);
						getColor.computeIntoArr(xCoord, yCoord, color);
						raster.setPixel(x, y, color);
					}
					if (repaintView!=null && (y&0xF)==0xF)
						repaintView.run();
				}
			
			if (repaintView!=null)
				repaintView.run();
		}
	}

	public interface ColorComputation
	{
		void computeIntoArr(double x, double y, int[] colorArr); // color is int[4]
	}

	public interface MouseListener
	{
		void mousePosChanged(double x, double y);
	}

	public static record BorderSpacing(int top, int left, int bottom, int right)
	{
		public static final BorderSpacing DEFAULT = new BorderSpacing(40, 40, 40, 40);
	}
}
