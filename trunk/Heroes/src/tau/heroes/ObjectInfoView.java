/**
 * 
 */
package tau.heroes;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Yuval & Shira
 * 
 */
public class ObjectInfoView extends Composite
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private Label imageLabel;
	private Label titleLabel;
	private ResourcesView pricesView;
	private Label messageLabel;
	private Label messageLabel2;

	/**
	 * @param parent
	 * @param style
	 */
	public ObjectInfoView(Composite parent, int style)
	{
		super(parent, style);

		setLayout(new GridLayout(1, false));

		imageLabel = new Label(this, SWT.LEFT);
		GridData imageLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		imageLayoutData.widthHint = IMAGE_WIDTH;
		imageLayoutData.heightHint = IMAGE_HEIGHT;
		imageLabel.setLayoutData(imageLayoutData);

		titleLabel = new Label(this, SWT.LEFT);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		pricesView = new ResourcesView(this, SWT.BORDER);
		pricesView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		messageLabel = new Label(this, SWT.LEFT);
		messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		messageLabel2 = new Label(this, SWT.LEFT);
		messageLabel2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	public void setImage(Image image)
	{
		this.imageLabel.setImage(new Image(getDisplay(), image.getImageData()
			.scaledTo(IMAGE_WIDTH, IMAGE_HEIGHT)));
	}

	public void setTitle(String title)
	{
		this.titleLabel.setText(title);
	}

	public void setPrices(Map<String, Integer> prices)
	{
		this.pricesView.setResources(prices);
	}

	public void setMessage(String message)
	{
		if (message != null)
			this.messageLabel.setText(message);
		else
			this.messageLabel.setText("");
	}
	
	public void setMessage2(String message)
	{
		if (message != null)
			this.messageLabel2.setText(message);
		else
			this.messageLabel2.setText("");
	}
}
