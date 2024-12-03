import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public class MyPanel extends JPanel {
    private ArrayList<Slice> slices;

    public MyPanel() {
        slices = new ArrayList<>();
    }

    public void updateSlices(HashMap<String, Integer> categoryExpenses) {
        slices.clear();

        // Define colors for categories
        Color[] colors = {Color.red, Color.blue, Color.orange, Color.green};
        int colorIndex = 0;

        double total = categoryExpenses.values().stream().mapToDouble(Integer::doubleValue).sum();
        for (String category : categoryExpenses.keySet()) {
            double value = categoryExpenses.get(category);
            slices.add(new Slice(value, colors[colorIndex % colors.length], category));
            colorIndex++;
        }

        repaint(); // Repaint the panel to update the chart
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (slices.isEmpty()) {
            g.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        double total = slices.stream().mapToDouble(s -> s.value).sum();
        double currValue = 0.0;
        int startAngle;

        int x = getWidth() / 4; // 차트의 X 좌표
        int y = getHeight() / 4; // 차트의 Y 좌표
        int width = getWidth() / 2; // 차트의 너비
        int height = getHeight() / 2; // 차트의 높이

        for (Slice s : slices) {
            startAngle = (int) (currValue * 360 / total);
            int arcAngle = (int) (s.value * 360 / total);

            // 차트 그리기
            g.setColor(s.color);
            g.fillArc(x, y, width, height, startAngle, arcAngle);

            // 각 섹터의 중심 각도 계산
            double angle = Math.toRadians(startAngle + arcAngle / 2.0);
            int textX = (int) (x + width / 2 + (width / 2.5) * Math.cos(angle));
            int textY = (int) (y + height / 2 - (height / 2.5) * Math.sin(angle));

            // 텍스트 그리기
            g2d.setColor(Color.black); // 텍스트 색상
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(s.label);
            g2d.drawString(s.label, textX - textWidth / 2, textY);

            currValue += s.value;
        }
    }
}
