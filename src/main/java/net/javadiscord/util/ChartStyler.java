package net.javadiscord.util;

import lombok.Getter;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides Java-Discord themed styling for charts created with JFreeCharts.
 */
public class ChartStyler {
	@Getter
	private static final ChartStyler instance = new ChartStyler();

	public ChartStyler() {
	}

	public void style(JFreeChart chart) {
		final Font uniSansHeavy = loadFont("font/UniSansHeavy.ttf");;
		final Font uniSansHeavyItalic = loadFont("font/UniSansHeavyItalic.ttf");
		final Font uniSansThin = loadFont("font/UniSansThin.ttf");
		final Font uniSansThinItalic = loadFont("font/UniSansThinItalic.ttf");

		chart.getPlot().setBackgroundPaint(Color.WHITE);

		chart.getTitle().setFont(uniSansHeavyItalic.deriveFont(30.0f));
		chart.getTitle().setMargin(10, 0, 10, 0);
		chart.getLegend().setItemFont(uniSansHeavy.deriveFont(16.0f));

		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setLabelFont(uniSansHeavy.deriveFont(16.0f));
			chart.getXYPlot().getDomainAxis().setLabelFont(uniSansHeavy.deriveFont(16.0f));
			chart.getXYPlot().getRangeAxis().setTickLabelFont(uniSansThinItalic.deriveFont(14.0f));
			chart.getXYPlot().getDomainAxis().setTickLabelFont(uniSansThinItalic.deriveFont(14.0f));
		}
	}

	private Font loadFont(String resourceName) {
		InputStream is = ChartStyler.class.getClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			return Font.getFont("Sans-Serif");
		}
		try {
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return Font.getFont("Sans-Serif");
		}
	}
}
