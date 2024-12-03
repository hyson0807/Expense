import javax.swing.*;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.HashMap;

public class ExpenseTracker extends JFrame {

    private JDatePickerImpl datePicker;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JTextField monthlyExpenseField;
    private JTextArea categoryExpenseTextArea; // 카테고리별 지출 금액 표시 영역
    private HashMap<String, Integer> categoryExpenses; // 카테고리별 총 지출 데이터
    private MyPanel chartPanel; // 파이 차트 패널
    private String currentMonth; // 현재 선택된 월


    public ExpenseTracker() {
        categoryExpenses = new HashMap<>();
        initialize();
    }
    
    private void resetData() {
        // 카테고리별 지출 데이터 초기화
        categoryExpenses.clear();
        monthlyExpenseField.setText(""); // 월 지출 금액 초기화
        updateChart(); // 차트 초기화
        updateCategoryExpenseTextArea(); // 텍스트 초기화
    }


    private void initialize() {
        // JFrame 설정
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // 크기 조정
        setLayout(new BorderLayout()); // BorderLayout 사용

        // 입력 패널 설정
        JPanel inputPanel = new JPanel(new GridLayout(6, 2)); // 행 수 증가

        // 날짜 선택 캘린더 추가
        JLabel dateLabel = new JLabel("날짜 선택:");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 가운데 정렬
        
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        datePicker = new JDatePickerImpl(datePanel);
        inputPanel.add(dateLabel);
        inputPanel.add(datePicker);

        // 지출 금액 입력
        JLabel amountLabel = new JLabel("지출 금액:");
        amountLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 가운데 정렬
        amountField = new JTextField();
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        // 카테고리 선택
        JLabel categoryLabel = new JLabel("카테고리:");
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 가운데 정렬
        String[] categories = {"식비", "교통비", "쇼핑", "기타"};
        categoryComboBox = new JComboBox<>(categories);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);

        // 입력 버튼
        JButton addButton = new JButton("입력");
        addButton.addActionListener(e -> addExpense());
        
        inputPanel.add(addButton);

        // 월 변경 버튼 추가
        JButton resetButton = new JButton("초기화");
        resetButton.addActionListener(e -> resetData());
        
        inputPanel.add(resetButton);

        // 월 지출 금액 필드
        JLabel monthlyExpenseLabel = new JLabel("월 지출 금액:");
        monthlyExpenseLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 가운데 정렬
        monthlyExpenseField = new JTextField();
        monthlyExpenseField.setEditable(false);
        inputPanel.add(monthlyExpenseLabel);
        inputPanel.add(monthlyExpenseField);

        // 입력 패널을 상단에 추가
        add(inputPanel, BorderLayout.NORTH);

        // 차트 패널 및 카테고리 텍스트 영역 설정
        JPanel chartAndTextPanel = new JPanel(new GridLayout(1, 2));

        // 차트 패널
        chartPanel = new MyPanel();
        chartAndTextPanel.add(chartPanel);

        // 카테고리별 지출 텍스트 영역
        categoryExpenseTextArea = new JTextArea();
        categoryExpenseTextArea.setEditable(false);
        categoryExpenseTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(categoryExpenseTextArea);
        chartAndTextPanel.add(scrollPane);

        // 차트 및 텍스트 영역을 중앙에 추가
        add(chartAndTextPanel, BorderLayout.CENTER);

        // 초기 상태 설정
        currentMonth = "";

        setVisible(true);
    }


    private void addExpense() {
        try {
            // 날짜 및 입력값 가져오기
            LocalDate date = getSelectedDate();
            int amount = Integer.parseInt(amountField.getText());
            String category = (String) categoryComboBox.getSelectedItem();

            // 현재 선택된 월 계산
            String selectedMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());

            // 월이 변경되었는지 확인
            if (!selectedMonth.equals(currentMonth)) {
                currentMonth = selectedMonth; // 현재 월 갱신
                resetData(); // 데이터 초기화
            }

            // 카테고리별 총 지출 데이터 갱신
            categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0) + amount);

            // 월 총 지출 계산
            int totalMonthlyExpense = categoryExpenses.values().stream().mapToInt(Integer::intValue).sum();
            monthlyExpenseField.setText(totalMonthlyExpense + "원");

            // 차트 및 텍스트 영역 업데이트
            updateChart();
            updateCategoryExpenseTextArea();

            JOptionPane.showMessageDialog(this, "지출이 추가되었습니다!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "유효하지 않은 금액입니다. 다시 입력해주세요.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "유효하지 않은 입력입니다. 다시 시도해주세요.");
        }
    }


    private void updateChart() {
        // 파이 차트 갱신
        chartPanel.updateSlices(categoryExpenses);
    }

    private void updateCategoryExpenseTextArea() {
        // 카테고리별 지출 데이터를 텍스트 영역에 표시
        StringBuilder sb = new StringBuilder();
        categoryExpenses.forEach((category, amount) -> {
            sb.append(String.format("%s : %d원%n", category, amount));
        });
        categoryExpenseTextArea.setText(sb.toString());
    }

    private LocalDate getSelectedDate() {
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        if (selectedDate == null) {
            throw new IllegalArgumentException("날짜를 선택하지 않았습니다.");
        }
        return LocalDate.parse(new java.sql.Date(selectedDate.getTime()).toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTracker());
    }
}
