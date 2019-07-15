/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import lombok.extern.slf4j.Slf4j;
import net.runelite.launcher.ui.LauncherPanel;
import net.runelite.launcher.ui.SwingUtil;
import net.runelite.launcher.util.LinkBrowser;

@Slf4j
public class LauncherFrame extends JFrame
{
	public static final Dimension FRAME_SIZE = new Dimension(200, 275);

	private static final BufferedImage LOGO;
	static
	{
		LOGO = SwingUtil.loadImage("runelite.png");
	}

	private final LauncherPanel panel;
	private final int steps;
	private int currentStep;

	public LauncherFrame(final String version, final int steps)
	{
		this.steps = steps;
		this.currentStep = 0;

		this.setTitle("RuneLite");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(FRAME_SIZE);
		this.setLayout(new BorderLayout());
		this.setUndecorated(true);
		this.setIconImage(LOGO);
		this.setShape(new RoundRectangle2D.Double(0, 0, FRAME_SIZE.width, FRAME_SIZE.height, 15, 15));

		panel = new LauncherPanel(version);
		this.setContentPane(panel);
		pack();

		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	void setMessage(final String msg)
	{
		panel.getMessageLabel().setText(msg);
		panel.getBar().setMaximum(steps);
		panel.getBar().setValue(++currentStep);
		setSubMessage(null);

		panel.revalidate();
		panel.repaint();
	}

	void setSubMessage(final String msg)
	{
		final JLabel label = panel.getSubMessageLabel();
		label.setText(msg);

		label.revalidate();
		label.repaint();
	}

	void progress(String filename, int bytes, int total)
	{
		if (total == 0)
		{
			return;
		}

		int percent = (int) (((float) bytes / (float) total) * 100f);

		final JProgressBar bar = panel.getBar();
		bar.setMaximum(100);
		bar.setString(filename + " (" + percent + "%)");
		bar.setValue(percent);

		setSubMessage("Downloading " + filename + "...");

		bar.revalidate();
		bar.repaint();
	}

	void invalidVersion()
	{
		final String message = "<html><div style='text-align: center;'>" +
			"Your RuneLite launcher version is outdated<br/>" +
			"Please visit runelite.net to download the updated version</div></html>";
		final Object[] buttons = new Object[]{"Visit runelite.net", "Close client"};

		final int result = JOptionPane.showOptionDialog(panel,
			message,
			"Outdated launcher",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.ERROR_MESSAGE,
			null,
			buttons,
			buttons[1]);

		if (result == JOptionPane.YES_OPTION)
		{
			LinkBrowser.browse("https://runelite.net");
		}
	}
}
