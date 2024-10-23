package View.Category;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import Control.ClientControl;
import Dto.ActionName;
import Dto.Request;
import Dto.Response;
import Dto.SearchCategoryForm;
import Model.CategoryModel;

public class Category extends JPanel  {

	private JPanel rootPanel;
	private static JScrollPane categoriesPanel;
	private JButton addButton;
	private JButton cancelButton;
	private JTextField codeInput;
	private JTextField nameInput;
	private JTextArea descriptionInput;

	public Category() {
		rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        createCategoryTop();
        categoriesPanel = loadCategories(getAllCategory());
		rootPanel.add(categoriesPanel, BorderLayout.CENTER);
		createCategorySearch();
		add(rootPanel);
	}

	public JScrollPane loadCategories(List<CategoryModel> categories) {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setPreferredSize(new Dimension(800, 40));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        headerPanel.add(new JLabel("Mã", JLabel.LEFT));
        headerPanel.add(new JLabel("Tên", JLabel.LEFT));
        headerPanel.add(new JLabel("Mô tả", JLabel.LEFT));
        headerPanel.setBackground(Color.LIGHT_GRAY);

        content.add(headerPanel);

        for (CategoryModel categoryItem : categories) {

        	JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem editItem = new JMenuItem("Cập nhật");
            JMenuItem deleteItem = new JMenuItem("Xóa");
            popupMenu.setPopupSize(120, 50);
            popupMenu.add(editItem);
            popupMenu.addSeparator();
            popupMenu.add(deleteItem);
            JPanel rowPanel = new JPanel(new GridLayout(1, 3));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            rowPanel.setPreferredSize(new Dimension(800, 40));
            rowPanel.add(new JLabel(categoryItem.getCode()));
            rowPanel.add(new JLabel(categoryItem.getName()));
            rowPanel.add(new JLabel(categoryItem.getDescription()));
            rowPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            rowPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showPopup(e);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showPopup(e);
                    }
                }

                private void showPopup(MouseEvent e) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            });

            editItem.addActionListener(editAction(categoryItem.getCode(), categoryItem.getName(), categoryItem.getDescription()));
            deleteItem.addActionListener(deleteAction(categoryItem.getCode()));
            content.add(rowPanel);
        }
        categoriesPanel = new JScrollPane(content);
        categoriesPanel.setPreferredSize(new Dimension(900, 400));
        categoriesPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        categoriesPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return categoriesPanel;
	}

    private ActionListener editAction(String code, String name, String description) {
        return e -> {
        	addButton.setText("Cập nhật");
        	cancelButton.setVisible(true);
        	codeInput.setText(code);
        	codeInput.setEnabled(false);
        	nameInput.setText(name);
        	descriptionInput.setText(description);
        };
    }

    private ActionListener deleteAction(String id) {
    	return e -> {
    		Request request = new Request();
    		request.setAction(ActionName.REMOVECATEGORY);
    		request.setData(new CategoryModel(id, null, null));
    		ClientControl clientCtr = new ClientControl();
    		Response result = clientCtr.connectAndGetData(request);
    		Boolean status = (Boolean) result.getData();
    		if (status) {
                JOptionPane.showMessageDialog(null, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                rootPanel.remove(categoriesPanel);
                rootPanel.add(loadCategories(getAllCategory()));
                rootPanel.revalidate();
                rootPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra. Vui lòng thử lại sau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private void createCategorySearch() {
    	JPanel block = new JPanel();
		block.setLayout(new FlowLayout(FlowLayout.LEFT));
        block.add(new JLabel("Tìm kiếm theo: "));

        block.add(new JLabel("Tên:"));
        JTextField searchInput = new JTextField();
        searchInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchInput.setPreferredSize(new Dimension(300, 30));
        block.add(searchInput);

        JButton btnSearch = new JButton();
        btnSearch.setText("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        block.add(btnSearch);
        btnSearch.addActionListener(serachCategory(searchInput));

		rootPanel.add(block, BorderLayout.SOUTH);
	}

    private ActionListener serachCategory(JTextField searchInput) {
    	return e -> {
    		Request request = new Request();
    		request.setAction(ActionName.SEARCHCATEGORY);
    		String keyword  = searchInput.getText();
    		request.setData(new SearchCategoryForm(keyword));

    		ClientControl clientCtr = new ClientControl();
    		Response result = clientCtr.connectAndGetData(request);

    		List<CategoryModel> categories = (List<CategoryModel>) result.getData();
    		if (categories.isEmpty()) {
    			JOptionPane.showMessageDialog(null, "Không có kết quả tìm kiếm", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    		} else {
    			rootPanel.remove(categoriesPanel);
                rootPanel.add(loadCategories(categories));
                rootPanel.revalidate();
                rootPanel.repaint();
    		}
    	};
    }

    private void createCategoryTop() {
    	JPanel categoryTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	categoryTop.add(createCategoryForm());
    	categoryTop.add(createCategoryReport());
    	rootPanel.add(categoryTop, BorderLayout.NORTH);
    }

    private ChartPanel createCategoryReport() {
    	Request request = new Request();
		request.setAction(ActionName.REPORTCATEGORY);
		ClientControl clientCtr = new ClientControl();
		Response result = clientCtr.connectAndGetData(request);
		HashMap<String, Integer> data = (HashMap<String, Integer>) result.getData();
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Tạo biểu đồ tròn
        JFreeChart pieChart = ChartFactory.createPieChart("Tỷ lệ số tin giữa các loại tin", dataset,  true, true, false);

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 250));

        return chartPanel;
    }

    private JPanel createCategoryForm() {
    	JPanel block = new JPanel();
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setPreferredSize(new Dimension(450, 200));

		JLabel label1 = new JLabel("Mã:");
        label1.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label1);
		codeInput = new JTextField();
		codeInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		codeInput.setPreferredSize(new Dimension(200, 30));
        codeInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(codeInput);

		JLabel label2 = new JLabel("Tên:");
		label2.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label2);
		nameInput = new JTextField();
		nameInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		nameInput.setPreferredSize(new Dimension(200, 30));
		nameInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(nameInput);

		JLabel label3 = new JLabel("Mô tả:");
		label3.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(label3);
		descriptionInput = new JTextArea();
		descriptionInput.setPreferredSize(new Dimension(200, 100));
		descriptionInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		descriptionInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(descriptionInput);

		JPanel buttonBlock = new JPanel(new FlowLayout());
		addButton = new JButton("Thêm");
		addButton.setPreferredSize(new Dimension(100, 30));
		addButton.addAncestorListener(null);
		addButton.addActionListener(storeCategory(codeInput, nameInput, descriptionInput));
		cancelButton = new JButton("Hủy bỏ");
		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.setVisible(false);
		cancelButton.addActionListener(e -> {
        	resetForm();
		});
		buttonBlock.add(addButton);
		buttonBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonBlock.add(cancelButton);
		block.add(buttonBlock);

		return block;

	}

    private ActionListener storeCategory(JTextField codeInput, JTextField nameInput, JTextArea descriptionInput) {
    	return e -> {
    		Request request = new Request();
    		if (codeInput.isEnabled()) {
    			request.setAction(ActionName.ADDCATEGORY);
    		} else {
    			request.setAction(ActionName.UPDATECATEGORY);
    		}
    		request.setData(new CategoryModel(codeInput.getText(), nameInput.getText(), descriptionInput.getText()));
    		ClientControl clientCtr = new ClientControl();
    		Response result = clientCtr.connectAndGetData(request);
    		Boolean status = (Boolean) result.getData();
    		if (status) {
                JOptionPane.showMessageDialog(null, "Thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                rootPanel.remove(categoriesPanel);
                rootPanel.add(loadCategories(getAllCategory()));
                rootPanel.revalidate();
                rootPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra. Vui lòng thử lại sau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
    		resetForm();
        };
    }

    private void resetForm() {
    	codeInput.setText("");
    	nameInput.setText("");
    	descriptionInput.setText("");
    	addButton.setText("Thêm");
    	cancelButton.setVisible(false);
    	codeInput.setEnabled(true);
    }

    private static List<CategoryModel> getAllCategory() {
    	Request request = new Request();
    	request.setAction(ActionName.ALLCATEGORY);
		ClientControl clientCtr = new ClientControl();
		Response result = clientCtr.connectAndGetData(request);
		return (List<CategoryModel>) result.getData();
	}
}
