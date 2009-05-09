package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

public class BaseLabelRowView extends Composite
{
	private CLabel[] labels;
	private Image[] images;
	private int imageWidth;
	private int imageHeight;

	/**
	 * @param parent
	 * @param style
	 *            - Creates the view under the parent
	 */
	public BaseLabelRowView(Composite parent, int style, int imageWidth, int imageHeight, int count)
	{
		super(parent, style);
		this.setLayout(new RowLayout());

		labels = new CLabel[count];
		images = new Image[count];
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		Color foreColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		for (int i = 0; i < count; i++)
		{
			labels[i] = new CLabel(this, SWT.RIGHT);
			labels[i].setLayoutData(new RowData(imageWidth, imageHeight));
			labels[i].setForeground(foreColor);
			labels[i].setFont(new Font(getDisplay(), "Tahoma", 10, SWT.BOLD));
		}

		IconCache.initResources(parent.getDisplay());
	}

	protected void setLabel(int index, Image image, String text, String tooltip)
	{
		CLabel label = labels[index];

		label.setText(text);
		label.setToolTipText(tooltip);
		image = new Image(getDisplay(), image.getImageData().scaledTo(imageWidth, imageHeight));
		label.setBackground(image);
		images[index] = image;
	}
	
	public int getLength()
	{
		return labels.length;
	}
	
	public Image getImage(int index)
	{
		return images[index];
	}
	
	final String getText(int index)
	{
		return labels[index].getToolTipText();
	}
	
	public void addMouseListener(int index, MouseListener listener)
	{
		labels[index].addMouseListener(listener);
	}
	
	public void addMouseTrackListener(int index, MouseTrackListener listener)
	{
		labels[index].addMouseTrackListener(listener);
	}
}