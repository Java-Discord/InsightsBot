package net.javadiscord.util;

import lombok.Getter;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ChartStyler {
	@Getter
	private static final ChartStyler instance = new ChartStyler();

	private final Font uniSansHeavy;
	private final Font uniSansHeavyItalic;
	private final Font uniSansThin;
	private final Font uniSansThinItalic;

	public ChartStyler() {
		this.uniSansHeavy = loadFont("font/UniSansHeavy.ttf");
		this.uniSansHeavyItalic = loadFont("font/UniSansHeavyItalic.ttf");
		this.uniSansThin = loadFont("font/UniSansThin.ttf");
		this.uniSansThinItalic = loadFont("font/UniSansThinItalic.ttf");
	}

	public void style(JFreeChart chart) {
		chart.getPlot().setBackgroundPaint(Color.WHITE);

		chart.getTitle().setFont(this.uniSansHeavyItalic.deriveFont(30.0f));
		chart.getTitle().setMargin(10, 0, 10, 0);
		chart.getLegend().setItemFont(this.uniSansHeavy.deriveFont(16.0f));

		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setLabelFont(this.uniSansHeavy.deriveFont(16.0f));
			chart.getXYPlot().getDomainAxis().setLabelFont(this.uniSansHeavy.deriveFont(16.0f));
			chart.getXYPlot().getRangeAxis().setTickLabelFont(this.uniSansThinItalic.deriveFont(14.0f));
			chart.getXYPlot().getDomainAxis().setTickLabelFont(this.uniSansThinItalic.deriveFont(14.0f));
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
